/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.editor;

import java.awt.Component;
import java.awt.event.MouseMotionAdapter;

import org.orbisgis.core.ui.editor.EditorListener;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.PlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;

/**
 * 
 * Adapt editor listener for plug-in
 *
 */
public class PlugInEditorListener implements EditorListener {

	/**	plug-in that listen editors */
	private PlugIn plugIn;
	/** plug-in component (JButton,JLabel...) to update */
	private Component component;
	/** plug-in 's tool bar parent */
	private String toolBarId;
	/** plug-in behavior on mouse motion */
	private MouseMotionAdapter mouseMotionAdapter;
	/** plug-in context */
	private PlugInContext context;
	/** plug-in can be a panel */
	private boolean isPanel;
	
	/**
	 * Constructor
	 * @param plugIn : plug-in that listen editors
	 * @param comp : plug-in component (JButton,JLabel...) to update
	 * @param toolBarId : plug-in 's tool bar parent
	 * @param mouseMotionAdapter :plug-in behavior on mouse motion
	 * @param context : plug-in context
	 * @param isPanel : plug-in can be a panel
	 */
	public PlugInEditorListener(PlugIn plugIn, Component comp, String toolBarId,
			MouseMotionAdapter mouseMotionAdapter, PlugInContext context, boolean isPanel) {
		this.plugIn = plugIn;
		this.component = comp;
		this.toolBarId = toolBarId;
		this.mouseMotionAdapter = mouseMotionAdapter;
		this.context = context;
		this.isPanel = isPanel;
	}
	
	/**
	 * Load plug-in in workbench context when element was loaded in editor
	 * @param editor : active editor
	 * @param comp : editor component (@see org.orbisgis.core.ui.editors.map.MapControl)
	 */
	public void elementLoaded(IEditor editor, Component comp) {
		MapEditorPlugIn mapEditor = ((MapEditorPlugIn) editor) ;
		if(isPanel)
			mapEditor.getToolBarById(toolBarId).addPanelPlugIn(plugIn,component,context);
		else
			mapEditor.getToolBarById(toolBarId).addPlugIn(plugIn,component,context);
		if(mouseMotionAdapter!=null)
			comp.addMouseMotionListener(mouseMotionAdapter);
	}
	
	public boolean activeEditorClosing(IEditor editor, String editorId) {		
		return true;
	}
	
	public void activeEditorChanged(IEditor previous, IEditor current) {
	}
	
	public void activeEditorClosed(IEditor editor, String editorId) {
	}

}
