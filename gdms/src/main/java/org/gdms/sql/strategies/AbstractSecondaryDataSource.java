package org.gdms.sql.strategies;

import java.io.IOException;
import java.util.Iterator;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCommonImpl;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.indexes.IndexQuery;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
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
		super(null, null);
	}

	/**
	 * @see org.gdms.data.DataSource#getWhereFilter()
	 */
	public long[] getWhereFilter() throws IOException {
		return null;
	}

	/**
	 * @see org.gdms.data.DataSource#setDataSourceFactory(org.gdms.data.DataSourceFactory)
	 */
	public void setDataSourceFactory(DataSourceFactory dsf) {
		super.setDataSourceFactory(dsf);
		setName(dsf.getUID());
		setAlias(null);
	}

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
			} else if (dimension == ReadOnlyDriver.X) {
				return new Number[] { spatialScope.getMinY(),
						spatialScope.getMaxY() };
			} else {
				throw new UnsupportedOperationException("Not implemented");
			}
		} else {
			return null;
		}
	}

	public Iterator<Row> queryIndex(IndexQuery queryIndex)
			throws DriverException {
		return null;
	}

}