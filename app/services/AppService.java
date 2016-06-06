package services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import models.Template;

public class AppService {

	public List<Template> findAllTemplates() {
		return Template.find.all();
	}
	
	/**
	 * 
	 * @param template
	 */
	public void saveTemplate(Template template) {
		template.save();
	}

	public static List<String> extractClasses(String html) {
		Document document = Jsoup.parse(html);
		Elements elements = document.body().children().select("*");
		Map<String, Integer> classesMap = new HashMap<String, Integer>();
		for (Element e : elements) {
			String classValue = e.attr("class");
			Pattern p = Pattern.compile("[\\s]+");
			if(!classValue.trim().isEmpty()){
				for (String c : p.split(classValue)) {
					if (classesMap.containsKey(c)) {
						classesMap.put(c, classesMap.get(c) + 1);
					} else {
						classesMap.put(c, 1);
					}
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
			classes.add((String) entry.getKey());
		}
		return classes;
	}

	public String readHtmlFile(File file){
		StringBuilder contentBuilder = new StringBuilder();
		try {
		    BufferedReader in = new BufferedReader(new FileReader(file));
		    String str;
		    while ((str = in.readLine()) != null) {
		        contentBuilder.append(str);
		    }
		    in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return contentBuilder.toString();
	}

	public Template getTemp(Long id){
		return Template.findById(id);
	}
}
