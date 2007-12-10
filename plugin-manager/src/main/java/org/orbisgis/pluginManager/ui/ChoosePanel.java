package org.orbisgis.pluginManager.ui;

import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.JList;

import org.sif.AbstractUIPanel;

public class ChoosePanel extends AbstractUIPanel {

	private String[] names;
	private String title;
	private JList lst;
	private DefaultListModel model;
	private String[] ids;

	public ChoosePanel(String title, String[] names, String[] ids) {
		this.title = title;
		this.names = names;
		this.ids = ids;
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

	public String getTitle() {
		return title;
	}

	public String validateInput() {
		if (lst.getSelectedIndex() == -1) {
			return "An item must be selected";
		}

		return null;
	}

	public String getSelected() {
		return ids[lst.getSelectedIndex()];
	}

	public int getSelectedIndex() {
		return lst.getSelectedIndex();
	}

}
