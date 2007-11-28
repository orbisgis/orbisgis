package org.example5;

import org.sif.UIFactory;

public class Main {
	public static void main(String[] args) {
		MyUIClass myUIClass = new MyUIClass();

		boolean answer = UIFactory.showDialog(myUIClass);
		
		if (answer) {
			System.out.println("you pressed ok and select "
					+ myUIClass.myOwnGetSelectionMethod());
		} else {
			System.out.println("you did not pressed ok");
		}
	}
}
