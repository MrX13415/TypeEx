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
			for (int index = undolist.size() - 1; index > undoindex; --index) {
				undolist.remove(index);
			}
		}
		
 		UndoData data = new UndoData(textinfo);
		System.out.println("doUndo: " + undoindex + " " + data.toString());
		undolist.add(data);
		undoindex++;
	}
	
	public UndoData getPrev() {
		System.out.println("getPrev: " + undoindex);
		if (undoindex <= 0) return null;
		--undoindex;
		UndoData data = undolist.get(undoindex);
		System.out.println("getPrev: " + undoindex + " " + data.toString());
		data = undolist.get(undoindex);
		System.out.println(data);
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
