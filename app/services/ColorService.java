package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Color;
import entity.RGB;

public class ColorService {
	/**
	 * CSSカラー
	 * @return
	 */
	public List<Color> getCssColors() {
		List<Color> cssColors = new ArrayList<Color>();
		cssColors.add(new Color("#000000"));
		cssColors.add(new Color("#800000"));
		cssColors.add(new Color("#008000"));
		cssColors.add(new Color("#000080"));
		cssColors.add(new Color("#808080"));
		cssColors.add(new Color("#ff0000"));
		cssColors.add(new Color("#00ff00"));
		cssColors.add(new Color("#0000ff"));
		cssColors.add(new Color("#c0c0c0"));
		cssColors.add(new Color("#800080"));
		cssColors.add(new Color("#808000"));
		cssColors.add(new Color("#008080"));
		cssColors.add(new Color("#ffffff"));
		cssColors.add(new Color("#ff00ff"));
		cssColors.add(new Color("#ffff00"));
		cssColors.add(new Color("#00ffff"));
		return cssColors;
	}
	
	/**
	 * WEBセーフカラーを表示
	 * @return
	 */
	public List<Color> getWebSafeColors() {
		final int[] nums = {0,51,102,153,204,255};
		List<Color> webSafeColors = new ArrayList<Color>();
		for(int r : nums) {
			for(int g : nums) {
				for(int b : nums) {
					RGB rgb = new RGB(r,g,b);
					Color color = new Color("#" + rgb.toHexString());
					webSafeColors.add(color);
				}
			}
		}
		return webSafeColors;
	}
	
