package net.icelane.typeex.book.io;

import net.minecraft.util.ChatAllowedCharacters;

public class KeyInfo {
	
	// ==== LWJGL key codes =============================
	public static final int Shift      =   42; //  16;
	public static final int Control    =   29; //  17;
	public static final int Alt        =   56; //  18;
	public static final int Meta       =  219; // 525;

	public static final int A          =   30; //  65;
	public static final int X          =   45; //  88;
	public static final int C          =   46; //  67;
	public static final int V          =   47; //  86;

	public static final int Enter      =   28; //   0;
	public static final int Num_Enter  =  156; //   0;

	public static final int Esc        =    1; //  27;
	public static final int Backspace  =   14; //   8;
	public static final int Del        =  211; // 127;
	public static final int Home       =  199; //  36;
	public static final int End        =  207; //  35;
	public static final int Ins        =  210; // 155;

	public static final int ArrowLeft  =  203; //  37;
	public static final int ArrowRight =  205; //  39;
	public static final int ArrowUp    =  200; //  38;
	public static final int ArrowDown  =  208; //  40;
	// ================================================== 
	
	/*
	// ==== AWT key codes =============================== 
	public static final int Shift      =   16;
	public static final int Control    =   17;
	public static final int Alt        =   18;
	public static final int Meta       =  525;

	public static final int A          =   65;
	public static final int X          =   88;
	public static final int C          =   67;
	public static final int V          =   86;

	public static final int Enter      =    10;
	public static final int Num_Enter  =    0;

	public static final int Esc        =   27;
	public static final int Backspace  =    8;
	public static final int Del        =  127;
	public static final int Home       =   36;
	public static final int End        =   35;
	public static final int Ins        =  155;

	public static final int ArrowLeft  =   37;
	public static final int ArrowRight =   39;
	public static final int ArrowUp    =   38;
	public static final int ArrowDown  =   40;
	// ==================================================
	*/

	private int keyCode;
	private char keyChar;
	private boolean state;
	
	private static boolean control;
	private static boolean alt;
	private static boolean shift;
	private static boolean meta;

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

	/**
	 * @return the key code associated to the current key.
	 */
	public int getKeyCode() {
		return keyCode;	
	}
	
	/**
	 * @return the key character associated to the current key.
	 */
	public char getCharacter() {
		return keyChar;
	}

	/**
	 * @return the current key character as string.
	 */
	public String getString() {
		return Character.toString(keyChar);
	}
	
	/**
	 * Weather the current character is allowed in the Minecraft chat.
	 * @return true if the current character is allowed.
	 */
	public boolean IsAllowed() {
		return ChatAllowedCharacters.isAllowedCharacter(keyChar);
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
