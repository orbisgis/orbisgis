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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import ij.process.ImageProcessor;
import org.apache.log4j.Logger;
import org.cts.crs.CoordinateReferenceSystem;
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
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_RasterToPoints extends AbstractTableFunction {

        private static final GeometryFactory GF = new GeometryFactory();
        private static final Logger LOG = Logger.getLogger(ST_RasterToPoints.class);

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

                        final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                                getMetadata(null));

                        final long rowCount = sds.getRowCount();
                        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                                CoordinateReferenceSystem crs = sds.getFieldValue(rowIndex, spatialFieldIndex).getCRS();
                                final GeoRaster geoRasterSrc = sds.getFieldValue(rowIndex, spatialFieldIndex).getAsRaster();
                                final ImageProcessor processor = geoRasterSrc.getImagePlus().getProcessor();
                                final float ndv = (float) geoRasterSrc.getNoDataValue();
                                final int height = geoRasterSrc.getHeight();
                                pm.startTask("Processing raster", height);
                                for (int y = 0, i = 0; y < height; y++) {

                                        if (y >= 100 && y % 100 == 0) {
                                                if (pm.isCancelled()) {
                                                        break;
                                                } else {
                                                        pm.progressTo(y);
                                                }
                                        }

                                        for (int x = 0; x < geoRasterSrc.getWidth(); x++) {
                                                final float h = processor.getPixelValue(x, y);
                                                if (h != ndv) {
                                                        final Point2D point2D = geoRasterSrc.fromPixelToRealWorld(x, y);
                                                        final Geometry point = GF.createPoint(new Coordinate(point2D.getX(),
                                                                point2D.getY(), h));
                                                        driver.addValues(new Value[]{
                                                                        ValueFactory.createValue(i),
                                                                        ValueFactory.createValue(point, crs),
                                                                        ValueFactory.createValue(h)});
                                                }
                                                i++;
                                        }
                                }
                                pm.progressTo(height);
                                pm.endTask();
                        }
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
        public String getDescription() {
                return "Transform a Raster into a set of points based on the pixel centroid";
        }

        @Override
        public String getName() {
                return "ST_RasterToPoints";
        }

        @Override
        public String getSqlOrder() {
                return "select * from ST_RasterToPoints(rasterTable);";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.GEOMETRY),
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
