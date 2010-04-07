package org.orbisgis.core.ui.plugins.views;

import java.awt.Component;
import java.util.Observable;

import org.orbisgis.core.Services;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.images.IconNames;
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
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchFrame;

public class MapEditorPlugIn extends ViewPlugIn implements WorkbenchFrame,
		IEditor {

	private MapControl map;
	private String editors[];
	private EditableElement mapElement;
	private Automaton defaultTool;
	private String defaultMouseCursor;

	// TODO (pyf): ajouter des plugins dans la popup
	private org.orbisgis.core.ui.pluginSystem.menu.MenuTree menuTree;

	public org.orbisgis.core.ui.pluginSystem.menu.MenuTree getMenuTreePopup() {
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
		this.defaultMouseCursor = IconNames.ZOOMIN_ICON;
		editors = new String[0];
		if (context.getWorkbenchContext().getWorkbench().getFrame()
				.getViewDecorator(Names.EDITOR_MAP_ID) == null)
			context.getWorkbenchContext().getWorkbench().getFrame().getViews()
					.add(
							new ViewDecorator(this, Names.EDITOR_MAP_ID, getIcon("map.png"),
									editors));
	}

	public boolean execute(PlugInContext context) throws Exception {
		getPlugInContext().loadView(getId());
		return true;
	}

	public boolean acceptElement(String typeId) {
		return typeId
				.equals("org.orbisgis.core.geocognition.MapContext");
	}

	public void setElement(EditableElement element) {
		MapContext mapContext = (MapContext) element.getObject();
		try {
			map = new MapControl(mapContext, element, getIndependentToolInstance(
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
	
	//View plugin is updated by EditorViewPlugIn
	public void update(Observable arg0, Object arg1) {
	}	

	public ViewPlugIn getView() {
		return this;
	}

	@Override
	public String getName() {
		return "Map Editor view";
	}
}
