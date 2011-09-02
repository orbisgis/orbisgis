/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
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
package org.gdms.sql.function.spatial.raster.convert;

import ij.process.ImageProcessor;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import org.apache.log4j.Logger;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Dimension3DConstraint;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_VectorizeLine extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private static final int[] NEIGHBORS_X = {1, 1, 0, -1, -1, -1, 0, 1};
        private static final int[] NEIGHBORS_Y = {0, -1, -1, -1, 0, 1, 1, 1};
        private static final Logger LOG = Logger.getLogger(ST_VectorizeLine.class);

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                final DataSet sds = tables[0];
                final int spatialFieldIndex;
                try {
                        if (1 == values.length) {
                                // if no raster's field's name is provided, the default (first)
                                // one is arbitrarily chosen.
                                spatialFieldIndex = sds.getMetadata().getFieldIndex(values[0].toString());
                        } else {
                                spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(sds.getMetadata());
                        }

                        final Map<Float, Set<LineString>> map = new HashMap<Float, Set<LineString>>();

                        final long rowCount = sds.getRowCount();
                        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                                final GeoRaster geoRasterSrc = sds.getFieldValue(rowIndex, spatialFieldIndex).getAsRaster();
                                final ImageProcessor processor = geoRasterSrc.getImagePlus().getProcessor();
                                final float ndv = Double.isNaN(geoRasterSrc.getNoDataValue()) ? GeoRaster.FLOAT_NO_DATA_VALUE
                                        : (float) geoRasterSrc.getNoDataValue();
                                final int ncols = geoRasterSrc.getWidth();
                                final int nrows = geoRasterSrc.getHeight();
                                pm.startTask("Processing", nrows);
                                final boolean[] alreadyVisited = new boolean[ncols * nrows];
                                final int[] neighborsIndices = new int[]{1, -ncols + 1,
                                        -ncols, -ncols - 1, -1, ncols - 1, ncols, ncols + 1};

                                for (int y = 0, i = 0; y < nrows; y++) {

                                        if (y >= 100 && y % 100 == 0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo(y);
                                                }
                                        }

                                        for (int x = 0; x < ncols; x++, i++) {
                                                if (!alreadyVisited[i]) {
                                                        alreadyVisited[i] = true;
                                                        final float pixelValue = processor.getPixelValue(x,
                                                                y);
                                                        // TODO : please simplify following test !
                                                        if ((pixelValue != ndv)
                                                                && (pixelValue != GeoRaster.FLOAT_NO_DATA_VALUE)
                                                                && (pixelValue != GeoRaster.SHORT_NO_DATA_VALUE)
                                                                && (pixelValue != GeoRaster.BYTE_NO_DATA_VALUE)) {
                                                                LineString lineString = fromPixelsToLineString(
                                                                        geoRasterSrc, processor,
                                                                        alreadyVisited, pixelValue, x, y, i,
                                                                        neighborsIndices);
                                                                if (null != lineString) {
                                                                        lineString = SimplificationUtilities.simplifyGeometry(lineString);
                                                                        if (map.containsKey(pixelValue)) {
                                                                                map.get(pixelValue).add(lineString);
                                                                        } else {
                                                                                final Set<LineString> set = new HashSet<LineString>();
                                                                                set.add(lineString);
                                                                                map.put(pixelValue, set);
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                                pm.progressTo(nrows);
                                pm.endTask();
                        }
                        final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                                getMetadata(null));
                        for (float pixelValue : map.keySet()) {
                                final Set<LineString> setOfLineString = map.get(pixelValue);
                                final LineString[] arrayOfLineString = setOfLineString.toArray(new LineString[setOfLineString.size()]);

                                driver.addValues(new Value[]{
                                                ValueFactory.createValue(pixelValue),
                                                ValueFactory.createValue(GF.createMultiLineString(arrayOfLineString))});
                        }
                        return driver;
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (IOException e) {
                        throw new FunctionException(e);
                }
        }

        private LineString fromPixelsToLineString(final GeoRaster geoRasterSrc,
                final ImageProcessor processor, final boolean[] alreadyVisited,
                final float pixelValue, final int x0, final int y0, final int i0,
                final int[] neighborsIndices) {
                final List<Coordinate> coordinates = new ArrayList<Coordinate>();
                final int ncols = processor.getWidth();
                int x = x0;
                int y = y0;
                Integer i = i0;

                do {
                        alreadyVisited[i] = true;

                        // add the current point to the linestring
                        final Point2D point2D = geoRasterSrc.fromPixelToRealWorld(x, y);
                        coordinates.add(new Coordinate(point2D.getX(), point2D.getY()));

                        // then find the next neighbor
                        i = findTheFirstNextNeighbor(processor, alreadyVisited, pixelValue,
                                x, y, i, neighborsIndices);

                        if (null != i) {
                                x = i % ncols;
                                y = i / ncols;
                        }
                } while (null != i);

                if (1 < coordinates.size()) {
                        return GF.createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
                } else {
                        return null;
                }
        }

        private Integer findTheFirstNextNeighbor(final ImageProcessor processor,
                final boolean[] alreadyVisited, final float pixelValue, int x0,
                int y0, Integer i0, int[] neighborsIndices) {
                final int nrows = processor.getHeight();
                final int ncols = processor.getWidth();

                for (int i = 0; i < 8; i++) {
                        int x = x0 + NEIGHBORS_X[i];
                        int y = y0 + NEIGHBORS_Y[i];
                        int iNext = i0 + neighborsIndices[i]; // y * ncols + x;

                        if ((0 <= x) && (x < ncols) && (0 <= y) && (y < nrows)
                                && !alreadyVisited[iNext]) {
                                final float pv = processor.getPixelValue(x, y);
                                if (pixelValue == pv) {
                                        return iNext;
                                }
                        }
                }
                return null;
        }

        @Override
        public String getDescription() {
                return "This custom query converts a (set of) GeoRaster(s) into a set of MultiLineString";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.DOUBLE),
                                TypeFactory.createType(Type.MULTILINESTRING,
                                new Dimension3DConstraint(2))},
                        new String[]{"gid", "the_geom"});
        }

        @Override
        public String getName() {
                return "ST_VectorizeLine";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_VectorizeLine() from mydata;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.RASTER)),};
        }
}
