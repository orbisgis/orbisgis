package org.gdms.driver.dbf;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.DefaultConstraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileDriver;

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

	// /**
	// * DOCUMENT ME!
	// *
	// * @param path DOCUMENT ME!
	// *
	// * @return DOCUMENT ME!
	// *
	// * @throws IOException DOCUMENT ME!
	// */
	// private WritableByteChannel getWriteChannel(String path)
	// throws IOException {
	// WritableByteChannel channel;
	//
	// File f = new File(path);
	//
	// if (!f.exists()) {
	// System.out.println("Creando fichero " + f.getAbsolutePath());
	//
	// if (!f.createNewFile()) {
	// throw new IOException("Cannot create file " + f);
	// }
	// }
	//
	// RandomAccessFile raf = new RandomAccessFile(f, "rw");
	// channel = raf.getChannel();
	//
	// return channel;
	// }

	public void writeFile(File file, DataSource dataSource)
			throws DriverException {
		/*
		 * DbaseFileWriterNIO dbfWrite = null; DbaseFileHeaderNIO myHeader;
		 * Value[] record;
		 *
		 * try { myHeader = DbaseFileHeaderNIO.createDbaseHeader(dataSource);
		 *
		 * myHeader.setNumRecords((int) dataSource.getRowCount()); dbfWrite =
		 * new DbaseFileWriterNIO(myHeader, (FileChannel)
		 * getWriteChannel(file.getPath())); record = new
		 * Value[dataSource.getDriverMetadata().getFieldCount()];
		 *
		 * for (int j = 0; j < dataSource.getRowCount(); j++) { for (int r = 0;
		 * r < dataSource.getDriverMetadata() .getFieldCount(); r++) { record[r] =
		 * dataSource.getFieldValue(j, r); }
		 *
		 * dbfWrite.write(record); }
		 *
		 * dbfWrite.close(); } catch (IOException e) { throw new
		 * DriverException(e); }
		 */
	}

	public void createSource(String path, Metadata dsm) throws DriverException {
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

	public Metadata getMetadata() throws DriverException {
		final int fc = dbf.getFieldCount();
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];

		for (int i = 0; i < fc; i++) {
			fieldsNames[i] = dbf.getFieldName(i);
			final int type = getFieldType(i);

			try {
				switch (type) {
				case Types.VARCHAR:
					fieldsTypes[i] = TypeFactory
							.createType(
									Type.STRING,
									STRING,
									new Constraint[] { new DefaultConstraint(
											ConstraintNames.LENGTH, Integer
													.toString(dbf
															.getFieldLength(i))) });
					break;
				case Types.INTEGER:
					fieldsTypes[i] = TypeFactory
							.createType(
									Type.INT,
									INTEGER,
									new Constraint[] { new DefaultConstraint(
											ConstraintNames.LENGTH, Integer
													.toString(dbf
															.getFieldLength(i))) });
					break;
				case Types.DOUBLE:
					fieldsTypes[i] = TypeFactory
							.createType(
									Type.DOUBLE,
									DOUBLE,
									new Constraint[] {
											new DefaultConstraint(
													ConstraintNames.LENGTH,
													Integer.toString(dbf
															.getFieldLength(i))),
											new DefaultConstraint(
													ConstraintNames.PRECISION,
													Integer
															.toString(dbf
																	.getFieldDecimalLength(i))) });
					break;
				case Types.BOOLEAN:
					fieldsTypes[i] = TypeFactory.createType(Type.BOOLEAN,
							BOOLEAN);
					break;
				case Types.DATE:
					fieldsTypes[i] = TypeFactory.createType(Type.DATE, DATE);
					break;
				default:
					throw new RuntimeException("Unknown dbf driver type: "
							+ type);
				}
			} catch (InvalidTypeException e) {
				throw new RuntimeException("Bug in the driver", e);
			}

		}
		return new DefaultMetadata(fieldsTypes, fieldsNames);
	}

	/**
	 * @see com.hardcode.gdbms.engine.data.driver.ObjectDriver#getFieldType(int)
	 */
	private int getFieldType(int i) throws DriverException {
		char fieldType = dbf.getFieldType(i);

		switch (fieldType) {
		case 'L':
			return Types.BOOLEAN;
		case 'F':
		case 'N':
			return (dbf.getFieldDecimalLength(i) > 0) ? Types.DOUBLE
					: Types.INTEGER;
		case 'C':
			return Types.VARCHAR;
		case 'D':
			return Types.DATE;
		default:
			throw new DriverException("Unknown field type");
		}
	}

	/*
	 * public String check(Field field, Value value) throws DriverException {
	 * String driverType = field.getDriverType(); if (driverType.equals(STRING)) {
	 * if (value.toString().length() > Integer.parseInt(field.getParams()
	 * .get(LENGTH))) { return "too long"; }
	 *
	 * return null; } else if (driverType.equals(NUMERIC)) { if (value
	 * instanceof NumericValue) {
	 *
	 * int decimalLength = ((NumericValue) value) .getDecimalDigitsCount(); if
	 * (decimalLength > Integer.parseInt(field.getParams().get( PRECISION))) {
	 * return "too many decimal digits"; }
	 *
	 * if (value.toString().length() > Integer.parseInt(field
	 * .getParams().get(LENGTH))) { return "too long"; }
	 *
	 * return null; } else { return "must be a number"; } } else if
	 * (driverType.equals(DATE)) { if (!(value instanceof DateValue)) { return
	 * "must be a date"; }
	 *
	 * return null; } else if (driverType.equals(BOOLEAN)) { if (!(value
	 * instanceof BooleanValue)) { return "must be a boolean"; }
	 *
	 * return null; }
	 *
	 * throw new RuntimeException(); return null; }
	 */

	public int getType(String driverType) {
		if (driverType.equals(STRING)) {
			return Type.STRING;
		} else if (driverType.equals(DOUBLE)) {
			return Type.DOUBLE;
		} else if (driverType.equals(INTEGER)) {
			return Type.INT;
		} else if (driverType.equals(DATE)) {
			return Type.DATE;
		} else if (driverType.equals(BOOLEAN)) {
			return Type.BOOLEAN;
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
		final char fieldType = dbf.getFieldType(fieldId);

		switch (fieldType) {
		case 'L':
			return ValueFactory.createValue(dbf.getBooleanFieldValue(
					(int) rowIndex, fieldId));
		case 'F':
		case 'N':
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
		case 'C':
			try {
				return ValueFactory.createValue(dbf.getStringFieldValue(
						(int) rowIndex, fieldId).trim());
			} catch (UnsupportedEncodingException e) {
				throw new DriverException(e);
			}
		case 'D':
			String date;
			try {
				date = dbf.getStringFieldValue((int) rowIndex, fieldId).trim();
			} catch (UnsupportedEncodingException e) {
				throw new DriverException(e);
			}
			// System.out.println(rowIndex + " data=" + date);
			if (date.length() == 0) {
				return null;
			}

			final String year = date.substring(0, 4);
			final String month = date.substring(4, 6);
			final String day = date.substring(6, 8);
			final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT,
					ukLocale);
			/*
			 * Calendar c = Calendar.getInstance(); c.clear();
			 * c.set(Integer.parseInt(year), Integer.parseInt(month),
			 * Integer.parseInt(day)); c.set(Calendar.MILLISECOND, 0);
			 */
			final String strAux = month + "/" + day + "/" + year;
			Date dat;
			try {
				dat = df.parse(strAux);
			} catch (ParseException e) {
				throw new DriverException("Bad Date Format");
			}

			// System.out.println("numReg = " + rowIndex + " date:" +
			// dat.getTime());

			return ValueFactory.createValue(dat);
		default:
			throw new DriverException("Unknown field type");
		}
	}

	public long getRowCount() throws DriverException {
		return dbf.getRecordCount();
	}

	public String getName() {
		return "DBF driver";
	}

	public Number[] getScope(int dimension)
			throws DriverException {
		return null;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		try {
			return new TypeDefinition[] {
					new DefaultTypeDefinition(STRING, Type.STRING,
							new ConstraintNames[] { ConstraintNames.LENGTH }),
					new DefaultTypeDefinition(INTEGER, Type.INT,
							new ConstraintNames[] { ConstraintNames.LENGTH }),
					new DefaultTypeDefinition(DOUBLE, Type.DOUBLE,
							new ConstraintNames[] { ConstraintNames.LENGTH,
									ConstraintNames.PRECISION }),
					new DefaultTypeDefinition(BOOLEAN, Type.BOOLEAN, null),
					new DefaultTypeDefinition(DATE, Type.DATE, null) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}
}