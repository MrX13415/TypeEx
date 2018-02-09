package net.icelane.typeex.test;

public abstract class KeyHandler {

	public static boolean handleKey(int keyCode, TextInfo textinfo) {
		// Retrieving first and last part early for performance reasons.
		String firstPart = textinfo.firstPart();
		String lastPart = textinfo.lastPart();
		
		boolean handled = true;
		
		switch(keyCode) {
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
				
			case  35: // end
				
				break;
			
			default: handled = false;
				break;
		}
		
		return handled;
	}
	
<<<<<<< HEAD


=======
>>>>>>> branch 'dev' of https://github.com/MrX13415/TypeEx.git
	private static void handleKey_BackSpace(TextInfo textinfo, String firstPart, String lastPart) {
		if (firstPart.length() == 0) return;
		
		// Remove a char from end of the first text part.
		firstPart = firstPart.substring(0, firstPart.length() - 1);
		
		// move the cursor position one to the left.
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : --textinfo.cursorPosition;
		
		textinfo.text = firstPart + lastPart;
	}
	
	private static void handleKey_Del(TextInfo textinfo, String firstPart, String lastPart) {
		if (firstPart.length() == 0) return;
		
		// Remove a char from the beginning of the last text part.
		lastPart = lastPart.substring(1, lastPart.length());
		
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
		
		// Index of the last new line char in the first text part. 
		int nlPosB = firstPart.lastIndexOf("\n");
		// Index of the new line char before the last new line char.
		int nlPosA = firstPart.substring(0, nlPosB).lastIndexOf("\n");

		// Cursor position in the current line.
		int curLinePos = textinfo.cursorPosition - nlPosB;
		// Line length of the previous line.
		int preLineLength = nlPosB - nlPosA;			
		
		// If the length of the current line is greater then
		// the length of the previous line, then just go to the end of the previous line.    
		// Otherwise go to the same position in the previous line.
		textinfo.cursorPosition = 
				curLinePos >= preLineLength ?
				nlPosB : nlPosA + curLinePos;	
	}
	
	private static void handleKey_ArrowDown(TextInfo textinfo, String firstPart, String lastPart) {
		if (!lastPart.contains("\n")) return;
		
		// Index of the last new line char in the first text part. 
		int nlPosA = firstPart.lastIndexOf("\n");
		// Index of the first new line char in the last text part.
		int nlPosB = lastPart.indexOf("\n");	
		// Index of the second new line char in the last text part.		
		int nlPosC = lastPart.indexOf("\n", nlPosB + 1);
		// No second line, so us the end of the text as pos. C.
		if (nlPosC < 0) nlPosC = lastPart.length();
		
		// Cursor position in the current line.
		int curLinePos = textinfo.cursorPosition - nlPosA;
		// Line length of next line.
		int postLineLength = nlPosC - nlPosB;		
		
		// If the length of the next line is smaller then
		// the length of the current line, then just go to the end of the next line.    
		// Otherwise go to the same position in the next line.
		textinfo.cursorPosition +=
				postLineLength < curLinePos ?
				nlPosC : nlPosB + curLinePos;
	}
	
	private static void handleKey_Home(TextInfo textinfo, String firstPart, String lastPart) {
		// test
		if (firstPart.endsWith("\n") || firstPart.length() == 0) return;
		if (firstPart.indexOf("\n") < textinfo.cursorPosition) textinfo.cursorPosition = 0;
	}
	
}
