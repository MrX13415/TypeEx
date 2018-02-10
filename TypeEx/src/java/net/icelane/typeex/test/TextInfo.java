package net.icelane.typeex.test;

/**
 * Holds text and additional information about cursor and editing. 
 */
public class TextInfo {
	
	/**
	 * The Text.
	 */
	public String text;
	/**
	 * The position of the cursor in the text.
	 */
	public int cursorPosition;
	
	public int markStart;
	public int markEnd;
	
	/**
	 * Weather overwriting of characters is enabled.
	 */
	public boolean overwrite; 
	
	public boolean isMarked;
	
	
	
	/**
	 * Returns the first part of the text, from the beginning to the current cursor position.
	 * @return The first text part.
	 */
	public String firstPart() {
		return firstPart(text, cursorPosition);
	}
	
	/**
	 * Returns the last part of the text, from the cursor position to the end of the text.
	 * @return The last part.
	 */
	public String lastPart() {
		return lastPart(text, cursorPosition);
	}
	
	/**
	 * Returns the first part of the given text, from the beginning to the given cursor position.
	 * @param text The text to split.
	 * @param cursorpos The cursor position to split the text.
	 * @return The first part of the text.
	 */
	public static String firstPart(String text, int cursorpos) {
		if (text.length() > 0 ){
			return text.substring(0, cursorpos);
		}
		return "";
	}
	
	/**
	 * Returns the last part of the given text, from the given cursor position to the end of the text.
	 * @param text The text to split.
	 * @param cursorpos The cursor position to split the text.
	 * @return The last part of the text.
	 */
	public static String lastPart(String text, int cursorpos) {
		if (cursorpos < text.length()) {
			return text.substring(cursorpos, text.length());
		}
		return "";
	}
}
