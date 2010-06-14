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
package org.orbisgis.core.layerModel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.persistence.BoundingBox;
import org.orbisgis.core.layerModel.persistence.LayerCollectionType;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.layerModel.persistence.SelectedLayer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.orbisgis.core.renderer.se.Rule;

/**
 * Class that contains the status of the view.
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public class DefaultMapContext implements MapContext{
	private ILayer root;

	private ILayer[] selectedLayers = new ILayer[0];
	private Rule[] selectedRules = new Rule[0];

	private ArrayList<MapContextListener> listeners = new ArrayList<MapContextListener>();

	private OpenerListener openerListener;

	private LayerRemovalSourceListener sourceListener;

	private ILayer activeLayer;

	private boolean open = false;

	private org.orbisgis.core.layerModel.persistence.MapContext jaxbMapContext;

	private Envelope boundingBox;
	


	/**
	 * @param mapControl
	 */
	public DefaultMapContext() {
		openerListener = new OpenerListener();
		sourceListener = new LayerRemovalSourceListener();		
		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		setRoot(dataManager.createLayerCollection("root"));
		this.jaxbMapContext = null;
	}

	private void setRoot(ILayer newRoot) {
		if (this.root != null) {
			this.root.removeLayerListenerRecursively(openerListener);
		}
		this.root = newRoot;
		this.root.addLayerListenerRecursively(openerListener);
	}

	@Override
	public void addMapContextListener(MapContextListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMapContextListener(MapContextListener listener) {
		listeners.remove(listener);
	}

	@Override
	public ILayer getLayerModel() {
		checkIsOpen();
		return root;
	}

	@Override
	public Envelope getBoundingBox() {
		if (boundingBox != null) {
			return boundingBox;
		} else {
			return null;
		}
	}

	@Override
	public void setBoundingBox(Envelope envelope) {
		this.boundingBox = envelope;
	}

	@Override
	public ILayer[] getLayers() {
		checkIsOpen();
		return getLayerModel().getLayersRecursively();
	}

	@Override
	public Rule[] getSelectedRules() {
		checkIsOpen();
		return selectedRules;
	}


	@Override
	public ILayer[] getSelectedLayers() {
		checkIsOpen();
		return selectedLayers;
	}

	@Override
	public void setSelectedRules(ArrayList<Rule> selectedRules) {
		checkIsOpen();
		this.selectedRules = selectedRules.toArray(new Rule[0]);

		for (MapContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	@Override
	public void setSelectedLayers(ILayer[] selectedLayers) {
		checkIsOpen();
		ArrayList<ILayer> filtered = new ArrayList<ILayer>();
		for (ILayer layer : selectedLayers) {
			if (root.getLayerByName(layer.getName()) != null) {
				filtered.add(layer);
			}
		}
		this.selectedLayers = filtered.toArray(new ILayer[0]);

		for (MapContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	private final class OpenerListener extends LayerListenerAdapter implements
			LayerListener {

		@Override
		public void layerAdded(LayerCollectionEvent e) {
			if (isOpen()) {
				for (final ILayer layer : e.getAffected()) {
					try {
						layer.open();
						layer.addLayerListenerRecursively(openerListener);
					} catch (LayerException ex) {
						Services.getErrorManager().error(
								"Cannot open layer: " + layer.getName()
										+ ". The layer is removed from view.",
								ex);
						try {
							layer.getParent().remove(layer);
						} catch (LayerException e1) {
							Services.getErrorManager().error(
									"Cannot remove layer: " + layer.getName(),
									ex);
						}
					}
				}
			}
		}

		@Override
		public void layerRemoved(LayerCollectionEvent e) {
			HashSet<ILayer> newSelection = new HashSet<ILayer>();
			for (ILayer selectedLayer : selectedLayers) {
				newSelection.add(selectedLayer);
			}
			ILayer[] affected = e.getAffected();
			for (final ILayer layer : affected) {
				// Check active
				if (activeLayer == layer) {
					setActiveLayer(null);
				}

				// Check selection
				newSelection.remove(layer);
				layer.removeLayerListenerRecursively(openerListener);
				if (isOpen()) {
					try {
						layer.close();
					} catch (LayerException e1) {
						Services.getErrorManager().warning(
								"Cannot close layer: " + layer.getName(), e1);
					}
				}
			}

			selectedLayers = newSelection.toArray(new ILayer[0]);
		}
	}

	/**
	 * Creates a layer from the information obtained in the specified XML mapped
	 * object. Layers that cannot be created are removed from the layer tree and
	 * an error message is sent to the ErrorManager service
	 * 
	 * @param layer
	 * @return
	 */
	public ILayer recoverTree(LayerType layer,
			HashMap<ILayer, LayerType> layerPersistenceMap) {
		DataManager dataManager = (DataManager) Services
				.getService(DataManager.class);
		ILayer ret = null;
		
		
		if (layer instanceof LayerCollectionType) {
			LayerCollectionType xmlLayerCollection = (LayerCollectionType) layer;
			ret = dataManager.createLayerCollection(layer.getName());
			List<LayerType> xmlChildren = xmlLayerCollection.getLayer();
			for (LayerType layerType : xmlChildren) {
				ILayer lyr = recoverTree(layerType, layerPersistenceMap);
				if (lyr != null) {
					try {
						ret.addLayer(lyr);
					} catch (Exception e) {
						Services.getErrorManager().error(
								"Cannot add layer to collection: "
										+ lyr.getName(), e);
					}
				}
			}
		} else {
			try {
				ret = dataManager.createLayer(layer.getSourceName());
				layerPersistenceMap.put(ret, layer);
			} catch (LayerException e) {
				Services.getErrorManager().error(
						"Cannot recover layer: " + layer.getName(), e);
			}
		}
		return ret;
	}

	@Override
	public void draw(BufferedImage inProcessImage, Envelope extent,
			IProgressMonitor pm) {
		checkIsOpen();
		Renderer renderer = new Renderer();
		renderer.draw(inProcessImage, extent, getLayerModel(), pm);
	}

	private void checkIsOpen() {
		if (!isOpen()) {
			throw new IllegalStateException("The map is closed");
		}
	}

	@Override
	public ILayer getActiveLayer() {
		checkIsOpen();
		return activeLayer;
	}

	@Override
	public void setActiveLayer(ILayer activeLayer) {
		checkIsOpen();
		ILayer lastActive = this.activeLayer;
		this.activeLayer = activeLayer;
		for (MapContextListener listener : listeners) {
			listener.activeLayerChanged(lastActive, this);
		}
	}

	@Override
	public Object getJAXBObject() {
		if (jaxbMapContext != null) {
			return jaxbMapContext;
		} else {
			org.orbisgis.core.layerModel.persistence.MapContext xmlMapContext = new org.orbisgis.core.layerModel.persistence.MapContext();
			for (ILayer selected : selectedLayers) {
				SelectedLayer sl = new SelectedLayer();
				sl.setName(selected.getName());
				xmlMapContext.getSelectedLayer().add(sl);
			}
			LayerType xmlRootLayer = root.saveLayer();
			xmlMapContext
					.setLayerCollection((LayerCollectionType) xmlRootLayer);
			
			//get BoundinBox from ,persistence file
			BoundingBox boundingBox = new BoundingBox();
			if (getBoundingBox() != null) {
				GeometryFactory geomF= new GeometryFactory();
				Geometry geom = geomF.toGeometry(getBoundingBox());
				boundingBox.setName(geom.toText());				
			} else {
				boundingBox.setName("");
			}
			xmlMapContext.setBoundingBox(boundingBox);

			return xmlMapContext;
		}
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {
		if (isOpen()) {
			throw new IllegalStateException("The map must"
					+ " be closed to invoke this method");
		}
		org.orbisgis.core.layerModel.persistence.MapContext mapContext = (org.orbisgis.core.layerModel.persistence.MapContext) jaxbObject;

		this.jaxbMapContext = mapContext;
	}

	@Override
	public void close(IProgressMonitor pm) {

		checkIsOpen();

		jaxbMapContext = (org.orbisgis.core.layerModel.persistence.MapContext) getJAXBObject();

		// Close the layers
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		ILayer[] layers = this.root.getLayersRecursively();
		for (int i = 0; i < layers.length; i++) {
			pm.progressTo(i * 100 / layers.length);
			if (!layers[i].acceptsChilds()) {
				try {
					layers[i].close();
				} catch (LayerException e) {
					Services.getErrorManager().error(
							"Could not close layer: " + layers[i].getName());
				}
			}
		}
		this.root.removeLayerListenerRecursively(openerListener);

		// Listen source removal events
		DataManager dm = Services.getService(DataManager.class);
		dm.getSourceManager().removeSourceListener(sourceListener);

		this.open = false;
	}

	@Override
	public void open(IProgressMonitor pm) throws LayerException {

		if (isOpen()) {
			throw new IllegalStateException("The map is already open");
		}

		this.activeLayer = null;

		// Recover layer tree
		HashMap<ILayer, LayerType> layerPersistenceMap = null;
		if (jaxbMapContext != null) {
			LayerType layer = jaxbMapContext.getLayerCollection();
			layerPersistenceMap = new HashMap<ILayer, LayerType>();
			ILayer newRoot = recoverTree(layer, layerPersistenceMap);
			setRoot(newRoot);
			try {
				String boundingBoxValue = jaxbMapContext.getBoundingBox()
						.getName();

				if (!boundingBoxValue.equals("")) {
					boundingBox = new WKTReader().read(boundingBoxValue)
							.getEnvelopeInternal();
				} else {
					boundingBox = null;
				}
			} catch (ParseException e) {
				Services.getErrorManager().error(
						"Cannot read the bounding box", e);
			}
		}

		// Listen source removal events
		DataManager dm = Services.getService(DataManager.class);
		dm.getSourceManager().addSourceListener(sourceListener);

		// open layers
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		ILayer[] layers = this.root.getLayersRecursively();
		int i = 0;
		try {
			ArrayList<ILayer> toRemove = new ArrayList<ILayer>();
			for (; i < layers.length; i++) {
				pm.progressTo(i * 100 / layers.length);
				if (!layers[i].acceptsChilds()) {
					try {
						layers[i].open();
					} catch (LayerException e) {
						Services.getService(ErrorManager.class).warning(
								"Cannot open '" + layers[i].getName()
										+ "'. Layer is removed", e);
						toRemove.add(layers[i]);
					}
				}
				if (layerPersistenceMap != null) {
					if (!toRemove.contains(layers[i])) {
						try {
							layers[i].restoreLayer(layerPersistenceMap
									.get(layers[i]));
						} catch (LayerException e) {
							Services.getService(ErrorManager.class).warning(
									"Cannot restore '" + layers[i].getName()
											+ "'. Layer is removed", e);
							toRemove.add(layers[i]);
						}
					}
				}
			}

			for (ILayer layer : toRemove) {
				layer.getParent().remove(layer);
			}
		} catch (LayerException e) {
			for (int j = 0; j < i; j++) {
				pm.progressTo(j * 100 / i);
				if (!layers[j].acceptsChilds()) {
					try {
						layers[j].close();
					} catch (LayerException e1) {
						// ignore
					}
				}
			}

			throw e;
		}
		this.open = true;

		if (jaxbMapContext != null) {
			// Recover selected layers
			List<SelectedLayer> selectedLayerList = jaxbMapContext
					.getSelectedLayer();
			final ArrayList<ILayer> selected = new ArrayList<ILayer>();
			for (final SelectedLayer selectedLayer : selectedLayerList) {
				LayerCollection.processLayersNodes(root, new ILayerAction() {

					@Override
					public void action(ILayer layer) {
						if (selectedLayer.getName().equals(layer.getName())) {
							selected.add(layer);
							return;
						}
					}

				});
			}
			setSelectedLayers(selected.toArray(new ILayer[0]));
		}
		jaxbMapContext = null;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	private final class LayerRemovalSourceListener implements SourceListener {

		@Override
		public void sourceRemoved(final SourceRemovalEvent e) {
			LayerCollection.processLayersLeaves(root,
					new DeleteLayerFromResourceAction(e));
		}

		@Override
		public void sourceNameChanged(SourceEvent e) {
		}

		@Override
		public void sourceAdded(SourceEvent e) {
		}
	}

	private final class DeleteLayerFromResourceAction implements
			org.orbisgis.core.layerModel.ILayerAction {

		private ArrayList<String> resourceNames = new ArrayList<String>();

		private DeleteLayerFromResourceAction(SourceRemovalEvent e) {
			String[] aliases = e.getNames();
			resourceNames.addAll(Arrays.asList(aliases));
			resourceNames.add(e.getName());
		}

		public void action(ILayer layer) {
			String layerName = layer.getName();
			if (resourceNames.contains(layerName)) {
				try {
					layer.getParent().remove(layer);
				} catch (LayerException e) {
					Services.getErrorManager().error(
							"Cannot associate layer: " + layer.getName()
									+ ". The layer must be removed manually.");
				}
			}
		}
	}
}