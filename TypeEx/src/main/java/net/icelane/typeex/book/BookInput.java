package net.icelane.typeex.book;

import java.awt.event.KeyEvent;
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.icelane.typeex.book.io.KeyHandler;
import net.icelane.typeex.book.io.KeyInfo;
import net.icelane.typeex.book.io.TextInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

abstract class BookInput extends BookRender {

	public BookInput(EntityPlayer player, ItemStack item, boolean signed) {
		super(player, item, signed);
	}

	TextInfo textinfo = new TextInfo();
	
    public void handleKeyboardInput() throws IOException
    {
        keyTyped(new KeyInfo(Keyboard.getEventKey(), Keyboard.getEventCharacter(), Keyboard.getEventKeyState()));
        
        this.mc.dispatchKeypresses();
    }
    
	public void keyTyped(KeyInfo keyinfo) {
		textinfo.text = getPageText();
		
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
		setPageText(textinfo.text);
	}
	
}
