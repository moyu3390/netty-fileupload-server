/**
 * GBKToUTF8
 * <p>
 * 1.0
 * <p>
 * 2023/3/8 16:04
 */

package cn.com.moyu3390.core.fileservice.utils;



import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Collection;

/**
 *
 * @描述 将GBK编码格式的文件转换为UTF-8编码格式的文件
 *
 * @创建人 HeChenglong
 * @创建时间 2023/3/8
 */
public class GBKToUTF8 {
    public static void main(String[] args) throws Exception {
        String gbkDirPath = "D:\\svnAndroid\\megrezMobile\\MegrezLib\\src";//GBK编码格式源码文件路径
        String utf8DirPath = "D:\\UTF8\\src";//转为UTF-8编码格式源码文件保存路径
        @SuppressWarnings("unchecked")
        Collection<File> gbkFileList =  FileUtils.listFiles(new File(gbkDirPath), new String[]{"java"}, true);//获取所有java文件
        for (File gbkFile : gbkFileList) {
            String utf8FilePath = utf8DirPath + gbkFile.getAbsolutePath().substring(gbkDirPath.length());//UTF-8编码格式文件保存路径
            FileUtils.writeLines(new File(utf8FilePath), "UTF-8", FileUtils.readLines(gbkFile, "GBK"));//使用GBK编码格式读取文件，然后用UTF-8编码格式写入数据
        }
    }
}
