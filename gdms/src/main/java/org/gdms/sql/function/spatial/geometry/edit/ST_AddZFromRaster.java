/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.function.spatial.geometry.edit;

import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.IOException;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.FunctionException;
import org.grap.model.GeoRaster;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.CoordinateSequenceFilter;
import com.vividsolutions.jts.geom.Geometry;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionSignature;
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

                        return ValueFactory.createValue(geometry);
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
