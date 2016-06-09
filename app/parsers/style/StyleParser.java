package parsers.style;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import play.Logger;

public class StyleParser {
	/**
	 * @param style
	 * @return
	 */
	private List<String> splitStyleIntoBlock(String style) {
		Pattern p = Pattern.compile("[\\s\\S]*?\\{[\\s\\S]*?\\}");
		Matcher m = p.matcher(style);
		List<String> list = new ArrayList<String>();
		while(m.find()) {
			list.add(m.group());
		}
		return list;
	}

	/**
	 * @param style
	 * @return
	 */
	private String[] blockToAttrsAndContent(String style) {
		//時々先頭の文字が切れてしまう(body -> ody)問題があるので、とりあえず応急処置
		style = "    " + style;
		Pattern p = Pattern.compile("([\\s\\S]*?)\\{([\\s\\S]*?)\\}");
		Matcher m = p.matcher(style);
		String attrs = "";
		if(m.find(1)) {
			attrs = m.group(1);
		}
		String elements = "";
		if(m.find(2)) {
			elements = m.group(2);
		}
		return new String[]{attrs,elements};
	}

	/**
	 * @param attrs
	 * @return
	 */
	private List<String> splitAndTrimAttrs(String attrs) {
		Pattern p = Pattern.compile("\\s");
		List<String> attrsList = new ArrayList<String>();
		for(String attr : p.split(attrs)) {
			if(StringUtils.isBlank(attr)) {
				continue;
			}
			attrsList.add(attr.replaceAll("\\s", ""));
		}
		return attrsList;
	}

	/**
	 * @param elementsStr
	 * @return
	 */
	private Map<String,String> splitAndTrimElements(String elementsStr) {
		String[] elements = elementsStr.split(";");
		Map<String,String> map = new LinkedHashMap<String,String>();
		for(String e : elements) {
			e = e.trim();
			if(StringUtils.isBlank(e)) {
				continue;
			}
			String[] keyValue = e.split(":");
			try {
				map.put(keyValue[0].trim(),
						keyValue[1].trim());
			}	catch(ArrayIndexOutOfBoundsException ex) {
				ex.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * @param attrs
	 * @return
	 */
	private List<Attr> convertAttrs(List<String> attrs) {
		List<Attr> result = new ArrayList<Attr>();
		for(String attr : attrs) {
			Attr e = new Attr();
			if(attr.startsWith(".")) {
				e.attrType = "class";
				e.attrName = attr.replace(".", "");
			}	else	{
				e.attrType = "tag";
				e.attrName = attr;
			}
			result.add(e);
		}
		return result;
	}

	/**
	 * @param elements
	 * @return
	 */
	private List<Elem> convertElements(Map<String,String> elements) {
		List<Elem> result = new ArrayList<Elem>();
		for(String key : elements.keySet()) {
			Elem e = new Elem();
			e.name = key;
			e.value = elements.get(key);
			result.add(e);
		}
		return result;
	}

	/**
	 * @param html
	 */
	private Style parseStyleTag(String html) {
		Document document = Jsoup.parse(html);
		Style style = new Style();
		for (Element e : document.getAllElements()) {
			if(e.tagName().equals("style")) {
				List<String> blocks = this.splitStyleIntoBlock(e.html());
				for(String b : blocks) {
					String[] result = this.blockToAttrsAndContent(b);
					Block block = new Block();
					block.attrs = this.convertAttrs(this.splitAndTrimAttrs(result[0]));
					block.elements = this.convertElements(
							this.splitAndTrimElements(result[1]));
					style.blocks.add(block);
				}
			}
		}
		return style;
	}

	/**
	 * @param html
	 */
	private Style parseStyleAttrs(String html) {
		Document document = Jsoup.parse(html);
		List<String> classNames = this.extractClasses(document);
		Style style = new Style();
		for(String className : classNames) {
			if(className.equals("")) {
				continue;
			}
			Attr attr = new Attr();
			attr.attrType = "class";
			attr.attrName = className;
			Block block = new Block();
			block.attrs.add(attr);
			Elements elements = document.getElementsByClass(className);
			Map<String,String> result = new HashMap<String,String>();
			for(int i = 0; i < elements.size(); i++) {
				String styleVal = elements.get(i).attr("style");
				if(StringUtils.isBlank(styleVal)) {
					continue;
				}
				Map<String,String> styleMap = this.splitAndTrimElements(styleVal);
				if(i == 0) {
					result = styleMap;
				}	else	{
					for(String key : styleMap.keySet()) {
						if(result.containsKey(key) &&
								!result.get(key).equals(styleMap.get(key))) {
							result.remove(key);
						}
					}
				}
			}
			block.elements = this.convertElements(result);
			if(block.elements.size() != 0) {
				style.blocks.add(block);
			}
		}
		return style;
	}

	/**
	 * @param html
	 * @return
	 */
	public Style parse(String html) {
		Style s1 = this.parseStyleTag(html);
		Style s2 = this.parseStyleAttrs(html);
		s1.mergeStyle(s2);
		return s1;
	}

	/**
	 * @param document
	 * @return
	 */
	private List<String> extractClasses(Document document) {
		Elements elements = document.body().children().select("*");
		Set<String> classNames = new HashSet<String>();
		for (Element e : elements) {
			String classValue = e.attr("class");
			Pattern p = Pattern.compile("[\\s]+");
			if(!classValue.trim().isEmpty()){
				for (String c : p.split(classValue)) {
					classNames.add(c);
				}
			}
		}
		return new ArrayList<String>(classNames);
	}

}