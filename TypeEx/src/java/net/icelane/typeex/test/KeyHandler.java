package net.icelane.typeex.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle keys and alter the given <code>TextInfo</code> object
 * accordingly, so it mimics the behavior of the text edit control.<br>
 */
public abstract class KeyHandler {

	private static final Pattern del_wordPattern = Pattern.compile("^([^A-za-z][^\\S\\n]*|\\n|\\w*\\s*)");  //\\s*\\w*\\s+
	private static final Pattern back_WordPattern = Pattern.compile("([^A-za-z][^\\S\\n]*|\\n|\\w+\\s*)$");
	
	/**
	 * Handle keys and alter the given <code>TextInfo</code> object
	 * accordingly, so it mimics the behavior of the text edit control.<br>
	 * <br>
	 * The following keys will be handle by this function:<br>
	 *  - Backspace<br>
	 *  - Del<br>
	 *  - Arrow keys (Left, Right, Up, Down)<br>
	 *  - Home*<br>
	 *  - End*<br>
	 *  <br>
	 *  *Also handled on the num pad.<br>
	 *  
	 * @param keyCode A keyCode of the key to take care of.
	 * @param textinfo A <code>TextInfo</code> object.
	 * @return
	 */
	public static boolean handleKey(KeyInfo keyinfo, TextInfo textinfo) {
		// Retrieving first and last part early for performance reasons.
		String firstPart = textinfo.firstPart();
		String lastPart = textinfo.lastPart();
		
		boolean handled = true;
		
		switch(keyinfo.getKeyCode()) {
			case  27: break; // ESC key writes a char otherwise
			
			case   8: handleKey_BackSpace(textinfo, firstPart, lastPart);
				break;
				
			case 127: handleKey_Del(textinfo, firstPart, lastPart);
				break;
				
			case  37: handleKey_ArrowLeft(textinfo, firstPart, lastPart);
				break;
				
			case  39: handleKey_ArrowRight(textinfo, firstPart, lastPart);
				break;
				
			case  38: handleKey_ArrowUp(textinfo, firstPart, lastPart);	
				break;
				
			case  40: handleKey_ArrowDown(textinfo, firstPart, lastPart);
				break;
				
			case  36: handleKey_Home(textinfo, firstPart, lastPart);
				break;
				
			case  35: handleKey_End(textinfo, firstPart, lastPart);
				break;

		
		default: handled = false;
			break;
		}
		
		return handled;
	}

	private static void handleKey_BackSpace(TextInfo textinfo, String firstPart, String lastPart) {
		if (firstPart.length() == 0) return;
		
		int charCount = 1;
		
		if (KeyInfo.isControlHeld() && KeyInfo.isShiftHeld()) {
			charCount = firstPart.lastIndexOf("\n");
			charCount = textinfo.cursorPosition - charCount - 1;
			
		}else if (KeyInfo.isControlHeld()) {
			Matcher matcher = back_WordPattern.matcher(firstPart);
			charCount = matcher.find() ? matcher.group(0).length() : 0;
			if (charCount <= 0) charCount = firstPart.length();
		}
		
		// Remove a char from end of the first text part.
		firstPart = firstPart.substring(0, firstPart.length() - charCount);
		
		// move the cursor position one to the left.
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : textinfo.cursorPosition - charCount;
		
		textinfo.text = firstPart + lastPart;
	}
	
	private static void handleKey_Del(TextInfo textinfo, String firstPart, String lastPart) {
		if (lastPart.length() == 0) return;
		
		int charCount = 1;
		
		if (KeyInfo.isControlHeld() && KeyInfo.isShiftHeld()) {
			charCount = lastPart.indexOf("\n");
			if (charCount <= 0) charCount = lastPart.length();
			
		}else if (KeyInfo.isControlHeld()) {
			Matcher matcher = del_wordPattern.matcher(lastPart);
			charCount = matcher.find() ? matcher.group(0).length() : 0;
			if (charCount <= 0) charCount = lastPart.length();
		}
		
		// Remove a char from the beginning of the last text part.
		lastPart = lastPart.substring(charCount, lastPart.length());
		
		textinfo.text = firstPart + lastPart;
	}
	
	private static void handleKey_ArrowLeft(TextInfo textinfo, String firstPart, String lastPart) {
		// Move the cursor position one to the left.
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : --textinfo.cursorPosition;
	}
	
	private static void handleKey_ArrowRight(TextInfo textinfo, String firstPart, String lastPart) {
		// Move the cursor position one to the right.
		textinfo.cursorPosition =
				textinfo.cursorPosition >= textinfo.text.length() ?
				textinfo.text.length() : ++textinfo.cursorPosition;
	}
		
	private static void handleKey_ArrowUp(TextInfo textinfo, String firstPart, String lastPart) {
		if (!firstPart.contains("\n")) return;

		// Pos A: Index of the new line char before the last new line char.
		// Pos B: Index of the last new line char in the first text part.
		int nlPosB = firstPart.lastIndexOf("\n");
		int nlPosA = firstPart.substring(0, nlPosB).lastIndexOf("\n");

		int curLinePos = textinfo.cursorPosition - nlPosB;	// Cursor position in the current line.
		int preLineLength = nlPosB - nlPosA;				// Line length of the previous line.
		
		// If the length of the current line is greater then
		// the length of the previous line, then just go to the end of the previous line.    
		// Otherwise go to the same position in the previous line.
		textinfo.cursorPosition = 
				curLinePos >= preLineLength ?
				nlPosB : nlPosA + curLinePos;	
	}
	
	private static void handleKey_ArrowDown(TextInfo textinfo, String firstPart, String lastPart) {
		if (!lastPart.contains("\n")) return;
		
		// Pos A: Index of the last new line char in the first text part. 
		// Pos B: Index of the first new line char in the last text part.
		// Pos C: Index of the second new line char in the last text part.	
		int nlPosA = firstPart.lastIndexOf("\n");
		int nlPosB = lastPart.indexOf("\n");			
		int nlPosC = lastPart.indexOf("\n", nlPosB + 1);		
		
		if (nlPosC < 0) nlPosC = lastPart.length();			// No second line, so us the end of the text as pos. C.
		
		int curLinePos = textinfo.cursorPosition - nlPosA; 	// Cursor position in the current line.
		int postLineLength = nlPosC - nlPosB;				// Line length of next line.
		
		// If the length of the next line is smaller then
		// the length of the current line, then just go to the end of the next line.    
		// Otherwise go to the same position in the next line.
		textinfo.cursorPosition +=
				postLineLength < curLinePos ?
				nlPosC : nlPosB + curLinePos;
	}
	
	private static void handleKey_Home(TextInfo textinfo, String firstPart, String lastPart) {
		// Checks for cursor on first position and start of document.
		if (firstPart.endsWith("\n") || firstPart.length() == 0) return;
		
		// Checks for first line and places cursor to start of document.
		if (firstPart.indexOf("\n") > textinfo.cursorPosition) {
			textinfo.cursorPosition = 0;
		} else {
			textinfo.cursorPosition = firstPart.lastIndexOf("\n") + 1;
		}		
	}
	
	private static void handleKey_End(TextInfo textinfo, String firstPart, String lastPart) {
		// Checks for cursor on last position and end of document
		if (lastPart.startsWith("\n") || textinfo.cursorPosition == textinfo.text.length()) return;
		
		if (lastPart.indexOf("\n") == -1) {
			textinfo.cursorPosition = textinfo.text.length();
		}else {
			textinfo.cursorPosition += lastPart.indexOf("\n");
		}
		
	}
	
}
