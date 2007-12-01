package org.orbisgis.core.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class JActionToggleButton extends JToggleButton implements
		IActionControl {

	private ISelectableAction action;

	public JActionToggleButton(ImageIcon imageIcon, boolean b, ISelectableAction action) {
		super(imageIcon, b);
		this.action = action;
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JActionToggleButton.this.action.actionPerformed();
				ActionControlsRegistry.refresh();
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
