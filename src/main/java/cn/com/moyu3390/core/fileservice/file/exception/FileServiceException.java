package cn.com.moyu3390.core.fileservice.file.exception;

public class FileServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5426772061121103899L;

	public FileServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileServiceException(String message) {
		super(message);
	}

	public FileServiceException(Throwable cause) {
		super(cause);
	}

}
