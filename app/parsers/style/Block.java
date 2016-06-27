package parsers.style;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Block {
	public List<Attr> attrs = new ArrayList<Attr>();
	public Map<String,Elem> elements = new LinkedHashMap<String,Elem>();
	public boolean important = false;
	public boolean deleted = false;
	
	@Override
	public String toString() {
		String block = "";
		for(Attr attr : attrs) {
			block += attr.attrName + " ";
		}
		block += "{" + System.getProperty("line.separator");
		for(Elem elem : this.elements.values()) {
			block += elem.toString() + System.getProperty("line.separator");
		}
		block += "}";
		return block;
	}
	
	/**
	 * @param target
	 * @return
	 */
	public boolean isMergeable(Block target) {
		if(this.attrs.get(0).equals(target.attrs.get(0))) {
			return true;
			/*
			if((this.important && !target.important) || 
					(!this.important && target.important)) {
				return true;
			}
			*/
		}	
		return false;
	}
	
	/**
	 * @param target
	 */
	public void merge(Block target) {
		this.elements.putAll(target.elements);
		/*
		if(this.important && !target.important) {
			target.elements.putAll(this.elements);
			this.elements = target.elements;
		}	else if(!this.important && target.important) {
			this.elements.putAll(target.elements);
		}
		*/
	}
}