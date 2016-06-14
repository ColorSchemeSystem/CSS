package services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import play.Logger;

public class ImageService {
	public static final String webShotUrl = "https://salty-taiga-57624.herokuapp.com/";
	
	public void saveBase64ImageDataAsImage(String base64ImageData,String type,String fileName) {
		Logger.info("base64 : " + base64ImageData);
		Logger.info("type : " + type);
		Logger.info("fileName : " + fileName);
		byte[] binaryImage = Base64.decodeBase64(base64ImageData);
		try {
			BufferedImage image = ImageIO.read(new ByteArrayInputStream(binaryImage));
			ImageIO.write(image, type, new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
