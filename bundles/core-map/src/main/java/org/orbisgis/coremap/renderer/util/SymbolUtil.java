/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.coremap.renderer.util;

import com.vividsolutions.jts.geom.Geometry;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import org.apache.log4j.Logger;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.ui.util.GUIUtil;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public class SymbolUtil {

	
	
        private static final Logger LOGGER = Logger.getLogger(SymbolUtil.class);
	private static final I18n I18N = I18nFactory.getI18n(SymbolUtil.class);
        
        
        /**
         * A method to display a symbol arround a shape.
	 * @param millisecondDelay
	 *            the GUI will be unresponsive for this length of time, so keep
	 *            it short!
	 */
	public static void flash(final Shape shape, final Graphics2D graphics,
			Color color, Stroke stroke, final int millisecondDelay) {
		graphics.setColor(color);
		graphics.setXORMode(Color.white);
		graphics.setStroke(stroke);

		try {
			GUIUtil.invokeOnEventThread(new Runnable() {
				public void run() {
					try {
						graphics.draw(shape);

						// Use sleep rather than Timer (which could allow a
						// third party to paint
						// the panel between my XOR draws, messing up the XOR).
						// Hopefully the user
						// won't Alt-Tab away and back! [Jon Aquino]
						Thread.sleep(millisecondDelay);
						graphics.draw(shape);
					} catch (Throwable t) {
						 LOGGER.error(I18N.tr("Cannot draw the shape"), t);
					}
				}
			});
		} catch (Throwable t) {
                        LOGGER.error(I18N.tr("Cannot draw the shape"), t);
		}
	}

	public static void flash(final Geometry geometry, Graphics2D graphics,
			MapTransform mt, final int millisecondDelay) {
		flash(mt.getShapeWriter().toShape(geometry), graphics, Color.red,
				new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND), millisecondDelay);
	}

	public static void flash(final Geometry geometry, Graphics2D graphics,
			MapTransform mt) {
		flash(mt.getShapeWriter().toShape(geometry), graphics, Color.red,
				new BasicStroke(5, BasicStroke.CAP_ROUND,
						BasicStroke.JOIN_ROUND), 100);
	}

	public static void flashPoint(final Geometry geometry, Graphics2D graphics,
			MapTransform mt) {

		Point2D p = new Point2D.Double(geometry.getCoordinate().x, geometry
				.getCoordinate().y);
		p = mt.getAffineTransform().transform(p, null);

		int IND_CIRCLE_RADIUS = 20;

		Ellipse2D.Double circle = new Ellipse2D.Double(p.getX()
				- (IND_CIRCLE_RADIUS / 2), p.getY() - (IND_CIRCLE_RADIUS / 2),
				IND_CIRCLE_RADIUS, IND_CIRCLE_RADIUS);

		flash(circle, graphics, Color.red, new BasicStroke(1,
				BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND), 1000);
	}
	

}
