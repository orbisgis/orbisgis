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
package org.gdms.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.edition.Commiter;
import org.gdms.data.indexes.IndexException;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.indexes.ResultIterator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.ReadWriteDriver;
import org.gdms.source.Source;
import org.gdms.sql.strategies.FullIterator;

/**
 * Base class for all the DataSources that directly access a driver. getDriver()
 * returns a not null instance
 *
 * @author Fernando Gonzalez Cortes
 *
 */
public abstract class DriverDataSource extends DataSourceCommonImpl {

	private Source source;

	private ArrayList<DataSourceListener> listeners = new ArrayList<DataSourceListener>();

	public DriverDataSource(Source source) {
		this.source = source;
	}

	public void addDataSourceListener(DataSourceListener listener) {
		listeners.add(listener);
	}

	public void removeDataSourceListener(DataSourceListener listener) {
		listeners.remove(listener);
	}

	protected void fireOpen(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.open(ds);
		}
	}

	protected void fireCancel(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.cancel(ds);
		}
	}

	protected void fireCommit(DataSource ds) {
		for (DataSourceListener listener : listeners) {
			listener.commit(ds);
		}
	}

	public Number[] getScope(int dimension) throws DriverException {
		return getDriver().getScope(dimension);
	}

	public boolean isEditable() {
		final ReadOnlyDriver driver = getDriver();

		if (driver instanceof ReadWriteDriver) {
			return ((ReadWriteDriver) driver).isCommitable();
		} else {
			return false;
		}
	}

	public void commit() throws DriverException {
		throw new UnsupportedOperationException("This DataSource has "
				+ "no committing capabilities");
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getFieldValue(long, int)
	 */
	public synchronized Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return getDriver().getFieldValue(rowIndex, fieldId);
	}

	/**
	 * @see org.gdms.driver.ReadAccess#getRowCount()
	 */
	public long getRowCount() throws DriverException {
		return getDriver().getRowCount();
	}

	/**
	 * @see org.gdms.data.DataSource#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		return getDriver().getMetadata();
	}

	public Iterator<Integer> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		try {
			int[] ret = getDataSourceFactory().getIndexManager().queryIndex(
					getName(), queryIndex);

			if (ret != null) {
				return new ResultIterator(ret);
			} else {
				return new FullIterator(this);
			}
		} catch (IndexException e) {
			throw new DriverException(e);
		} catch (NoSuchTableException e) {
			throw new RuntimeException(e);
		}
	}

	public Commiter getCommiter() {
		return (Commiter) this;
	}

	public String[] getReferencedSources() {
		return source.getReferencedSources();
	}

	public Source getSource() {
		return source;
	}

}
