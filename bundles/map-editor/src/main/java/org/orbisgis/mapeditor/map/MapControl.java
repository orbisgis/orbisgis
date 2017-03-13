/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.mapeditor.map;

import com.vividsolutions.jts.geom.Envelope;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.corejdbc.TableEditEvent;
import org.orbisgis.corejdbc.TableEditListener;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.Layer;
import org.orbisgis.coremap.layerModel.LayerCollectionEvent;
import org.orbisgis.coremap.layerModel.LayerListener;
import org.orbisgis.coremap.layerModel.LayerListenerEvent;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.SelectionEvent;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.map.TransformListener;
import org.orbisgis.coremap.renderer.ImageRenderer;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.mapeditor.map.tool.Automaton;
import org.orbisgis.mapeditor.map.tool.ToolListener;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.JComponent;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * MapControl.
 *
 */

public class MapControl extends JComponent implements ContainerListener {
        //Minimal Time in ms between two intermediate paint of drawing process
        private ResultSetProviderFactory resultSetProviderFactory = new CachedResultSetContainer();
        private static final Point MAX_IMAGE_SIZE = new Point(20000, 20000);
        private static final Logger LOGGER = LoggerFactory.getLogger(MapControl.class);
        private static final I18n I18N = I18nFactory.getI18n(MapControl.class);
	private static int lastMapControlId = 0;
        private static final long serialVersionUID = 1L;
        private AtomicBoolean awaitingDrawing=new AtomicBoolean(false); /*!< A drawing process is currently requested, it is useless to request another */
    private ExecutorService executorService;

    /** The map will draw the last generated image without querying the data. */
	public static final int UPDATED = 0;

	/** The map will query the data to obtain a new image. */
	public static final int DIRTY = 1;
        private RefreshLayerListener refreshLayerListener = new RefreshLayerListener(this);

	private int status = DIRTY;

	private ToolManager toolManager;

	private Color defaultBackColor = Color.white;

    // True if mapTransform is a intermediateDrawing and should be drawn instead of adapting updatedMapTranform image
    private AtomicBoolean intermediateDrawing = new AtomicBoolean(false);
	private MapTransform mapTransform = new MapTransform();

	private MapContext mapContext;

	private Drawer drawer;

	private boolean showCoordinates = true;


	TransformListener element;

	Automaton defaultTool;

        PropertyChangeListener boundingBoxPropertyListener = EventHandler.create(PropertyChangeListener.class,this,"onMapContextBoundingBoxChange");

