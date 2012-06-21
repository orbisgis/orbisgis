/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ViewRenderingPanel extends JPanel {

	float alpha = 1.0f;

	boolean antialiasing = false;

	private AlphaComposite ac;

	public ViewRenderingPanel(String composite_value) {
		if (composite_value != null) {
			this.alpha = new Float(composite_value);
		}
	}

	// Resets the alpha and composite rules with selected items.
	public void changeRule(String a) {
		alpha = Float.valueOf(a).floatValue();
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

		if (antialiasing) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		}
		Dimension d = getSize();
		int w = d.width;
		int h = d.height;

		// Creates the buffered image.
		BufferedImage buffImg = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D gbi = buffImg.createGraphics();

		// Clears the previously drawn image.
		g2.setColor(Color.white);
		g2.fillRect(0, 0, d.width, d.height);

		int rectx = w / 4;
		int recty = h / 4;

		// Draws the rectangle and ellipse into the buffered image.
		gbi.setColor(new Color(0.0f, 0.0f, 1.0f, 1.0f));
		gbi.fill(new Rectangle2D.Double(rectx, recty, 150, 100));
		gbi.setColor(new Color(1.0f, 0.0f, 0.0f, 1.0f));
		gbi.setComposite(ac);
		gbi.fill(new Ellipse2D.Double(rectx + rectx / 2, recty + recty / 2,
				100, 80));

		// Draws the buffered image.
		g2.drawImage(buffImg, null, 0, 0);
	}

	public void changeAntialiasing(boolean antialiasing) {
		this.antialiasing = antialiasing;
		repaint();

	}

}
