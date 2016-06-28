import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegexTest {
	
	@Test
	public void test() {
		String path = "/aaa/bbb/xxx/123.png";
		Pattern p = Pattern.compile(".*/(.*?)\\.(jpeg|jpg|png)");
		Matcher m = p.matcher(path);
		if(m.find()) {
			System.out.println(m.group(1));
		}
	}
}
