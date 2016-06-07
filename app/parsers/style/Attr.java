package parsers.style;

public class Attr {
	public String attrType;
	public String attrName;
	
	/**
	 * 
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Attr)) {
			return false;
		}
		Attr attr = (Attr) obj;
		if(this.attrType.equals(attr.attrType) && this.attrName.equals(attr.attrName)) {
			return true;
		}	else	{
			return false;
		}
	}
}