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
package org.gdms.data.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.orbisgis.progress.NullProgressMonitor;

import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.RightValueDecorator;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalRowAddress;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileDriver;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.Source;

/**
 * Adapter for file drivers.
 */
public class FileDataSourceAdapter extends DriverDataSource implements
		Commiter, CommitListener {

	private FileDriver driver;

	private File file;

	private boolean realSource;

        private static final Logger LOG = Logger.getLogger(FileDataSourceAdapter.class);

	/**
         * Creates a new FileDataSourceAdapter based on a FileDriver for a given file.
	 * @param src a source
	 * @param file a file
	 * @param driver a FileDriver for the file
	 * @param commitable if the file is the source itself
	 */
	public FileDataSourceAdapter(Source src, File file, FileDriver driver,
			boolean commitable) {
		super(src);
		this.driver = driver;
		this.file = file;
		this.realSource = commitable;
                LOG.trace("Constructor");
	}

        @Override
	public FileDriver getDriver() {
		return driver;
	}

	@Override
	public void saveData(DataSet ds) throws DriverException {
            LOG.trace("Saving Data");
		((FileReadWriteDriver) driver).writeFile(file, ds,
				new NullProgressMonitor());
	}

        @Override
	public void open() throws DriverException {
            LOG.trace("Opening");
		driver.open();
		fireOpen(this);

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.addCommitListener(this);
	}

        @Override
	public void close() throws DriverException {
            LOG.trace("Closing");
		driver.close();
		fireCancel(this);

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.removeCommitListener(this);
	}

	@Override
	public boolean commit(List<PhysicalRowAddress> rowsAddresses,
			String[] fieldNames, List<EditionInfo> schemaActions,
			List<EditionInfo> editionActions,
			List<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException {
            LOG.trace("Commiting");
		String tempFileName = getDataSourceFactory().getTempFile() + "."
				+ driver.getFileExtensions()[0];
		File temp = new File(tempFileName);
		((FileReadWriteDriver) driver).writeFile(temp, new RightValueDecorator(
				modifiedSource), new NullProgressMonitor());
		try {
			driver.close();
		} catch (DriverException e) {
			throw new DriverException("Cannot free resources: data writen in "
					+ temp.getAbsolutePath(), e);
		}
		try {
			((FileReadWriteDriver) driver).copy(temp, file);
		} catch (IOException e) {
			throw new DriverException("Cannot copy file: data writen in "
					+ temp.getAbsolutePath(), e);
		}

                temp.delete();

		driver.open();

		fireCommit(this);

		return false;
	}

	@Override
	public boolean isEditable() {
		return super.isEditable() && realSource;
	}

        @Override
	public void commitDone(String name) throws DriverException {
		if (realSource) {
			sync();
		}
	}

        @Override
	public void syncWithSource() throws DriverException {
            LOG.trace("Force sync with source");
		sync();
	}

	private void sync() throws DriverException {
		driver.close();
		driver.open();
	}

        @Override
	public void isCommiting(String name, Object source) {
	}
}