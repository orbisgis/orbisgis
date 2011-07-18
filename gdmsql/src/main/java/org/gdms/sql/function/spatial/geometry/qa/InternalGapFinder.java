package org.gdms.sql.function.spatial.geometry.qa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.generic.GenericObjectDriver;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import org.apache.log4j.Logger;
import org.gdms.driver.ReadAccess;

public final class InternalGapFinder {

	private static final double EPSYLON = 10e-6;
	private ReadAccess sds;

	private ProgressMonitor pm;

        private int spatialFieldIndex;

	private GenericObjectDriver driver;

        private static final Logger LOG = Logger.getLogger(InternalGapFinder.class);

	public InternalGapFinder(ReadAccess sds, int spatialFieldIndex, ProgressMonitor pm) {
            LOG.trace("Constructor");
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

			Geometry cascadedPolygonUnion = CascadedPolygonUnion
					.union(geometries);
                        int nbOfGeometries = cascadedPolygonUnion.getNumGeometries();

                        pm.startTask("Gap processing", nbOfGeometries);

			driver = new GenericObjectDriver(sds.getMetadata());
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
						Polygon result = gf.createPolygon(gf
								.createLinearRing(poly.getInteriorRingN(j)
										.getCoordinates()), null);

						List query = spatialIndex.query(result
								.getEnvelopeInternal());
						Geometry geomDiff = result;

						for (Iterator k = query.iterator(); k.hasNext();) {
							Geometry queryGeom = (Geometry) k.next();

							if (result.contains(queryGeom)) {
								geomDiff = result.difference(queryGeom);

							}
						}

						//EPSYLON value used to limit small polygon.
						if (geomDiff.getArea()> EPSYLON){
						fieldsValues[spatialFieldIndex] = ValueFactory
								.createValue(geomDiff);
						driver.addValues(fieldsValues);
						}

					}
				}

			}
			pm.endTask();
	}

	public GenericObjectDriver getObjectMemoryDriver() {
		return driver;
	}

}
