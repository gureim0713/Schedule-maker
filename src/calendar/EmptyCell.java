package calendar;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;

public class EmptyCell extends JPanel{
	public JTextPane pane;

	
	public EmptyCell() {
	
		pane = new JTextPane();
		
		setBackground(new Color(230,230,230));
		setPreferredSize(new Dimension(100,100));
		setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1));
	}
	public void setText(int index) {
		pane.setText(Integer.toString(index));
		add(pane);
	}
}
