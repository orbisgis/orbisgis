/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.driver;

/**
 * A description of a table in a remote database.
 * Contains a name, a type and a schema.
 * The schema is null if it's the current schema of the database (schema=null).
 *
 * @author Fernando Gonzalez Cortes
 */
public class TableDescription {

        private String name;
        private String type;
        private String schema;
        private int geometryType;

        public TableDescription(String name, String type) {
                this(name, type, null);
        }

        public TableDescription(String name, String type, String schema) {
                this(name, type, schema, 0);
        }

       public TableDescription(String name, String type, String schema, int geometryType) {
                this.name = name;
                this.type = type;
                this.schema = schema;
                this.geometryType = geometryType;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public String getType() {
                return type;
        }

        public void setType(String type) {
                this.type = type;
        }

        public String getSchema() {
                return schema;
        }

        public void setSchema(String schema) {
                this.schema = schema;
        }

        public void setGeometryType(int geometryType) {
                this.geometryType = geometryType;

        }

        public int getGeometryType() {
                return this.geometryType;

        }
}
