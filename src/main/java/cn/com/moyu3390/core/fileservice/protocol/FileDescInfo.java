package cn.com.moyu3390.core.fileservice.protocol;

/**
 * 文件描述信息
 * 
 * @author admin
 *
 */
public class FileDescInfo {

	private String	fileUrl;	// 文件路径
	private String	fileName;	// 文件名称
	private String	fileType;	// 文件类型（业务日志、存储日志、升级文件）
	private Long	fileSize;	// 文件大小

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

}
