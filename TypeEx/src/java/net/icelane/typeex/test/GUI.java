package net.icelane.typeex.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class GUI implements KeyListener{
	
	public static final String textTemplateBeginn = "<html><body>";
	public static final String textTemplateEnd = "</body></html>";
	public static final String newLineTemplate = "<br>";
	
	private JFrame myWindow;
	private JLabel myLabel;
	
	private ArrayList<AKeyListener> listener = new ArrayList<>();
	private int cursorpos;
	
	public GUI() {	
		initializeGui();
		
		String txt = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\r\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\r\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\r\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\r\n" + 
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\r\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\r\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\r\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\r\n" + 
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\r\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\r\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\r\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. \r\n";
		setText(txt, 0);		
	}
	
	// WORKING GUI ... YAAAAHHH! (and bugs I guess)
	public void initializeGui() {
		myWindow = new JFrame();
		myLabel = new JLabel();
		
		myLabel.setBackground(Color.WHITE);
		myLabel.setOpaque(true);
		myLabel.setVerticalAlignment(JLabel.TOP);
		myLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
		
		myWindow.setBounds(200, 200, 500, 500);
		myWindow.setMinimumSize(new Dimension(500, 500));
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.addKeyListener(this);
		
		myWindow.add(myLabel, BorderLayout.CENTER);
		myWindow.pack();
		myWindow.setVisible(true);
	}
	
	public void addListener(AKeyListener listener) {
		this.listener.add(listener);
	}

	@Override
	public void keyPressed(KeyEvent e) {	
		for (AKeyListener aKeyListener : listener) {
			aKeyListener.keyTyped(e.getKeyChar(), e.getKeyCode(), e.getModifiers());
		}
	}

	@Override
	public void keyReleased(KeyEvent e) { }

	@Override
	public void keyTyped(KeyEvent e) { }

	public JFrame getMyWindow() {
		return myWindow;
	}

	public void setMyWindow(JFrame myWindow) {
		this.myWindow = myWindow;
	}

	public JLabel getMyLabel() {
		return myLabel;
	}

	public void setMyLabel(JLabel myLabel) {
		this.myLabel = myLabel;
	}
	
	/**
	 * Creepieingly adding text ...
	 * @param text
	 */
	public void setText(String text, int cursorpos) {
		this.cursorpos = cursorpos;

		// adding a hack-ish cursor ...
		String ta = cursorpos > 0 ? text.substring(0, cursorpos) : "";
		String tb = cursorpos < text.length() ? text.substring(cursorpos, text.length()) : "";	
		text = ta + "|" + tb;
			
		text = text.replaceAll("\r", "");
		text = text.replaceAll("\n", newLineTemplate);
		
		myLabel.setText(String.format("%s%s%s", textTemplateBeginn, text, textTemplateEnd));
	}
	
	/**
	 * Creepieingly returning text ...
	 * @param text
	 */
	public String getText() {
		String text = myLabel.getText().replaceAll(newLineTemplate, "\n");
		text = text.replaceAll(textTemplateBeginn, "");
		text = text.replaceAll(textTemplateEnd, "");
		
		// hacking-ish removing our cursor ...
		if (cursorpos > text.length()) cursorpos = text.length();
		String ta = cursorpos > 0 ? text.substring(0, cursorpos) : "";
		String tb = cursorpos < text.length() ? text.substring(cursorpos + 1) : "";	
		text = ta + tb;
		
		return text;
	}
}

