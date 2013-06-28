/**
 * The GDMS library (Generic Datasource Management System) is a middleware
 * dedicated to the management of various kinds of data-sources such as spatial
 * vectorial data or alphanumeric. Based on the JTS library and conform to the
 * OGC simple feature access specifications, it provides a complete and robust
 * API to manipulate in a SQL way remote DBMS (PostgreSQL, H2...) or flat files
 * (.shp, .csv...).
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
 * or contact directly: info@orbisgis.org
 */
package org.gdms.data.crs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import java.util.List;
import org.cts.IllegalCoordinateException;
import org.cts.crs.CRSException;
import org.cts.crs.GeodeticCRS;
import org.cts.op.CoordinateOperation;
import org.cts.op.CoordinateOperationFactory;
import org.cts.registry.RegistryException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;

/**
 *
 * Transform a geometry from crs code to another one.
 *
 */
public class SpatialReferenceSystem {

    private GeodeticCRS targetCRS;
    private CoordinateOperation coordinateOperation;

    public SpatialReferenceSystem(String sourceCRS, String targetCRS) throws CRSException, RegistryException {
        init((GeodeticCRS) DataSourceFactory.getCRSFactory().getCRS(sourceCRS), (GeodeticCRS) DataSourceFactory.getCRSFactory().getCRS(targetCRS));
    }

    public SpatialReferenceSystem(int sourceCRS, int targetCRS) throws CRSException, RegistryException {
        init((GeodeticCRS) DataSourceFactory.getCRSFactory().getCRS("EPSG:" + sourceCRS), (GeodeticCRS) DataSourceFactory.getCRSFactory().getCRS("EPSG:" + targetCRS));
    }

    public SpatialReferenceSystem(GeodeticCRS sourceCRS, GeodeticCRS targetCRS) {
        init(sourceCRS, targetCRS);
    }

    private void init(GeodeticCRS sourceCRS, GeodeticCRS targetCRS) {
        this.targetCRS = targetCRS;
        if ((sourceCRS != null) || (targetCRS != null)) {
            List<CoordinateOperation> ops = CoordinateOperationFactory
                    .createCoordinateOperations(sourceCRS, targetCRS);
            if (!ops.isEmpty()) {
                coordinateOperation = ops.get(0);
            } else {
                throw new RuntimeException("Cannot find a coordinate operation for"
                        + "this transformation.");
            }
        } else {
            throw new RuntimeException("The source or target coordinate "
                    + "reference system cannot be null.");
        }

    }

    public CoordinateOperation getCoordinateOperationSequence() {
        return coordinateOperation;
    }

    public Value transform(Geometry geom) {
        Geometry g = getGeometryTransformer().transform(geom);
        return ValueFactory.createValue(g, targetCRS);
    }

    public GeometryTransformer getGeometryTransformer() {
        GeometryTransformer gt = null;
        if (gt == null) {
            gt = new GeometryTransformer() {
                @Override
                protected CoordinateSequence transformCoordinates(
                        CoordinateSequence cs, Geometry geom) {
                    Coordinate[] cc = geom.getCoordinates();
                    CoordinateSequence newcs = new CoordinateArraySequence(cc);
                    for (int i = 0; i < cc.length; i++) {
                        Coordinate c = cc[i];
                        try {
                            if(Double.isNaN(c.z)){
                                c.z=0;
                            }                            
                            double[] xyz = coordinateOperation
                                    .transform(new double[]{c.x, c.y, c.z});
                            newcs.setOrdinate(i, 0, xyz[0]);
                            newcs.setOrdinate(i, 1, xyz[1]);
                            if (xyz.length > 2) {
                                newcs.setOrdinate(i, 2, xyz[2]);
                            } else {
                                newcs.setOrdinate(i, 2, Double.NaN);
                            }
                        } catch (IllegalCoordinateException ice) {
                            ice.printStackTrace();
                        }
                    }
                    return newcs;
                }
            };
        }

        return gt;
    }
}
