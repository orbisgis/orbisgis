package org.orbisgis.plugins.core.ui.views;

import java.awt.Component;
import java.util.Observable;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.edition.EditableElement;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.map.MapTransform;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.ViewPlugIn;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.map.DefaultMapContextManager;
import org.orbisgis.plugins.core.ui.editors.map.MapContextManager;
import org.orbisgis.plugins.core.ui.editors.map.MapControl;
import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.editors.map.tool.TransitionException;
import org.orbisgis.plugins.core.ui.menu.MenuTree;
import org.orbisgis.plugins.core.ui.workbench.Names;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchFrame;

public class MapEditorPlugIn extends ViewPlugIn implements WorkbenchFrame,
		IEditor {

	private MapControl map;
	private String editors[];
	private EditableElement mapElement;
	private Automaton defaultTool;
	private String defaultMouseCursor;

	// TODO (pyf): ajouter des plugins dans la popup
	private org.orbisgis.plugins.core.ui.menu.MenuTree menuTree;

	public org.orbisgis.plugins.core.ui.menu.MenuTree getMenuTreePopup() {
		return menuTree;
	}

	public void initialize(PlugInContext context) throws Exception {
	}

	public void initialize(PlugInContext context, Automaton automaton) {
		menuTree = new MenuTree();
		Services.registerService(MapContextManager.class,
				"Gives access to the current MapContext",
				new DefaultMapContextManager());
		Services.registerService(ViewPlugIn.class,
				"Gives access to the current MapContext", this);
		this.defaultTool = automaton;
		this.defaultMouseCursor = Names.ZOOMIN_ICON;
		editors = new String[0];
		if (context.getWorkbenchContext().getWorkbench().getFrame()
				.getViewDecorator("Map") == null)
			context.getWorkbenchContext().getWorkbench().getFrame().getViews()
					.add(
							new ViewDecorator(this, "Map", getIcon("map.png"),
									editors));
	}

	public boolean execute(PlugInContext context) throws Exception {
		getUpdateFactory().loadView(getId());
		return true;
	}

	public boolean acceptElement(String typeId) {
		return typeId
				.equals("org.orbisgis.plugins.core.geocognition.MapContext");
	}

	public void setElement(EditableElement element) {
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

	private Automaton getIndependentToolInstance(Automaton tool,
			String mouseCursor) throws InstantiationException,
			IllegalAccessException {
		Automaton ret = tool.getClass().newInstance();
		ret.setMouseCursor(mouseCursor);
		return ret;
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

	public String getTitle() {
		return mapElement.getId();
	}

	public EditableElement getElement() {
		return mapElement;
	}

	public boolean getShowInfo() {
		return map.getShowCoordinates();
	}

	public void setShowInfo(boolean showInfo) {
		map.setShowCoordinates(showInfo);
	}

	public MapControl getMap() {
		return map;
	}

	public Automaton getDefaultTool() {
		return defaultTool;
	}

	public void setDefaultTool(Automaton defaultTool) {
		this.defaultTool = defaultTool;
	}

	public String getDefaultMouseCursor() {
		return defaultMouseCursor;
	}

	public void setDefaultMouseCursor(String defaultMouseCursor) {
		this.defaultMouseCursor = defaultMouseCursor;
	}

	// Observer & PlugIn
	public void update(Observable arg0, Object arg1) {
	}

	public boolean isVisible() {
		return false;
	}

	public ViewPlugIn getView() {
		return this;
	}
}
