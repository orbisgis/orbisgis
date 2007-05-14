package org.gdms.driver.dbf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.channels.WritableByteChannel;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.FileDriver;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverUtilities;

public class DBFDriver implements FileDriver {

	public static final String STRING = "String";

	public static final String DOUBLE = "Double";

	public static final String INTEGER = "Integer";

	public static final String DATE = "Date";

	public static final String BOOLEAN = "Boolean";

	public static final String LENGTH = "Length";

	public static final String PRECISION = "Precision";

	private static Locale ukLocale = new Locale("en", "UK");

	private DbaseFile dbf = new DbaseFile();

	public void setDataSourceFactory(DataSourceFactory dsf) {
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param path DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws IOException DOCUMENT ME!
	 */
	private WritableByteChannel getWriteChannel(String path)
	    throws IOException {
	    WritableByteChannel channel;

	    File f = new File(path);

	    if (!f.exists()) {
	        System.out.println("Creando fichero " + f.getAbsolutePath());

	        if (!f.createNewFile()) {
	            throw new IOException("Cannot create file " + f);
	        }
	    }

	    RandomAccessFile raf = new RandomAccessFile(f, "rw");
	    channel = raf.getChannel();

	    return channel;
	}

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
	/*
		DbaseFileWriterNIO dbfWrite = null;
		DbaseFileHeaderNIO myHeader;
		Value[] record;

		try {
			myHeader = DbaseFileHeaderNIO.createDbaseHeader(dataSource);

			myHeader.setNumRecords((int) dataSource.getRowCount());
			dbfWrite = new DbaseFileWriterNIO(myHeader,
					(FileChannel) getWriteChannel(file.getPath()));
			record = new Value[dataSource.getDriverMetadata().getFieldCount()];

			for (int j = 0; j < dataSource.getRowCount(); j++) {
				for (int r = 0; r < dataSource.getDriverMetadata()
						.getFieldCount(); r++) {
					record[r] = dataSource.getFieldValue(j, r);
				}

				dbfWrite.write(record);
			}

			dbfWrite.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
		*/
	}

	public void createSource(String path, DriverMetadata dsm)
			throws DriverException {
		/*
		 * DbaseFileHeaderNIO myHeader;
		 *
		 * try { FieldDescription[] fd = getFileDescriptors(dsm); myHeader =
		 * DbaseFileHeaderNIO.createDbaseHeader(fd); myHeader.setNumRecords(0);
		 * DbaseFileWriterNIO d = new DbaseFileWriterNIO(myHeader, (FileChannel)
		 * getWriteChannel(path)); d.close(); } catch (IOException e) { throw
		 * new DriverException(e); }
		 */}

	/*
	 * private FieldDescription[] getFileDescriptors(DriverMetadata dsm) throws
	 * DriverException { String[] names = new String[dsm.getFieldCount()];
	 * String[] types = new String[names.length]; int[] lengths = new
	 * int[types.length]; int[] precision = new int[types.length]; for (int i =
	 * 0; i < types.length; i++) { names[i] = dsm.getFieldName(i); types[i] =
	 * dsm.getFieldType(i); if ((types[i].equals(STRING)) ||
	 * (types[i].equals(NUMERIC))) { lengths[i] = new
	 * Integer(dsm.getFieldParam(i, LENGTH)); } if (types[i].equals(NUMERIC)) {
	 * precision[i] = new Integer(dsm.getFieldParam(i, PRECISION)); } }
	 *
	 * FieldDescription[] fd = new FieldDescription[dsm.getFieldCount()]; for
	 * (int i = 0; i < precision.length; i++) { fd[i] = new FieldDescription();
	 * fd[i].setFieldName(names[i]); fd[i].setFieldType(to08Type(types[i]));
	 * fd[i].setFieldLength(lengths[i]);
	 * fd[i].setFieldDecimalCount(precision[i]); }
	 *
	 * return fd; }
	 *
	 * private int to08Type(String dbfType) { if (STRING.equals(dbfType)) {
	 * return Types.VARCHAR; } else if (BOOLEAN.equals(dbfType)) { return
	 * Types.BOOLEAN; } else if (DATE.equals(dbfType)) { return Types.DATE; }
	 * else if (NUMERIC.equals(dbfType)) { return Types.DOUBLE; }
	 *
	 * throw new RuntimeException("Not a valid dbf field type: " + dbfType); }
	 *
	 * private WritableByteChannel getWriteChannel(String path) throws
	 * IOException { WritableByteChannel channel;
	 *
	 * File f = new File(path);
	 *
	 * if (!f.exists()) { System.out.println("Creando fichero " +
	 * f.getAbsolutePath());
	 *
	 * if (!f.createNewFile()) { throw new IOException("Cannot create file " +
	 * f); } }
	 *
	 * RandomAccessFile raf = new RandomAccessFile(f, "rw"); channel =
	 * raf.getChannel();
	 *
	 * return channel; }
	 */
	public String completeFileName(String fileName) {
		if (!fileName.toLowerCase().endsWith(".dbf")) {
			return fileName + ".dbf";
		} else {
			return fileName;
		}
	}

