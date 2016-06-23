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
	public List<Color> getStandardColors() {
		List<Color> cssColors = new ArrayList<Color>();
		cssColors.add(new Color("#FF0000","red"));
		cssColors.add(new Color("#800000","maroon"));
		cssColors.add(new Color("#000000","black"));
		cssColors.add(new Color("#FFFF00","yellow"));
		cssColors.add(new Color("#808000","olive"));
		cssColors.add(new Color("#808080","gray"));
		cssColors.add(new Color("#00FF00","lime"));
		cssColors.add(new Color("#008000","green"));
		cssColors.add(new Color("#C0C0C0","silver"));
		cssColors.add(new Color("#00FFFF","aqua"));
		cssColors.add(new Color("#008080","teal"));
		cssColors.add(new Color("#FFFFFF","white"));
		cssColors.add(new Color("#0000FF","blue"));
		cssColors.add(new Color("#000080","navy"));
		cssColors.add(new Color("#FFA500","orange"));
		cssColors.add(new Color("#FF00FF","fuchsia"));
		cssColors.add(new Color("#800080","purple"));
		return cssColors;
	}
	
	/**
	 * WEBセーフカラーを表示
	 * @return
	 */
	public List<Color> getWebSafeColors() {
		final int[] nums = {0,51,102,153,204,255};
		List<Color> webSafeColors = new ArrayList<Color>();
		Map<String,String> colorNamesMap = this.getColorNamesMap();
		for(int r : nums) {
			for(int g : nums) {
				for(int b : nums) {
					RGB rgb = new RGB(r,g,b);
					String hex = "#" + rgb.toHexString();
					Color color = null;
					if(colorNamesMap.containsKey(hex)) {
						color = new Color(hex,colorNamesMap.get(hex));
					}	else	{
						color = new Color(hex);
					}
					webSafeColors.add(color);
				}
			}
		}
		return webSafeColors;
	}
	
	/**s
	 * @return
	 */
	private Map<String,String> getColorNamesMap() {
		List<Color> primaryColors = this.getPrimaryColors();
		Map<String,String> result = new HashMap<String,String>();
		for(Color color : primaryColors) {
			result.put(color.colorHex, color.colorName);
		}
		return result;
	}
	
	/**
	 * @return
	 */
	public List<Color> getPrimaryColors() {
		List<Color> primaryColors = new ArrayList<Color>();
		primaryColors.add(new Color("#000000","black"));
		primaryColors.add(new Color("#F0F8FF","aliceblue"));
		primaryColors.add(new Color("#008B8B","darkcyan"));
		primaryColors.add(new Color("#FFFFE0","lightyellow"));
		primaryColors.add(new Color("#FF7F50","coral"));
		primaryColors.add(new Color("#696969","dimgray"));
		primaryColors.add(new Color("#E6E6FA","lavender"));
		primaryColors.add(new Color("#008080","teal"));
		primaryColors.add(new Color("#FAFAD2","lightgoldenrodyellow"));
		primaryColors.add(new Color("#FF6347","tomato"));
		primaryColors.add(new Color("#808080","gray"));
		primaryColors.add(new Color("#B0C4DE","lightsteelblue"));
		primaryColors.add(new Color("#2F4F4F","darkslategray"));
		primaryColors.add(new Color("#FFFACD","lemonchiffon"));
		primaryColors.add(new Color("#FF4500","orangered"));
		primaryColors.add(new Color("#A9A9A9","darkgray"));
		primaryColors.add(new Color("#778899","lightslategray"));
		primaryColors.add(new Color("#006400","darkgreen"));
		primaryColors.add(new Color("#F5DEB3","wheat"));
		primaryColors.add(new Color("#FF0000","red"));
		primaryColors.add(new Color("#C0C0C0","silver"));
		primaryColors.add(new Color("#708090","slategray"));
		primaryColors.add(new Color("#008000","green"));
		primaryColors.add(new Color("#DEB887","burlywood"));
		primaryColors.add(new Color("#DC143C","crimson"));
		primaryColors.add(new Color("#D3D3D3","lightgrey"));
		primaryColors.add(new Color("#4682B4","steelblue"));
		primaryColors.add(new Color("#228B22","forestgreen"));
		primaryColors.add(new Color("#D2B48C","tan"));
		primaryColors.add(new Color("#C71585","mediumvioletred"));
		primaryColors.add(new Color("#DCDCDC","gainsboro"));
		primaryColors.add(new Color("#4169E1","royalblue"));
		primaryColors.add(new Color("#2E8B57","seagreen"));
		primaryColors.add(new Color("#F0E68C","khaki"));
		primaryColors.add(new Color("#FF1493","deeppink"));
		primaryColors.add(new Color("#F5F5F5","whitesmoke"));
		primaryColors.add(new Color("#191970","midnightblue"));
		primaryColors.add(new Color("#3CB371","mediumseagreen"));
		primaryColors.add(new Color("#FFFF00","yellow"));
		primaryColors.add(new Color("#FF69B4","hotpink"));
		primaryColors.add(new Color("#FFFFFF","white"));
		primaryColors.add(new Color("#000080","navy"));
		primaryColors.add(new Color("#66CDAA","mediumaquamarine"));
		primaryColors.add(new Color("#FFD700","gold"));
		primaryColors.add(new Color("#DB7093","palevioletred"));
		primaryColors.add(new Color("#FFFAFA","snow"));
		primaryColors.add(new Color("#00008B","darkblue"));
		primaryColors.add(new Color("#8FBC8F","darkseagreen"));
		primaryColors.add(new Color("#FFA500","orange"));
		primaryColors.add(new Color("#FFC0CB","pink"));
		primaryColors.add(new Color("#F8F8FF","ghostwhite"));
		primaryColors.add(new Color("#0000CD","mediumblue"));
		primaryColors.add(new Color("#7FFFD4","aquamarine"));
		primaryColors.add(new Color("#F4A460","sandybrown"));
		primaryColors.add(new Color("#FFB6C1","lightpink"));
		primaryColors.add(new Color("#FFFAF0","floralwhite"));
		primaryColors.add(new Color("#0000FF","blue"));
		primaryColors.add(new Color("#98FB98","palegreen"));
		primaryColors.add(new Color("#FF8C00","darkorange"));
		primaryColors.add(new Color("#D8BFD8","thistle"));
		primaryColors.add(new Color("#FAF0E6","linen"));
		primaryColors.add(new Color("#1E90FF","dodgerblue"));
		primaryColors.add(new Color("#90EE90","lightgreen"));
		primaryColors.add(new Color("#DAA520","goldenrod"));
		primaryColors.add(new Color("#FF00FF","magenta"));
		primaryColors.add(new Color("#FAEBD7","antiquewhite"));
		primaryColors.add(new Color("#6495ED","cornflowerblue"));
		primaryColors.add(new Color("#00FF7F","springgreen"));
		primaryColors.add(new Color("#CD853F","peru"));
		primaryColors.add(new Color("#FF00FF","fuchsia"));
		primaryColors.add(new Color("#FFEFD5","papayawhip"));
		primaryColors.add(new Color("#00BFFF","deepskyblue"));
		primaryColors.add(new Color("#00FA9A","mediumspringgreen"));
		primaryColors.add(new Color("#B8860B","darkgoldenrod"));
		primaryColors.add(new Color("#EE82EE","violet"));
		primaryColors.add(new Color("#FFEBCD","blanchedalmond"));
		primaryColors.add(new Color("#87CEFA","lightskyblue"));
		primaryColors.add(new Color("#7CFC00","lawngreen"));
		primaryColors.add(new Color("#D2691E","chocolate"));
		primaryColors.add(new Color("#DDA0DD","plum"));
		primaryColors.add(new Color("#FFE4C4","bisque"));
		primaryColors.add(new Color("#87CEEB","skyblue"));
		primaryColors.add(new Color("#7FFF00","chartreuse"));
		primaryColors.add(new Color("#A0522D","sienna"));
		primaryColors.add(new Color("#DA70D6","orchid"));
		primaryColors.add(new Color("#FFE4B5","moccasin"));
		primaryColors.add(new Color("#ADD8E6","lightblue"));
		primaryColors.add(new Color("#ADFF2F","greenyellow"));
		primaryColors.add(new Color("#8B4513","saddlebrown"));
		primaryColors.add(new Color("#BA55D3","mediumorchid"));
		primaryColors.add(new Color("#FFDEAD","navajowhite"));
		primaryColors.add(new Color("#B0E0E6","powderblue"));
		primaryColors.add(new Color("#00FF00","lime"));
		primaryColors.add(new Color("#800000","maroon"));
		primaryColors.add(new Color("#9932CC","darkorchid"));
		primaryColors.add(new Color("#FFDAB9","peachpuff"));
		primaryColors.add(new Color("#AFEEEE","paleturquoise"));
		primaryColors.add(new Color("#32CD32","limegreen"));
		primaryColors.add(new Color("#8B0000","darkred"));
		primaryColors.add(new Color("#9400D3","darkviolet"));
		primaryColors.add(new Color("#FFE4E1","mistyrose"));
		primaryColors.add(new Color("#E0FFFF","lightcyan"));
		primaryColors.add(new Color("#9ACD32","yellowgreen"));
		primaryColors.add(new Color("#A52A2A","brown"));
		primaryColors.add(new Color("#8B008B","darkmagenta"));
		primaryColors.add(new Color("#FFF0F5","lavenderblush"));
		primaryColors.add(new Color("#00FFFF","cyan"));
		primaryColors.add(new Color("#556B2F","darkolivegreen"));
		primaryColors.add(new Color("#B22222","firebrick"));
		primaryColors.add(new Color("#800080","purple"));
		primaryColors.add(new Color("#FFF5EE","seashell"));
		primaryColors.add(new Color("#00FFFF","aqua"));
		primaryColors.add(new Color("#6B8E23","olivedrab"));
		primaryColors.add(new Color("#CD5C5C","indianred"));
		primaryColors.add(new Color("#4B0082","indigo"));
		primaryColors.add(new Color("#FDF5E6","oldlace"));
		primaryColors.add(new Color("#40E0D0","turquoise"));
		primaryColors.add(new Color("#808000","olive"));
		primaryColors.add(new Color("#BC8F8F","rosybrown"));
		primaryColors.add(new Color("#483D8B","darkslateblue"));
		primaryColors.add(new Color("#FFFFF0","ivory"));
		primaryColors.add(new Color("#48D1CC","mediumturquoise"));
		primaryColors.add(new Color("#BDB76B","darkkhaki"));
		primaryColors.add(new Color("#E9967A","darksalmon"));
		primaryColors.add(new Color("#8A2BE2","blueviolet"));
		primaryColors.add(new Color("#F0FFF0","honeydew"));
		primaryColors.add(new Color("#00CED1","darkturquoise"));
		primaryColors.add(new Color("#EEE8AA","palegoldenrod"));
		primaryColors.add(new Color("#F08080","lightcoral"));
		primaryColors.add(new Color("#9370DB","mediumpurple"));
		primaryColors.add(new Color("#F5FFFA","mintcream"));
		primaryColors.add(new Color("#20B2AA","lightseagreen"));
		primaryColors.add(new Color("#FFF8DC","cornsilk"));
		primaryColors.add(new Color("#FA8072","salmon"));
		primaryColors.add(new Color("#6A5ACD","slateblue"));
		primaryColors.add(new Color("#F0FFFF","azure"));
		primaryColors.add(new Color("#5F9EA0","cadetblue"));
		primaryColors.add(new Color("#F5F5DC","beige"));
		primaryColors.add(new Color("#FFA07A","lightsalmon"));
		primaryColors.add(new Color("#7B68EE","mediumslateblue"));
		return primaryColors;
	}
}