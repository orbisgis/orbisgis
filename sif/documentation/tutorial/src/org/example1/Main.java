package org.example1;

import org.sif.UIFactory;

public class Main {
	public static void main(String[] args) {
		UIFactory.showDialog(new MyUIClass());
		// SIFDialog sifDialog = UIFactory.getSimpleDialog(new MyUIClass());
		// sifDialog.pack();
		// sifDialog.setVisible(true);
	}
}
