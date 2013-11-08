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
package org.orbisgis.view.sqlconsole.ui;

import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.h2gis.utilities.TableLocation;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A class to manage function name an type in the jlist function
 * 
 * @author Erwan Bocher
 */
public class FunctionElement {
    private static final Logger LOGGER = Logger.getLogger(FunctionElement.class);
    private final String functionName;
    /**
     * @see DatabaseMetaData#procedureResultUnknown DatabaseMetaData#procedureNoResult DatabaseMetaData#procedureReturnsResult
     **/
    private final int functionType;
    private String description;
    private String command;

    /**
     * @param functionName Function identifier
     * @param functionType Function type {@see DatabaseMetaData#getProcedures}
     */
    FunctionElement(String functionName, int functionType) {
        this.functionName = functionName;
        this.functionType = functionType;
    }

    /**     *
     * @param functionName Function identifier
     * @param functionType Function type {@see DatabaseMetaData#getProcedures}
     * @param description Function remarks
     */
    public FunctionElement(String functionName, int functionType, String description) {
        this.functionName = functionName;
        this.functionType = functionType;
        this.description = description;
    }

    @Override
    public String toString() {
        return functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

    public int getFunctionType() {
        return functionType;
    }

    String getToolTip() {
        if(description==null) {
                //Retrieve function ToolTip
                DataSource dataSource = Services.getService(DataSource.class);
                try {
                    Connection connection = dataSource.getConnection();
                    try {
                        TableLocation functionLocation = TableLocation.parse(functionName);
                        ResultSet functionData = connection.getMetaData().getProcedures(functionLocation.getCatalog(),functionLocation.getSchema(), functionLocation.getTable());
                        if(functionData.next()) {
                            description = functionData.getString("REMARKS");
                        }
                        functionData.close();
                    } finally {
                        connection.close();
                    }
                } catch (SQLException ex) {
                    LOGGER.warn("Could not read function remarks");
                }
        }
        return description;
    }

    /**
     * @return SQL Command ex: UPPER( param1 VARCHAR )
     */
    String getSQLCommand() {
        if(command==null) {
            //Retrieve function ToolTip
            DataSource dataSource = Services.getService(DataSource.class);
            try {
                Connection connection = dataSource.getConnection();
                try {
                    TableLocation functionLocation = TableLocation.parse(functionName);
                    ResultSet functionData = connection.getMetaData().getProcedureColumns(functionLocation.getCatalog(), functionLocation.getSchema(), functionLocation.getTable(), null);
                    StringBuilder sb = new StringBuilder(getFunctionName());
                    sb.append("(");
                    int argCount = 0;
                    while(functionData.next()) {
                        if(functionData.getInt("COLUMN_TYPE") != DatabaseMetaData.procedureColumnReturn) {
                            if(argCount++>0) {
                                sb.append(", ");
                            }
                            sb.append(functionData.getString("COLUMN_NAME"));
                            sb.append(" ");
                            sb.append(functionData.getString("TYPE_NAME"));
                        }
                    }
                    sb.append(")");
                    functionData.close();
                    command = sb.toString();
                } finally {
                    connection.close();
                }
            } catch (SQLException ex) {
                LOGGER.warn("Could not read function command");
            }
        }
        return command;
    }
}
