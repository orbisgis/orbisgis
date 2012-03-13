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
package org.gdms.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.gdms.data.sql.SQLEvent;
import org.gdms.data.sql.SQLSourceDefinition;
import org.gdms.driver.DriverException;
import org.gdms.sql.engine.SQLEngine;
import org.gdms.sql.engine.SqlStatement;
import org.gdms.sql.engine.ParseException;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * Main entry point for Gdmsql.
 * @author Antoine Gourlay
 */
public class SQLDataSourceFactory extends DataSourceFactory {

        private final List<DataSourceFactoryListener> listeners = new ArrayList<DataSourceFactoryListener>();
        private static final Logger LOG = Logger.getLogger(SQLDataSourceFactory.class);
        private SQLEngine sqlEngine;

        /**
         * Creates a new {@code SQLDataSourceFactory} with a <tt>sourceInfoDir</tt>
         * set to a sub-folder '.gdms' in the user's home.
         */
        public SQLDataSourceFactory() {
                super(new String[] {"org.gdms.source.sqldirectory"});
        }

        /**
         * Creates a new {@code SQLDataSourceFactory}.
         * @param sourceInfoDir the directory where the sources are stored
         */
        public SQLDataSourceFactory(String sourceInfoDir) {
                super(sourceInfoDir, new String[] {"org.gdms.source.sqldirectory"});
        }

        /**
         * Creates a new {@code SQLDataSourceFactory}.
         * @param sourceInfoDir the directory where the sources are stored
         * @param tempDir the directory where temporary sources are stored
         */
        public SQLDataSourceFactory(String sourceInfoDir, String tempDir) {
                super(sourceInfoDir, tempDir, new String[] {"org.gdms.source.sqldirectory"});
        }
        
        /**
         * Creates a new {@code SQLDataSourceFactory}.
         * @param sourceInfoDir the directory where the sources are stored
         * @param tempDir the directory where temporary sources are stored
         * @param plugInDir  the directory where plugIn jar files are stored 
         */
        public SQLDataSourceFactory(String sourceInfoDir, String tempDir, String plugInDir) {
                super(sourceInfoDir, tempDir, new String[] {"org.gdms.source.sqldirectory"}, plugInDir);
        }

