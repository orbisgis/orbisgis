/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.components.sif;

import java.text.ParseException;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.InputType;
import org.orbisgis.sif.multiInputPanel.MIPValidationDouble;
import org.orbisgis.sif.multiInputPanel.MIPValidationFloat;
import org.orbisgis.sif.multiInputPanel.MIPValidationInteger;
import org.orbisgis.sif.multiInputPanel.MIPValidationLong;
import org.orbisgis.sif.multiInputPanel.MIPValidationNumeric;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;

/**
 * An input panel created in order to insert a valid DataSource row.
 */
public class AskValidRow extends MultiInputPanel {

	private int fieldCount;
	private DataSource ds;
	private int[] types;

	public AskValidRow(String title, DataSource ds) throws DriverException {
		super(title);
		this.ds = ds;
		this.fieldCount = ds.getFieldCount();
		this.types = new int[fieldCount];
		for (int i = 0; i < fieldCount; i++) {
            String fieldName = "f" + i;
			types[i] = ds.getFieldType(i).getTypeCode();
            InputType input;
            //Field
            switch (types[i]) {
                case Type.BOOLEAN:
                    input = new CheckBoxChoice(false);
                    break;
                default:
                    input =  new TextBoxType();
            }
            // Constraint
            switch (types[i]) {
                case Type.INT:
                    addValidation(new MIPValidationInteger(fieldName,ds.getFieldName(i)));
                    break;
                case Type.LONG:
                    addValidation(new MIPValidationLong(fieldName,ds.getFieldName(i)));
                    break;
                case Type.FLOAT:
                    addValidation(new MIPValidationFloat(fieldName,ds.getFieldName(i)));
                    break;
                case Type.DOUBLE:
                    addValidation(new MIPValidationDouble(fieldName, ds.getFieldName(i)));
                    break;
            }
			addInput(fieldName, ds.getFieldName(i),input);
		}
	}

	@Override
	public String validateInput() {
        String errMess = super.validateInput();
		try {
			for (int i = 0; i < fieldCount; i++) {
				String input = getInput("f" + i);
				Value inputValue = ValueFactory.createNullValue();
				if (!input.isEmpty()) {
                        inputValue = AskValidValue.inputToValue(input, types[i]);
				}
				String error = AskValidValue.validateValue(ds, inputValue, i,
						types[i]);
				if(error!=null) {
					return error;
                }
			}
		} catch (Exception e) {
            if(errMess!=null && !errMess.isEmpty()) {
			    return errMess;
            } else {
                return e.getMessage();
            }
		}
		return null;
	}

    /**
     * Get all fields
     * @return A valid row
     * @throws ParseException If the type conversion failed
     */
	public Value[] getRow() throws ParseException {
		Value[] ret = new Value[fieldCount];
		for (int i = 0; i < ret.length; i++) {
			String input = getInput("f" + i);
			if (!input.isEmpty()) {
				ret[i] = AskValidValue.inputToValue(input, types[i]);
			} else {
				ret[i] = ValueFactory.createNullValue();
			}
		}
		return ret;
	}

}
