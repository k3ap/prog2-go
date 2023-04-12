package logika;

/**
 * Abstraktni razred, ki predstavlja sprehod (iskanje) po mreži.
 * Razred implementira osnovno abstrakcijo, skupno DFS in BFS.
 * Za dejansko uporabo glej IskanjeVGlobino in IskanjeVSirino
 */
public abstract class Iskanje {
	
	/**
	 * Podatki, povezani s tem iskanjem.
	 */
	protected PodatkiIskanja podatki;
	
	/**
	 * Mreža, po kateri iščemo.
	 */
	protected Mreza mreza;
	
	/**
	 * Pripravi strukturo za iskanje od danega indeksa.
	 * @param zacetniIdx Indeks, kjer začnemo iskanje
	 */
	protected abstract void zacetek(Indeks zacetniIdx);
	
	/**
	 * Preveri, če je še kakšen indeks za preiskati
	 * @return true, če bo naslednji() še vrnil smiselno vrednost, sicer false
	 */
	protected abstract boolean jeNaslednji();

	/**
	 * Funkcija skrbi za vrstni red iskanja po grafu.
	 * @return Naslednji element, ki ga moramo preiskati
	 */
	protected abstract Indeks naslednji();
	
	/**
	 * Akcija, ki se zgodi na začetku obravnave vozlišča
	 * @param idx Indeks vozlišča, ki ga preiskujemo
	 * @param zacetniIdx Indeks, kjer se je iskanje začelo
	 */
	protected abstract void vstopnaAkcija(Indeks idx, Indeks zacetniIdx);
	
	/**
	 * Akcija, ki se zgodi tik pred koncem obravnave vozlišča
	 * @param idx Indeks vozlišča
	 * @param zacetniIdx Indeks, kjer se je iskanje začelo
	 */
	protected abstract void izstopnaAkcija(Indeks idx, Indeks zacetniIdx);
	
	/**
	 * Akcija, ki se zgodi, preden dodamo soseda nekega vozlišča v obravnavo
	 * (tudi če ga ne bomo)
	 * @param sosed Vozlišče, ki ga opazimo
	 * @param stars Vozlišče, iz katerega opazimo
	 * @param zacetniIdx Indeks, kjer se je iskanje začelo
	 */
	protected abstract void opaznaAkcija(Indeks sosed, Indeks stars, Indeks zacetniIdx);
	
	/**
	 * Funkcija vrne true, če moramo vozlišče sosed še obdelati 
	 * @param stars Vozlišče, iz katerega prihajamo
	 * @param sosed Vozlišče, ki ga želimo obravnavati
	 * @return Ali naj sosednjo vozlišče obravnavamo
	 */
	protected boolean dodajSoseda(Indeks stars, Indeks sosed) {
		return podatki.zaPreiskati(sosed);
	}
	
	/**
	 * Označi(dodaj v vrsto/sklad/...), da je treba dani indeks preiskati
	 * @param idx Indeks
	 */
	protected abstract void oznaciZaPreiskati(Indeks idx);
	
	/**
	 * Pomožna tabela, ki hrani indekse sosednjih vozlišč.
	 */
	private final int sosedi[][] = new int[][] {
		{-1, 0}, {1, 0}, {0, 1}, {0, -1}
	};
	
	/**
	 * Funkcija naredi en korak v zaporedju iskanja.
	 * @param zacetniIdx Indeks, kjer se je iskanje začelo
	 */
	private void korakIskanja(Indeks zacetniIdx) {
		Indeks idx = naslednji();
		vstopnaAkcija(idx, zacetniIdx);
		
		// Preišči vse sosede trenutnega vozlišča
		for (int x = 0; x < 4; x++) {
			int i = idx.i() + sosedi[x][0];
			int j = idx.j() + sosedi[x][1];
			
			if (i < 0 || j < 0 || i >= mreza.visina() || j >= mreza.sirina())
				continue;
			
			Indeks novIdx = new Indeks(i, j);
			opaznaAkcija(novIdx, idx, zacetniIdx);
			if (dodajSoseda(idx, novIdx)) {
				oznaciZaPreiskati(novIdx);
				podatki.oznaciPreiskano(novIdx);
			}
		}
		izstopnaAkcija(idx, zacetniIdx);
	}
	
	/**
	 * Ustvari novo instanco iskanja
	 * @param mreza Mreža, po kateri iščemo
	 * @param podatki Podatki, povezani z iskanjem
	 */
	public Iskanje(Mreza mreza, PodatkiIskanja podatki) {
		this.mreza = mreza;
		this.podatki = podatki;
	}
	
	/**
	 * Požene iskanje, začenši z enim indeksom
	 * @param zacetniIdx Indeks, od kjer iščemo
	 */
	public void pozeni(Indeks zacetniIdx) {
		this.zacetek(zacetniIdx);
		while (this.jeNaslednji()) {
			korakIskanja(zacetniIdx);
		}
	}
	
	/**
	 * Poženi iskanje na vseh delih mreže
	 */
	public void pozeniVse() {
		podatki.ponastavi();
		for (int i = 0; i < mreza.visina(); i++) {
			for (int j = 0; j < mreza.sirina(); j++) {
				Indeks idx = new Indeks(i, j);
				if (podatki.zaPreiskati(idx)) {
					pozeni(idx);
				}
			}
		}
	}
}
