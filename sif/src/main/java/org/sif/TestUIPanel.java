package org.sif;

import java.awt.Component;
import java.net.URL;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class TestUIPanel implements UIPanel {

	public static void main(String[] args) {
		SIFDialog dlg = UIFactory.getSimpleDialog(new TestUIPanel(), null);
		dlg.setSize(300, 300);
		dlg.setModal(true);
		dlg.setVisible(true);
		System.out.println(dlg.isAccepted());
	}

	public JTextField txt;

	public Component getComponent() {
		JPanel pnl = new JPanel();
		txt = new JTextField(10);
		pnl.add(txt);
		return pnl;
	}

	public URL getIconURL() {
		return null;
	}

	public String getTitle() {
		return "Testing dialog";
	}

	public void initialize() {
	}

	public String validate() {
		if (txt.getText().trim().length() == 0) {
			return "you fool! write something!";
		}

		return null;
	}
}
