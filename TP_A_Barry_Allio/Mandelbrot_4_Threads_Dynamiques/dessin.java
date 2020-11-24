
public class dessin extends Thread{
	volatile int x,y,epaisseur;
	volatile float debut2, fin2, duree2;
	public dessin(int x, int y, int epaisseur){
		this.x=x;
		this.y=y;
		this.epaisseur=epaisseur;
		this.debut2=System.nanoTime() ;
		this.fin2=0;
		this.duree2=0;
	}

	@Override
	public void run(){
		while(Mandelbrot.Ligne_tracees<Mandelbrot.taille){
			synchronized (Mandelbrot.Ligne_tracees){
				this.x=Mandelbrot.Ligne_tracees;
				this.y=Mandelbrot.Ligne_tracees+1;
				Mandelbrot.Ligne_tracees++;
			}
			
			for (int i = 0; i < epaisseur; i++) {
				for (int j = x; j < y; j++) {
					synchronized(this){Mandelbrot.colorierPixel(i,j);}
				}
				synchronized(Mandelbrot.image){Mandelbrot.image.show();}         // Pour visualiser l'evolution de l'image
			}
			
		}
		fin2 = System.nanoTime() ;
		duree2 = (fin2 - debut2) / 1_000_000 ;
		System.out.println("Duree du thread = " + (double) duree2 / 1000 + " s.") ;
	}
}
