package org.gdms.sql.function.spatial.raster.interpolation;

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
import org.gdms.sql.function.ScalarArgument;
import org.grap.model.GeoRaster;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

/**
 * This function is used to build a raster using a set of points. We assume that
 * the geometry contains the z value.
 * 
 * We used the delaunay triangulation method developed by Martin Schlueter
 * 
 * @author bocher
 * 
 */
public final class ST_Interpolate extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(ST_Interpolate.class);

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                final DataSet sds = tables[0];
                String zField = null;
                boolean isBaseOnfield = false;
                double pixelSize;

                if (values.length == 3) {
                        zField = values[1].getAsString();
                        isBaseOnfield = true;
                        pixelSize = values[2].getAsDouble();
                } else {
                        pixelSize = values[1].getAsDouble();
                }
                try {
                        final long rowCount = sds.getRowCount();
                        pm.startTask("Interpolating", rowCount);
                        final int spatialFieldIndex = sds.getMetadata().getFieldIndex(values[0].toString());
                        int zFieldIndex = -1;
                        if (zField != null) {
                                zFieldIndex = sds.getMetadata().getFieldIndex(zField);
                        }

                        int numberOfPoints = (int) rowCount;

                        // Initialize coordinate arrays:
                        double[] xVcl = new double[(numberOfPoints + 4)]; // X coordinates
                        // (input) (xVcl[0]
                        // is not used)
                        double[] yVcl = new double[(numberOfPoints + 4)]; // Y coordinates
                        // (input) (yVcl[0]
                        // is not used)
                        double[] zVcl = new double[(numberOfPoints + 4)]; // Z coordinates
                        // (input) (zVcl[0]

                        for (int i = 0; i < rowCount; i++) {

                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }

                                final Geometry geometry = sds.getFieldValue(i, spatialFieldIndex).getAsGeometry();

                                double zFromField = Double.NaN;
                                if (isBaseOnfield) {
                                        zFromField = sds.getFieldValue(i, zFieldIndex).getAsDouble();
                                }
                                if (geometry instanceof Point) {
                                        final Point p = (Point) geometry;
                                        final double x = p.getCoordinate().x;
                                        final double y = p.getCoordinate().y;
                                        final double z = p.getCoordinate().z;

                                        xVcl[i + 1] = x;
                                        yVcl[i + 1] = y;

                                        if (zField != null) {
                                                zVcl[i + 1] = zFromField;
                                        } else {
                                                zVcl[i + 1] = z;
                                        }

                                }

                        }
                        pm.progressTo(rowCount);

                        TINToRaster tinToRaster = new TINToRaster(pixelSize,
                                numberOfPoints, xVcl, yVcl, zVcl);

                        GeoRaster georaster = tinToRaster.getGeoRaster();

                        final MemoryDataSetDriver driver = new MemoryDataSetDriver(
                                getMetadata(null));

                        driver.addValues(new Value[]{ValueFactory.createValue(georaster)});

                        pm.endTask();
                        return driver;
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }
        }

        public TableDefinition[] geTablesDefinitions() {
                return new TableDefinition[]{TableDefinition.GEOMETRY};
        }

        @Override
        public String getDescription() {
                return "Build a raster using an interpolate method based on delaunay triangulation";
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[]{TypeFactory.createType(Type.RASTER)}, new String[]{"raster"});
        }

        @Override
        public String getName() {
                return "ST_Interpolate";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_Interpolate(the_geom [,zField] , pixelsize) from mydata;";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.RASTER,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE,
                                ScalarArgument.DOUBLE),
                                new TableFunctionSignature(TableDefinition.RASTER,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.GEOMETRY, ScalarArgument.DOUBLE)
                        };
        }
}
