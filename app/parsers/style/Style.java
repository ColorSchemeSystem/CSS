package parsers.style;

import java.util.ArrayList;
import java.util.List;

public class Style {
	public List<Block> blocks = new ArrayList<Block>();
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		String style = "@charset \"UTF-8\"" + ls + ls;
		for(Block block : blocks) {
			style += block.toString() + ls + ls;
		}
		return style;
	}
	
	/**
	 * 
	 * @param targetStyle
	 */
	public void mergeStyle(Style targetStyle) {
		this.blocks.addAll(targetStyle.blocks);
	}
}