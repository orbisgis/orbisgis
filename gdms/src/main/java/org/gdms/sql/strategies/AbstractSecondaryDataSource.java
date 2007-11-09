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
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
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
package org.gdms.sql.strategies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.Commiter;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.source.Source;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * operation layer DataSource base class
 *
 * @author Fernando Gonzalez Cortes
 */
public abstract class AbstractSecondaryDataSource extends DataSourceCommonImpl {
	private String sql;

	private Envelope spatialScope;

	public AbstractSecondaryDataSource() {
		super(null);
	}

	/**
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return null;
	}

	@Override
	public DataSourceFactory getDataSourceFactory() {
		return getDataSourceFactoryFromDecorated();
	}

	protected abstract DataSourceFactory getDataSourceFactoryFromDecorated();

	/**
	 * sets the sql query of this operation DataSource. It's needed by the
	 * getMemento method which contains basically the sql
	 *
	 * @param sql
	 *            query
	 */
	public void setSQL(String sql) {
		this.sql = sql;
	}

	/**
	 * Gets the SQL string that created this DataSource
	 *
	 * @return String with the query
	 */
	public String getSQL() {
		return sql;
	}

	public void commit() throws DriverException {
	}

	public void saveData(DataSource ds) throws DriverException {
		throw new UnsupportedOperationException(
				"OperationDataSources are not editable");
	}

	public String check(int fieldId, Value value) throws DriverException {
		if (getMetadata().getFieldType(fieldId).getTypeCode() == value
				.getType()) {
			return null;
		} else {
			return "Types does not match";
		}
	}

	public ReadOnlyDriver getDriver() {
		return null;
	}

	public boolean isEditable() {
		return false;
	}

	public Number[] getScope(int dimension) throws DriverException {
		if ((dimension == ReadOnlyDriver.X) || (dimension == ReadOnlyDriver.Y)) {
			if (spatialScope == null) {
				for (int i = 0; i < getRowCount(); i++) {
					Metadata m = getMetadata();
					for (int j = 0; j < m.getFieldCount(); j++) {
						if (m.getFieldType(j).getTypeCode() == Type.GEOMETRY) {
							Geometry g = ((GeometryValue) getFieldValue(i, j))
									.getGeom();
							if (spatialScope == null) {
								spatialScope = new Envelope(g
										.getEnvelopeInternal());
							} else {
								spatialScope.expandToInclude(g
										.getEnvelopeInternal());
							}
						}
					}
				}
			}

			if (spatialScope == null) {
				return null;
			} else if (dimension == ReadOnlyDriver.X) {
				return new Number[] { spatialScope.getMinX(),
						spatialScope.getMaxX() };
			} else if (dimension == ReadOnlyDriver.Y) {
				return new Number[] { spatialScope.getMinY(),
						spatialScope.getMaxY() };
			} else {
				throw new UnsupportedOperationException("Not implemented");
			}
		} else {
			return null;
		}
	}

	public Iterator<PhysicalDirection> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		return null;
	}

	public Commiter getCommiter() {
		return null;
	}

	public Source getSource() {
		return getDataSourceFactory().getSourceManager().getSource(
				this.getName());
	}

	public String[] getReferencedSources() {
		ArrayList<String> ret = new ArrayList<String>();
		Source src = getSource();
		if (src != null) {
			ret.add(src.getName());
			String[] referencedSources = src.getReferencedSources();
			for (String referenced : referencedSources) {
				ret.add(referenced);
			}

			return ret.toArray(new String[0]);
		} else {
			return getRelatedSourcesDelegating();
		}
	}

	protected abstract String[] getRelatedSourcesDelegating();
}