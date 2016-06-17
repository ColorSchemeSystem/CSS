package services;

import org.junit.Test;

public class CompressionServiceTest {
	@Test
	public void test() {
		CompressionService s = new CompressionService();
		byte[] b = s.compress("adasdgtrhsfweergrfdfdfascsdcsf");
		String t = s.decompress(b);
		System.out.println(t);
	}
}
