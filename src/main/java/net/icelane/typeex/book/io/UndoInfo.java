package net.icelane.typeex.book.io;

import java.util.ArrayList;


public class UndoInfo {

	private ArrayList<UndoData> undolist = new ArrayList<UndoData>();
	
	private int undoindex = -1;

	public ArrayList<UndoData> undolist() {
		return undolist;
	}

	public void undolist(ArrayList<UndoData> undolist) {
		this.undolist = undolist;
	}
	
	public void doUndo(TextInfo textinfo) {
		if (undoindex < (undolist.size() - 1)) {
			for (int index = undoindex + 1; index < undolist.size(); index++) {
				undolist.remove(index);
			}
		}
		
		undolist.add(new UndoData(textinfo));
		undoindex++;
	}
	
	public UndoData getPrev() {
		if (undoindex <= 0) return null;
		--undoindex;
		UndoData data = undolist.get(undoindex);
		return data;
	}
	
	public UndoData getNext() {
		if (undoindex >= (undolist.size() - 2)) return null;
		undoindex++;
		return undolist.get(undoindex);
	}

	public int undoindex() {
		return undoindex;
	}

	public void setUndoindex(int undoindex) {
		this.undoindex = undoindex;
	}
	
}
