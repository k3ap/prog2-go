package vodja;

import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import gui.Okno;
import inteligenca.Inteligenca;
import logika.BarvaIgralca;
import logika.BarvaPolja;
import logika.Igra;
import logika.Indeks;
import splosno.Poteza;

/**
 * Ovojnica okoli Igra in Inteligenca, ki sodeluje s Platno-om, da igra teče.
 */
public class VodenaIgra {
	private Igra igra;
	private TipIgre tipIgre;
	private Inteligenca inteligenca;
	private RezultatPoteze status;
	private Okno okno;

	/**
	 * Naredi novo vodeno 9x9 igro glede na želen tip igre.
	 * @param tipIgre Glej razred {@link TipIgre}
	 */
	public VodenaIgra(TipIgre tipIgre, Okno okno) {
		igra = new Igra();
		this.okno = okno;
		urediTip(tipIgre);
	}

	/**
	 * Naredi novo vodeno igro glede na želen tip igre in velikost mreže.
	 * @param tipIgre Glej razred {@link TipIgre}
	 * @param velikost Velikost (širina in višina) mreže, na kateri se igra, privzeta vrednost je 9.
	 */
	public VodenaIgra(TipIgre tipIgre, int velikost) {
		igra = new Igra(velikost);
		urediTip(tipIgre);
	}

	private void urediTip(TipIgre tipIgre) {
		this.tipIgre = tipIgre;
		switch (tipIgre) {
		case CLOCLO:
			inteligenca = null;
			status = RezultatPoteze.IGRAMO;
			break;
		case CLORAC:
			inteligenca = new Inteligenca();
			status = RezultatPoteze.IGRAMO; // najprej je na vrsti človek
			break;
		case RACCLO:
			inteligenca = new Inteligenca();
			status = RezultatPoteze.POCAKAJ; // najprej je na vrsti računalnik
			inteligencaNarediPotezo();
			break;
		}
	}

	/**
	 * Preveri če je veljavna in po potrebi odigra dano potezo.
	 * Spremeni status na rezultat obravnave poteze.
	 * @param poteza Instanca razreda {@link Poteza}
	 * @see #statusIgre
	 */
	public void odigraj(Poteza poteza) {
		if (!igra.odigraj(poteza)) {
			// če je izbrana neveljavna poteza ne naredimo nič
			status = RezultatPoteze.NEVELJAVNA;
			return;
		}
		
		status = zmagovalecVRezultat(igra.zmagovalec());
		if ((tipIgre == TipIgre.CLORAC || tipIgre == TipIgre.RACCLO) && status == RezultatPoteze.IGRAMO) {
			// ko človek konča s potezo je na vrsti računalnik
			status = RezultatPoteze.POCAKAJ;
			inteligencaNarediPotezo();
		}
	}

	private void inteligencaNarediPotezo() {
		SwingWorker<Poteza, Void> worker =
				new SwingWorker<Poteza, Void> () {
			@Override
			protected Poteza doInBackground() {
				return inteligenca.izberiPotezo(igra);
			}
			@Override
			protected void done () {
				// Poskusimo prebrat odločitev inteligence.
				Poteza poteza = null;
				try {
					poteza = get();
				}
				catch (ExecutionException | InterruptedException e) {
					// V končanem programu se inteligenca naj ne bi sesuvala,
					// tako da je to samo za debugging.
					e.printStackTrace();
					status = RezultatPoteze.NAPAKA;
					okno.update();
					return;
				}

				if (poteza == null) {
					// Samo za debugging, to se naj ne bi zgodilo.
					System.out.println("Inteligenca.izberiPotezo je vrnil null.");
					status = RezultatPoteze.NAPAKA;
					okno.update();
					return;
				}

				igra.odigraj(poteza);
				status = zmagovalecVRezultat(igra.zmagovalec());
				okno.update();
			}
		};
		status = RezultatPoteze.POCAKAJ;
		worker.execute();
	}
	
	private RezultatPoteze zmagovalecVRezultat(BarvaIgralca zmagovalec) {
		if (zmagovalec == null) {
			return RezultatPoteze.IGRAMO;
		}
		switch (igra.zmagovalec()) {
		case BELA:
			return RezultatPoteze.ZMAGABEL;
		case CRNA:
			return RezultatPoteze.ZMAGACRN;
		default:
			assert false;
			return RezultatPoteze.IGRAMO;
		}
	}

	/**
	 * Vrne trenuten status igre.
	 * @return Trenuten status igre kot {@link RezultatPoteze}.
	 */
	public RezultatPoteze statusIgre() { return status; }

	// pokažemo metode iz igre
	/**
	 * @return Širina mreže trenutne igre.
	 */
	public int sirina() { return igra.sirina(); }
	/**
	 * @return Višina mreže trenutne igre.
	 */
	public int visina() { return igra.visina(); }
	/**
	 * @param idx {@link Indeks} polja, ki ga želimo preveriti.
	 * @return {@link BarvaPolja}, na danem indeksu.
	 */
	public BarvaPolja barvaPolja(Indeks idx) { return igra.barvaPolja(idx); }
	/**
	 * @return {@link BarvaIgralca}, ki je na potezi.
	 */
	public BarvaIgralca naPotezi() { return igra.naPotezi(); }
}
