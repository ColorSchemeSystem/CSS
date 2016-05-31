package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AppService {

	public static List<String> extractClasses(String html) {
		Document document = Jsoup.parse(html);
		Elements elements = document.body().children().select("*");
		Map<String, Integer> classesMap = new HashMap<String, Integer>();
		for (Element e : elements) {
			String classValue = e.attr("class");
			for (String c : classValue.split(" ")) {
				if (classesMap.containsKey(c)) {
					classesMap.put(c, classesMap.get(c) + 1);
				} else {
					classesMap.put(c, 1);
				}
			}
		}
		List<Map.Entry> entries = new ArrayList<Map.Entry>(classesMap.entrySet());
		Collections.sort(entries, new Comparator() {
			public int compare(Object o1, Object o2) {
				Map.Entry e1 = (Map.Entry) o1;
				Map.Entry e2 = (Map.Entry) o2;
				return ((Integer) e2.getValue()).compareTo((Integer) e1.getValue());
			}
		});
		List<String> classes = new ArrayList<String>();
		for (Map.Entry entry : entries) {
			classes.add((String) entry.getKey() + " : " + entry.getValue());
		}
		return classes;
	}
}