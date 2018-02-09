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
	public void keyTyped(char typedChar, int keyCode, int modifier) {
		//debug: System.out.println(String.format("%s <--> %s", typedChar, keyCode));
		
		textinfo.text = getText();
		
		// handle special keys ...
		boolean keyHandled = KeyHandler.handleKey(keyCode, textinfo);
		
		// handle normal char keys ...
		if (!keyHandled && typedChar != KeyEvent.CHAR_UNDEFINED) {			
			textinfo.text = textinfo.firstPart() + typedChar + textinfo.lastPart();
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
