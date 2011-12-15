package org.gdms.sql.function.spatial.geometry.qa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import org.apache.log4j.Logger;
import org.gdms.data.SQLDataSourceFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;

public final class InternalGapFinder {

        private double EPSYLON = 10e-6;
        private DataSet sds;
        private ProgressMonitor pm;
        private int spatialFieldIndex;
        private DiskBufferDriver driver;
        private static final Logger LOG = Logger.getLogger(InternalGapFinder.class);
        private final SQLDataSourceFactory dsf;

        /**
         * This method extract all gap in a set of geometries : polygon and multi-polygon.
         * It returns a new table that contains identified gaps as polygons.
         * It can produce some out of memory due to the use of JTS union method.
         * @param dsf
         * @param sds
         * @param spatialFieldIndex
         * @param pm
         */
        public InternalGapFinder(SQLDataSourceFactory dsf, DataSet sds, int spatialFieldIndex, ProgressMonitor pm) {
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

                SpatialIndex spatialIndex = new STRtree(10);

                List<Geometry> geometries = new ArrayList<Geometry>();
                for (int i = 0; i < rowCount; i++) {
                        Geometry geom = sds.getFieldValue(i, spatialFieldIndex).getAsGeometry();

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
                                        if (geomDiff.getArea() > EPSYLON) {
                                                fieldsValues[spatialFieldIndex] = ValueFactory.createValue(geomDiff);
                                                driver.addValues(fieldsValues);
                                        }

                                }
                        }

                }
                driver.writingFinished();
                driver.start();
                pm.endTask();
        }

        /**
         * Set a minimun area to filter gap.
         * If the area of the gap is less than the min area don't keep it.
         * @param minArea
         */
        public void setMinGAPArea(double minArea) {
                EPSYLON = minArea;
        }

        public DiskBufferDriver getDriver() {
                return driver;
        }
}
