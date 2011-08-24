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
package org.gdms.data.memory;

import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalRowAddress;
import org.gdms.driver.DriverException;
import org.gdms.driver.MemoryDriver;
import org.gdms.driver.EditableMemoryDriver;
import org.gdms.source.CommitListener;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.NullProgressMonitor;

/**
 * DataSource implementation for accessing <code>MemoryDriver</code> sources.
 * @author Antoine Gourlay
 */
public class MemoryDataSourceAdapter extends DriverDataSource implements
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
		driver.start();
		fireOpen(this);

		SourceManager sm = getDataSourceFactory().getSourceManager();
		sm.addCommitListener(this);
	}

        @Override
	public void close() throws DriverException {
            LOG.trace("Closing");
		driver.stop();
		fireCancel(this);

		SourceManager sm = getDataSourceFactory().getSourceManager();
		sm.removeCommitListener(this);
	}

        @Override
	public void saveData(DataSource ds) throws DriverException {
            LOG.trace("Saving data");
		ds.open();
		((EditableMemoryDriver) driver).write(ds, new NullProgressMonitor());
		ds.close();
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
		driver.stop();
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
		driver.start();
		driver.stop();
	}

        @Override
	public void isCommiting(String name, Object source) {
	}
}