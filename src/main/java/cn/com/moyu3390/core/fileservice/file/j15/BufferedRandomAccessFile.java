package cn.com.moyu3390.core.fileservice.file.j15;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/**
 * 近期优化
 */
public class BufferedRandomAccessFile extends RandomAccessFile {
    static final int LOG_BUFF_SZ = 16;                      // 64K buffer
    public static final int BUFF_SZ = (1 << LOG_BUFF_SZ);
    static final long BUFF_MASK = ~(((long) BUFF_SZ) - 1L);

    private String path;

    /*
     * This implementation is based on the buffer implementation in Modula-3's
     * "Rd", "Wr", "RdClass", and "WrClass" interfaces.
     */
    private boolean dirty;                               // true iff unflushed bytes exist
    private boolean syncNeeded;                          // dirty_ can be cleared by e.g. seek, so track sync separately
    private long curr;                                // current position in file
    private long lo, hi;                             // bounds on characters in "buff"
    private byte[] buff;                                // local buffer
    private long maxHi;                               // this.lo + this.buff.length
    private boolean hiteof;                              // buffer contains last file block?
    private long diskPos;                             // disk position

    /*
     * To describe the above fields, we introduce the following abstractions for
     * the file "f": len(f) the length of the file curr(f) the current position
     * in the file c(f) the abstract contents of the file disk(f) the contents
     * of f's backing disk file closed(f) true iff the file is closed "curr(f)"
     * is an index in the closed interval [0, len(f)]. "c(f)" is a character
     * sequence of length "len(f)". "c(f)" and "disk(f)" may differ if "c(f)"
     * contains unflushed writes not reflected in "disk(f)". The flush operation
     * has the effect of making "disk(f)" identical to "c(f)". A file is said to
     * be *valid* if the following conditions hold: V1. The "closed" and "curr"
     * fields are correct: f.closed == closed(f) f.curr == curr(f) V2. The
     * current position is either contained in the buffer, or just past the
     * buffer: f.lo <= f.curr <= f.hi V3. Any (possibly) unflushed characters
     * are stored in "f.buff": (forall i in [f.lo, f.curr): c(f)[i] == f.buff[i
     * - f.lo]) V4. For all characters not covered by V3, c(f) and disk(f)
     * agree: (forall i in [f.lo, len(f)): i not in [f.lo, f.curr) => c(f)[i] ==
     * disk(f)[i]) V5. "f.dirty" is true iff the buffer contains bytes that
     * should be flushed to the file; by V3 and V4, only part of the buffer can
     * be dirty. f.dirty == (exists i in [f.lo, f.curr): c(f)[i] != f.buff[i -
     * f.lo]) V6. this.maxHi == this.lo + this.buff.length Note that "f.buff"
     * can be "null" in a valid file, since the range of characters in V3 is
     * empty when "f.lo == f.curr". A file is said to be *ready* if the buffer
     * contains the current position, i.e., when: R1. !f.closed && f.buff !=
     * null && f.lo <= f.curr && f.curr < f.hi When a file is ready, reading or
     * writing a single byte can be performed by reading or writing the
     * in-memory buffer without performing a disk operation.
     */

    /**
     * Open a new <code>BufferedRandomAccessFile</code> on <code>file</code> in
     * mode <code>mode</code>, which should be "r" for reading only, or "rw" for
     * reading and writing.
     */
    public BufferedRandomAccessFile(File file, String mode) throws IOException {
        this(file, mode, 0);
    }

    public BufferedRandomAccessFile(File file, String mode, int size) throws IOException {
        super(file, mode);
        path = file.getAbsolutePath();
        this.init(size);
    }

    /**
     * Open a new <code>BufferedRandomAccessFile</code> on the file named
     * <code>name</code> in mode <code>mode</code>, which should be "r" for
     * reading only, or "rw" for reading and writing.
     */
    public BufferedRandomAccessFile(String name, String mode) throws IOException {
        this(name, mode, 0);
    }

    public BufferedRandomAccessFile(String name, String mode, int size) throws FileNotFoundException {
        super(name, mode);
        path = name;
        this.init(size);
    }

    private void init(int size) {
        this.dirty = false;
        this.lo = this.curr = this.hi = 0;
        this.buff = (size > BUFF_SZ) ? new byte[size] : new byte[BUFF_SZ];
        this.maxHi = (long) BUFF_SZ;
        this.hiteof = false;
        this.diskPos = 0L;
    }

    public String getPath() {
        return path;
    }

    public void sync() throws IOException {
        if (syncNeeded) {
            flush();
            getChannel().force(true);
            syncNeeded = false;
        }
    }

    //      public boolean isEOF() throws IOException
    //      {
    //          assert getFilePointer() <= length();
    //          return getFilePointer() == length();
    //      }

    @Override
    public void close() throws IOException {
        this.flush();
        this.buff = null;
        super.close();
    }

