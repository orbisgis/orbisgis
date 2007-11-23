package org.orbisgis.core.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButtonMenuItem;

public class JActionRadioButtonMenuItem extends JRadioButtonMenuItem implements
		IActionControl {

	private ISelectableAction action;

	public JActionRadioButtonMenuItem(String text, ISelectableAction action) {
		super(text);
		this.action = action;
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JActionRadioButtonMenuItem.this.action.actionPerformed();
			}

		});
		ActionControlsRegistry.addActionControl(this);
	}

	public void refresh() {
		if (action != null) {
			this.setEnabled(action.isEnabled());
			this.setVisible(action.isVisible());
			this.setSelected(action.isSelected());
		}
	}

}
