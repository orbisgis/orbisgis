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
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;

/**
 * Interface to be implement by the DB drivers that as also RW capabilities
 *
 */
public interface DBReadWriteDriver extends DBDriver, ReadWriteDriver {
	/**
	 * Executes an instruction against the server
	 *
	 * @param con
	 *            Connection used to execute the instruction
	 * @param sql
	 *            Instruction to execute
	 * @param props
	 *            Properties of the overlaying DataSource layer
	 *
	 * @throws SQLException
	 *             If the execution fails
	 */
	public void execute(Connection con, String sql) throws SQLException;

	/**
	 * Creates a new table. The source argument provides information about the
	 * name of the table to be created and the host, port and database where the
	 * table has to be created
	 *
	 * @param source
	 * @param driverMetadata
	 * @throws DriverException
	 */
	public void createSource(DBSource source, Metadata driverMetadata)
			throws DriverException;

	/**
	 * Begins a transaction
	 *
	 * @param Connection
	 *            to perform the transacion begining
	 *
	 * @throws SQLException
	 *             If the transaction could not be started
	 */
	public void beginTrans(Connection con) throws SQLException;

	/**
	 * Commits the changes made during the transaction
	 *
	 * @param Connection
	 *            to perform the transacion commitment
	 *
	 * @throws SQLException
	 *             If the transaction could not be commited
	 */
	public void commitTrans(Connection con) throws SQLException;

	/**
	 * Cancels the changes made during the transaction
	 *
	 * @param Connection
	 *            to perform the transacion rollback
	 *
	 * @throws SQLException
	 *             If the transaction could not be cancelled
	 */
	public void rollBackTrans(Connection con) throws SQLException;

	/**
	 * Returns the SQL statement that changes the name of the specified name in
	 * the specified table
	 *
	 * @param tableName
	 *            Name of the table
	 * @param oldName
	 *            Name of the field to change
	 * @param newName
	 *            New name
	 * @return
	 */
	public String getChangeFieldNameSQL(String tableName, String oldName,
			String newName) throws DriverException;

	/**
	 * Returns the SQL instruction that adds a field with the specified type and
	 * name in the specified table
	 *
	 * @param tableName
	 *            Name of the table
	 * @param fieldName
	 *            Name of the field to add
	 * @param fieldType
	 *            Type of the field to add
	 * @return
	 */
	public String getAddFieldSQL(String tableName, String fieldName,
			Type fieldType) throws DriverException;

	/**
	 * Returns the SQL instruction that deletes the record in the specified
	 * table that matches the condition of equality between the specified
	 * primary key and the specified values. The corresponding value for
	 * pkNames[i] is stored in values[i]
	 *
	 * @param tableName
	 *            Name of the table
	 * @param pkNames
	 *            Name of the fields that are primary key
	 * @param values
	 *            Values of the pkNames fields in the record to remove
	 * @return
	 */
	public String getDeleteRecordSQL(String tableName, String[] pkNames,
			Value[] values) throws DriverException;

	/**
	 * Returns the SQL instruction that inserts a row containing the values
	 * specified in 'row' for each field specified in 'fieldNames' in the table
	 * specified by 'tableName'. The corresponding value for fieldNames[i] is
	 * stored in row[i]
	 *
	 * @param tableName
	 *            Name of the table
	 * @param fieldNames
	 *            Names of the fields
	 * @param fieldTypes
	 *            Types of the fields
	 * @param row
	 *            values for the 'fieldNames'
	 * @return
	 */
	public String getInsertSQL(String tableName, String[] fieldNames,
			Type[] fieldTypes, Value[] row) throws DriverException;

	/**
	 * Returns the SQL instruction that removes field specified by fieldName of
	 * the table specified in tableName
	 *
	 * @param tableName
	 *            Name of the table
	 * @param fieldName
	 *            Name of the field to delete
	 * @return
	 */
	public String getDeleteFieldSQL(String tableName, String fieldName)
			throws DriverException;

	/**
	 * Returns the SQl instruction that updates the contents that match the
	 * condition of equality between the specified primary key and the specified
	 * values. The corresponding value for pkNames[i] is stored in values[i].
	 *
	 * @param tableName
	 *            Name of the table
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
	 */
	public String getUpdateSQL(String tableName, String[] pkNames,
			Value[] values, String[] fieldNames, Type[] fieldTypes, Value[] row)
			throws DriverException;
}