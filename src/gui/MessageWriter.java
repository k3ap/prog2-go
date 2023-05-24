package gui;

import vodja.ManagedGame;

/**
 * Processes the message about the status of the game form a
 * ManagedGame object. 
 */
public class MessageWriter {
	public static String getMessage(ManagedGame game) {
		switch (game.gameStatus()) {
		case INVALID:
		case PLAY:
			// in either of these cases the game is waiting for player input
			if (game.gameType().mixedGame()) {
				// mixedGame => there is only one human player
				return "Na vrsti ste.";
			}
			else {
				switch (game.playerTurn()) {
				case WHITE:
					return "Na vrsti je bel.";
				case BLACK:
					return "Na vrsti je črn.";
				}
			}
			break;
		case WAIT:
			return "Algoritem " + game.intelligenceName() + " izbira potezo...";
		case ERROR:
			return "Program za izbiranje poteze se je sesul.";
		case WHITEWINS:
		case BLACKWINS:
			switch (game.getOutcome()) {
			case COMBLACKWON:
				return "Algoritem " + game.intelligence1Name() + " (črni) je zmagal.";
			case COMWHITEWON:
				return "Algoritem " + game.intelligence2Name() + " (beli) je zmagal.";
			case COMWON:
				return "Zmagal je računalnik (" + game.intelligenceName() + ").";
			case HUMBLACKWON:
				return "Zmagal je igralec črnih.";
			case HUMWHITEWON:
				return "Zmagal je igralec belih.";
			case HUMWON:
				return "Zmagali ste.";
			}
			break;
		case ALLCOMPUTERS:
			return "Računalnik igra proti samemu sebi...<br />Črn: " + 
				game.intelligence1Name() + "<br />Bel: " + game.intelligence2Name();
		}
		return "";
	}
}
