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
package org.orbisgis.editors.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.orbisgis.IProgressMonitor;
import org.orbisgis.Services;
import org.orbisgis.editors.map.tool.Automaton;
import org.orbisgis.editors.map.tool.ToolManager;
import org.orbisgis.editors.map.tool.TransitionException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.LayerCollectionEvent;
import org.orbisgis.layerModel.LayerListener;
import org.orbisgis.layerModel.LayerListenerEvent;
import org.orbisgis.layerModel.ModificationEvent;
import org.orbisgis.layerModel.SelectionEvent;
import org.orbisgis.layerModel.MapContext;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.SystemListener;
import org.orbisgis.pluginManager.background.BackgroundJob;
import org.orbisgis.pluginManager.background.BackgroundManager;
import org.orbisgis.pluginManager.background.DefaultJobId;
import org.orbisgis.windows.mainFrame.UIManager;

import com.vividsolutions.jts.geom.Envelope;

/**
 * MapControl.
 *
 * @author Fernando Gonzlez Corts
 */
public class MapControl extends JComponent implements ComponentListener {

	private static int lastProcessId = 0;

	private int processId;

	/** The map will draw the last generated image without querying the data. */
	public static final int UPDATED = 0;

	/** The map will query the data to obtain a new image. */
	public static final int DIRTY = 1;

	private int status = DIRTY;

	private ToolManager toolManager;

	private Color backColor;

	private BufferedImage inProcessImage;

	private MapTransform mapTransform = new MapTransform();

	private MapContext mapContext;

	/**
	 * Creates a new NewMapControl.
	 *
	 * @param mapContext
	 * @param defaultTool
	 *
	 * @param ec
	 * @throws TransitionException
	 */
	public MapControl(MapContext mapContext, Automaton defaultTool)
			throws TransitionException {
		synchronized (this) {
			this.processId = lastProcessId++;
		}
		this.mapContext = mapContext;
		setDoubleBuffered(true);
		setOpaque(true);
		status = DIRTY;

		toolManager = new ToolManager(defaultTool, mapContext, mapTransform,
				this);
		this.addMouseListener(toolManager);
		this.addMouseMotionListener(toolManager);

		// events
		this.addComponentListener(this);

		mapTransform.addTransformListener(new TransformListener() {

			public void imageSizeChanged(int oldWidth, int oldHeight,
					MapTransform mapTransform) {
				invalidateImage();
			}

			public void extentChanged(Envelope oldExtent,
					MapTransform mapTransform) {
				invalidateImage();
			}

		});

		mapTransform.setExtent(mapContext.getLayerModel().getEnvelope());

		mapContext.getLayerModel().addLayerListenerRecursively(
				new RefreshLayerListener());

		// Check the status of the tools
		PluginManager psm = (PluginManager) Services
				.getService("org.orbisgis.PluginManager");
		psm.addSystemListener(new SystemListener() {

			public void statusChanged() {
				try {
					toolManager.checkToolStatus();
					UIManager uiManager = (UIManager) Services
							.getService("org.orbisgis.UIManager");
					uiManager.refreshUI();
				} catch (TransitionException e) {
				}
			}

		});

	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		if (status == UPDATED) {
			// If not waiting for an image
			if ((mapTransform.getImage() == inProcessImage)
					|| (inProcessImage == null)) {
				g.drawImage(mapTransform.getImage(), 0, 0, null);
				toolManager.paintEdition(g);
			}
		} else if (status == DIRTY) {
			inProcessImage = new BufferedImage(this.getWidth(), this
					.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics gImg = inProcessImage.createGraphics();
			gImg.setColor(backColor);
			gImg.fillRect(0, 0, getWidth(), getHeight());
			status = UPDATED;
			if (mapTransform.getAdjustedExtent() != null) {
				Drawer d = new Drawer();
				BackgroundManager bm = (BackgroundManager) Services
						.getService("org.orbisgis.BackgroundManager");
				bm.nonBlockingBackgroundOperation(new DefaultJobId(
						"org.orbisgis.jobs.MapControl-" + processId), d);
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

	/**
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {
	}

	/**
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
	}

	/**
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		mapTransform.resizeImage(getWidth(), getHeight());
	}

	/**
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
		repaint();
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

	private void invalidateImage() {
		status = DIRTY;
		repaint();
	}

	public class Drawer implements BackgroundJob {

		public String getTaskName() {
			return "Drawing";
		}

		public void run(IProgressMonitor pm) {
			try {
				mapContext.draw(inProcessImage, mapTransform
						.getAdjustedExtent(), pm);
			} catch (RuntimeException e) {
				throw e;
			} catch (Error e) {
				throw e;
			} finally {
				mapTransform.setImage(inProcessImage);
				MapControl.this.repaint();
			}
		}
	}

	public MapTransform getMapTransform() {
		return mapTransform;
	}

	public ToolManager getToolManager() {
		return toolManager;
	}

	public Automaton getTool() {
		return toolManager.getTool();
	}

	private class RefreshLayerListener implements LayerListener {
		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.addLayerListenerRecursively(this);
				if (mapTransform.getAdjustedExtent() == null) {
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

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.removeLayerListenerRecursively(this);
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

		public void dataChanged(ModificationEvent e) {
			invalidateImage();
		}

		public void selectionChanged(SelectionEvent e) {
			// TODO Should invalidate only an selection dedicated image
			invalidateImage();
		}
	}

}