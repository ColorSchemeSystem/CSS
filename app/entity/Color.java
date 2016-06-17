package entity;

public class Color {
	public String colorHex;
	public String colorName;
	
	public Color(String colorHex, String colorName) {
		this.colorHex = colorHex;
		this.colorName = colorName;
	}
	
	public Color(String colorHex) {
		this.colorHex = colorHex;
		this.colorName = null;
	}
}
