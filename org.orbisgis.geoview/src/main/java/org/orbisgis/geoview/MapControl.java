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
package org.orbisgis.geoview;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

import org.orbisgis.tools.Automaton;
import org.orbisgis.tools.ToolManager;
import org.orbisgis.tools.TransitionException;
import org.orbisgis.tools.ViewContext;

import com.vividsolutions.jts.geom.Envelope;

/**
 * MapControl.
 *
 * @author Fernando Gonzlez Corts
 */
public class MapControl extends JComponent implements ComponentListener {

	/** Cuando la vista est actualizada. */
	public static final int UPDATED = 0;

	/** Cuando la vista est desactualizada. */
	public static final int DIRTY = 1;

	private MapControlModel mapControlModel = null;

	private int status = DIRTY;

	private ToolManager toolManager;

	private Color backColor;

	private BufferedImage inProcessImage;

	private MapTransform mapTransform = new MapTransform();

	/**
	 * Crea un nuevo NewMapControl.
	 *
	 * @param ec
	 */
	public MapControl() {
		setDoubleBuffered(false);
		setOpaque(true);
		status = DIRTY;

		// eventos
		this.addComponentListener(this);
	}

	public void setMapControlModel(MapControlModel ms) {
		this.mapControlModel = ms;
		this.setExtent(ms.getMapArea());
		this.drawMap();
	}

	public void drawFinished() {
		mapTransform.setImage(inProcessImage);
		this.repaint();
	}

	/**
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		if (null != mapControlModel) {
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
					mapControlModel.draw(inProcessImage);
				}
				// to avoid strange effects
				g.drawImage(inProcessImage, 0, 0, null);
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
	 * Redraws the map accessing the model
	 *
	 * @param doClear
	 */
	public void drawMap() {
		status = DIRTY;
		repaint();
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
		drawMap();
	}

	/**
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
		System.out.println("shown");
		repaint();
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public void setExtent(Rectangle2D newExtent) {
		mapTransform.setExtent(newExtent);
		drawMap();
	}

	public void setExtent(Envelope newExtent) {
		if (newExtent != null) {
			Rectangle2D.Double extent = new Rectangle2D.Double(newExtent
					.getMinX(), newExtent.getMinY(), newExtent.getWidth(),
					newExtent.getHeight());
			mapTransform.setExtent(extent);
			drawMap();
		}
	}

	public Rectangle2D getAdjustedExtent() {
		return mapTransform.getAdjustedExtent();
	}

	public AffineTransform getTrans() {
		return mapTransform.getAffineTransform();
	}

	public void setTool(Automaton tool) throws TransitionException {
		toolManager.setTool(tool);
	}

	public Envelope toPixel(final Envelope geographicEnvelope) {
		return mapTransform.toPixel(geographicEnvelope);
	}

	public void setEditionContext(ViewContext ec) {
		if (toolManager != null) {
			this.removeMouseListener(toolManager);
			this.removeMouseMotionListener(toolManager);
		}
		Automaton defaultTool = ec.getView().getDefaultTool();
		toolManager = new ToolManager(defaultTool, ec);
		try {
			toolManager.setTool(defaultTool);
		} catch (TransitionException e) {
			throw new RuntimeException();
		}
		this.addMouseListener(toolManager);
		this.addMouseMotionListener(toolManager);
	}

	public Rectangle2D getExtent() {
		return mapTransform.getExtent();
	}
}
