/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

/**
 * A row address within the internal buffer for edition.
 * @author Fernando Gonzalez Cortes
 */
class InternalBufferRowAddress implements PhysicalRowAddress {

	private InternalBuffer buffer;

	private int row;

	private ValueCollection pk;

	private DataSource dataSource;

	InternalBufferRowAddress(ValueCollection pk, InternalBuffer buffer,
			int row, DataSource dataSource) {
		this.row = row;
		this.buffer = buffer;
		this.pk = pk;
		this.dataSource = dataSource;
	}

        @Override
	public Value getFieldValue(int fieldId) throws DriverException {
		return buffer.getFieldValue(row, fieldId);
	}

	void setFieldValue(int fieldId, Value value) {
		buffer.setFieldValue(row, fieldId, value);
	}

        @Override
	public ValueCollection getPK() throws DriverException {
		return pk;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InternalBufferRowAddress) {
			InternalBufferRowAddress od = (InternalBufferRowAddress) obj;
			return (od.buffer == buffer) && (od.row == row) && (od.pk == pk);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return buffer.hashCode() + row + pk.hashCode();
	}

        @Override
	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

}
