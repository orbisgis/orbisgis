/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.sql.function.spatial.geometry.qa;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.orbisgis.progress.ProgressMonitor;

public final class InternalGapFinder {

        private DataSet sds;
        private ProgressMonitor pm;
        private int spatialFieldIndex;
        private DiskBufferDriver driver;
        private static final Logger LOG = Logger.getLogger(InternalGapFinder.class);
        private final DataSourceFactory dsf;
        private double minGapArea = 10e-6;

        /**
         * This method extract all gap in a set of geometries : polygon and multi-polygon.
         * It returns a new table that contains identified gaps as polygons.
         * It can produce some out of memory due to the use of JTS union method.
         * @param dsf
         * @param sds
         * @param spatialFieldIndex
         * @param pm
         */
        public InternalGapFinder(DataSourceFactory dsf, DataSet sds, int spatialFieldIndex, ProgressMonitor pm) {
                LOG.trace("Constructor");
                this.dsf = dsf;
                this.sds = sds;
                this.pm = pm;
                this.spatialFieldIndex = spatialFieldIndex;
        }

        public void findGaps() throws DriverException {
                LOG.trace("Finding gaps");

                long rowCount = sds.getRowCount();
                pm.startTask("Read data", rowCount);
                
                CoordinateReferenceSystem crs = sds.getCRS();

                SpatialIndex spatialIndex = new STRtree(10);

                List<Geometry> geometries = new ArrayList<Geometry>();
                for (int i = 0; i < rowCount; i++) {
                        Geometry geom = sds.getGeometry(i, spatialFieldIndex);

                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        if (geom instanceof GeometryCollection) {
                                final int nbOfGeometries = geom.getNumGeometries();
                                for (int j = 0; j < nbOfGeometries; j++) {
                                        Geometry simpleGeom = geom.getGeometryN(j);
                                        if (geom.getDimension() == 2) {
                                                geometries.add(simpleGeom);
                                        }

                                }
                        } else {

                                if (geom.getDimension() == 2) {
                                        geometries.add(geom);

                                }
                        }

                }

                pm.endTask();

                Geometry cascadedPolygonUnion = CascadedPolygonUnion.union(geometries);
                int nbOfGeometries = cascadedPolygonUnion.getNumGeometries();

                pm.startTask("Gap processing", nbOfGeometries);

                driver = new DiskBufferDriver(dsf, sds.getMetadata());
                final Value[] fieldsValues = new Value[spatialFieldIndex];

                GeometryFactory gf = new GeometryFactory();


                for (int i = 0; i < nbOfGeometries; i++) {
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        Geometry simpleGeom = cascadedPolygonUnion.getGeometryN(i);
                        spatialIndex.insert(simpleGeom.getEnvelopeInternal(),
                                simpleGeom);
                }

                pm.endTask();

                pm.startTask("Result saving", nbOfGeometries);

                for (int i = 0; i < nbOfGeometries; i++) {
                        Geometry simpleGeom = cascadedPolygonUnion.getGeometryN(i);
                        Polygon poly = (Polygon) simpleGeom;
                        if (i >= 100 && i % 100 == 0) {
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(i);
                                }
                        }
                        if (poly.getNumInteriorRing() > 0) {
                                for (int j = 0; j < poly.getNumInteriorRing(); j++) {
                                        Polygon result = gf.createPolygon(gf.createLinearRing(poly.getInteriorRingN(j).getCoordinates()), null);

                                        List query = spatialIndex.query(result.getEnvelopeInternal());
                                        Geometry geomDiff = result;

                                        for (Iterator k = query.iterator(); k.hasNext();) {
                                                Geometry queryGeom = (Geometry) k.next();

                                                if (result.contains(queryGeom)) {
                                                        geomDiff = result.difference(queryGeom);

                                                }
                                        }
                                        //EPSYLON value used to limit small polygon.
                                        if (geomDiff.getArea() > minGapArea) {
                                                fieldsValues[spatialFieldIndex] = ValueFactory.createValue(geomDiff, crs);
                                                driver.addValues(fieldsValues);
                                        }

                                }
                        }

                }
                driver.writingFinished();
                driver.open();
                pm.endTask();
        }

        /**
         * Set a minimun area to filter gap.
         * If the area of the gap is less than the min area don't keep it.
         * @param minArea
         */
        public void setMinGAPArea(double minArea) {
                minGapArea = minArea;
        }

        public DiskBufferDriver getDriver() {
                return driver;
        }
}
