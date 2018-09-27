package net.icelane.typeex.util;


public class Color {

	public float alpha;
	public float red;
	public float green;
	public float blue;
	
	public Color(int argb) {
		org.lwjgl.util.Color color = get(argb);
		alpha = getFloat(color.getAlpha());
		red = getFloat(color.getRed());
		green = getFloat(color.getGreen());
		blue = getFloat(color.getBlue());
	}
		
	public static org.lwjgl.util.Color get(int argb) {
		return new org.lwjgl.util.Color(
				(argb >> 16) & 0xFF,
				(argb >> 8) & 0xFF,
				(argb >> 0) & 0xFF,
				(argb >> 24) & 0xFF);
	}
	
	public static int hsb2argb(float hue, float saturation, float brightness) {
		java.awt.Color awtc = java.awt.Color.getHSBColor(hue, saturation, brightness);
		
		int argb = 0; 
		argb += 255 << 24;
		argb += awtc.getRed() << 16;
		argb += awtc.getGreen() << 8;
		argb += awtc.getBlue();
		
		return argb;
	}
	
	public static float getFloat(int color) {
		return 1f / 255f * color;
	}
	
	public static float getInt(float color) {
		return Math.round(255 / 1f * color);
	}
		
	@Override
	public String toString() {
		return String.format("[a: %s, r: %s, g: %s, b: %s]", alpha, red, green, blue);
	}

	public String toStringInt() {
		return String.format("[a: %s, r: %s, g: %s, b: %s]", getInt(alpha), getInt(red), getInt(green), getInt(blue));
	}
}
