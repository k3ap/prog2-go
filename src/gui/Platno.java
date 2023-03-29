package gui;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import logika.Igra;

class Platno extends JPanel {
	private static final long serialVersionUID = 7693008210122811280L;
	private Igra igra;
	private Dimension dimenzije;
	private Izgled izgled;

	public Platno(int sirina, int visina) {
		super();
		dimenzije = new Dimension(sirina, visina);
		setPreferredSize(dimenzije);
		igra = new Igra();
		izgled = new Izgled();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		int sirinaMreze = igra.sirina();
		int visinaMreze = igra.visina();

		// 1x1 mreže se ne da narisati
		assert sirinaMreze > 1;
		assert visinaMreze > 1;

		int leviRob = (int) (dimenzije.width * 0.1);
		int sirinaKorak = (int) (dimenzije.width * 0.8 / (sirinaMreze - 1));
		int zgornjiRob = (int) (dimenzije.height * 0.1);
		int visinaKorak = (int) (dimenzije.height * 0.8 / (visinaMreze - 1));

		g2.setStroke(izgled.mreza);
		g2.setColor(izgled.barvaMreze);
		for (int i = 0; i < visinaMreze; i++) {
			g2.drawLine(
					leviRob,
					zgornjiRob + visinaKorak * i,
					leviRob + sirinaKorak * (sirinaMreze - 1),
					zgornjiRob + visinaKorak * i
			);
		}
		for (int j = 0; j < sirinaMreze; j++) {
			g2.drawLine(
					leviRob + sirinaKorak * j,
					zgornjiRob,
					leviRob + sirinaKorak * j,
					zgornjiRob + visinaKorak * (visinaMreze - 1)
			);
		}
	}
}