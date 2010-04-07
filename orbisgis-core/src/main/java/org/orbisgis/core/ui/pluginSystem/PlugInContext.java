package org.orbisgis.core.ui.pluginSystem;

import java.util.ArrayList;
import java.util.Observer;

import javax.swing.tree.TreePath;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.editors.map.MapControl;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.geocognition.GeocognitionTree;
import org.orbisgis.core.ui.pluginSystem.workbench.FeatureInstaller;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.ViewDecorator;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.NewGeocognitionObject;

import com.vividsolutions.jts.util.Assert;

public class PlugInContext {
	private WorkbenchContext workbenchContext;
	private FeatureInstaller featureInstaller;
	private AbstractPlugIn plugin;

	public PlugInContext(WorkbenchContext workbenchContext) {
		this.workbenchContext = workbenchContext;
		featureInstaller = new FeatureInstaller(workbenchContext);
	}

	public PlugInContext(WorkbenchContext context, AbstractPlugIn plugIn) {
		Assert.isTrue(context != null);
		this.plugin = plugIn;
		this.workbenchContext = context;
		workbenchContext.getPopupPlugInObservers().add(plugIn);	
	}

	public PlugInContext(WorkbenchContext context, PlugIn plugIn) {
		Assert.isTrue(context != null);
		this.workbenchContext = context;
		workbenchContext.getViewsPlugInObservers().add(plugIn);
	}

	public WorkbenchContext getWorkbenchContext() {
		return workbenchContext;
	}

	public FeatureInstaller getFeatureInstaller() {
		return featureInstaller;
	}

	/****** TOC PlugIns ******/
	// Set active or not All PlugIns actions about TOC
	public boolean checkLayerAvailability() {
		// TODO - refactor
		MapContext mapContext = workbenchContext.getWorkbench().getFrame()
				.getToc().getMapContext();
		if (mapContext != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			boolean acceptsAllResources = true;
			if (plugin.acceptsSelectionCount(selectedLayers.length)) {
				for (ILayer layer : selectedLayers) {
					if (!plugin.accepts(mapContext, layer)) {
						acceptsAllResources = false;
					}
				}
			} else {
				acceptsAllResources = false;
			}

			return acceptsAllResources;
		} else {
			return false;
		}
	}
	

	public boolean checkLayerAvailability(LayerSelectionTest[] layerSelectionTests, int nbLayers,
			LayerTest[] layerTests, boolean tableEditor) {		
		// TODO - refactor
		MapContext mapContext = workbenchContext.getWorkbench().getFrame()
				.getToc().getMapContext();
		
		if (mapContext != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			for(LayerSelectionTest test : layerSelectionTests) {
				switch( test ) {
					case EQUAL :
						if(!(selectedLayers.length == nbLayers)) return false;
						break;
					case SUPERIOR :
						if(!(selectedLayers.length > nbLayers)) return false;
						break;
					case INFERIOR_EQUAL :
						if(!(selectedLayers.length <= nbLayers)) return false;
						break;
					case ACTIVE_MAPCONTEXT :
						if(Services.getService(MapContextManager.class).getActiveMapContext() == null) 
							return false;
						break;
					default:
						break;
				}
			}
			for (ILayer layer : selectedLayers) {
				for(LayerTest test : layerTests) {
					switch( test ) {
						case VECTORIAL :
							try {							
								if(tableEditor) 
									/* get Nb row selected in table editor */
									if(!(layer.getSelection().length > 0)) return false;
								if(!layer.isVectorial())	return false;							
							} catch (DriverException e) {
								Services.getService(ErrorManager.class).error(
										"Cannot compute layer availability", e);
							}
							break;
						case ACCEPT_CHILDS:
							if(!(layer.acceptsChilds())) return false;
							break;
						case LAYER_NOT_NULL:
							if(layer == null) return false;
							break;
						case IS_MODIFIED:
							SpatialDataSourceDecorator dataSource = layer.getDataSource();
							if( (dataSource == null) || !dataSource.isModified() ) return false;
							break;
						case DATASOURCE_NOT_NULL:
							if( layer.getDataSource() == null ) return false;
							break;
						case ACTIVE_LAYER:
							if( mapContext.getActiveLayer() != layer) return false;
							break;
						case NOT_ACTIVE_LAYER:
							if( mapContext.getActiveLayer() == layer) return false;
							break;
						case IS_EDTABLE:
							if( !layer.getDataSource().isEditable() ) return false;
						default:
							break;
					}
				}
				
			}			
			return true;
		} 
		return false;
		
	}
	
	
	// Run PlugIns actions about TOC
	public void executeLayers() {
		MapContext mapContext = workbenchContext.getWorkbench().getFrame()
				.getToc().getMapContext();
		ILayer[] selectedResources = mapContext.getSelectedLayers();

		if (selectedResources.length == 0) {
			plugin.execute(mapContext, null);
		} else {
			for (ILayer resource : selectedResources) {
				plugin.execute(mapContext, resource);
			}
		}
		workbenchContext.setLastAction("update");
	}

