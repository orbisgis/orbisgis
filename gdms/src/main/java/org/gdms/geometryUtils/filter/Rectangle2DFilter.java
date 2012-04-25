/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.geometryUtils.filter;

import java.awt.geom.Rectangle2D;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;

/**
 * A coordinate filter to get a Rectangle2D from a geometry
 * Source : http://docs.codehaus.org/display/GEOTDOC/04+Using+CoordinateFilter+to+implement+operations
 * Usage :
 * 
 *  Rectangle2DFilter bf = new Rectangle2DFilter();
 *  myGeometry.apply(bf);
 *  Rectangle2D bounds = bf.getBounds();
 * 
 * @author Michael Bedward,
 *
 */
public class Rectangle2DFilter implements CoordinateFilter {

        private double minx, miny, maxx, maxy;
        private boolean first = true;

        /**
         * First coordinate visited initializes the min and max fields.
         * Subsequent coordinates are compared to current bounds.
         * @param c
         */
        @Override
        public void filter(Coordinate c) {
                if (first) {
                        minx = c.x;
                        maxx = minx;
                        miny = c.y;
                        maxy = miny;
                        first = false;
                } else {
                        minx = Math.min(minx, c.x);
                        miny = Math.min(miny, c.y);
                        maxx = Math.max(maxx, c.x);
                        maxy = Math.max(maxy, c.y);
                }
        }

        /**
         * Return bounds as a Rectangle2D object
         */
        public Rectangle2D getBounds() {
                return new Rectangle2D.Double(minx, miny, maxx - minx, maxy - miny);
        }
}
