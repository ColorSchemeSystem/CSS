package parsers.style;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class StyleCleaner {
	public String removeStyleTagAndStyleAttrs(String html) {
		Document document = Jsoup.parse(html);
		for(Element e : document.getAllElements()) {
			if(e.tagName().equals("head")) {
				e.append("<link rel='stylesheet' type='text/css' href='./style.css'>");
			} else if(e.tagName().equals("style")) {
				e.remove();
			}	else if(e.hasAttr("style") && e.hasAttr("class")) {
				e.removeAttr("style");
			}	
			if(e.tagName().equals("img") && e.hasAttr("src")) {
				String src = e.attr("src");
				Pattern p = Pattern.compile(".*/(.*?\\.(jpeg|jpg|png))");
				Matcher m = p.matcher(src);
				if(m.find() && m.groupCount() >= 1) {
					src = "./" + m.group(1);
				}
				e.attr("src", src);
			}
		}
		return document.html();
	}
}