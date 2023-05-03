package gui;

import inteligenca.Inteligenca;
import inteligenca.RandomMoveChooser;

/**
 * Class that represents an algorithm used by Inteligenca to choose moves, 
 * that is exposed to the user as an option. The difference between it and
 * MoveChooser is that one algorithm can have multiple IntelligenceOption-s,
 * eg. alpha beta with depth 2 and alpha beta with depth 4
 */
public class IntelligenceOption {
	enum Options {
		RANDOM,
	}
	
	private Options option;
	
	public IntelligenceOption(Options opt) {
		this.option = opt;
	}
	
	/**
	 * Get all possible options to be displayed to the user.
	 * @return An array of all possible options.
	 */
	public static IntelligenceOption[] getAll() {
		IntelligenceOption[] out = new IntelligenceOption[Options.values().length];
		int pos = 0;
		for (Options opt : Options.values()) {
			out[pos++] = new IntelligenceOption(opt);
		}
		return out;
	}
	
	/**
	 * Return the nice readable name of this intelligence,
	 * to be presented to the user directly.
	 */
	@Override
	public String toString() {
		switch (option) {
		case RANDOM:
			return "Naključne poteze";
		}
		
		assert(false);
		return "Nima imena";
	}
	
	/**
	 * Create an instance of the Inteligenca class that this option represents.
	 * @return An instance of Inteligenca corresponding to this option.
	 */
	public Inteligenca toIntelligence() {
		switch (option) {
		case RANDOM:
			return new Inteligenca(new RandomMoveChooser());
		}
		
		assert(false);
		return null;
	}
}
