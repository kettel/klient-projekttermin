package sip;

import java.util.Observable;


public class ObservableCallStatus extends Observable{
	private boolean callStatus = false;

	public ObservableCallStatus(){}

	public void setStatus(boolean status){
		callStatus = status;
		setChanged();
		notifyObservers();
	}

	public boolean getStatus(){
		return callStatus;
	}
}
