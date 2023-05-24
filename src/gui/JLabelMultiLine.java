package gui;


import javax.swing.JLabel;

/**
 * A version of JLabel which automatically splits the
 * text over multiple lines if it's too long.
 *
 */
public class JLabelMultiLine extends JLabel {
	private static final long serialVersionUID = 4850443323738161114L;

	public JLabelMultiLine(String text, int horizontalAlignment) {
		super();
		this.setHorizontalAlignment(horizontalAlignment);
		this.setText(text);
	}

	@Override
	public void setText(String message) {
		// use <html> and <br /> to break the text into multiple lines
		if (this.getHorizontalAlignment() == JLabel.CENTER) {
			super.setText("<html><center>" + message + "</center></html>");
		}
		else {
			super.setText("<html>" + message + "</html>");
		}
	}
}
