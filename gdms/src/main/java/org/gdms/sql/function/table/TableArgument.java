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
package org.gdms.sql.function.table;

import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.gdms.sql.function.Argument;

/**
 * An argument that represents a table (i.e. a dataset).
 * @author Antoine Gourlay
 */
public class TableArgument implements Argument {
        
        /**
         * Default {@code TableArgument} for tables that contain a spatial (ie geometric or raster) column.
         */
	public static final TableArgument SPATIAL = new TableArgument(TableDefinition.SPATIAL);
        /**
         * Default {@code TableArgument} for tables that contain a geometric column.
         */
	public static final TableArgument GEOMETRY = new TableArgument(TableDefinition.GEOMETRY);
        /**
         * Default {@code TableArgument} for tables that contain a raster column.
         */
	public static final TableArgument RASTER = new TableArgument(TableDefinition.RASTER);
        /**
         * Default {@code TableArgument} for table of any kind.
         */
	public static final TableArgument ANY = new TableArgument(TableDefinition.ANY);

        private TableDefinition def;

        /**
         * Creates a TableArgument with the specified table definition.
         * @param def a table definition. null equals {@link TableDefinition.ANY}.
         */
        public TableArgument(final TableDefinition def) {
                if (def == null) {
                        this.def = TableDefinition.ANY;
                } else {
                        this.def = def;
                }
        }

        @Override
        public boolean isScalar() {
                return false;
        }

        @Override
        public boolean isTable() {
                return true;
        }

        /**
         * Returns true if the given metadata is valid for this argument.
         * @param m some metadata
         * @return true if valid
         * @throws DriverException 
         */
        public boolean isValid(Metadata m) throws DriverException {
                return def.isValid(m);
        }

        @Override
        public String getDescription() {
                return def.getDescription();
        }
}
