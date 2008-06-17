package org.orbisgis.views.geocatalog.actions.create;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.sif.AbstractUIPanel;

public class LiteralConstraintPanel extends AbstractUIPanel implements
		UIConstraintPanel {

	private JPanel panel;
	private boolean isInt;
	private JTextField txtPanel;
	private int constraintCode;

	public LiteralConstraintPanel(int constraintCode, boolean isInt,
			String value) {
		this.constraintCode = constraintCode;
		this.isInt = isInt;
		panel = new JPanel();
		txtPanel = new JTextField(value, 8);
		panel.add(new JLabel("Value: "));
		panel.add(txtPanel);
	}

	public Constraint getConstraint() {
		if (isInt) {
			int intValue = Integer.parseInt(txtPanel.getText());
			return ConstraintFactory.createConstraint(constraintCode, intValue);
		} else {
			return ConstraintFactory.createConstraint(constraintCode, txtPanel
					.getText().trim());
		}
	}

	public Component getComponent() {
		return panel;
	}

	public String getTitle() {
		return "Specify the value for the "
				+ ConstraintFactory.getConstraintName(constraintCode);
	}

	public String validateInput() {
		if (isInt) {
			try {
				Integer.parseInt(txtPanel.getText());
			} catch (NumberFormatException e) {
				return "the value must be int";
			}
		}

		return null;
	}

}
