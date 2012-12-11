/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.map;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import org.apache.log4j.Logger;
import org.gdms.data.ClosedDataSourceException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.orbisgis.core.Services;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.DefaultJobId;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.ToolListener;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.ZoomInTool;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * MapControl.
 * 
 */

public class MapControl extends JComponent implements ContainerListener {
        //Minimal Time in ms between two intermediate paint of drawing process
        private static final long INTERMEDIATE_DRAW_PAINT_INTERVAL = 200;
        public static final String JOB_DRAWING_PREFIX_ID = "MapControl-Drawing";
        private static final Logger LOGGER = Logger.getLogger(MapControl.class);
        private static final I18n I18N = I18nFactory.getI18n(MapControl.class);
	private static int lastMapControlId = 0;
        private static final long serialVersionUID = 1L;
        private AtomicBoolean awaitingDrawing=new AtomicBoolean(false); /*!< A drawing process is currently requested, it is useless to request another */
	private int mapControlId;

	/** The map will draw the last generated image without querying the data. */
	public static final int UPDATED = 0;

	/** The map will query the data to obtain a new image. */
	public static final int DIRTY = 1;
        private RefreshLayerListener refreshLayerListener = new RefreshLayerListener();

	private int status = DIRTY;

	private ToolManager toolManager;

	private Color backColor = Color.white;

	private MapTransform mapTransform = new MapTransform();

	private MapContext mapContext;

	private Drawer drawer;

	private boolean showCoordinates = true;

	TransformListener element;

	Automaton defaultTool;
        
        PropertyChangeListener boundingBoxPropertyListener = EventHandler.create(PropertyChangeListener.class,this,"onMapContextBoundingBoxChange");
        
        BufferedImage updatedImage=null; /*!< The last drawn image paint, shown when the status of the mapTransform is dirty */

	public MapControl() {
            defaultTool = new ZoomInTool();
	}
        private void setStatus(int newStatus) {
            status = newStatus;
        }
        
        /**
         * The bounding box of the map context need 
         * to be read and applied to the MapTransform
         */
        public void onMapContextBoundingBoxChange() {
                Envelope boundingBox = mapContext.getBoundingBox();
                if (boundingBox != null) {
                        mapTransform.setExtent(boundingBox);
                }
        }
        
        
	final public void initMapControl() throws TransitionException {
		synchronized (this) {
			this.mapControlId = lastMapControlId++;
		}
		setDoubleBuffered(true);
		setOpaque(true);
		setStatus(DIRTY);

                // creating objects
                if(toolManager!=null) {
                        removeMouseListener(toolManager);
                        removeMouseMotionListener(toolManager);
                        removeMouseWheelListener(toolManager);
                }
		toolManager = new ToolManager(defaultTool, mapContext, mapTransform,
				this);

                // Set extent with BoundingBox value
		ILayer rootLayer = mapContext.getLayerModel();
		Envelope boundingBox = mapContext.getBoundingBox();
		if (boundingBox != null) {
			mapTransform.setExtent(boundingBox);
		} else {
			mapTransform.setExtent(rootLayer.getEnvelope());
		}

                setLayout(new BorderLayout());
                
                // adding listeners at the endupdatedImage
                // to prevent multiple useless repaint
		toolManager.addToolListener(new MapToolListener());
		addMouseListener(toolManager);
                addMouseMotionListener(toolManager);
                addMouseWheelListener(toolManager);
                
		mapTransform.addTransformListener(new MapControlTransformListener());
                
                //Component event invalidate the picture
                this.addComponentListener(EventHandler.create(ComponentListener.class, this,"invalidateImage"));
		// Add editable element listen transform event
                if(element!=null) {
                        mapTransform.addTransformListener(element);
                }

	}

	private void addLayerListenerRecursively(ILayer rootLayer,
			RefreshLayerListener refreshLayerListener) {
		rootLayer.addLayerListener(refreshLayerListener);
		DataSource dataSource = rootLayer.getDataSource();
		if (dataSource != null) {
                        if (dataSource.isEditable()) {
                                try {
                                        dataSource.addEditionListener(refreshLayerListener);
                                } catch (UnsupportedOperationException ex) {
                                        LOGGER.warn(I18N.tr("The MapEditor cannot listen to source modifications"), ex);
                                }
                        }
			dataSource.addDataSourceListener(refreshLayerListener);
		}
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			addLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}

