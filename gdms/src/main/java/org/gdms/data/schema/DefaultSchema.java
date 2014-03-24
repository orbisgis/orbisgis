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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
package org.gdms.data.schema;

import java.util.HashMap;
import java.util.Map;

/**
 * Default Schema implementation.
 * 
 * @author Antoine Gourlay
 */
public class DefaultSchema implements Schema {

        private Map<String, Metadata> metadata;
        private Map<String, Schema> schema;
        private Schema parent;
        private String name;

        /**
         * Creates a Schema with the specified name
         * @param name the name of the Schema
         * @throws IllegalArgumentException if the name is empty
         */
        public DefaultSchema(String name) {
                this.name = name;
                metadata = new HashMap<String, Metadata>();
                schema = new HashMap<String, Schema>();
        }

        @Override
        public int getTableCount() {
                return metadata.size();
        }

        @Override
        public Metadata getTableByName(String name) {
                return metadata.get(name);
        }

        @Override
        public String[] getTableNames() {
                return metadata.keySet().toArray(new String[metadata.size()]);
        }

        @Override
        public void addTable(String name, Metadata table) {
                metadata.put(name, table);
        }

        @Override
        public Metadata removeTable(String name) {
                return metadata.remove(name);
        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public void addSubSchema(Schema s) {
                if (s != null) {
                        s.setParentSchema(this);
                        schema.put(s.getName(), s);
                }
        }

        @Override
        public Schema removeSubSchema(String name) {
                Schema s = schema.remove(name);
                if (s != null) {
                        s.setParentSchema(null);
                }
                return s;
        }

        @Override
        public String[] getSubSchemaNames() {
                return schema.keySet().toArray(new String[schema.size()]);
        }

        @Override
        public Schema getParentSchema() {
                return parent;
        }

        @Override
        public void setParentSchema(Schema s) {
                if (s != parent && parent != null) {
                        parent.removeSubSchema(this.name);
                }
                parent = s;
        }

        @Override
        public Schema getSubSchemaByName(String name) {
                return schema.get(name);
        }

        @Override
        public String getFullyQualifiedName() {
                return parent.getFullyQualifiedName() + "." + name;
        }
}
