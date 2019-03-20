package me.huqiao.loganlyzer.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件操作工具�?
 * @author hq
 *
 */
public class FileUtil {
	
	
	public static String getExtensionName(String originalFilename){
		if(originalFilename==null || originalFilename.trim().equals("")){
			return "";
		}
		return originalFilename.substring(originalFilename.lastIndexOf("."));
	}

	/**
	 * 保存文件到磁盘，如果该文件已经存在则将会被覆�?
	 * @param path
	 * @param fileName
	 * @param data
	 * @throws IOException
	 */
	public static void saveFileOnDisk(String path,String fileName,byte[] data,boolean append) throws IOException{
		File parentDir = new File(path);
		if(!parentDir.exists()){
			parentDir.mkdirs();
		}
		File file = new File(parentDir,fileName);
		if(!file.exists()){
			file.createNewFile();
		}
		saveFileOnDisk(file,data,append);
	}
	
	/**
	 * 保存文件到磁盘，如果该文件已经存在则将会被覆�?
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void saveFileOnDisk(File file,byte[] data,boolean append) throws IOException{
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file,append);
		fos.write(data);
		fos.close();
	}
	
	public static void saveFileOnDisk(File file,InputStream is) throws IOException{
		if(!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(file,false);
		byte[] buffer = new byte[1024];
		int len = 0;
		while((len = is.read(buffer))!=-1){
			fos.write(buffer, 0, len);
		}
		fos.close();
	}
	
	/**
	 * 保存文件到磁盘，如果该文件已经存在则将会被覆�?
	 * @param file
	 * @param data
	 * @throws IOException
	 */
	public static void saveFileOnDisk(File file,String data,boolean append) throws IOException{
		saveFileOnDisk(file,data.getBytes(),append);
	}
	
}
