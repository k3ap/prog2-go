package logika;

import java.util.HashMap;
import java.util.HashSet;

public class Mreza {
	
	/*	Pomožna razreda za iskanje povezanih komponent v grafu
	 * */
	
	private class PodatkiIskanjaKomponent extends PodatkiIskanja {
		private int steviloBarv = 0;
		
		public int naslednjaBarva() {
			return ++steviloBarv;
		}
		
		@Override
		public void ponastavi() {
			steviloBarv = 0;
		}
	}
	
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
			return mreza.barvaPolja(sosed) == mreza.barvaPolja(stars);
		}
	}
	
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
			return svobode.get(povezanaBarva).size();
		}
	}
	
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
			return mreza.barvaPolja(stars) == mreza.barvaPolja(sosed) && mreza.barvaPolja(stars) != BarvaPolja.PRAZNA;
		}

		@Override
		protected void opaznaAkcija(Indeks idx, Indeks stars, Indeks zacetniIdx) {
			if (mreza.barvaPolja(stars) == BarvaPolja.PRAZNA) 
				return;
			
			if (mreza.barvaPolja(idx) == BarvaPolja.PRAZNA) {
				int barva = mreza.povezaneKomponente[idx.i()][idx.j()];
				PodatkiIskanjaSvobod p = (PodatkiIskanjaSvobod) (this.podatki);
				p.svobode.get(barva).add(idx);
			}
		}
		
	}
	
	private BarvaPolja mreza[][];
	private int povezaneKomponente[][];
	private int visina, sirina;
	private IskanjeKomponent iskanjeKomponent;
	private IskanjeSvobod iskanjeSvobod;
	
	public Mreza(int visina, int sirina) {
		mreza = new BarvaPolja[visina][sirina];
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
	
	public BarvaPolja barvaPolja(Indeks idx) {
		return mreza[idx.i()][idx.j()];
	}
	
	public boolean istaKomponenta(Indeks idx1, Indeks idx2) {
		return povezaneKomponente[idx1.i()][idx1.j()] == povezaneKomponente[idx2.i()][idx2.j()];
	}
	
	public void postaviBarvo(Indeks idx, BarvaPolja barva) {
		mreza[idx.i()][idx.j()] = barva;
		iskanjeKomponent.pozeniVse();
		iskanjeSvobod.pozeniVse();
	}
	
	public boolean jeBarvaIzgubila(BarvaPolja barva) {
		// vrne true, če je dana barva izgubila (ima skupino z 0 svobodami)
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
}
