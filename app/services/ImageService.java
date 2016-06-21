package services;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import comparators.AscComparator;
import comparators.DescComparator;
import entity.RGB;
import play.Logger;

public class ImageService {
	public static final String webShotUrl = "http://ec2-52-11-169-235.us-west-2.compute.amazonaws.com:5000";
	
	/**
	 * 
	 * @param base64ImageData
	 * @param type
	 * @param fileName
	 */
	public void saveBase64ImageDataAsImage(String base64ImageData,String type,String fileName) {
		Logger.info("type : " + type);
		Logger.info("fileName : " + fileName);
		BufferedImage image = this.convertBase64ImageDataToBufferedImage(base64ImageData, type);
		if(image != null) {
			try {
				ImageIO.write(image, type, new File(fileName));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @return
	 */
	public BufferedImage convertBase64ImageDataToBufferedImage(String base64ImageData,String type) {
		byte[] binaryImage = Base64.decodeBase64(base64ImageData);
		BufferedImage image = null;
		try {
			image = ImageIO.read(new ByteArrayInputStream(binaryImage));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
	
	/**
	 * @param image
	 */
	public Map<String,String> analyze(BufferedImage image) {
		Map<String,Long> histgram = new HashMap<String,Long>();
		for(int h = 0; h < image.getHeight(); h++) {
			for(int w = 0; w < image.getWidth(); w++) {
				RGB rgb = new RGB(image.getRGB(w, h));
				if(histgram.containsKey(rgb.toHexString())) {
					histgram.put(rgb.toHexString(), histgram.get(rgb.toHexString()) + 1L);
				}	else	{
					histgram.put(rgb.toHexString(), 1L);
				}
			}
		}
		List<Map.Entry> entries2 = new ArrayList<Map.Entry>(histgram.entrySet());
		Collections.sort(entries2, new AscComparator());
		List<String> colorHexes = new ArrayList<String>();
		for (Map.Entry entry : entries2) {
			colorHexes.add((String) entry.getKey());
		}
		final long total = image.getWidth() * image.getHeight();
		long removedPixels = 0;
		Iterator<String> iterator = colorHexes.iterator();
		while(iterator.hasNext()){
			String colorHex = iterator.next();
			double percent = ((double) histgram.get(colorHex) / (double) total) * 100.0;
			if(percent < 0.1) {
				removedPixels += histgram.get(colorHex);
				iterator.remove();
			}
		}
		double removedPixelsPercentage = ((double) removedPixels / (double) total) * 100.0;
		Logger.info(String.format("%.1f", removedPixelsPercentage) + "%のピクセルが削除されました。");
		Set<String> removingColorHexes = new HashSet<String>();
		for(int i = 0; i < colorHexes.size(); i++) {
			int minDistance = Integer.MAX_VALUE;
			String colorHex = colorHexes.get(i);
			String closestColorHex = null;
			for(int j = i; j < colorHexes.size(); j++) {
				String colorHex2 = colorHexes.get(j);
				int distance = new RGB(colorHex).calcRGBDist(new RGB(colorHex2));
				if(distance < minDistance && 
						(histgram.get(colorHex) < histgram.get(colorHex2))
						&& !colorHex.equals(colorHex2)) {
					minDistance = distance;
					closestColorHex = colorHex2;
				}
			}
			if(closestColorHex != null && minDistance < 20) {
				Long newValue = histgram.get(closestColorHex) 
						+ histgram.get(colorHex);
				histgram.put(closestColorHex, newValue);
				removingColorHexes.add(colorHex);
			}
		}
		Iterator<String> iterator2 = colorHexes.iterator();
		while(iterator2.hasNext()){
			String colorHex = iterator2.next();
			if(removingColorHexes.contains(colorHex)) {
				iterator2.remove();
			}
		}
		Map<String,Long> tmpMap = new HashMap<String,Long>();
		for(String colorHex : colorHexes) {
			tmpMap.put(colorHex, histgram.get(colorHex));
		}
		List<Map.Entry> entries = new ArrayList<Map.Entry>(tmpMap.entrySet());
		Collections.sort(entries, new DescComparator());
		Map<String,String> result = new LinkedHashMap<String,String>();
		for (Map.Entry entry : entries) {
			double percent = Double.parseDouble(String.valueOf(entry.getValue())) / (double) total * 100.0;
			if(percent > 0.1) {
				result.put((String) entry.getKey(), String.format("%.1f", percent));
			}
		}
		result.put("その他", String.format("%.1f", removedPixelsPercentage));
		return result;
	}
}