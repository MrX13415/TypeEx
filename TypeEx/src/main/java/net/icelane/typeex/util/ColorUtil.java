package net.icelane.typeex.util;

import org.lwjgl.util.Color;

public final class ColorUtil {

	public static Color get(int argb) {
		return new Color(
				(argb >> 16) & 0xFF,
				(argb >> 8) & 0xFF,
				(argb >> 0) & 0xFF,
				(argb >> 24) & 0xFF);
	}
}
