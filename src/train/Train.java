package train;


import java.util.Random;
import java.util.concurrent.*;
import client.Chauffeur;
import client.Voyageur;
import main.TrainFrame;
import trainstation.Gare;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.*;

public class Train {


	/**
	 * Veuillez créer vos fonctions/ modifications  en bas du fichier svp
	 * Donner des ï¿½tats aux trains afin de mieux gerer l'exclusion mutuelle
	 */

	private static final int ETAT_EN_GARE = 1;
	static final int ETAT_EN_MARCHE = 2;
	static final int ETAT_SORTIE_DE_GARE = 3;
	static final int ETAT_ENTREE_EN_GARE = 4;
	static final int ETAT_ENTREE_EN_PANNE = 5;
	static final int ETAT_ENTREE_EN_GREVE = 6;
	
	/**
	 * Creer train
	 * Avec un chauffeur, une capacite et une liste de passagers
	 *  & **NOUVEAU** un etat
	 */
	Gare gareDeTrain;
	private int type;
	private String Destination;
	private Chauffeur chauffeur;

	private static String nomTrain;
	private static int id;
	static final int capacite = 25;
	//etat du train
	int etat;
		
		
	//Passager dans le train
	Voyageur[] passagers = new Voyageur[capacite];
	//Ajout de quais pour pouvoir positionner des passager
	Voyageur[] passagerQuais;

	/**
	 * Semaphore represantant les passagers et le chauffeur
	 * exclusion mutuelle pour gerer les places
	 */
	private static Semaphore mutexTrain = new Semaphore(1);
	private static Semaphore[] semaphoreTrain = new Semaphore[capacite];
	private static Semaphore accessTrain = new Semaphore(capacite);
	private static Semaphore semaphoreChauffeur = new Semaphore(1);

	JLabel label;
	JTextArea textArea;

	/**
	 * Gestion des passagers dans le trains
	 */

	public static  int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	/**
	 * Initialisation d'un Train et des semaphores representant les passagers
	 * @param nomTrain
	 * @param id
	 */
	public Train(String  nomTrain, int id, int etat) {
		this.nomTrain = nomTrain;
		this.id = id;
        gareDeTrain = Gare.getInstance();
        label = TrainFrame.getInstance().trains(nomTrain);
		setState(etat);
	}
	
	 @Override
	    public void finalize() {
		 	TrainFrame.getInstance().delete(label);
	    }   
	
	/**
	 * fonction qui fait entree un passager dans le train 
	 * On utilise accesTrain pour faire savoir le nombre de personne 
	 * qui pourront acceder le train
	 * On utilise l'exclusion mutuelle sur la section critique pour
	 * s'assurer que l'on a des siege disponible et on affecte le passagers
	 * @param v
	 * @throws InterruptedException
	 */
	public void entree(Voyageur v) throws InterruptedException {
		accessTrain.acquire();
		mutexTrain.acquire();
		int idSiege = placeDisponible();
		passagers[idSiege] = v;
		mutexTrain.release();
	}
	/**
	 * Permet de trouver les sieges disponible
	 * @return
	 */
	public int placeDisponible() {
		for(int i = 0; i<capacite; i++) {
			if(passagers[i] == null) {
				return i;
			}
		}
		return 0;
	}
	/**
	 * On fait quitter le passager et
	 * On libere le siege qu'il occupe
	 * @param v
	 * @throws InterruptedException
	 */
	public void quitterTrain(Voyageur v) throws InterruptedException{
		int idSiege = sortirVoyageur(v);
		semaphoreTrain[idSiege].acquire();
		passagers[idSiege] = null;
		semaphoreTrain[idSiege].release();
		accessTrain.release();
	}

