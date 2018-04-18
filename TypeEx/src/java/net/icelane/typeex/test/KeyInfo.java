package net.icelane.typeex.test;

public class KeyInfo {

	private int keyCode;
	private char keyChar;
	private boolean state;
	
	private static boolean control; // 17 	
	private static boolean alt;     // 18
	private static boolean shift;   // 16
	private static boolean meta;    // 525
	
	
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
