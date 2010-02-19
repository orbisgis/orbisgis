package org.orbisgis.core.ui.pluginSystem.workbench;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.JToolBar;

import org.orbisgis.core.ui.editors.map.tool.Automaton;

public class EnableableToolBar extends JToolBar {
	public EnableableToolBar() {
	}

	public EnableableToolBar(String name) {
		super(name);
	}

	public void add(AbstractButton button, boolean dropDown, String tooltip,
			ActionListener actionListener, Automaton automaton) {
		// button.setMargin(new Insets(0, 0, 0, 0));
		button.setToolTipText(tooltip);
		button.addActionListener(actionListener);
		if(!dropDown)
			add(button, automaton);
	}
}
