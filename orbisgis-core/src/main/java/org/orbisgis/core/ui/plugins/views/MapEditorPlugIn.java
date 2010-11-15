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
package org.orbisgis.core.ui.plugins.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.ImageIcon;

import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.DefaultMapContextManager;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.editors.map.MapControl;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.pluginSystem.ViewPlugIn;
import org.orbisgis.core.ui.pluginSystem.menu.MenuTree;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchToolBar;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.utils.I18N;

public class MapEditorPlugIn extends ViewPlugIn implements WorkbenchFrame,
		IEditor {

	private RootPanePanel mapEditor;
	private MapControl mapControl;
	private WorkbenchToolBar mapToolBar;

	private String editors[];
	private EditableElement mapElement;
	private Automaton defaultTool;
	private ImageIcon defaultMouseCursor;

	private org.orbisgis.core.ui.pluginSystem.menu.MenuTree menuTree;

	public org.orbisgis.core.ui.pluginSystem.menu.MenuTree getMenuTreePopup() {
		return menuTree;
	}

	public MapEditorPlugIn() {
		WorkbenchContext wbContext = Services
				.getService(WorkbenchContext.class);
		mapEditor = new RootPanePanel();
		mapControl = new MapControl();
		// Map editor tool bar
		mapToolBar = new WorkbenchToolBar(wbContext, Names.MAP_TOOLBAR_NAME);

		WorkbenchToolBar scaleToolBar = new WorkbenchToolBar(wbContext,
				Names.MAP_TOOLBAR_SCALE);
		mapToolBar.add(scaleToolBar);
		mapToolBar.add(Box.createHorizontalGlue());

		WorkbenchToolBar projectionToolBar = new WorkbenchToolBar(wbContext,
				Names.MAP_TOOLBAR_PROJECTION);
		mapToolBar.add(projectionToolBar);
	}

	public void initialize(PlugInContext context) throws Exception {
		menuTree = new MenuTree();
		Services.registerService(MapContextManager.class,
				"Gives access to the current MapContext",
				new DefaultMapContextManager());
		Services.registerService(ViewPlugIn.class,
				"Gives access to the current MapContext", this);

		Automaton defaultTool = (Automaton) Services
				.getService(Automaton.class);
		this.defaultTool = defaultTool;
		this.defaultMouseCursor = OrbisGISIcon.ZOOMIN;
		editors = new String[0];
		setPlugInContext(context);
		if (context.getWorkbenchContext().getWorkbench().getFrame()
				.getViewDecorator(Names.EDITOR_MAP_ID) == null)
			context.getWorkbenchContext().getWorkbench().getFrame().getViews()
					.add(
							new ViewDecorator(this, Names.EDITOR_MAP_ID,
									getIcon("map.png"), editors));
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public boolean acceptElement(String typeId) {
		return typeId.equals("org.orbisgis.core.geocognition.MapContext");
	}

	public void setElement(EditableElement element) {
		MapContext mapContext = (MapContext) element.getObject();
		try {
			mapControl.setMapContext(mapContext);
			mapControl.setElement(element);
			mapControl.setDefaultTool(getIndependentToolInstance(defaultTool,
					defaultMouseCursor));
			mapControl.initMapControl();
			mapEditor.setContentPane(mapControl);
			mapToolBar
					.setPreferredSize(new Dimension(mapControl.getWidth(), 30));
			mapToolBar.setFloatable(false);
			mapEditor.add(mapToolBar, BorderLayout.PAGE_END);

		} catch (TransitionException e) {
			Services.getErrorManager().error(
					I18N.getText("orbisgis.core.tool.not_valid"), e);
		} catch (InstantiationException e) {
			Services.getErrorManager().error(
					I18N.getText("orbisgis.core.tool.not_valid"), e);
		} catch (IllegalAccessException e) {
			Services.getErrorManager().error(
					I18N.getText("orbisgis.core.tool.not_valid"), e);
		}

		this.mapElement = element;
	}

	private Automaton getIndependentToolInstance(Automaton tool,
			ImageIcon mouseCursor) throws InstantiationException,
			IllegalAccessException {
		Automaton ret = tool.getClass().newInstance();
		ret.setMouseCursor(mouseCursor);
		return ret;
	}

	public void delete() {
		mapControl.closing();
	}

	public Component getComponent() {
		return mapControl;
	}

	public MapTransform getMapTransform() {
		return mapControl.getMapTransform();
	}

	public String getTitle() {
		return mapElement.getId();
	}

	public EditableElement getElement() {
		return mapElement;
	}

	public boolean getShowInfo() {
		return mapControl.getShowCoordinates();
	}

	public void setShowInfo(boolean showInfo) {
		mapControl.setShowCoordinates(showInfo);
	}

	public MapControl getMapControl() {
		return mapControl;
	}

	public Automaton getDefaultTool() {
		return defaultTool;
	}

	public void setDefaultTool(Automaton defaultTool) {
		this.defaultTool = defaultTool;
	}

	public ImageIcon getDefaultMouseCursor() {
		return defaultMouseCursor;
	}

	public void setDefaultMouseCursor(ImageIcon defaultMouseCursor) {
		this.defaultMouseCursor = defaultMouseCursor;
	}

	// View plugin is updated by EditorViewPlugIn
	public boolean isEnabled() {
		return true;
	}

	public boolean isSelected() {
		return true;
	}

	public ViewPlugIn getView() {
		return this;
	}

	public String getName() {
		return "Map Editor view";
	}

	public WorkbenchToolBar getMapToolBar() {
		return mapToolBar;
	}

	public WorkbenchToolBar getScaleToolBar() {
		return mapToolBar.getToolbars().get(Names.MAP_TOOLBAR_SCALE);
	}

	public WorkbenchToolBar getProjectionToolBar() {
		return mapToolBar.getToolbars().get(Names.MAP_TOOLBAR_PROJECTION);
	}

	public WorkbenchToolBar getToolBarById(String id) {
		return mapToolBar.getToolbars().get(id);
	}
}
