/** OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.sql.engine;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.driver.DataSet;
import org.orbisgis.progress.ProgressMonitor;

/**
 * This class represents a SQL Statement to be executed.
 * @author Antoine Gourlay
 */
public class SqlStatement {

        private String sql;
        private ExecutionGraph graph;
        private boolean clean = true;
        private DataSourceFactory dsf;

        /**
         * Creates a new SQL Statement with the given graph, generated from
         * the given SQL.
         * @param sql a sql script
         * @param graph the graph to be executed
         */
        public SqlStatement(String sql, ExecutionGraph graph) {
                this.sql = sql;
                this.graph = graph;
        }

        /**
         * Returns the SQL script associated with this statement.
         * @return a script
         */
        public String getSQL() {
                return sql;
        }

        /**
         * Prepares the statement for running with the given <tt>dsf</tt>.
         *
         * This makes the necessary validation against the tables in the dsf.
         * @param dsf
         */
        public void prepare(DataSourceFactory dsf) {
                prepare(dsf, null);
        }
        
        public void prepare(DataSourceFactory dsf, ProgressMonitor pm) {
                graph.setProgressMonitor(pm);
                if (dsf != this.dsf) {
                        this.dsf = dsf;
                        graph.prepare(dsf);
                        clean = false;
                }
        }

        /**
         * Executes the statement. Needs to be called after <code>prepare(dsf)</code>.
         *
         * Note: the result of the statement is written to disk before returning.
         * @return the result of the statement, or null if there is none.
         * @throws DriverException  
         */
        public DataSet execute() throws DriverException {
                return graph.execute();
        }

        /**
         * Cleans up the graph. The <code>prepare</code> method can be called
         * again after <code>cleanUp</code> to prepare for another execution.
         */
        public void cleanUp() {
                if (!clean) {
                        doCleanUp();
                }
        }

        private void doCleanUp() {
                graph.cleanUp();
                clean = true;
                dsf = null;
        }

        /**
         * Returns all referenced sources.
         *
         * This method can only be called between calls to <code>prepare</code>
         * and <code>cleanUp/<code>.
         * @return an array of source names.
         */
        public String[] getReferencedSources() {
                return graph.getReferencedSources();
        }

        /**
         * Gets the result metadata of this statement.
         * @return the metadata, or null if there is no result.
         * @throws DriverException
         */
        public Metadata getResultMetadata() throws DriverException {
                return graph.getResultMetadata();
        }
}
