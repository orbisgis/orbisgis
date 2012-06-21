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
package org.orbisgis.core.ui.components.sif;

import java.text.ParseException;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.core.sif.SQLUIPanel;
import org.orbisgis.core.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.core.sif.multiInputPanel.StringType;

public class AskValidRow extends MultiInputPanel implements SQLUIPanel {

	private int fieldCount;
	private DataSource ds;
	private int[] types;

	public AskValidRow(String title, DataSource ds) throws DriverException {
		super(title);
		this.ds = ds;
		this.fieldCount = ds.getFieldCount();
		this.types = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
			types[i] = ds.getFieldType(i).getTypeCode();
			addInput("f" + i, ds.getFieldName(i), new StringType());
		}
	}

	@Override
	public String validateInput() {
		try {
			for (int i = 0; i < fieldCount; i++) {
				String input = getInput("f" + i);
				Value inputValue = ValueFactory.createNullValue();
				if (input.length() > 0) {
					inputValue = AskValidValue.inputToValue(input, types[i]);
				}
				String error = AskValidValue.validateValue(ds, inputValue, i,
						types[i]);
				if (error != null) {
					return error;
				}
			}
		} catch (ParseException e) {
			return e.getMessage();
		}
		return super.validateInput();
	}

	public Value[] getRow() throws ParseException {
		Value[] ret = new Value[fieldCount];
		for (int i = 0; i < ret.length; i++) {
			String input = getInput("f" + i);
			if (input.length() > 0) {
				ret[i] = AskValidValue.inputToValue(input, types[i]);
			} else {
				ret[i] = ValueFactory.createNullValue();
			}
		}
		return ret;
	}

}
