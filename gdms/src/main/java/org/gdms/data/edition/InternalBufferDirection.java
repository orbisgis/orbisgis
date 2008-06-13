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
package org.gdms.data.edition;

import org.gdms.data.DataSource;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

public class InternalBufferDirection implements PhysicalDirection {

	private InternalBuffer buffer;

	private int row;

	private ValueCollection pk;

	private DataSource dataSource;

	public InternalBufferDirection(ValueCollection pk, InternalBuffer buffer,
			int row, DataSource dataSource) {
		this.row = row;
		this.buffer = buffer;
		this.pk = pk;
		this.dataSource = dataSource;
	}

	public Value getFieldValue(int fieldId) throws DriverException {
		return buffer.getFieldValue(row, fieldId);
	}

	public void setFieldValue(int fieldId, Value value) {
		buffer.setFieldValue(row, fieldId, value);
	}

	public ValueCollection getPK() throws DriverException {
		return pk;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof InternalBufferDirection) {
			InternalBufferDirection od = (InternalBufferDirection) obj;
			return (od.buffer == buffer) && (od.row == row) && (od.pk == pk);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return buffer.hashCode() + row + pk.hashCode();
	}

	public Metadata getMetadata() throws DriverException {
		return dataSource.getMetadata();
	}

}
