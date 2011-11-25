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
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT
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
 * info@orbisgis.org
 */
package org.orbisgis.core.layerModel;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.errorManager.ErrorManager;
import org.orbisgis.core.layerModel.persistence.BoundingBox;
import org.orbisgis.core.layerModel.persistence.IdTime;
import org.orbisgis.core.layerModel.persistence.LayerCollectionType;
import org.orbisgis.core.layerModel.persistence.LayerType;
import org.orbisgis.core.layerModel.persistence.SelectedLayer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.utils.I18N;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.core.renderer.se.Rule;

/**
 * Class that contains the status of the map view.
 * 
 * 
 * 
 */
public class DefaultMapContext implements MapContext {

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
	private long idTime;
	private boolean selectionInducedRefresh = false;

	// private int srid = -1;

	/**
         */
	public DefaultMapContext() {
		openerListener = new OpenerListener();
		sourceListener = new LayerRemovalSourceListener();
		DataManager dataManager = Services.getService(DataManager.class);
		setRoot(dataManager.createLayerCollection("root")); //$NON-NLS-1$
		this.jaxbMapContext = null;
		idTime = System.currentTimeMillis();
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

	public long getIdTime() {
		return idTime;
	}

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
		this.selectedLayers = filtered.toArray(new ILayer[filtered.size()]);

		for (MapContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	/**
	 * @return the selectionInducedRefresh
	 */
	@Override
	public boolean isSelectionInducedRefresh() {
		return selectionInducedRefresh;
	}

	/**
	 * @param selectionInducedRefresh
	 *            the selectionInducedRefresh to set
	 */
	@Override
	public void setSelectionInducedRefresh(boolean selectionInducedRefresh) {
		this.selectionInducedRefresh = selectionInducedRefresh;
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
						// checking & possibly setting SRID
						// checkSRID(layer);
					} catch (LayerException ex) {
						Services
								.getErrorManager()
								.error(
										I18N
												.getString("orbisgis.org.orbisgis.defaultMapContext.cannotOpenLayer") + layer.getName() //$NON-NLS-1$
												+ I18N
														.getString("orbisgis.org.orbisgis.defaultMapContext.layerRemovedFromView"), //$NON-NLS-1$
										ex);
						try {
							layer.getParent().remove(layer);
						} catch (LayerException e1) {
							Services
									.getErrorManager()
									.error(
											I18N
													.getString("orbisgis.org.orbisgis.defaultMapContext.cannotRemoveLayer") + layer.getName(), //$NON-NLS-1$
											ex);
						}
					}
				}
			}
		}

		@Override
		public void layerRemoved(LayerCollectionEvent e) {
			HashSet<ILayer> newSelection = new HashSet<ILayer>();
			newSelection.addAll(Arrays.asList(selectedLayers));
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
						Services
								.getErrorManager()
								.warning(
										I18N
												.getString("orbisgis.org.orbisgis.defaultMapContext.cannotCloseLayer") + layer.getName(), e1); //$NON-NLS-1$
					}
				}
			}

			selectedLayers = newSelection.toArray(new ILayer[newSelection
					.size()]);
			// checkIfHasToResetSRID();
		}
	}

	// /**
	// * Reset if necessary the SRID of this MapContext
	// */
	// private void checkIfHasToResetSRID() {
	// // maybe we need to reset the SRID
	// if (getLayers().length == 0) {
	// // reset it
	// srid = -1;
	// }
	// }

	// /**
	// * Checks if the given layer can or cannot be added because of its SRID.
	// *
	// * If the methods does not throw an exception, the layer MUST be added.
	// * @param layer the layer to check
	// * @throws LayerException if the SRID of the layer is not compatible
	// * with the one of the MapContext
	// */
	// private void checkSRID(final ILayer layer) throws LayerException {
	// // getting the SRID of the layer
	// int layerSRID = -1;
	// try {
	// Metadata m = layer.getDataSource().getMetadata();
	// int spatialIndex = layer.getDataSource().getSpatialFieldIndex();
	// Constraint[] c =
	// m.getFieldType(spatialIndex).getConstraints(Constraint.SRID);
	// if (c.length != 0) {
	// layerSRID = Integer.parseInt(((SRIDConstaint)
	// c[0]).getConstraintValue());
	// }
	// } catch (DriverException ex) {
	// throw new LayerException(ex);
	// }
	//
	// if (srid == -1) {
	// if (layerSRID != -1) {
	// // this layer is ok, this SRID become the one and only one
	// // of the Mapcontext
	// srid = layerSRID;
	// return;
	// } else {
	// // nobody has a SRID...
	// return;
	// }
	// } else {
	// if (layerSRID != srid) {
	// // different SRIDs!! Problem
	// throw new LayerException("Cannot add a layer with SRID " + layerSRID +
	// "because the"
	// + "current MapContext has the SRID " + srid);
	// }
	// }
	// }

	/**
	 * Creates a layer from the information obtained in the specified XML mapped
	 * object. Layers that cannot be created are removed from the layer tree and
	 * an error message is sent to the ErrorManager service
	 * 
	 * @param layer
	 * @param layerPersistenceMap
	 * @return
	 */
	public ILayer recoverTree(LayerType layer,
			HashMap<ILayer, LayerType> layerPersistenceMap) {
		DataManager dataManager = Services.getService(DataManager.class);
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
						Services.getErrorManager().error(I18N
                                                        .getString(
                                                        "orbisgis.org.orbisgis."
                                                        + "defaultMapContext.cannotAddLayerToCollection") //$NON-NLS-1$
                                                        + lyr.getName(), e);
					}
				}
			}
		} else {
			try {
				ret = dataManager.createLayer(layer.getSourceName());
				layerPersistenceMap.put(ret, layer);
			} catch (LayerException e) {
				Services.getErrorManager().error(I18N.getString(
                                        "orbisgis.org.orbisgis.defaultMapContext.cannotRecoverLayer")
                                        + layer.getName(), e); //$NON-NLS-1$
			}
		}
		return ret;
	}

	@Override
	public void draw(MapTransform mt,
			ProgressMonitor pm) {
		checkIsOpen();
		Renderer renderer = new ImageRenderer();
		renderer.draw(mt, getLayerModel(), pm);
	}

	private void checkIsOpen() {
		if (!isOpen()) {
			throw new IllegalStateException(
					I18N.getString("orbisgis.core.ui.plugins."
                                + "views.geocognition.wizards.newMap")); //$NON-NLS-1$
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

			// Set the id time
			IdTime tempIdTime = new IdTime();
			tempIdTime.setName(getIdTime());

			xmlMapContext.setIdTime(tempIdTime);

			// get BoundinBox from ,persistence file
			BoundingBox boundingBox = new BoundingBox();
			if (getBoundingBox() != null) {
				GeometryFactory geomF = new GeometryFactory();
				Geometry geom = geomF.toGeometry(getBoundingBox());
				boundingBox.setName(geom.toText());
			} else {
				boundingBox.setName(""); //$NON-NLS-1$
			}
			xmlMapContext.setBoundingBox(boundingBox);

			for (ILayer selected : selectedLayers) {
				SelectedLayer sl = new SelectedLayer();
				sl.setName(selected.getName());
				xmlMapContext.getSelectedLayer().add(sl);
			}
			LayerType xmlRootLayer = root.saveLayer();
			xmlMapContext
					.setLayerCollection((LayerCollectionType) xmlRootLayer);

			/*
			 * OgcCrs ogcCrs = new OgcCrs(); if (getCoordinateReferenceSystem()
			 * == null) { ogcCrs.setName(""); } else { //
			 * ogcCrs.setName(crs.toWkt()); } xmlMapContext.setOgcCrs(ogcCrs);
			 */

			return xmlMapContext;
		}
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {
		if (isOpen()) {
			throw new IllegalStateException(
					I18N
							.getString("orbisgis.org.orbisgis.defaultMapContext.theMapMust") //$NON-NLS-1$
							+ I18N
									.getString("orbisgis.org.orbisgis.defaultMapContext.beClosedToInvokeThisMethod")); //$NON-NLS-1$
		}
		org.orbisgis.core.layerModel.persistence.MapContext mapContext = (org.orbisgis.core.layerModel.persistence.MapContext) jaxbObject;

		this.jaxbMapContext = mapContext;
	}

	@Override
	public void close(ProgressMonitor pm) {

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
					Services.getErrorManager().error(I18N.getString(""
                                                + "orbisgis.org.orbisgis.defaultMapContext.cannotCloseLayer")
                                                + layers[i].getName()); //$NON-NLS-1$
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
	public void open(ProgressMonitor pm) throws LayerException {

		if (isOpen()) {
			throw new IllegalStateException(
					I18N.getString("orbisgis.org.orbisgis.defaultMapContext.mapAlreadyOpen"));
                        //$NON-NLS-1$
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

				if (jaxbMapContext.getIdTime() != null) {
					idTime = jaxbMapContext.getIdTime().getName();
				}

				String boundingBoxValue = jaxbMapContext.getBoundingBox()
						.getName();

				if (!boundingBoxValue.isEmpty()) {
					boundingBox = new WKTReader().read(boundingBoxValue)
							.getEnvelopeInternal();
				} else {
					boundingBox = null;
				}
			} catch (ParseException e) {
				Services
						.getErrorManager()
						.error(
								I18N
										.getString("orbisgis.org.orbisgis.defaultMapContext.cannotReadBoundingBox"), e); //$NON-NLS-1$
			}
			/*
			 * /String wkt = jaxbMapContext.getOgcCrs().getName(); if (wkt !=
			 * null) { if (wkt.length() > 0) { crs =
			 * PRJUtils.getCRSFromWKT(wkt); } }
			 */
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
						Services
								.getService(ErrorManager.class)
								.warning(
										I18N
												.getString("orbisgis.org.orbisgis.defaultMapContext.cannotOpen") + layers[i].getName() //$NON-NLS-1$
												+ I18N
														.getString("orbisgis.org.orbisgis.defaultMapContext.layerIsRemoved"), e); //$NON-NLS-1$
						toRemove.add(layers[i]);
					}
				}
				if (layerPersistenceMap != null) {
					if (!toRemove.contains(layers[i])) {
						try {
							layers[i].restoreLayer(layerPersistenceMap
									.get(layers[i]));
						} catch (LayerException e) {
							Services
									.getService(ErrorManager.class)
									.warning(
											I18N
													.getString("orbisgis.org.orbisgis.defaultMapContext.cannotRestore") + layers[i].getName() //$NON-NLS-1$
													+ I18N
															.getString("orbisgis.org.orbisgis.defaultMapContext.layerIsRemoved"), e); //$NON-NLS-1$
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
			setSelectedLayers(selected.toArray(new ILayer[selected.size()]));
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
					Services
							.getErrorManager()
							.error(
									I18N
											.getString("orbisgis.org.orbisgis.defaultMapContext.cannotAssociateLayer") + layer.getName() //$NON-NLS-1$
											+ I18N
													.getString("orbisgis.org.orbisgis.defaultMapContext.layerMustRemovedManually")); //$NON-NLS-1$
				}
			}
		}
	}

	/*
	 * A mapcontext must have only one {@link CoordinateReferenceSystem} By
	 * default the crs is set to null.
	 */
	/*
	 * public CoordinateReferenceSystem getCoordinateReferenceSystem() { return
	 * crs; }
	 * 
	 * /** Set a {@link CoordinateReferenceSystem} to the mapContext
	 * 
	 * @param crs
	 *//*
		 * public void setCoordinateReferenceSystem(CoordinateReferenceSystem
		 * crs) { this.crs = crs; }
		 */

	public void checkSelectionRefresh(final int[] selectedRows,
			final int[] oldSelectedRows, final DataSource dataSource) {
		Envelope env = new Envelope();
		env = getBoundingBox();
		boolean mustUpdate = false;
		try {
			int geometryIndex = MetadataUtilities
					.getSpatialFieldIndex(dataSource.getMetadata());
			for (int i = 0; i < selectedRows.length; i++) {
				Geometry g = dataSource.getFieldValue(selectedRows[i],
						geometryIndex).getAsGeometry();
				if (g.getEnvelopeInternal().intersects(env)) {
					// geometry is on screen -> update
					mustUpdate = true;
					break;
				}
			}
			if (!mustUpdate) {
				for (int i = 0; i < oldSelectedRows.length; i++) {
					Geometry g = dataSource.getFieldValue(oldSelectedRows[i],
							geometryIndex).getAsGeometry();
					if (g.getEnvelopeInternal().intersects(env)) {
						// old geometry was on screen -> update
						mustUpdate = true;
						break;
					}
				}
			}

		} catch (DriverException ex) {
			mustUpdate = true;
		}
		setSelectionInducedRefresh(mustUpdate);
	}

}
