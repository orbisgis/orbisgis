package org.example7;

import org.sif.DynamicUIPanel;
import org.sif.UIFactory;

public class Main {
	public static void main(String[] args) {
		DynamicUIPanel myDynamicUIPanel = UIFactory.getDynamicUIPanel(
				"my 1st dynamic panel", null, new String[] { "my 1st field",
						"my 2nd field" });
		UIFactory.showDialog(myDynamicUIPanel);
		for (String item : myDynamicUIPanel.getValues()) {
			System.out.println("you have enter : " + item);
		}
	}
}
