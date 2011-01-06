package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import org.orbisgis.core.Services;
import org.orbisgis.core.workspace.DefaultWorkspace;

public class PeriodicSaveWorkspace  extends Thread{
	
	DefaultWorkspace workpsace;
	
	public long periodicTimeToSaveWrksp;
	private boolean stopSaving = false;

	public void setPeriodicTimeToSaveWrksp(long periodicTimeToSaveWrksp) {
		this.periodicTimeToSaveWrksp = periodicTimeToSaveWrksp;
	}
	
	public PeriodicSaveWorkspace(DefaultWorkspace workpsace){
		this.workpsace = workpsace;
	}
	
	public void run() {		
		try{
			while(!stopSaving){
				workpsace.saveWorkspace();
				Thread.sleep(periodicTimeToSaveWrksp);				
			}			
		}catch(InterruptedException e){
			
		} catch (Exception e) {
			Services.getErrorManager().error(
					"Error while saving workspace", e);
		}
		stopSaving = false;
	}
	
	
	
	public synchronized void stopSaving() {
		stopSaving = true;
	}
	

}
