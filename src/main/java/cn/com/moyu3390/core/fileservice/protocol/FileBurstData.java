package cn.com.moyu3390.core.fileservice.protocol;

/**
 * 文件分片数据
 * 
 * @author admin
 *
 */
public class FileBurstData {

	private String	fileUrl;	// 客户端文件地址
	private String	fileName;	// 文件名称
	private Long	beginPos;	// 开始位置
	private Long	endPos;		// 结束位置
	private byte[]	bytes;		// 文件字节；再实际应用中可以使用非对称加密，以保证传输信息安全
	private Integer	status;		// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝

	public FileBurstData() {

	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public FileBurstData(Integer status) {
		this.status = status;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getBeginPos() {
		return beginPos;
	}

	public void setBeginPos(Long beginPos) {
		this.beginPos = beginPos;
	}

	public Long getEndPos() {
		return endPos;
	}

	public void setEndPos(Long endPos) {
		this.endPos = endPos;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
