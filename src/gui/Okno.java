package gui;
import javax.swing.JFrame;

public class Okno extends JFrame {
	private static final long serialVersionUID = -3977009338403276682L;
	private Platno platno;
	public Okno() {
		super();
		setTitle("Igra Capture Go");
		platno = new Platno(700, 700);
		add(platno);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}