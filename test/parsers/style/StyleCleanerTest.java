package parsers.style;

import org.junit.Test;

import services.FileService;

public class StyleCleanerTest {
	StyleCleaner c = new StyleCleaner();
	FileService f = new FileService();
	
	@Test
	public void test() {
		String html = f.fileGetContents("test/parsers/style/t1.html");
		System.out.println(c.removeStyleTagAndStyleAttrs(html));
	}
}