	// Several layers selected in TOC?
	public boolean IsMultipleLayer() {
		MapContext mapContext = workbenchContext.getWorkbench().getFrame()
				.getToc().getMapContext();
		ILayer[] layers = null;
		if (mapContext != null)
			layers = mapContext.getSelectedLayers();
		else
			return false;
		return plugin.acceptsAll(layers);
	}

	/****** Geocognition PlugIns ******/
	// Set active or not All PlugIns actions about Geocognition
	public boolean geocognitionIsVisible() {
		GeocognitionTree tree = workbenchContext.getWorkbench().getFrame()
				.getGeocognition().getTree();
		Geocognition geocog = Services.getService(Geocognition.class);
		TreePath[] res = tree.getSelection();
		GeocognitionElement[] elements = tree.toElementArray(res);
		boolean allAccepted = true;
		if (!plugin.acceptsSelectionCount(geocog, elements.length)) {
			allAccepted = false;
		} else {
			for (GeocognitionElement selectedElement : elements) {
				if (!plugin.accepts(geocog, selectedElement)) {
					allAccepted = false;
					break;
				}
			}
		}

		return allAccepted;
	}

	// Run PlugIns actions about Geocognition
	public void executeGeocognition() {
		GeocognitionTree tree = workbenchContext.getWorkbench().getFrame()
				.getGeocognition().getTree();
		Geocognition geocog = Services.getService(Geocognition.class);
		GeocognitionElement[] elements = GeocognitionTree.toElementArray(tree
				.getSelection());
		if (elements.length == 0) {
			plugin.execute(geocog, null);
		} else {
			for (GeocognitionElement element : elements) {
				plugin.execute(geocog, element);
			}
		}
	}

	// Run PlugIns actions from one Geocognition element
	public void executeGeocognitionElement(INewGeocognitionElement element) {
		EPGeocognitionWizardHelper wh = new EPGeocognitionWizardHelper();
		GeocognitionTree tree = workbenchContext.getWorkbench().getFrame()
				.getGeocognition().getTree();
		TreePath[] parents = tree.getSelection();
		try {
			element.runWizard();
		} catch (GeocognitionException e) {
			Services.getErrorManager().error("Cannot generate element", e);
		}
		NewGeocognitionObject[] objs = new NewGeocognitionObject[element
				.getElementCount()];
		for (int i = 0; i < element.getElementCount(); i++) {
			NewGeocognitionObject newGeocognitionObject;
			String name = element.getFixedName(i);
			if (name == null) {
				name = element.getBaseName(i);
				newGeocognitionObject = new NewGeocognitionObject(name, element
						.getElement(i));
			} else {
				newGeocognitionObject = new NewGeocognitionObject(name, element
						.getElement(i));
				newGeocognitionObject.setFixedName(true);
			}
			newGeocognitionObject.setUniqueId(element.isUniqueIdRequired(i));
			objs[i] = newGeocognitionObject;
		}
		if (objs != null) {
			if (parents.length == 0) {
				wh.addElements(objs, "");
			} else {
				GeocognitionElement parent = (GeocognitionElement) parents[0]
						.getLastPathComponent();
				String parentPath = parent.getIdPath();
				wh.addElements(objs, parentPath);
			}
		}
	}

	/****** Geocatalog PlugIns ******/
	// Set active or not All PlugIns actions about Geocatalog
	public boolean geocatalogIsVisible() {
		String[] res = workbenchContext.getWorkbench().getFrame()
				.getGeocatalog().getSelectedSources();
		DataManager dataManager = Services.getService(DataManager.class);
		SourceManager sourceManager = dataManager.getSourceManager();
		boolean acceptsAllSources = true;
		if (plugin.acceptsSelectionCount(res.length)) {
			for (String source : res) {
				try {
					if (!plugin.accepts(sourceManager, source)) {
						acceptsAllSources = false;
						break;
					}
				} catch (Throwable t) {
					acceptsAllSources = false;
					Services.getErrorManager().error(
							"Error getting pop up : " + t);
				}
			}
		} else {
			acceptsAllSources = false;
		}
		return acceptsAllSources;
	}

