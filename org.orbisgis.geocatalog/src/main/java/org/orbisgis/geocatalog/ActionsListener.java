package org.orbisgis.geocatalog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.orbisgis.geocatalog.resources.Folder;

public class ActionsListener implements ActionListener {
	private JFrame jFrame = null;

	private Catalog myCatalog = null;

	public void actionPerformed(ActionEvent e) {

		if ("DEL".equals(e.getActionCommand())) {
			// Removes the selected node
			if (JOptionPane.showConfirmDialog(jFrame,
					"Are you sure you want to delete this node ?",
					"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
				myCatalog.removeNode();
			}

		} else if ("NEWFOLDER".equals(e.getActionCommand())) {
			String name = JOptionPane.showInputDialog(jFrame, "Name");
			if (name != null && name.length() != 0) {
				Folder newNode = new Folder(name);
				myCatalog.getCatalogModel().insertNode(newNode);
			}

		} else if ("CLRCATALOG".equals(e.getActionCommand())) {
			// Clears the catalog
			if (JOptionPane.showConfirmDialog(jFrame,
					"Are you sure you want to clear the catalog ?",
					"Confirmation", JOptionPane.YES_NO_OPTION) == 0) {
				myCatalog.clearCatalog();
			}

		} else if ("EXIT".equals(e.getActionCommand())) {
			// Exit the program
			System.exit(0);

		} else if ("ABOUT".equals(e.getActionCommand())) {
			// Shows the about dialog
			JOptionPane.showMessageDialog(jFrame, "GeoCatalog\nVersion 0.0",
					"About GeoCatalog", JOptionPane.INFORMATION_MESSAGE);

		}

	}

	public JFrame getJFrame() {
		return jFrame;
	}

	public Catalog getMyCatalog() {
		return myCatalog;
	}

	public void setParameters(JFrame jFrame, Catalog myCatalog) {
		this.jFrame = jFrame;
		this.myCatalog = myCatalog;
	}
}