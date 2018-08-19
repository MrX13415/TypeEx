package net.icelane.typeex.io;

public class KeyInfo {

	private int keyCode;
	private char keyChar;
	private boolean state;
	
	private static boolean control; // 17 	
	private static boolean alt;     // 18
	private static boolean shift;   // 16
	private static boolean meta;    // 525
	
	public static final int Shift      = 16;
	public static final int A          = 65;
	public static final int C          = 67;
	public static final int V          = 86;
	public static final int X          = 88;
	
	public static final int Esc        = 27;
	public static final int Backspace  = 8;
	public static final int Del        = 127;
	public static final int Home       = 36;
	public static final int End        = 35;
	public static final int Ins        = 155;
	
	public static final int ArrowLeft  = 37;
	public static final int ArrowRight = 39;
	public static final int ArrowUp    = 38;
	public static final int ArrowDown  = 40;	
	
	public KeyInfo(int keyCode, char keyChar, boolean state) {
		super();
		this.keyCode = keyCode;
		this.keyChar = keyChar;
		this.state = state;
		
		switch (keyCode) {
		case 17: control = state;
			break;
		case 18: alt = state;
			break;
		case 16: shift = state;
			break;
		case 525: meta = state;
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
