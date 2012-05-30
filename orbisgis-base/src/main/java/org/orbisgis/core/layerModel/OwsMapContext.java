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

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import net.opengis.ows._2.BoundingBoxType;
import net.opengis.ows_context.OWSContextType;
import net.opengis.ows_context.ObjectFactory;
import org.apache.log4j.Logger;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceEvent;
import org.gdms.source.SourceListener;
import org.gdms.source.SourceManager;
import org.gdms.source.SourceRemovalEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.ImageRenderer;
import org.orbisgis.core.renderer.Renderer;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.progress.ProgressMonitor;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;
/**
 * Class that contains the status of the map view.
 * 
 * 
 * 
 */
public class OwsMapContext extends BeanMapContext {

        private final static I18n I18N = I18nFactory.getI18n(OwsMapContext.class);
        private final static Logger LOGGER = Logger.getLogger(OwsMapContext.class);
 

        
	private ArrayList<MapContextListener> listeners = new ArrayList<MapContextListener>();
	private OpenerListener openerListener;
	private LayerRemovalSourceListener sourceListener;

	private boolean open = false;
	private OWSContextType jaxbMapContext=null; //Persistent form of the MapContext

	private long idTime;
	private boolean selectionInducedRefresh = false;
        
	/**
         * Default constructor
         */
	public OwsMapContext() {
		openerListener = new OpenerListener();
		sourceListener = new LayerRemovalSourceListener();
		DataManager dataManager = Services.getService(DataManager.class);
		layerModel=dataManager.createLayerCollection("root");
                
                //Create an empty map context
		createJaxbMapContext();
		idTime = System.currentTimeMillis();
	}

        
        
        //TODO Copy this methods header to map context interface
        //@Override
        /**
         * Return a layer compatible with this layer model
         * @param sds
         * @return A new layer, you can add this layer in the map context layer model
         * @throws LayerException 
         */
        public ILayer createLayer(DataSource sds) throws LayerException {
                int type = sds.getSource().getType();
                if ((type & SourceManager.WMS) == SourceManager.WMS) {
                        return new WMSLayer(sds.getName(), sds);
                } else {
                        boolean hasSpatialData = true;
                        if ((type & SourceManager.VECTORIAL) == 0 && 
                                (type & SourceManager.LIVE) == SourceManager.LIVE) {
                                int sfi;
                                try {
                                        sds.open();
                                        sfi = sds.getSpatialFieldIndex();
                                        try {
                                                sds.close();
                                        } catch (AlreadyClosedException e) {
                                                // ignore
                                                LOGGER.debug(I18N.tr("Cannot close the data source"), e);
                                        }
                                        hasSpatialData = (sfi != -1);
                                } catch (DriverException e) {
                                        throw new LayerException(I18N.tr("Cannot check source contents"), e);
                                }
                        }
                        if (hasSpatialData) {
                                return new Layer(sds.getName(), sds);
                        } else {
                                throw new LayerException("The source contains no spatial info");
                        }
                }
        }

        /**         * 
         * @param layerName
         * @todo Copy this methods header to map context interface
         * @return 
         */
        //@Override
	public ILayer createLayerCollection(String layerName) {
		return new LayerCollection(layerName);
	}

        @Override
	protected void setLayerModel(ILayer newRoot) {
		if (layerModel != null) {
			layerModel.removeLayerListenerRecursively(openerListener);
		}
		super.setLayerModel(newRoot);
		layerModel.addLayerListenerRecursively(openerListener);
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
		return super.getLayerModel();
	}

        @Override
	public long getIdTime() {
		return idTime;
	}


	@Override
	public ILayer[] getLayers() {
		checkIsOpen();
		return getLayerModel().getLayersRecursively();
	}

	@Override
	public Style[] getSelectedStyles() {
		checkIsOpen();
		return super.getSelectedStyles();
	}


	@Override
	public ILayer[] getSelectedLayers() {
		checkIsOpen();
		return super.getSelectedLayers();
	}

	@Override
	public void setSelectedStyles(ArrayList<Style> selectedRules) {
		checkIsOpen();
		super.setSelectedStyles(selectedRules.toArray(new Style[0]));
                //DEPRECATED LISTENERS
		for (MapContextListener listener : listeners) {
			listener.layerSelectionChanged(this);
		}
	}

	@Override
	public void setSelectedLayers(ILayer[] selectedLayers) {
		checkIsOpen();
		ArrayList<ILayer> filtered = new ArrayList<ILayer>();
		for (ILayer layer : selectedLayers) {
			if (layerModel.getLayerByName(layer.getName()) != null) {
				filtered.add(layer);
			}
		}
                super.setSelectedLayers(filtered.toArray(new ILayer[filtered.size()]));
                
                //DEPRECATED LISTENERS
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

	private final class OpenerListener extends LayerListenerAdapter {

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
						LOGGER.error(
							I18N.tr("Cannot open layer : {0} ",layer.getName()),ex);
						try {
							layer.getParent().remove(layer);
						} catch (LayerException e1) {
							LOGGER.error(
                                                       I18N.tr("Cannot remove the layer {0}",layer.getName()),ex);
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
						LOGGER.warn(I18N.tr("Cannot close layer {0}",layer.getName()), e1);
					}
				}
			}

