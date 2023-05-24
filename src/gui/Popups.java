package gui;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import inteligenca.AlphaBetaMoveChooser;
import inteligenca.Inteligenca;
import inteligenca.PrimitiveGridEstimator;
import inteligenca.RandomMoveChooser;
import inteligenca.WeightedGridEstimator;

public class Popups {
	/**
	 * The options for the AI to be presented to the user.
	 */
	private final static Inteligenca[] intelligenceOptions = {
			new Inteligenca(new AlphaBetaMoveChooser(4, new WeightedGridEstimator())),
			new Inteligenca(new AlphaBetaMoveChooser(3, new WeightedGridEstimator())),
			new Inteligenca(new AlphaBetaMoveChooser(2, new WeightedGridEstimator())),
			new Inteligenca(new AlphaBetaMoveChooser(3, new PrimitiveGridEstimator())),
			new Inteligenca(new RandomMoveChooser()),
	};
	
	/**
	 * Show a pop up to choose the pair of intelligences to be played against each other.
	 * @return The IntelligencePair of the selected intelligences.
	 */
	protected static IntelligencePair getIntelligencePairChoice() {
		JComboBox<Inteligenca> izbira = new JComboBox<Inteligenca>(intelligenceOptions);
		izbira.setSelectedIndex(0);
		JComboBox<Inteligenca> izbira2 = new JComboBox<Inteligenca>(intelligenceOptions);
		izbira2.setSelectedIndex(0);
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Igralec črnih kamnov:"),
				izbira,
				new JLabel("Igralec belih kamnov:"),
				izbira2,
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite odločevalca potez", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			return new IntelligencePair(
					intelligenceOptions[izbira.getSelectedIndex()],
					intelligenceOptions[izbira2.getSelectedIndex()]
			);
		} else {
			return null;
		}
	}

	/**
	 * Shows a pop up for choosing the intelligence that will be the users opponent.
	 * @return The chosen intelligence.
	 */
	protected static Inteligenca getIntelligenceChoice() {
		JComboBox<Inteligenca> izbira = new JComboBox<Inteligenca>(intelligenceOptions);
		izbira.setSelectedIndex(0);
		final JComponent[] inputs = new JComponent[] {
				new JLabel("Nasprotnik:"),
				izbira,
		};
		int result = JOptionPane.showConfirmDialog(null, inputs, "Izberite tip nasprotnika", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			return intelligenceOptions[izbira.getSelectedIndex()];
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
	private static int getDelayOption(int currentDelay, boolean error) { // error here is used to report errors via recursion
		NumberFormat intFormat = NumberFormat.getIntegerInstance();
		JFormattedTextField amountField = new JFormattedTextField(intFormat);
		amountField.setText(Integer.toString(currentDelay));
		JComponent[] inputs = null;
		if (error) {
			// the user has inputed an invalid value, let them know
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
