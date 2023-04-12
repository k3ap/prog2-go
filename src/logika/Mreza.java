package logika;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Predstavitev igralne mreže
 */
public class Mreza {
	
	/**
	 * Pomožni razred za iskanje povezanih komponent v grafu
	 * Hrani "barvo" vozlišč; vozlišči sta iste barve nt, ko sta v isti komponenti
	 */
	private class PodatkiIskanjaKomponent extends PodatkiIskanja {
		private int steviloBarv = 0;
		
		public int naslednjaBarva() {
			return ++steviloBarv;
		}
		
		@Override
		public void ponastavi() {
			super.ponastavi();
			steviloBarv = 0;
		}
	}
	
	/**
	 * Pomožni razred, ki išče povezane komponente v grafu.
	 * Med iskanjem nastavi vrednosti v mreza.povezaneKomponente
	 */
	private class IskanjeKomponent extends IskanjeVSirino {

		public IskanjeKomponent(Mreza mreza, PodatkiIskanjaKomponent podatki) {
			super(mreza, podatki);
		}

		@Override
		protected void vstopnaAkcija(Indeks idx, Indeks zacetniIdx) {
			if (idx.equals(zacetniIdx)) {
				mreza.povezaneKomponente[idx.i()][idx.j()] = 
						((PodatkiIskanjaKomponent)podatki).naslednjaBarva();
			} else {
				mreza.povezaneKomponente[idx.i()][idx.j()] = 
						mreza.povezaneKomponente[zacetniIdx.i()][zacetniIdx.j()];
			}
		}

		@Override
		protected void izstopnaAkcija(Indeks idx, Indeks zacetniIdx) {}

		@Override
		protected void opaznaAkcija(Indeks idx, Indeks stars, Indeks zacetniIdx) {}

		@Override
		protected boolean dodajSoseda(Indeks stars, Indeks sosed) {
			return super.dodajSoseda(stars, sosed) 
					&& mreza.barvaPolja(sosed).equals(mreza.barvaPolja(stars));
		}
	}
	
	/**
	 * Pomožni razred za hranjenje podatkov o svobodah med iskanjem
	 */
	private class PodatkiIskanjaSvobod extends PodatkiIskanja {
		private HashMap< Integer, HashSet<Indeks> > svobode;
		
		public PodatkiIskanjaSvobod() {
			super();
			this.svobode = new HashMap<>();
		}
		
		@Override
		public void ponastavi() {
			super.ponastavi();
			svobode.clear();
		}
		
		public int steviloSvobod(int povezanaBarva) {
			if (!svobode.containsKey(povezanaBarva)) {
				return 0;
			}
			return svobode.get(povezanaBarva).size();
		}
		
		public void oznaciKotSvobodo(int barva, Indeks idx) {
			if (svobode.containsKey(barva)) {
				svobode.get(barva).add(idx);
			} else {
				svobode.put(barva, new HashSet<>());
				svobode.get(barva).add(idx);
			}
		}
	}
	
	/**
	 * Pomožni razred za iskanje svobod določene komponente
	 * Predpostavi, da je bilo iskanje komponent že zaključeno in 
	 * da so podatki v mreza.povezaneKomponente točni
	 */
	private class IskanjeSvobod extends IskanjeVSirino {

		public IskanjeSvobod(Mreza mreza, PodatkiIskanjaSvobod podatki) {
			super(mreza, podatki);
		}

		@Override
		protected void vstopnaAkcija(Indeks idx, Indeks zacetniIdx) {}

		@Override
		protected void izstopnaAkcija(Indeks idx, Indeks zacetniIdx) {}

		@Override
		protected boolean dodajSoseda(Indeks stars, Indeks sosed) {
			return super.dodajSoseda(stars, sosed)
					&& mreza.barvaPolja(stars).equals(mreza.barvaPolja(sosed)) 
					&& !mreza.barvaPolja(stars).equals(BarvaPolja.PRAZNA);
		}

		@Override
		protected void opaznaAkcija(Indeks idx, Indeks stars, Indeks zacetniIdx) {
			if (mreza.barvaPolja(stars) == BarvaPolja.PRAZNA) 
				return;
			
			if (mreza.barvaPolja(idx) == BarvaPolja.PRAZNA) {
				int barva = mreza.povezaneKomponente[stars.i()][stars.j()];
				PodatkiIskanjaSvobod p = (PodatkiIskanjaSvobod) (this.podatki);
				p.oznaciKotSvobodo(barva, idx);
			}
		}
		
	}
	
