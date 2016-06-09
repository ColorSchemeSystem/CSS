package parsers.style;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class StyleCleaner {
	public String removeStyleTagAndStyleAttrs(String html) {
		Document document = Jsoup.parse(html);
		for(Element e : document.getAllElements()) {
			if(e.tagName().equals("head")) {
				e.append("<link rel='stylesheet' type='text/css' href='./style.css'>");
			}
			if(e.tagName().equals("style")) {
				e.remove();
			}	else if(e.hasAttr("style")) {
				e.removeAttr("style");
			}
		}
		return document.html();
	}
}
