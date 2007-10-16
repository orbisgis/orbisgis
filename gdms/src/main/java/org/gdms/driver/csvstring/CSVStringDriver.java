package org.gdms.driver.csvstring;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
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

import com.vividsolutions.jts.geom.Geometry;

/**
 * CSV file driver where the first row is used to define the field names
 *
 * @author Fernando Gonzalez Cortes
 */
public class CSVStringDriver implements FileReadWriteDriver, ValueWriter {
	public static final String DRIVER_NAME = "csv string";

	private String FIELD_SEPARATOR = ";";

	private BufferedReader reader;

	private List<String[]> rows;

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
		return rows.get(0)[fieldId];
	}

	/**
	 * @see org.gdms.data.DataSource#getIntFieldValue(int, int)
	 */
	public Value getFieldValue(final long rowIndex, final int fieldId)
			throws DriverException {
		final String[] fields = rows.get((int) (rowIndex + 1));
		if (fieldId < fields.length) {
			if (fields[fieldId].equals("null")) {
				return null;
			}
		} else {
			return ValueFactory.createNullValue();
		}

		Value value = ValueFactory.createValue(fields[fieldId]);

		return value;
	}

	/**
	 * @see org.gdms.data.DataSource#getFieldCount()
	 */
	private int getFieldCount() throws DriverException {
		return rows.get(0).length;
	}

	/**
	 * @see org.gdms.data.DataSource#open(java.io.File)
	 */
	public void open(final File file) throws DriverException {
		try {
			reader = new BufferedReader(new FileReader(file));
			rows = new ArrayList<String[]>();
			String aux;

			while ((aux = reader.readLine()) != null) {
				final String[] fields = aux.split(FIELD_SEPARATOR);
				rows.add(fields);
			}
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	/**
	 * @see org.gdms.data.DataSource#close(Connection)
	 */
	public void close() throws DriverException {
		try {
			reader.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
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

	private void writeHeaderPart(final PrintWriter out, final Metadata metaData)
			throws DriverException {
		final StringBuilder header = new StringBuilder(metaData.getFieldName(0));
		for (int i = 1; i < metaData.getFieldCount(); i++) {
			header.append(FIELD_SEPARATOR).append(metaData.getFieldName(i));
		}
		out.println(header.toString());
	}

	/**
	 * @throws DriverException
	 * @see org.gdms.data.driver.AlphanumericFileDriver#writeFile(org.gdms.data.edition.DataWare,
	 *      java.io.File)
	 */
	public void writeFile(final File file, final DataSource dataSource)
			throws DriverException {
		PrintWriter out;

		try {
			out = new PrintWriter(new FileOutputStream(file));

			Metadata metadata = dataSource.getMetadata();
			writeHeaderPart(out, metadata);

			for (int i = 0; i < dataSource.getRowCount(); i++) {
				final StringBuilder row = new StringBuilder(dataSource
						.getFieldValue(i, 0).getStringValue(this));

				for (int j = 1; j < metadata.getFieldCount(); j++) {
					row.append(FIELD_SEPARATOR)
							.append(
									dataSource.getFieldValue(i, j)
											.getStringValue(this));
				}
				out.println(row);
			}
			out.close();
		} catch (FileNotFoundException e) {
			throw new DriverException(e);
		}
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			final File file = new File(path);
			file.getParentFile().mkdirs();
			file.createNewFile();

			final PrintWriter out = new PrintWriter(new FileOutputStream(file));
			writeHeaderPart(out, metadata);
			out.close();

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
		try {
			return new TypeDefinition[] { new DefaultTypeDefinition("STRING",
					Type.STRING) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}
}