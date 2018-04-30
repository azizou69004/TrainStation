package main;

import trainstation.Gare;
public class Main {

	public static void main(String args[]) throws InterruptedException{

		/**
		 * TODO 
		 * 1- ajout du FRAME CLASS gare de LYON 
		 * 2- instanciation GARE
		 */
		TrainFrame.getInstance().setVisible(true);
		try {
			Gare.getInstance().initGare();
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

}
