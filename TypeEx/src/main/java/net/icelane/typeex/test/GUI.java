package net.icelane.typeex.test;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.commons.lang3.StringUtils;

import net.icelane.typeex.book.io.IKeyListener;
import net.icelane.typeex.book.io.KeyInfo;
import net.icelane.typeex.book.io.TextInfo;

public class GUI implements KeyListener{
	
	public static final String textTemplateBeginn = "<html><body><nobr>";
	public static final String textTemplateEnd = "</nobr></body></html>";
	public static final String newLineTemplate = "<br>";
	
	public static boolean debug;
	
	private JFrame myWindow;
	private JLabel myLabel;
	
	private ArrayList<IKeyListener> listener = new ArrayList<>();
	private TextInfo textinfo;

	Color colorCurLineBack = new Color(230, 239, 255);
	Color colorCurLineText;
	Color colorSelectionBack = new Color(90, 180, 255);
	Color colorSelectionText;
	Color colorCursor = new Color(255/2, 0, 255);
		
	public GUI() {	
		initializeGui();
		
		textinfo = new TextInfo();
		textinfo.text = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n" + 
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n" + 
				"Lorem ipsum dolor sit amet, consetetur sadipscing elitr,\n" + 
				"sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat,\n" + 
				"sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum.\n" + 
				"Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.";
		textinfo.cursorPosition = textinfo.text.length();
		setText(textinfo);
	}
	
	// WORKING GUI ... YAAAAHHH! (and bugs I guess)
	public void initializeGui() {
		myWindow = new JFrame();
		
		myLabel = new JLabel() {
			private static final long serialVersionUID = 7611324292363152736L;

			public void paint(Graphics g) {
				super.paint(g);
				drawCursor(g);				
			};
		};
		
		myLabel.setBackground(Color.WHITE);
		myLabel.setOpaque(true);
		myLabel.setVerticalAlignment(JLabel.TOP);
		myLabel.setFont(new Font("Consolas", Font.PLAIN, 15));
		//myLabel.setForeground(Color.BLACK);
		
		myWindow.setBounds(400, 200, 500, 500);
		myWindow.setMinimumSize(new Dimension(800, 500));
		myWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myWindow.addKeyListener(this);
		
		myWindow.add(myLabel, BorderLayout.CENTER);
		myWindow.pack();
		myWindow.setVisible(true);
	}
	
	public void addListener(IKeyListener listener) {
		this.listener.add(listener);
	}

