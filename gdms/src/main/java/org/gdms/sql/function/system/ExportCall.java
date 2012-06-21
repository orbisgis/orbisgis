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
package org.gdms.sql.function.system;

import java.io.File;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.source.SourceManager;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.gdms.sql.function.table.TableArgument;

/**
 *
 * @author Antoine Gourlay
 */
public final class ExportCall extends AbstractExecutorFunction {

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                final SourceManager sourceManager = dsf.getSourceManager();

                if (values.length == 1) {
                        DataSet ds = tables[0];
                        String file = values[0].getAsString();

                        String destName = sourceManager.nameAndRegister(new File(file));
                        try {
                                dsf.saveContents(destName, ds, pm);
                        } catch (DriverException ex) {
                                throw new FunctionException(ex);
                        } finally {
                                sourceManager.remove(destName);
                        }
                } else if (values.length > 6 && values.length < 9) {

                        DataSet ds = tables[0];
                        String vendor = values[0].toString();
                        final String host = values[1].toString();
                        final int port = values[2].getAsInt();
                        final String dbName = values[3].toString();
                        final String user = values[4].toString();
                        final String password = values[5].toString();
                        String schemaName = null;
                        String tableName = null;
                        if (values.length == 7) {
                                tableName = values[6].toString();
                        }
                        if (values.length == 8) {
                                schemaName = values[6].toString();
                                tableName = values[7].toString();
                        }
                        
                        if (!vendor.startsWith("jdbc:")) {
                                vendor = "jdbc:" + vendor;
                        }

                        String destName = sourceManager.nameAndRegister(new DBTableSourceDefinition(
                                new DBSource(host, port, dbName,
                                user, password, schemaName, tableName, vendor)));
                        try {
                                dsf.saveContents(destName, ds);
                        } catch (DriverException ex) {
                                throw new FunctionException(ex);
                        } finally {
                                sourceManager.remove(destName);
                        }
                } else {
                        throw new FunctionException("Wrong number of arguments. See function description");
                }
        }

        @Override
        public String getName() {
                return "Export";
        }

        @Override
        public String getDescription() {
                return "Exports an existing table to the specified file and format.";
        }

        @Override
        public String getSqlOrder() {
                return "1) EXECUTE Export(myTable, '/home/myuser/myFile.shp')\n"
                        + "2) EXECUTE Export(myTable, 'vendor', 'host', port, "
                        + "dbName, user, password, tableName);\n"
                        + "3) EXECUTE Export(myTable, 'vendor', 'host', port, "
                        + "dbName, user, password, schema, tableName);\n";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(TableArgument.ANY, ScalarArgument.STRING),
                                new ExecutorFunctionSignature(TableArgument.ANY, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.INT, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.STRING, ScalarArgument.STRING),
                                new ExecutorFunctionSignature(TableArgument.ANY, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.INT, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.STRING, ScalarArgument.STRING,
                                ScalarArgument.STRING)
                        };
        }
}
