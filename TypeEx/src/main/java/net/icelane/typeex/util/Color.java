package net.icelane.typeex.util;


public class Color {

	public final float alpha;
	public final float red;
	public final float green;
	public final float blue;
	
	public Color(int argb) {
		org.lwjgl.util.Color color = get(argb);
		alpha = getFloat(color.getAlpha());
		red = getFloat(color.getRed());
		green = getFloat(color.getGreen());
		blue = getFloat(color.getBlue());
	}
	
	public static float getFloat(int color) {
		return 1f / 255f * color;
	}
		
	public static org.lwjgl.util.Color get(int argb) {
		return new org.lwjgl.util.Color(
				(argb >> 16) & 0xFF,
				(argb >> 8) & 0xFF,
				(argb >> 0) & 0xFF,
				(argb >> 24) & 0xFF);
	}

}
