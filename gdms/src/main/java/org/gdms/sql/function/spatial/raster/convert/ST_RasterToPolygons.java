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
package org.gdms.sql.function.spatial.raster.convert;

import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.union.CascadedPolygonUnion;
import ij.process.ImageProcessor;
import org.apache.log4j.Logger;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_RasterToPolygons extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private static final int THRESHOLD = 100;
        private static final Logger LOG = Logger.getLogger(ST_RasterToPolygons.class);
        private DiskBufferDriver driver;

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
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

                        driver = new DiskBufferDriver(dsf,
                                getMetadata(null));

                        final long rowCount = sds.getRowCount();
                        for (int rowIndex = 0, i = 0; rowIndex < rowCount; rowIndex++) {
                                final GeoRaster geoRasterSrc = sds.getFieldValue(rowIndex, spatialFieldIndex).getAsRaster();
                                final float ndv = (float) geoRasterSrc.getNoDataValue();
                                final ImageProcessor processor = geoRasterSrc.getImagePlus().getProcessor();

                                int nrows = geoRasterSrc.getHeight();
                                int ncols = geoRasterSrc.getWidth();

                                final float halfPixelSizeX = geoRasterSrc.getMetadata().getPixelSize_X() / 2;
                                final float halfPixelSizeY = geoRasterSrc.getMetadata().getPixelSize_Y() / 2;

                               
                                final Map<Double, LinkedList<Geometry>> hm = new HashMap<Double, LinkedList<Geometry>>();

                                pm.startTask("Processing raster", nrows);
                                for (int y = 0; y < nrows; y++) {
                                        if (y >= 100 && y % 100 == 0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo(y);
                                                }
                                        }
                                        for (int x = 0; x < ncols; x++) {
                                                final Double height = (double) processor.getPixelValue(
                                                        x, y);
                                                final Point2D pixelCentroid = geoRasterSrc.fromPixelToRealWorld(x, y);
                                                Geometry polygon = createPolygon(pixelCentroid,
                                                        halfPixelSizeX, halfPixelSizeY, height);

                                                if (ndv != height) {
                                                        if (hm.containsKey(height)) {
                                                                LinkedList<Geometry> list = hm.get(height);
                                                                list.add(polygon);
                                                                if (list.size() > THRESHOLD) {
                                                                        Geometry unionOfList = CascadedPolygonUnion.union(list);
                                                                        list.clear();
                                                                        list.add(SimplificationUtilities.simplifyGeometry(unionOfList));
                                                                }
                                                        } else {
                                                                LinkedList<Geometry> list = new LinkedList<Geometry>();
                                                                list.add(polygon);
                                                                hm.put(height, list);
                                                        }
                                                }
                                        }
                                }

                                for (double height : hm.keySet()) {
                                        driver.addValues(new Value[]{
                                                        ValueFactory.createValue(i++),
                                                        ValueFactory.createValue(SimplificationUtilities.simplifyGeometry(CascadedPolygonUnion.union(hm.get(height)))),
                                                        ValueFactory.createValue(height)});
                                }
                                pm.progressTo(nrows);
                                pm.endTask();
                        }
                        driver.open();
                        return driver;
                } catch (DriverException e) {
                        throw new FunctionException(e);
                } catch (FileNotFoundException e) {
                        throw new FunctionException(e);
                } catch (IOException e) {
                        throw new FunctionException(e);
                }
        }

        @Override
        public void workFinished() throws DriverException {
                if (driver != null) {
                        driver.close();
                }
        }

        private Polygon createPolygon(final Point2D pixelCentroid,
                final float halfPixelSizeX, final float halfPixelSizeY,
                double height) {
                final Coordinate[] coordinates = new Coordinate[5];
                coordinates[0] = new Coordinate(pixelCentroid.getX() - halfPixelSizeX,
                        pixelCentroid.getY() + halfPixelSizeY, height);
                coordinates[1] = new Coordinate(pixelCentroid.getX() + halfPixelSizeX,
                        pixelCentroid.getY() + halfPixelSizeY, height);
                coordinates[2] = new Coordinate(pixelCentroid.getX() + halfPixelSizeX,
                        pixelCentroid.getY() - halfPixelSizeY, height);
                coordinates[3] = new Coordinate(pixelCentroid.getX() - halfPixelSizeX,
                        pixelCentroid.getY() - halfPixelSizeY, height);
                coordinates[4] = coordinates[0];

                final LinearRing shell = GF.createLinearRing(coordinates);
                return GF.createPolygon(shell, null);
        }

        @Override
        public String getDescription() {
                return "Transform a Raster into a set of polygons (one rectangle per pixel)";
        }

        @Override
        public String getName() {
                return "ST_RasterToPolygons";
        }

        @Override
        public String getSqlOrder() {
                return "select * from ST_RasterToPolygons(rasterTable);";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.POLYGON),
                                TypeFactory.createType(Type.DOUBLE)}, new String[]{"gid",
                                "the_geom", "value"});
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.RASTER)),
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.RASTER), ScalarArgument.RASTER)
                        };
        }
}
