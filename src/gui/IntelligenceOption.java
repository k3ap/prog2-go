package gui;

import inteligenca.Inteligenca;
import inteligenca.RandomMoveChooser;

public class IntelligenceOption {
	enum Options {
		RANDOM,
	}
	
	private Options option;
	
	public IntelligenceOption(Options opt) {
		this.option = opt;
	}
	
	public static IntelligenceOption[] getAll() {
		IntelligenceOption[] out = new IntelligenceOption[Options.values().length];
		int pos = 0;
		for (Options opt : Options.values()) {
			out[pos++] = new IntelligenceOption(opt);
		}
		return out;
	}
	
	@Override
	public String toString() {
		switch (option) {
		case RANDOM:
			return "Naključne poteze";
		}
		
		assert(false);
		return "Nima imena";
	}
	
	public Inteligenca toIntelligence() {
		switch (option) {
		case RANDOM:
			return new Inteligenca(new RandomMoveChooser());
		}
		
		assert(false);
		return null;
	}
}
