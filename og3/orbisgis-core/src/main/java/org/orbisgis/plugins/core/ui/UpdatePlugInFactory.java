package org.orbisgis.plugins.core.ui;

import java.util.Observer;

import javax.swing.tree.TreePath;

import org.gdms.source.SourceManager;
import org.orbisgis.plugins.core.DataManager;
import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.geocognition.Geocognition;
import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.plugins.core.layerModel.ILayer;
import org.orbisgis.plugins.core.layerModel.MapContext;
import org.orbisgis.plugins.core.ui.editor.IEditor;
import org.orbisgis.plugins.core.ui.editors.map.MapControl;
import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionTree;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.editor.EditorManager;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.NewGeocognitionObject;
import org.orbisgis.plugins.core.ui.workbench.WorkbenchContext;

import com.vividsolutions.jts.util.Assert;

public class UpdatePlugInFactory {

	private WorkbenchContext workbenchContext;
	private AbstractPlugIn plugin;

	public UpdatePlugInFactory(WorkbenchContext workbenchContext, PlugIn plugIn) {
		Assert.isTrue(workbenchContext != null);
		this.plugin = (AbstractPlugIn) plugIn;
		workbenchContext.addObserver((Observer) plugIn);
		this.workbenchContext = workbenchContext;
	}

	/****** TOC PlugIns ******/
	// Set active or not All PlugIns actions about TOC
	public boolean checkLayerAvailability() {
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
	public boolean geocognitionIsVIsible() {
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

	/****** Tools PlugIns (visibility constraints) ******/
	public static void checkTool(Automaton automaton) {
		// System.out.println("Check " + automaton.getClass() );
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
}
