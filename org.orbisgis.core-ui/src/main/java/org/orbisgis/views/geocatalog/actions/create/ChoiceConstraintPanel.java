package org.orbisgis.views.geocatalog.actions.create;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.sif.AbstractUIPanel;

public class ChoiceConstraintPanel extends AbstractUIPanel implements
		UIConstraintPanel {

	private JPanel panel;
	private int constraintCode;
	private JComboBox cmbOptions;
	private int[] choiceCodes;

	public ChoiceConstraintPanel(int constraintCode, String[] choices,
			int[] choiceCodes, String value) {
		this.constraintCode = constraintCode;
		panel = new JPanel();
		panel.add(new JLabel(ConstraintFactory
				.getConstraintName(constraintCode)
				+ ": "));
		cmbOptions = new JComboBox(choices);
		cmbOptions.setSelectedItem(value);
		if (cmbOptions.getSelectedIndex() == -1) {
			cmbOptions.setSelectedIndex(0);
		}
		panel.add(cmbOptions);
		this.choiceCodes = choiceCodes;
	}

	public Constraint getConstraint() {
		int code = cmbOptions.getSelectedIndex();
		return ConstraintFactory.createConstraint(constraintCode,
				choiceCodes[code]);
	}

	public Component getComponent() {
		return panel;
	}

	public String getTitle() {
		return "Enter new "
				+ ConstraintFactory.getConstraintName(constraintCode)
				+ " value";
	}

	public String validateInput() {
		return null;
	}

}
