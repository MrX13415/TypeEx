package net.icelane.typeex.book.io;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import net.icelane.typeex.util.StringUtils;
import net.minecraft.client.gui.FontRenderer;
import scala.reflect.internal.Trees.Return;

/**
 * Holds text and additional information about cursor and editing.
 */
public class TextInfo {
	
	public final FontRenderer fontRenderer;

	private UndoInfo undoinfo = new UndoInfo();
	
	/**
	 * The Text.
	 */
	private String text = "";

	private String textWrapped = "";

	/**
	 * The position of the cursor in the text.
	 */
	public int cursorPosition;

	/**
	 * The maximum length of the text.
	 */
	public int maxLength;

	/**
	 * The maximum length a line must have in pixels.
	 */
	public int wordWrap;

	/**
	 * Weather overwriting of characters is enabled.
	 */
	public boolean overwrite = false;

	/**
	 * The characters to use for new line.
	 */
	public final char newLine = '\n';

	/**
	 * The first position of the selection. (A and B positions might be reversed.)
	 */
	public int selectionPosA;

	/**
	 * The second position of the selection. (A and B positions might be reversed.)
	 */
	public int selectionPosB;

	/**
	 * Weather text is selected.
	 */
	public boolean selected;

	public TextInfo(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
	}

	private void apply(UndoData undodata) {
		if (undodata == null) return;
		
		text           = undodata.text;
		cursorPosition = undodata.cursorPosition;
		selected       = undodata.selected;
		selectionPosA  = undodata.selectionPosA;
		selectionPosB  = undodata.selectionPosB;
	}
	
	public UndoInfo undoinfo() {
		return undoinfo;
	}

	public void undoinfo(UndoInfo undoinfo) {
		this.undoinfo = undoinfo;	
	}
	
	public void undo() {
		apply(undoinfo.getPrev());
	}
	
