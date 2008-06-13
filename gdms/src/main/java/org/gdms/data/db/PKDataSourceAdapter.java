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
package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DriverException;

/**
 * This class simulates a DataSource with primary key that uses the getPK method
 * as a pk field. This class only wraps DataSource without primary key and so
 * the primary key is a single integer value
 *
 * @author Fernando Gonzalez Cortes
 *
 */
class PKDataSourceAdapter extends AbstractDataSourceDecorator implements
		DataSource {

	private DefaultMetadata met;

	public PKDataSourceAdapter(DataSource ds) {
		super(ds);
	}

	public Metadata getMetadata() throws DriverException {
		if (met == null) {
			met = new DefaultMetadata(getDataSource().getMetadata());

			try {
				met.addField(0, getPKName(), TypeFactory.createType(Type.INT)
						.getTypeCode(),
						new Constraint[] { new PrimaryKeyConstraint() });
			} catch (InvalidTypeException e) {
				throw new DriverException(e);
			}
		}
		return met;
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (fieldId == 0) {
			return ((ValueCollection) getDataSource().getPK((int) rowIndex))
					.getValues()[0];
		} else {
			return getDataSource().getFieldValue(rowIndex, fieldId - 1);
		}
	}

	public long getRowCount() throws DriverException {
		return getDataSource().getRowCount();
	}

	private String getPKName() throws DriverException {
		int i = 0;
		String pkName = "pk_" + i;
		while (getDataSource().getFieldIndexByName(pkName) != -1) {
			i++;
			pkName = "pk_" + i;
		}
		return pkName;
	}
}
