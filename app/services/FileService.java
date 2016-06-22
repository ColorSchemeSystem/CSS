package services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileService {
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

}