	public void redo() {
		apply(undoinfo.getNext());
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text
	 */
	public void text(String text) {
		text(text, true);
	}
	
	/**
	 * Sets the text.
	 * 
	 * @param text
	 */
	public void text(String text, boolean undo) {
		if (text == this.text) return;
		this.text = text;
		this.textWrapped = wrapText();
		validateCursorPosition();
		
		if (undo) undoinfo.doUndo(this);
	}

	public String text() {
		return text;
	}

	public String textWrapped() {
		return textWrapped;
	}

	/**
	 * Inserts a character at the current cursor position. Increases the cursor
	 * position by one.
	 * 
	 * @param character
	 *            The character to be inserted.
	 */
	public void insert(char character) {
		insert(Character.toString(character));
	}

	/**
	 * Inserts a text at the current cursor position. Increases the cursor position
	 * by the length of the text.
	 * 
	 * @param text
	 *            The text to be inserted.
	 */
	public void insert(String text) {
		text = enforceLimit(text);
		String firstPart = firstPart();
		String lastPart = lastPart();
		
		// handle overwrite mode ...
		if (overwrite && lastPart.length() > 0)
			lastPart = lastPart.substring(1);

		// type next char ...
		cursorPosition += text.length();
		text(firstPart + text + lastPart);
	}

	private String enforceLimit(String text) {
		if (maxLength <= 0)
			return text;
		int overflow = text().length() + text.length() - maxLength;
		if (overflow > 0) {
			text = text.substring(0, text.length() - overflow);
		}

		int wrappedLines = wrappedLineCount();
		if (wrappedLines >= 14) {
			text = StringUtils.stripChars(text, newLine);

			LineInfo last = lastWrappedLine();
			int lastWidth = last.width();
			int lineWidth = lastWidth + width(text);

			while (lineWidth >= wordWrap) {
				if (text.length() == 0)
					break;
				text = text.substring(0, text.length() - 1);
				lineWidth = lastWidth + width(text);
			}
		}

		return text;
	}

	public void moveCursorToEnd() {
		cursorPosition = text.length();
	}

	public boolean isCursorWithin() {
		return cursorPosition < text.length();
	}

	public void validateCursorPosition() {
		if (cursorPosition < 0 || cursorPosition > text.length())
			cursorPosition = text.length();
	}

	public void validateSelectionPosition() {
		if (IsForwardSelection()) {
			if (selectionPosA < 0)
				selectionPosA = 0;
			if (selectionPosB > text.length())
				selectionPosB = text.length();
		} else {
			if (selectionPosB < 0)
				selectionPosB = 0;
			if (selectionPosA > text.length())
				selectionPosA = text.length();
		}
	}

	/**
	 * Copies the selected text to the clipboard.
	 */
	public void copy() {
		if (!selected)
			return;
		StringSelection selection = new StringSelection(selection());
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(selection, selection);
	}

	/**
	 * Cuts the selected text and stores a copy in the clipboard.
	 */
	public void cut() {
		if (!selected)
			return;
		copy();
		removeSelection();
	}

	/**
	 * Pastes the text in the clipboard to the current cursor position. If a
	 * selection is present, it get's removed first.
	 */
	public void past() {
		try {
			if (selected)
				removeSelection();
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			String text = (String) clipboard.getData(DataFlavor.stringFlavor);
			insert(text);
		} catch (UnsupportedFlavorException | IOException e) {
		}
	}

	public boolean IsBackwardSelection() {
		return selectionPosA > selectionPosB;
	}

	public boolean IsForwardSelection() {
		return selectionPosA < selectionPosB;
	}

	/**
	 * The start position of the selection.
	 */
	public int selectionStart() {
		int start = IsForwardSelection() ? selectionPosA : selectionPosB;
		return start >= 0 ? start : 0;
	}

	/**
	 * The end position of the selection.
	 */
	public int selectionEnd() {
		int end = IsForwardSelection() ? selectionPosB : selectionPosA;
		return end <= text.length() ? end : text.length();
	}

	/**
	 * Sets the selection start position to the current cursor position.
	 */
	public void setSelectionStart() {
		setSelectionStart(cursorPosition);
	}

	/**
	 * Sets the selection start position to the given cursor position.
	 */
	public void setSelectionStart(int position) {
		if (selected)
			return;
		this.selected = true;

		this.selectionPosA = position;
	}

	/**
	 * Sets the selection end position to the current cursor position.
	 */
	public void setSelectionEnd() {
		setSelectionEnd(cursorPosition);
	}

	/**
	 * Sets the selection end position to the given cursor position.
	 */
	public void setSelectionEnd(int position) {
		this.selectionPosB = position;
	}

	/**
	 * Selects all text.
	 */
	public void selectAll() {
		this.selected = true;
		this.selectionPosA = 0;
		this.selectionPosB = text.length();
		moveCursorToEnd();
	}

	/**
	 * @return the selected text.
	 */
	public String selection() {
		if (selected && text.length() > 0) {
			return text.substring(selectionStart(), selectionEnd());
		}
		return "";
	}

	/**
	 * removes the selected part form the text.
	 */
	public void removeSelection() {
		cursorPosition = selectionStart();
		text(text.substring(0, selectionStart()) + text.substring(selectionEnd()));

		// make sure we don't have invalid positions here (just in case)
		setSelectionStart();
		setSelectionEnd();

		// remove selection ...
		selected = false;
	}

	/**
	 * Returns the first part of the text, from the beginning to the current cursor
	 * position.
	 * 
	 * @return The first text part.
	 */
	public String firstPart() {
		validateCursorPosition();
		return firstPart(text, cursorPosition);
	}

	/**
	 * Returns the last part of the text, from the cursor position to the end of the
	 * text.
	 * 
	 * @return The last part.
	 */
	public String lastPart() {
		validateCursorPosition();
		return lastPart(text, cursorPosition);
	}
	
	public String wrappedFirstPart() {
		validateCursorPosition();
		return firstPart(textWrapped, cursorPosition);
	}
	
	public String wrappedLastPart() {
		validateCursorPosition();
		return lastPart(textWrapped, cursorPosition);
	}

	/**
	 * @return an array containing all lines.
	 */
	public String[] lines() {
		return lines(text, Character.toString(newLine)); // "\" => "\\" e.g. "\n" => "\\n" (because of regex)
	}

	public LineInfo line(int index) {
		return new LineInfo(this, index);
	}

	public LineInfo lastLine() {
		return line(lineCount() - 1);
	}

	public int lineCount() {
		return lineCount(text);
	}

	public LineInfo wrappedLine(int index) {
		return new LineInfo(this, true, index);
	}

	public LineInfo lastWrappedLine() {
		return wrappedLine(wrappedLineCount() - 1);
	}

	public int wrappedLineCount() {
		return lineCount(textWrapped);
	}

	public int lineCount(String text) {
		if (text.length() <= 0)
			return 0;
		return StringUtils.countMatches(text, newLine) + 1;
	}

	// /**
	// * @return the current Line.
	// */
	// public LineInfo currentLine() {
	// return new LineInfo(this);
	// }

	/**
	 * Returns the first part of the given text, from the beginning to the given
	 * cursor position.
	 * 
	 * @param text
	 *            The text to split.
	 * @param cursorpos
	 *            The cursor position to split the text.
	 * @return The first part of the text.
	 */
	public static String firstPart(String text, int cursorpos) {
		if (cursorpos >= text.length())
			return text;
		if (text.length() > 0) {
			return text.substring(0, cursorpos);
		}
		return "";
	}

	/**
	 * Returns the last part of the given text, from the given cursor position to
	 * the end of the text.
	 * 
	 * @param text
	 *            The text to split.
	 * @param cursorpos
	 *            The cursor position to split the text.
	 * @return The last part of the text.
	 */
	public static String lastPart(String text, int cursorpos) {
		if (cursorpos <= 0)
			return text;
		if (cursorpos < text.length()) {
			return text.substring(cursorpos, text.length());
		}
		return "";
	}

	/**
	 * @return an array containing all lines of the given text.
	 */
	public static String[] lines(String text, String newLine) {
		return text.split(newLine.replace("\\", "\\\\")); // "\" => "\\" e.g. "\n" => "\\n" (because of regex)
	}

	public String wrapText() {
		if (text.length() == 0)
			return "";

		int width = 0;
		String nText = "";

		for (int index = 0; index < text.length(); index++) {
			char character = text.charAt(index);
			int charwidth = fontRenderer.getCharWidth(character);
			width += charwidth;

			if (character == newLine) {
				width = 0;
			} else if (width >= wordWrap) {
				width = charwidth;
				nText += newLine;
			}

			nText += character;
		}

		return nText; // textinfo.fontRenderer. listFormattedStringToWidth(line,
						// textinfo.wordWrap).toArray(out);
	}

	public int width(String text) {
		return fontRenderer.getStringWidth(text);
	}
	
	public static abstract class SubTextInfo {
		public final TextInfo textinfo;

		protected SubTextInfo(TextInfo textinfo) { 
			this.textinfo = textinfo;
		}

		public abstract int width();

		public abstract int cursorPosition();

		public abstract int cursorWidth();

		public abstract boolean isCursorWithin();

		public abstract int selectionStartWidth();
		
		public abstract boolean isSelectionStartWithin();
		
		public abstract int selectionEndWidth();
		
		public abstract boolean isSelectionEndWithin();
		
		public int cursorWidth(String text) {
			if (!isCursorWithin())
				return 0;
			String s = text.substring(0, cursorPosition());
			return width(s);
		}

		public int width(String text) {
			return textinfo.width(text);
		}

		public int cursorPosition(int start, int end) {
			if (!isCursorWithin())
				return -1;
			return textinfo.cursorPosition - start;
		}

		public boolean isCursorWithin(int start, int end) {
			return textinfo.cursorPosition >= start && textinfo.cursorPosition <= end;
		}
		
		public int selectionStartWidth(String text, int start) {
			if (!isSelectionStartWithin())
				return -1;
			String s = text.substring(0, textinfo.selectionStart() - start);
			return textinfo.width(s);
		}
		
		public boolean isSelectionStartWithin(int start, int end) {
			return textinfo.selectionStart() >= start && textinfo.selectionStart() <= end;
		}
		
		public int selectionEndWidth(String text, int start) {
			if (!isSelectionEndWithin())
				return -1;
			String s = text.substring(0, textinfo.selectionEnd() - start);
			return textinfo.width(s);
		}
		
		public boolean isSelectionEndWithin(int start, int end) {
			return textinfo.selectionEnd() >= start && textinfo.selectionEnd() <= end;
		}

	}

	public static class LineInfo extends SubTextInfo {
		public final String text;
		public final int start;
		public final int end;

		protected LineInfo(TextInfo textinfo, int index) {
			this(textinfo, false, index);
		}

		protected LineInfo(TextInfo textinfo, boolean wrapped, int index) {
			super(textinfo);

			String _text = wrapped ? textinfo.textWrapped() : textinfo.text();

			int _start = 0;
			if (index > 0)
				_start = StringUtils.ordinalIndexOf(_text, Character.toString(textinfo.newLine), index) + 1;

			int _end = _text.indexOf(textinfo.newLine, _start);
			if (_end < 0)
				_end = _text.length();

			start = _start;
			end = _end;
			text = StringUtils.stripChars(_text.substring(_start, _end), textinfo.newLine);
		}

		@Override
		public int width() {
			return width(text);
		}

		@Override
		public int cursorPosition() {
			return cursorPosition(start, end);
		}

		@Override
		public int cursorWidth() {
			return cursorWidth(text);
		}

		@Override
		public boolean isCursorWithin() {
			return isCursorWithin(start, end);
		}

		@Override
		public int selectionStartWidth() {
			throw new UnsupportedOperationException("Use the method in class 'ChunckInfo'!");
			//return selectionStartWidth(text);
		}

		@Override
		public boolean isSelectionStartWithin() {
			throw new UnsupportedOperationException("Use the method in class 'ChunckInfo'!");
			//return selectionStartWithin(start, end);
		}

		@Override
		public int selectionEndWidth() {
			throw new UnsupportedOperationException("Use the method in class 'ChunckInfo'!");
			//return selectionEndWidth(text);
		}

		@Override
		public boolean isSelectionEndWithin() {
			throw new UnsupportedOperationException("Use the method in class 'ChunckInfo'!");
			//return selectionEndWithin(start, end);
		}
		
		public ChunckInfo[] wordWrap() {
			ArrayList<ChunckInfo> chuncks = new ArrayList<>();
			ChunckInfo[] out = new ChunckInfo[0];

			if (text.length() == 0) {
				chuncks.add(new ChunckInfo(this, 0, 0));
				return chuncks.toArray(out);
			}

			int width = 0;
			int start = 0;

			for (int index = 0; index < text.length(); index++) {
				int charwidth = textinfo.fontRenderer.getCharWidth(text.charAt(index));
				width += charwidth;

				if (width >= textinfo.wordWrap) {
					chuncks.add(new ChunckInfo(this, start, index));
					start = index;
					width = charwidth;
				}

				if (index == text.length() - 1) {
					chuncks.add(new ChunckInfo(this, start, index + 1));
				}
			}

			if (chuncks.size() == 0) {
				chuncks.add(new ChunckInfo(this));
			}

			return chuncks.toArray(out); // textinfo.fontRenderer. listFormattedStringToWidth(line,
											// textinfo.wordWrap).toArray(out);
		}

	}

	public static class ChunckInfo extends SubTextInfo {
		public final LineInfo lineinfo;
		public final String text;
		public final int start;
		public final int end;
		public final boolean wrapped;

		protected ChunckInfo(LineInfo lineinfo, int start, int end) {
			super(lineinfo.textinfo);

			this.lineinfo = lineinfo;

			this.text = lineinfo.text.substring(start, end);
			this.start = lineinfo.start + start;
			this.end = lineinfo.start + end;

			this.wrapped = this.end != lineinfo.end;
		}

		protected ChunckInfo(LineInfo lineinfo) {
			super(lineinfo.textinfo);

			this.wrapped = false;
			this.lineinfo = lineinfo;
			this.text = lineinfo.text.substring(0);
			this.start = lineinfo.start + 0;
			this.end = lineinfo.start + text.length();
		}

		@Override
		public int width() {
			return width(text);
		}

		@Override
		public int cursorPosition() {
			return cursorPosition(start, end);
		}

		@Override
		public int cursorWidth() {
			return cursorWidth(text);
		}

		@Override
		public boolean isCursorWithin() {
			return isCursorWithin(start, wrapped ? end - 1 : end);
		}
		
		@Override
		public int selectionStartWidth() {
			return selectionStartWidth(text, start);
		}

		@Override
		public boolean isSelectionStartWithin() {
			return isSelectionStartWithin(start, end);
		}

		@Override
		public int selectionEndWidth() {
			return selectionEndWidth(text, start);
		}

		@Override
		public boolean isSelectionEndWithin() {
			return isSelectionEndWithin(start, end);
		}

	}
}
