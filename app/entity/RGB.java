package entity;

public class RGB {
	public int r;
	public int g;
	public int b;
	
	public RGB() {}
	
	public RGB(int rgb) {
		this.r = rgb >> 16 & 0xff;
		this.g = rgb >> 8 & 0xff;
		this.b = rgb & 0xff;
	}
	
	public RGB(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public RGB(String hex) {
		this.r = Integer.parseInt(hex.substring(0,2),16);
		this.g = Integer.parseInt(hex.substring(2,4),16);
		this.b = Integer.parseInt(hex.substring(4,6),16);
	}
	
	/**
	 * @param rgb
	 * @return
	 */
	public int calcRGBDist(RGB rgb) {
		return calcDist(this.r,rgb.r) + calcDist(this.g,rgb.g) + calcDist(this.b,rgb.b);
	}
	
	/**
	 * @param n1
	 * @param n2
	 * @return
	 */
	private static int calcDist(int n1, int n2) {
		int diff = n1 - n2;
		return (int) Math.sqrt((double) diff * diff);
	}
	
	@Override
	public String toString() {
		return "(r,g,b) = (" + this.r + "," + this.g + "," + this.b + ")";
	}
	
	/**
	 * rgb表記を16進数
	 * @return
	 */
	public String toHexString() {
		return  pad(Integer.toHexString(this.r).toUpperCase()) + 
				pad(Integer.toHexString(this.g).toUpperCase()) + 
				pad(Integer.toHexString(this.b).toUpperCase());
	}
	
	/**
	 * @param hex
	 * @return
	 */
	private static String pad(String hex) {
		if(hex.length() == 1) {
			return "0" + hex;
		}	else	{
			return hex;
		}
	}
}