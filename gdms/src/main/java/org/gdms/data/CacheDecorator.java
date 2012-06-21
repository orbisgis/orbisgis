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
package org.gdms.data;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;

/**
 * This decorator implements caching of getMetadata, getRowCount and getScope for
 * the underlying DataSource.
 *
 * Note that is does reset its cache on its own when the underlying source is committed to.
 */

public class CacheDecorator extends AbstractDataSourceDecorator implements
		CommitListener {

	private Metadata metadata;

	private long rc;

	private Envelope extent;

	public CacheDecorator(DataSource internalDataSource) {
		super(internalDataSource);
	}

	@Override
	public void open() throws DriverException {
		commitDone(getName());
		getDataSource().open();

		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.addCommitListener(this);
	}

	@Override
	public void close() throws DriverException {
		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.removeCommitListener(this);

		getDataSource().close();
	}

        @Override
	public Metadata getMetadata() throws DriverException {
		if (metadata == null) {
			metadata = getDataSource().getMetadata();
		}

		return metadata;
	}

        @Override
	public long getRowCount() throws DriverException {
		if (rc == -1) {
			rc = getDataSource().getRowCount();
		}

		return rc;
	}

        @Override
	public Number[] getScope(int dimension) throws DriverException {
		if (extent == null) {
			Number[] x = getDataSource().getScope(X);
			Number[] y = getDataSource().getScope(Y);
			if ((x != null) && (y != null)) {
				extent = new Envelope(new Coordinate(x[0].doubleValue(), y[0]
						.doubleValue()), new Coordinate(x[1].doubleValue(),
						y[1].doubleValue()));
			} else {
				for (int i = 0; i < getRowCount(); i++) {
					Metadata m = getMetadata();
					for (int j = 0; j < m.getFieldCount(); j++) {
						if ((m.getFieldType(j).getTypeCode() & Type.GEOMETRY)!=0) {

							Value v = getFieldValue(i, j);
							if ((v != null) && (!v.isNull())) {
								Envelope r = v.getAsGeometry()
										.getEnvelopeInternal();
								if (extent == null) {
									extent = new Envelope(r);
								} else {
									extent.expandToInclude(r);
								}
							}
						}
					}
				}
			}
		}

		if (extent == null) {
			return null;
		} else {
			if (dimension == X) {
				return new Number[] { extent.getMinX(), extent.getMaxX() };
			} else if (dimension == Y) {
				return new Number[] { extent.getMinY(), extent.getMaxY() };
			} else {
				throw new UnsupportedOperationException(
						"Unsupported dimension: " + dimension);
			}
		}
	}

        @Override
	public void isCommiting(String name, Object source) {
	}

        @Override
	public void commitDone(String name) {
		sync();
	}

        @Override
	public void syncWithSource() throws DriverException {
		getDataSource().syncWithSource();
		sync();
	}

	private void sync() {
		rc = -1;
		metadata = null;
		extent = null;
	}
}