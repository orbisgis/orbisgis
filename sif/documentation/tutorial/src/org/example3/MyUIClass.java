package org.example3;

import java.awt.Component;
import java.net.URL;

import org.sif.UIPanel;

public class MyUIClass implements UIPanel {
	MyJPanel myJPanel;

	public Component getComponent() {
		if (null == myJPanel) {
			myJPanel = new MyJPanel();
		}
		return myJPanel;
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public String initialize() {
		// TODO Auto-generated method stub
		return null;
	}

	public String validate() {
		return (null == getSelection()) ? "enter a not null value !" : null;
	}

	public String getSelection() {
		return ((MyJPanel) getComponent()).getSelection();
	}

	public String getInfoText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String validateInput() {
		// TODO Auto-generated method stub
		return null;
	}

}
