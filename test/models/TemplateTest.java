package models;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import play.api.libs.Files;

public class TemplateTest {
	@Test
	public void testExtractClasses() {
		String html = Files.readFile(new File("test/models/test.html"));
		List<String> classes = Template.extractClasses(html);
		for(String c : classes) {
			System.out.println(c);
		}
	}
}
