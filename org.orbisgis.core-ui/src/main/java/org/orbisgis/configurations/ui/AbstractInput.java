package org.orbisgis.configurations.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class AbstractInput extends JPanel {

	/**
	 * Creates a new AbstractInput
	 */
	AbstractInput() {
		super(new GridBagLayout());
	}

	/**
	 * Sets the input component of this abstract input
	 * 
	 * @param text
	 *            the label at the left of the component
	 * @param width
	 *            the width of the input component
	 * @param comp
	 *            the input component
	 */
	protected void setComponent(String text, int width, JComponent comp) {
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0;
		c.gridy = 0;
		JLabel label = new JLabel(text);
		label.setPreferredSize(new Dimension(75, 20));
		label.setMinimumSize(new Dimension(75, 20));
		add(label, c);

		c.gridx = 1;
		comp.setPreferredSize(new Dimension(width, 20));
		comp.setMinimumSize(new Dimension(width, 20));
		add(comp, c);

		c.gridx = 2;
		JLabel foo = new JLabel("");
		foo.setPreferredSize(new Dimension(250 - width, 20));
		foo.setMinimumSize(new Dimension(250 - width, 20));
		add(foo, c);
	}

	/**
	 * Sets the input component as editable or not
	 * 
	 * @param b
	 *            flag to set as editable
	 */
	public abstract void setEditable(boolean b);

	/**
	 * Gets the value of the input component
	 * 
	 * @return the value of the input component
	 */
	public abstract String getValue();
}
