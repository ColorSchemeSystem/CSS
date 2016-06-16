package services;

import java.io.UnsupportedEncodingException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class CompressionService {
	/**
	 * @return
	 */
	public byte[] compress(String str) {
		byte[] output = new byte[65535];
		int length = 0;
		Deflater compresser = new Deflater();
		try {
			compresser.setInput(str.getBytes("UTF-8"));
			compresser.finish();
			length = compresser.deflate(output);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++) {
			result[i] = output[i];
		}
		System.out.println(result + "\n\n\n\n");
		return result;
	}

	/**
	 * @return
	 */
	public String decompress(byte[] data) {
		Inflater decompresser = new Inflater();
		decompresser.setInput(data, 0, data.length);
		String str = "";
		byte[] result = new byte[65535];
		try {
			int resultLength = decompresser.inflate(result);
			decompresser.end();
			str = new String(result, 0, resultLength, "UTF-8");
		} catch (DataFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(str + "\n\n\n\n");
		return str;
	}
}
