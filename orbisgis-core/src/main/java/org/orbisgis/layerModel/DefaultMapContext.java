/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at french IRSTV institute and is able
 * to manipulate and create vectorial and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geomatic team of
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
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
/**
 *
 */
package org.orbisgis.layerModel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orbisgis.DataManager;
import org.orbisgis.PersistenceException;
import org.orbisgis.Services;
import org.orbisgis.layerModel.persistence.LayerCollectionType;
import org.orbisgis.layerModel.persistence.LayerType;
import org.orbisgis.layerModel.persistence.SelectedLayer;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.renderer.Renderer;

import com.vividsolutions.jts.geom.Envelope;

/**
 * Class that contains the status of the view.
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public class DefaultMapContext implements MapContext {
	private ILayer root;

	private ILayer[] selectedLayers = new ILayer[0];

	public ArrayList<MapContextListener> listeners = new ArrayList<MapContextListener>();

	private OpenerListener openerListener;

	private ILayer activeLayer;

	/**
	 * @param mapControl
	 */
	public DefaultMapContext() {
		DataManager dataManager = (DataManager) Services
				.getService("org.orbisgis.DataManager");
		this.root = dataManager.createLayerCollection("root");
		openerListener = new OpenerListener();
		this.root.addLayerListenerRecursively(openerListener);

		// Listen source removal events
	}

	public void addMapContextListener(MapContextListener listener) {
		listeners.add(listener);
	}

	public void removeMapContextListener(MapContextListener listener) {
		listeners.remove(listener);
	}

	public ILayer getLayerModel() {
		return root;
	}

	public ILayer[] getLayers() {
		return getLayerModel().getChildren();
	}

	public ILayer[] getSelectedLayers() {
		return selectedLayers;
	}

	public void setSelectedLayers(ILayer[] selectedLayers) {
		this.selectedLayers = selectedLayers;
		for (MapContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	private final class OpenerListener implements LayerListener {
		public void visibilityChanged(LayerListenerEvent e) {
		}

		public void styleChanged(LayerListenerEvent e) {
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void layerMoved(LayerCollectionEvent e) {
		}

		public void layerAdded(LayerCollectionEvent e) {
			for (final ILayer layer : e.getAffected()) {
				try {
					layer.open();
					layer.addLayerListenerRecursively(openerListener);
				} catch (LayerException ex) {
					Services.getErrorManager().error(
							"Cannot open layer: " + layer.getName()
									+ ". The layer is removed from view.", ex);
					try {
						layer.getParent().remove(layer);
					} catch (LayerException e1) {
						Services.getErrorManager().error(
								"Cannot remove layer: " + layer.getName(), ex);
					}
				}
			}
		}

		public void layerRemoved(LayerCollectionEvent e) {
			HashSet<ILayer> newSelection = new HashSet<ILayer>();
			for (ILayer selectedLayer : selectedLayers) {
				newSelection.add(selectedLayer);
			}
			ILayer[] affected = e.getAffected();
			for (final ILayer layer : affected) {
				newSelection.remove(layer);
				layer.removeLayerListenerRecursively(openerListener);
				try {
					layer.close();
				} catch (LayerException e1) {
					Services.getErrorManager().warning(
							"Cannot close layer: " + layer.getName(), e1);
				}
			}

			selectedLayers = newSelection.toArray(new ILayer[0]);
		}

		public void dataChanged(ModificationEvent e) {
		}

		public void selectionChanged(SelectionEvent e) {
		}
	}

	public void saveStatus(File file, IProgressMonitor pm)
			throws PersistenceException {
		org.orbisgis.layerModel.persistence.MapContext vc = new org.orbisgis.layerModel.persistence.MapContext();
		for (ILayer selected : selectedLayers) {
			SelectedLayer sl = new SelectedLayer();
			sl.setName(selected.getName());
			vc.getSelectedLayer().add(sl);
		}
		LayerType xmlRootLayer = root.getStatus();
		vc.setLayerCollection((LayerCollectionType) xmlRootLayer);

		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.layerModel.persistence", this.getClass()
							.getClassLoader());
			PrintWriter printWriter = new PrintWriter(file);
			jc.createMarshaller().marshal(vc, printWriter);
			printWriter.close();
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save view context", e);
		} catch (FileNotFoundException e) {
			throw new PersistenceException("Cannot write the file: " + file);
		}

	}

	public void loadStatus(File file, IProgressMonitor pm)
			throws PersistenceException {
		try {
			JAXBContext jc = JAXBContext.newInstance(
					"org.orbisgis.layerModel.persistence", this.getClass()
							.getClassLoader());
			org.orbisgis.layerModel.persistence.MapContext mapContext = (org.orbisgis.layerModel.persistence.MapContext) jc
					.createUnmarshaller().unmarshal(file);

			LayerType layer = mapContext.getLayerCollection();
			ILayer layerCollection;
			try {
				DataManager dataManager = (DataManager) Services
						.getService("org.orbisgis.DataManager");
				layerCollection = dataManager.createLayer(layer);
				for (int i = 0; i < layerCollection.getLayerCount(); i++) {
					pm.progressTo(i * 100 / layerCollection.getLayerCount());
					ILayer newLayer = layerCollection.getLayer(i);
					try {
						root.addLayer(newLayer);
					} catch (LayerException e) {
						Services.getErrorManager().error(
								"Cannot add "
										+ "this layer to the collection: "
										+ newLayer.getName(), e);
					} catch (Exception e) {
						Services.getErrorManager().error(
								"Cannot add layer to collection: "
										+ newLayer.getName(), e);
					}
				}
			} catch (LayerException e1) {
				Services.getErrorManager().error("Cannot recover layer tree",
						e1);
			}

			List<SelectedLayer> selectedLayerList = mapContext
					.getSelectedLayer();
			final ArrayList<ILayer> selected = new ArrayList<ILayer>();
			for (final SelectedLayer selectedLayer : selectedLayerList) {
				LayerCollection.processLayersNodes(root, new ILayerAction() {

					public void action(ILayer layer) {
						if (selectedLayer.getName().equals(layer.getName())) {
							selected.add(layer);
							return;
						}
					}

				});
			}
			setSelectedLayers(selected.toArray(new ILayer[0]));
		} catch (JAXBException e) {
			throw new PersistenceException("Cannot save view context", e);
		}

	}

	public void draw(BufferedImage inProcessImage, Envelope extent,
			IProgressMonitor pm) {
		Renderer renderer = new Renderer();
		renderer.draw(inProcessImage, extent, getLayerModel(), pm);
	}

	public ILayer getActiveLayer() {
		return activeLayer;
	}

	public void setActiveLayer(ILayer activeLayer) {
		this.activeLayer = activeLayer;
	}
}