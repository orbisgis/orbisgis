package org.orbisgis.core.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

public class JActionMenuItem extends JMenuItem implements IActionControl {

	private IAction action;

	public JActionMenuItem(String text, IAction action) {
		super(text);
		this.action = action;
		this.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JActionMenuItem.this.action.actionPerformed();
			}

		});
		ActionControlsRegistry.addActionControl(this);
	}

	public void refresh() {
		this.setEnabled(action.isEnabled());
		this.setVisible(action.isVisible());
	}

}
