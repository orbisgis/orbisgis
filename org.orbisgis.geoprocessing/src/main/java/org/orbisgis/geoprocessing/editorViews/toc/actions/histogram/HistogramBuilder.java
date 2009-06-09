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
package org.orbisgis.geoprocessing.editorViews.toc.actions.histogram;

import ij.process.ImageStatistics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class HistogramBuilder extends JPanel {
	final static int WIDTH = 256;
	final static int HEIGHT = 128;
	double defaultMin = 0;
	double defaultMax = 255;
	double min = 0;
	double max = 255;
	int[] histogram;
	int hmax;
	Image os;

	public HistogramBuilder(final ImageStatistics stats) {
		setHistogram(stats);
	}

	private void setHistogram(final ImageStatistics stats) {
		histogram = stats.histogram;
		if (histogram.length != 256) {
			histogram = null;
			return;
		}
		for (int i = 0; i < 128; i++)
			histogram[i] = (histogram[2 * i] + histogram[2 * i + 1]) / 2;
		int maxCount = 0;
		int mode = 0;
		for (int i = 0; i < 128; i++) {
			if (histogram[i] > maxCount) {
				maxCount = histogram[i];
				mode = i;
			}
		}
		int maxCount2 = 0;
		for (int i = 0; i < 128; i++) {
			if ((histogram[i] > maxCount2) && (i != mode))
				maxCount2 = histogram[i];
		}
		hmax = stats.maxCount;
		if ((hmax > (maxCount2 * 2)) && (maxCount2 != 0)) {
			hmax = (int) (maxCount2 * 1.5);
			histogram[mode] = hmax;
		}
		os = null;
	}

	public void update(final Graphics g) {
		paint(g);
	}

	public void paint(final Graphics g) {
		int x1, y1, x2, y2;
		final double scale = (double) WIDTH / (defaultMax - defaultMin);
		double slope = 0.0;
		if (max != min)
			slope = HEIGHT / (max - min);
		if (min >= defaultMin) {
			x1 = (int) (scale * (min - defaultMin));
			y1 = HEIGHT;
		} else {
			x1 = 0;
			if (max > min)
				y1 = HEIGHT - (int) ((defaultMin - min) * slope);
			else
				y1 = HEIGHT;
		}
		if (max <= defaultMax) {
			x2 = (int) (scale * (max - defaultMin));
			y2 = 0;
		} else {
			x2 = WIDTH;
			if (max > min)
				y2 = HEIGHT - (int) ((defaultMax - min) * slope);
			else
				y2 = 0;
		}
		if (histogram != null) {
			if (os == null && hmax != 0) {
				os = createImage(WIDTH, HEIGHT);
				final Graphics osg = os.getGraphics();
				osg.setColor(Color.white);
				osg.fillRect(0, 0, WIDTH, HEIGHT);
				osg.setColor(Color.gray);
				for (int i = 0; i < WIDTH; i++)
					osg.drawLine(i, HEIGHT, i, HEIGHT
							- ((int) (HEIGHT * histogram[i]) / hmax));
				osg.dispose();
			}
			if (os != null)
				g.drawImage(os, 0, 0, this);
		} else {
			g.setColor(Color.white);
			g.fillRect(0, 0, WIDTH, HEIGHT);
		}
		g.setColor(Color.black);
		g.drawLine(x1, y1, x2, y2);
		g.drawLine(x2, HEIGHT - 5, x2, HEIGHT);
		g.drawRect(0, 0, WIDTH, HEIGHT);
	}
}