        /**
         * Gets a DataSource instance to access the result of a query
         *
         * @param sql the SQL query to execute
         * @return a DataSource mapped to the result of the query
         *
         * @throws DriverLoadException
         *             If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         *             If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         * @throws NoSuchTableException 
         */
        public final DataSource getDataSourceFromSQL(String sql)
                throws DataSourceCreationException,
                DriverException, ParseException,
                NoSuchTableException {
                return getDataSourceFromSQL(sql, DEFAULT, new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the result of a query
         *
         * @param sql the SQL query to execute
         * @return a DataSource mapped to the result of the query
         * @param mode
         *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @throws DriverLoadException
         *             If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         *             If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public final DataSource getDataSourceFromSQL(String sql, int mode)
                throws DataSourceCreationException,
                DriverException, ParseException {
                return getDataSourceFromSQL(sql, mode, new NullProgressMonitor());
        }

        /**
         * Gets a DataSource instance to access the file with the default mode
         *
         * @param sql the SQL query to execute
         * @param pm
         *            Instance that monitors the process. Can be null
         * @return
         *
         * @throws DriverLoadException
         *             If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         *             If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public final DataSource getDataSourceFromSQL(String sql, ProgressMonitor pm)
                throws DataSourceCreationException,
                DriverException, ParseException {
                return getDataSourceFromSQL(sql, DEFAULT, pm);
        }

        /**
         * Gets a DataSource instance to access the result of the SQL
         *
         * @param sql the SQL query to execute
         * @param mode
         *            To enable undo/redo operations UNDOABLE. NORMAL otherwise
         * @param pm
         *            Instance that monitors the process. Can be null
         * @return The result of the instruction or null if the execution was
         *         canceled
         *
         * @throws DriverLoadException
         *             If there isn't a suitable driver for such a file
         * @throws DataSourceCreationException
         *             If the instance creation fails
         * @throws DriverException
         * @throws ParseException
         */
        public final DataSource getDataSourceFromSQL(String sql, int mode,
                ProgressMonitor pm) throws
                DataSourceCreationException, DriverException, ParseException {
                LOG.trace("Getting datasource from SQL :\n" + sql);
                if (pm == null) {
                        pm = new NullProgressMonitor();
                }
                SQLEngine engine = new SQLEngine(this);
                SqlStatement[] statement = engine.parse(sql);

                return getDataSource(statement[0], mode, pm);
        }

        /**
         * Gets a DataSource instance to access the result of the instruction
         *
         * @param instruction
         *            Instruction to evaluate.
         * @param mode
         *            The DataSource mode {@link #EDITABLE} {@link #STATUS_CHECK}
         *            {@link #NORMAL} {@link #DEFAULT}
         * @param pm
         *            To monitor progress and cancel
         *
         * @return
         * @throws DataSourceCreationException
         */
        public final DataSource getDataSource(SqlStatement instruction, int mode,
                ProgressMonitor pm) throws DataSourceCreationException {
                return getDataSource(new SQLSourceDefinition(instruction), mode, pm);
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         * @param pm
         * @throws ParseException
         * @throws DriverException
         */
        public final void executeSQL(String sql, ProgressMonitor pm)
                throws ParseException, DriverException {
                executeSQL(sql, pm, DEFAULT);
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         * @throws ParseException
         * @throws DriverException
         * @throws SemanticException if something wrong happens during query validation
         */
        public final void executeSQL(String sql) throws ParseException,
                DriverException {
                executeSQL(sql, new NullProgressMonitor(), DEFAULT);
        }

        /**
         * Executes a SQL statement
         *
         * @param sql
         *            sql statement
         * @param pm
         *
         * @param mode
         * @throws ParseException
         *             If the sql is not well formed
         * @throws DriverException
         *             If there is a problem accessing the sources
         */
        public final void executeSQL(String sql, ProgressMonitor pm, int mode)
                throws ParseException, DriverException {
                LOG.trace("Execute SQL Statement" + '\n' + sql);
                if (!sql.trim().endsWith(";")) {
                        sql += ";";
                }

                fireInstructionExecuted(sql);

                SQLEngine engine = getSqlEngine();

                engine.execute(sql);
        }

        public final void fireInstructionExecuted(String sql) {
                for (DataSourceFactoryListener listener : listeners) {
                        listener.sqlExecuted(new SQLEvent(sql, this));
                }
        }

        /**
         * Adds a DataSourceFactoryListener to this DataSourceFactory
         * @param e a DataSourceFactoryListener
         * @return true if the add succeeded
         */
        public final boolean addDataSourceFactoryListener(DataSourceFactoryListener e) {
                return listeners.add(e);
        }

        /**
         * Removes a DataSourceFactoryListener from this DataSourceFactory
         * @param o a DataSourceFactoryListener
         * @return true if the removal succeeded
         */
        public final boolean removeDataSourceFactoryListener(DataSourceFactoryListener o) {
                return listeners.remove(o);
        }

        /**
         * Gets all listeners associated with this Factory.
         * @return a (possibly empty) list of listeners
         */
        public final List<DataSourceFactoryListener> getListeners() {
                return listeners;
        }

        /**
         * @param name
         * @param sql
         * @throws DriverException
         * @throws SourceAlreadyExistsException
         * @throws ParseException
         */
        public final void register(String name, String sql)
                throws ParseException,
                DriverException {
                SQLEngine engine = getSqlEngine();
                SqlStatement[] instruction = engine.parse(sql);
                getSourceManager().register(name, new SQLSourceDefinition(instruction[0]));

        }

        /**
         * @param sql
         * @return
         * @throws DriverException
         * @throws ParseException
         */
        public final String nameAndRegister(String sql) throws ParseException,
                DriverException {
                SQLEngine engine = getSqlEngine();
                SqlStatement[] instruction = engine.parse(sql);
                return getSourceManager().nameAndRegister(new SQLSourceDefinition(instruction[0]));
        }

        public final SQLEngine getSqlEngine() {
                // this is called by the super constructor, there is no problem of synchronisation.
                if (sqlEngine == null) {
                        sqlEngine = new SQLEngine(this);
                }
                return sqlEngine;
        }
}
