package org.orbisgis.configurations.ui;

import javax.swing.JPasswordField;

public class PasswordInput extends AbstractInput {
	private JPasswordField field;

	/**
	 * Creates a new password input component
	 * 
	 * @param text
	 *            the label at the left of the component
	 * @param enable
	 *            determines if the component must be enabled at startup
	 * @param width
	 *            the width of the component
	 */
	public PasswordInput(String text, boolean enable, int width) {
		super();
		field = new JPasswordField();
		field.setEditable(enable);
		setComponent(text, width, field);
	}

	@Override
	public void setEditable(boolean b) {
		field.setEditable(b);
	}

	@Override
	public String getValue() {
		return new String(field.getPassword());
	}

	@Override
	public void setValue(String value) {
		field.setText(value);
	}
}
