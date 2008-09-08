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
package org.orbisgis.editors.map;

import java.awt.Component;
import java.util.HashMap;

import org.orbisgis.Services;
import org.orbisgis.action.EPBaseActionHelper;
import org.orbisgis.action.IActionAdapter;
import org.orbisgis.action.IActionFactory;
import org.orbisgis.action.ISelectableActionAdapter;
import org.orbisgis.action.MenuTree;
import org.orbisgis.action.ToolBarArray;
import org.orbisgis.editor.IEditor;
import org.orbisgis.editor.IExtensionPointEditor;
import org.orbisgis.editors.map.tool.Automaton;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.map.MapTransform;
import org.orbisgis.views.editor.EditorManager;

public class MapEditor implements IExtensionPointEditor {

	private MapControl map;
	private GeocognitionElement mapElement;
	private static Automaton defaultTool;
	private static String defaultMouseCursor;

	public boolean acceptElement(String typeId) {
		return typeId.equals("org.orbisgis.geocognition.MapContext");
	}

	public void setElement(GeocognitionElement element) {
		MapContext mapContext = (MapContext) element.getObject();
		try {
			map = new MapControl(mapContext, getIndependentToolInstance(
					defaultTool, defaultMouseCursor));
		} catch (TransitionException e) {
			Services.getErrorManager()
					.error("The default tool is not valid", e);
		} catch (InstantiationException e) {
			Services.getErrorManager()
					.error("The default tool is not valid", e);
		} catch (IllegalAccessException e) {
			Services.getErrorManager()
					.error("The default tool is not valid", e);
		}

		this.mapElement = element;
	}

	public void delete() {
		map.closing();
	}

	public Component getComponent() {
		return map;
	}

	public MapTransform getMapTransform() {
		return map.getMapTransform();
	}

	public void initialize() {
		Services.registerService("org.orbisgis.MapContextManager",
				MapContextManager.class,
				"Gives access to the current MapContext",
				new DefaultMapContextManager());
	}

	public void loadStatus() {
	}

	public void saveStatus() {
	}

	public String getTitle() {
		return mapElement.getId();
	}

	public GeocognitionElement getElement() {
		return mapElement;
	}

	public void installExtensionPoint(MenuTree menuTree,
			ToolBarArray toolBarArray) {
		IActionFactory toolFactory = new ToolFactory();
		EPBaseActionHelper.configureMenuAndToolBar(
				"org.orbisgis.editors.map.Tool", "tool", toolFactory, menuTree,
				toolBarArray);
	}

	public void prepareMenus(MenuTree menuTree, ToolBarArray toolBarArray) {
		EPBaseActionHelper.configureParentMenusAndToolBars(
				new String[] { "org.orbisgis.editors.map.Tool" }, menuTree,
				toolBarArray);
	}

	private Automaton getIndependentToolInstance(Automaton tool,
			String mouseCursor) throws InstantiationException,
			IllegalAccessException {
		Automaton ret = tool.getClass().newInstance();
		ret.setMouseCursor(mouseCursor);
		return ret;
	}

	private final class ToolFactory implements IActionFactory {

		public IActionAdapter getAction(Object action,
				HashMap<String, String> attributes) {
			return new IToolDecorator(action, attributes.get("mouse-cursor"));
		}

		public ISelectableActionAdapter getSelectableAction(Object action,
				HashMap<String, String> attributes) {
			return new IToolDecorator(action, attributes.get("mouse-cursor"));
		}
	}

	private final class IToolDecorator implements IActionAdapter,
			ISelectableActionAdapter {

		private Automaton action;
		private String mouseCursor;

		public IToolDecorator(Object action, String mouseCursor) {
			this.action = (Automaton) action;
			this.mouseCursor = mouseCursor;
			if (defaultTool == null) {
				defaultTool = this.action;
				defaultMouseCursor = mouseCursor;
			}
		}

		public boolean isVisible() {
			EditorManager em = (EditorManager) Services
					.getService("org.orbisgis.EditorManager");
			IEditor editor = em.getActiveEditor();
			if ((editor == null) || !(editor instanceof MapEditor)) {
				return false;
			} else {
				MapEditor mapEditor = (MapEditor) editor;
				MapContext mapContext = (MapContext) mapEditor.mapElement
						.getObject();
				if (mapContext != null) {
					return action.isVisible(mapContext, mapEditor.map
							.getToolManager());
				} else {
					return false;
				}
			}
		}

		public boolean isEnabled() {
			EditorManager em = (EditorManager) Services
					.getService("org.orbisgis.EditorManager");
			IEditor editor = em.getActiveEditor();
			if ((editor == null) || !(editor instanceof MapEditor)) {
				return false;
			} else {
				MapEditor mapEditor = (MapEditor) editor;
				MapContext mapContext = (MapContext) mapEditor.mapElement
						.getObject();
				if (mapContext != null) {
					return action.isEnabled(mapContext, mapEditor.map
							.getToolManager());
				} else {
					return false;
				}
			}
		}

		public void actionPerformed() {
			EditorManager em = (EditorManager) Services
					.getService("org.orbisgis.EditorManager");
			IEditor editor = em.getActiveEditor();
			MapEditor mapEditor = (MapEditor) editor;
			try {
				Automaton newTool = getIndependentToolInstance(action,
						mouseCursor);
				mapEditor.map.setTool(newTool);
			} catch (TransitionException e) {
				Services.getErrorManager().error("Cannot use tool", e);
			} catch (InstantiationException e) {
				Services.getErrorManager().error(
						"Invalid tool. Check " + "it has an empty constructor",
						e);
			} catch (IllegalAccessException e) {
				Services.getErrorManager().error("Cannot use tool", e);
			}
		}

		public boolean isSelected() {
			EditorManager em = (EditorManager) Services
					.getService("org.orbisgis.EditorManager");
			IEditor editor = em.getActiveEditor();
			if ((editor == null) || !(editor instanceof MapEditor)) {
				return false;
			} else {
				MapEditor mapEditor = (MapEditor) editor;
				MapContext mapContext = (MapContext) mapEditor.mapElement
						.getObject();
				if (mapContext != null) {
					return mapEditor.map.getTool().getClass().equals(
							action.getClass());
				} else {
					return false;
				}
			}
		}
	}

	public boolean getShowInfo() {
		return map.getShowCoordinates();
	}

	public void setShowInfo(boolean showInfo) {
		map.setShowCoordinates(showInfo);
	}

}
