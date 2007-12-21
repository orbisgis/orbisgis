package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.Font;
import java.awt.Insets;

import javax.swing.JButton;

import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;

public class SQLConsoleButton extends JButton {
	public SQLConsoleButton(final Integer actionCommandType,
			final ActionsListener actionsListener) {
		setMargin(new Insets(0, 0, 0, 0));
		setText("");
		setIcon(ConsoleAction.getImageIcon(actionCommandType));
		setFont(new Font("Dialog", Font.BOLD, 10));
		setToolTipText(ConsoleAction.getToolTipText(actionCommandType));
		setActionCommand(actionCommandType.toString());
		addActionListener(actionsListener);
	}
}