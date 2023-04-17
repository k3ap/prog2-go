package gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import vodja.TipIgre;


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

	/**
	 * Posodobi ves GUI.
	 */
	public void update() {
		repaint();
		platno.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// Klik na menu bar na vrhu ekrana.
		Object vir = e.getSource();
		if (vir == cloRac) {
			platno.novaIgra(TipIgre.CLORAC, this);
		}
		else if (vir == racClo) {
			platno.novaIgra(TipIgre.RACCLO, this);
		}
		else if (vir == cloClo) {
			platno.novaIgra(TipIgre.CLOCLO, this);
		}
		else {
			assert false;
		}
		
		update();
	}
}