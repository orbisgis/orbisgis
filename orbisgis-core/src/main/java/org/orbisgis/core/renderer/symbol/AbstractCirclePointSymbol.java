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
package org.orbisgis.core.renderer.symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public abstract class AbstractCirclePointSymbol extends AbstractPointSymbol {

	AbstractCirclePointSymbol(Color outline, int lineWidth, Color fillColor,
			int size, boolean mapUnits) {
		super(outline, lineWidth, fillColor, size, mapUnits);
	}

	protected void paintCircle(Graphics2D g, double x, double y, double size) {
		x = x - size / 2;
		y = y - size / 2;
		if (fillColor != null) {
			g.setPaint(fillColor);
			g.fillOval((int) x, (int) y, (int) size, (int) size);
		}
		if (outline != null) {
			g.setStroke(new BasicStroke(lineWidth));
			g.setColor(outline);
			g.drawOval((int) x, (int) y, (int) size, (int) size);
		}
	}
}
