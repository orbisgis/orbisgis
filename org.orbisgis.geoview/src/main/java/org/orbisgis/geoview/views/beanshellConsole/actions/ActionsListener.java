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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.orbisgis.geoview.views.beanshellConsole.BSHConsolePanel;
import org.orbisgis.geoview.views.beanshellConsole.ConsoleAction;
import org.orbisgis.geoview.views.sqlConsole.ui.History;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.OpenFilePanel;
import org.orbisgis.pluginManager.ui.SaveFilePanel;
import org.sif.UIFactory;

public class ActionsListener implements ActionListener, KeyListener {
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
		case ConsoleAction.OPEN:
			open();
			break;
		case ConsoleAction.SAVE:
			save();
			break;
		}
		setButtonsStatus();
	}




	private void setScript(String query) {
		consolePanel.setText(query);
	}

	public void save() {
		final SaveFilePanel outfilePanel = new SaveFilePanel(
				"org.orbisgis.geoview.BSHConsoleOutFile", "Select a bsh file");
		outfilePanel.addFilter("bsh", "BeanShell script (*.bsh)");


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

	private void open() {
		final OpenFilePanel inFilePanel = new OpenFilePanel(
				"org.orbisgis.geoview.BSHConsoleInFile", "Select a bsh file");
		inFilePanel.addFilter("bsh", "BeanShell script (*.bsh)");

		if (UIFactory.showDialog(inFilePanel)) {
			try {
				for (File selectedFile : inFilePanel.getSelectedFiles()) {

					long fileLength = selectedFile.length();
		            if(fileLength>1048576){
		                consolePanel.getJEditTextArea().setText(("\nERROR : Script files of more than 1048576 bytes can't be read !!"));
		                return;
		            }

					FileReader fr = new FileReader(selectedFile);
		            char[] buff = new char[(int)fileLength];
		            fr.read(buff,0,(int)fileLength);
		            String string = new String(buff);
		            consolePanel.setText(selectedFile.getAbsolutePath());
		            consolePanel.getJEditTextArea().setText(string);
		            fr.close();
				}
			} catch (FileNotFoundException e) {
				PluginManager.warning("SQL script file not found : "
						+ inFilePanel.getSelectedFile(), e);
			} catch (IOException e) {
				PluginManager.warning("IOException with "
						+ inFilePanel.getSelectedFile(), e);
			}
		}
	}

	public void execute() {

		consolePanel.getJEditTextArea().setForeground(Color.BLACK);
		final String queryPanelContent = consolePanel.getText();

		if (queryPanelContent.length() > 0) {			
			consolePanel.eval(queryPanelContent);
			consolePanel.getJEditTextArea().setText("");
		}
		else {
			
		}

	}

	public void setButtonsStatus() {
		consolePanel.setButtonsStatus();
	}

	public void keyPressed(KeyEvent e) {
		setButtonsStatus();
	}

	public void keyReleased(KeyEvent e) {
		setButtonsStatus();
	}

	public void keyTyped(KeyEvent e) {
		setButtonsStatus();
	}



}