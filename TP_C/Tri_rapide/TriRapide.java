// -*- coding: utf-8 -*-

import java.util.Arrays;
import java.util.Random ;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TriRapide {
    static final int taille = 100_000_000 ;                   // Longueur du tableau à trier
    //static final int taille = 100000 ;
    static final int [] tableau = new int[taille] ;         // Le tableau d'entiers à trier 
    static final int [] tableau2 = new int[taille] ;
    static final int borne = 10 * taille ;                  // Valeur maximale dans le tableau

    private static void echangerElements(int[] t, int m, int n) {
        int temp = t[m] ;
        t[m] = t[n] ;
        t[n] = temp ;
    }

    private static int partitionner(int[] t, int début, int fin) {
        int v = t[fin] ;                               // Choix (arbitraire) du pivot : t[fin]
        int place = début ;                            // Place du pivot, à droite des éléments déplacés
        for (int i = début ; i<fin ; i++) {            // Parcours du *reste* du tableau
            if (t[i] < v) {                            // Cette valeur t[i] doit être à droite du pivot
                echangerElements(t, i, place) ;        // On le place à sa place
                place++ ;                              // On met à jour la place du pivot
            }
        }
        echangerElements(t, place, fin) ;              // Placement définitif du pivot
        return place ;
    }

    private static void trierRapidement(int[] t, int début, int fin) {
        if (début < fin) {                             // S'il y a un seul élément, il n'y a rien à faire!
            int p = partitionner(t, début, fin) ;      
            trierRapidement(t, début, p-1) ;
            trierRapidement(t, p+1, fin) ;
        }
    }
/*
    public static class trierRapidement2 implements Runnable {
        final int t[];
        final int debut;
        final int fin;
        public trierRapidement2(int[] t, int debut, int fin){
            this.t=t;
            this.debut=debut;
            this.fin=fin;
        }

        public void run() {
            if (debut < fin) {// S'il y a un seul élément, il n'y a rien à faire!
                final int p[] = new int[4];
                p[1]= partitionner(t, debut, fin);
                p[2] = partitionner(t, debut, fin - 1);
                p[3] = partitionner(t, debut, fin - 2);
                Arrays.sort(p);
                ExecutorService executeur = Executors.newFixedThreadPool(4);

                // Remplissage de la liste des tâches
                trierRapidement2[] mesTaches = new trierRapidement2[4];

                mesTaches[0] = new trierRapidement2(t, 0, p[1]);
                executeur.execute(mesTaches[0]);

                mesTaches[1] = new trierRapidement2(t, p[1], p[2]);
                executeur.execute(mesTaches[1]);

                mesTaches[2] = new trierRapidement2(t, p[2], p[3]);
                executeur.execute(mesTaches[2]);

                mesTaches[3] = new trierRapidement2(t, p[3], fin);
                executeur.execute(mesTaches[3]);


                executeur.shutdown(); // Il n'y a plus aucune tâche à soumettre
                // Il faut maintenant attendre la fin des calculs

                try {
                    while (!executeur.awaitTermination(1, TimeUnit.SECONDS)) {
                        //System.out.print("#");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
*/
public static class trierRapidement2 implements Runnable {

    //public final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    public final int MAX_THREADS = 4;
    final ExecutorService executeur = Executors.newFixedThreadPool(MAX_THREADS);


    final int t[];
    final int debut;
    final int fin;

    private final int minParitionSize;

    public trierRapidement2(int minParitionSize, int[] t, int debut, int fin){
        this.t=t;
        this.minParitionSize=minParitionSize;
        this.debut=debut;
        this.fin=fin;
    }

    public void run() {
        ExecParallèle(t, debut, fin);
    }

    public void ExecParallèle(int[] t, int debut, int fin) {
        int TailleActuelle = fin - debut + 1;

        if (TailleActuelle <= 1)
            return;

        int Pivot = partitionner(t, debut, fin);

        if (TailleActuelle > minParitionSize && TailleActuelle > taille / 100) {

            trierRapidement2 quick = new trierRapidement2(minParitionSize, t, debut, Pivot - 1);
            Future<?> future = executeur.submit(quick);
            ExecParallèle(t, Pivot + 1, fin);

            try {
                future.get(1000, TimeUnit.SECONDS);
            } catch (Exception ex) {
                //ex.printStackTrace();
            }
        } else {
            trierRapidement(t, debut, Pivot - 1);
            trierRapidement(t, Pivot + 1, fin);
        }
    }
}

    private static void afficher(int[] t, int début, int fin) {
        for (int i = début ; i <= début+3 ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("...") ;
        for (int i = fin-3 ; i <= fin ; i++) {
            System.out.print(" " + t[i]) ;
        }
        System.out.print("\n") ;
    }

    public static void main(String[] args) {
        Random alea = new Random() ;
        for (int i=0 ; i<taille ; i++) {                          // Remplissage aléatoire du tableau
            tableau[i] = alea.nextInt(2*borne) - borne ;
            tableau2[i] = tableau[i];
        }
        System.out.print("Tableau initial : ") ;
        afficher(tableau, 0, taille -1) ;                         // Affiche le tableau à trier

        System.out.println("Démarrage du tri rapide.") ;
        long débutDuTri = System.nanoTime();
        trierRapidement(tableau, 0, taille-1) ;                   // Tri du tableau
        long finDuTri = System.nanoTime();
        long duréeDuTri = (finDuTri - débutDuTri) / 1_000_000 ;
        System.out.print("Tableau trié Séquentiel : ") ;
        afficher(tableau, 0, taille -1) ;
        System.out.println("obtenu en " + duréeDuTri + " millisecondes.") ;


        System.out.println("Démarrage du tri Parallèle.") ;
        long débutDuTriParallèle = System.nanoTime();
        trierRapidement2 quick = new trierRapidement2(1000, tableau2, 0, taille-1);
        quick.run();
        long finDuTriParallèle = System.nanoTime();
        long duréeDuTriParallèle = (finDuTriParallèle - débutDuTriParallèle) / 1_000_000 ;
        System.out.print("Tableau trié Parallèle : ") ;
        afficher(tableau2, 0, taille -1) ;
        System.out.println("obtenu en " + duréeDuTriParallèle + " millisecondes.") ;

        System.out.println("est trié : " + Arrays.equals(tableau, tableau2));
        System.out.println("On obtient donc un gain de " + duréeDuTri / duréeDuTriParallèle + " secondes.");
    }
}


/*
  $ make
  javac *.java
  $ java TriRapide
  Tableau initial :  4967518 -8221265 -951337 4043143... -4807623 -1976577 -2776352 -6800164
  Démarrage du tri rapide.
  Tableau trié :  -9999981 -9999967 -9999957 -9999910... 9999903 9999914 9999947 9999964
  obtenu en 85 millisecondes.
*/
