/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.editorjdbc;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        List<Boolean> generated = new ArrayList<>();
        List<String> defaultValues = new ArrayList<>();
        try(Connection connection = ds.getConnection();
            ResultSet rs = connection.getMetaData().getColumns(location.getCatalog(), location.getSchema(), location.getTable(), null)) {
            Set<String> availableAttributes = new HashSet<>();
            ResultSetMetaData rsMeta = rs.getMetaData();
            for(int i = 1; i <= rsMeta.getColumnCount(); i++) {
                availableAttributes.add(rsMeta.getColumnName(i).toUpperCase());
            }
            while(rs.next()) {
                types.add(rs.getInt("DATA_TYPE"));
                fieldsNameList.add(rs.getString("COLUMN_NAME"));
                if(availableAttributes.contains("COLUMN_DEF")) {
                    defaultValues.add(rs.getString("COLUMN_DEF"));
                } else {
                    defaultValues.add("");
                }
                if(availableAttributes.contains("IS_GENERATEDCOLUMN")) {
                    generated.add(rs.getBoolean("IS_GENERATEDCOLUMN"));
                } else if(availableAttributes.contains("IS_AUTOINCREMENT")) {
                    generated.add(rs.getBoolean("IS_AUTOINCREMENT"));
                } else {
                    generated.add(false);
                }

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
            String defaultValue = defaultValues.get(i);
            switch (type) {
                case Types.BOOLEAN:
                    input = new CheckBoxChoice(defaultValue != null && !defaultValue.isEmpty() && Boolean.getBoolean(defaultValues.get(i)));
                    break;
                default:
                    input =  new TextBoxType();
                    if(defaultValue != null && !defaultValue.isEmpty()) {
                        input.setValue(defaultValue);
                    }
            }
            // Generated field cannot be set
            if(generated.get(i)) {
                input.getComponent().setEnabled(false);
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
            return null;
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

    /**
     * Returns the list of field names.
     * @return The list of field names.
     */
    public List<String> getFieldNames(){
        return fieldsNameList;
    }

}
