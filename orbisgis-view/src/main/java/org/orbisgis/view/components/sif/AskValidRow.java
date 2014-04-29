/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.InputType;
import org.orbisgis.sif.multiInputPanel.MIPValidationDouble;
import org.orbisgis.sif.multiInputPanel.MIPValidationFloat;
import org.orbisgis.sif.multiInputPanel.MIPValidationInteger;
import org.orbisgis.sif.multiInputPanel.MIPValidationLong;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;

import javax.sql.DataSource;

/**
 * An input panel created in order to insert a valid DataSource row.
 */
public class AskValidRow extends MultiInputPanel {

	private int fieldCount;
	private DataSource ds;
    private TableLocation location;
    private List<Integer> types;
    private List<String> fieldsNameList;

    public AskValidRow(String title, DataSource ds,String tableReference) throws SQLException {
		super(title);
		this.ds = ds;
        location = TableLocation.parse(tableReference);
        types = new ArrayList<>(10);
        fieldsNameList = new ArrayList<>(10);
        try(Connection connection = ds.getConnection();
            ResultSet rs = connection.getMetaData().getColumns(location.getCatalog(), location.getSchema(), location.getTable(), null)) {
            while(rs.next()) {
                types.add(rs.getInt("DATA_TYPE"));
                fieldsNameList.add(rs.getString("TABLE_NAME"));
                this.fieldCount++;
            }
        }
		this.fieldCount = types.size();
		for (int i = 0; i < fieldCount; i++) {
            String fieldName = "f" + i;
            String fieldLabel = fieldsNameList.get(i);
            InputType input;
            int type = types.get(i);
            //Field
            switch (type) {
                case Types.BOOLEAN:
                    input = new CheckBoxChoice(false);
                    break;
                default:
                    input =  new TextBoxType();
            }
            // Constraint
            switch (type) {
                case Types.INTEGER:
                case Types.SMALLINT:
                case Types.TINYINT:
                    addValidation(new MIPValidationInteger(fieldName,fieldLabel));
                    break;
                case Types.NUMERIC:
                case Types.DECIMAL:
                case Types.BIGINT:
                    addValidation(new MIPValidationLong(fieldName,fieldLabel));
                    break;
                case Types.FLOAT:
                    addValidation(new MIPValidationFloat(fieldName,fieldLabel));
                    break;
                case Types.REAL:
                case Types.DOUBLE:
                    addValidation(new MIPValidationDouble(fieldName, fieldLabel));
                    break;
            }
			addInput(fieldName, fieldLabel,input);
		}
	}

	@Override
	public String validateInput() {
        // Try to insert the row in a transaction
        StringBuilder fieldNames = new StringBuilder();
        StringBuilder parameters = new StringBuilder();
        for(String fieldName : fieldsNameList) {
            if(fieldNames.length() != 0) {
                fieldNames.append(", ");
                parameters.append(", ");
            }
            fieldNames.append("\"");
            fieldNames.append(fieldName);
            fieldNames.append("\"");
            parameters.append("?");
        }
        try(Connection connection = ds.getConnection()) {
            connection.setAutoCommit(false);
            try(PreparedStatement st = connection.prepareStatement(String.format("INSERT INTO %s(%s) VALUES (%s)",location,fieldNames.toString(),parameters.toString()))) {
                // Set parameters value
                for (int i = 0; i < fieldCount; i++) {
                    String input = getInput("f" + i);
                    Object value = null;
                    if (!input.isEmpty()) {
                        value = MetaData.castToSQLType(input, types.get(i));
                    }
                    st.setObject(i+1, value);
                }
                st.execute();
            } finally {
                connection.rollback();
            }
            // Ok
            return "";
        } catch (SQLException ex) {
            return ex.getLocalizedMessage();
        }
	}

    /**
     * Get all fields
     * @return A valid row
     * @throws ParseException If the type conversion failed
     */
	public Object[] getRow() throws ParseException {
		Object[] ret = new Object[fieldCount];
		for (int i = 0; i < ret.length; i++) {
			String input = getInput("f" + i);
			ret[i] = MetaData.castToSQLType(input, types.get(i));
		}
		return ret;
	}

}
