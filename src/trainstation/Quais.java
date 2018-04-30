package trainstation;

import java.util.ArrayList;

import java.util.concurrent.*;

import client.Voyageur;
import main.TrainFrame;

import java.util.List;
import train.*;

import java.awt.*;

import javax.swing.*;

public class Quais {
	/**
	 * GESTION DE L'ENTR�E EN GARE DES TRAINS  "sur les QUAIS"
	 * instancier les trains 
	 * gestion des semaphores
	 * exclusion mutuelles sur les regions critiques
	 */
	static final int NOMBRE_DE_QUAIS = 10;
	public Train[] locomotive = new Train[NOMBRE_DE_QUAIS];
	private static Semaphore mutexQuais = new Semaphore(1);
	//	creation tableau de semaphore pour les 10 quais
	private static Semaphore[] semaphoreQuais = new Semaphore[NOMBRE_DE_QUAIS];
	private static Semaphore entreeQuai = new Semaphore(NOMBRE_DE_QUAIS);
	
	JLabel[] labels = new JLabel[NOMBRE_DE_QUAIS];
	
	/**
	 * Ajout de passager sur le quais,
	 * Chaque quais a une capacite fixer
	 */
	 private static int CAPACITE = 35;
	 Voyageur[] passagers = new Voyageur[CAPACITE];
	 
	 private static Semaphore mutexPassager = new Semaphore(1);
	 private static Semaphore[] semaphorePassager = new Semaphore[CAPACITE];
	 private static Semaphore accessPassager = new Semaphore(CAPACITE);
	 
	/**
	 * instanciation d'un tableau de semaphore
	 */

	public Quais ()
	{
		for(int i=0; i<NOMBRE_DE_QUAIS; i++) {
			locomotive[i] =null;
		}

		for(int i=0; i<NOMBRE_DE_QUAIS; i++) {
			semaphoreQuais[i] = new Semaphore(1);
		}
		
		for(int i=0; i<NOMBRE_DE_QUAIS; i++) {
			labels[i] = TrainFrame.getInstance().createStationnementLabel("Quais "+i, pointQuais(i));
		}
	}	

	/**
	 * demander l'autorisation d'entrer dans un quai si l'un des 5 est disponible
	 * l'entree sur le quai se fait avec un semaphore
	 * La recherche d'un quai de libre se fait en exclusion mutuelle (ressource partag�e)
	 * @param Train train
	 * @throws InterruptedException
	 */
	/*public void demande_affectation_Quais (Train train) throws InterruptedException
	{
		entreeQuai.acquire();
		//rechercher un quai de disponible et de libre en exclusion mutuelle 
		mutexQuais.acquire();
		int numeroQuai = rechercheQuaiDispo();
		locomotive[numeroQuai] = train;
		train.setSizeQuais(CAPACITE);
		mutexQuais.release();
	}
	*/
	public void demandeEntreeSurQuai(Train locomotiveRecue) throws InterruptedException {
		// TODO Auto-generated method stub
		entreeQuai.acquire();
		mutexQuais.acquire();
		int numeroQuai = getQuaiDispo();
		locomotive[numeroQuai]= locomotiveRecue;
		mutexQuais.release();
		
	}

	private int rechercheQuaiDispo() {
		// TODO Auto-generated method stub
		for(int j =0; j< NOMBRE_DE_QUAIS; j++) {
			if(locomotive[j]== null)
				return j;
		}
		return 0;
	}
	/***
	 * Quitter le quai et rendre une place disponible 
	 * @param Train train aussi
	 * @throws InterruptedException
	 *  RechercheNumerodeQuai ()
	 *  release du semaphore dentree sur le quai
	 */
	public void quitterQuais (Train train) throws InterruptedException {

		int numeroQuai = rechercheQuaiAliberer(train);

		semaphoreQuais[numeroQuai].acquire();
		train.animerQuitterQuais(numeroQuai);
		// aucun train n'occupe ce quai anymore
		locomotive[numeroQuai] = null;
		semaphoreQuais[numeroQuai].release();

		entreeQuai.release();
	}

	/**
	 * rechercher le quai a liberer 
	 * @param train
	 * @return
	 */
	private int rechercheQuaiAliberer(Train train) {
		// TODO Auto-generated method stub
		for(int k = 0; k<NOMBRE_DE_QUAIS;k++) {
			if(train.equals(locomotive[k]))
				return k;
		}
		return 0;
	}
	
