package sip;

import java.util.Observable;


public class ObservableCallStatus extends Observable{
	private boolean callStatus = false;
	
	public void setStatus(boolean status){
		this.callStatus = status;
		setChanged();
		if(this.callStatus){
			notifyObservers();
		}
	}
	
	public boolean getStatus(){
		return callStatus;
	}
}
