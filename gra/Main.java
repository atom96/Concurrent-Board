package gra;


import gra.Kierunek;

public class Main {

	public static void main(String[] args) {
		
		/**
		 * Test poruszania się
		 */
		Thread test1 = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Plansza plansza = new MojaPlansza(10, 10);
				Postać postać = new MojaPostać(1, 1, 'a');
				try {
					plansza.postaw(postać, 0, 0);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.PRAWO);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.PRAWO);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.PRAWO);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.DÓŁ);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.DÓŁ);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.LEWO);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.LEWO);
					System.out.println(plansza);
					plansza.przesuń(postać, Kierunek.GÓRA);
					System.out.println(plansza);

				} catch (InterruptedException | DeadlockException e) {
					System.out.println("Coś się popsuło dla postaci " + postać);
					e.printStackTrace();
				}
				
				
			}
		});
		
		test1.start();
		
		/**
		 * Test Deadlocka
		 */
		final Plansza plansza = new MojaPlansza(10, 10);
		
		Thread threads[] = {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Postać pionek = new MojaPostać(1, 3, 'a');
						try {
							Thread.sleep(100);
							plansza.postaw(pionek, 3, 1);
							System.out.println(plansza);
							Thread.sleep(100);
							plansza.przesuń(pionek, Kierunek.DÓŁ);
							System.out.println(plansza);
							Thread.sleep(100);

						} catch (InterruptedException e) {
							System.out.println("Cos się popsuło z postacią: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						} catch (DeadlockException e) {
							System.out.println("Deadlock dla postaci: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}
				}),
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Postać pionek = new MojaPostać(1, 3, 'b');
						try {
							Thread.sleep(200);
							plansza.postaw(pionek, 4, 1);
							System.out.println(plansza);
							Thread.sleep(200);
							plansza.przesuń(pionek, Kierunek.PRAWO);
							System.out.println(plansza);
							Thread.sleep(100);
							//plansza.przesuń(pionek, Kierunek.DÓŁ);

						} catch (InterruptedException e) {
							System.out.println("Cos się popsuło z postacią: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}  catch (DeadlockException e) {
							System.out.println("Deadlock dla postaci: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}
				}),
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Postać pionek = new MojaPostać(2, 4, 'c');
						try {
							Thread.sleep(300);
							plansza.postaw(pionek, 4, 4);
							System.out.println(plansza);
							Thread.sleep(300);
							plansza.przesuń(pionek, Kierunek.GÓRA);
							System.out.println(plansza);
							Thread.sleep(100);
							plansza.usuń(pionek);
							System.out.println(plansza);
							Thread.sleep(100);

						} catch (InterruptedException e) {
							System.out.println("Cos się popsuło z postacią: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						} catch (DeadlockException e) {
							System.out.println("Deadlock dla postaci: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						} catch (IllegalArgumentException e) {
							System.out.println("Coś jest zle dla postaci: " + pionek);
							e.printStackTrace();		
							Thread.currentThread().interrupt();
						}
						
					}
				}),

				new Thread(new Runnable() {
					
					@Override
					public void run() {
						Postać pionek = new MojaPostać(1, 3, 'd');
						try {
							Thread.sleep(400);
							plansza.postaw(pionek, 3, 4);
							System.out.println(plansza);
							Thread.sleep(400);
							plansza.przesuń(pionek, Kierunek.LEWO);
							System.out.println(plansza);
							Thread.sleep(100);
						} catch (InterruptedException e) {
							System.out.println("Cos się popsuło z postacią: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						} catch (DeadlockException e) {
							System.out.println("Deadlock dla postaci: " + pionek);
							e.printStackTrace();	
							Thread.currentThread().interrupt();
						}
					}
				}),
				/*new Thread(new Runnable() {
					
					@Override
					public void run() {
						Postać pionek = new MojaPostać(3, 3, 'e');
						try {
							Thread.sleep(500);
							plansza.postaw(pionek, 3, 4);

						} catch (InterruptedException e) {
							System.out.println("Cos się popsuło z postacią: " + pionek);
							e.printStackTrace();
							Thread.currentThread().interrupt();
						}
					}
				}),*/
		};
		
		for(Thread t: threads) {
			t.start();
		}
		Thread.currentThread().interrupt();
	}
}
