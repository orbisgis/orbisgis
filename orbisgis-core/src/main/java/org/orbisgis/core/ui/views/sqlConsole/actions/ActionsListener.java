/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.core.ui.views.sqlConsole.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.views.sqlConsole.ui.ConsoleAction;
import org.orbisgis.core.ui.views.sqlConsole.ui.ConsolePanel;

public class ActionsListener implements ActionListener, DocumentListener{

	private ConsolePanel consolePanel;

	private ConsoleListener listener;

	public ActionsListener(ConsoleListener listener, ConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
		this.listener = listener;
	}

	public void actionPerformed(ActionEvent e) {
		switch (new Integer(e.getActionCommand())) {
		case ConsoleAction.EXECUTE:
			listener.execute(consolePanel.getText());
			break;
		case ConsoleAction.CLEAR:
			if (consolePanel.getText().trim().length() > 0) {
				int answer = JOptionPane.showConfirmDialog(null,
						"Do you want to clear the contents of the console?",
						"Clear script", JOptionPane.YES_NO_OPTION);
				if (answer == JOptionPane.YES_OPTION) {
					consolePanel.setText("");
				}
			}
			break;
		case ConsoleAction.OPEN:
			try {
				String script = listener.open();
				if (script != null) {
					int answer = JOptionPane.NO_OPTION;
					if (consolePanel.getText().trim().length() > 0) {
						answer = JOptionPane
								.showConfirmDialog(
										null,
										"Do you want to clear all before loadding the file ?",
										"Open file",
										JOptionPane.YES_NO_CANCEL_OPTION);
					}

					if (answer == JOptionPane.YES_OPTION) {
						consolePanel.setText("");
					}

					if (answer != JOptionPane.CANCEL_OPTION) {
						consolePanel.insertString(script);
					}
				}
			} catch (BadLocationException e1) {
				Services.getErrorManager().error("Cannot add script", e1);
			} catch (IOException e1) {
				Services.getErrorManager().error("IO error.", e1);
			}
			break;
		case ConsoleAction.SAVE:
			try {
				listener.save(consolePanel.getText());
				JOptionPane.showMessageDialog(null, "The file has been saved.");
			} catch (IOException e1) {
				Services.getErrorManager().error("IO error.", e1);
			}

			break;
		}
		setButtonsStatus();
	}

	public void setButtonsStatus() {
		consolePanel.setButtonsStatus();
	}

	@Override
	public void changedUpdate(DocumentEvent e) {
		insertUpdate(e);
	}

	@Override
	public void insertUpdate(DocumentEvent e) {
		setButtonsStatus();
		listener.change();
	}

	@Override
	public void removeUpdate(DocumentEvent e) {
		insertUpdate(e);
	}

}