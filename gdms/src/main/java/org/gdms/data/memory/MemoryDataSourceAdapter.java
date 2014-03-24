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
package org.gdms.data.memory;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.NullProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalRowAddress;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.EditableMemoryDriver;
import org.gdms.driver.MemoryDriver;
import org.gdms.source.CommitListener;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;

/**
 * DataSource implementation for accessing <code>MemoryDriver</code> sources.
 * @author Antoine Gourlay
 */
public final class MemoryDataSourceAdapter extends DriverDataSource implements
        Commiter, CommitListener {

        private MemoryDriver driver;
        private static final Logger LOG = Logger.getLogger(MemoryDataSourceAdapter.class);

        /**
         * Creates a new MemoryDataSourceAdapter on the given source and driver.
         * @param source a source object
         * @param driver an MemoryDriver
         */
        public MemoryDataSourceAdapter(Source source, MemoryDriver driver) {
                super(source);
                this.driver = driver;
                LOG.trace("Constructor");
        }

        @Override
        public void open() throws DriverException {
                LOG.trace("Opening");
                driver.open();
                fireOpen(this);

                SourceManager sm = getDataSourceFactory().getSourceManager();
                sm.addCommitListener(this);
        }

        @Override
        public void close() throws DriverException {
                LOG.trace("Closing");
                driver.close();
                fireCancel(this);

                SourceManager sm = getDataSourceFactory().getSourceManager();
                sm.removeCommitListener(this);
        }

        @Override
        public void saveData(DataSet ds) throws DriverException {
                LOG.trace("Saving data");
                ((EditableMemoryDriver) driver).write(ds, new NullProgressMonitor());
        }

        @Override
        public MemoryDriver getDriver() {
                return driver;
        }

        public long[] getWhereFilter() throws IOException {
                return null;
        }

        @Override
        public boolean commit(List<PhysicalRowAddress> rowsDirections,
                String[] fieldName, List<EditionInfo> schemaActions,
                List<EditionInfo> editionActions,
                List<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
                throws DriverException {
                LOG.trace("Commiting");
                boolean rowChanged = ((EditableMemoryDriver) driver).write(modifiedSource,
                        new NullProgressMonitor());
                driver.close();
                fireCommit(this);

                return rowChanged;
        }

        @Override
        public void commitDone(String name) throws DriverException {
                sync();
        }

        @Override
        public void syncWithSource() throws DriverException {
                sync();
        }

        private void sync() throws DriverException {
                driver.open();
                driver.close();
        }

        @Override
        public void isCommiting(String name, Object source) {
        }
}