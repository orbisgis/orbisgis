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
package org.orbisgis.sif;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

public class CRFlowLayout extends FlowLayout {

	public CRFlowLayout(int alignment) {
		super(alignment);
	}

	public CRFlowLayout() {
	}
	
	@Override
	public void layoutContainer(Container target) {
		synchronized (target.getTreeLock()) {
			Insets insets = target.getInsets();
			int maxwidth = target.getWidth()
					- (insets.left + insets.right + getHgap() * 2);
			int nmembers = target.getComponentCount();
			int x = 0, y = insets.top + getVgap();
			int rowh = 0, start = 0;

			boolean ltr = target.getComponentOrientation().isLeftToRight();

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {

					if (m instanceof Container) {
						if (((Container) m).getLayout() instanceof CRFlowLayout) {
							layoutContainer((Container) m);
						}
					}

					Dimension d = m.getPreferredSize();
					m.setSize(d.width, d.height);

					if (((x == 0) || ((x + d.width) <= maxwidth))
							&& !(m instanceof CarriageReturn)) {
						if (x > 0) {
							x += getHgap();
						}
						x += d.width;
						rowh = Math.max(rowh, d.height);
					} else {
						moveComponents(target, insets.left + getHgap(), y,
								maxwidth - x, rowh, start, i, ltr);
						x = d.width;
						y += getVgap() + rowh;
						rowh = d.height;
						start = i;
					}
				}
			}
			moveComponents(target, insets.left + getHgap(), y, maxwidth - x,
					rowh, start, nmembers, ltr);
		}
	}

	@Override
	public Dimension preferredLayoutSize(Container target) {
		synchronized (target.getTreeLock()) {
			Dimension dim = new Dimension(0, 0);
			int nmembers = target.getComponentCount();
			boolean firstVisibleComponent = true;
			boolean newLine = true;
			boolean firstLine = true;
			int x = 0;
			int y = 0;

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);
				if (m.isVisible()) {
					Dimension d = m.getPreferredSize();
					if (!(m instanceof CarriageReturn)) {
						if (firstVisibleComponent) {
							firstVisibleComponent = false;
						} else {
							x += getHgap();
							y = Math.max(y, d.height);
						}
						x += d.width;
					}
					if (newLine) {
						if (!firstLine) {
							dim.height += getVgap();
						}
						firstLine = false;
						y += d.height;
						newLine = false;
					}

					if (m instanceof CarriageReturn) {
						dim.height += y;
						y = 0;
						dim.width = Math.max(x, dim.width);
						x = 0;
						newLine = true;
						firstVisibleComponent = true;
					}
				}
			}
			dim.height += y;
			dim.width = Math.max(x, dim.width);

			Insets insets = target.getInsets();
			dim.width += insets.left + insets.right + getHgap() * 2;
			dim.height += insets.top + insets.bottom + getVgap() * 2;
			return dim;
		}
	}

	/**
	 * Centers the elements in the specified row, if there is any slack.
	 * 
	 * @param target
	 *            the component which needs to be moved
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param width
	 *            the width dimensions
	 * @param height
	 *            the height dimensions
	 * @param rowStart
	 *            the beginning of the row
	 * @param rowEnd
	 *            the the ending of the row
	 */
	private void moveComponents(Container target, int x, int y, int width,
			int height, int rowStart, int rowEnd, boolean ltr) {
		synchronized (target.getTreeLock()) {
			switch (getAlignment()) {
			case LEFT:
				x += ltr ? 0 : width;
				break;
			case CENTER:
				x += width / 2;
				break;
			case RIGHT:
				x += ltr ? width : 0;
				break;
			case LEADING:
				break;
			case TRAILING:
				x += width;
				break;
			}
			for (int i = rowStart; i < rowEnd; i++) {
				Component m = target.getComponent(i);
				if (m instanceof CarriageReturn)
					continue;
				if (m.isVisible()) {
					if (ltr) {
						m.setLocation(x, y + (height - m.getHeight()) / 2);
					} else {
						m.setLocation(target.getWidth() - x - m.getWidth(), y
								+ (height - m.getHeight()) / 2);
					}
					x += m.getWidth() + getHgap();
				}
			}
		}
	}

}
