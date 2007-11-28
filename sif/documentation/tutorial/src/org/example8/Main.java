package org.example8;

import org.sif.DynamicUIPanel;
import org.sif.SQLUIPanel;
import org.sif.UIFactory;

public class Main {
	public static void main(String[] args) {
		DynamicUIPanel myDynamicUIPanel = UIFactory.getDynamicUIPanel(
				"org.example8.myId", "my 1st dynamic panel", null,
				new String[] { "my_String_Field", "myDoublefield" }, new int[] {
						SQLUIPanel.STRING, SQLUIPanel.DOUBLE }, new String[] {
						"my_String_Field LIKE 'abc%'", "myDoublefield > 12" },
				new String[] { "My String field must start with 'abc'",
						"My Double field must be greater than 12 !" });
		UIFactory.showDialog(myDynamicUIPanel);
		for (String item : myDynamicUIPanel.getValues()) {
			System.out.println("you have enter : " + item);
		}
	}
}
