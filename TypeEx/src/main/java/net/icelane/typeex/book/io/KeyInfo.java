package net.icelane.typeex.book.io;

public class KeyInfo {

	private int keyCode;
	private char keyChar;
	private boolean state;
	
	private static boolean control;
	private static boolean alt;
	private static boolean shift;
	private static boolean meta;
	
	public static final int Shift      = 42;   // 16
	public static final int Control    = 29;   // 17
	public static final int Alt        = 56;   // 18
	public static final int Meta       = 219;  // 525
	
	public static final int A          = 30;   // 65
	public static final int X          = 45;   // 88 
	public static final int C          = 46;   // 67
	public static final int V          = 47;   // 86
		
	public static final int Esc        = 1;    // 27
	public static final int Backspace  = 14;   // 8
	public static final int Del        = 211;  // 127
	public static final int Home       = 199;  // 36
	public static final int End        = 207;  // 35
	public static final int Ins        = 210;  // 155
	
	public static final int ArrowLeft  = 203;  // 37
	public static final int ArrowRight = 205;  // 39
	public static final int ArrowUp    = 200;  // 38
	public static final int ArrowDown  = 208;  // 40
	
	public KeyInfo(int keyCode, char keyChar, boolean state) {
		super();
		this.keyCode = keyCode;
		this.keyChar = keyChar;
		this.state = state;
		
		switch (keyCode) {
		case Control: control = state;
			break;
		case Alt: alt = state;
			break;
		case Shift: shift = state;
			break;
		case Meta: meta = state;
			break;
		}
	}

	public int getKeyCode() {
		return keyCode;	
	}
	
	public char getKeyChar() {
		return keyChar;
	}
	
	public boolean getKeyState() {
		return state;
	}

	public static boolean isControlHeld() {
		return control;
	}

	public static boolean isAltHeld() {
		return alt;
	}

	public static boolean isShiftHeld() {
		return shift;
	}

	public static boolean isMetaHeld() {
		return meta;
	}
	
	@Override
	public String toString() {
		return String.format("[%s, %s = %s]", keyCode, keyChar, state);
	}

}