	// Run PlugIns actions from one Geocatalog
	public void executeGeocatalog() {
		DataManager dm = Services.getService(DataManager.class);
		String[] res = workbenchContext.getWorkbench().getFrame()
				.getGeocatalog().getSelectedSources();
		if (res.length == 0) {
			plugin.execute(dm.getSourceManager(), null);
		} else {
			for (String resource : res) {
				plugin.execute(dm.getSourceManager(), resource);
			}
		}
	}
	/****** Editors **********************************/
	public EditorManager getEditorManager(){
		return Services.getService(EditorManager.class);
	}
	
	public IEditor getActiveEditor(){		
		return getEditorManager().getActiveEditor();
	}
	
	public String getEditorId(IEditor editor){
		return getEditorManager().getEditorId(getActiveEditor());
	}
	
	/****** Table Editor **********************************/
	
	public TableEditorPlugIn getTableEditor(){
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if(Names.EDITOR_TABLE_ID.equals(em.getEditorId(editor)) && editor != null)
			return (TableEditorPlugIn) editor;
		return null;		
	}

	/****** Map Editor **********************************/	
	public MapEditorPlugIn getMapEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor mapEditor = em.getActiveEditor();
		if (Names.EDITOR_MAP_ID.equals(em.getEditorId(mapEditor)) && mapEditor != null)
			return (MapEditorPlugIn) mapEditor;		
		return null;
	}

	/****** Tools PlugIns (visibility constraints) ******/
	public static void checkTool(Automaton automaton) {
		boolean check = false;
		EditorManager em = (EditorManager) Services
				.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if (editor == null || !(editor instanceof MapEditorPlugIn))
			automaton.getButton().setEnabled(false);
		else {
			MapEditorPlugIn mapEditor = (MapEditorPlugIn) editor;
			MapContext mapContext = (MapContext) mapEditor.getElement()
					.getObject();
			if (mapContext != null) {
				check = automaton.isEnabled(mapContext, ((MapControl) mapEditor
						.getComponent()).getToolManager());
				automaton.getButton().setEnabled(check);
				check = automaton.isVisible(mapContext, ((MapControl) mapEditor
						.getComponent()).getToolManager());
				automaton.getButton().setVisible(check);
				automaton.getButton().setSelected(
						((MapControl) mapEditor.getComponent()).getTool()
								.getClass().equals(automaton.getClass()));
			}
		}
	}

	/****** ViewPlugIn : Open/Close View PlugIn in docking window environment ******/
	public void loadView(String id) {
		if (getViewDecorator(
				workbenchContext.getWorkbench().getFrame().getViews(), id)
				.isOpen()) {
			getViewDecorator(
					workbenchContext.getWorkbench().getFrame().getViews(), id)
					.close();
		} else {
			EditorManager em = Services.getService(EditorManager.class);
			IEditor activeEditor = em.getActiveEditor();
			getViewDecorator(
					workbenchContext.getWorkbench().getFrame().getViews(), id)
					.open(workbenchContext.getWorkbench().getFrame().getRoot(),
							activeEditor, em.getEditorId(activeEditor));
		}
	}

	public boolean viewIsOpen(String id) {
		return getViewDecorator(
				workbenchContext.getWorkbench().getFrame().getViews(), id)
				.isOpen();
	}

	private ViewDecorator getViewDecorator(ArrayList<ViewDecorator> views,
			String id) {
		for (ViewDecorator view : views) {
			if (view.getId().equals(id)) {
				return view;
			}
		}
		return null;
	}
	
	 public static enum LayerTest {
	        VECTORIAL,
	        ACCEPT_CHILDS,
	        LAYER_NOT_NULL,
	        DATASOURCE_NOT_NULL,
	        IS_MODIFIED,
	        ACTIVE_LAYER,
	        NOT_ACTIVE_LAYER,
	        IS_EDTABLE,
	 };
	 
	 public static enum LayerSelectionTest {
		 	EQUAL,
	        SUPERIOR,
	        INFERIOR, 
	        INFERIOR_EQUAL, 
	        ACTIVE_MAPCONTEXT
	 };

}
