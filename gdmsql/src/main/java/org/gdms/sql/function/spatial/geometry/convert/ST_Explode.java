/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.sql.function.spatial.geometry.convert;

import org.gdms.data.SQLDataSourceFactory;
import org.gdms.sql.function.FunctionException;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.schema.MetadataUtilities;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DiskBufferDriver;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.orbisgis.progress.ProgressMonitor;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import org.apache.log4j.Logger;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;

public final class ST_Explode extends AbstractTableFunction {

        private static final Logger LOG = Logger.getLogger(ST_Explode.class);
        private DiskBufferDriver driver;

        @Override
        public String getName() {
                return "ST_Explode";
        }

        @Override
        public String getSqlOrder() {
                return "select ST_Explode( [geomFieldName] ) from myTable;";
        }

        @Override
        public String getDescription() {
                return "Convert any GeometryCollection into a set of single Geometries";
        }

        @Override
        public DataSet evaluate(SQLDataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) throws FunctionException {
                LOG.trace("Evaluating");
                try {
                        int spatialFieldIndex;
                        final DataSet sds = tables[0];
                        if (1 == values.length) {
                                // if no spatial's field's name is provided, the default (first)
                                // one is arbitrarily chosen.
                               spatialFieldIndex = sds.getMetadata().getFieldIndex(values[0].toString());
                        } else {
                                spatialFieldIndex = MetadataUtilities.getSpatialFieldIndex(sds.getMetadata());
                        }

                        long rowCount = sds.getRowCount();
                        pm.startTask("Exploding", rowCount);
                        DefaultMetadata metadata = new DefaultMetadata(sds.getMetadata());
                        String field = MetadataUtilities.getUniqueFieldName(metadata,
                                "explod_id");
                        metadata.addField(field, TypeFactory.createType(Type.INT));

                        driver = new DiskBufferDriver(dsf, metadata);
                        int gid = 1;
                        int fieldCount = sds.getMetadata().getFieldCount();
                        
                        for (long i = 0; i < rowCount; i++) {

                                if (i >= 100 && i % 100 == 0) {
                                        if (pm.isCancelled()) {
                                                break;
                                        } else {
                                                pm.progressTo(i);
                                        }
                                }

                                final Value[] fieldsValues = new Value[fieldCount];
                                for (int j = 0; j < fieldCount; j++) {
                                        fieldsValues[j] = sds.getFieldValue(i, j);
                                }
                                final Value[] newValues = new Value[fieldsValues.length + 1];
                                System.arraycopy(fieldsValues, 0, newValues, 0,
                                        fieldsValues.length);
                                newValues[fieldsValues.length] = ValueFactory.createValue(gid++);
                                final Geometry geometry = sds.getFieldValue(i, spatialFieldIndex).getAsGeometry();
                                explode(driver, newValues, geometry, spatialFieldIndex);
                        }
                        pm.progressTo(rowCount);
                        driver.writingFinished();
                        pm.endTask();
                        driver.start();
                        return driver;
                } catch (DriverLoadException e) {
                        throw new FunctionException(e);
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }
        }

        private void explode(final DiskBufferDriver driver,
                final Value[] fieldsValues, final Geometry geometry,
                final int spatialFieldIndex) throws DriverException {
                int gid = 1;
                if (geometry instanceof GeometryCollection) {
                        final int nbOfGeometries = geometry.getNumGeometries();
                        for (int i = 0; i < nbOfGeometries; i++) {
                                fieldsValues[spatialFieldIndex] = ValueFactory.createValue(geometry.getGeometryN(i));
                                fieldsValues[fieldsValues.length - 1] = ValueFactory.createValue(gid);
                                gid++;
                                explode(driver, fieldsValues, geometry.getGeometryN(i),
                                        spatialFieldIndex);
                        }
                } else {
                        driver.addValues(fieldsValues);
                }
        }

        @Override
        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                final Metadata metadata = tables[0];
                // we don't want the resulting Metadata to be constrained !
                final int fieldCount = metadata.getFieldCount();
                final Type[] fieldsTypes = new Type[fieldCount];
                final String[] fieldsNames = new String[fieldCount];

                for (int fieldId = 0; fieldId < fieldCount; fieldId++) {
                        fieldsNames[fieldId] = metadata.getFieldName(fieldId);
                        final Type tmp = metadata.getFieldType(fieldId);
                        fieldsTypes[fieldId] = TypeFactory.createType(tmp.getTypeCode());
                }
                return new DefaultMetadata(fieldsTypes, fieldsNames);
        }

       @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.STRING),
                                new TableFunctionSignature(TableDefinition.GEOMETRY,
                                new TableArgument(TableDefinition.GEOMETRY))
                        };
        }

        @Override
        public void workFinished() throws DriverException {
                if (driver != null) {
                        driver.stop();
                }
        }

}
