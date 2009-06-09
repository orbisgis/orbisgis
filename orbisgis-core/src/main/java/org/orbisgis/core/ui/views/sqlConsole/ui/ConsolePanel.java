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
package org.orbisgis.core.ui.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.orbisgis.core.ui.views.sqlConsole.actions.ActionsListener;
import org.orbisgis.core.ui.views.sqlConsole.actions.ConsoleListener;

public class ConsolePanel extends JPanel {
	private JButton btExecute = null;
	private JButton btClear = null;
	private JButton btOpen = null;
	private JButton btSave = null;

	private ActionsListener actionAndKeyListener;
	private JPanel centerPanel;

	private ScriptPanel scriptPanel;

	/**
	 * Creates a console for sql or java.
	 */
	public ConsolePanel(boolean sql, ConsoleListener listener) {
		actionAndKeyListener = new ActionsListener(listener, this);

		setLayout(new BorderLayout());
		add(getCenterPanel(sql, listener), BorderLayout.CENTER);
		if (listener.showControlButtons()) {
			add(getNorthPanel(), BorderLayout.NORTH);
		}
		setButtonsStatus();
	}

	// getters
	private JPanel getNorthPanel() {
		final JPanel northPanel = new JPanel();
		final FlowLayout flowLayout = new FlowLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		northPanel.setLayout(flowLayout);

		northPanel.add(getBtExecute());
		northPanel.add(getBtClear());
		northPanel.add(getBtOpen());
		northPanel.add(getBtSave());

		setBtExecute();
		setBtClear();
		setBtSave();

		return northPanel;
	}

	private JPanel getCenterPanel(boolean sql, ConsoleListener listener) {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			scriptPanel = new ScriptPanel(actionAndKeyListener, listener, sql);
			centerPanel.add(scriptPanel, BorderLayout.CENTER);
		}
		return centerPanel;
	}

	private JButton getBtExecute() {
		if (null == btExecute) {
			btExecute = new ConsoleButton(ConsoleAction.EXECUTE,
					actionAndKeyListener);
		}
		return btExecute;
	}

	private JButton getBtClear() {
		if (null == btClear) {
			btClear = new ConsoleButton(ConsoleAction.CLEAR,
					actionAndKeyListener);
		}
		return btClear;
	}

	private JButton getBtOpen() {
		if (null == btOpen) {
			btOpen = new ConsoleButton(ConsoleAction.OPEN, actionAndKeyListener);
		}
		return btOpen;
	}

	private JButton getBtSave() {
		if (null == btSave) {
			btSave = new ConsoleButton(ConsoleAction.SAVE, actionAndKeyListener);
		}
		return btSave;
	}

	public String getText() {
		return scriptPanel.getText();
	}

	// setters
	private void setBtExecute() {
		if (0 == getText().length()) {
			getBtExecute().setEnabled(false);
		} else {
			getBtExecute().setEnabled(true);
		}
	}

	private void setBtClear() {
		if (0 == getText().length()) {
			getBtClear().setEnabled(false);
		} else {
			getBtClear().setEnabled(true);
		}
	}

	private void setBtOpen() {
	}

	private void setBtSave() {
		if (0 == getText().length()) {
			getBtSave().setEnabled(false);
		} else {
			getBtSave().setEnabled(true);
		}
	}

	public void setButtonsStatus() {
		setBtExecute();
		setBtClear();
		setBtOpen();
		setBtSave();
	}

	public void setText(String text) {
		scriptPanel.setText(text);
	}

	public void insertString(String string) throws BadLocationException {
		scriptPanel.insertString(string);
	}

	public JTextComponent getTextComponent() {
		return scriptPanel.getTextComponent();
	}
}