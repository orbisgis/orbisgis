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
import org.orbisgis.utils.FileUtils;

import org.gdms.data.DataSourceFactory;
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
 * @author Erwan Bocher
 */
public class ImportCall extends AbstractExecutorFunction {

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] values, ProgressMonitor pm) throws FunctionException {
                try {
                        final SourceManager sourceManager = dsf.getSourceManager();
                        if (values.length == 1) {
                                         File file = new File(values[0].toString());
                                        if (!file.exists()) {
                                                throw new FunctionException("The specified file does not exist! "
                                                        + "Path: " + file.getAbsolutePath());
                                        }
                                        String name = FileUtils.getFileNameWithoutExtensionU(file);
                                        sourceManager.importFrom(name, file);                                

                        } else if (values.length == 2) {
                                final String name = values[1].toString();
                                final File file = new File(values[0].toString());
                                if (!file.exists()) {
                                        throw new FunctionException("The specified file does not exist! "
                                                + "Path: " + file.getAbsolutePath());
                                }
                                sourceManager.importFrom(name, file); 
                        }
                } catch (DriverException e) {
                        throw new FunctionException(e);
                }

        }

        @Override
        public String getDescription() {
                return "Usage: \n"
                        + "1) EXECUTE IMPORT('path_to_file');\n"
                        + "2) EXECUTE IMPORT('path_to_file','name' );\n";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(ScalarArgument.STRING),
                                new ExecutorFunctionSignature(ScalarArgument.STRING,
                                ScalarArgument.STRING)
                        };
        }

        @Override
        public String getName() {
                return "Import";
        }

        @Override
        public String getSqlOrder() {
                return "EXECUTE IMPORT('/tmp/mydata.gpx')";
        }
}
