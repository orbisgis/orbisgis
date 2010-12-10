/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 * 
 *  Team leader Erwan BOCHER, scientific researcher,
 * 
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.pluginSystem;

import java.awt.Component;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.orbisgis.core.PersistenceException;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.ViewDecorator;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;
import org.orbisgis.utils.I18N;

/**
 * Default implementation of Docking PlugIn. OrbisGIS based on Docking Window library.
 * So to add docking view as plug-in we created a specific plug-in
 */

public abstract class ViewPlugIn implements PlugIn {

	private String id;
	private Component component;	
	private PlugInContext plugInContext;
	
	// private ResourceBundle i18n;
	private String langAndCountry;
	// Constructors
    public ViewPlugIn() {
    	getI18n();
    }
	
	public void i18nConfigure(String langAndCountry) {
		delI18n();
		this.langAndCountry = langAndCountry;	
		getI18n();
	}

	private void delI18n() {
		I18N.delI18n(null, this.getClass());		
	}
	
	public void getI18n() {
		I18N.addI18n(langAndCountry, null, this.getClass());
	}

	public void createPlugInContext(JComponent c, String id,
			ImageIcon icon, String[] editors, WorkbenchContext context) {
		this.component = c;
		this.id = id;
		context.getWorkbench().getFrame().getViews().add(
				new ViewDecorator(this, id, icon,
						(editors == null) ? new String[0] : editors));
		if(plugInContext == null)
			plugInContext = context.createPlugInContext(this);
		if(!context.getViewsPlugInObservers().contains(this))
			context.getViewsPlugInObservers().add(this);		
	}
	
	protected PlugInContext getPlugInContext() {
			return plugInContext;
	}
	
	public void setPlugInContext(PlugInContext plugInContext) {
		this.plugInContext = plugInContext;
	}

	public void update(Observable o, Object arg) {
		isEnabled();
		isSelected();		
	}
    
	// View PlugIn Icon
	public static ImageIcon getIcon(String nameIcone) {
		return IconLoader.getIcon(nameIcone);
	}

	// Get View PlugIn Id
	public String getId() {
		return id;
	}

	// get panel in the View PlugIn for load his popup
	public JPanel getPanel() {
		return null;
	}

	// PlugIn implementation
	// Editor in View (used by DW to place MapEditor & TableEditor Views in
	// EditorPanel)
	public void editorViewDisabled() {
	}

	public boolean setEditor(IEditor editor) {
		return false;
	}

	// ViewPlugIn persistence
	public Component getComponent() {
		return component;
	}

	public void delete() {
	}

	public void loadStatus() throws PersistenceException {
	}

	public void saveStatus() throws PersistenceException {
	}

	public void initialize(WorkbenchContext wbContext) {
	}

	public void initialize(PlugInContext wbContext, Automaton automaton) {
	}
}
