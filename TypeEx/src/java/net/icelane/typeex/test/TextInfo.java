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
	
	/**
	 * Weather overwriting of characters is enabled.
	 */
	public boolean overwrite; 
	
	/**
	 * The first position of the selection. (A and B positions might be reversed.)
	 */
	public int selectionPosA;

	/**
	 * The second position of the selection. (A and B positions might be reversed.)
	 */
	public int selectionPosB;
	
	
	public boolean selected;
	
	/**
	 * The start position of the selection.
	 */
	public int selectionStart() {
		return selectionPosA < selectionPosB ? selectionPosA : selectionPosB;
	}
	
	/**
	 * The end position of the selection.
	 */
	public int selectionEnd() {
		return selectionPosB > selectionPosA ? selectionPosB : selectionPosA;
	}
	
	public void setSelectionStart() {
		if (selected) return;
		this.selected = true;
		
		this.selectionPosA = cursorPosition;
	}
	
	public void setSelectionEnd() {
		this.selectionPosB = cursorPosition;
	}
	
	public String preSelection() {
		if (selected && text.length() > 0 && selectionStart() > 0){
			return text.substring(0, selectionStart());
		}
		return "";
	}
	
	public String postSelection() {
		if (selected && text.length() > 0 && selectionEnd() < text.length()){
			return text.substring(selectionEnd());
		}
		return "";
	}

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
	
	public LineInfo currentLine() {
		return new LineInfo(this);
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
		
	public class LineInfo {	
		private TextInfo textinfo;
		private String line;
		private int startPos;
		private int endPos;
		
		public LineInfo(TextInfo textinfo) {
			super();
			this.textinfo = textinfo;
			getLineInfo();
		}

		private void getLineInfo() {
			this.startPos = firstPart(text, textinfo.cursorPosition).lastIndexOf("\n", textinfo.cursorPosition) + 1;			
			this.endPos = text.indexOf("\n", textinfo.cursorPosition);
			if (endPos <= 0) endPos = text.length();
			
			this.line = text.substring(startPos, endPos);
		}
		
		public TextInfo getTextInfo() {
			return textinfo;
		}
		
		public String text() {
			return line;
		}

		public int startPos() {
			return startPos;
		}

		public int endPos() {
			return endPos;
		}			
	}

}
