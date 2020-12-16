/*
 * Bundle datastore/utils is part of the OrbisGIS platform
 *
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
 * OSM is distributed under LGPL 3 license.
 *
 * Copyright (C) 2020 CNRS (Lab-STICC UMR CNRS 6285)
 *
 *
 * OSM is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OSM is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * OSM. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.datastore.jdbcutils

import groovy.sql.OutParameter

import java.sql.Types

/**
 * Class declaring {@link OutParameter} used for dedicated methods in {@link JDBCDataStoreUtils}.
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class OutParameters{
    public static final OutParameter ARRAY = new OutParameter ( ) { int getType ( ) { return Types.ARRAY } }
    public static final OutParameter BIGINT = new OutParameter ( ) { int getType ( ) { return Types.BIGINT } }
    public static final OutParameter BINARY = new OutParameter ( ) { int getType ( ) { return Types.BINARY } }
    public static final OutParameter BIT = new OutParameter ( ) { int getType ( ) { return Types.BIT } }
    public static final OutParameter BLOB = new OutParameter ( ) { int getType ( ) { return Types.BLOB } }
    public static final OutParameter BOOLEAN = new OutParameter ( ) { int getType ( ) { return Types.BOOLEAN } }
    public static final OutParameter CHAR = new OutParameter ( ) { int getType ( ) { return Types.CHAR } }
    public static final OutParameter CLOB = new OutParameter ( ) { int getType ( ) { return Types.CLOB } }
    public static final OutParameter DATALINK = new OutParameter ( ) { int getType ( ) { return Types.DATALINK } }
    public static final OutParameter DATE = new OutParameter ( ) { int getType ( ) { return Types.DATE } }
    public static final OutParameter DECIMAL = new OutParameter ( ) { int getType ( ) { return Types.DECIMAL } }
    public static final OutParameter DISTINCT = new OutParameter ( ) { int getType ( ) { return Types.DISTINCT } }
    public static final OutParameter DOUBLE = new OutParameter ( ) { int getType ( ) { return Types.DOUBLE } }
    public static final OutParameter FLOAT = new OutParameter ( ) { int getType ( ) { return Types.FLOAT } }
    public static final OutParameter INTEGER = new OutParameter ( ) { int getType ( ) { return Types.INTEGER } }
    public static final OutParameter JAVA_OBJECT = new OutParameter ( ) { int getType ( ) { return Types.JAVA_OBJECT } }
    public static final OutParameter LONGVARBINARY = new OutParameter ( ) { int getType ( ) { return Types.LONGVARBINARY } }
    public static final OutParameter LONGVARCHAR = new OutParameter ( ) { int getType ( ) { return Types.LONGVARCHAR } }
    public static final OutParameter NULL = new OutParameter ( ) { int getType ( ) { return Types.NULL } }
    public static final OutParameter NUMERIC = new OutParameter ( ) { int getType ( ) { return Types.NUMERIC } }
    public static final OutParameter OTHER = new OutParameter ( ) { int getType ( ) { return Types.OTHER } }
    public static final OutParameter REAL = new OutParameter ( ) { int getType ( ) { return Types.REAL } }
    public static final OutParameter REF = new OutParameter ( ) { int getType ( ) { return Types.REF } }
    public static final OutParameter SMALLINT = new OutParameter ( ) { int getType ( ) { return Types.SMALLINT } }
    public static final OutParameter STRUCT = new OutParameter ( ) { int getType ( ) { return Types.STRUCT } }
    public static final OutParameter TIME = new OutParameter ( ) { int getType ( ) { return Types.TIME } }
    public static final OutParameter TIMESTAMP = new OutParameter ( ) { int getType ( ) { return Types.TIMESTAMP } }
    public static final OutParameter TINYINT = new OutParameter ( ) { int getType ( ) { return Types.TINYINT } }
    public static final OutParameter VARBINARY = new OutParameter ( ) { int getType ( ) { return Types.VARBINARY } }
    public static final OutParameter VARCHAR = new OutParameter ( ) { int getType ( ) { return Types.VARCHAR } }
}