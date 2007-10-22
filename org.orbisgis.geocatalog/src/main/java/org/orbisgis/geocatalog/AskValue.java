package org.orbisgis.geocatalog;


import java.awt.Component;
import java.net.URL;

import javax.swing.JTextField;

import org.sif.SQLUIPanel;

public class AskValue implements SQLUIPanel {

	private JTextField txtField;
	private String sql;
	private String title;
	private String error;

	public AskValue(String title, String sql, String error) {
		this.title = title;
		this.sql = sql;
		this.error = error;
	}

	public Component getComponent() {
		txtField = new JTextField();
		return txtField;
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
		return null;
	}

	public String[] getErrorMessages() {
		return new String[] { error };
	}

	public String[] getFieldNames() {
		return new String[] { "txt" };
	}

	public int[] getFieldTypes() {
		return new int[] { SQLUIPanel.STRING };
	}

	public String[] getValidationExpressions() {
		return new String[] { sql };
	}

	public String[] getValues() {
		return new String[] { txtField.getText() };
	}

	public String getValue() {
		return getValues()[0];
	}

}
