package cn.com.moyu3390.core.fileservice.protocol;

/**
 * 文件分片指令
 * @author admin
 *
 */
public class FileBurstInstruct {

    private Integer status;       //Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
    private String clientFileUrl; //客户端文件URL
    private Long readPosition; //读取位置

    public FileBurstInstruct(){}

    public FileBurstInstruct(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getClientFileUrl() {
        return clientFileUrl;
    }

    public void setClientFileUrl(String clientFileUrl) {
        this.clientFileUrl = clientFileUrl;
    }

    public Long getReadPosition() {
        return readPosition;
    }

    public void setReadPosition(Long readPosition) {
        this.readPosition = readPosition;
    }
}
