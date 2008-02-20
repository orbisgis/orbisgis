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
package org.orbisgis.geoview.views.beanshellConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.views.beanshellConsole.actions.ActionsListener;
import org.orbisgis.geoview.views.sqlConsole.ui.ConsoleAction;
import org.orbisgis.geoview.views.sqlConsole.ui.History;
import org.orbisgis.pluginManager.PluginManager;

import bsh.EvalError;
import bsh.Interpreter;

public class BSHConsolePanel extends JPanel {

	private JButton btExecute = null;
	private JButton btClear = null;
	private JButton btOpen = null;
	private JButton btSave = null;


	private ActionsListener actionAndKeyListener;

	private GeoView2D geoview;

	private JPanel centerPanel;

	private ScriptPanel scrollPane;



	/**
	 * This is the default constructor
	 *
	 * @param geoview
	 */
	public BSHConsolePanel(GeoView2D geoview) {
		this.geoview = geoview;

		setLayout(new BorderLayout());
		add(getNorthPanel(), BorderLayout.WEST);
		add(getCenterPanel(), BorderLayout.CENTER);
		setButtonsStatus();
		//	TODO
		/*
		 * There is a pb to set enable the buton execute. Look actionAndKeyListener.
		 */
		getBtExecute().setEnabled(true);
	}

	// getters
	private JPanel getNorthPanel() {
		final JPanel northPanel = new JPanel();
		

		final FlowLayout flowLayout = new FlowLayout();		
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		JToolBar toolBar = new JToolBar("Toolbar", JToolBar.VERTICAL);
		toolBar.add(getBtExecute());
		toolBar.add(getBtClear());
		

		toolBar.add(getBtOpen());
		toolBar.add(getBtSave());
		
		toolBar.setFloatable(false);
		
		northPanel.add(toolBar);
		northPanel.setLayout(flowLayout);
		
		setBtExecute();
		setBtClear();
		setBtSave();



		return northPanel;
	}
	
	private JPanel getCenterPanel() {
		if (centerPanel == null) {
			centerPanel = new JPanel();
			centerPanel.setLayout(new BorderLayout());
			centerPanel.add(getScriptPanel(), BorderLayout.CENTER);
		}
		return centerPanel;
	}

	
	private ScriptPanel getScriptPanel() {
		if (scrollPane == null) {
			
			scrollPane = new ScriptPanel(geoview,getActionAndKeyListener());
			
		}
		return scrollPane;
	}
	

		private JButton getBtClear() {
			if (null == btClear) {
				btClear = new BSHConsoleButton(ConsoleAction.CLEAR,
						getActionAndKeyListener());
			}
			return btClear;
		}

		
		private JButton getBtOpen() {
			if (null == btOpen) {
				btOpen = new BSHConsoleButton(ConsoleAction.OPEN,
						getActionAndKeyListener());
			}
			return btOpen;
		}

		private JButton getBtSave() {
			if (null == btSave) {
				btSave = new BSHConsoleButton(ConsoleAction.SAVE,
						getActionAndKeyListener());
			}
			return btSave;
		}

	
	

	public JTextPane getJEditTextArea() {
		return getScriptPanel().getJTextPane();
	}

	public String getText() {
		return getJEditTextArea().getText();
	}

	public GeoView2D getGeoview() {
		return geoview;
	}

	public void setText(String text) {
		getScriptPanel().setText(text);
	}

	private JButton getBtExecute() {
		if (null == btExecute) {
			btExecute = new BSHConsoleButton(ConsoleAction.EXECUTE,
					getActionAndKeyListener());
		}
		return btExecute;
	}

	private ActionsListener getActionAndKeyListener() {
		if (null == actionAndKeyListener) {
			actionAndKeyListener = new ActionsListener(this);
		}
		return actionAndKeyListener;
	}

	public void execute() {
		getActionAndKeyListener().execute();
	}

	public Interpreter getInterpreter() {

		return getScriptPanel().getInterpreter();
	}

	public FileOutputStream getFileOutputStream() {

		return getScriptPanel().getFileOutputStream();

	}

	public void eval(String queryPanelContent) {
		try {
			getInterpreter().eval(queryPanelContent);
			if (getScriptPanel().getOut().length()>0){
				getJEditTextArea().setText(getScriptPanel().getOut());
				getJEditTextArea().setForeground(Color.BLUE);
			}
			
		} catch (EvalError e) {
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(bos);
            e.printStackTrace(ps);
            
            PluginManager.error("Error executing beanshell script" , e);

		}

	}

	
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
		setBtOpen();
		setBtSave();
	}
	
	

}