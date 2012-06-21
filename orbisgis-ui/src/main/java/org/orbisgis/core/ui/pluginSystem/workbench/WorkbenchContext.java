/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.pluginSystem.workbench;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.orbisgis.core.Services;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.pluginSystem.PlugIn;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.mapEditor.MapEditorPlugIn;

public abstract class WorkbenchContext extends Observable {

	private String lastAction;
	private List<Observer> popupPlugInObservers = new ArrayList<Observer>();
	private List<Observer> viewsPlugInObservers = new ArrayList<Observer>();

	public List<Observer> getViewsPlugInObservers() {
		return viewsPlugInObservers;
	}

	public List<Observer> getPopupPlugInObservers() {
		return popupPlugInObservers;
	}

	public String getLastAction() {
		return lastAction;
	}	

	public OrbisWorkbench getWorkbench() {
		return null;
	}
	
	public PlugInContext createPlugInContext(PlugIn plugIn) {
		return new PlugInContext(this,plugIn);
	}
	
	public PlugInContext createPlugInContext() {
		return new PlugInContext(this);
	}

	public void setLastAction(String lastAction) {		
		this.lastAction = lastAction;
		//System.out.println("#############################"+lastAction+":"+countObservers()+"#################");
		addViewsPlugInObservers();
		addToolsPlugInObservers();
		//addTableObservers();
		//System.out.println("#############################"+countObservers()+"#################");
		setChanged();
		notifyObservers(lastAction);
		removeToolsPlugInObservers();
		removeViewsPlugInObservers();
		//removeTableObservers();
		//System.out.println("#############################"+countObservers()+"#################");		
	}

	public void setRowSelected(MouseEvent e) {
		addToolsPlugInObservers();
		addTableObservers();
		setChanged();
		notifyObservers(e);
		removeToolsPlugInObservers();
		removeTableObservers();
	}	

	public void setHeaderSelected(int selectedColumn) {
		addToolsPlugInObservers();
		addTableObservers();
		setChanged();
		notifyObservers(selectedColumn);
		removeToolsPlugInObservers();
		removeTableObservers();
	}	
	
	private void removeTableObservers() {
		for (int i = 0; i < popupPlugInObservers.size(); i++)									
			deleteObserver(popupPlugInObservers.get(i));		
	}
	
	private void addTableObservers() {
		for (int i = 0; i < popupPlugInObservers.size(); i++)									
			addObserver(popupPlugInObservers.get(i));		
	}
	
	private void removeViewsPlugInObservers() {
		for (int i = 0; i < viewsPlugInObservers.size(); i++)									
			deleteObserver(viewsPlugInObservers.get(i));		
	}
	
	private void addViewsPlugInObservers() {
		for (int i = 0; i < viewsPlugInObservers.size(); i++)									
			addObserver(viewsPlugInObservers.get(i));		
	}
	
	private void addToolsPlugInObservers(){	
		Map<String, WorkbenchToolBar> bars = getWorkbench().getFrame().getWorkbenchToolBar().getToolbars();
		Iterator<WorkbenchToolBar> it = bars.values().iterator();						
		List<Observer> observers = new ArrayList<Observer>();
		while(it.hasNext()){	
			WorkbenchToolBar wb = it.next();			
			observers = wb.getToolsPlugInObservers();
			addObserver(wb);
			for (int i = 0; i < observers.size(); i++)									
				addObserver(observers.get(i));
		}
		
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if(editor instanceof MapEditorPlugIn) {
			MapEditorPlugIn mapEditor = (MapEditorPlugIn)editor;
			if(mapEditor!=null) {
				bars = mapEditor.getMapToolBar().getToolbars();
				it = bars.values().iterator();						
				observers = new ArrayList<Observer>();
				while(it.hasNext()){	
					WorkbenchToolBar wb = it.next();			
					observers = wb.getToolsPlugInObservers();
					addObserver(wb);
					for (int i = 0; i < observers.size(); i++)									
						addObserver(observers.get(i));
				}
			}
		}
	}
	
	private void  removeToolsPlugInObservers(){
		Map<String, WorkbenchToolBar> bars = getWorkbench().getFrame().getWorkbenchToolBar().getToolbars();
		Iterator<WorkbenchToolBar> it = bars.values().iterator();						
		List<Observer> observers = new ArrayList<Observer>();
		while(it.hasNext()){	
			WorkbenchToolBar wb = it.next();						
			observers = wb.getToolsPlugInObservers();
			for (int i = 0; i < observers.size(); i++)
				deleteObserver(observers.get(i));
			deleteObserver(wb);			
		}
		
		IEditor editor = Services.getService(EditorManager.class).getActiveEditor();
		if(editor instanceof MapEditorPlugIn) {
			MapEditorPlugIn mapEditor = (MapEditorPlugIn)editor;
			bars = mapEditor.getMapToolBar().getToolbars();
			it = bars.values().iterator();						
			observers = new ArrayList<Observer>();
			while(it.hasNext()){	
				WorkbenchToolBar wb = it.next();						
				observers = wb.getToolsPlugInObservers();
				for (int i = 0; i < observers.size(); i++)
					deleteObserver(observers.get(i));
				deleteObserver(wb);			
			}
		}
	}
}
