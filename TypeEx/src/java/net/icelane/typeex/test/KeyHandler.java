package net.icelane.typeex.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import scala.languageFeature.reflectiveCalls;

/**
 * Handle keys and alter the given <code>TextInfo</code> object
 * accordingly, so it mimics the behavior of the text edit control.
 */
public abstract class KeyHandler {
	
	/**
	 * At text start:<br>
	 * Group 0: Check for one non word character followed by any whitespace but not new lines.<br> 
	 * Group 1: Check for new lines.<br>
	 * Group 2: Check any word characters followed by any whitespace.<br>
	 */
	private static final Pattern FirstWordPattern = Pattern.compile("^(\\W[^\\S\\n]*|\\n|\\w*\\s*)", Pattern.UNICODE_CHARACTER_CLASS);
	
	/**
	 * At text end:<br>
	 * Group 0: Check for one non word character followed by any whitespace but not new lines.<br>
	 * Group 1: Check for new lines.<br>
	 * Group 2: Check at least one word character followed by any whitespace.<br>
	 */
	private static final Pattern LastWordPattern = Pattern.compile("(\\W[^\\S\\n]*|\\n|\\w+[^\\S\\n]*)$", Pattern.UNICODE_CHARACTER_CLASS);
	
	private static boolean handled;		// key was handled
	private static boolean selection;	// do default selection handling	
	
	/**
	 * Handle keys and alter the given <code>TextInfo</code> object
	 * accordingly, so it mimics the behavior of the text edit control.<br>
	 * <br>
	 * The following keys will be handle by this function:<br>
	 *  - Backspace<br>
	 *  - Del<br>
	 *  - Arrow keys (Left, Right, Up, Down)<br>
	 *  - Home<br>
	 *  - End
	 *  - Ins
	 *  - CTRL + A<br>
	 *  <br>
	 *  Numpad keys are also handled.<br>
	 *  
	 * @param keyinfo A <code>KeyInfo</code> object.
	 * @param textinfo A <code>TextInfo</code> object.
	 * @return weather the given key in the <code>KeyInfo</code> object, has been handled.
	 */
	public static boolean handleKey(KeyInfo keyinfo, TextInfo textinfo) {
		// retrieving first and last part early for performance reasons.
		String firstPart = textinfo.firstPart();
		String lastPart = textinfo.lastPart();
		
		handled = true;								        // assume key will be handled 
		selection = keyinfo.getKeyCode() != KeyInfo.Shift;  //SHIFT: Don't trigger selection handling yet!
		int cursorPosition = textinfo.cursorPosition;       // save the current cursor position for selection handling 
		
		switch(keyinfo.getKeyCode()) {
		case KeyInfo.Esc: break; // ESC key writes a char otherwise

		case KeyInfo.Backspace: handleKey_BackSpace(textinfo, firstPart, lastPart);
			selection = false;
			break;
			 
		case KeyInfo.Del: handleKey_Del(textinfo, firstPart, lastPart);
			selection = false;
			break;
			
		case KeyInfo.ArrowLeft: handleKey_ArrowLeft(textinfo, firstPart, lastPart);
			break;
			
		case KeyInfo.ArrowRight: handleKey_ArrowRight(textinfo, firstPart, lastPart);
			break;
			
		case KeyInfo.ArrowUp: handleKey_ArrowUp(textinfo, firstPart, lastPart);	
			break;
			
		case KeyInfo.ArrowDown: handleKey_ArrowDown(textinfo, firstPart, lastPart);
			break;
			
		case KeyInfo.Home: handleKey_Home(textinfo, firstPart, lastPart);
			break;
			
		case KeyInfo.End: handleKey_End(textinfo, firstPart, lastPart);
			break;

		case KeyInfo.Ins: handleKey_Ins(textinfo, firstPart, lastPart);
			break;
			
		case KeyInfo.A: handleKey_A(textinfo);
			selection = false; // bypass selection code
			break;

		case KeyInfo.C: handleKey_C(textinfo);
			selection = false; // bypass selection code
			break;
			
		case KeyInfo.V: handleKey_V(textinfo);
			break;
			
		case KeyInfo.X: handleKey_X(textinfo);
			break;
		
		default: handled = false;
			break;
		}
			
		// default selection handling
		if (selection && handled) {
			if (KeyInfo.isShiftHeld()) {
				textinfo.setSelectionStart(cursorPosition);
				textinfo.setSelectionEnd();
			} else {
				textinfo.selected = false;
			}
		}

		return handled;
	}

	/**
	 * Returns the length of the first word of the given text.<br>
	 * A word is determined by multiple characters followed
	 * by any whitespace character at the end of beginning.
	 * @param text A text to match.
	 * @return The length of the first word or the length of the given text.
	 */
	public static int getFirstWordLength(String text) {	
		return getGroupLength(FirstWordPattern, text);
	}
	
