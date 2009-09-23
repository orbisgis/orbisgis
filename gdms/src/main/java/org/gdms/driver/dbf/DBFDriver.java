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
package org.gdms.driver.dbf;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.WarningListener;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.LengthConstraint;
import org.gdms.data.types.PrecisionConstraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.FileReadWriteDriver;
import org.gdms.source.SourceManager;
import org.orbisgis.progress.IProgressMonitor;
import org.orbisgis.utils.FileUtils;

public class DBFDriver implements FileReadWriteDriver {

	public static final String STRING = "String";

	public static final String DOUBLE = "Double";

	public static final String INTEGER = "Integer";

	public static final String LONG = "Long";

	public static final String DATE = "Date";

	public static final String BOOLEAN = "Boolean";

	public static final String LENGTH = "Length";

	public static final String PRECISION = "Precision";

	public static String DRIVER_NAME = "Dbase driver";

	private DbaseFileReader dbaseReader;

	private DataSourceFactory dataSourceFactory;

	public void setDataSourceFactory(DataSourceFactory dsf) {
		this.dataSourceFactory = dsf;
	}

	public void createSource(String path, Metadata metadata,
			DataSourceFactory dataSourceFactory) throws DriverException {
		try {
			FileOutputStream fos = new FileOutputStream(new File(path));
			DbaseFileHeader header = getHeader(metadata, 0, dataSourceFactory
					.getWarningListener());
			DbaseFileWriter writer = new DbaseFileWriter(header, fos
					.getChannel());
			writer.close();
		} catch (IOException e) {
			throw new DriverException(e);
		} catch (DbaseFileException e) {
			throw new DriverException(e);
		}
	}

	public void writeFile(File file, DataSource dataSource, IProgressMonitor pm)
			throws DriverException {
		writeFile(file, new DefaultRowProvider(dataSource), dataSourceFactory
				.getWarningListener(), pm);
	}