	/**
	 * @return
	 */
	public List<Color> getPrimaryColors() {
		List<Color> primaryColors = new ArrayList<Color>();
		primaryColors.add(new Color("black","#000000"));
		primaryColors.add(new Color("aliceblue","#f0f8ff"));
		primaryColors.add(new Color("darkcyan","#008b8b"));
		primaryColors.add(new Color("lightyellow","#ffffe0"));
		primaryColors.add(new Color("coral","#ff7f50"));
		primaryColors.add(new Color("dimgray","#696969"));
		primaryColors.add(new Color("lavender","#e6e6fa"));
		primaryColors.add(new Color("teal","#008080"));
		primaryColors.add(new Color("lightgoldenrodyellow","#fafad2"));
		primaryColors.add(new Color("tomato","#ff6347"));
		primaryColors.add(new Color("gray","#808080"));
		primaryColors.add(new Color("lightsteelblue","#b0c4de"));
		primaryColors.add(new Color("darkslategray","#2f4f4f"));
		primaryColors.add(new Color("lemonchiffon","#fffacd"));
		primaryColors.add(new Color("orangered","#ff4500"));
		primaryColors.add(new Color("darkgray","#a9a9a9"));
		primaryColors.add(new Color("lightslategray","#778899"));
		primaryColors.add(new Color("darkgreen","#006400"));
		primaryColors.add(new Color("wheat","#f5deb3"));
		primaryColors.add(new Color("red","#ff0000"));
		primaryColors.add(new Color("silver","#c0c0c0"));
		primaryColors.add(new Color("slategray","#708090"));
		primaryColors.add(new Color("green","#008000"));
		primaryColors.add(new Color("burlywood","#deb887"));
		primaryColors.add(new Color("crimson","#dc143c"));
		primaryColors.add(new Color("lightgrey","#d3d3d3"));
		primaryColors.add(new Color("steelblue","#4682b4"));
		primaryColors.add(new Color("forestgreen","#228b22"));
		primaryColors.add(new Color("tan","#d2b48c"));
		primaryColors.add(new Color("mediumvioletred","#c71585"));
		primaryColors.add(new Color("gainsboro","#dcdcdc"));
		primaryColors.add(new Color("royalblue","#4169e1"));
		primaryColors.add(new Color("seagreen","#2e8b57"));
		primaryColors.add(new Color("khaki","#f0e68c"));
		primaryColors.add(new Color("deeppink","#ff1493"));
		primaryColors.add(new Color("whitesmoke","#f5f5f5"));
		primaryColors.add(new Color("midnightblue","#191970"));
		primaryColors.add(new Color("mediumseagreen","#3cb371"));
		primaryColors.add(new Color("yellow","#ffff00"));
		primaryColors.add(new Color("hotpink","#ff69b4"));
		primaryColors.add(new Color("white","#ffffff"));
		primaryColors.add(new Color("navy","#000080"));
		primaryColors.add(new Color("mediumaquamarine","#66cdaa"));
		primaryColors.add(new Color("gold","#ffd700"));
		primaryColors.add(new Color("palevioletred","#db7093"));
		primaryColors.add(new Color("snow","#fffafa"));
		primaryColors.add(new Color("darkblue","#00008b"));
		primaryColors.add(new Color("darkseagreen","#8fbc8f"));
		primaryColors.add(new Color("orange","#ffa500"));
		primaryColors.add(new Color("pink","#ffc0cb"));
		primaryColors.add(new Color("ghostwhite","#f8f8ff"));
		primaryColors.add(new Color("mediumblue","#0000cd"));
		primaryColors.add(new Color("aquamarine","#7fffd4"));
		primaryColors.add(new Color("sandybrown","#f4a460"));
		primaryColors.add(new Color("lightpink","#ffb6c1"));
		primaryColors.add(new Color("floralwhite","#fffaf0"));
		primaryColors.add(new Color("blue","#0000ff"));
		primaryColors.add(new Color("palegreen","#98fb98"));
		primaryColors.add(new Color("darkorange","#ff8c00"));
		primaryColors.add(new Color("thistle","#d8bfd8"));
		primaryColors.add(new Color("linen","#faf0e6"));
		primaryColors.add(new Color("dodgerblue","#1e90ff"));
		primaryColors.add(new Color("lightgreen","#90ee90"));
		primaryColors.add(new Color("goldenrod","#daa520"));
		primaryColors.add(new Color("magenta","#ff00ff"));
		primaryColors.add(new Color("antiquewhite","#faebd7"));
		primaryColors.add(new Color("cornflowerblue","#6495ed"));
		primaryColors.add(new Color("springgreen","#00ff7f"));
		primaryColors.add(new Color("peru","#cd853f"));
		primaryColors.add(new Color("fuchsia","#ff00ff"));
		primaryColors.add(new Color("papayawhip","#ffefd5"));
		primaryColors.add(new Color("deepskyblue","#00bfff"));
		primaryColors.add(new Color("mediumspringgreen","#00fa9a"));
		primaryColors.add(new Color("darkgoldenrod","#b8860b"));
		primaryColors.add(new Color("violet","#ee82ee"));
		primaryColors.add(new Color("blanchedalmond","#ffebcd"));
		primaryColors.add(new Color("lightskyblue","#87cefa"));
		primaryColors.add(new Color("lawngreen","#7cfc00"));
		primaryColors.add(new Color("chocolate","#d2691e"));
		primaryColors.add(new Color("plum","#dda0dd"));
		primaryColors.add(new Color("bisque","#ffe4c4"));
		primaryColors.add(new Color("skyblue","#87ceeb"));
		primaryColors.add(new Color("chartreuse","#7fff00"));
		primaryColors.add(new Color("sienna","#a0522d"));
		primaryColors.add(new Color("orchid","#da70d6"));
		primaryColors.add(new Color("moccasin","#ffe4b5"));
		primaryColors.add(new Color("lightblue","#add8e6"));
		primaryColors.add(new Color("greenyellow","#adff2f"));
		primaryColors.add(new Color("saddlebrown","#8b4513"));
		primaryColors.add(new Color("mediumorchid","#ba55d3"));
		primaryColors.add(new Color("navajowhite","#ffdead"));
		primaryColors.add(new Color("powderblue","#b0e0e6"));
		primaryColors.add(new Color("lime","#00ff00"));
		primaryColors.add(new Color("maroon","#800000"));
		primaryColors.add(new Color("darkorchid","#9932cc"));
		primaryColors.add(new Color("peachpuff","#ffdab9"));
		primaryColors.add(new Color("paleturquoise","#afeeee"));
		primaryColors.add(new Color("limegreen","#32cd32"));
		primaryColors.add(new Color("darkred","#8b0000"));
		primaryColors.add(new Color("darkviolet","#9400d3"));
		primaryColors.add(new Color("mistyrose","#ffe4e1"));
		primaryColors.add(new Color("lightcyan","#e0ffff"));
		primaryColors.add(new Color("yellowgreen","#9acd32"));
		primaryColors.add(new Color("brown","#a52a2a"));
		primaryColors.add(new Color("darkmagenta","#8b008b"));
		primaryColors.add(new Color("lavenderblush","#fff0f5"));
		primaryColors.add(new Color("cyan","#00ffff"));
		primaryColors.add(new Color("darkolivegreen","#556b2f"));
		primaryColors.add(new Color("firebrick","#b22222"));
		primaryColors.add(new Color("purple","#800080"));
		primaryColors.add(new Color("seashell","#fff5ee"));
		primaryColors.add(new Color("aqua","#00ffff"));
		primaryColors.add(new Color("olivedrab","#6b8e23"));
		primaryColors.add(new Color("indianred","#cd5c5c"));
		primaryColors.add(new Color("indigo","#4b0082"));
		primaryColors.add(new Color("oldlace","#fdf5e6"));
		primaryColors.add(new Color("turquoise","#40e0d0"));
		primaryColors.add(new Color("olive","#808000"));
		primaryColors.add(new Color("rosybrown","#bc8f8f"));
		primaryColors.add(new Color("darkslateblue","#483d8b"));
		primaryColors.add(new Color("ivory","#fffff0"));
		primaryColors.add(new Color("mediumturquoise","#48d1cc"));
		primaryColors.add(new Color("darkkhaki","#bdb76b"));
		primaryColors.add(new Color("darksalmon","#e9967a"));
		primaryColors.add(new Color("blueviolet","#8a2be2"));
		primaryColors.add(new Color("honeydew","#f0fff0"));
		primaryColors.add(new Color("darkturquoise","#00ced1"));
		primaryColors.add(new Color("palegoldenrod","#eee8aa"));
		primaryColors.add(new Color("lightcoral","#f08080"));
		primaryColors.add(new Color("mediumpurple","#9370db"));
		primaryColors.add(new Color("mintcream","#f5fffa"));
		primaryColors.add(new Color("lightseagreen","#20b2aa"));
		primaryColors.add(new Color("cornsilk","#fff8dc"));
		primaryColors.add(new Color("salmon","#fa8072"));
		primaryColors.add(new Color("slateblue","#6a5acd"));
		primaryColors.add(new Color("azure","#f0ffff"));
		primaryColors.add(new Color("cadetblue","#5f9ea0"));
		primaryColors.add(new Color("beige","#f5f5dc"));
		primaryColors.add(new Color("lightsalmon","#ffa07a"));
		primaryColors.add(new Color("mediumslateblue","#7b68ee"));
		return primaryColors;
	}
}