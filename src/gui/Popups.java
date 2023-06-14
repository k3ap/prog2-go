package gui;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import inteligenca.AlphaBetaFCMoveChooser;
import inteligenca.AlphaBetaGOMoveChooser;
import inteligenca.GoGridEstimator;
import inteligenca.Inteligenca;
import inteligenca.MCTSMoveChooser;
import inteligenca.RandomMoveChooser;
import inteligenca.WeightedGridEstimator;
import logika.GoGameType;
import vodja.GameType;
import vodja.ManagedGame;

public class Popups {
	private final static Inteligenca[] intelligenceOptionsFC = {
			new Inteligenca("Človek", true),
			new Inteligenca(new AlphaBetaFCMoveChooser(4, new WeightedGridEstimator())),
			new Inteligenca(new AlphaBetaFCMoveChooser(3, new WeightedGridEstimator())),
			new Inteligenca(new AlphaBetaFCMoveChooser(2, new WeightedGridEstimator())),
			new Inteligenca(new RandomMoveChooser()),
	};
	
	private final static Inteligenca[] intelligenceOptionsGO = {
			new Inteligenca("Človek", true),
			new Inteligenca(new MCTSMoveChooser()),
			new Inteligenca(new AlphaBetaGOMoveChooser(3, new GoGridEstimator())),
			new Inteligenca(new AlphaBetaGOMoveChooser(2, new GoGridEstimator())),
			new Inteligenca(new RandomMoveChooser()),
	};
	
	protected static GameParams getGameChoice() {
		JComboBox<GoGameType> type = new JComboBox<GoGameType>(new GoGameType[] {GoGameType.FCGO, GoGameType.GO});
		JComboBox<Inteligenca> izbira = new JComboBox<Inteligenca>(intelligenceOptionsFC);
		izbira.setSelectedIndex(0);
		JComboBox<Inteligenca> izbira2 = new JComboBox<Inteligenca>(intelligenceOptionsFC);
		izbira2.setSelectedIndex(0);
		type.addItemListener(new GameTypeListener(izbira, izbira2, intelligenceOptionsFC, intelligenceOptionsGO));
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Igra:"),
				type,
				new JLabel("Igralec črnih kamnov:"),
				izbira,
				new JLabel("Igralec belih kamnov:"),
				izbira2,
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite odločevalca potez", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			Inteligenca black = (Inteligenca) izbira.getSelectedItem();
			Inteligenca white = (Inteligenca) izbira2.getSelectedItem();
			
			GameType gameType = GameType.COMCOM;
			if (black.isHuman() && white.isHuman())
				gameType = GameType.HUMHUM;
			if (black.isHuman() && !white.isHuman())
				gameType = GameType.HUMCOM;
			if (!black.isHuman() && white.isHuman())
				gameType = GameType.COMHUM;
			
			return new GameParams(
					(GoGameType) type.getSelectedItem(),
					gameType,
					new IntelligencePair(black, white)
			);
		} else {
			return null;
		}
	}

	/**
	 * Shows a pop up for choosing the minimum time that the computer will take to choose a move.
	 * @param currentDelay The value that the delay was set to before this method was called.
	 * @return The chosen time in milliseconds.
	 */
	protected static int getDelayOption(int currentDelay) { return getDelayOption(currentDelay, false); }
	protected static int getDelayOption(int currentDelay, boolean error) {
		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		JFormattedTextField amountField = new JFormattedTextField(intFormat);
		amountField.setText(Integer.toString(currentDelay));
		JComponent[] inputs = null;
		if (error) {
			inputs = new JComponent[] {
					new JLabel("Če bo računalnik izbral potezo v manj kot toliko milisekundah, "
							+ "bo počakal, da je minilo toliko časa."),
					new JLabel("Izbrana vrednost mora biti nenegativno celo število!"),
					amountField,
			};
		}
		else {
			inputs = new JComponent[] {
					new JLabel("Če bo računalnik izbral potezo v manj kot toliko milisekundah, "
							+ "bo počakal, da je minilo toliko časa"),
					amountField,
			};
		}
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite hitrost računalnika", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			int amount;
			try {
				amount = intFormat.parse(amountField.getText()).intValue();
			} catch (ParseException e) {
				amount = -1;
			}
			if (amount < 0) {
				// recursively ask for another input if this one is invalid
				return getDelayOption(currentDelay, true);
			}
			return amount;
		} else {
			return currentDelay;
		}
	}
}
