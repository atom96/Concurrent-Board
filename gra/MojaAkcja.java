package gra;

public class MojaAkcja implements Akcja{
	@Override
	public void wykonaj(Postać postać) {
		System.out.println(postać + "istnieje na planszy");
	}
}
