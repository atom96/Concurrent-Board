package gra;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MojaPlansza implements Plansza {
	private static class Para {
		int wiersz;
		int kolumna;

		Para(int wiersz, int kolumna) {
			this.wiersz = wiersz;
			this.kolumna = kolumna;
		}

		int getWiersz() {
			return wiersz;
		}

		int getKolumna() {
			return kolumna;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null) {
				return false;
			}

			if (!(o instanceof Para)) {
				return false;
			}

			Para para = (Para) o;
			return this.wiersz == para.wiersz && this.kolumna == para.kolumna;
		}

		@Override
		public int hashCode() {
			return wiersz & kolumna;
		}
	}

	private int wysokość;
	private int szerokośc;
	private Map<Postać, Para> lewyGórnyRóg;
	private Map<Para, Postać> zajętePola;
	private Map<Postać, Set<Postać>> naKogoCzekam;

	public MojaPlansza(int wysokość, int szerokość) {
		this.wysokość = wysokość;
		this.szerokośc = szerokość;
		this.lewyGórnyRóg = new HashMap<>();
		this.zajętePola = new HashMap<>();
		this.naKogoCzekam = new HashMap<>();
	}

	private synchronized void usuńZPlanszy(Postać postać) {
		if (!lewyGórnyRóg.containsKey(postać)) {
			throw new IllegalArgumentException();
		}
		Para współrzędnePostaci = lewyGórnyRóg.get(postać);
		lewyGórnyRóg.remove(postać);
		naKogoCzekam.remove(postać);
		for (int i = współrzędnePostaci.getWiersz(); i < współrzędnePostaci
		        .getWiersz() + postać.dajWysokość(); ++i) {
			for (int j = współrzędnePostaci.getKolumna(); j < współrzędnePostaci
			        .getKolumna() + postać.dajSzerokość(); ++j) {
				zajętePola.remove(new Para(i, j));
			}
		}
	}

	private synchronized void dodajDoPlanszy(Postać postać, int wiersz,
	        int kolumna) {
		lewyGórnyRóg.put(postać, new Para(wiersz, kolumna));
		for (int i = wiersz; i < wiersz + postać.dajWysokość(); ++i) {
			for (int j = kolumna; j < kolumna + postać.dajSzerokość(); ++j) {
				zajętePola.put(new Para(i, j), postać);
			}
		}
	}

	@Override
	public synchronized void usuń(Postać postać) {
		usuńZPlanszy(postać);
		notifyAll();
	}

	@Override
	public synchronized void sprawdź(int wiersz, int kolumna, Akcja jeśliZajęte,
	        Runnable jeśliWolne) {
		if (wiersz < 0 || wiersz >= wysokość || kolumna < 0
		        || kolumna >= szerokośc) {
			throw new IllegalArgumentException();
		}

		if (zajętePola.containsKey(new Para(wiersz, kolumna))) {
			jeśliZajęte.wykonaj(zajętePola.get(new Para(wiersz, kolumna)));
		} else {
			jeśliWolne.run();
		}
	}

	private synchronized Set<Postać> postaciNaPolu(int wiersz, int kolumna,
	        int wysokość, int szerokość) {
		Set<Postać> postaciNaPolu = new HashSet<>();
		for (int i = wiersz; i < wiersz + wysokość; ++i) {
			for (int j = kolumna; j < kolumna + szerokość; ++j) {
				if (zajętePola.containsKey(new Para(i, j))) {
					postaciNaPolu.add(zajętePola.get(new Para(i, j)));
				}

			}
		}
		return postaciNaPolu;
	}

	@Override
	public synchronized void postaw(Postać postać, int wiersz, int kolumna)
	        throws InterruptedException {
		if (wiersz < 0 || wiersz + postać.dajWysokość() > wysokość
		        || kolumna < 0 || kolumna + postać.dajSzerokość() > szerokośc) {
			throw new IllegalArgumentException();
		}

		Set<Postać> blokujący = postaciNaPolu(wiersz, kolumna,
		        postać.dajWysokość(), postać.dajSzerokość());
		naKogoCzekam.put(postać, blokujący);
		while (!blokujący.isEmpty()) {
			wait();
			blokujący = postaciNaPolu(wiersz, kolumna, postać.dajWysokość(),
			        postać.dajSzerokość());
			naKogoCzekam.remove(postać);
			naKogoCzekam.put(postać, blokujący);
		}

		naKogoCzekam.remove(postać);
		dodajDoPlanszy(postać, wiersz, kolumna);
	}

	private synchronized void sprawdzDeadlock(Postać postać,
	        Set<Postać> naŚcieżce, Set<Postać> odwiedzone)
	        throws DeadlockException {
		if (naŚcieżce.contains(postać)) {
			throw new DeadlockException();
		}

		odwiedzone.add(postać);
		if (!naKogoCzekam.containsKey(postać)) {
			return;
		}

		for (Postać p : naKogoCzekam.get(postać)) {
			naŚcieżce.add(postać);
			if (!odwiedzone.contains(p) || naŚcieżce.contains(p)) {
				sprawdzDeadlock(p, naŚcieżce, odwiedzone);
			}
			naŚcieżce.remove(postać);
		}
	}

	private synchronized void sprawdzDeadlock(Postać postać)
	        throws DeadlockException {
		sprawdzDeadlock(postać, new HashSet<>(), new HashSet<>());
	}

	private synchronized int przesunięciePoziom(Kierunek k) {
		switch (k) {
		case GÓRA:
			return 0;
		case DÓŁ:
			return 0;
		case LEWO:
			return -1;
		case PRAWO:
			return 1;
		default:
			throw new IllegalArgumentException();
		}
	}

	private synchronized int przesunięciePion(Kierunek k) {
		switch (k) {
		case GÓRA:
			return -1;
		case DÓŁ:
			return 1;
		case LEWO:
			return 0;
		case PRAWO:
			return 0;
		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public synchronized void przesuń(Postać postać, Kierunek kierunek)
	        throws InterruptedException, DeadlockException {
		if (!lewyGórnyRóg.containsKey(postać)) {
			throw new IllegalArgumentException();
		}

		Para współrzędne = lewyGórnyRóg.get(postać);

		if (współrzędne.getWiersz() + przesunięciePion(kierunek) < 0
		        || współrzędne.getWiersz() + postać.dajWysokość()
		                + przesunięciePion(kierunek) > wysokość
		        || współrzędne.getKolumna() + przesunięciePoziom(kierunek) < 0
		        || współrzędne.getKolumna() + postać.dajSzerokość()
		                + przesunięciePoziom(kierunek) > szerokośc) {
			throw new IllegalArgumentException();
		}

		Set<Postać> blokujący = postaciNaPolu(
		        współrzędne.getWiersz() + przesunięciePion(kierunek),
		        współrzędne.getKolumna() + przesunięciePoziom(kierunek),
		        postać.dajWysokość(), postać.dajSzerokość());

		blokujący.remove(postać);
		naKogoCzekam.put(postać, blokujący);
		while (!blokujący.isEmpty()) {
			sprawdzDeadlock(postać);
			wait();
			blokujący = postaciNaPolu(
			        współrzędne.getWiersz() + przesunięciePion(kierunek),
			        współrzędne.getKolumna() + przesunięciePoziom(kierunek),
			        postać.dajWysokość(), postać.dajSzerokość());
			blokujący.remove(postać);
			naKogoCzekam.put(postać, blokujący);
		}

		naKogoCzekam.remove(postać);
		usuńZPlanszy(postać);
		dodajDoPlanszy(postać,
		        współrzędne.getWiersz() + przesunięciePion(kierunek),
		        współrzędne.getKolumna() + przesunięciePoziom(kierunek));
		notifyAll();
	}

	@Override
	public synchronized String toString() {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < wysokość; ++i) {
			for (int j = 0; j < szerokośc; ++j) {
				Para para = new Para(i, j);
				if (zajętePola.containsKey(para)) {
					try {
						builder.append(zajętePola.get(para).toString());
					} catch (NullPointerException e) {
						System.out.println(zajętePola.get(para));
					}
				} else {
					builder.append(".");
				}
			}
			builder.append('\n');
		}

		return builder.toString();
	}
}
