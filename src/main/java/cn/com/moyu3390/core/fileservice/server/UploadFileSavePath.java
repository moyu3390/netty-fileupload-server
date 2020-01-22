package cn.com.moyu3390.core.fileservice.server;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UploadFileSavePath {
	public static Map<String, String> save_path_map = new HashMap<>();
	public static Map<String, String> save_path_temp_map = new HashMap<>();
	static {
		save_path_map.put("business_log", "/data/log/childprocess");
		save_path_map.put("storage_log", "/data/log/childprocess");
		save_path_map.put("upgrade_file", "/data/signservice/upgrade");
		
		save_path_temp_map.put("business_log", "/data/uploadtmp/businesslog");
		save_path_temp_map.put("storage_log", "/data/uploadtmp/storagelog");
		save_path_temp_map.put("upgrade_file", "/data/uploadtmp/upgrade");
	}

	public static String getSavePath(String fileType, String ipAddr) {
		String saveFilePath = save_path_map.get(fileType);
		if (saveFilePath == null) saveFilePath = save_path_map.get("business_log");
		if (fileType.equals("upgrade_file")) return saveFilePath;
		saveFilePath = saveFilePath + File.separator + ipAddr;
		return saveFilePath;
	}
	
	public static String getSaveTempPath(String fileType, String ipAddr) {
		String saveFilePath = save_path_temp_map.get(fileType);
		if (saveFilePath == null) saveFilePath = save_path_temp_map.get("business_log");
		if (fileType.equals("upgrade_file")) return saveFilePath;
		saveFilePath = saveFilePath + File.separator + ipAddr;
		return saveFilePath;
	}
}
