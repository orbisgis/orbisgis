package org.orbisgis.core.ui.plugins.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

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
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchToolBar;


public class MapEditorPlugIn extends ViewPlugIn implements WorkbenchFrame, 
		IEditor {

	private RootPanePanel mapEditor;
	private MapControl mapControl;
	private WorkbenchToolBar mapToolBar;	


	private String editors[];
	private EditableElement mapElement;
	private Automaton defaultTool;
	private String defaultMouseCursor;	
	
	


	// TODO (pyf): ajouter des plugins dans la popup
	private org.orbisgis.core.ui.pluginSystem.menu.MenuTree menuTree;

	public org.orbisgis.core.ui.pluginSystem.menu.MenuTree getMenuTreePopup() {
		return menuTree;
	}
	
	public MapEditorPlugIn() {
		mapEditor = new RootPanePanel ();
		mapControl = new MapControl();
		//mapToolBar = null;
	}

	public void initialize(PlugInContext context) throws Exception {		
		menuTree = new MenuTree();		
		Services.registerService(MapContextManager.class,
				"Gives access to the current MapContext",
				new DefaultMapContextManager());
		Services.registerService(ViewPlugIn.class,
				"Gives access to the current MapContext", this);
		
		Automaton defaultTool = (Automaton) Services.getService(Automaton.class);
		this.defaultTool = defaultTool;
		this.defaultMouseCursor = IconNames.ZOOMIN_ICON;
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
			mapControl = new MapControl(mapContext, element,
					getIndependentToolInstance(defaultTool, defaultMouseCursor));
			//TODO
		/*	mapControl.setMapContext(mapContext);
			mapControl.setElement(element);
			mapControl.setDefaultTool(getIndependentToolInstance(defaultTool, defaultMouseCursor));
			mapControl.initMapControl();	
			mapEditor.setContentPane(mapControl);
			mapToolBar.setPreferredSize(new Dimension(mapControl.getWidth(),25));
			mapToolBar.setFloatable(false);		
			mapEditor.add(mapToolBar, BorderLayout.PAGE_END);*/
					
			
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

	public String getDefaultMouseCursor() {
		return defaultMouseCursor;
	}

	public void setDefaultMouseCursor(String defaultMouseCursor) {
		this.defaultMouseCursor = defaultMouseCursor;
	}
	
	//View plugin is updated by EditorViewPlugIn
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
	
	public void setMapToolBar(WorkbenchToolBar mapToolBar) {
		this.mapToolBar = mapToolBar;
		
	}
	
	public WorkbenchToolBar getScaleToolBar() {
		return mapToolBar.getToolbars().get(Names.MAP_TOOLBAR_SCALE);
	}
	
	public WorkbenchToolBar getProjectionToolBar() {
		return mapToolBar.getToolbars().get(Names.MAP_TOOLBAR_PROJECTION);
	}

	public RootPanePanel getPane() {
		return mapEditor;
	}


}
