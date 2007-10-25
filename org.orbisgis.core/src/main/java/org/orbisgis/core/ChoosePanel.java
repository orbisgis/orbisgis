package org.orbisgis.core;

import java.awt.Component;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.sif.UIPanel;

public class ChoosePanel implements UIPanel {

	private String[] names;
	private String title;
	private JList lst;
	private DefaultListModel model;

	public ChoosePanel(String title, String[] names) {
		this.title = title;
		this.names = names;
	}

	public Component getComponent() {
		lst = new JList();
		model = new DefaultListModel();
		for (int i = 0; i < names.length; i++) {
			model.addElement(names[i]);
		}
		lst.setModel(model);
		return lst;
	}

	public URL getIconURL() {
		return null;
	}

	public String getTitle() {
		return title;
	}

	public void initialize() {
	}

	public String validate() {
		if (lst.getSelectedIndex() == -1) {
			return "An item must be selected";
		}

		return null;
	}

	public String getSelected() {
		return (String) model.getElementAt(lst.getSelectedIndex());
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}
}
