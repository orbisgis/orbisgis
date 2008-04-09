/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

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

	public DriverDataSource(Source source) {
		this.source = source;
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
			int[] ret = getDataSourceFactory()
					.getIndexManager().queryIndex(getName(), queryIndex);

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
