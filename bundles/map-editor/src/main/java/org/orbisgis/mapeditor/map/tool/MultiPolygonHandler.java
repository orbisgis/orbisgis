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
package org.orbisgis.mapeditor.map.tool;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.TopologyException;

public class MultiPolygonHandler extends AbstractHandler implements Handler {

        private int polygonIndex;
        private int holeIndex;

        public MultiPolygonHandler(com.vividsolutions.jts.geom.Geometry g,
                int polygonIndex, int holeIndex, int vertexIndex, Coordinate p,
                long geomPk) {
                super(g, vertexIndex, p, geomPk);
                this.polygonIndex = polygonIndex;
                this.holeIndex = holeIndex;
        }

        public Geometry moveTo(double x, double y)
                throws CannotChangeGeometryException {
                MultiPolygon mp = (MultiPolygon) geometry.clone();
                Polygon[] polygons = new Polygon[mp.getNumGeometries()];
                for (int i = 0; i < polygons.length; i++) {
                        if (i == polygonIndex) {
				PolygonHandler handler = new PolygonHandler((Polygon) mp
						.getGeometryN(i), holeIndex, vertexIndex, null,
                                        geomIndex);
                                polygons[i] = handler.moveJTSTo(x, y);
                        } else {
                                polygons[i] = (Polygon) mp.getGeometryN(i);
                        }
                }

                mp = gf.createMultiPolygon(polygons);

                if (!mp.isValid()) {
                        throw new CannotChangeGeometryException(I18N.tr("Invalid multipolygon"));
                }

                return mp;
        }

        public Geometry remove() throws TopologyException {

                MultiPolygon mp = (MultiPolygon) geometry;
                Polygon[] polygons = new Polygon[mp.getNumGeometries()];
                int vIndex = vertexIndex;
                for (int i = 0; i < polygons.length; i++) {
                        if (i == polygonIndex) {
				PolygonHandler handler = new PolygonHandler((Polygon) mp
						.getGeometryN(i), holeIndex, vIndex, null, geomIndex);
                                polygons[i] = (Polygon) handler.removeVertex();
                        } else {
                                polygons[i] = (Polygon) mp.getGeometryN(i);
                        }
                }

                mp = gf.createMultiPolygon(polygons);

                if (!mp.isValid()) {
                        throw new TopologyException(I18N.tr("Invalid multipolygon"));
                }

                return mp;
        }
}
