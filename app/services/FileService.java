package services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileService {
	/**
	 * 
	 * @param fileName
	 * @param content
	 */
	public void saveFile(String fileName, String content) {
		File file = new File(fileName);
		try {
			FileWriter filewriter = new FileWriter(file);
			filewriter.write(content);
			filewriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public void deleteFile(String fileName) {
		File file = new File(fileName);
		file.delete();
	}
	
	/**
	 * @param dir
	 */
	public void mkdir(String dir) {
		new File(dir).mkdirs();
	}
	
	/**
	 * @param fileName
	 * @param targetFileNames
	 * @throws IOException
	 */
	public void zip(String fileName,String[] targetFileNames) throws IOException{
	    try(ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(fileName)))
	    {
	        for(String path : targetFileNames){
	            zos.putNextEntry(new ZipEntry(path));
	            Path p = Paths.get(path);
	            Files.copy(p, zos);
	            zos.closeEntry();
	        }
	    }
	}
	
	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public String fileGetContents(String fileName) {
		File file = new File(fileName);
		String content = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			boolean isContinue = true;
			while(isContinue){
				String str = br.readLine();
				if(str != null) {
					content += str;
				}	else	{
					isContinue = false;
				}
			  }
			br.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
}