	private void removeLayerListenerRecursively(ILayer rootLayer,
			RefreshLayerListener refreshLayerListener) {
		rootLayer.removeLayerListener(refreshLayerListener);
		DataSource dataSource = rootLayer.getDataSource();
		if (dataSource != null) {
                        if (dataSource.isEditable()) {
                                try {
                                        dataSource.removeEditionListener(refreshLayerListener);
                                } catch (UnsupportedOperationException ex) {
                                        // ignore
                                }
                        }
			dataSource.removeDataSourceListener(refreshLayerListener);
		}
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			removeLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}
        
	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
        @Override
	protected void paintComponent(Graphics g) {
		BufferedImage mapTransformImage = mapTransform.getImage();

                // we always fill the Graphics with an opaque color
                // before drawing anything.
                g.setColor(backColor);
                g.fillRect(0, 0, getWidth(), getHeight());
		// then we render on top the already computed image
		// if it exists
		if (mapTransformImage != null && status == UPDATED) {
                    updatedImage = mapTransformImage;
		}
                    
                if(updatedImage!=null){
                    g.drawImage(updatedImage, 0, 0, null);
                    toolManager.paintEdition(g);
                }

		// if the image itself is dirty
		if (status == DIRTY && mapContext!=null) {
                    if(!awaitingDrawing.getAndSet(true)) {
                        setStatus(UPDATED);
			// is never null, except at first loading with no layer
			// in that case we do not draw anything
				int width = this.getWidth();
				int height = this.getHeight();

				// getting an image to draw in
				GraphicsConfiguration configuration = GraphicsEnvironment
						.getLocalGraphicsEnvironment().getDefaultScreenDevice()
						.getDefaultConfiguration();
				BufferedImage inProcessImage = configuration
						.createCompatibleImage(width, height,
								BufferedImage.TYPE_INT_ARGB);

				Graphics gImg = inProcessImage.createGraphics();

				// filling image
				gImg.setColor(backColor);
				gImg.fillRect(0, 0, getWidth(), getHeight());

				// this is the new image
				// mapTransform will update the AffineTransform
				mapTransform.setImage(inProcessImage);

				// now we start the actual drawer
				drawer = new Drawer();
				BackgroundManager bm = Services.getService(BackgroundManager.class);
                                bm.nonBlockingBackgroundOperation(
                                        new DefaultJobId(JOB_DRAWING_PREFIX_ID +
                                        mapControlId), drawer);
                    } else {
                        // Currently drawing with a mix of old and new map context !
                        // Stop the drawing
                        // The drawer will call paint when it will release the awaitingDrawing
                        drawer.cancel();
                    }
		}
	}

	/**
	 * Returns the drawn image
	 * 
	 * @return imagen.
	 */
	public BufferedImage getImage() {
		return mapTransform.getImage();
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public void setTool(Automaton tool) throws TransitionException {
		toolManager.setTool(tool);
	}
        
	public void invalidateImage() {
		setStatus(DIRTY);
		repaint();
	}

	public class Drawer implements BackgroundJob {

		private boolean cancel = false;
		private CancellablePM pm;

		public Drawer() {
		}

                @Override
		public String getTaskName() {
			return I18N.tr("Drawing");
		}

                @Override
		public void run(ProgressMonitor pm) {
			synchronized (this) {
                                this.pm = new CancellablePM(cancel, pm);
                                pm = this.pm;
                        }
                        try {
                                mapContext.draw(mapTransform, pm);
                        } catch (ClosedDataSourceException e) {
                                invalidateImage();
                                if (!cancel) {
                                        throw e;
                                }
                        } finally {
                                awaitingDrawing.set(false);
                                MapControl.this.repaint();
                        }
                }

		public void cancel() {
			synchronized (this) {
                                if(!cancel) {
                                    LOGGER.debug("Cancel drawing !");
                                }
				if (pm != null) {
					pm.cancel = true;
					cancel = true;
				} else {
					cancel = true;
				}
			}
		}
	}

	private class CancellablePM implements ProgressMonitor {

		private ProgressMonitor decoratedPM;
		private boolean cancel;
                private long lastIntermediateDrawPaint = System.currentTimeMillis();

		public CancellablePM(boolean cancel, ProgressMonitor pm) {
			this.decoratedPM = pm;
			this.cancel = cancel;
		}

                @Override
		public void endTask() {
			decoratedPM.endTask();
		}

                @Override
		public int getCurrentProgress() {
			return decoratedPM.getCurrentProgress();
		}

                @Override
		public String getCurrentTaskName() {
			return decoratedPM.getCurrentTaskName();
		}

                @Override
		public int getOverallProgress() {
			return decoratedPM.getOverallProgress();
		}

                @Override
		public void init(String taskName, long i) {
			decoratedPM.init(taskName, i);
		}

                @Override
		public boolean isCancelled() {
			return cancel || decoratedPM.isCancelled();
		}

                @Override
		public void progressTo(long progress) {
			decoratedPM.progressTo(progress);
		}

                @Override
		public void startTask(String taskName, long i) {
			decoratedPM.startTask(taskName, i);
                        //Show the current state of the drawing on new task
                        long curTime = System.currentTimeMillis();
                        if(!cancel && status != DIRTY && curTime-lastIntermediateDrawPaint > INTERMEDIATE_DRAW_PAINT_INTERVAL) {
                            lastIntermediateDrawPaint=curTime;
                            LOGGER.debug("Task "+taskName+" paint the drawing");
                            MapControl.this.repaint();
                        }
		}

                @Override
                public void setCancelled(boolean cancelled) {
                        throw new UnsupportedOperationException("Not supported yet.");
                }

	}

	public MapTransform getMapTransform() {
		return mapTransform;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}
        /**
         * 
         * @return The current used tool
         */
	public Automaton getTool() {
            if(toolManager!=null) {
                return toolManager.getTool();
            }else{
                return defaultTool;
            }
	}

	private class RefreshLayerListener implements LayerListener,
			EditionListener, DataSourceListener {
                @Override
		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				addLayerListenerRecursively(layer, this);
                                ILayer[] model = getMapContext().getLayers();
                                int count = 0;
                                //We check that we have only one spatial layer in
                                //the layer model. It it is the case, we will :
                                // - set adjustExtent to true in the MapTransform
                                // - set the extent of the map to the extent of the layer.
                                for(int i=0; i<model.length && count <2;i++){
                                        if(model[i] instanceof Layer){
                                                count++;
                                        }
                                }
				if (count == 1) {
					final Envelope e = layer.getEnvelope();
					if (e != null) {
                                                mapTransform.setAdjustExtent(true);
						mapTransform.setExtent(e);
					}
				} else {
                                        invalidateImage();
                                }
                        }
		}

