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
package org.orbisgis.view.map;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
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
        private static final Logger LOGGER = Logger.getLogger(MapControl.class);
        protected final static I18n I18N = I18nFactory.getI18n(MapControl.class);
	private static int lastProcessId = 0;
        private AtomicBoolean awaitingDrawing=new AtomicBoolean(false); /*!< A drawing process is currently requested, it is useless to request another */
	private int processId;

	/** The map will draw the last generated image without querying the data. */
	public static final int UPDATED = 0;

	/** The map will query the data to obtain a new image. */
	public static final int DIRTY = 1;
        private RefreshLayerListener refreshLayerListener;

	private int status = DIRTY;

	private ToolManager toolManager;

	private Color backColor = Color.white;

	private MapTransform mapTransform = new MapTransform();

	private MapContext mapContext;

	private Drawer drawer;

	private boolean showCoordinates = true;

	TransformListener element;

	Automaton defaultTool;

	public MapControl() {
            defaultTool = new ZoomInTool();
	}
        private void setStatus(int newStatus) {
            if(newStatus==UPDATED || !awaitingDrawing.get()) {
                status = newStatus;
            }
        }
	final public void initMapControl() throws TransitionException {
		synchronized (this) {
			this.processId = lastProcessId++;
		}
		setDoubleBuffered(true);
		setOpaque(true);
		setStatus(DIRTY);

                // creating objects
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
                
                // adding listeners at the end
                // to prevent multiple useless repaint
		toolManager.addToolListener(new ToolListener() {

			@Override
			public void transitionException(ToolManager toolManager,
					TransitionException e) {
				LOGGER.error(I18N.tr("Tool error"), e); //$NON-NLS-1$
			}

			@Override
			public void stateChanged(ToolManager toolManager) {
			}

			@Override
			public void currentToolChanged(Automaton previous,
					ToolManager toolManager) {
			}
		});
		addMouseListener(toolManager);
                addMouseMotionListener(toolManager);
                addMouseWheelListener(toolManager);
                
		mapTransform.addTransformListener(new MapControlTransformListener());
                
                //Component event invalidate the picture
                this.addComponentListener(EventHandler.create(ComponentListener.class, this,"invalidateImage"));
		// Add editable element listen transform event
                mapTransform.addTransformListener(element);

                refreshLayerListener = new RefreshLayerListener();

		// Add refresh listener
		addLayerListenerRecursively(rootLayer, refreshLayerListener);
	}

	private void addLayerListenerRecursively(ILayer rootLayer,
			RefreshLayerListener refreshLayerListener) {
		rootLayer.addLayerListener(refreshLayerListener);
		DataSource dataSource = rootLayer.getDataSource();
		if (dataSource != null) {
			dataSource.addEditionListener(refreshLayerListener);
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
			dataSource.removeEditionListener(refreshLayerListener);
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
		if (mapTransformImage != null && !awaitingDrawing.get()) {
			g.drawImage(mapTransformImage, 0, 0, null);
			toolManager.paintEdition(g);
		}

		// if the image itself is dirty
		if (status == DIRTY && mapContext!=null) {
                    if(!awaitingDrawing.getAndSet(true))
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
				BackgroundManager bm = Services
						.getService(BackgroundManager.class);
				bm.nonBlockingBackgroundOperation(new DefaultJobId(
						"org.orbisgis.jobs.MapControl-" + processId), drawer); //$NON-NLS-1$


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

		public String getTaskName() {
			return I18N.tr("Drawing"); //$NON-NLS-1$
		}

		public void run(ProgressMonitor pm) {
			synchronized (this) {
				this.pm = new CancellablePM(cancel, pm);
				pm = this.pm;
			}
			try {
				mapContext.draw( mapTransform, pm);

			} catch (ClosedDataSourceException e) {
				if (!cancel) {
					throw e;
				}
			} finally {
				mapContext.setBoundingBox(mapTransform.getAdjustedExtent());
                                awaitingDrawing.set(false);
				MapControl.this.repaint();
				mapContext.setBoundingBox(mapTransform.getAdjustedExtent());
                                
			}
		}

		public void cancel() {
			synchronized (this) {
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

		public CancellablePM(boolean cancel, ProgressMonitor pm) {
			this.decoratedPM = pm;
			this.cancel = cancel;
		}

		public void endTask() {
			decoratedPM.endTask();
		}

		public int getCurrentProgress() {
			return decoratedPM.getCurrentProgress();
		}

		public String getCurrentTaskName() {
			return decoratedPM.getCurrentTaskName();
		}

		public int getOverallProgress() {
			return decoratedPM.getOverallProgress();
		}

		public void init(String taskName, long i) {
			decoratedPM.init(taskName, i);
		}

		public boolean isCancelled() {
			return cancel || decoratedPM.isCancelled();
		}

		public void progressTo(long progress) {
			decoratedPM.progressTo(progress);
		}

		public void startTask(String taskName, long i) {
			decoratedPM.startTask(taskName, i);
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
		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				addLayerListenerRecursively(layer, this);
				if (mapTransform.getExtent() == null) {
					final Envelope e = layer.getEnvelope();
					if (e != null) {
						mapTransform.setExtent(e);
					}
				} else {
					invalidateImage();
				}
			}
		}

		public void layerMoved(LayerCollectionEvent listener) {
			invalidateImage();
		}

		@Override
		public boolean layerRemoving(LayerCollectionEvent layerCollectionEvent) {
			return true;
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				removeLayerListenerRecursively(layer, this);
				invalidateImage();
			}
		}

		public void nameChanged(LayerListenerEvent e) {
		}

		public void visibilityChanged(LayerListenerEvent e) {
			invalidateImage();
		}

		public void styleChanged(LayerListenerEvent e) {
			invalidateImage();
		}

		public void selectionChanged(SelectionEvent e) {
                        if (mapContext.isSelectionInducedRefresh()) {
                                invalidateImage();
                                mapContext.setSelectionInducedRefresh(false);
                        }
		}

		public void multipleModification(MultipleEditionEvent e) {
			invalidateImage();
		}

		public void singleModification(EditionEvent e) {
			invalidateImage();
		}

		public void cancel(DataSource ds) {
		}

		public void commit(DataSource ds) {
		}

		public void open(DataSource ds) {
			invalidateImage();
		}

	}

        public void closing() {
		/*
		 * if (drawer != null) { drawer.cancel(); }
		 */
		toolManager.freeResources();
		toolManager = null;
                
                removeLayerListenerRecursively(mapContext.getLayerModel(), refreshLayerListener);
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

	public void setMapContext(MapContext mapContext) {
		this.mapContext = mapContext;
	}

	public MapContext getMapContext() {
		return mapContext;
	}

        private class MapControlTransformListener implements TransformListener {
            
            public void imageSizeChanged(int oldWidth, int oldHeight,
                            MapTransform mapTransform) {
                    invalidateImage();
            }

            public void extentChanged(Envelope oldExtent,
                            MapTransform mapTransform) {
                    invalidateImage();
                    // Record new BoundingBox value for map context
                    mapContext.setBoundingBox(mapTransform.getAdjustedExtent());
            }            
        }
}