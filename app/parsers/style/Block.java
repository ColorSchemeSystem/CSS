package parsers.style;

import java.util.ArrayList;
import java.util.List;

public class Block {
	List<Attr> attrs = new ArrayList<Attr>();
	List<Elem> elements = new ArrayList<Elem>();
	
	@Override
	public String toString() {
		String block = "";
		for(Attr attr : attrs) {
			if(attr.attrType.equals("class")) {
				block += ".";
			}
			block += attr.attrName + " ";
		}
		block += "{" + System.getProperty("line.separator");
		for(Elem elem : this.elements) {
			block += elem.toString() + System.getProperty("line.separator");
		}
		block += "}";
		return block;
	}
}