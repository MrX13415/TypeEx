package net.icelane.typeex.test;

public class TextInfo {
	
	public String text;
	public int cursorPosition;

	public String firstPart() {
		return firstPart(text, cursorPosition);
	}
	
	public String lastPart() {
		return lastPart(text, cursorPosition);
	}
	
	public static String firstPart(String text, int cursorpos) {
		if (text.length() > 0 ){
			return text.substring(0, cursorpos);
		}
		return "";
	}
	
	public static String lastPart(String text, int cursorpos) {
		if (cursorpos < text.length()) {
			return text.substring(cursorpos, text.length());
		}
		return "";
	}
}
