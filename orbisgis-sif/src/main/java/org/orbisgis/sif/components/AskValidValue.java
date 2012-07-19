/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sif.components;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;

public class AskValidValue extends AskValue {

	private DataSource ds;
	private int fieldIndex;
	private int fieldType;

	public AskValidValue(DataSource ds, int fieldIndex) throws DriverException {
		super("Field '" + ds.getFieldName(fieldIndex) + "'", null, null);
		this.ds = ds;
		this.fieldIndex = fieldIndex;
		this.fieldType = ds.getFieldType(fieldIndex).getTypeCode();
	}

	@Override
	public String validateInput() {
		try {
			return validateValue(ds, inputToValue(getValue(), fieldType),
					fieldIndex, fieldType);
		} catch (ParseException e) {
			return e.getMessage();
		}
	}

	public static String validateValue(DataSource ds, Value inputValue,
			int fieldIndex, int fieldType) {
		try {
			String error = ds.check(fieldIndex, inputValue);
			if (error != null) {
				return error;
			}
		} catch (NumberFormatException e) {
			return "Invalid number" + e.getMessage();
		} catch (DriverException e) {
			return e.getMessage();
		}

		return null;
	}

	public Value getUserValue() throws ParseException {
		String userInput = getValue();
		return inputToValue(userInput, fieldType);
	}

	public static Value inputToValue(String userInput, int fieldType)
			throws ParseException {
		Value value = ValueFactory.createValueByType(userInput, fieldType);
		return value;
	}
}
