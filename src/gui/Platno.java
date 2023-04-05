package gui;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import logika.BarvaIgralca;
import logika.Igra;
import logika.Indeks;
import splosno.Poteza;


class Platno extends JPanel implements MouseListener {
	private static final long serialVersionUID = 7693008210122811280L;
	private Igra igra;
	private Dimension dimenzije;
	private Izgled izgled;
	private int leviRob, zgornjiRob, sirinaKorak, visinaKorak;

	public Platno(int sirina, int visina) {
		super();
		dimenzije = new Dimension(sirina, visina);
		setPreferredSize(dimenzije);
		izgled = new Izgled();
		igra = null;
		addMouseListener(this);
	}
	
	/**
	 * Zračuna vrednosti nekaterih privatnih spremenljivk, ki so potrebne za
	 * risanje mreže.
	 */
	private void izracunajDimenzije() {
		if (igra == null) { return; }
		
		leviRob = (int) (dimenzije.width * 0.1);
		sirinaKorak = (int) (dimenzije.width * 0.8 / (igra.sirina() - 1));
		zgornjiRob = (int) (dimenzije.height * 0.1);
		visinaKorak = (int) (dimenzije.height * 0.8 / (igra.visina() - 1));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		if (igra == null) {
			g2.setFont(g2.getFont().deriveFont((float) 30.0));
			g2.drawString("Izberite tip igre", dimenzije.width / 3, dimenzije.height / 2);
			return;
		}
		
		izracunajDimenzije();

		int sirinaMreze = igra.sirina();
		int visinaMreze = igra.visina();

		// 1x1 mreže se ne da narisati
		assert sirinaMreze > 1;
		assert visinaMreze > 1;

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
		
		// kamni
		for (int i = 0; i < visinaMreze; i++) {
			int y = zgornjiRob + i * visinaKorak - izgled.premerKamna / 2;
			for (int j = 0; j < sirinaMreze; j++) {
				int x = leviRob + j * sirinaKorak - izgled.premerKamna / 2;
				
				switch (igra.barvaPolja(new Indeks(i, j))) {
				case BELA:
					narisiKamen(g2, x, y, BarvaIgralca.BELA);
					break;
				case CRNA:
					narisiKamen(g2, x, y, BarvaIgralca.CRNA);
					break;
				case PRAZNA:
					break;
				}
			}
		}
		
		// kdo je na vrsti
		switch (igra.naPotezi()) {
		case BELA:
			napisiSporocilo(g2, "Na vrsti je bel.");
			break;
		case CRNA:
			napisiSporocilo(g2, "Na vrsti je črn.");
			break;
		}
	}
	
	/**
	 * Nariše kamen (bel ali črn krog) na koodrinatah x, y.
	 * @param g2 Grafika, na katero se nariše kamen.
	 * @param x Koordinata na platnu, na katero se nariše kamen (ne indeks polja).
	 * @param y Koordinata na platnu, na katero se nariše kamen (ne indeks polja).
	 * @param barva Barva igralca, CRNA ali pa BELA.
	 */
	private void narisiKamen(Graphics2D g2, int x, int y, BarvaIgralca barva) {
		Color sredina = Color.PINK;
		Color rob = Color.CYAN;
		switch (barva) {
			case CRNA:
				sredina = izgled.crnKamen;
				rob = izgled.crnKamenRob;
				break;
			case BELA:
				sredina = izgled.belKamen;
				rob = izgled.belKamenRob;
			default:
				assert false;
		}
		
		g2.setColor(sredina);
		g2.fillOval(x, y, izgled.premerKamna, izgled.premerKamna);
		g2.setColor(rob);
		g2.drawOval(x, y, izgled.premerKamna, izgled.premerKamna);
	}
	
	/**
	 * Nariše nekaj teksta na vrh zaslona nad mrežo.
	 * @param g2 Grafika, na katero se bo tekst napisal.
	 * @param sporocilo Tekst, ki ga želimo napisati.
	 */
	private void napisiSporocilo(Graphics2D g2, String sporocilo) {
		g2.setFont(g2.getFont().deriveFont((float) 20.0));
		g2.drawString(sporocilo, (int) (dimenzije.width * 0.1), (int) (dimenzije.height * 0.05));
	}
	
	/**
	 * Začne novo igro danega tipa.
	 * @param tip Tip igre, ki se igra.
	 */
	protected void novaIgra(TipIgre tip) {
		switch (tip) {
		case CLOCLO:
			igra = new Igra();
			break;
		case CLORAC:
			System.out.println("Ta tip igre še ni podprt.");
			break;
		case RACCLO:
			System.out.println("Ta tip igre še ni podprt.");
			break;
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (igra == null) { return; }
		
		izracunajDimenzije();
		int x = e.getX();
		int y = e.getY();
		
		// izračunaj polje, v katerem je bil klik
		int i = (y - zgornjiRob + visinaKorak / 2) / visinaKorak;
		int j = (x - leviRob + sirinaKorak / 2) / sirinaKorak;
		if (i < 0 || i >= igra.sirina() || j < 0 || j >= igra.visina()) { return; }
		
		Poteza p = new Poteza(i, j);
		igra.odigraj(p);
		repaint();
	}
	
	// neuporabljene metode MouseListener-ja
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
}