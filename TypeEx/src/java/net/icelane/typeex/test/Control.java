package net.icelane.typeex.test;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;

public class Control implements AKeyListener{
	
	private GUI gui;
	
	private int cursorpos = 0;
	
	private String txt = "";
	
	public Control(GUI gui) {
		this.gui = gui;	 
		gui.addListener(this); 
		
	}

	@Override
	public void keyTyped(char typedChar, int keyCode, int modifier) {
		System.out.println(String.format("%s <--> %s", typedChar, keyCode));
		
		switch(keyCode) {
			case 8: // backspace
				String txt = getFirstTextPt(cursorpos);
				txt = (txt.length() > 0 ? txt.substring(0, txt.length() - 1) : "") + getLastTextPt(cursorpos);
				cursorpos = cursorpos <= 0 ? 0 : --cursorpos;
				setText(txt);
				break;
				
			case 127: // del
				txt = getLastTextPt(cursorpos);
				txt = getFirstTextPt(cursorpos) + (txt.length() > 0 ? txt.substring(1, txt.length()) : "");
				setText(txt);
				break;
				
			case 37: //left TODO No negative positions
				cursorpos = cursorpos <= 0 ? 0 : --cursorpos;
				break;
				
			case 39: //right
				cursorpos = cursorpos >= getText().length() ? getText().length() : ++cursorpos;
				break;
				
			case 38: //up
				txt = getFirstTextPt(cursorpos);
				if (!txt.contains("\n")) break;
				int nlPosB = txt.lastIndexOf("\n");
				int nlPosA = txt.substring(0, nlPosB).lastIndexOf("\n");
	
				int curLineLength = cursorpos - nlPosB;
				int preLineLength = nlPosB - nlPosA;			
				cursorpos = curLineLength >= preLineLength ? nlPosB : nlPosA + curLineLength;				
				break;
				
			case 40: //down
				txt = getFirstTextPt(cursorpos);
				nlPosA = txt.lastIndexOf("\n");

				txt = getLastTextPt(cursorpos);
				if (!txt.contains("\n")) break;
				nlPosB = txt.indexOf("\n");
				int nlPosC = txt.indexOf("\n", nlPosB + 1);
				if (nlPosC < 0) nlPosC = txt.length();
					
				int curLinePos = cursorpos - nlPosA;
				int postLineLength = nlPosC - nlPosB;		
				
				cursorpos += postLineLength < curLinePos ? nlPosC : nlPosB + curLinePos;

				break;
				
			case 36: // home
				
				break;
			case 35: // end
				
				break;
			default:
				if (typedChar == KeyEvent.CHAR_UNDEFINED) break;
				
				txt = getFirstTextPt(cursorpos) + typedChar + getLastTextPt(cursorpos);
				cursorpos++;
				setText(txt);

				
				break;
		}
		
		// update hack-ish cursor ...
		setText(getText());
	}
	
	private void setText(String txt) {
		gui.setText(txt, cursorpos);
	}
	
	private String getText() {
		return gui.getText();
	}
	
	private String getFirstTextPt(int cursorpos) {
		 if (getText().length() > 0 ){
			return getText().substring(0, cursorpos);
		}else return "";
	}
	
	private String getLastTextPt(int cursorpos) {
		if (cursorpos < getText().length()) {
			return getText().substring(cursorpos, getText().length());
		}else return "";
	}
	
}
