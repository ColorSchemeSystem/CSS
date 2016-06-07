package parsers.style;

public class Elem {
	public String name;
	public String value;
	
	@Override
	public String toString() {
		String element = "    " + name + " : " + value + ";";
		return element;
	}
}
