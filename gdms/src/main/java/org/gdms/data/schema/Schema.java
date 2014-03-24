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

/**
 * This interface represents a single GDMS group of Table. Every driver has to
 * declare a schema containing the tables that can be accessed through it.
 *
 * The SourceManager contains a top-level Schema set as the parent of all
 * driver-based Schema. It keeps references to the DataSource
 * objects associated with the sub-schemas Table objects.
 * @see DataSourceFactory.getSchema()
 * 
 * @author Antoine Gourlay
 */
public interface Schema {

        /**
         * The name of the Schema. The driver that creates the Schema should
         * ensure that this name is uniquely bound to the currently-accessed
         * resource.
         * @return the name of this Schema
         */
        String getName();
        
        /**
         * Gets the fully-qualified name of this schema (names of all the parent schemas starting with the
         * root PUBLIC schema & until this one included, separated by dots).
         * @return the fully-qualified name
         */
        String getFullyQualifiedName();

        /**
         * The number of tables in this Schema. This method does not count
         * the tables of sub-schemas.
         * @return the number of tables
         */
        int getTableCount();

        /**
         * The {@link Metadata} associated with a particular table. This method does not
         * search in the sub-schemas.
         * @param name the name of the table
         * @return the Metadata of the table, of null if not found
         */
        Metadata getTableByName(String name);

        /**
         * All the names in the current schema. This method does not count
         * the tables of sub-schemas.
         * @return an array of String containing the names
         */
        String[] getTableNames();

        /**
         * Adds a table to the current Schema. If the Schema already contains
         * a table with the specified name, it replaces it.
         * @param name the name of the table
         * @param table
         */
        void addTable(String name, Metadata table);

        /**
         * Removes the table with the specified name.
         * @param name the name of the table to remove.
         * @return the Metadata of the removed table, or null if no table with
         * that name was found
         */
        Metadata removeTable(String name);

        /**
         * Sets the parent Schema of this Schema. Can be null;
         * @param s a Schema object, or null;
         */
        void setParentSchema(Schema s);

        /**
         * Gets the parent Schema, or null if there is none
         * @return a Schema object or null
         */
        Schema getParentSchema();

        /**
         * Adds a sub-schema to this schema. The Schema will be stored under the
         * name returned by <code>Schema.getName()</code>.
         * @param s the schema
         */
        void addSubSchema(Schema s);

        /**
         * Removes the Schema with the specified name.
         * @param name the name of the Schema
         * @return the removed Schema, or null if no Schema with that name was
         * found
         */
        Schema removeSubSchema(String name);

        /**
         * Returns the names of the sub-schemas of this Schema.
         * @return a (possibly empty) array of names.
         */
        String[] getSubSchemaNames();
        
        /**
         * Get the sub-Schema with the given name.
         * @param name the name of the Schema
         * @return the Schema or null if not found
         */
        Schema getSubSchemaByName(String name);
}
