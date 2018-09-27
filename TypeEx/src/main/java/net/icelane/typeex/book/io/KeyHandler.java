package net.icelane.typeex.book.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.icelane.typeex.book.io.TextInfo.TextChunk;

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

		case KeyInfo.Enter:
		case KeyInfo.Num_Enter: handleKey_Enter(textinfo, firstPart, lastPart);
			break;
			
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
			
		case KeyInfo.Z: handleKey_Z(textinfo);
			break;
			
		case KeyInfo.Y: handleKey_Y(textinfo);
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
	
	private static void handleKey_Enter(TextInfo textinfo, String firstPart, String lastPart) {
		textinfo.insert(textinfo.newLine);
	}
	
	private static void handleKey_BackSpace(TextInfo textinfo, String firstPart, String lastPart) {
		// handling of deletion while selection is active
		if (textinfo.selected) { 
			textinfo.removeSelection(); return;
		}
		
		if (firstPart.length() == 0) return;
		
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
		
		textinfo.text(firstPart + lastPart);
	}
	
	private static void handleKey_Del(TextInfo textinfo, String firstPart, String lastPart) {
		// do nothing if nothing is selected an shift is held ...
		if (!textinfo.selected && KeyInfo.isShiftHeld()) return;

		// handling of deletion while selection is active
		if (textinfo.selected) { 
			textinfo.removeSelection(); return;
		}
		
		// do nothing if there is nothing to remove :)
		if (lastPart.length() == 0) return;	
		
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
		
		textinfo.text(firstPart + lastPart);
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
				textinfo.cursorPosition >= textinfo.text().length() ?
				textinfo.text().length() : textinfo.cursorPosition + charCount;
	}
		
	private static void handleKey_ArrowUp(TextInfo textinfo, String firstPart, String lastPart) {
		
		int lineCount = textinfo.lineCount(firstPart);
		if (lineCount < 1) return;
		
		TextChunk currentLine = textinfo.line(lineCount - 1);  // current line
		TextChunk[] chunks = currentLine.wordWrap();  // current line

		TextChunk prev = null; // previous line (chunk)
		TextChunk curr = null; // current line (chunk)
		
		// Find the current line/chunk and the previous line/chunk to jump to.
		if (chunks.length >= 2 && !chunks[0].isCursorWithin()) {
			// Wrapped lines:
			// The current line is splitted into chunks
			// and the cursor is not in the first "chunk".
			 
			for (TextChunk chunk : chunks) {
				if (chunk.isCursorWithin()) {
					curr = chunk;
					break; 
				}
				prev = chunk;
			}
			
		} else if (lineCount >= 2) {
			// Normal lines:
			// We are dealing with a "normal" (non splitted) line
			// or the cursor is in the first "chunk" of a splitted line.
			// Also there is at least another line we can jump to.
			
			curr = chunks[0];
			currentLine = textinfo.line(lineCount - 2);  // previous line
			chunks = currentLine.wordWrap();
			prev = chunks[chunks.length - 1];
		}
		
		// no line to jump to found ...
		if (prev == null) return;
		
		int cursorPos = prev.cursorPosition(curr.cursorWidth()); 

		// If the cursor position of the current line is greater then
		// the length of the previous line, then just go to the end of the previous line.    
		// Otherwise go to the same position in the previous line.
		textinfo.cursorPosition = 
				cursorPos >= prev.text.length() ?
				prev.end : prev.start + cursorPos;
	}
	
	private static void handleKey_ArrowDown(TextInfo textinfo, String firstPart, String lastPart) {
		
		int lineCountF = textinfo.lineCount(firstPart);
		int lineCountL = textinfo.lineCount(lastPart);
		if (lineCountL < 1) return;
		
		TextChunk currLine = textinfo.line(lineCountF - 1); // current line
		TextChunk nextLine = textinfo.line(lineCountF); // next line		
		TextChunk[] currChunks = currLine.wordWrap(); // current line
		TextChunk[] nextChunks = nextLine.wordWrap(); // next line
		
		TextChunk curr = null; // current line
		TextChunk next = null; // next line
		
		if (currChunks.length >= 2 && !currChunks[currChunks.length - 1].isCursorWithin()) {
			// Wrapped lines:
			// The current line is splitted into chunks
			// and the cursor is not in the last "chunk".

			for (int i = 0; i < currChunks.length - 1; i++) {
				if (currChunks[i].isCursorWithin()) {
					curr = currChunks[i];
					next = currChunks[i + 1];
				}
			}
			
		} else if (lineCountL >= 2) {
			// Normal lines:
			// We are dealing with a "normal" (non splitted) line
			// or the cursor is in the last "chunk" of a splitted line.
			// Also there is at least another line we can jump to.
			
			curr = currChunks[currChunks.length - 1];
			next = nextChunks[0];
		}
		
		// Nothing to jump to ...
		if (curr == null) return;
		if (next == null) return;
		
		int cursorPos = next.cursorPosition(curr.cursorWidth());

		// If the length of the next line is smaller then
		// the length of the current line, then just go to the end of the next line.    
		// Otherwise go to the same position in the next line.
		textinfo.cursorPosition = 
				next.text.length() < cursorPos ? 
				next.end : next.start + cursorPos;
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
			textinfo.cursorPosition = textinfo.text().length();	// Move to the end of the text.
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
	
	private static void handleKey_Z(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.undo();
		else handled = false;
	}
	
	private static void handleKey_Y(TextInfo textinfo) {
		if (KeyInfo.isControlHeld()) textinfo.redo();
		else handled = false;
	}
}