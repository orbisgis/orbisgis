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
package org.gdms.sql.function.spatial.raster.properties;

import java.awt.geom.Point2D;
import java.io.IOException;

import com.vividsolutions.jts.geom.Coordinate;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import org.grap.model.GeoRaster;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.sql.function.AbstractScalarFunction;
import org.gdms.sql.function.BasicFunctionSignature;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;

/**
 *
 * @author Erwan Bocher
 */
public class ST_PixelValue extends AbstractScalarFunction {

        GeoRaster geoRaster = null;
        ImageProcessor ip = null;

        @Override
        public Value evaluate(DataSourceFactory dsf, Value... values) throws FunctionException {
                try {
                        if (geoRaster == null) {
                                geoRaster = values[0].getAsRaster();
                                ip = geoRaster.getImagePlus().getProcessor();
                        } else {
                                GeoRaster georaster2 = values[0].getAsRaster();
                                if (!geoRaster.equals(georaster2)) {
                                        geoRaster = georaster2;
                                        ip = georaster2.getImagePlus().getProcessor();
                                }
                        }
                        float pixelValue = Float.NaN;
                        if (values.length == 3) {
                                int bands = ip.getNChannels();
                                int bandIndice = values[1].getAsInt();
                                if (bandIndice >= 1) {
                                        if (bandIndice <= bands) {
                                                Coordinate realWorldCoord = values[2].getAsGeometry().getCoordinate();
                                                Point2D pixelGridCoord = geoRaster.fromRealWorldToPixel(
                                                        realWorldCoord.x, realWorldCoord.y);
                                                int x = (int) pixelGridCoord.getX();
                                                int y = (int) pixelGridCoord.getY();
                                                if (ip instanceof ColorProcessor) {
                                                        ColorProcessor cp = (ColorProcessor) ip;
                                                        int[] iArray = new int[cp.getNChannels()];
                                                        cp.getPixel(x, y, iArray);
                                                        pixelValue = iArray[bandIndice - 1];
                                                } else {
                                                        pixelValue = ip.getPixelValue(x, y);
                                                }

                                        } else {
                                                throw new FunctionException("Band indice must be equal or less than " + " " + bands);
                                        }
                                } else {
                                        throw new FunctionException("Band indice must be equal or greater than 0");
                                }

                        } else {
                                Coordinate realWorldCoord = values[1].getAsGeometry().getCoordinate();
                                Point2D pixelGridCoord = geoRaster.fromRealWorldToPixel(
                                        realWorldCoord.x, realWorldCoord.y);
                                int pixelX = (int) pixelGridCoord.getX();
                                int pixelY = (int) pixelGridCoord.getY();
                                pixelValue = ip.getPixelValue(pixelX, pixelY);

                        }
                        return ValueFactory.createValue(pixelValue);
                } catch (IOException e) {
                        throw new FunctionException("Cannot read the raster", e);
                }

        }

        @Override
        public String getDescription() {
                return "Returns the value of a single pixel given that pixel's location within raster object.";
        }

        @Override
        public String getName() {
                return "ST_PixelValue";
        }

        @Override
        public String getSqlOrder() {
                return "SELECT ST_PixelValue(raster, point) FROM table";
        }

        @Override
        public int getType(int[] types) {
                return Type.FLOAT;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.POINT),
                                new BasicFunctionSignature(getType(null),
                                ScalarArgument.RASTER, ScalarArgument.INT, ScalarArgument.POINT)
                        };
        }
}