	/**
	 * Hrani trenutno barvo kamna, postavljenega na nek del mreže
	 */
	private BarvaPolja mreza[][];
	
	/**
	 * Hrani števila ("barve"), ki predstavljajo povezano komponento.
	 * Enako število pomeni ista komponenta.
	 * To tabelo izpolni IskanjeKomponent
	 */
	private int povezaneKomponente[][];
	
	/**
	 * Višina in širina mreže.
	 */
	private int visina, sirina;

	private IskanjeKomponent iskanjeKomponent;
	private IskanjeSvobod iskanjeSvobod;
	
	public Mreza(int visina, int sirina) {
		mreza = new BarvaPolja[visina][sirina];
		for (int i = 0; i < visina; i++) {
			for (int j = 0; j < sirina; j++) {
				mreza[i][j] = BarvaPolja.PRAZNA;
			}
		}
		povezaneKomponente = new int[visina][sirina];
		this.visina = visina;
		this.sirina = sirina;
		iskanjeKomponent = new IskanjeKomponent(this, new PodatkiIskanjaKomponent());
		iskanjeKomponent.pozeniVse();
		iskanjeSvobod = new IskanjeSvobod(this, new PodatkiIskanjaSvobod());
		iskanjeSvobod.pozeniVse();
	}
	
	public int visina() { return visina; }
	public int sirina() { return sirina; }
	
	/**
	 * Vrne trenutno barvo kamna na danem polju.
	 * @param idx Indeks polja
	 * @return Barva kamna na indeksu
	 */
	public BarvaPolja barvaPolja(Indeks idx) {
		return mreza[idx.i()][idx.j()];
	}
	
	/**
	 * Preveri, če sta dva indeksa v isti komponenti
	 * @param idx1 Indeks prvega vozlišča
	 * @param idx2 Indeks drugega vozlišča
	 * @return true nt, ko sta indeksa v isti komponenti 
	 */
	public boolean istaKomponenta(Indeks idx1, Indeks idx2) {
		return povezaneKomponente[idx1.i()][idx1.j()] == povezaneKomponente[idx2.i()][idx2.j()];
	}
	
	/**
	 * Nastavi kamen v mreži na dano barvo
	 * @param idx Indeks v mreži
	 * @param barva Barva kamna
	 */
	public void postaviBarvo(Indeks idx, BarvaPolja barva) {
		mreza[idx.i()][idx.j()] = barva;
		iskanjeKomponent.pozeniVse();
		iskanjeSvobod.pozeniVse();
		/*for (int i = 0; i < visina; i++) {
			for (int j = 0; j < sirina; j++) {
				System.out.format("%d ", povezaneKomponente[i][j]);
			}
			System.out.println();
		}*/
	}
	
	/**
	 * Preveri, če je dana barva izgubila igro (če ima komponento brez svobod)
	 * @param barva Barva, za katero iščemo prazne svobode. Deluje smiselno le za CRNA ali BELA
	 * @return true, če je dana barva izgubila igro
	 */
	public boolean jeBarvaIzgubila(BarvaPolja barva) {
		for (int i = 0; i < visina; i++) {
			for (int j = 0; j < sirina; j++) {
				if (mreza[i][j] != barva) continue;
				int povezanaBarva = povezaneKomponente[i][j];
				if (((PodatkiIskanjaSvobod)iskanjeSvobod.podatki).steviloSvobod(povezanaBarva) == 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Poglej, če je polje z danim ideksom prosto
	 * @param idx Indeks
	 * @return true, kadar je polje idx prosto
	 */
	public boolean jePoljeProsto(Indeks idx) {
		return barvaPolja(idx) == BarvaPolja.PRAZNA;
	}
	
	/**
	 * Poišči vsa prosta polja
	 * @return Seznam prostih polj v mreži
	 */
	public List<Indeks> prostaPolja() {
		ArrayList<Indeks> res = new ArrayList<>();
		for (int i = 0; i < visina; i++) {
			for (int j = 0; j < sirina; j++) {
				Indeks idx = new Indeks(i, j);
				if (jePoljeProsto(idx)) {
					res.add(idx);
				}
			}
		}
		return res;
	}

	public void izpisi() {
		for (int i = 0; i < visina; i++) {
			for (int j = 0; j < sirina; j++) {
				char c = switch(mreza[i][j]) {
				case PRAZNA -> '.';
				case BELA -> 'o';
				case CRNA -> '#';
				};
				System.out.print(c);
			}
			System.out.println();
		}
	}
}
