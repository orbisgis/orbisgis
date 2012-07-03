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
package org.gdms.sql.function.spatial.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.orbisgis.progress.ProgressMonitor;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.ply.PlyExporter;
import org.gdms.sql.function.FunctionException;
import org.gdms.sql.function.FunctionSignature;
import org.gdms.sql.function.ScalarArgument;
import org.gdms.sql.function.executor.AbstractExecutorFunction;
import org.gdms.sql.function.executor.ExecutorFunctionSignature;
import org.gdms.sql.function.table.TableArgument;
import org.gdms.sql.function.table.TableDefinition;

/**
 *
 * @author Erwan Bocher
 */
public class ST_PLYExporter extends AbstractExecutorFunction {

        @Override
        public void evaluate(DataSourceFactory dsf, DataSet[] tables, Value[] args, ProgressMonitor pm) throws FunctionException {
                if (args[0] != null) {
                        try {
                                PlyExporter plyExporter = new PlyExporter(dsf, tables[0], new File(args[0].getAsString()));
                                plyExporter.write("Exported from OrbisGIS.");
                        } catch (FileNotFoundException ex) {
                                throw new FunctionException(ex);
                        } catch (UnsupportedEncodingException ex) {
                                throw new FunctionException(ex);
                        } catch (IOException ex) {
                                throw new FunctionException(ex);
                        } catch (DriverException ex) {
                                throw new FunctionException(ex);
                        }
                }
        }

        @Override
        public String getDescription() {
                return "Store a dataset into a ply format";
        }

        @Override
        public FunctionSignature[] getFunctionSignatures() {
                return new FunctionSignature[]{
                                new ExecutorFunctionSignature(new TableArgument(TableDefinition.GEOMETRY),
                                ScalarArgument.STRING)};

        }

        @Override
        public String getName() {
                return "ST_PLYExporter";
        }

        @Override
        public String getSqlOrder() {
                return "EXECUTE ST_PLYExporter(table [, '/tmp/file.ply']);";
        }
}
