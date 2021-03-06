package gra;

public class MojaPostać implements Postać {
	int wysokość;
	int szerokość;
	char imię;
	
	public MojaPostać(int wysokość, int szerokość, char imię) {
		this.wysokość = wysokość;
		this.szerokość = szerokość;
		this.imię = imię;
	}
	
	@Override
	public int dajWysokość() {
		return wysokość;
	}
	
	@Override
	public int dajSzerokość() {
		return szerokość;
	}
	
	@Override
	public String toString() {
		return Character.toString(imię);
	}
}