	/**
	 * Verifier si le passager est dans le train
	 * @param v
	 * @return
	 */
	public int sortirVoyageur(Voyageur v) {
		for(int i = 0; i<capacite; i++) {
			if(v.equals(passagers[i])) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Cette fonction affecte un chauffeur au train 
	 * et s'attribue la ressource jusqu'a ce que le chauffeur finisse
	 */
	public void prendreDuService(Chauffeur c){
		this.chauffeur = c;
		try {
			semaphoreChauffeur.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * On verifie si le chauffeur est bien celui qui conduit le train
	 * Si oui on arrete son service et on relache la ressource
	 * @param c
	 */
	public void serviceTerminer(Chauffeur c) {
		if(c.equals(this.chauffeur)) {
			this.chauffeur = null;
		}
		semaphoreChauffeur.release();
	}
	public Chauffeur getChauffeur() {
		return chauffeur;
	}

	public void setChauffeur(Chauffeur chauffeur) {
		this.chauffeur = chauffeur;
	}

	public int getCapacite() {
		return capacite;
	}
	public static String  getnomTrain() {
		return nomTrain;
	}


	public static int getEtatEnGare() {
		return ETAT_EN_GARE;
	}

	/**
	 * Permet de fixer la taille du quais que l'on pourra utiliser dans la classe quais
	 * @param i
	 */

	public void setSizeQuais(int i) {
		passagerQuais = new Voyageur[i];
	}

	/**
	 * Get Taille du quais 
	 * @return
	 */
	public int getSizeQuais() {
		return passagerQuais.length;
	}

	/**
	 * On retourne la liste des passager sur le quais
	 * @return
	 */
	public Voyageur[] getPassagerQuais() {
		return passagerQuais;
	}

	/**
	 * On ajoute des passager sur le quais
	 * @param i
	 * @param v
	 */
	public void ajouterAuQuais(int i, Voyageur v) {
		passagerQuais[i] = v;
	}

	public Voyageur getVoyageurTrain(Voyageur[] vs, int i) {
		return vs[i];
	}

	public void setVoyageurTrainNull(Voyageur[] vs, int i) {
		vs[i] = null;
	}
	/***
	 * getteur et seteur de l'etat
	 */
	public int getState()
	{
		return etat;
	}

	public void setState(int nouvelEtat)
	{
		etat = nouvelEtat;
		setImage(etat);
		switch (etat)
		{
		case ETAT_EN_GARE:  break;
		case ETAT_EN_MARCHE:  
			//rotation signifie que le train est en marche sur des rail a lexterieur de la gare 
			rotationTrain();
			break;
		case ETAT_SORTIE_DE_GARE:  break;
		case ETAT_ENTREE_EN_GARE:  break;                
		}        
	}  

	public static int getEtatEnRotation() {
		// TODO Auto-generated method stub
		return ETAT_EN_MARCHE;
	}
	
	/**
	 * list des fonctions detats
	 */
	public void trainEnRotationEnMarche() {
		// TODO Auto-generated method stub
		etat = ETAT_EN_MARCHE;
		rotationTrain(); //ok
	}
	public void surLeQuai() {
		// TODO Auto-generated method stub
		etat = ETAT_EN_GARE;
		trainSurQuai(); //ok
	}
	public void entreeEnGare() {
		// TODO Auto-generated method stub
		etat = ETAT_ENTREE_EN_GARE;
		entreeEnGareDuTrain();
	}


	public void sortiedeGare() throws InterruptedException {
		// TODO Auto-generated method stub
		etat = ETAT_SORTIE_DE_GARE;
		sortieDeGareTrain(); //ok
	}
	

	/**
	 * Les actions relatives aux etats et changement detat sont a ecrire ci apres
	 * 
	 */
	private void sortieDeGareTrain() throws InterruptedException{
		// TODO Auto-generated method stub
		Train thisTrain = this;
		Thread thread = new Thread()
		{
			public void run()
			{                            
				int QuaisId = 0;
			
				try {
					QuaisId = gareDeTrain.voies.occuperVoies(thisTrain);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Point pd = getPosition();
				Point pf = gareDeTrain.voies.getVoiesDebut(QuaisId);
				
				while(true) {
					if ((pd.x == pf.x)&&(pd.y == pf.y)) break;                    
                    pd = getNextPoint(pd, pf);
                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
                    setPosition(pd);
				}
				
				setImage(ETAT_SORTIE_DE_GARE);
				setPosition(gareDeTrain.voies.getVoiesDebut(QuaisId));
	            pd = getPosition();
	            pf = gareDeTrain.voies.getVoiesFin(QuaisId);
				
	            while (true)
                {
                    if ((pd.x == pf.x)&&(pd.y == pf.y)) break;
                    
                    pd = getNextPoint(pd, pf);

                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
                    setPosition(pd);
                }
	            
	            try {
					gareDeTrain.voies.libereVoies(thisTrain);
					gareDeTrain.retirerTrain(thisTrain);
	            }
	            catch (InterruptedException e)
                {
	                // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}                       
		};
		thread.start();
		
	}
	
	private void entreeEnGareDuTrain() {
		Train thisTrain = this;
		Thread thread = new Thread() {
			public void run() {
				int voiesId = 0;
				try {
					voiesId = gareDeTrain.voies.occuperVoies(thisTrain);
				}catch (InterruptedException e){
					e.printStackTrace();
				}
				
				setImage(ETAT_ENTREE_EN_GARE);
				setPosition(gareDeTrain.voies.getVoiesDebut(voiesId));
	            Point pf = getPosition();
	            Point pd = gareDeTrain.voies.getVoiesFin(voiesId);
	                
	             while (true)
	                {
	                    if ((pd.x == pf.x)&&(pd.y == pf.y)) break;
	                    
	                    pd = getNextPoint(pd, pf);

	                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
	                    setPosition(pd);
	                }
	             
				try {
					gareDeTrain.aiguilleur.demandeEntreeSurQuai(thisTrain);
				}catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
				
				pd = getPosition();
				pf = gareDeTrain.aiguilleur.getTrainPoint(thisTrain);
				while (true)
                {
                    if ((pd.x == pf.x)&&(pd.y == pf.y)) break;                    
                    pd = getNextPoint(pd, pf);
                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
                    setPosition(pd);
                }
				setImage(ETAT_EN_GARE);
				try {
				gareDeTrain.voies.libereVoies(thisTrain);
				
				}catch (InterruptedException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
			}
		};
		thread.start();
	}

	private void trainSurQuai() {
		// TODO Auto-generated method stub
		/**
		 * quand le train sarrete sur le quai final
		 */
		Train locomotiveX = this;
		
		Thread thread = new Thread()
		{
			public void run()
			{                   
				try {
					gareDeTrain.aiguilleur.demandeEntreeSurQuai(locomotiveX);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				};
				JLabel test = new JLabel("depart");
				test.setBounds(1700, 42, 56, 16);

                TrainFrame.getInstance().panel.add(test);
               
				Point pd =  test.getBounds().getLocation();
				Point pf = gareDeTrain.aiguilleur.getTrainPoint(locomotiveX);
				
				while (true)
				{
					if ((pd.x == pf.x)&&(pd.y == pf.y)) break;                    
					pd = getNextPoint(pd, pf);
                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
                    setPosition(pd);
				}
				setImage(ETAT_EN_GARE);
			}                       
		};
		thread.start();

	}
	private void rotationTrain() {
		// TODO Auto-generated method stub
		/**
		 * 
		 */	
		Thread thread = new Thread()
		{
			public void run()
			{     
				setImage(ETAT_EN_MARCHE);
				Rectangle r = TrainFrame.getInstance().getBounds();               
                //Point pd = makePoint(1000,1000);
                //Point pf = makePoint(1000,1000);
				
				Point pd = makePoint(0,0);
	            Point pf = makePoint(0,0);
                Point p=pd;              
				while(true) {
					if(etat != ETAT_EN_MARCHE) break;
					 
					//if ((p.x == pf.y)&&(p.y == pf.x)){ 
					if ((p.x == pf.x)&&(p.y == pf.y)){ 
	                       pd = makePoint(-150, -150);
	                       pf = makePoint(-150, -150);
	                       p=pd;
	                      }                      
	                    
	                    p = getNextPoint(p, pf);

                    try  {  Thread.sleep(3);  }   catch (InterruptedException e)  {  }
                    setPosition(p);
				}
			}                       
		};
		thread.start();
	}
	
	
	private Point getPosition(){
		Point p = new Point();
		p = label.getBounds().getLocation();
		return p;
	}
	
	private Point getNextPoint(Point p, Point pf)
	    {
	        if (p.x < pf.x) p.x++;
	        if (p.x > pf.x) p.x--;
	        if (p.y < pf.y) p.y++;
	        if (p.y > pf.y) p.y--;
	        return p;
	    }  
	
	private void setImage(int state) {
		String imageTrain = "/resources/silverTrain.png";
		switch(state) {
			case ETAT_EN_GARE:
				imageTrain = "/resources/RedTrain.png";
				TrainFrame.getInstance().panel.setComponentZOrder(label, 1);
				label.setIcon(new ImageIcon(TrainFrame.class.getResource(imageTrain)));
				break;
			case ETAT_EN_MARCHE:
				imageTrain = "/resources/TrainOnRails.png";
				TrainFrame.getInstance().panel.setComponentZOrder(label, 0);
				//label.setVisible(false);
				label.setIcon(new ImageIcon(TrainFrame.class.getResource(imageTrain)));
				break;
			case ETAT_SORTIE_DE_GARE:
				imageTrain = "/resources/TrainOnRails2.png";
				TrainFrame.getInstance().panel.setComponentZOrder(label, 1);
				label.setIcon(new ImageIcon(TrainFrame.class.getResource(imageTrain)));
				break;
			case ETAT_ENTREE_EN_GARE:
				imageTrain = "/resources/TrainEntrance.png";
				TrainFrame.getInstance().panel.setComponentZOrder(label, 1);
				label.setIcon(new ImageIcon(TrainFrame.class.getResource(imageTrain)));
				break;
		}
		
	}
	
    private void setPosition(Point p)
    {
        label.setBounds(p.x, p.y, (int) label.getBounds().getWidth(), (int) label.getBounds().getHeight());
    }
    
    
    private Point makePoint(int x, int y)
    {
        Point p = new Point();
        p.x = x;
        p.y = y;
        return p;
    } 
    
    private int getRandom(int min, int max)
    {
      Random r = new Random();
      return  min + r.nextInt(max-min);
    } 
    
  //--------------------------------------------------------------------------------    
    public void showtrain()
    {
        label.setVisible(true);
    }

    //--------------------------------------------------------------------------------    
    public void hidetrain()
    {
        label.setVisible(false);
    }
    
  //--------------------------------------------------------------------------------    
    public void animerQuitterQuais(int statno)
    {
      Point p = gareDeTrain.aiguilleur.getTrainPoint(this); 
      p.y = p.y-200;   
      setPosition(p);
    }

}

