package gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import vodja.GameType;


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	private Panel panel;
	private JMenuItem humCom, comHum, humHum;
	
	public Window() {
		super();
		setTitle("Capture Go");
		panel = new Panel(700, 700);
		add(panel);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu igre = newMenu(menubar, "Games");
		humCom = newMenuItem(igre, "Human vs. computer");
		comHum = newMenuItem(igre, "Computer vs. human");
		humHum = newMenuItem(igre, "Human vs. human");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private JMenu newMenu(JMenuBar menubar, String name) {
		JMenu newMenu = new JMenu(name);
		menubar.add(newMenu);
		return newMenu;
	}
	
	private JMenuItem newMenuItem(JMenu menu, String name) {
		JMenuItem newItem = new JMenuItem(name);
		menu.add(newItem);
		newItem.addActionListener(this);
		return newItem;
	}

	/**
	 * Uptide the enture GUI.
	 */
	public void update() {
		repaint();
		panel.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// A click on the menu bar at the top of the window.
		Object source = e.getSource();
		if (source == humCom) {
			panel.newGame(GameType.HUMCOM, this);
		}
		else if (source == comHum) {
			panel.newGame(GameType.COMHUM, this);
		}
		else if (source == humHum) {
			panel.newGame(GameType.HUMHUM, this);
		}
		else {
			assert false;
		}
		
		update();
	}
}