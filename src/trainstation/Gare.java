package trainstation;

import java.util.ArrayList;
import java.util.concurrent.*;

import client.Voyageur;

import java.util.List;
import train.*;

public class Gare {
	Semaphore sem;
	String processName;
	private int stationID;
	private int capacite;
	private String stationName;
	private int id;
	public Quais aiguilleur;
	public Voies voies;
	public ReseauFerroviere reseauFerroviere;

	//utilisation d'un singleton
	private static Gare instance=null ;    

	public static Gare getInstance()
	{
		if (instance == null) 
			instance = new Gare();
		return instance;
	}

	public ArrayList<Voyageur> passagers = new ArrayList<Voyageur>();
	public ArrayList<Train> trains = new ArrayList<Train>();


	private Gare()
	{
		aiguilleur = new Quais();
		voies = new Voies();
		reseauFerroviere = new ReseauFerroviere();
	}

	public void  initGare() throws InterruptedException
	{
		/***
		 * initialiser des trains en gare a lexecution du programme
		 * initialiser des trains qui demandent a entrer en gare
		 * initilisaton de personnes sur les quais PARTIE EDDY
		 */
		//		intialisation de 5 trains en gare
		//TrainFrame.getInstance().quaisTextArea.append("INITIALISATION DE 5 TRAINS EN GARE\n");
		for(int k=0 ; k<5;k++)
			nouveauTrainEnGare();
		//		intialisation de 15 trains en rotation

		//TrainFrame.getInstance().rotationTextArea.append("INITIALISATION DE 5 TRAINS EN ROTATION\n");
		for(int j=0 ; j<7;j++)
			nouveauTrainEnRotation();

	}

    public void retirerTrain(Train trainRecu)
    {
        for (int i=0; i < trains.size()-1; i++ )
            if (trainRecu.equals(trains.get(i)))
            {
            	trains.get(i).finalize(); 
            	trains.remove(i);                
            } 
    }   
    
    public Train ajouterTrain(int etat) throws InterruptedException{
    	String nomTrain = "TGV " + trains.size()+" ";
		Train train = new Train(nomTrain, Train.getId(), etat);
		if(etat == Train.getEtatEnGare()) {
			aiguilleur.demandeEntreeSurQuai(train);
		}
		trains.add(train);
		return train;
    }
    
    public Train nouveauTrainEnRotation() throws InterruptedException{
		// TODO Auto-generated method stub
		Train train = creerTrainGeneral(Train.getEtatEnRotation());
		//TrainFrame.getInstance().rotationTextArea.append(train.getId()+"Ajouter a la rotation\n");
		reseauFerroviere.addTrain(train);
		//train.trainEnRotationEnMarche();
		return train;

	}

    public Train  nouveauTrainEnGare() throws InterruptedException{
		// TODO Auto-generated method stub
		Train train = creerTrainGeneral(Train.getEtatEnGare());
		//TrainFrame.getInstance().quaisTextArea.append(train.getId()+"Ajouter au Quais\n");
		train.surLeQuai();
		return train;
	}

    private Train creerTrainGeneral(int etat) {
		// TODO Auto-generated method stub
		String nomTrain = "TGV ";
		Train train = new Train(nomTrain, Train.getId(), etat);
		trains.add(train);
		return train;

	}
    
    public void quitterGare() throws InterruptedException{
        Train train = aiguilleur.getTrainOnQuais();
        if(train!=null) {
            aiguilleur.quitterQuais(train);
            train.sortiedeGare();
        }
    }
     
    public void entreeGare() throws InterruptedException{
        Train train = reseauFerroviere.getTrain();
        if(train!=null) {
            reseauFerroviere.removeTrain(train);
            train.entreeEnGare();
        }
    }
    
	public int getStationID() {
		return stationID;
	}

	public void setStationID(int stationID) {
		this.stationID = stationID;
	}


	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}


	public Gare(Semaphore sem, String processName) {
		this.sem = sem;
		this.processName = processName;
	}


	public List<Train> getTrains() {
		return trains;
	}

	public void setTrains(List<Train> trains) {
		this.trains = (ArrayList<Train>) trains;
	}

	public int getCapacite() {
		return capacite;
	}

	public void setCapacite(int capacite) {
		this.capacite = capacite;
	}

	public void nouveauPassager() {
		Voyageur passager = new Voyageur(id);
		passagers.add(passager);
		id++;
	}
	
	public void entreePassager() {
		if(!passagers.isEmpty()) {
			passagers.remove(0);
		}
	}

}