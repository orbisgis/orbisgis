package org.example2;

import org.sif.UIFactory;

public class Main {
	public static void main(String[] args) {
		boolean answer = UIFactory.showDialog(new MyUIClass());
		if (answer) {
			System.out.println("you pressed ok");
		} else {
			System.out.println("you did not pressed ok");
		}
	}
}
