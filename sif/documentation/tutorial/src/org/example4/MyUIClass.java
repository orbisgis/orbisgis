package org.example4;

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
		return "4th example - next example is persistance in UI";
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public String validate() {
		return (null == getSelection()) ? "enter a non null value !" : null;
	}

	public String getSelection() {
		return ((MyJPanel) getComponent()).getSelection();
	}

}
