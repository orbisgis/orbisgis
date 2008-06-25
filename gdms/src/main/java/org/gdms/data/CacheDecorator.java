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

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.source.CommitListener;
import org.gdms.source.DefaultSourceManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

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
	public void close() throws DriverException, AlreadyClosedException {
		DefaultSourceManager sm = (DefaultSourceManager) getDataSourceFactory()
				.getSourceManager();
		sm.removeCommitListener(this);

		getDataSource().close();
	}

	public Metadata getMetadata() throws DriverException {
		if (metadata == null) {
			metadata = getDataSource().getMetadata();
		}

		return metadata;
	}

	public long getRowCount() throws DriverException {
		if (rc == -1) {
			rc = getDataSource().getRowCount();
		}

		return rc;
	}

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
						if (m.getFieldType(j).getTypeCode() == Type.GEOMETRY) {

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

	public void isCommiting(String name, Object source) {
	}

	public void commitDone(String name) {
		sync();
	}

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