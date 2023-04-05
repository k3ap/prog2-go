package gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Vrsta igre, glede na to katere barve je AI, ali pa sta oba igralca človeka.
 * @see #CLORAC
 * @see #RACCLO
 * @see #CLOCLO
 */
enum TipIgre {
	/**
	 * Človek je črn, računalnik pa bel.
	 */
	CLORAC,
	/**
	 * Računalnik je bel, človek pa črn.
	 */
	RACCLO,
	/**
	 * Človek nadzira črnega in belega.
	 */
	CLOCLO,
}

public class Okno extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	private Platno platno;
	private JMenuItem cloRac, racClo, cloClo;
	
	public Okno() {
		super();
		setTitle("Igra Capture Go");
		platno = new Platno(700, 700);
		add(platno);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu igre = novMenu(menubar, "Igre");
		cloRac = novMenuItem(igre, "Človek-računalnik");
		racClo = novMenuItem(igre, "Računalnik-človek");
		cloClo = novMenuItem(igre, "Človek-človek");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JMenu novMenu(JMenuBar menubar, String ime) {
		JMenu nov = new JMenu(ime);
		menubar.add(nov);
		return nov;
	}
	
	private JMenuItem novMenuItem(JMenu menu, String ime) {
		JMenuItem nov = new JMenuItem(ime);
		menu.add(nov);
		nov.addActionListener(this);
		return nov;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Klik na menu bar na vrhu ekrana.
		Object vir = e.getSource();
		if (vir == cloRac) {
			platno.novaIgra(TipIgre.CLORAC);
		}
		else if (vir == racClo) {
			platno.novaIgra(TipIgre.RACCLO);
		}
		else if (vir == cloClo) {
			platno.novaIgra(TipIgre.CLOCLO);
		}
		else {
			assert false;
		}
		
		repaint();
	}
}