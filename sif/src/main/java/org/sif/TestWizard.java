package org.sif;

import javax.swing.JDialog;

public class TestWizard {

	public static void main(String[] args) {
		DynamicUIPanel[] panel = new DynamicUIPanel[3];
		panel[0] = UIFactory.getDynamicUIPanel("Connection", null,
				new String[] { "host", "port" }, new int[] { SQLUIPanel.STRING,
						SQLUIPanel.INT }, null, null);
		panel[1] = UIFactory.getDynamicUIPanel("Database", null, new String[] {
				"database", "user", "password" }, new int[] {
				SQLUIPanel.STRING, SQLUIPanel.STRING, SQLUIPanel.INT },
				new String[] { "database is not null" },
				new String[] { "Ey men! You have to specify a database!" });
		panel[2] = UIFactory.getDynamicUIPanel("Congratulations!", null,
				new String[] { "any float" }, new int[] { SQLUIPanel.DOUBLE },
				null, null);

		JDialog dlg = UIFactory.getWizard(panel);
		dlg.setModal(true);
		dlg.pack();
		dlg.setVisible(true);

		System.out.println(panel[0].getValue("host"));
		System.out.println(panel[0].getValue("port"));
		System.out.println(panel[1].getValue("database"));
		System.out.println(panel[1].getValue("user"));
		System.out.println(panel[1].getValue("password"));
		System.out.println(panel[2].getValue("any float"));

	}
}