	public void copy(File in, File out) throws IOException {
		DriverUtilities.copy(in, out);
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		DefaultDriverMetadata ret = new DefaultDriverMetadata();

		for (int i = 0; i < dbf.getFieldCount(); i++) {
			String name = dbf.getFieldName(i);
			int type = getFieldType(i);
			String[] paramNames = null;
			String[] paramValues = null;
			String driverType = null;
			switch (type) {
			case Types.VARCHAR:
				driverType = STRING;
				paramNames = new String[] { LENGTH };
				paramValues = new String[] { Integer.toString(dbf
						.getFieldLength(i)) };
				break;
			case Types.DOUBLE:
			case Types.INTEGER:
				if (dbf.getFieldDecimalLength(i) > 0) {
					driverType = DOUBLE;
					paramNames = new String[] { LENGTH, PRECISION };
					paramValues = new String[] {
							Integer.toString(dbf.getFieldLength(i)),
							Integer.toString(dbf.getFieldDecimalLength(i)) };
				} else {
					driverType = INTEGER;
					paramNames = new String[] { LENGTH };
					paramValues = new String[] { Integer.toString(dbf
							.getFieldLength(i)) };
				}
				break;
			case Types.BOOLEAN:
				driverType = BOOLEAN;
				paramNames = new String[0];
				paramValues = new String[0];
				break;
			case Types.DATE:
				driverType = DATE;
				paramNames = new String[0];
				paramValues = new String[0];
				break;
			default:
				throw new RuntimeException("Unknown dbf driver type: " + type);
			}
			ret.addField(name, driverType, paramNames, paramValues);
		}
		return ret;
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	private int getFieldType(int i) throws DriverException {
		char fieldType = dbf.getFieldType(i);

		if (fieldType == 'L') {
			return Types.BOOLEAN;
		} else if ((fieldType == 'F') || (fieldType == 'N')) {
			if (dbf.getFieldDecimalLength(i) > 0)
				return Types.DOUBLE;
			else
				return Types.INTEGER;
		} else if (fieldType == 'C') {
			return Types.VARCHAR;
		} else if (fieldType == 'D') {
			return Types.DATE;
		} else {
			throw new DriverException("Unknown field type");
		}
	}

	public String check(Field field, Value value) throws DriverException {
		/*
		 * String driverType = field.getDriverType(); if
		 * (driverType.equals(STRING)) { if (value.toString().length() >
		 * Integer.parseInt(field.getParams() .get(LENGTH))) { return "too
		 * long"; }
		 *
		 * return null; } else if (driverType.equals(NUMERIC)) { if (value
		 * instanceof NumericValue) {
		 *
		 * int decimalLength = ((NumericValue) value) .getDecimalDigitsCount();
		 * if (decimalLength > Integer.parseInt(field.getParams().get(
		 * PRECISION))) { return "too many decimal digits"; }
		 *
		 * if (value.toString().length() > Integer.parseInt(field
		 * .getParams().get(LENGTH))) { return "too long"; }
		 *
		 * return null; } else { return "must be a number"; } } else if
		 * (driverType.equals(DATE)) { if (!(value instanceof DateValue)) {
		 * return "must be a date"; }
		 *
		 * return null; } else if (driverType.equals(BOOLEAN)) { if (!(value
		 * instanceof BooleanValue)) { return "must be a boolean"; }
		 *
		 * return null; }
		 *
		 * throw new RuntimeException();
		 */
		return null;
	}

	public boolean isReadOnly(int i) throws DriverException {
		return false;
	}

	public String[] getAvailableTypes() throws DriverException {
		return new String[] { STRING, DOUBLE, INTEGER, DATE, BOOLEAN };
	}

	public String[] getParameters(String driverType) throws DriverException {
		if (driverType.equals(STRING)) {
			return new String[] { LENGTH };
		} else if (driverType.equals(DOUBLE)) {
			return new String[] { LENGTH, PRECISION };
		} else if (driverType.equals(INTEGER)) {
			return new String[] { LENGTH };
		} else {
			return new String[0];
		}
	}

	public boolean isValidParameter(String driverType, String paramName,
			String paramValue) {
		if (paramName.equals(LENGTH) || paramName.equals(PRECISION)) {
			try {
				Integer.parseInt(paramValue);
			} catch (NumberFormatException e) {
				return false;
			}
		}
		return true;
	}

	public int getType(String driverType) {
		if (driverType.equals(STRING)) {
			return Value.STRING;
		} else if (driverType.equals(DOUBLE)) {
			return Value.DOUBLE;
		} else if (driverType.equals(INTEGER)) {
			return Value.INT;
		} else if (driverType.equals(DATE)) {
			return Value.DATE;
		} else if (driverType.equals(BOOLEAN)) {
			return Value.BOOLEAN;
		}

		throw new RuntimeException(driverType);
	}

	public void open(File file) throws DriverException {
		try {
			dbf = new DbaseFile();
			dbf.open(file);
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public void close() throws DriverException {
		try {
			dbf.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public boolean fileAccepted(File f) {
		return f.getAbsolutePath().toUpperCase().endsWith("DBF");
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		// Field Type (C or M)
		char fieldType = dbf.getFieldType(fieldId);

		if (fieldType == 'L') {
			return ValueFactory.createValue(dbf.getBooleanFieldValue(
					(int) rowIndex, fieldId));
		} else if ((fieldType == 'F') || (fieldType == 'N')) {
			String strValue;
			try {
				strValue = dbf.getStringFieldValue((int) rowIndex, fieldId)
						.trim();
			} catch (UnsupportedEncodingException e1) {
				throw new DriverException(e1);
			}

			if (strValue.length() == 0) {
				return null;
			}
			double value = 0;
			try {
				value = Double.parseDouble(strValue);
			} catch (Exception e) {
				return ValueFactory.createValue(0D);
			}
			return ValueFactory.createValue(value);
		} else if (fieldType == 'C') {
			try {
				return ValueFactory.createValue(dbf.getStringFieldValue(
						(int) rowIndex, fieldId).trim());
			} catch (UnsupportedEncodingException e) {
				throw new DriverException(e);
			}
		} else if (fieldType == 'D') {
			String date;
			try {
				date = dbf.getStringFieldValue((int) rowIndex, fieldId).trim();
			} catch (UnsupportedEncodingException e1) {
				throw new DriverException(e1);
			}
			// System.out.println(rowIndex + " data=" + date);
			if (date.length() == 0) {
				return null;
			}

			String year = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
					ukLocale);
			/*
			 * Calendar c = Calendar.getInstance(); c.clear();
			 * c.set(Integer.parseInt(year), Integer.parseInt(month),
			 * Integer.parseInt(day)); c.set(Calendar.MILLISECOND, 0);
			 */
			String strAux = month + "/" + day + "/" + year;
			Date dat;
			try {
				dat = df.parse(strAux);
			} catch (ParseException e) {
				throw new DriverException("Bad Date Format");
			}

			// System.out.println("numReg = " + rowIndex + " date:" +
			// dat.getTime());

			return ValueFactory.createValue(dat);
		} else {
			throw new DriverException("Unknown field type");
		}
	}

	public long getRowCount() throws DriverException {
		return dbf.getRecordCount();
	}

	public String getName() {
		return "DBF driver";
	}

	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		return null;
	}

}
