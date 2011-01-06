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

import java.util.ArrayList;

import javax.swing.tree.TreePath;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.OrbisGISPersitenceConfig;
import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.Geocognition;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.mapContext.GeocognitionException;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInCustomQuery;
import org.orbisgis.core.geocognition.sql.GeocognitionBuiltInFunction;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.ui.editor.IEditor;
import org.orbisgis.core.ui.editors.map.MapContextManager;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tool.ToolManager;
import org.orbisgis.core.ui.pluginSystem.workbench.FeatureInstaller;
import org.orbisgis.core.ui.pluginSystem.workbench.Names;
import org.orbisgis.core.ui.pluginSystem.workbench.WorkbenchContext;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.ViewDecorator;
import org.orbisgis.core.ui.plugins.views.editor.EditorManager;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionTree;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.EPGeocognitionWizardHelper;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.ui.plugins.views.geocognition.wizard.NewGeocognitionObject;

import com.vividsolutions.jts.util.Assert;

/**
 * Adapt context to OrbisGIS from OpenJump Project.
 * 
 * Passed to PlugIns to enable them to access the rest of the OrbisGIS
 * Workbench.
 * 
 * @see PlugIn
 */
public class PlugInContext {
	private WorkbenchContext workbenchContext;
	private FeatureInstaller featureInstaller;

	public PlugInContext(WorkbenchContext workbenchContext) {
		this.workbenchContext = workbenchContext;
		featureInstaller = new FeatureInstaller(workbenchContext);
	}

	public PlugInContext(WorkbenchContext context, PlugIn plugIn) {
		Assert.isTrue(context != null);
		this.workbenchContext = context;
	}

	public WorkbenchContext getWorkbenchContext() {
		return workbenchContext;
	}

	public FeatureInstaller getFeatureInstaller() {
		return featureInstaller;
	}

	/****** TOC PlugIns ******/

