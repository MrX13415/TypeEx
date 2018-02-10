package net.icelane.typeex.test;
import java.awt.event.KeyEvent;

public class Control implements AKeyListener{
	
	private GUI gui;
	
	private TextInfo textinfo = new TextInfo();
	

	public Control(GUI gui) {
		this.gui = gui;	 
		gui.addListener(this); 
		
	}

	@Override
	public void keyTyped(KeyInfo keyinfo) {
		//debug:
		System.out.println(String.format("%s c: %s a: %s s: %s m: %s",
				keyinfo.toString(),
				KeyInfo.isControlHeld(),
				KeyInfo.isAltHeld(),
				KeyInfo.isShiftHeld(),
				KeyInfo.isMetaHeld()));
		
		if (!keyinfo.getKeyState()) return;
		
		textinfo.text = getText();
		
		// handle special keys ...
		boolean keyHandled = KeyHandler.handleKey(keyinfo, textinfo);
		
		// handle normal char keys ...
		if (!keyHandled && keyinfo.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {			
			textinfo.text = textinfo.firstPart() + keyinfo.getKeyChar() + textinfo.lastPart();
			textinfo.cursorPosition++;
		}
				
		// update ui
		setText(textinfo);
	}
	
	private void setText(TextInfo textInfo) {
		gui.setText(textInfo.text, textInfo.cursorPosition);
	}
	
	private String getText() {
		return gui.getText();
	}
	
}
