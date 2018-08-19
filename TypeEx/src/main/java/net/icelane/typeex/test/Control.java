package net.icelane.typeex.test;
import java.awt.event.KeyEvent;

public class Control implements AKeyListener{
	
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
		if (!keyHandled && keyinfo.getKeyChar() != KeyEvent.CHAR_UNDEFINED) {						
	
			// handle selection overwrite ...
			if (textinfo.selected) textinfo.removeSelection();
			
			textinfo.insert(keyinfo.getKeyChar());
			
//			String lastPart = textinfo.lastPart();
//			
//			// handle overwrite mode ...
//			if (textinfo.overwrite && lastPart.length() > 0)
//				lastPart = lastPart.substring(1);
//
//			// type next char ...
//			textinfo.text = textinfo.firstPart() + keyinfo.getKeyChar() + lastPart;
//			textinfo.cursorPosition++;
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