	/**
	 * Returns the length of the last word of the given text.<br>
	 * A word is determined by multiple characters followed
	 * by any whitespace character at the end of beginning.
	 * @param text A text to match.
	 * @return The length of the last word or the length of the given text.
	 */
	public static int getLastWordLength(String text) {
		return getGroupLength(LastWordPattern, text);
	}
	
	/**
	 * Returns the length of the first group match by the given pattern.
	 * If no match was found or the length of the first group is zero,
	 * the length of the given text is returned.
	 * @param pattern The pattern.
	 * @param text A text to match.
	 * @return The length of the first matched group or the length of the given group.
	 */
	public static int getGroupLength(Pattern pattern, String text) {
		Matcher matcher = pattern.matcher(text);
		
		int charCount = matcher.find() ? matcher.group(0).length() : 0;		
		if (charCount <= 0) charCount = text.length();
		return charCount;
	}
	
	private static void handleKey_BackSpace(TextInfo textinfo, String firstPart, String lastPart) {
		if (firstPart.length() == 0) return;
		
		// handling of deletion while selection is active
		if (textinfo.selected) { 
			textinfo.removeSelection(); return;
		}
		
		int charCount = 1;
		
		if (KeyInfo.isControlHeld() && KeyInfo.isShiftHeld()) {
			// Number of chars from the beginning of the line to the cursor.
			charCount = firstPart.lastIndexOf("\n");
			charCount = textinfo.cursorPosition - charCount - 1;
			
		}else if (KeyInfo.isControlHeld()) {
			charCount = getLastWordLength(firstPart);
		}
		
		// Remove a number of chars from end of the first text part.
		firstPart = firstPart.substring(0, firstPart.length() - charCount);
		
		// move the cursor position to the left.
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : textinfo.cursorPosition - charCount;
		
		textinfo.text = firstPart + lastPart;
	}
	
	private static void handleKey_Del(TextInfo textinfo, String firstPart, String lastPart) {
		// do nothing if nothing is selected an shift is held ...
		if (!textinfo.selected && KeyInfo.isShiftHeld()) return;
		
		// do nothing if there is nothing to remove :)
		if (lastPart.length() == 0) return;	
		
		// handling of deletion while selection is active
		if (textinfo.selected) { 
			textinfo.removeSelection(); return;
		}
		
		int charCount = 1;
		
		if (KeyInfo.isControlHeld() && KeyInfo.isShiftHeld()) {
			// Number of chars to the end of the line from the cursor.
			charCount = lastPart.indexOf("\n");
			if (charCount < 0) charCount = lastPart.length();
			
		}else if (KeyInfo.isControlHeld()) {
			charCount = getFirstWordLength(lastPart);
		}
		
		// remove a number of chars from the beginning of the last text part.
		lastPart = lastPart.substring(charCount, lastPart.length());
		
		textinfo.text = firstPart + lastPart;
	}
	
	private static void handleKey_ArrowLeft(TextInfo textinfo, String firstPart, String lastPart) {
		int charCount = 1;
		if (KeyInfo.isControlHeld()) 
			charCount = getLastWordLength(firstPart);
		
		// move the cursor position one to the left.
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : textinfo.cursorPosition - charCount;
	}
	
	private static void handleKey_ArrowRight(TextInfo textinfo, String firstPart, String lastPart) {
		int charCount = 1;
		if (KeyInfo.isControlHeld())
			charCount = getFirstWordLength(lastPart);
		
		// move the cursor position one to the right.
		textinfo.cursorPosition =
				textinfo.cursorPosition >= textinfo.text.length() ?
				textinfo.text.length() : textinfo.cursorPosition + charCount;
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
		if (firstPart.length() == 0) return;
		// Find beginning of the previous line.
		int nlPos = firstPart.lastIndexOf("\n") + 1;
		
		if (KeyInfo.isControlHeld()) nlPos = 0;		// Always move to the beginning of the text.
		textinfo.cursorPosition = nlPos;			// Move to the beginning of line.
		
	
	}
	
	private static void handleKey_End(TextInfo textinfo, String firstPart, String lastPart) {
		if (lastPart.length() == 0) return;
	
		// Find end of the current line.
		int nlPos = lastPart.indexOf("\n");
		if (KeyInfo.isControlHeld() || nlPos < 0)
			textinfo.cursorPosition = textinfo.text.length();	// Move to the end of the text.
		else
			textinfo.cursorPosition += nlPos;					// Move to the end of line.
	}

	private static void handleKey_Ins(TextInfo textinfo, String firstPart, String lastPart) {
		textinfo.overwrite = !textinfo.overwrite;
	}
	
	private static void handleKey_A(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.selectAll();
		else handled = false; // type letter "A" 
	}

	private static void handleKey_C(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.copy();
		else handled = false; // type letter "C" 
	}
	
	private static void handleKey_V(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.past();
		else handled = false; // type letter "V" 
	}
	
	private static void handleKey_X(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.cut();
		else handled = false; // type letter "X" 
	}
}