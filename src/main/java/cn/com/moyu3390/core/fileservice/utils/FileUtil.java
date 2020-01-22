package cn.com.moyu3390.core.fileservice.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import cn.com.moyu3390.core.fileservice.protocol.Constants;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstData;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstInstruct;

/**
 * 文件读写工具类
 * 
 * @author admin
 *
 */
public class FileUtil {
	public static int SEND_FILEDATA_LENGTH = 1024 * 100;

	public static FileBurstData readFile(String fileUrl, Long readPosition) throws IOException {
		File file = new File(fileUrl);
		// r: 只读模式 rw:读写模式
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
		randomAccessFile.seek(readPosition);
		byte[] bytes = new byte[SEND_FILEDATA_LENGTH];
		int readSize = randomAccessFile.read(bytes);
		if (readSize <= 0) {
			randomAccessFile.close();
			// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
			return new FileBurstData(Constants.FileStatus.COMPLETE);
		}
		FileBurstData fileInfo = new FileBurstData();
		fileInfo.setFileUrl(fileUrl);
		fileInfo.setFileName(file.getName());
		fileInfo.setBeginPos(readPosition);
		fileInfo.setEndPos(readPosition + readSize);
		// 不足1024需要拷贝去掉空字节
		if (readSize < SEND_FILEDATA_LENGTH) {
			byte[] copy = new byte[readSize];
			System.arraycopy(bytes, 0, copy, 0, readSize);
			fileInfo.setBytes(copy);
			fileInfo.setStatus(Constants.FileStatus.END);
		} else {
			fileInfo.setBytes(bytes);
			fileInfo.setStatus(Constants.FileStatus.CENTER);
		}
		randomAccessFile.close();
		return fileInfo;
	}

	public static FileBurstInstruct writeFile(String baseUrl, FileBurstData fileBurstData) throws IOException {

		if (Constants.FileStatus.COMPLETE == fileBurstData.getStatus()) {
			// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
			return new FileBurstInstruct(Constants.FileStatus.COMPLETE);
		}

		File file = new File(baseUrl + "/" + fileBurstData.getFileName());
		// r: 只读模式 rw:读写模式
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		// 移动文件记录指针的位置,
		randomAccessFile.seek(fileBurstData.getBeginPos());
		// 调用了seek（start）方法，是指把文件的记录指针定位到start字节的位置。也就是说程序将从start字节开始写数据
		randomAccessFile.write(fileBurstData.getBytes());
		randomAccessFile.close();

		if (Constants.FileStatus.END == fileBurstData.getStatus()) {
			// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
			return new FileBurstInstruct(Constants.FileStatus.COMPLETE);
		}

		// 文件分片传输指令
		FileBurstInstruct fileBurstInstruct = new FileBurstInstruct();
		// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
		fileBurstInstruct.setStatus(Constants.FileStatus.CENTER);
		// 客户端文件URL
		fileBurstInstruct.setClientFileUrl(fileBurstData.getFileUrl());
		// 下次读取位置
		fileBurstInstruct.setReadPosition(fileBurstData.getEndPos());

		return fileBurstInstruct;
	}

	public static void createDir(String baseDir) {
		File file = new File(baseDir);
		if (!file.exists()) file.mkdirs();
	}

}
