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
package org.gdms.sql.function.spatial.geometry.edit;

import java.awt.geom.Point2D;
import java.io.IOException;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import ij.process.ImageProcessor;
import org.grap.model.GeoRaster;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.spatial.geometry.AbstractScalarSpatialFunction;

/**
 * This function modify (or set) the z component of (each vertex of) the
 * geometric parameter to the corresponding value given by a raster.
 */
public final class ST_AddZFromRaster extends AbstractScalarSpatialFunction {

        private GeoRaster dem = null;
        private ImageProcessor demIp = null;

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... args) throws FunctionException {
                if ((args[0].isNull()) || (args[1].isNull())) {
                        return ValueFactory.createNullValue();
                }

                Geometry geometry = args[0].getAsGeometry();

                try {
                        if (null == dem) {
                                dem = args[1].getAsRaster();
                                dem.open();
                                demIp = dem.getImagePlus().getProcessor();
                        }

                        RasterZFilter zFilter = new RasterZFilter();
                        geometry.apply(zFilter);
                        if (null != zFilter.exception) {
                                throw new FunctionException(zFilter.exception);
                        }

                        return ValueFactory.createValue(geometry, args[0].getCRS());
                } catch (IOException e) {
                        throw new FunctionException(
                                "Bug while trying to retrieve the GeoRaster data", e);
                }

        }

        private class RasterZFilter implements CoordinateSequenceFilter {

                private boolean done = false;
                IOException exception = null;

                @Override
                public void filter(CoordinateSequence seq, int i) {
                        double x = seq.getX(i);
                        double y = seq.getY(i);
                        seq.setOrdinate(i, 0, x);
                        seq.setOrdinate(i, 1, y);
                        try {
                                seq.setOrdinate(i, 2, getGroundZ(x, y));
                                if (i == seq.size()) {
                                        done = true;
                                }
                        } catch (IOException e) {
                                exception = e;
                                done = true;
                        }
                }

                @Override
                public boolean isDone() {
                        return done;
                }

                @Override
                public boolean isGeometryChanged() {
                        return true;
                }
        }

        private double getGroundZ(final double x, final double y)
                throws IOException {
                final Point2D pixelPoint = dem.fromRealWorldToPixel(x, y);
                return demIp.getPixelValue((int) pixelPoint.getX(), (int) pixelPoint.getY());
        }

        @Override
        public String getDescription() {
                return "This function modify (or set) the z component of (each vertex of) the "
                        + "geometric parameter to the corresponding value given by a raster.";
        }

        @Override
        public String getName() {
                return "ST_AddZFromRaster";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_AddZFromRaster(b.the_geom, d.raster) from buildings b, dem d;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null), ScalarArgument.GEOMETRY, ScalarArgument.RASTER)
                        };
        }
}