    /**
     * Flush any bytes in the file's buffer that have not yet been written to
     * disk. If the file was created read-only, this method is a no-op.
     */
    public void flush() throws IOException {
        this.flushBuffer();
    }

    /* Flush any dirty bytes in the buffer to disk. */
    private void flushBuffer() throws IOException {
        if (this.dirty) {
            if (this.diskPos != this.lo) {
                super.seek(this.lo);
            }
            int len = (int) (this.curr - this.lo);
            super.write(this.buff, 0, len);
            this.diskPos = this.curr;
            this.dirty = false;
        }
    }

    /*
     * Read at most "this.buff.length" bytes into "this.buff", returning the
     * number of bytes read. If the return result is less than
     * "this.buff.length", then EOF was read.
     */
    private int fillBuffer() throws IOException {
        int cnt = 0;
        int rem = this.buff.length;
        while (rem > 0) {
            int n = super.read(this.buff, cnt, rem);
            if (n < 0) {
                break;
            }
            cnt += n;
            rem -= n;
        }
        if ((cnt < 0) && (this.hiteof = (cnt < this.buff.length))) {
            // make sure buffer that wasn't read is initialized with -1
            Arrays.fill(this.buff, cnt, this.buff.length, (byte) 0xff);
        }
        this.diskPos += cnt;
        return cnt;
    }

    /*
     * This method positions <code>this.curr</code> at position
     * <code>pos</code>. If <code>pos</code> does not fall in the current
     * buffer, it flushes the current buffer and loads the correct one.<p> On
     * exit from this routine <code>this.curr == this.hi</code> iff
     * <code>pos</code> is at or past the end-of-file, which can only happen if
     * the file was opened in read-only mode.
     */
    @Override
    public void seek(long pos) throws IOException {
        if (pos >= this.hi || pos < this.lo) {
            // seeking outside of current buffer -- flush and read
            this.flushBuffer();
            this.lo = pos & BUFF_MASK; // start at BuffSz boundary
            this.maxHi = this.lo + (long) this.buff.length;
            if (this.diskPos != this.lo) {
                super.seek(this.lo);
                this.diskPos = this.lo;
            }
            int n = this.fillBuffer();
            this.hi = this.lo + (long) n;
        } else {
            // seeking inside current buffer -- no read required
            if (pos < this.curr) {
                // if seeking backwards, we must flush to maintain V4
                this.flushBuffer();
            }
        }
        this.curr = pos;
    }

    @Override
    public long getFilePointer() {
        return this.curr;
    }

    @Override
    public long length() throws IOException {
        // max accounts for the case where we have written past the old file length, but not yet flushed our buffer
        return Math.max(this.curr, super.length());
    }

    @Override
    public int read() throws IOException {
        if (this.curr >= this.hi) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hiteof) {
                return -1;
            }

            // slow path -- read another buffer
            this.seek(this.curr);
            if (this.curr == this.hi) {
                return -1;
            }
        }
        byte res = this.buff[(int) (this.curr - this.lo)];
        this.curr++;
        return ((int) res) & 0xFF; // convert byte -> int
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.curr >= this.hi) {
            // test for EOF
            // if (this.hi < this.maxHi) return -1;
            if (this.hiteof) {
                return -1;
            }

            // slow path -- read another buffer
            this.seek(this.curr);
            if (this.curr == this.hi) {
                return -1;
            }
        }
        len = Math.min(len, (int) (this.hi - this.curr));
        int buffOff = (int) (this.curr - this.lo);
        System.arraycopy(this.buff, buffOff, b, off, len);
        this.curr += len;
        return len;
    }

    @Override
    public void write(int b) throws IOException {
        if (this.curr >= this.hi) {
            if (this.hiteof && this.hi < this.maxHi) {
                // at EOF -- bump "hi"
                this.hi++;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr);
                if (this.curr == this.hi) {
                    // appending to EOF -- bump "hi"
                    this.hi++;
                }
            }
        }
        this.buff[(int) (this.curr - this.lo)] = (byte) b;
        this.curr++;
        this.dirty = true;
        syncNeeded = true;
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        while (len > 0) {
            int n = this.writeAtMost(b, off, len);
            off += n;
            len -= n;
            this.dirty = true;
            syncNeeded = true;
        }
    }

    /*
     * Write at most "len" bytes to "b" starting at position "off", and return
     * the number of bytes written.
     */
    private int writeAtMost(byte[] b, int off, int len) throws IOException {
        if (this.curr >= this.hi) {
            if (this.hiteof && this.hi < this.maxHi) {
                // at EOF -- bump "hi"
                this.hi = this.maxHi;
            } else {
                // slow path -- write current buffer; read next one
                this.seek(this.curr);
                if (this.curr == this.hi) {
                    // appending to EOF -- bump "hi"
                    this.hi = this.maxHi;
                }
            }
        }
        len = Math.min(len, (int) (this.hi - this.curr));
        int buffOff = (int) (this.curr - this.lo);
        System.arraycopy(b, off, this.buff, buffOff, len);
        this.curr += len;
        return len;
    }
}


