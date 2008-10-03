package org.orbisgis.configurations.ui;

import javax.swing.JTextField;

public class TextInput extends AbstractInput {
	private JTextField field;

	/**
	 * Creates a new text input component
	 * 
	 * @param text
	 *            the label at the left of the component
	 * @param enable
	 *            determines if the component must be enabled at startup
	 * @param width
	 *            the width of the component
	 */
	public TextInput(String text, boolean enable, int width) {
		super();
		field = new JTextField();
		field.setEditable(enable);
		setComponent(text, width, field);
	}

	@Override
	public void setEditable(boolean b) {
		field.setEditable(b);
	}

	@Override
	public String getValue() {
		return field.getText();
	}
}
