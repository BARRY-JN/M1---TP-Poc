// -*- coding: utf-8 -*-

enum Cote { EST, OUEST }                       // Le canyon possède un cote EST et un cote OUEST

class Babouin extends Thread{
    private static int numeroSuivant = 0;      // Compteur partage par tous les babouins
    private final int numero;                  // Numero du babouin
    private Corde corde;                       // Corde utilisee par le babouin
    private Cote origine;                      // Cote du canyon où apparaît le babouin: EST ou OUEST

    Babouin(Corde corde, Cote origine){        // Constructeur de la classe Babouin
        this.corde = corde;                    // Chaque babouin peut utiliser la corde
        this.origine = origine;                // Chaque babouin apparaît d'un cote precis du canyon
        numero = ++numeroSuivant;              // Chaque babouin possède un numero distinct
    }

    public void run(){
        System.out.println("Le babouin " + numero + " arrive sur le cote " + origine + " du canyon.");
        corde.saisir(origine);                 // Pour traverser, le babouin saisit la corde
        System.out.println("Le babouin " + numero + " commence a traverser sur la corde en partant de l'" + origine + ".");
        try { sleep(5000); } catch(InterruptedException e){} // La traversee ne dure que 5 secondes
        corde.lacher(origine);                 // Arrive de l'autre cote, le babouin lache la corde
        System.out.println("Le babouin " + numero + " a lache la corde et s'en va.");
    }
    
    public static void main(String[] args){ 
        Corde corde = new Corde();                        // La corde relie les deux cotes du canyon
        for (int i = 1; i < 20; i++){
            try { Thread.sleep(500); } catch(InterruptedException ignoree){}		    
            if (Math.random() >= 0.5){
                new Babouin(corde, Cote.EST).start();     // Creation d'un babouin à l'est du canyon
            } else {
                new Babouin(corde, Cote.OUEST).start(); // Creation d'un babouin à l'ouest du canyon
            }
        } // Une vingtaine de babouins sont repartis sur les deux cotes du canyon
    }
}

class Corde {

    private volatile int nbBabouinsSurCorde=5;
	private volatile Cote Cote;

    public synchronized void saisir(Cote origine){

        try {
            while(!(nbBabouinsSurCorde==5) &&(nbBabouinsSurCorde ==0 ||Cote!=origine)){
                wait();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Cote=origine;
        nbBabouinsSurCorde--;
    }

    public synchronized void lacher(Cote origine){
        nbBabouinsSurCorde++;
        notifyAll();
    }
}
