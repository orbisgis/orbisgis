package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.Font;
import java.awt.Insets;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;

public class SQLConsoleButton extends JButton {
	public SQLConsoleButton(final URL iconUrl, final String toolTipText,
			final SQLConsoleAction actionCommand,
			final ActionsListener actionsListener) {
		setMargin(new Insets(0, 0, 0, 0));
		setText("");
		setIcon(new ImageIcon(iconUrl));
		setFont(new Font("Dialog", Font.BOLD, 10));
		setToolTipText(toolTipText);
		setActionCommand(actionCommand.toString());
		addActionListener(actionsListener);
	}
}