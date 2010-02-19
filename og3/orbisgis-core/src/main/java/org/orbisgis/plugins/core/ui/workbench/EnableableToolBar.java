package org.orbisgis.plugins.core.ui.workbench;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;

import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;

public class EnableableToolBar extends JToolBar {
	public EnableableToolBar() {
	}

	public EnableableToolBar(String name) {
		super(name);
	}

	public void add(AbstractButton button, String tooltip,
			ActionListener actionListener, Automaton automaton) {
		// button.setMargin(new Insets(0, 0, 0, 0));
		button.setToolTipText(tooltip);
		button.addActionListener(actionListener);
		// System.out.println("Add button to "+ this.getName() + " toolbar");
		add(button, automaton);
	}
}
