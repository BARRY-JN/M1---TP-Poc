// -*- coding: utf-8 -*-

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Mandelbrot {
    final static int taille = 500 ;   // nombre de pixels par ligne et par colonne
	static volatile Integer Ligne_tracees=0; 
    final static Picture image = new Picture(taille, taille) ;
    // Il y a donc taille*taille pixels blancs ou gris à determiner
    final static int max = 75_000 ;
    // C'est le nombre maximum d'iterations pour determiner la couleur d'un pixel

    public static void main(String[] args)  {
        final long debut = System.nanoTime() ;
		
		int Threads=4;
        int Lignes=500;
		
		List<dessin> threadList= new ArrayList<dessin>();
        for (int i=0; i<Threads;i++){
            threadList.add(new dessin((i*taille/Lignes),((i+1)*taille/Lignes),taille));
            threadList.get(i).start();
        }

		try{
			for(int i=0;i<Threads;i++){
                threadList.get(i).join();
            }
		}catch(InterruptedException e){e.printStackTrace();}
		
		final long fin = System.nanoTime() ;
		final long duree = (fin - debut) / 1_000_000 ;
		System.out.println("Duree totale = " + (double) duree / 1000 + " s.") ;
		
		synchronized(image){image.show();}
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
