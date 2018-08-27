package net.icelane.typeex.book.io;

public class UndoData {

	public final String text;
	public final int cursorPosition;
	public final boolean selected;
	public final int selectionPosA;
	public final int selectionPosB;
	
	public UndoData(String text, int cursorPosition, boolean selected, int selectionPosA, int selectionPosB) {
		this.text = text;
		this.cursorPosition = cursorPosition;
		this.selected = selected;
		this.selectionPosA = selectionPosA;
		this.selectionPosB = selectionPosB;
	}
	
	public UndoData(TextInfo textinfo) {
		this(textinfo.text(),
				textinfo.cursorPosition,
				textinfo.selected,
				textinfo.selectionPosA,
				textinfo.selectionPosB);
	}

	@Override
	public String toString() {
		return "UndoData [text=" + text + ", cursorPosition=" + cursorPosition + ", selected=" + selected
				+ ", selectionPosA=" + selectionPosA + ", selectionPosB=" + selectionPosB + "]";
	}
}
