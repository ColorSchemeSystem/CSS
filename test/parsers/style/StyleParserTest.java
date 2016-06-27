package parsers.style;

import org.junit.Test;

import services.FileService;

public class StyleParserTest {
	StyleParser p = new StyleParser();
	FileService f = new FileService();
	@Test
	public void test() {
		String html = f.fileGetContents("test/parsers/style/t1.html");
		System.out.println(p.parse(html).toString());
	}
	
	@Test
	public void test2() {
		String html = f.fileGetContents("test/parsers/style/t2.html");
		System.out.println(p.parse(html).toString());
	}
	
	@Test
	public void test3() {
		String html = f.fileGetContents("test/parsers/style/t3.html");
		System.out.println(p.parse(html).toString());
	}
	
	@Test
	public void test4() {
		String html = f.fileGetContents("test/parsers/style/t4.html");
		System.out.println(p.parse(html).toString());
	}
	
	@Test
	public void test5() {
		String html = f.fileGetContents("test/parsers/style/t5.html");
		System.out.println(p.parse(html).toString());
	}
}
