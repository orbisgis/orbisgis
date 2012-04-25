/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 * 
 * Team leader : Erwan BOCHER, scientific researcher,
 * 
 * User support leader : Gwendall Petit, geomatic engineer.
 * 
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC, 
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer.
 * 
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 * 
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
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

package org.gdms.sql.engine.operations

import org.gdms.sql.evaluator.Expression

/**
 * Represents an alter element of an ALTER TABLE instruction.
 * 
 * @param column the column to be altered
 * @author Antoine Gourlay
 * @since 0.1
 */
abstract class AlterElement(val column: String)

/**
 * Represents the addition of a new empty column.
 * 
 * @param column the name of the column to be added
 * @param sqlType the sql string alias of the Gdms type of the column
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AddColumn(override val column: String, sqlType: String) extends AlterElement(column) {
  override def toString = "Add col(" + column + ", " + sqlType + ")"
}

/**
 * Represents the removal of an existing column.
 * 
 * @param column the name of the column to be removed
 * @param ifExists true if no exception should be thrown in the case the column does not exist
 * @author Antoine Gourlay
 * @since 0.1
 */
case class DropColumn(override val column: String, ifExists: Boolean) extends AlterElement(column) {
  override def toString = "Drop col(" + column + ") ifExists=" + ifExists
}

/**
 * Represents the change of the type of an existing column.
 * 
 * @param column the name of the column to be changed
 * @param newSqlType the new sql type of the column
 * @param init an optional expression to initialize the column. If not present, NULL values are used.
 * @author Antoine Gourlay
 * @since 0.1
 */
case class AlterTypeOfColumn(override val column: String, newSqlType: String, init: Option[Expression]) extends AlterElement(column) {
  override def toString = "Alter col(" + column + ") newtype=" + newSqlType + " with(" + init + ")"
}

/**
 * Represents the change of the name of an existing column.
 * 
 * @param column the name of the column to be changed
 * @param newname the new name of the column
 * @author Antoine Gourlay
 * @since 0.1
 */
case class RenameColumn(override val column: String, newname: String) extends AlterElement(column) {
  override def toString = "Rename col(" + column + ") to(" + newname + ")"
}