	public void rechercheQuaisLibre(Voyageur v, int idQuais) throws InterruptedException{
		//Ajouter un passager au semaphore 
		accessPassager.acquire();
		//On monopoliser la ressource
		mutexPassager.acquire();
		//On cherche un quai libre
		int quaiId = getQuais();
		//On cherche une place disponible sur le quai
		int placeId = ajouterPassagerAuQuai(
				locomotive[quaiId].getPassagerQuais());
		//On ajoute le voyageur sur le quai
		locomotive[quaiId].ajouterAuQuais(placeId, v);
		//Et on relache la ressource
		mutexPassager.release();
	}
	/**
	 * Cette fonction nous permet de rechercher un Quais dans lequel se trouve un train
	 * Et dont le quai n'est pas plein
	 * @return
	 */
	public int getQuais() {
		for(int i=0; i<NOMBRE_DE_QUAIS;i++) {
			//Chercher un quai sur le train il existe deja et le quai n'est pas plein
			if(locomotive[i]!=null) {
				//On retourne l'id du quai
				return i;
			}
		}
		return 0;
	}
	/**
	 * On cherche une place disponible sur le quais ou on peut ajouter un passager 
	 * @param vs
	 * @return
	 */
	public int ajouterPassagerAuQuai(Voyageur[] vs) {
		//Cherche une place disponible sur le quai ou on peut placer le passager
		for(int i=0; i<vs.length; i++) {
			if(vs[i]==null) {
				//On retourne l'id de la place trouver
				return i;
			}
		}
		return 0;
	}
	
	/**
	 * Le passager quitte le quais et va dans le train
	 * @param v
	 * @throws InterruptedException
	 */
	public void passageentreTrain(Voyageur v) throws InterruptedException {
		//On cherche la position du voyageur
		int idVoyageur = rechercherVoyageur(v);
		//Elle la meme que dans le semaphore 
		semaphorePassager[idVoyageur].acquire();
		//On recupere aussi le quai sur lequel se trouve le passager
		//Pour pouvoir le retirer du bon quais
		int idQuais = rechercherQuaisVoyageur(v);
		//On retire ensuite le voyageur du quai 
		locomotive[idQuais].setVoyageurTrainNull(locomotive[idQuais].getPassagerQuais(),idVoyageur);
		semaphorePassager[idVoyageur].release();
		
		accessPassager.release();
		
	}
	
	/**
	 * Cette fonction nous permets de recuperer le quais sur lequel se trouve le passager que l'on cherche
	 * @param v
	 * @return
	 */
	public int rechercherQuaisVoyageur(Voyageur v) {
		for(int i=0; i<NOMBRE_DE_QUAIS; i++) {
			//Si le quai existe
			if(locomotive[i]!=null) {
				//On recherche sur le quais 
				for(int j=0; j<locomotive[i].getSizeQuais(); j++) {
					//Si on trouve le voyageur
					if(v.equals(locomotive[i].getVoyageurTrain(locomotive[i].getPassagerQuais(),j))) {
						//On retourne le quai sur lequel il se trouve
						return i;
					}
					
				}
			}
		}
		return 0;
	}
	
	/**
	 * On retourne l'id de la place dans laquelle se trouve le passager sur le quai
	 * @param v
	 * @return
	 */
	public int rechercherVoyageur(Voyageur v) {
		for(int i=0; i<NOMBRE_DE_QUAIS; i++) {
			//Si le quai existe 
			if(locomotive[i]!=null) {
				//Acceder au nombre de passager existant sur le quais
				for(int j=0; j<locomotive[i].getSizeQuais(); j++) {
					//Verifier si le passager ce trouve sur le quai
					if(v.equals(locomotive[i].getVoyageurTrain(locomotive[i].getPassagerQuais(),j))) {
						//Retourner la position sur le quai qui correspondra aussi a celui du semaphore
						return j;
					}
				}
			}
		}
		return 0;
	}
	
	public int getCapacite() {
		return CAPACITE;
	}


	/**
	 * methode donant les quais ou le train pourra se mettre donc dispo
	 * @return
	 */

	private int getQuaiDispo() {
		// TODO Auto-generated method stub
		for(int k =0; k< NOMBRE_DE_QUAIS ;k++) {
			if(locomotive[k] == null)
				return k;
		}
		return 0;
	}
	
	private Point pointQuais(int QuaisId) {
		Point point = new Point();
		Rectangle rectangle = TrainFrame.getInstance().getBounds();
		point.x = 40 + QuaisId*160;
		point.y = rectangle.height-900;
		return point;
	}
	
	public Point getTrainPoint(Train t) {
		int trainId = rechercheQuaiAliberer(t);
		return pointQuais(trainId);
	}
	
	public Train getTrainOnQuais() {
		for(int i=0; i< NOMBRE_DE_QUAIS; i++) {
			if(locomotive[i]!=null) {
				return locomotive[i];
			}
		}
		return null;
	}
}