// -*- coding: utf-8 -*-

import java.util.ArrayList;
import static java.lang.Thread.sleep;

public class SeptNains {
    public static void main(String[] args) {
        int nbNains = 7;
        String nom [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux", "Prof", "Timide"};
        Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++){
			nain[i] = new Nain(nom[i]);
		}
        for(int i = 0; i < nbNains; i++){
			nain[i].start();
		}
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        for(int i = 0; i < nbNains; i++) {
            System.out.println(nom[i] + " s'interrompt");
            nain[i].interrupt();
        }
		for(int i = 0; i < nbNains; i++) {
            try { 
				nain[i].join(); 
			}catch (InterruptedException e){
				e.printStackTrace();
			}	
        }
        System.out.println("Programme fini !");        
    }
}    

class BlancheNeige {
     // Initialement, Blanche-Neige est libre.
    private ArrayList<Thread> liste = new ArrayList<>();

    public synchronized void requerir () {
        //System.out.println("\t" + Thread.currentThread().getName() + " veut un acces exclusif a la ressource");
        for(Thread t:liste){
            System.out.print(t.getName()+" ");
        }
        System.out.println("");
        
        liste.add(Thread.currentThread());
    }

    public synchronized void acceder () throws InterruptedException {
        while( liste.get(0) != Thread.currentThread() ) { // Le nain s'endort sur l'objet bn
            wait();
        }
        //System.out.println("\t\t" + Thread.currentThread().getName()+ " va acceder a la ressource.");
    }

    public synchronized void relâcher () {
        liste.remove(0);
        //System.out.println("\t\t\t" + Thread.currentThread().getName()+ " relâche la ressource.");
        notifyAll();
    }
}

class Nain extends Thread {
    private static final BlancheNeige bn = new BlancheNeige();
    public Nain(String nom) {
        this.setName(nom);
    }
    public void run() {
        while(true) {
            bn.requerir();
			try {
				bn.acceder();
				System.out.println("\t\t" + getName() + " possede le privilege d'acces a Blanche-Neige.");
				sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(getName() + " a termine !");
				return;
			}

            bn.relâcher();

        }
    }	
}

/*
  $ make
  $ $ java SeptNains
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
	Grincheux veut un acces exclusif a la ressource
	Timide veut un acces exclusif a la ressource
	Joyeux veut un acces exclusif a la ressource
	Atchoum veut un acces exclusif a la ressource
	Dormeur veut un acces exclusif a la ressource
	Prof veut un acces exclusif a la ressource
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
			Simplet relâche la ressource.
	Simplet veut un acces exclusif a la ressource
		Simplet va acceder a la ressource.
		Simplet possede le privilege d'acces a Blanche-Neige.
        ...
*/
