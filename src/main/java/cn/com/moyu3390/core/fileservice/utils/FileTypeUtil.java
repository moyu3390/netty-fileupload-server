package cn.com.moyu3390.core.fileservice.utils;

import cn.com.moyu3390.core.fileservice.exception.FileServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * 判断文件类型工具类
 */
public class FileTypeUtil {
    private static Logger logger = LoggerFactory.getLogger(FileTypeUtil.class);
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    private FileTypeUtil() {
    }

    static {
        getAllFileType(); // 初始化文件类型信息
    }

    /**
     * Discription:[getAllFileType,常见文件头信息]
     */
    private static void getAllFileType() {
        FILE_TYPE_MAP.put("ffd8ffe0".toLowerCase(), "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("ffd8ffe1".toLowerCase(), "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("ffd8ffe8".toLowerCase(), "jpg"); // JPEG (jpg)
        FILE_TYPE_MAP.put("89504e470d0a1a0a0000".toLowerCase(), "png"); // PNG (png)
        FILE_TYPE_MAP.put("47494638396126026f01".toLowerCase(), "gif"); // GIF (gif)
        FILE_TYPE_MAP.put("49492a00227105008037".toLowerCase(), "tif"); // TIFF (tif)
        FILE_TYPE_MAP.put("424d228c010000000000".toLowerCase(), "bmp"); // 16色位图(bmp)
        FILE_TYPE_MAP.put("424d8240090000000000".toLowerCase(), "bmp"); // 24位位图(bmp)
        FILE_TYPE_MAP.put("424d8e1b030000000000".toLowerCase(), "bmp"); // 256色位图(bmp)
        FILE_TYPE_MAP.put("41433130313500000000".toLowerCase(), "dwg"); // CAD (dwg)
        FILE_TYPE_MAP.put("3c21444f435459504520".toLowerCase(), "html"); // HTML (html)
        FILE_TYPE_MAP.put("3c21646f637479706520".toLowerCase(), "htm"); // HTM (htm)
        FILE_TYPE_MAP.put("48544d4c207b0d0a0942".toLowerCase(), "css"); // css
        FILE_TYPE_MAP.put("696b2e71623d696b2e71".toLowerCase(), "js"); // js
        FILE_TYPE_MAP.put("7b5c727466315c616e73".toLowerCase(), "rtf"); // Rich Text Format (rtf)
        FILE_TYPE_MAP.put("38425053000100000000".toLowerCase(), "psd"); // Photoshop (psd)
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762".toLowerCase(), "eml"); // Email [Outlook Express 6] (eml)
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000".toLowerCase(), "doc"); // MS Excel 注意：word、msi 和 excel的文件头一样
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000".toLowerCase(), "vsd"); // Visio 绘图
        FILE_TYPE_MAP.put("5374616E64617264204A".toLowerCase(), "mdb"); // MS Access (mdb)
        FILE_TYPE_MAP.put("252150532D41646F6265".toLowerCase(), "ps");
        FILE_TYPE_MAP.put("255044462d312e350d0a".toLowerCase(), "pdf"); // Adobe Acrobat (pdf)
        FILE_TYPE_MAP.put("2e524d46000000120001".toLowerCase(), "rmvb"); // rmvb/rm相同
        FILE_TYPE_MAP.put("464c5601050000000900".toLowerCase(), "flv"); // flv与f4v相同
        FILE_TYPE_MAP.put("00000020667479706d70".toLowerCase(), "mp4");
        FILE_TYPE_MAP.put("49443303000000002176".toLowerCase(), "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180".toLowerCase(), "mpg"); //
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9".toLowerCase(), "wmv"); // wmv与asf相同
        FILE_TYPE_MAP.put("52494646e27807005741".toLowerCase(), "wav"); // Wave (wav)
        FILE_TYPE_MAP.put("52494646d07d60074156".toLowerCase(), "avi");
        FILE_TYPE_MAP.put("4d546864000000060001".toLowerCase(), "mid"); // MIDI (mid)
        FILE_TYPE_MAP.put("504b0304140000000800".toLowerCase(), "zip");
        FILE_TYPE_MAP.put("526172211a0700cf9073".toLowerCase(), "rar");
        FILE_TYPE_MAP.put("235468697320636f6e66".toLowerCase(), "ini");
        FILE_TYPE_MAP.put("504b03040a0000000000".toLowerCase(), "jar");
        FILE_TYPE_MAP.put("4d5a9000030000000400".toLowerCase(), "exe");// 可执行文件
        FILE_TYPE_MAP.put("3c25402070616765206c".toLowerCase(), "jsp");// jsp文件
        FILE_TYPE_MAP.put("4d616e69666573742d56".toLowerCase(), "mf");// MF文件
        FILE_TYPE_MAP.put("3c3f786d6c2076657273".toLowerCase(), "xml");// xml文件
        FILE_TYPE_MAP.put("494e5345525420494e54".toLowerCase(), "sql");// xml文件
        FILE_TYPE_MAP.put("7061636b616765207765".toLowerCase(), "java");// java文件
        FILE_TYPE_MAP.put("406563686f206f66660d".toLowerCase(), "bat");// bat文件
        FILE_TYPE_MAP.put("1f8b0800000000000000".toLowerCase(), "gz");// gz文件
        FILE_TYPE_MAP.put("6c6f67346a2e726f6f74".toLowerCase(), "properties");// bat文件
        FILE_TYPE_MAP.put("cafebabe0000002e0041".toLowerCase(), "class");// bat文件
        FILE_TYPE_MAP.put("49545346030000006000".toLowerCase(), "chm");// bat文件
        FILE_TYPE_MAP.put("04000000010000001300".toLowerCase(), "mxp");// bat文件
        FILE_TYPE_MAP.put("504b0304140006000800".toLowerCase(), "docx");// docx文件
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000".toLowerCase(), "wps");// WPS文字wps、表格et、演示dps都是一样的
        FILE_TYPE_MAP.put("6431303a637265617465".toLowerCase(), "torrent");
        FILE_TYPE_MAP.put("6D6F6F76".toLowerCase(), "mov"); // Quicktime (mov)
        FILE_TYPE_MAP.put("FF575043".toLowerCase(), "wpd"); // WordPerfect (wpd)
        FILE_TYPE_MAP.put("CFAD12FEC5FD746F".toLowerCase(), "dbx"); // Outlook Express (dbx)
        FILE_TYPE_MAP.put("2142444E".toLowerCase(), "pst"); // Outlook (pst)
        FILE_TYPE_MAP.put("AC9EBD8F".toLowerCase(), "qdf"); // Quicken (qdf)
        FILE_TYPE_MAP.put("E3828596".toLowerCase(), "pwl"); // Windows Password (pwl)
        FILE_TYPE_MAP.put("2E7261FD".toLowerCase(), "ram"); // Real Audio (ram)
        FILE_TYPE_MAP.put("2321414D520A".toLowerCase(), "amr"); //AMR
        FILE_TYPE_MAP.put("75736167".toLowerCase(), "txt");
        FILE_TYPE_MAP.put("756470ef".toLowerCase(), "txt_Mac");
    }

    /**
     * 得到上传文件的文件头
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 根据制定文件的文件头判断其文件类型
     *
     * @param filePath
     * @return
     */
    public static String getFileType(String filePath) {
        String res = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            String fileCode = bytesToHexString(b);
            // System.out.println(fileCode);
            // 这种方法在字典的头代码不够位数的时候可以用但是速度相对慢一点
            Iterator<String> keyIter = FILE_TYPE_MAP.keySet().iterator();
            while (keyIter.hasNext()) {
                String key = keyIter.next();
                if (key.startsWith(fileCode) || fileCode.contains(key)) {
                    res = FILE_TYPE_MAP.get(key);
                    break;
                }
            }
            return res;
        } catch (FileNotFoundException e) {
            logger.error("文件不存在,message={}", e);
            throw new FileServiceException(e);
        } catch (IOException e) {
            logger.error("文件不存在,message={}", e);
            throw new FileServiceException(e);
        } finally {
            try {
                if (Objects.nonNull(is)) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
                logger.error("关闭文件流失败, message={}", e);
                throw new FileServiceException("关闭文件流失败,", e);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        //注意：测试时故意把文件后缀给去掉，模拟从远程文件服务器下载的文件
        String fileType = getFileType("/Users/Data/Trackers.txt");//txt文档
        System.out.println("The file format of word is：" + fileType);

    }
}
