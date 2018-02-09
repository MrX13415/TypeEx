package net.icelane.typeex.test;

public abstract class KeyHandler {

	public static boolean handleKey(int keyCode, TextInfo textinfo) {
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
				
			case  38:  handleKey_ArrowUp(textinfo, firstPart, lastPart);	
				break;
				
			case  40: handleKey_ArrowDown(textinfo, firstPart, lastPart);
				break;
				
			case  36: // home
				
				break;
			case  35: // end
				
				break;
			
			default: handled = false;
				break;
		}
		
		return handled;
	}
	
	private static void handleKey_BackSpace(TextInfo textinfo, String firstPart, String lastPart) {
		String txt = firstPart;
		txt = (txt.length() > 0 ? txt.substring(0, txt.length() - 1) : "") + lastPart; //getLastTextPt(cursorPositon);
		
		textinfo.text = txt;
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : --textinfo.cursorPosition;
	}
	
	private static void handleKey_Del(TextInfo textinfo, String firstPart, String lastPart) {
		String txt = lastPart;
		txt = firstPart + (txt.length() > 0 ? txt.substring(1, txt.length()) : "");
		
		textinfo.text = txt;
	}
	
	private static void handleKey_ArrowLeft(TextInfo textinfo, String firstPart, String lastPart) {
		textinfo.cursorPosition =
				textinfo.cursorPosition <= 0 ?
				0 : --textinfo.cursorPosition;
	}
	
	private static void handleKey_ArrowRight(TextInfo textinfo, String firstPart, String lastPart) {
		textinfo.cursorPosition =
				textinfo.cursorPosition >= textinfo.text.length() ?
				textinfo.text.length() : ++textinfo.cursorPosition;
	}
		
	private static void handleKey_ArrowUp(TextInfo textinfo, String firstPart, String lastPart) {
		String txt = firstPart;
		if (!txt.contains("\n")) return;
		
		int nlPosB = txt.lastIndexOf("\n");
		int nlPosA = txt.substring(0, nlPosB).lastIndexOf("\n");

		int curLineLength = textinfo.cursorPosition - nlPosB;
		int preLineLength = nlPosB - nlPosA;			
		
		textinfo.cursorPosition = 
				curLineLength >= preLineLength ?
				nlPosB : nlPosA + curLineLength;	
	}
	
	private static void handleKey_ArrowDown(TextInfo textinfo, String firstPart, String lastPart) {
		String txt = firstPart;
		int nlPosA = txt.lastIndexOf("\n");

		txt = lastPart;
		if (!txt.contains("\n")) return;
		
		int nlPosB = txt.indexOf("\n");
		int nlPosC = txt.indexOf("\n", nlPosB + 1);
		if (nlPosC < 0) nlPosC = txt.length();
			
		int curLinePos = textinfo.cursorPosition - nlPosA;
		int postLineLength = nlPosC - nlPosB;		
		
		textinfo.cursorPosition +=
				postLineLength < curLinePos ?
				nlPosC : nlPosB + curLinePos;
	}
}
