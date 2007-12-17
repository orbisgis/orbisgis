package org.sif.multiInputPanel;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.JScrollPane;

import org.sif.SQLUIPanel;

public class ListChoice implements InputType {
	public static final String SEPARATOR = "#";
	private JList comp;

	public ListChoice(String... choices) {
		comp = new JList(choices);
	}

	public Component getComponent() {
		return new JScrollPane(comp);
	}

	public int getType() {
		return SQLUIPanel.STRING;
	}

	public String getValue() {
		final Object[] selectedValues = comp.getSelectedValues();
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < selectedValues.length; i++) {
			sb.append(selectedValues[i]);
			if (i + 1 != selectedValues.length) {
				sb.append(SEPARATOR);
			}
		}
		return sb.toString();
	}

	public void setValue(String value) {
		if (null != value) {
			comp.setListData(value.split(SEPARATOR));
		}
	}

	public boolean isPersistent() {
		return true;
	}
}