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
package org.gdms.sql.strategies;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.AbstractDataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.table.TableDefinition;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.table.AbstractTableFunction;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableFunctionSignature;
import org.orbisgis.progress.ProgressMonitor;

public class FieldReferenceQuery extends AbstractTableFunction {

        public DataSet evaluate(DataSourceFactory dsf, DataSet[] tables,
                Value[] values, ProgressMonitor pm) {
                return new AbstractDataSet() {

                        @Override
                        public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
                                return null;
                        }

                        @Override
                        public long getRowCount() throws DriverException {
                                return 0;
                        }

                        @Override
                        public Number[] getScope(int dimension) throws DriverException {
                                return null;
                        }

                        @Override
                        public Metadata getMetadata() throws DriverException {
                                return FieldReferenceQuery.this.getMetadata(null);
                        }
                };
        }

        public String getDescription() {
                return null;
        }

        public Metadata getMetadata(Metadata[] tables) throws DriverException {
                return new DefaultMetadata(new Type[] { TypeFactory.createType(Type.INT)}, new String[] {"res"});
        }

        public String getName() {
                return "fieldReferenceQuery";
        }

        public String getSqlOrder() {
                return null;
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new TableFunctionSignature(null, new TableArgument(TableDefinition.ANY),
                                new TableArgument(TableDefinition.ANY), ScalarArgument.STRING),
                                new TableFunctionSignature(null, new TableArgument(TableDefinition.ANY),
                                new TableArgument(TableDefinition.ANY), ScalarArgument.STRING, ScalarArgument.STRING)};
        }
}
