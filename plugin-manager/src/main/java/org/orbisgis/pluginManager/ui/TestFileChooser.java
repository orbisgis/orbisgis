package org.orbisgis.pluginManager.ui;

import java.awt.Container;

import javax.swing.JDialog;
import javax.swing.JFileChooser;

public class TestFileChooser extends JDialog {

	public TestFileChooser() {
		Container c = this.getContentPane();
		JFileChooser fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setControlButtonsAreShown(false);
		c.add(fc);
		this.pack();
		this.setModal(true);
		this.setVisible(true);

		fc.approveSelection();
		System.out.println(fc.getSelectedFile());
		System.exit(0);
	}

	public static void main(String[] args) {
		new TestFileChooser();
	}

}
