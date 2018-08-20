package net.icelane.typeex.test;
import java.awt.event.KeyEvent;

import net.icelane.typeex.book.io.IKeyListener;
import net.icelane.typeex.book.io.KeyHandler;
import net.icelane.typeex.book.io.KeyInfo;
import net.icelane.typeex.book.io.TextInfo;

public class Control implements IKeyListener{
	
	private GUI gui;
	
	private TextInfo textinfo = new TextInfo();
	

	public Control(GUI gui) {
		this.gui = gui;	 
		gui.addListener(this); 
		textinfo = this.gui.getText();
	}

	@Override
	public void keyTyped(KeyInfo keyinfo) {
		//DEBUG
		System.out.println(String.format("%16s %s%s%s%s%s",
				keyinfo.toString(),
				textinfo.overwrite ? "Ins" : "   ",
				KeyInfo.isControlHeld() ? " CTRL" : "",
				KeyInfo.isAltHeld() ? " Alt" : "",
				KeyInfo.isShiftHeld() ? " Shift" : "",
				KeyInfo.isMetaHeld() ? " Meta" : ""));
		
		if (!keyinfo.getKeyState()) return;
		
		// handle special keys ...
		boolean keyHandled = KeyHandler.handleKey(keyinfo, textinfo);
		
		// handle normal char keys ...
		if (!keyHandled && keyinfo.getCharacter() != KeyEvent.CHAR_UNDEFINED) {						
	
			// handle selection overwrite ...
			if (textinfo.selected) textinfo.removeSelection();
			
			textinfo.insert(keyinfo.getCharacter());
		}

		// update UI
		setText(textinfo);
	}
	
	private void setText(TextInfo textInfo) {
		gui.setText(textInfo);
	}
	
//	private String getText() {
//		return gui.getText();
//	}
	
}
