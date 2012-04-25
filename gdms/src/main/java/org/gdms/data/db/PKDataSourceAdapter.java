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
package org.gdms.data.db;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.PrimaryKeyConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;

/**
 * This class simulates a DataSource with primary key that uses the getPK method
 * as a pk field. This class only wraps DataSource without primary key and so
 * the primary key is a single integer value
 *
 * @author Fernando Gonzalez Cortes
 *
 */
class PKDataSourceAdapter extends AbstractDataSourceDecorator {

	private DefaultMetadata met;

	PKDataSourceAdapter(DataSource ds) {
		super(ds);
	}

        @Override
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

        @Override
	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		if (fieldId == 0) {
			return getDataSource().getPK((int) rowIndex).getAsValueCollection().get(0);
		} else {
			return getDataSource().getFieldValue(rowIndex, fieldId - 1);
		}
	}

        @Override
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
