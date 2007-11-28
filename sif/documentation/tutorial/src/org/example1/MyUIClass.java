package org.example1;

import java.awt.Component;
import java.net.URL;

import org.sif.UIPanel;

public class MyUIClass implements UIPanel {
	public Component getComponent() {
		return new MyJPanel();
	}

	public URL getIconURL() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public String validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
