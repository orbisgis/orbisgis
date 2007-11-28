package org.sif;

import javax.swing.JDialog;

public class TestOnTheFlyUIPanel {

	public static void main(String[] args) {
		DynamicUIPanel panel = UIFactory.getDynamicUIPanel("Automaticman!",
				null, new String[] { "host", "port" }, new int[] {
						SQLUIPanel.STRING, SQLUIPanel.DOUBLE },
				new String[] { "port > 12" },
				new String[] { "port must be > 12" });
		JDialog dlg = UIFactory.getSimpleDialog(panel);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);

		System.out.println(panel.getValue("host"));
		System.out.println(panel.getValue("port"));
	}
}
