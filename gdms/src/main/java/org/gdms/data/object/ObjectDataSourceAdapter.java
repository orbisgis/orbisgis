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
package org.gdms.data.object;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DriverDataSource;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.DeleteEditionInfo;
import org.gdms.data.edition.EditionInfo;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.ObjectReadWriteDriver;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;
import org.gdms.source.Source;
import org.orbisgis.progress.NullProgressMonitor;

public class ObjectDataSourceAdapter extends DriverDataSource implements
		Commiter, CommitListener {

	private ObjectDriver driver;

	public ObjectDataSourceAdapter(Source source, ObjectDriver driver) {
		super(source);
		this.driver = driver;
	}

	public void open() throws DriverException {
		driver.start();
		fireOpen(this);

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.addCommitListener(this);
	}

	public void close() throws DriverException, AlreadyClosedException {
		driver.stop();
		fireCancel(this);

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.removeCommitListener(this);
	}

	public void saveData(DataSource ds) throws DriverException {
		ds.open();
		((ObjectReadWriteDriver) driver).write(ds, new NullProgressMonitor());
		ds.close();
	}

	public ObjectDriver getDriver() {
		return driver;
	}

	public long[] getWhereFilter() throws IOException {
		return null;
	}

	@Override
	public boolean commit(List<PhysicalDirection> rowsDirections,
			String[] fieldName, ArrayList<EditionInfo> schemaActions,
			ArrayList<EditionInfo> editionActions,
			ArrayList<DeleteEditionInfo> deletedPKs, DataSource modifiedSource)
			throws DriverException {
		boolean rowChanged = ((ObjectReadWriteDriver) driver).write(modifiedSource,
				new NullProgressMonitor());
		driver.stop();
		fireCommit(this);
		
		return rowChanged;
	}

	public void commitDone(String name) throws DriverException {
		sync();
	}

	public void syncWithSource() throws DriverException {
		sync();
	}

	private void sync() throws DriverException {
		driver.start();
		driver.stop();
	}

	public void isCommiting(String name, Object source) {
	}
}