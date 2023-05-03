package gui;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import inteligenca.Inteligenca;
import vodja.GameType;


public class Window extends JFrame implements ActionListener {
	private static final long serialVersionUID = -3977009338403276682L;
	private Panel panel;
	private JMenuItem humCom, comHum, humHum, comCom;
	
	public Window() {
		super();
		setTitle("Igra Capture Go");
		panel = new Panel(700, 700);
		add(panel);
		
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);
		
		JMenu igre = newMenu(menubar, "Igre");
		humCom = newMenuItem(igre, "Človek vs. računalnik...");
		comHum = newMenuItem(igre, "Računalnik vs. človek...");
		humHum = newMenuItem(igre, "Človek vs. človek");
		comCom = newMenuItem(igre, "Računalnik vs. računalnik...");

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
	 * Update the entire GUI.
	 */
	public void update() {
		repaint();
		panel.repaint();
	}
	
	private IntelligencePair getIntelligencePairChoice() {
		IntelligenceOption[] options = IntelligenceOption.getAll();
		JComboBox<IntelligenceOption> izbira = new JComboBox<IntelligenceOption>(options);
		izbira.setSelectedIndex(0);
		JComboBox<IntelligenceOption> izbira2 = new JComboBox<IntelligenceOption>(options);
		izbira2.setSelectedIndex(0);
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Igralec belih kamnov:"),
		        izbira,
				new JLabel("Igralec črnih kamnov:"),
		        izbira2,
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite odločevalca potez", JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			return new IntelligencePair(
					options[izbira.getSelectedIndex()].toIntelligence(),
					options[izbira2.getSelectedIndex()].toIntelligence()
			);
		} else {
		    return null;
		}
	}
	
	private Inteligenca getIntelligenceChoice() {
		IntelligenceOption[] options = IntelligenceOption.getAll();
		JComboBox<IntelligenceOption> izbira = new JComboBox<IntelligenceOption>(options);
		izbira.setSelectedIndex(0);
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Nasprotnik:"),
		        izbira,
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite tip nasprotnika", JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			return ((IntelligenceOption) izbira.getSelectedItem()).toIntelligence();
		} else {
		    System.out.println("User canceled / closed the dialog, result = " + result);
		    return null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// A click on the menu bar at the top of the window.
		Object source = e.getSource();
		if (source == humCom) {
			Inteligenca selected = getIntelligenceChoice();
			if (selected != null) {
				panel.newComGame(GameType.HUMCOM, selected, this);
			}
		}
		else if (source == comHum) {
			Inteligenca selected = getIntelligenceChoice();
			if (selected != null) {
				panel.newComGame(GameType.COMHUM, selected, this);
			}
		}
		else if (source == humHum) {
			panel.newHumHumGame(this);
		}
		else if (source == comCom) {
			IntelligencePair selected = getIntelligencePairChoice();
			if (selected != null) {
				panel.newComComGame(selected, this);
			}
		}
		else {
			assert false;
		}
		
		update();
	}
}