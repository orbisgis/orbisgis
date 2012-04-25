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
package org.gdms.sql.function.system;

import java.io.File;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
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

/**
 *
 * @author Antoine Gourlay
 */
public final class ExportCall extends AbstractExecutorFunction {

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                final SourceManager sourceManager = dsf.getSourceManager();

                if (values.length == 2) {
                        String name = values[0].getAsString();
                        String file = values[1].getAsString();

                        DataSource ds = null;
                        try {
                                ds = dsf.getDataSource(name);
                        } catch (NoSuchTableException ex) {
                                throw new FunctionException(ex);
                        } catch (DataSourceCreationException ex) {
                                throw new FunctionException(ex);
                        }
                        String destName = sourceManager.nameAndRegister(new File(file));
                        try {
                                ds.open();
                                dsf.saveContents(destName, ds, pm);
                                ds.close();
                        } catch (DriverException ex) {
                                throw new FunctionException(ex);
                        } finally {
                                sourceManager.remove(destName);
                        }
                } else if (values.length > 7 && values.length < 10) {

                        final String fromName = values[0].toString();
                        DataSource ds = null;
                        try {
                                ds = dsf.getDataSource(fromName);
                        } catch (NoSuchTableException ex) {
                                throw new FunctionException(ex);
                        } catch (DataSourceCreationException ex) {
                                throw new FunctionException(ex);
                        }

                        String vendor = values[1].toString();
                        final String host = values[2].toString();
                        final int port = values[3].getAsInt();
                        final String dbName = values[4].toString();
                        final String user = values[5].toString();
                        final String password = values[6].toString();
                        String schemaName = null;
                        String tableName = null;
                        if (values.length == 8) {
                                tableName = values[7].toString();
                        }
                        if (values.length == 9) {
                                schemaName = values[7].toString();
                                tableName = values[8].toString();
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
                return "1) EXECUTE Export('myTable', '/home/myuser/myFile.shp')\n"
                        + "2) EXECUTE Export('myTable', 'vendor', 'host', port, "
                        + "dbName, user, password, tableName);\n"
                        + "3) EXECUTE Export('myTable', 'vendor', 'host', port, "
                        + "dbName, user, password, schema, tableName);\n";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING),
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.INT, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.STRING, ScalarArgument.STRING),
                                
                                new ExecutorFunctionSignature(ScalarArgument.STRING, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.INT, ScalarArgument.STRING,
                                ScalarArgument.STRING, ScalarArgument.STRING, ScalarArgument.STRING,
                                ScalarArgument.STRING)
                        };
        }
}
