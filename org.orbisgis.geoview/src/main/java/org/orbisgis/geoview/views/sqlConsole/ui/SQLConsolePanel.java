/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.geoview.views.sqlConsole.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.sqlConsole.actions.ActionsListener;

public class SQLConsolePanel extends JPanel {
	private JButton btExecute = null;
	private JButton btClear = null;
	// private JButton btStop = null;
	//private JButton btPrevious = null;
	//private JButton btNext = null;
	private JButton btOpen = null;
	private JButton btSave = null;

	private ActionsListener actionAndKeyListener;
	private GeoView2D geoview;
	private JPanel centerPanel;

	//private History history;
	private ScrollPane scrollPane2;

	/**
	 * This is the default constructor
	 * 
	 * @param geoview
	 */
	public SQLConsolePanel(GeoView2D geoview) {
		this.geoview = geoview;

		setLayout(new BorderLayout());
		add(getNorthPanel(), BorderLayout.NORTH);
		add(getCenterPanel(), BorderLayout.CENTER);
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
		// northPanel.add(getBtStop());

		//northPanel.add(getBtPrevious());
		//northPanel.add(getBtNext());

		northPanel.add(getBtOpen());
		northPanel.add(getBtSave());

		setBtExecute();
		setBtClear();
		setBtSave();

		return northPanel;
	}

	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(getScrollPane(), BorderLayout.CENTER);
		}
		return centerPanel;
	}

	private ScrollPane getScrollPane() {
		if (scrollPane2 == null) {
			scrollPane2 = new ScrollPane(getActionAndKeyListener());
		}
		return scrollPane2;
	}

	private ActionsListener getActionAndKeyListener() {
		if (null == actionAndKeyListener) {
			actionAndKeyListener = new ActionsListener(this);
		}
		return actionAndKeyListener;
	}

	private JButton getBtExecute() {
		if (null == btExecute) {
			btExecute = new SQLConsoleButton(ConsoleAction.EXECUTE,
					getActionAndKeyListener());
		}
		return btExecute;
	}

	private JButton getBtClear() {
		if (null == btClear) {
			btClear = new SQLConsoleButton(ConsoleAction.CLEAR,
					getActionAndKeyListener());
		}
		return btClear;
	}

	/*private JButton getBtPrevious() {
		if (null == btPrevious) {
			btPrevious = new SQLConsoleButton(ConsoleAction.PREVIOUS,
					getActionAndKeyListener());
		}
		return btPrevious;
	}

	private JButton getBtNext() {
		if (null == btNext) {
			btNext = new SQLConsoleButton(ConsoleAction.NEXT,
					getActionAndKeyListener());
		}
		return btNext;
	}*/

	private JButton getBtOpen() {
		if (null == btOpen) {
			btOpen = new SQLConsoleButton(ConsoleAction.OPEN,
					getActionAndKeyListener());
		}
		return btOpen;
	}

	private JButton getBtSave() {
		if (null == btSave) {
			btSave = new SQLConsoleButton(ConsoleAction.SAVE,
					getActionAndKeyListener());
		}
		return btSave;
	}

	public JTextPane getJTextPane() {
		return getScrollPane().getJTextPane();
	}

	public String getText() {
		return getJTextPane().getText();
	}

	public GeoView2D getGeoview() {
		return geoview;
	}

	/*public History getHistory() {
		if (null == history) {
			history = new History();
		}
		return history;
	}*/

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

	/*private void setBtPrevious() {
		if (getHistory().isPreviousAvailable()) {
			getBtPrevious().setEnabled(true);
		} else {
			getBtPrevious().setEnabled(false);
		}
	}

	private void setBtNext() {
		if (getHistory().isNextAvailable()) {
			getBtNext().setEnabled(true);
		} else {
			getBtNext().setEnabled(false);
		}
	}*/

	private void setBtOpen() {
		// btOpen.setEnabled(true);
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
		//setBtPrevious();
		//setBtNext();
		setBtOpen();
		setBtSave();
	}

	public void setText(String text) {
		getScrollPane().setText(text);
	}

	public void execute() {
		getActionAndKeyListener().execute();
	}
}