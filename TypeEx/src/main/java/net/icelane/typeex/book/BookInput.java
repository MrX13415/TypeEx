package net.icelane.typeex.book;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.icelane.typeex.book.io.KeyHandler;
import net.icelane.typeex.book.io.KeyInfo;

public class BookInput {

	
    public void handleKeyboardInput() throws IOException
    {
        keyTyped(new KeyInfo(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState()));
        
        this.mc.dispatchKeypresses();
    }
    
	public void keyTyped(KeyInfo keyinfo) {
		textinfo.text = pageGetCurrent();
		
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
		if (!keyHandled && keyinfo.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {						
	
			// handle selection overwrite ...
			if (textinfo.selected) textinfo.removeSelection();
			
			textinfo.insert(keyinfo.getKeyChar());
		}

		// update UI
		pageSetCurrent(textinfo.text);
//		setText(textinfo);
	}
	
}