                @Override
		public void layerMoved(LayerCollectionEvent listener) {
			invalidateImage();
		}

		@Override
		public boolean layerRemoving(LayerCollectionEvent layerCollectionEvent) {
			return true;
		}

                @Override
		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				removeLayerListenerRecursively(layer, this);
                                if(!mapContext.isLayerModelSpatial()){
                                        mapTransform.setAdjustExtent(false);
                                        mapTransform.setExtent(new Envelope());
                                }
				invalidateImage();
			}
		}

                @Override
		public void nameChanged(LayerListenerEvent e) {
		}

                @Override
		public void visibilityChanged(LayerListenerEvent e) {
			invalidateImage();
		}

                @Override
		public void styleChanged(LayerListenerEvent e) {
			invalidateImage();
                }

                @Override
		public void selectionChanged(SelectionEvent e) {
                        //TODO use the bean property selection event (when feature/table-editor will be merged) to find if the redraw has to be done
                        invalidateImage();
		}

                @Override
		public void multipleModification(MultipleEditionEvent e) {
			invalidateImage();
		}

                @Override
		public void singleModification(EditionEvent e) {
			invalidateImage();
		}

                @Override
		public void cancel(DataSource ds) {
		}

                @Override
		public void commit(DataSource ds) {
		}

                @Override
		public void open(DataSource ds) {
			invalidateImage();
		}

	}

        /**
         * Free resources allocated by the MapControl
         */
        public void closing() {
		/*
		 * if (drawer != null) { drawer.cancel(); }
		 */
		toolManager.freeResources();
		toolManager = null;
                removeMapContextListener();
	}

	public void setShowCoordinates(boolean showCoordinates) {
		this.showCoordinates = showCoordinates;
		repaint();
	}

	public boolean getShowCoordinates() {
		return showCoordinates;
	}

	@Override
	public void componentAdded(ContainerEvent e) {

	}

	@Override
	public void componentRemoved(ContainerEvent e) {

	}

	public void setDefaultTool(Automaton defaultTool) {
		this.defaultTool = defaultTool;
	}

	public void setElement(TransformListener element) {
		this.element = element;
	}
        /**
        * Remove the property listeners
        */
        private void removeMapContextListener() {
                if(mapContext!=null) {
                        mapContext.removePropertyChangeListener(boundingBoxPropertyListener);
                        mapContext.getLayerModel().removeLayerListenerRecursively(refreshLayerListener);
                }                
        }
        /**
         * Switch the loaded map context
         * @param mapContext new map context
         */
	public void setMapContext(MapContext mapContext) {
                //Remove the property listeners
                removeMapContextListener();
                mapContext.addPropertyChangeListener(boundingBoxPropertyListener);
                // Add refresh listener
                addLayerListenerRecursively(mapContext.getLayerModel(), refreshLayerListener);
		this.mapContext = mapContext;
	}

	public MapContext getMapContext() {
		return mapContext;
	}

        private class MapControlTransformListener implements TransformListener {
            
            @Override
            public void imageSizeChanged(int oldWidth, int oldHeight,
                            MapTransform mapTransform) {
                    invalidateImage();
            }

            @Override
            public void extentChanged(Envelope oldExtent,
                            MapTransform mapTransform) {
                    invalidateImage();
                    // Record new BoundingBox value for map context
                    mapContext.setBoundingBox(mapTransform.getAdjustedExtent());
            }            
        }
        
        private class MapToolListener implements ToolListener {
                @Override
                public void transitionException(ToolManager toolManager,
                                TransitionException e) {
                        //The error has to be shown to the user,
                        //without the stack trace
                        LOGGER.error(I18N.tr("Tool error {0}",e.getMessage())); //$NON-NLS-1$
                }

                @Override
                public void stateChanged(ToolManager toolManager) {
                }

                @Override
                public void currentToolChanged(Automaton previous,
                                ToolManager toolManager) {
                }
        }
}