        MapTransform updatedMapTranform = new MapTransform();

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

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    private void execute(SwingWorker swingWorker) {
        if(executorService != null) {
            executorService.execute(swingWorker);
        } else {
            swingWorker.execute();
        }
    }
	final public void initMapControl(Automaton defaultTool) throws TransitionException {
		synchronized (this) {
            int mapControlId = lastMapControlId++;
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
		toolManager = new ToolManager(defaultTool, mapContext, mapTransform,this);

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
        this.addComponentListener(EventHandler.create(ComponentListener.class, this, "invalidateImage"));
        // Add editable element listen transform event
        if (element != null) {
            mapTransform.addTransformListener(element);
        }

	}

	private void addLayerListenerRecursively(ILayer rootLayer,
			RefreshLayerListener refreshLayerListener) {
		rootLayer.addLayerListener(refreshLayerListener);
        if(!rootLayer.getTableReference().isEmpty() && rootLayer.getDataManager() != null) {
            rootLayer.getDataManager().removeTableEditListener(rootLayer.getTableReference(), refreshLayerListener);
            rootLayer.getDataManager().addTableEditListener(rootLayer.getTableReference(), refreshLayerListener);
        }
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			addLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}

	private void removeLayerListenerRecursively(ILayer rootLayer,
			RefreshLayerListener refreshLayerListener) {
		rootLayer.removeLayerListener(refreshLayerListener);
        if(!rootLayer.getTableReference().isEmpty() && rootLayer.getDataManager() != null) {
            rootLayer.getDataManager().removeTableEditListener(rootLayer.getTableReference(), refreshLayerListener);
        }
        // TODO remove edition listener on EditorManagerImpl
		for (int i = 0; i < rootLayer.getLayerCount(); i++) {
			removeLayerListenerRecursively(rootLayer.getLayer(i),
					refreshLayerListener);
		}
	}

    /**
     * Remove cached result set
     */
    public void clearCache() {
        if(resultSetProviderFactory instanceof  CachedResultSetContainer) {
            ((CachedResultSetContainer) resultSetProviderFactory).clearCache();
        }
        for(ILayer layer : getMapContext().getLayers()) {
            layer.clearCache();
        }
    }
    /**
     * Remove cached result set
     */
    public void clearCache(String tableReference) {
        if(resultSetProviderFactory instanceof  CachedResultSetContainer) {
            ((CachedResultSetContainer) resultSetProviderFactory).removeCache(tableReference);
        }
    }
        
	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
        @Override
        protected void paintComponent(Graphics g) {
            mapTransform.updateRenderingHints();            
            BufferedImage mapTransformImage = mapTransform.getImage();

            // we always fill the Graphics with an opaque color
            // before drawing anything.
            g.setColor(getBackColor());
            g.fillRect(0, 0, getWidth(), getHeight());

            // Overwrite the updateImage if the draw status is up to date.
            if (mapTransformImage != null && status == UPDATED && !awaitingDrawing.get()) {
                updatedMapTranform.setImage(mapTransformImage);
                updatedMapTranform.setExtent(mapTransform.getExtent());
            }

            // then we render on top the already computed image
            // if it exists
            if(updatedMapTranform.getImage() != null && !mapTransform.getAdjustedExtent().isNull()){
                if(status == UPDATED && !awaitingDrawing.get()) {
                    // Render last finished drawing
                    g.drawImage(updatedMapTranform.getImage(), 0, 0, null);
                } else if(intermediateDrawing.get()) {
                    // Render intermediate drawing
                    g.drawImage(mapTransform.getImage(), 0, 0, null);
                } else {
                    // Render adapted last finished drawing (first ms of redraw)
                    // If the image extent is not the same as the current map context extent then
                    // Compute the translation to apply to the updated image
                    Envelope targetExtent = mapTransform.getAdjustedExtent();
                    Point pixelPosTarget = mapTransform.fromMapPoint(
                            new Point2D.Double(targetExtent.getMinX(), targetExtent.getMinY()));
                    Point pixelPosDirty = mapTransform.fromMapPoint(
                            new Point2D.Double(updatedMapTranform.getAdjustedExtent().getMinX(), updatedMapTranform.getAdjustedExtent().getMinY()));
                    // Compute resize
                    int width = updatedMapTranform.getImage().getWidth();
                    int height = updatedMapTranform.getImage().getHeight();
                    double wRatio = updatedMapTranform.getAdjustedExtent().getWidth() / targetExtent.getWidth();
                    double hRatio = updatedMapTranform.getAdjustedExtent().getHeight() / targetExtent.getHeight();
                    width = (int)(((double)width) * wRatio);
                    int hdiff = (int)(((double)height) * hRatio) - height;
                    height = (int)(((double)height) * hRatio);
                    // Do not resize if the image to too big
                    if(width < MAX_IMAGE_SIZE.x && height < MAX_IMAGE_SIZE.y) {
                        g.drawImage(updatedMapTranform.getImage(), pixelPosDirty.x - pixelPosTarget.x, pixelPosDirty.y - pixelPosTarget.y - hdiff, width, height, null);
                    }
                }
                toolManager.paintEdition(g);
            }

            // if the image itself is dirty
            if (status == DIRTY && mapContext!=null) {
                if(!awaitingDrawing.getAndSet(true)) {
                    try {
                        setStatus(UPDATED);
                        intermediateDrawing.set(false);
                        // is never null, except at first loading with no layer
                        // in that case we do not draw anything
                        int width = this.getWidth();
                        int height = this.getHeight();

                        // getting an image to draw in
                        GraphicsConfiguration configuration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                        BufferedImage inProcessImage = configuration.createCompatibleImage(width, height,
                                BufferedImage.TYPE_INT_ARGB);

                        Graphics gImg = inProcessImage.createGraphics();

                        initImage(gImg);

                        // this is the new image
                        // mapTransform will update the AffineTransform
                        mapTransform.setImage(inProcessImage);

                        // now we start the actual drawer
                        drawer = new Drawer(mapContext, awaitingDrawing, this, resultSetProviderFactory, intermediateDrawing);
                        execute(drawer);
                    } catch (Exception ex) {
                        awaitingDrawing.set(false);
                        throw ex;
                    }
                } else {
                    // Currently drawing with a mix of old and new map context !
                    // Stop the drawing
                    // The drawer will call paint when it will release the awaitingDrawing
                    try {
                        if(drawer.isDone()) {
                            // Drawer have failing to restore awaitingDrawing flag value.
                            awaitingDrawing.set(false);
                        } else {
                            drawer.cancel();
                        }
                    } catch (Exception ex) {
                        // Ignore errors
                    }
                }
            }
        }

    private void initImage(Graphics gImg) {
        // filling image
        gImg.setColor(getBackColor());
        gImg.fillRect(0, 0, getWidth(), getHeight());
    }

	/**
	 * Returns the drawn image
	 * 
	 * @return imagen.
	 */
	public BufferedImage getImage() {
		return mapTransform.getImage();
	}

        /**
         * Return the background color
         * @return 
         */
	public Color getBackColor() {
            String bColor = System.getProperty("map.editor.color.background");
            if (bColor == null) {
                return defaultBackColor;
            }
            if (bColor.isEmpty()) {
                return defaultBackColor;
            }
            try {
                return Color.decode(bColor);

            } catch (NumberFormatException e) {
                return defaultBackColor;
            }
    }

	public void setBackColor(Color backColor) {
		this.defaultBackColor = backColor;
	}

	public void setTool(Automaton tool) throws TransitionException {
        if(toolManager!=null) {
		    toolManager.setTool(tool);
        }
	}

	public void invalidateImage() {
		setStatus(DIRTY);
        intermediateDrawing.set(false);
		repaint();
	}

	private static class Drawer extends SwingWorkerPM implements ActionListener {
        private MapContext mapContext;
        private AtomicBoolean awaitingDrawing;
        private AtomicBoolean intermediateDrawing;
        private MapControl mapControl;
        private ResultSetProviderFactory resultSetProviderFactory;
	private static final String DEFAULT_LOG_RENDERING_TIME_MIN = "1.0";	
        private static final int FIRST_DELAY_DRAWING = 2500;
        private static final int DELAY_DRAWING = 250; // drawing delay in ms
        private ImageRenderer renderer;
        private BufferedImage rendererImage;
        private long beginDrawing = 0;

        private Drawer(MapContext mapContext, AtomicBoolean awaitingDrawing, MapControl mapControl, ResultSetProviderFactory resultSetProviderFactory,AtomicBoolean intermediateDrawing) {
            this.mapContext = mapContext;
            this.awaitingDrawing = awaitingDrawing;
            this.mapControl = mapControl;
            this.resultSetProviderFactory = resultSetProviderFactory;
            this.intermediateDrawing = intermediateDrawing;
            setTaskName(I18N.tr("Drawing"));
        }

        @Override
        protected Object doInBackground() throws Exception {
            Timer updateViewTime = new Timer(DELAY_DRAWING, this);
            try {
                beginDrawing = System.currentTimeMillis();
                renderer = new ImageRenderer();
                renderer.setRsProvider(resultSetProviderFactory);
                updateViewTime.start();
                rendererImage = mapControl.getMapTransform().getImage();
                renderer.draw(mapControl.getMapTransform(), mapContext.getLayerModel(), this.getProgressMonitor());
		double renderingTime =  (System.currentTimeMillis() - beginDrawing) / 1000.0;    
		if(renderingTime >= Double.valueOf(System.getProperty("map.editor.renderingtimemin", DEFAULT_LOG_RENDERING_TIME_MIN)))  {
                	LOGGER.debug(I18N.tr("Rendering done in {0} seconds", renderingTime));
		}
            } catch (Exception ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            } finally {
                mapControl.getMapTransform().setImage(rendererImage);
                intermediateDrawing.set(false);
                awaitingDrawing.set(false);
                updateViewTime.stop();
                mapControl.repaint();
            }
            return null;
        }

        /**
         * Some delay while drawing.
         * The user may want to see the progression of drawing after some waiting time.
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            // Conditions to clean rendering of last complete image and
            if(!isCancelled() && awaitingDrawing.get() && (intermediateDrawing.get() ||
                    (beginDrawing + FIRST_DELAY_DRAWING < System.currentTimeMillis()))) {
                BufferedImage intermediateImg;
                if(!intermediateDrawing.get()) {
                    // Swap mapcontrol image with an intermediate rendering image
                    MapTransform mt = mapControl.getMapTransform();
                    intermediateImg = new BufferedImage(mt.getWidth(), mt.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    mapControl.getMapTransform().setImage(intermediateImg);
                } else {
                    // Clear image
                    intermediateImg = mapControl.getMapTransform().getImage();
                    mapControl.initImage(intermediateImg.createGraphics());
                }
                intermediateDrawing.set(true);
                // Build a new image target for intermediate drawing.
                Graphics2D sG2 = intermediateImg.createGraphics();
                // Add old layers image
                sG2.drawImage(rendererImage, null, null);
                // Add last layer image
                renderer.updateImage(sG2);
                mapControl.repaint();
            }
        }

        @Override
        public void cancel(){
            this.getProgressMonitor().setCancelled(true);
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

    private static class RefreshLayerListener implements LayerListener, TableEditListener {
        private MapControl mapControl;

        private RefreshLayerListener(MapControl mapControl) {
            this.mapControl = mapControl;
        }

        @Override
        public void tableChange(TableEditEvent event) {
            // Clear selection of all layers linked with this table
            TableLocation tableName = TableLocation.parse(event.getTableName());
            for(ILayer layer : mapControl.getMapContext().getLayers()) {
                String layerTable = layer.getTableReference();
                if(!layerTable.isEmpty() && TableLocation.parse(layerTable).equals(tableName)) {
                    layer.setSelection(new HashSet<Long>());
                    layer.clearCache();
                    // The trigger may be lost
                    mapControl.addLayerListenerRecursively(layer, this);
                    break;
                }
            }
            mapControl.clearCache();
            // Redraw
            mapControl.invalidateImage();
        }



        private void clearLayerCacheRecursively(ILayer rootLayer) {
            if(!rootLayer.getTableReference().isEmpty() && rootLayer.getDataManager() != null) {
                mapControl.clearCache(rootLayer.getTableReference());
            }
            for (int i = 0; i < rootLayer.getLayerCount(); i++) {
                clearLayerCacheRecursively(rootLayer.getLayer(i));
            }
        }

        @Override
        public void layerAdded(LayerCollectionEvent listener) {
            for (ILayer layer : listener.getAffected()) {
                mapControl.addLayerListenerRecursively(layer, this);
                ILayer[] model = mapControl.getMapContext().getLayers();
                int count = 0;
                //We check that we have only one spatial layer in
                //the layer model. It it is the case, we will :
                // - set adjustExtent to true in the MapTransform
                // - set the extent of the map to the extent of the layer.
                for (int i = 0; i < model.length && count < 2; i++) {
                    if (model[i] instanceof Layer) {
                        count++;
                    }
                }
                if (count == 1) {
                    final Envelope e = layer.getEnvelope();
                    if (e != null) {
                        mapControl.mapTransform.setExtent(e);
                    }
                } else {
                    mapControl.invalidateImage();
                }
            }
        }

                @Override
		public void layerMoved(LayerCollectionEvent listener) {
                    mapControl.invalidateImage();
		}

		@Override
		public boolean layerRemoving(LayerCollectionEvent layerCollectionEvent) {
			return true;
		}

        @Override
		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
                mapControl.removeLayerListenerRecursively(layer, this);
                clearLayerCacheRecursively(layer);
			}
            if(!mapControl.mapContext.isLayerModelSpatial()){
                mapControl.mapTransform.setExtent(new Envelope());
            }
            mapControl.invalidateImage();
		}

                @Override
		public void nameChanged(LayerListenerEvent e) {
		}

                @Override
		public void visibilityChanged(LayerListenerEvent e) {
                    mapControl.invalidateImage();
		}

                @Override
		public void styleChanged(LayerListenerEvent e) {
                    mapControl.invalidateImage();
                }

                @Override
		public void selectionChanged(SelectionEvent e) {
                        //TODO use the bean property selection event (when feature/table-editor will be merged) to find if the redraw has to be done
                    mapControl.invalidateImage();
		}
	}

        /**
         * Free resources allocated by the MapControl
         */
        public void closing() {
		/*
		 * if (drawer != null) { drawer.cancel(); }
		 */
            clearCache();
            if(toolManager!=null) {
                toolManager.freeResources();
                toolManager = null;
                removeMapContextListener();
            }
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
            this.mapContext = mapContext;
            if(mapContext!=null) {
                mapContext.addPropertyChangeListener(boundingBoxPropertyListener);
                // Add refresh listener
                addLayerListenerRecursively(mapContext.getLayerModel(), refreshLayerListener);
            }
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
                    mapContext.setBoundingBox(mapTransform.getExtent());
            }            
        }
        
        private class MapToolListener implements ToolListener {
                @Override
                public void transitionException(ToolManager toolManager,
                                TransitionException e) {
                        //The error has to be shown to the user,
                        //without the stack trace
                        LOGGER.error(I18N.tr("Tool error {0}",e.getMessage()), e);
                        toolManager.checkToolStatus();
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
