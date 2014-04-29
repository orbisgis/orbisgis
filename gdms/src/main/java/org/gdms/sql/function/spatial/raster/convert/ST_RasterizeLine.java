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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import ij.gui.Roi;
import ij.gui.ShapeRoi;
import org.apache.log4j.Logger;
import org.grap.model.GeoRaster;
import org.grap.processing.Operation;
import org.grap.processing.OperationException;
import org.grap.processing.operation.others.RasteringMode;
import org.grap.processing.operation.others.Rasterization;
import org.grap.utilities.JTSConverter;
import org.grap.utilities.PixelsUtil;
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

public final class ST_RasterizeLine extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(ST_RasterizeLine.class);

        @Override
        public String getDescription() {
                return "Convert a set of lines into a raster grid.";
        }

        @Override
        public String getName() {
                return "ST_RasterizeLine";
        }

        @Override
        public String getSqlOrder() {
                return "select * from ST_RasterizeLine(table, rasterTable, value);";
        }

        @Override
        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                final DataSet sds = tables[0];
                final int spatialFieldIndex;
                final DataSet dsRaster = tables[1];
                final int rasterFieldIndex;

                try {
                        int value = values[0].getAsInt();

                        final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                                getMetadata(null));
                        long dsGeomRowCount = sds.getRowCount();
                        long dsRasterRowCount = dsRaster.getRowCount();

                        spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(sds.getMetadata());
                        rasterFieldIndex = MetadataUtilities.getSpatialFieldIndex(dsRaster.getMetadata());

                        for (int rasterIdx = 0; rasterIdx < dsRasterRowCount; rasterIdx++) {
                                final GeoRaster raster = dsRaster.getFieldValue(rasterIdx, rasterFieldIndex).getAsRaster();
                                final PixelsUtil pixelsUtil = new PixelsUtil(raster);
                                final ArrayList<Roi> rois = new ArrayList<Roi>();

                                for (int geomIdx = 0; geomIdx < dsGeomRowCount; geomIdx++) {
                                        final Geometry geom = sds.getFieldValue(geomIdx, spatialFieldIndex).getAsGeometry();
                                        rois.addAll(getRoi(geom, pixelsUtil));
                                }

                                if (!rois.isEmpty()) {
                                        final Operation rasterizing = new Rasterization(
                                                RasteringMode.DRAW, rois, value);
                                        final GeoRaster grResult = raster.doOperation(rasterizing);
                                        driver.addValues(new Value[]{ValueFactory.createValue(grResult)});
                                }
                        }
                        return driver;
                } catch (DriverException e) {
                        throw new FunctionException(
                                "Problem trying to access input datasources", e);
                } catch (IOException e) {
                        throw new FunctionException(
                                "Problem trying to raster input datasource", e);
                } catch (OperationException e) {
                        throw new FunctionException(
                                "error with GRAP Rasterization operation", e);
                }
        }

        private List<Roi> getRoi(LineString lineString, PixelsUtil pixelsUtil)
                throws FunctionException {
                final Geometry env = lineString.getEnvelope();
                if ((env instanceof LineString) && (lineString.getNumPoints() > 2)) {
                        // simplification process
                        return getRoi((LineString) env, pixelsUtil);
                }
                return Arrays.asList(new Roi[]{new ShapeRoi(JTSConverter.toPolygonRoi(pixelsUtil.toPixel(lineString)))});
        }

        private List<Roi> getRoi(Polygon polygon, PixelsUtil pixelsUtil)
                throws FunctionException {
                return getRoi(polygon.getExteriorRing(), pixelsUtil);
        }

        private List<Roi> getRoi(GeometryCollection gc, PixelsUtil pixelsUtil)
                throws FunctionException {
                final List<Roi> result = new ArrayList<Roi>();
                for (int i = 0; i < gc.getNumGeometries(); i++) {
                        result.addAll(getRoi(gc.getGeometryN(i), pixelsUtil));
                }
                return result;
        }

        private List<Roi> getRoi(Point point, PixelsUtil pixelsUtil) throws FunctionException {
                // TODO: implement this
                return new ArrayList<Roi>();
        }

        private List<Roi> getRoi(Geometry geometry, PixelsUtil pixelsUtil)
                throws FunctionException {
                if (geometry instanceof Point) {
                        return getRoi((Point) geometry, pixelsUtil);
                } else if (geometry instanceof LineString) {
                        return getRoi((LineString) geometry, pixelsUtil);
                } else if (geometry instanceof Polygon) {
                        return getRoi((Polygon) geometry, pixelsUtil);
                } else {
                        return getRoi((GeometryCollection) geometry, pixelsUtil);
                }
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{TypeFactory.createType(Type.RASTER)}, new String[]{"raster"});
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{                               
                                new TableFunctionSignature(TableDefinition.RASTER,
                                new TableArgument(TableDefinition.GEOMETRY),
                                new TableArgument(TableDefinition.RASTER),
                                ScalarArgument.DOUBLE)
                        };
        }
}
