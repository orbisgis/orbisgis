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

import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.MetaData;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import javax.sql.DataSource;

/**
 * Value check. Only Null and type check are done due to JDBC limitations.
 */
public class AskValidValue extends AskValue {

        /** SQL Type from {@link java.sql.Types} */
        private int fieldType;

        public AskValidValue(DataSource ds,String tableReference, String fieldName) throws SQLException {
                super("Field '" + fieldName + "'", null, null);
                try(Connection connection = ds.getConnection()) {
                    DatabaseMetaData meta = connection.getMetaData();
                    TableLocation location = TableLocation.parse(tableReference);
                    try(ResultSet rs = meta.getColumns(location.getCatalog(), location.getSchema(), location.getTable(), fieldName)) {
                        if(rs.next()) {
                            this.fieldType = rs.getInt("DATA_TYPE");
                        }
                    }
                }
        }

        @Override
        public String validateInput() {
                try {
                    getUserValue();
                    // Cast goes well
                    return "";
                } catch (ParseException e) {
                        return e.getMessage();
                }
        }

        public Object getUserValue() throws ParseException {
                String userInput = getValue();
                return MetaData.castToSQLType(userInput, fieldType);
        }
}