			selectedLayers = newSelection.toArray(new ILayer[newSelection
					.size()]);
			// checkIfHasToResetSRID();
		}
	}

        /**
         * Implementation of public draw method
         * @param mt Contain the extent and the image to draw on
         * @param pm Object to report process and check the cancelled condition
         * @param layer Draw recursively this layer
         * @throws IllegalStateException
	 *             If the map is closed
         */
        private void drawImpl(MapTransform mt, ProgressMonitor pm,ILayer layer) throws IllegalStateException {
            checkIsOpen();
            Renderer renderer = new ImageRenderer();
            renderer.draw(mt, layer, pm);            
        }
        
        @Override
	public void draw(MapTransform mt, ProgressMonitor pm,ILayer layer) {
            //Layer must be from this layer model
            if(!isLayerFromThisLayerModel(layer)) {
                throw new IllegalStateException(I18N.tr("Layer provided for drawing is not from the map context layer model."));
            }
            drawImpl(mt,pm,layer);
        }
        
	@Override
	public void draw(MapTransform mt,
			ProgressMonitor pm) {
            drawImpl(mt,pm,getLayerModel());
	}

        /**
         * Search recursively for the specified layer in the layer model
         * @param layer Searched layer
         * @return True if the layer is in the map context layer model
         */
        private boolean isLayerFromThisLayerModel(ILayer layer) {
            ILayer[] allLayers = getLayerModel().getLayersRecursively();
            return Arrays.asList(allLayers).contains(layer);
        }
        
	private void checkIsOpen() {
		if (!isOpen()) {
			throw new IllegalStateException(
					I18N.tr("The map is not open")); //$NON-NLS-1$
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
                
                propertyChangeSupport.firePropertyChange(PROP_ACTIVELAYER, lastActive , activeLayer);
                //Deprecated listeners
		for (MapContextListener listener : listeners) {
			listener.activeLayerChanged(lastActive, this);
		}
	}
       
        private void createJaxbMapContext() {
            //Create jaxbcontext data
            ObjectFactory ows_context_factory = new ObjectFactory();
            net.opengis.ows._2.ObjectFactory ows_factory = new net.opengis.ows._2.ObjectFactory();

            jaxbMapContext = ows_context_factory.createOWSContextType();

            //GeneralType
            jaxbMapContext.setGeneral(ows_context_factory.createGeneralType());                   

            //GeneralType:Bounding Box
            if(boundingBox!=null) {
                BoundingBoxType bbox = ows_factory.createBoundingBoxType();
                bbox.getLowerCorner().add(boundingBox.getMinX());
                bbox.getLowerCorner().add(boundingBox.getMinY());
                bbox.getUpperCorner().add(boundingBox.getMaxX());
                bbox.getUpperCorner().add(boundingBox.getMaxY());
                jaxbMapContext.getGeneral().setBoundingBox(ows_factory.createBoundingBox(bbox));
            }            
        }
	@Override
	public Object getJAXBObject() {
		if(jaxbMapContext==null) {
                    createJaxbMapContext();
                }
                return jaxbMapContext;		
	}

	@Override
	public void setJAXBObject(Object jaxbObject) {
		if (isOpen()) {
			throw new IllegalStateException(
					I18N.tr("The map must be closed to invoke this method")); //$NON-NLS-1$
		}
                if(jaxbObject instanceof OWSContextType) {
                    this.jaxbMapContext = (OWSContextType) jaxbObject;
                } else {
                    LOGGER.error("The jaxb object must be an instance of OWSContextType");
                }
	}

	@Override
	public void close(ProgressMonitor pm) {

		checkIsOpen();

                //Backup Consistent data
		jaxbMapContext = (OWSContextType) getJAXBObject();

		// Close the layers
		if (pm == null) {
			pm = new NullProgressMonitor();
		}
		ILayer[] layers = layerModel.getLayersRecursively();
		for (int i = 0; i < layers.length; i++) {
			pm.progressTo(i * 100 / layers.length);
			if (!layers[i].acceptsChilds()) {
				try {
					layers[i].close();
				} catch (LayerException e) {
					LOGGER.error(I18N.tr("Cannot close layer {0}",layers[i].getName()));
				}
			}
		}
		layerModel.removeLayerListenerRecursively(openerListener);

		// Listen source removal events
		DataManager dm = Services.getService(DataManager.class);
		dm.getSourceManager().removeSourceListener(sourceListener);

		this.open = false;
	}

	@Override
	public void open(ProgressMonitor pm) throws LayerException {

		if (isOpen()) {
			throw new IllegalStateException(
					I18N.tr("The map is already open"));
		}

		this.activeLayer = null;
                //Read the specified jaxbMapContext
                setSelectedLayers(new ILayer[]{});
                setSelectedStyles(new ArrayList<Style>());
                if(jaxbMapContext!=null) {

                    List<Double> lowerCorner = jaxbMapContext.getGeneral().getBoundingBox().getValue().getLowerCorner();
                    List<Double> upperCorner = jaxbMapContext.getGeneral().getBoundingBox().getValue().getUpperCorner();
                    if(lowerCorner.size() >= 2 && upperCorner.size() >= 2) {
                        setBoundingBox(new Envelope(lowerCorner.get(0),upperCorner.get(0),lowerCorner.get(1),upperCorner.get(1)));
                    }
                    
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
			LayerCollection.processLayersLeaves(layerModel,
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

                @Override
		public void action(ILayer layer) {
			String layerName = layer.getName();
			if (resourceNames.contains(layerName)) {
				try {
					layer.getParent().remove(layer);
				} catch (LayerException e) {
					LOGGER.error(I18N.tr("Cannot associate layer {0}, this layer must be removed manually",layer.getName()),e);
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

        @Override
	public void checkSelectionRefresh(final int[] selectedRows,
			final int[] oldSelectedRows, final DataSource dataSource) {
		Envelope env;
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
