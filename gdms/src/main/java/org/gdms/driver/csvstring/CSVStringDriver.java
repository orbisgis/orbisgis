/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.driver.csvstring;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;

import com.vividsolutions.jts.geom.Geometry;

/**
 * CSV file driver where the first row is used to define the field names
 *
 * @author Fernando Gonzalez Cortes
 */
public class CSVStringDriver implements FileReadWriteDriver, ValueWriter {
	public static final String DRIVER_NAME = "csv string";

	private char FIELD_SEPARATOR = ';';

	private List<List<String>> rows;

	private ValueWriter valueWriter = ValueWriter.internalValueWriter;

	/**
	 * @see org.gdms.driver.Driver#getName()
	 */
	public String getName() {
		return DRIVER_NAME;
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldName(int)
	 */
	private String getFieldName(final int fieldId) throws DriverException {
		return rows.get(0).get(fieldId);
	}

	/**
	 * @see org.gdms.data.DataSource#getIntFieldValue(int, int)
	 */
	public Value getFieldValue(final long rowIndex, final int fieldId)
			throws DriverException {
		final List<String> fields = rows.get((int) (rowIndex + 1));
		if (fieldId < fields.size()) {
			if (fields.get(fieldId).equals("null")) {
				return null;
			}
		} else {
			return ValueFactory.createNullValue();
		}

		Value value = ValueFactory.createValue(fields.get(fieldId));

		return value;
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	private int getFieldCount() throws DriverException {
		return rows.get(0).size();
	}

	/**
	 * @see org.gdms.data.DataSource#open(java.io.File)
	 */
	public void open(final File file) throws DriverException {
		try {
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(file));
			rows = CsvUtil.parseCsv(bis, FIELD_SEPARATOR);
			bis.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.data.DataSource#close(Connection)
	 */
	public void close() throws DriverException {
	}

	/**
	 * @see org.gdms.data.DataSource#getRowCount()
	 */
	public long getRowCount() {
		return rows.size() - 1;
	}

	/**
	 * @see org.gdms.data.driver.AlphanumericFileDriver#fileAccepted(java.io.File)
	 */
	public boolean fileAccepted(File f) {
		return f.getAbsolutePath().toUpperCase().endsWith("CSV");
	}

	private List<String> getHeaderRow(final Metadata metaData)
			throws DriverException {
		List<String> ret = new ArrayList<String>();

		for (int i = 0; i < metaData.getFieldCount(); i++) {
			ret.add(metaData.getFieldName(i));
		}

		return ret;
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.driver.AlphanumericFileDriver#writeFile(org.gdms.data.edition.DataWare,
	 *      java.io.File)
	 */
	public void writeFile(final File file, final DataSource dataSource)
			throws DriverException {
		try {
			List<List<String>> content = new ArrayList<List<String>>();
			Metadata metadata = dataSource.getMetadata();
			List<String> row = getHeaderRow(metadata);
			content.add(row);
			for (int i = 0; i < dataSource.getRowCount(); i++) {
				row = new ArrayList<String>();
				for (int j = 0; j < metadata.getFieldCount(); j++) {
					if (dataSource.isNull(i, j)) {
						row.add("null");
					} else {
						row.add(dataSource.getFieldValue(i, j).toString());
					}
				}
				content.add(row);
			}
			InputStream csvContent = CsvUtil.formatCsv(content, FIELD_SEPARATOR);
			copy(csvContent, new FileOutputStream(file));
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	private void copy(InputStream csvContent, FileOutputStream fileOutputStream)
			throws IOException {
		DriverUtilities.copy(csvContent, fileOutputStream);
		fileOutputStream.close();
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			final File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();

			List<List<String>> content = new ArrayList<List<String>>();
			List<String> row = getHeaderRow(metadata);
			content.add(row);
			InputStream csvContent = CsvUtil.formatCsv(content, FIELD_SEPARATOR);
			copy(csvContent, new FileOutputStream(file));

		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getNullStatementString() {
		return valueWriter.getNullStatementString();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param b
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(boolean b) {
		return valueWriter.getStatementString(b);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param binary
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(byte[] binary) {
		return valueWriter.getStatementString(binary);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param d
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Date d) {
		return valueWriter.getStatementString(d);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param d
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(double d, int sqlType) {
		return valueWriter.getStatementString(d, sqlType);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param i
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(int i, int sqlType) {
		return valueWriter.getStatementString(i, sqlType);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param i
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(long i) {
		return valueWriter.getStatementString(i);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param str
	 *            DOCUMENT ME!
	 * @param sqlType
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(String str, int sqlType) {
		return str;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param t
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Time t) {
		return t.toString();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param ts
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getStatementString(Timestamp ts) {
		return ts.toString();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	public String getStatementString(Geometry g) {
		return valueWriter.getStatementString(g);
	}

	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".csv")) {
			return fileName + ".csv";
		} else {
			return fileName;
		}
	}

	/**
	 * @see org.gdms.driver.FileDriver#copy(java.io.File, java.io.File)
	 */
	public void copy(File in, File out) throws IOException {
		DriverUtilities.copy(in, out);
	}

	/**
	 * @see org.gdms.driver.ReadOnlyDriver#getMetadata()
	 */
	public Metadata getMetadata() throws DriverException {
		final int fc = getFieldCount();
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];
		TypeDefinition csvTypeDef;
		try {
			csvTypeDef = new DefaultTypeDefinition("STRING", Type.STRING);

			for (int i = 0; i < fc; i++) {
				fieldsNames[i] = getFieldName(i);
				fieldsTypes[i] = csvTypeDef.createType(null);
			}

			return new DefaultMetadata(fieldsTypes, fieldsNames);
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}
		// DefaultDriverMetadata ret = new DefaultDriverMetadata();
		// for (int i = 0; i < getFieldCount(); i++) {
		// ret.addField(getFieldName(i), "STRING");
		// }
		//
		// return ret;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public boolean isCommitable() {
		return true;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		return new TypeDefinition[] { new DefaultTypeDefinition("STRING",
				Type.STRING) };
	}

	public int getType() {
		return SourceManager.CSV;
	}
}