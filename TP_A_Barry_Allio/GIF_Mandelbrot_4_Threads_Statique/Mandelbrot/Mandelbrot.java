// -*- coding: utf-8 -*-

import java.awt.Color;

public class Mandelbrot {
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à determiner
    final static int max = 100_000 ; 
    // C'est le nombre maximum d'iterations pour determiner la couleur d'un pixel

	public static boolean ThreadAlive(Thread t1, Thread t2, Thread t3, Thread t4) {
		if(t1.isAlive() || t2.isAlive() || t3.isAlive()|| t4.isAlive() ){
			return true;
		}else{return false;}
	}
		
		
		
    static class dessin extends Thread{
        volatile int a1,a2,b1,b2;
        volatile float debut2, fin2, duree2;
        public dessin(int xa, int ya, int xb, int yb){
            this.a1=xa;
            this.a2=ya;
            this.b1=xb;
            this.b2=yb;
            this.debut2=System.nanoTime() ;
            this.fin2=0;
            this.duree2=0;
        }

        public void run(){
            for (int i = a1; i < a2; i++) {
                for (int j = b1; j < b2; j++) {
                    synchronized(this){colorierPixel(i,j) ;}
                }
                synchronized(image){image.show();}         // Pour visualiser l'evolution de l'image
            }
            System.out.println(a1+"|"+a2+"|"+b1+"|"+b2);
            fin2 = System.nanoTime() ;
            duree2 = (fin2 - debut2) / 1_000_000 ;
            System.out.println("Duree du thread = " + (double) duree2 / 1000 + " s.") ;
        }
    }
    public static void main(String[] args)  {
        final long debut = System.nanoTime() ;
		long prec = System.nanoTime() ;
		long nb_png = 001;
		
        Thread t1 = new Thread(new dessin(0,taille/2,0, taille/2));
        Thread t2 = new Thread(new dessin(taille/2,taille,0,taille)); 
        Thread t3 = new Thread(new dessin(0,taille/2,taille/2,taille));  
        Thread t4 = new Thread(new dessin(taille/2,taille,taille/2,taille));
            
        t1.start();
        t2.start();
        t3.start();
        t4.start();
		
		while(ThreadAlive(t1,t2,t3,t4)){
			final long tmp_actuel = (System.nanoTime() - prec)/ 1_000_000;	
			if((tmp_actuel/100)>=1 && nb_png<1_000_000){
				System.out.println("temps passe depuis precedent screenshot : " + (tmp_actuel)/100 +" secondes") ;
				System.out.println("Sauvegarde de l'image " + "pic"+nb_png+".png") ;
				synchronized(image){image.save("pic"+nb_png+".png");}
				prec = System.nanoTime() ;
				nb_png=nb_png+001;
			}
		}
		
        
try{
        t1.join();
        t2.join();
        t3.join();
        t4.join();
		
}catch(InterruptedException exc){System.out.println("aled");}
        final long fin = System.nanoTime() ;
        final long duree = (fin - debut) / 1_000_000 ;
        System.out.println("Duree totale = " + (double) duree / 1000 + " s.") ;
        
        //synchronized(image){image.show();}
    }    

    // La fonction colorierPixel(i,j) colorie le pixel (i,j) de l'image en gris ou blanc
    public static void colorierPixel(int i, int j) {
        final Color gris = new Color(80, 0, 0) ;
        final Color blanc = new Color(255, 255, 255) ;
        final double xc = -.5 ;
        final double yc = 0 ; // Le point (xc,yc) est le centre de l'image
        final double region = 2 ;
        /*
          La region du plan consideree est un carre de côte egal à 2.
          Elle s'etend donc du point (xc - 1, yc - 1) au point (xc + 1, yc + 1)
          c'est-à-dire du point (-1.5, -1) en bas à gauche au point (0.5, 1) en haut
          à droite
        */
        double a = xc - region/2 + region*i/taille ;
        double b = yc - region/2 + region*j/taille ;
        // Le pixel (i,j) correspond au point (a,b)
        if (mandelbrot(a, b, max)) image.set(i, j, gris) ;
        else image.set(i, j, blanc) ; 
    }

    // La fonction mandelbrot(a, b, max) determine si le point (a,b) est gris
    public static boolean mandelbrot(double a, double b, int max) {
        double x = 0 ;
        double y = 0 ;
        for (int t = 0; t < max; t++) {
            if (x*x + y*y > 4.0) return false ; // Le point (a,b) est blanc
            double nx = x*x - y*y + a ;
            double ny = 2*x*y + b ;
            x = nx ;
            y = ny ;
        }
        return true ; // Le point (a,b) est gris
    }
}


/* 
   $ make
   javac *.java 
   jar cvmf MANIFEST.MF Mandelbrot.jar *.class 
   manifeste ajoute
   ajout : Mandelbrot.class(entree = 1697) (sortie = 1066)(compression : 37 %)
   ajout : Picture.class(entree = 5689) (sortie = 3039)(compression : 46 %)
   rm *.class 
   $ java -jar Mandelbrot.jar
   Duree = 35.851 s.
   ^C
*/
