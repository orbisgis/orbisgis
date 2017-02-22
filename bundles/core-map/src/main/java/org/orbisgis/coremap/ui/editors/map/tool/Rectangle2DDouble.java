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
package org.orbisgis.coremap.ui.editors.map.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import java.awt.geom.Rectangle2D;

public class Rectangle2DDouble extends Rectangle2D.Double {

    /**
     * Create a Rectangle2D
     */
    public Rectangle2DDouble() {
        super();
    }

    /**
     * Create Rectangle2D
     *
     * @param x the X coordinate of the upper-left corner of the newly
     * constructed <code>Rectangle2D</code>
     * @param y the Y coordinate of the upper-left corner of the newly
     * constructed <code>Rectangle2D</code>
     * @param w the width of the newly constructed <code>Rectangle2D</code>
     * @param h the height of the newly constructed <code>Rectangle2D</code>
     */
    public Rectangle2DDouble(double x, double y, double w, double h) {
        super(x, y, w, h);
    }

    /**
     * Transform a Rectangle2D to a JTS geometry
     *
     * @param gf
     * @return
     */
    public Geometry getEnvelope(GeometryFactory gf) {
        LinearRing ret = gf.createLinearRing(new Coordinate[]{
            new Coordinate(getMinX(), getMinY()),
            new Coordinate(getMaxX(), getMinY()),
            new Coordinate(getMaxX(), getMaxY()),
            new Coordinate(getMinX(), getMaxY()),
            new Coordinate(getMinX(), getMinY()),});
        return gf.createPolygon(ret, new LinearRing[]{});
    }
}
