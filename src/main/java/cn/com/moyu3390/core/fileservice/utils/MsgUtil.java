package cn.com.moyu3390.core.fileservice.utils;

import cn.com.moyu3390.core.fileservice.protocol.Constants;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstData;
import cn.com.moyu3390.core.fileservice.protocol.FileBurstInstruct;
import cn.com.moyu3390.core.fileservice.protocol.FileDescInfo;
import cn.com.moyu3390.core.fileservice.protocol.FileTransferProtocol;

/**
 * 消息工具类
 * @author admin
 *
 */
public class MsgUtil {

    /**
     * 构建对象；请求传输文件(客户端)
     *
     * @param fileUrl  客户端文件地址
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @return 传输协议
     */
    public static FileTransferProtocol buildRequestTransferFile(String fileUrl, String fileName,String fileType, Long fileSize) {

        FileDescInfo fileDescInfo = new FileDescInfo();
        fileDescInfo.setFileUrl(fileUrl);
        fileDescInfo.setFileName(fileName);
        fileDescInfo.setFileType(fileType);
        fileDescInfo.setFileSize(fileSize);

        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(0);//0请求传输文件、1文件传输指令、2文件传输数据
        fileTransferProtocol.setTransferObj(fileDescInfo);

        return fileTransferProtocol;

    }

    /**
     * 构建对象；文件传输指令(服务端)
     * @param status          0请求传输文件、1文件传输指令、2文件传输数据
     * @param clientFileUrl   客户端文件地址
     * @param readPosition    读取位置
     * @return                传输协议
     */
    public static FileTransferProtocol buildTransferInstruct(Integer status, String clientFileUrl, Long readPosition) {

        FileBurstInstruct fileBurstInstruct = new FileBurstInstruct();
        fileBurstInstruct.setStatus(status);
        fileBurstInstruct.setClientFileUrl(clientFileUrl);
        fileBurstInstruct.setReadPosition(readPosition);

        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(Constants.TransferType.INSTRUCT); //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransferObj(fileBurstInstruct);

        return fileTransferProtocol;
    }

    /**
     * 构建对象；文件传输指令(服务端)
     *
     * @return 传输协议
     */
    public static FileTransferProtocol buildTransferInstruct(FileBurstInstruct fileBurstInstruct) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(Constants.TransferType.INSTRUCT);  //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransferObj(fileBurstInstruct);
        return fileTransferProtocol;
    }

    /**
     * 构建对象；文件传输数据(客户端)
     *
     * @return 传输协议
     */
    public static FileTransferProtocol buildTransferData(FileBurstData fileBurstData) {
        FileTransferProtocol fileTransferProtocol = new FileTransferProtocol();
        fileTransferProtocol.setTransferType(Constants.TransferType.DATA); //0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
        fileTransferProtocol.setTransferObj(fileBurstData);
        return fileTransferProtocol;
    }

}
