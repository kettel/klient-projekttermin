package sip;

import java.util.Observable;


public class ObservableCallStatus extends Observable{
	private boolean callStatus = false;
	
	public ObservableCallStatus(){}
	
	public void setStatus(boolean status){
		if(callStatus != status) {
			callStatus = status;
			setChanged();
			notifyObservers();
		}
	}
	
	public boolean getStatus(){
		return callStatus;
	}
}
