/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver;

import java.sql.Connection;
import java.sql.SQLException;

import org.gdms.data.db.DBSource;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

/**
 * Interface to be implement by the DB drivers that also have write capabilities.
 */
public interface DBReadWriteDriver extends DBDriver, Driver {

	/**
	 * Executes an instruction against the server.
	 *
	 * @param con
	 *            Connection used to execute the instruction
	 * @param sql
	 *            Instruction to execute
	 *
	 * @throws SQLException
	 *             If the execution fails
	 */
	void execute(Connection con, String sql) throws SQLException;

	/**
	 * Creates a new table. The source argument provides information about the
	 * name of the table to be created and the host, port, schema and database where the
	 * table has to be created.
	 *
	 * @param source
	 * @param driverMetadata
	 * @throws DriverException
	 */
	void createSource(DBSource source, Metadata driverMetadata)
			throws DriverException;

	/**
	 * Begins a transaction.
	 *
	 * @param con
	 *            to perform the transaction beginning
	 *
	 * @throws SQLException
	 *             If the transaction could not be started
	 */
	void beginTrans(Connection con) throws SQLException;

	/**
	 * Commits the changes made during the transaction.
	 *
	 * @param con
	 *            to perform the transaction commitment
	 *
	 * @throws SQLException
	 *             If the transaction could not be committed
	 */
	void commitTrans(Connection con) throws SQLException;

	/**
	 * Cancels the changes made during the transaction.
	 *
	 * @param con
	 *            to perform the transaction rollback
	 *
	 * @throws SQLException
	 *             If the transaction could not be canceled
	 */
	void rollBackTrans(Connection con) throws SQLException;

	/**
	 * Returns the SQL statement that changes the name of the specified name in
	 * the current table.
	 *
	 * @param oldName
	 *            Name of the field to change
	 * @param newName
	 *            New name
         * @return
         * @throws DriverException
	 */
	String getChangeFieldNameSQL(String oldName,
			String newName) throws DriverException;

	/**
	 * Returns the SQL instruction that adds a field with the specified type and
	 * name in the current table.
	 *
	 * @param fieldName
	 *            Name of the field to add
	 * @param fieldType
	 *            Type of the field to add
         * @return
         * @throws DriverException
	 */
	String getAddFieldSQL(String fieldName,
			Type fieldType) throws DriverException;

	/**
	 * Returns the SQL instruction that deletes the record in the current
	 * table that matches the condition of equality between the specified
	 * primary key and the specified values. The corresponding value for
	 * pkNames[i] is stored in values[i].
	 *
	 * @param pkNames
	 *            Name of the fields that are primary key
	 * @param values
	 *            Values of the pkNames fields in the record to remove
         * @return
         * @throws DriverException
	 */
	String getDeleteRecordSQL(String[] pkNames,
			Value[] values) throws DriverException;

	/**
	 * Returns the SQL instruction that inserts a row containing the values
	 * specified in 'row' for each field specified in 'fieldNames' in the current table.
	 * The corresponding value for fieldNames[i] is stored in row[i].
	 *
	 * @param fieldNames
	 *            Names of the fields
	 * @param fieldTypes
	 *            Types of the fields
	 * @param row
	 *            values for the 'fieldNames'
         * @return
         * @throws DriverException
	 */
	String getInsertSQL(String[] fieldNames,
			Type[] fieldTypes, Value[] row) throws DriverException;

	/**
	 * Returns the SQL instruction that removes field specified by fieldName of
	 * the current table.
	 *
	 * @param fieldName
	 *            Name of the field to delete
         * @return
         * @throws DriverException
	 */
	String getDeleteFieldSQL(String fieldName)
			throws DriverException;

	/**
	 * Returns the SQl instruction that updates the contents that match the
	 * condition of equality between the specified primary key and the specified
	 * values. The corresponding value for pkNames[i] is stored in values[i].
	 *
	 * @param pkNames
	 *            Name of the fields in the table that are primary key
	 * @param values
	 *            Values of the primary key fields of the row to update
	 * @param fieldNames
	 *            Names of the fields
	 * @param fieldTypes
	 *            Types of the fields
	 * @param row
	 *            Values to update
         * @return
         * @throws DriverException 
	 */
	String getUpdateSQL(String[] pkNames,
			Value[] values, String[] fieldNames, Type[] fieldTypes, Value[] row)
			throws DriverException;
}