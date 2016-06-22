package parsers.style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Style {
	public List<Block> blocks = new ArrayList<Block>();
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		final String ls = System.getProperty("line.separator");
		String style = "@charset \"UTF-8\";" + ls + ls;
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
		this.devideBlocks();
		this.mergeBlocks();
	}
	
	/**
	 * 
	 */
	public void devideBlocks() {
		List<Block> newBlockes = new ArrayList<Block>();
		Iterator<Block> iterator = this.blocks.iterator();
		while(iterator.hasNext()){
			Block block = iterator.next();
			if(block.attrs.size() >= 2) {
				for(Attr attr : block.attrs) {
					Block newBlock = new Block();
					newBlock.attrs.add(attr);
					newBlock.elements = block.elements;
					newBlockes.add(newBlock);
				}
				iterator.remove();
			}
		}
		this.blocks.addAll(newBlockes);
	}
	
	/**
	 * 
	 */
	public void mergeBlocks() {
		for(int i = 0; i < this.blocks.size(); i++) {
			for(int j = i + 1; j < this.blocks.size(); j++) {
				if(this.blocks.get(i).isMergeable(this.blocks.get(j))) {
					this.blocks.get(i).merge(this.blocks.get(j));
					this.blocks.get(j).deleted = true;
				}
			}
		}
		Iterator<Block> iterator = this.blocks.iterator();
		while(iterator.hasNext()){
			if(iterator.next().deleted) {
				iterator.remove();
			}
		}
	}
}