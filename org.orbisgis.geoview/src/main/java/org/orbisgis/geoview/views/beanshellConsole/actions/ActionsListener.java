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
package org.orbisgis.geoview.views.beanshellConsole.actions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.orbisgis.geoview.views.beanshellConsole.BSHConsolePanel;
import org.orbisgis.geoview.views.beanshellConsole.ConsoleAction;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;
import org.syntax.jedit.JEditTextArea;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import bsh.ConsoleInterface;
import bsh.EvalError;


public class ActionsListener implements ActionListener, KeyListener {
	private final String EOL = System.getProperty("line.separator");

	private BSHConsolePanel consolePanel;



	public ActionsListener(BSHConsolePanel consolePanel) {
		this.consolePanel = consolePanel;
		
	}

	public void actionPerformed(ActionEvent e) {
		switch (new Integer(e.getActionCommand())) {
		case ConsoleAction.EXECUTE:
			execute();
			break;
		case ConsoleAction.CLEAR:
			consolePanel.getJEditTextArea().setForeground(Color.BLACK);
			consolePanel.setText("");
			break;
		case ConsoleAction.STOP:
			break;
		case ConsoleAction.SAVE:
			save();
			break;
		}
	}

	private void setQuery(String query) {
		consolePanel.setText(query);
	}

	public void save() {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.sqlConsoleOutFile", "Select a sql file");
		outfilePanel.addFilter("sql", "SQL script (*.sql)");
		outfilePanel.addFilter("txt", "Text file (*.txt)");

		if (UIFactory.showDialog(outfilePanel)) {
			try {
				final BufferedWriter out = new BufferedWriter(new FileWriter(
						outfilePanel.getSelectedFile()));
				out.write(consolePanel.getText());
				out.close();
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ outfilePanel.getSelectedFile(), e);
			}
		}
	}

	public void execute() {

		consolePanel.getJEditTextArea().setForeground(Color.BLACK);
		final String queryPanelContent = consolePanel.getText();

			consolePanel.eval(queryPanelContent);

	}

	public void keyPressed(KeyEvent e) {
		

	}

	public void keyReleased(KeyEvent e) {
		

	}

	public void keyTyped(KeyEvent e) {

	}
	
	

}