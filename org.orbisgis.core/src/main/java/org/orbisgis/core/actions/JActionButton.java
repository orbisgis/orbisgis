package org.orbisgis.core.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class JActionButton extends JButton implements IActionControl {

	private IAction action;

	public JActionButton(ImageIcon imageIcon, IAction action) {
		super(imageIcon);
		this.action = action;
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JActionButton.this.action.actionPerformed();
			}

		});
		ActionControlsRegistry.addActionControl(this);
	}

	public void refresh() {
		this.setEnabled(action.isEnabled());
		this.setVisible(action.isVisible());
	}

}