	public boolean checkLayerAvailability(
			SelectionAvailability[] selectionAvailability, int nbLayers,
			LayerAvailability[] layerAvailability) {
		// TODO - refactor
		MapContext mapContext = workbenchContext.getWorkbench().getFrame()
				.getToc().getMapContext();

		if (mapContext != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectionAvailability != null) {
				for (SelectionAvailability test : selectionAvailability) {
					switch (test) {
					case EQUAL:
						if (!(selectedLayers.length == nbLayers))
							return false;
						break;
					case SUPERIOR:
						if (!(selectedLayers.length > nbLayers))
							return false;
						break;
					case INFERIOR_EQUAL:
						if (!(selectedLayers.length <= nbLayers))
							return false;
						break;
					case ACTIVE_MAPCONTEXT:
						if (Services.getService(MapContextManager.class)
								.getActiveMapContext() == null)
							return false;
						break;
					default:
						break;
					}
				}
			}
			for (ILayer layer : selectedLayers) {
				if (layerAvailability != null) {
					for (LayerAvailability test : layerAvailability) {
						switch (test) {
						case VECTORIAL:
							if (!isVectorial(layer))
								return false;
							break;
						case RASTER:
							if (!isRaster(layer))
								return false;
							break;
						case ACCEPT_CHILDS:
							if (!(layer.acceptsChilds()))
								return false;
							break;
						case LAYER_NOT_NULL:
							if (layer == null)
								return false;
							break;
						case IS_MODIFIED:
							SpatialDataSourceDecorator dataSource = layer
									.getDataSource();
							if ((dataSource == null)
									|| !dataSource.isModified())
								return false;
							break;
						case DATASOURCE_NOT_NULL:
							if (layer.getDataSource() == null)
								return false;
							break;
						case ACTIVE_LAYER:
							if (mapContext.getActiveLayer() != layer)
								return false;
							break;
						case NOT_ACTIVE_LAYER:
							if (mapContext.getActiveLayer() == layer)
								return false;
							break;
						case IS_EDTABLE:
							if (!layer.getDataSource().isEditable())
								return false;
							break;
						case ROW_SELECTED:
							if (layer.getSelection().length <= 0)
								return false;
							break;
						default:
							break;
						}
					}

				}
			}
			return true;
		}
		return false;
	}

	public static enum SelectionAvailability {
		EQUAL, SUPERIOR, INFERIOR, INFERIOR_EQUAL, ACTIVE_MAPCONTEXT
	};

	public static enum LayerAvailability {
		VECTORIAL, RASTER,ACCEPT_CHILDS, LAYER_NOT_NULL, DATASOURCE_NOT_NULL, IS_MODIFIED, ACTIVE_LAYER, NOT_ACTIVE_LAYER, IS_EDTABLE, ROW_SELECTED
	};

	public MapContext getMapContext() {
		return workbenchContext.getWorkbench().getFrame().getToc()
				.getMapContext();
	}

	public boolean isVectorial(ILayer layer) {
		try {
			if (layer.isVectorial())
				return true;
		} catch (DriverException e) {
			return false;
		}
		return false;
	}

	public boolean isRaster(ILayer layer) {
		try {
			if (layer.isRaster())
				return true;
		} catch (DriverException e) {
			return false;
		}
		return false;
	}

	public ToolManager getToolManager() {
		ToolManager toolManager = null;
		if (getMapEditor() != null)
			toolManager = getMapEditor().getMapControl().getToolManager();
		return toolManager;
	}

	/****** Geocognition PlugIns ******/

	public boolean checkLayerAvailability(
			SelectionAvailability[] selectionAvailability, int nbElements,
			ElementAvailability[] elAvailability) {
		// TODO - refactor

		GeocognitionElement[] elements = getElements();
		if (selectionAvailability != null && elements != null) {
			for (SelectionAvailability test : selectionAvailability) {
				switch (test) {
				case EQUAL:
					if (!(elements.length == nbElements))
						return false;
					break;
				case SUPERIOR:
					if (!(elements.length > nbElements))
						return false;
					break;
				case INFERIOR_EQUAL:
					if (!(elements.length <= nbElements))
						return false;
					break;
				case ACTIVE_MAPCONTEXT:
					if (Services.getService(MapContextManager.class)
							.getActiveMapContext() == null)
						return false;
					break;
				default:
					break;
				}
			}
		}
		for (GeocognitionElement el : elements) {
			if (elAvailability != null) {
				for (ElementAvailability test : elAvailability) {
					switch (test) {
					case FOLDER:
						if (!el.isFolder())
							return false;
						break;
					case FOLDER_OR_NULL:
						if (!(el.isFolder() || el == null))
							return false;
						break;
					case HAS_EDITOR:
						EditorManager em = Services
								.getService(EditorManager.class);
						if (!em.hasEditor(el))
							return false;
						break;
					case CUSTOM_QUERY_IS_NOT_REGISTERED:
						if (OrbisGISPersitenceConfig.GEOCONGITION_CUSTOMQUERY_FACTORY_ID
								.equals(el.getTypeId())) {
							String registered = el.getProperties().get(
									GeocognitionBuiltInCustomQuery.REGISTERED);
							if ((registered != null)
									&& registered
											.equals(GeocognitionBuiltInCustomQuery.IS_NOT_REGISTERED)) {
								break;
							}
						}
						return false;
					case FUNCTION_QUERY_IS_NOT_REGISTERED:
						if (OrbisGISPersitenceConfig.GEOCOGNITION_FUNCTION_FACTORY_ID
								.equals(el.getTypeId())) {
							String registered = el.getProperties().get(
									GeocognitionBuiltInFunction.REGISTERED);
							if ((registered != null)
									&& registered
											.equals(GeocognitionBuiltInFunction.IS_NOT_REGISTERED)) {
								break;
							}
						}
						return false;
					case CUSTOM_QUERY_IS_REGISTERED:
						if (OrbisGISPersitenceConfig.GEOCONGITION_CUSTOMQUERY_FACTORY_ID
								.equals(el.getTypeId())) {
							String registered = el.getProperties().get(
									GeocognitionBuiltInCustomQuery.REGISTERED);
							if ((registered != null)
									&& registered
											.equals(GeocognitionBuiltInCustomQuery.IS_REGISTERED)) {
								break;
							}
						}
						return false;
					case FUNCTION_QUERY_IS_REGISTERED:
						if (OrbisGISPersitenceConfig.GEOCOGNITION_FUNCTION_FACTORY_ID
								.equals(el.getTypeId())) {
							String registered = el.getProperties().get(
									GeocognitionBuiltInFunction.REGISTERED);
							if ((registered != null)
									&& registered
											.equals(GeocognitionBuiltInFunction.IS_REGISTERED)) {
								break;
							}
						}
						return false;

					default:
						break;
					}
				}

			}
		}
		return true;
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

	public static enum ElementAvailability {
		FOLDER, FOLDER_OR_NULL, CUSTOM_QUERY_IS_NOT_REGISTERED, FUNCTION_QUERY_IS_NOT_REGISTERED, CUSTOM_QUERY_IS_REGISTERED, FUNCTION_QUERY_IS_REGISTERED, HAS_EDITOR
	};

	public GeocognitionElement[] getElements() {
		GeocognitionTree tree = workbenchContext.getWorkbench().getFrame()
				.getGeocognition().getTree();
		TreePath[] res = tree.getSelection();
		return tree.toElementArray(res);
	}

	public GeocognitionTree getTree() {
		return workbenchContext.getWorkbench().getFrame().getGeocognition()
				.getTree();
	}

	public Geocognition getGeocognition() {
		return Services.getService(Geocognition.class);
	}

	/****** Geocatalog PlugIns ******/

	public boolean checkLayerAvailability(
			SelectionAvailability[] selectionAvailability, int nbSrc,
			SourceAvailability[] sourceAvailability) {

		String[] res = getSelectedSources();
		DataManager dataManager = Services.getService(DataManager.class);
		SourceManager sourceManager = dataManager.getSourceManager();

		if (selectionAvailability != null && res != null) {
			for (SelectionAvailability test : selectionAvailability) {
				switch (test) {
				case EQUAL:
					if (!(res.length == nbSrc))
						return false;
					break;
				case SUPERIOR:
					if (!(res.length > nbSrc))
						return false;
					break;
				case INFERIOR_EQUAL:
					if (!(res.length <= nbSrc))
						return false;
					break;
				case ACTIVE_MAPCONTEXT:
					if (Services.getService(MapContextManager.class)
							.getActiveMapContext() == null)
						return false;
					break;
				default:
					break;
				}
			}
		}
		for (String src : res) {
			if (sourceAvailability != null) {
				Source source = null;
				for (SourceAvailability test : sourceAvailability) {
					switch (test) {
					case NODE_NOT_NULL:
						if (src == null)
							return false;
						break;
					case WMS:
						source = sourceManager.getSource(src);
						if (!((source.getType() & SourceManager.WMS) == 0))
							return false;
						break;
					case RASTER:
						source = sourceManager.getSource(src);
						if (!((source.getType() & SourceManager.RASTER) == 0))
							return false;
						break;
					default:
						break;
					}
				}

			}
		}
		return true;
	}

	public static enum SourceAvailability {
		NODE_NOT_NULL, WMS, RASTER, VECTOR
	};

	public String[] getSelectedSources() {
		return workbenchContext.getWorkbench().getFrame().getGeocatalog()
				.getSelectedSources();
	}

	/****** Editors **********************************/
	public EditorManager getEditorManager() {
		return Services.getService(EditorManager.class);
	}

	public IEditor getActiveEditor() {
		return getEditorManager().getActiveEditor();
	}

	public String getEditorId(IEditor editor) {
		return getEditorManager().getEditorId(getActiveEditor());
	}

	/****** Table Editor **********************************/

	public TableEditorPlugIn getTableEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor editor = em.getActiveEditor();
		if (Names.EDITOR_TABLE_ID.equals(em.getEditorId(editor))
				&& editor != null)
			return (TableEditorPlugIn) editor;
		return null;
	}

	/****** Map Editor **********************************/
	public MapEditorPlugIn getMapEditor() {
		EditorManager em = Services.getService(EditorManager.class);
		IEditor mapEditor = em.getActiveEditor();
		if (Names.EDITOR_MAP_ID.equals(em.getEditorId(mapEditor))
				&& mapEditor != null)
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
				check = automaton.isEnabled(mapContext, mapEditor
						.getMapControl().getToolManager());
				automaton.getButton().setEnabled(check);
				check = automaton.isVisible(mapContext, mapEditor
						.getMapControl().getToolManager());
				automaton.getButton().setVisible(check);
				automaton.getButton().setSelected(
						mapEditor.getMapControl().getTool().getClass().equals(
								automaton.getClass()));
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

	/*
	 * ************************* All prepared methods available for plug-in
	 * development ******************************************
	 */
	/**
	 * All layer types confuse one layer must exists
	 * 
	 * @return enable
	 */
	public boolean AtLeastOneLayerSelected() {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			return selectedLayers.length > 0;
		}
		return false;
	}

	/**
	 * Return enable if layer is editable
	 * 
	 * @return enable
	 */
	public boolean areSelectedLayersSelectedInEditing() {
		MapContext mapContext = null;
		SpatialDataSourceDecorator sds = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length > 0) {
				for (ILayer layer : selectedLayers) {
					if ((sds = layer.getDataSource()) != null
							&& !sds.isModified())
						return false;
				}
			}
		}
		return false;
	}

	/**
	 * Return enable if layer is under edition
	 * 
	 * @return enable
	 */
	public boolean areSelectedLayersSelectedEditable() {
		MapContext mapContext = null;
		SpatialDataSourceDecorator sds = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length > 0) {
				for (ILayer layer : selectedLayers) {
					if ((sds = layer.getDataSource()) != null
							&& !sds.isEditable())
						return false;
				}
			}
		}
		return true;
	}

	/*   **FOR VECTOR** */

	/**
	 * At least one layer of Vector Type must exists
	 * 
	 * @return enable
	 */
	public boolean AtLeastOneVectorLayerExists() {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] layers = mapContext.getLayers();
			if (layers.length > 0) {
				for (ILayer layer : layers) {
					if (isVectorial(layer))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * At least one layer of Vector Type must exists and selected
	 * 
	 * @return enable
	 */
	public boolean AtLeastOneVectorLayerSelected() {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length > 0) {
				for (ILayer layer : selectedLayers) {
					if (isVectorial(layer))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Number layers of Vector Type must be equal or greater than count
	 * 
	 * @return enable
	 */
	public boolean vectorLayersGreaterOrEqualThan(int count) {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] layers = mapContext.getLayers();
			if (layers.length >= count) {
				int i = 0;
				while (count != 0 && i < layers.length) {
					if (isVectorial(layers[i]))
						count--;
					i++;
				}
			}
		}
		if (count == 0)
			return true;
		else
			return false;
	}

	/**
	 * Number layers of Vector Type selected must be equal or greater than count
	 * 
	 * @return enable
	 */
	public boolean vectorLayersSelectedGreaterOrEqualThan(int count) {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length >= count) {
				int i = 0;
				while (count != 0 && i < selectedLayers.length) {
					if (isVectorial(selectedLayers[i]))
						count--;
					i++;
				}
			}
		}
		if (count == 0)
			return true;
		else
			return false;
	}

	/*   **FOR RASTER** */
	/**
	 * At least one layer of Raster Type must exists
	 * 
	 * @return enable
	 */
	public boolean AtLeastOneRasterLayerExists() {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] layers = mapContext.getLayers();
			if (layers.length > 0) {
				for (ILayer layer : layers) {
					if (isRaster(layer))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * At least one layer of Raster Type must exists and selected
	 * 
	 * @return enable
	 */
	public boolean AtLeastOneRasterLayerSelected() {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length > 0) {
				for (ILayer layer : selectedLayers) {
					if (isRaster(layer))
						return true;
				}
			}
		}
		return false;
	}

	/**
	 * Number layers of Raster Type must be equal or greater than count
	 * 
	 * @return enable
	 */
	public boolean rasterLayersGreaterOrEqualThan(int count) {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] layers = mapContext.getLayers();
			if (layers.length >= count) {
				int i = 0;
				while (count != 0 && i < layers.length) {
					if (isRaster(layers[i]))
						count--;
					i++;
				}
			}
		}
		if (count == 0)
			return true;
		else
			return false;
	}

	/**
	 * Number layers of Raster Type selected must be equal or greater than count
	 * 
	 * @return enable
	 */
	public boolean rasterLayersSelectedGreaterOrEqualThan(int count) {
		MapContext mapContext = null;
		if ((mapContext = getMapContext()) != null) {
			ILayer[] selectedLayers = mapContext.getSelectedLayers();
			if (selectedLayers.length >= count) {
				int i = 0;
				while (count != 0 && i < selectedLayers.length) {
					if (isRaster(selectedLayers[i]))
						count--;
					i++;
				}
			}
		}
		if (count == 0)
			return true;
		else
			return false;
	}

}