	public void writeFile(File file, RowProvider dataSource,
			WarningListener warningListener, IProgressMonitor pm)
			throws DriverException {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			DbaseFileHeader header = getHeader(dataSource.getMetadata(),
					(int) dataSource.getRowCount(), dataSourceFactory
							.getWarningListener());
			DbaseFileWriter writer = new DbaseFileWriter(header, fos
					.getChannel());
			for (int i = 0; i < header.getNumRecords(); i++) {
				if (i / 100 == i / 100.0) {
					if (pm.isCancelled()) {
						break;
					} else {
						pm.progressTo((int) (100 * i / header.getNumRecords()));
					}
				}
				writer.write(dataSource.getRow(i));
			}
			writer.close();
		} catch (IOException e) {
			throw new DriverException(e);
		} catch (DbaseFileException e) {
			throw new DriverException(e);
		}
	}

	private DbaseFileHeader getHeader(Metadata m, int rowCount,
			WarningListener warningListener) throws DriverException,
			DbaseFileException {
		DbaseFileHeader header = new DbaseFileHeader();
		for (int i = 0; i < m.getFieldCount(); i++) {
			String fieldName = m.getFieldName(i);
			Type gdmsType = m.getFieldType(i);
			DBFType type = getDBFType(gdmsType);
			header.addColumn(fieldName, type.type, type.fieldLength,
					type.decimalCount, warningListener);
		}
		header.setNumRecords(rowCount);

		return header;
	}

	private class DBFType {
		char type;

		int fieldLength;

		int decimalCount;

		public DBFType(char type, int fieldLength, int decimalCount) {
			super();
			this.type = type;
			this.fieldLength = fieldLength;
			this.decimalCount = decimalCount;
		}
	}

	private DBFType getDBFType(Type fieldType) throws DriverException {
		Constraint lengthConstraint = fieldType
				.getConstraint(Constraint.LENGTH);
		int length = Integer.MAX_VALUE;
		if (lengthConstraint != null) {
			length = Integer.parseInt(lengthConstraint.getConstraintValue());
		}

		Constraint decimalCountConstraint = fieldType
				.getConstraint(Constraint.PRECISION);
		int decimalCount = Integer.MAX_VALUE;
		if (decimalCountConstraint != null) {
			decimalCount = Integer.parseInt(decimalCountConstraint
					.getConstraintValue());
		}

		switch (fieldType.getTypeCode()) {
		case Type.BOOLEAN:
			return new DBFType('l', 1, 0);
		case Type.BYTE:
			return new DBFType('n', Math.min(3, length), 0);
		case Type.DATE:
			return new DBFType('d', 8, 0);
		case Type.DOUBLE:
		case Type.FLOAT:
			return new DBFType('f', Math.min(20, length), Math.min(18,
					decimalCount));
		case Type.INT:
			return new DBFType('n', Math.min(10, length), 0);
		case Type.LONG:
			return new DBFType('n', Math.min(18, length), 0);
		case Type.SHORT:
			return new DBFType('n', Math.min(5, length), 0);
		case Type.STRING:
			return new DBFType('c', Math.min(254, length), 0);
		default:
			throw new DriverException("Cannot store "
					+ TypeFactory.getTypeName(fieldType.getTypeCode())
					+ " in dbase");
		}
	}

	public void copy(File in, File out) throws IOException {
		FileUtils.copy(in, out);
	}

	public Metadata getMetadata() throws DriverException {
		DbaseFileHeader header = dbaseReader.getHeader();

		final int fc = header.getNumFields();
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[fc];

		for (int i = 0; i < fc; i++) {
			fieldsNames[i] = header.getFieldName(i);
			final int type = getFieldType(i);

			try {
				switch (type) {
				case Type.STRING:
					fieldsTypes[i] = TypeFactory.createType(Type.STRING,
							STRING, new Constraint[] { new LengthConstraint(
									header.getFieldLength(i)) });
					break;
				case Type.INT:
					fieldsTypes[i] = TypeFactory.createType(Type.INT, INTEGER,
							new Constraint[] { new LengthConstraint(header
									.getFieldLength(i)) });
					break;
				case Type.LONG:
					fieldsTypes[i] = TypeFactory.createType(Type.LONG, LONG,
							new Constraint[] { new LengthConstraint(header
									.getFieldLength(i)) });
					break;
				case Type.DOUBLE:
					fieldsTypes[i] = TypeFactory.createType(Type.DOUBLE,
							DOUBLE, new Constraint[] {
									new LengthConstraint(header
											.getFieldLength(i)),
									new PrecisionConstraint(header
											.getFieldDecimalCount(i)) });
					break;
				case Type.BOOLEAN:
					fieldsTypes[i] = TypeFactory.createType(Type.BOOLEAN,
							BOOLEAN);
					break;
				case Type.DATE:
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
	 * @see org.gdms.driver.ObjectDriver#getFieldType(int)
	 */
	private int getFieldType(int i) throws DriverException {
		DbaseFileHeader header = dbaseReader.getHeader();
		char fieldType = header.getFieldType(i);

		switch (fieldType) {
		// (L)logical (T,t,F,f,Y,y,N,n)
		case 'l':
		case 'L':
			return Type.BOOLEAN;
			// (C)character (String)
		case 'c':
		case 'C':
			return Type.STRING;
			// (D)date (Date)
		case 'd':
		case 'D':
			return Type.DATE;
			// (F)floating (Double)
		case 'n':
		case 'N':
			if ((header.getFieldDecimalCount(i) == 0)) {
				if ((header.getFieldLength(i) >= 0)
						&& (header.getFieldLength(i) < 10)) {
					return Type.INT;
				} else {
					return Type.LONG;
				}
			}
		case 'f':
		case 'F': // floating point number
			return Type.DOUBLE;
		default:
			throw new DriverException("Unknown field type");
		}
	}

	public void open(File file) throws DriverException {
		try {
			FileInputStream fis = new FileInputStream(file);
			dbaseReader = new DbaseFileReader(fis.getChannel(),
					dataSourceFactory.getWarningListener());

		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public void close() throws DriverException {
		try {
			dbaseReader.close();
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		try {
			return dbaseReader.getFieldValue((int) rowIndex, fieldId,
					dataSourceFactory.getWarningListener());
		} catch (IOException e) {
			throw new DriverException(e);
		}
	}

	public long getRowCount() throws DriverException {
		return dbaseReader.getHeader().getNumRecords();
	}

	public String getDriverId() {
		return DRIVER_NAME;
	}

	public Number[] getScope(int dimension) throws DriverException {
		return null;
	}

	public TypeDefinition[] getTypesDefinitions() {
		return new TypeDefinition[] {
				new DefaultTypeDefinition(STRING, Type.STRING,
						new int[] { Constraint.LENGTH }),
				new DefaultTypeDefinition(INTEGER, Type.INT,
						new int[] { Constraint.LENGTH }),
				new DefaultTypeDefinition(DOUBLE, Type.DOUBLE, new int[] {
						Constraint.LENGTH, Constraint.PRECISION }),
				new DefaultTypeDefinition(BOOLEAN, Type.BOOLEAN),
				new DefaultTypeDefinition(DATE, Type.DATE) };
	}

	public boolean isCommitable() {
		return true;
	}

	public int getType() {
		return SourceManager.FILE;
	}

	public String validateMetadata(Metadata metadata) {
		return null;
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { "dbf" };
	}

	@Override
	public String getTypeDescription() {
		return "dBase file";
	}

	@Override
	public String getTypeName() {
		return "DBF";
	}

}
