package cn.com.moyu3390.core.fileservice.protocol;

/**
 * 常量类
 * @author admin
 *
 */
public class Constants {

	// Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
	public static class FileStatus {
		public static int	BEGIN		= 0;	// 开始
		public static int	CENTER		= 1;	// 中间
		public static int	END			= 2;	// 结尾
		public static int	COMPLETE	= 3;	// 完成
	}

	// 0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
	public static class TransferType {
		public static int	REQUEST		= 0;	// 0请求传输文件
		public static int	INSTRUCT	= 1;	// 1文件传输指令
		public static int	DATA		= 2;	// 2文件传输数据
	}

}
