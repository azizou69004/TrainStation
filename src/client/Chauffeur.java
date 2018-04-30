package client;
import java.util.concurrent.*;

public class Chauffeur extends Personnes{
	
	private boolean isOnStrike;
	Semaphore sem;
	String processName;
	
	public Chauffeur(Semaphore sem, String processName){
		this.sem = sem;
		this.processName = processName;
		
	}

	
//	etat
	
	public boolean isOnStrike() {
		return isOnStrike;
	}
	public void setOnStrike(boolean isOnStrike) {
		this.isOnStrike = isOnStrike;
	}

	
}
