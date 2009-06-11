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

import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.driver.DBReadWriteDriver;
import org.gdms.driver.DriverException;

/**
 * Location info of a PK
 *
 * @author Fernando Gonzalez Cortes
 */
public class UpdateEditionInfo extends OriginalEditionInfo {

	private ValueCollection originalPK;

	private PhysicalDirection dir;

	/**
	 * Creates a new FlagIndexPair.
	 *
	 * @param index
	 *            Index on the data source where the pk is
	 * @param originalPK
	 *            Value of the PK fields when the edition started
	 */
	public UpdateEditionInfo(ValueCollection originalPK, PhysicalDirection dir) {
		this.originalPK = originalPK;
		this.dir = dir;
	}

	public ValueCollection getOriginalPK() {
		return originalPK;
	}

	public String getSQL(String tableName, String[] pkNames,
			String[] fieldNames, DBReadWriteDriver driver)
			throws DriverException {
		Metadata metadata = dir.getMetadata();
		Type[] fieldTypes = new Type[metadata.getFieldCount()];
		for (int i = 0; i < metadata.getFieldCount(); i++) {
			fieldTypes[i] = metadata.getFieldType(i);
		}
		Value[] row = new Value[fieldNames.length];
		for (int i = 0; i < row.length; i++) {
			row[i] = dir.getFieldValue(i);
		}
		return driver.getUpdateSQL(tableName, pkNames, originalPK.getValues(),
				fieldNames, fieldTypes, row);
	}

	@Override
	public ValueCollection getPK() {
		return originalPK;
	}
}