	@Override
	public void keyPressed(KeyEvent e) {	
		for (IKeyListener aKeyListener : listener) {		
			KeyInfo kinfo = new KeyInfo(e.getKeyCode(), e.getKeyChar(), true);
			aKeyListener.keyTyped(kinfo);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		for (IKeyListener aKeyListener : listener) {		
			KeyInfo kinfo = new KeyInfo(e.getKeyCode(), e.getKeyChar(), false);
			aKeyListener.keyTyped(kinfo);
		}
	}

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
	
	private void drawCursor(Graphics gx) {
		if (textinfo == null) return;
		
		colorCurLineText = myLabel.getForeground();
		colorSelectionText = myLabel.getBackground();
		
        Graphics2D g = (Graphics2D)gx;
        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        		
		FontMetrics fontMetric = myLabel.getFontMetrics(myLabel.getFont());
		
		String firstPart = textinfo.firstPart();
		String lastPart = textinfo.lastPart();
		String curLineFP = firstPart.substring(firstPart.lastIndexOf("\n") + 1);
		int nlPos = lastPart.indexOf("\n");
		String curLineLP = lastPart.substring(0, nlPos >= 0 ? nlPos : lastPart.length());
		
		Rectangle2D clfBounds = fontMetric.getStringBounds(curLineFP, g);
		//Rectangle2D cllBounds = fontMetric.getStringBounds(curLineLP, g);

		int x, y, w, h;
		
		// background ...
		x = 0;
		y = fontMetric.getHeight() * StringUtils.countMatches(firstPart, "\n") + fontMetric.getLeading();
		//w = (int) (clfBounds.getWidth() + cllBounds.getWidth());
		w = myLabel.getWidth();
		h = (int) clfBounds.getHeight();		
		g.setColor(colorCurLineBack);
		g.fillRect(x, y, w, h);
		
		// current line text ...
		g.setFont(myLabel.getFont());
		g.setColor(colorCurLineText);		
		g.drawString(curLineFP + curLineLP, x, y + fontMetric.getAscent());

		// selection ...
		drawSelection(g, fontMetric);

		// cursor ...
		x = (int) clfBounds.getWidth();
		w = 1;
		h = h + 1;
		
		if (textinfo.overwrite && lastPart.length() > 0) {
			y = y + h - 2;
			h = 2;
			w = (int) fontMetric.getStringBounds(lastPart, 0, 1, g).getWidth();
			if (w <= 0) w = 8;
		}
			
		g.setColor(colorCursor);
		g.fillRect(x, y, w, h);

	}
	
	private void drawSelection(Graphics2D g, FontMetrics fontMetric) {
		//selection
		if (!textinfo.selected) return;
		
		if (debug) System.out.println("--------------------------------------------------");
		
		String[] lines = textinfo.text.split("\n");
		int linePosStart = 0;
		int linePosEnd = 0;
		
		for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
			
			String line = lines[lineIndex] + " ";
			String selslFP = "";  // selection start line first part
			String selelFP = "";  // selection end line first part
			String selText = line;  // the actually selected text in the current line. 
				
			linePosStart = linePosEnd;
			linePosEnd += line.length();
		
			if (textinfo.selectionStart() > linePosEnd) continue;
			if (textinfo.selectionEnd() < linePosStart) continue;
			
			Rectangle2D selclBounds = fontMetric.getStringBounds(line, g);
			
			int x, y, w, h;			
			x = 0;
			y = fontMetric.getHeight() * lineIndex + fontMetric.getLeading();
			w = (int) selclBounds.getWidth();
			h = (int) selclBounds.getHeight() + fontMetric.getLeading();

			if (debug) System.out.print("# " + lineIndex + " S: " + textinfo.selectionStart() + " - " + textinfo.selectionEnd() + " | L: " + linePosStart + " - " + linePosEnd + "     ");

			// handle start pos ...
			if (textinfo.selectionStart() > linePosStart && textinfo.selectionStart() <= linePosEnd) {
				if (debug) System.out.print("A");
				
				selslFP = line.substring(0, textinfo.selectionStart() - linePosStart);
				selText = selText.substring(textinfo.selectionStart() - linePosStart);
				Rectangle2D selslFPBounds = fontMetric.getStringBounds(selslFP, g);
				
				x = (int) selslFPBounds.getWidth();
				w = w - x;
			}
			
			// handle end pos ...
			if (textinfo.selectionEnd() >= linePosStart && textinfo.selectionEnd() < linePosEnd) {
				if (debug) System.out.print("B");
				
				selelFP = line.substring(0, textinfo.selectionEnd() - linePosStart);
				selText = selText.substring(0, textinfo.selectionEnd() - linePosStart - (line.length() - selText.length()));
				Rectangle2D selelFPBounds = fontMetric.getStringBounds(selelFP, g);
				
				w = (int) selelFPBounds.getWidth() - x;
			}
			
			if (debug) System.out.println("");
			
			// draw selection for current line ...
			g.setColor(colorSelectionBack);
			g.fillRect(x, y, w, h);
			
			// redraw text ...
			w = myLabel.getWidth();
			g.setFont(myLabel.getFont());
			g.setColor(colorSelectionText);	
			g.drawString(selText, x, y + fontMetric.getAscent());
		}
	}
	
	/**
	 * Creepieingly adding text ...
	 * @param text
	 */
	public void setText(TextInfo textinfo) {
		this.textinfo = textinfo;
		String text = textinfo.text;
		
		// adding a hack-ish cursor ...
//		String ta = textinfo.cursorPosition > 0 ? text.substring(0, textinfo.cursorPosition) : "";
//		String tb = textinfo.cursorPosition < text.length() ? text.substring(textinfo.cursorPosition, text.length()) : "";	
//		text = ta + (textinfo.overwrite ? "\u00A6" : "\u007C") + tb;
	
		text = text.replaceAll("&", "&amp;");
		text = text.replaceAll(" ", "&nbsp;");
		text = text.replaceAll("<", "&lt;");
		text = text.replaceAll(">", "&gt;");
		text = text.replaceAll("\r", "");
		text = text.replaceAll("\n", newLineTemplate);
		
		myLabel.setText(String.format("%s%s%s", textTemplateBeginn, text, textTemplateEnd));
		myLabel.repaint();
	}
	
	/**
	 * Creepieingly returning text ...
	 * @param text
	 */
	public TextInfo getText() {
//		String text = myLabel.getText();
//		
//		text = text.replaceAll("&nbsp;", " ");
//		text = text.replaceAll("&lt;", "<");
//		text = text.replaceAll("&gt;", ">");
//		text = text.replaceAll("&amp;", "&");
//		text = text.replaceAll(newLineTemplate, "\n");
//		text = text.replaceAll(textTemplateBeginn, "");
//		text = text.replaceAll(textTemplateEnd, "");

		// hacking-ish removing our cursor ...
//		if (textinfo.cursorPosition > text.length()) textinfo.cursorPosition = text.length();
//		String ta = textinfo.cursorPosition > 0 ? text.substring(0, textinfo.cursorPosition) : "";
//		String tb = textinfo.cursorPosition < text.length() ? text.substring(textinfo.cursorPosition + 1) : "";	
//		text = ta + tb;
		
		return textinfo;
	}
}

