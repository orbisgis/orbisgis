package org.orbisgis.plugins.core.ui.workbench;

import java.awt.event.MouseEvent;
import java.util.Observable;

import org.orbisgis.plugins.core.ui.PlugInContext;

public abstract class WorkbenchContext extends Observable {

	private String lastAction;

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
		setChanged();
		notifyObservers(lastAction);
		System.out
				.println(" Event : "
						+ lastAction
						+ ", WorkbenchContext update finished--------------------------");
	}

	public void setRowSelected(MouseEvent e) {
		setChanged();
		notifyObservers(e);
		System.out
				.println("MouseEvent : on "
						+ e.getSource()
						+ ", WorkbenchContext update finished--------------------------");

	}

	public OrbisWorkbench getWorkbench() {
		return null;
	}

	public PlugInContext createPlugInContext() {
		return new PlugInContext(this);
	}

	public void setHeaderSelected(int selectedColumn) {
		notifyObservers(selectedColumn);
		System.out
				.println("------------------------------WorkbenchContext update finished--------------------------");
		setChanged();

	}
}
