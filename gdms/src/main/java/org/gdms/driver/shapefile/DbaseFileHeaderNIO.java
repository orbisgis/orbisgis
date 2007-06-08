/*
 *    Geotools - OpenSource mapping toolkit
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *    This file is based on an origional contained in the GISToolkit project:
 *    http://gistoolkit.sourceforge.net/
 *
 */
/* gvSIG. Sistema de Informacin Geogrfica de la Generalitat Valenciana
 *
 * Copyright (C) 2004 IVER T.I. and Generalitat Valenciana.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,USA.
 *
 * For more information, contact:
 *
 *  Generalitat Valenciana
 *   Conselleria d'Infraestructures i Transport
 *   Av. Blasco Ibez, 50
 *   46010 VALENCIA
 *   SPAIN
 *
 *      +34 963862235
 *   gvsig@gva.es
 *      www.gvsig.gva.es
 *
 *    or
 *
 *   IVER T.I. S.A
 *   Salamanca 50
 *   46005 Valencia
 *   Spain
 *
 *   +34 963163400
 *   dac@iver.es
 */
package org.gdms.driver.shapefile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vividsolutions.jts.geom.Geometry;

/**
 * Class to represent the header of a Dbase III file. Creation date: (5/15/2001
 * 5:15:30 PM)
 */
public class DbaseFileHeaderNIO {
	// Constant for the size of a record
	private static final int FILE_DESCRIPTOR_SIZE = 32;

	// type of the file, must be 03h
	private static final byte MAGIC = 0x03;

	private static final int MINIMUM_HEADER = 33;

	// Date the file was last updated.
	private Date date = new Date();

	private int recordCnt = 0;

	private int fieldCnt = 0;

	private int myFileType = 0;

	// set this to a default length of 1, which is enough for one "space"
	// character which signifies an empty record
	private int recordLength = 1;

	// set this to a flagged value so if no fields are added before the write,
	// we know to adjust the headerLength to MINIMUM_HEADER
	private int headerLength = -1;

	private int largestFieldSize = 0;

	private Logger logger = Logger.getLogger("org.geotools.data.shapefile");

	// collection of header records.
	// lets start out with a zero-length array, just in case
	private DbaseField[] fields = null; // new DbaseField[0];

	/**
	 * Determine the most appropriate Java Class for representing the data in
	 * the field.
	 * 
	 * <PRE>
	 * 
	 * All packages are java.lang unless otherwise specified. C (Character) ->
	 * String N (Numeric) -> Integer or Double (depends on field's decimal
	 * count) F (Floating) -> Double L (Logical) -> Boolean D (Date) ->
	 * java.util.Date Unknown -> String
	 * 
	 * </PRE>
	 * 
	 * @param i
	 *            The index of the field, from 0 to <CODE>getNumFields() - 1</CODE> .
	 * 
	 * @return A Class which closely represents the dbase field type.
	 */
	public Class getFieldClass(int i) {
		Class typeClass = null;

		switch (fields[i].fieldType) {
		case 'C':
			typeClass = String.class;

			break;

		case 'N':

			if (fields[i].decimalCount == 0) {
				typeClass = Integer.class;
			} else {
				typeClass = Double.class;
			}

			break;

		case 'F':
			typeClass = Double.class;

			break;

		case 'L':
			typeClass = Boolean.class;

			break;

		case 'D':
			typeClass = Date.class;

			break;

		default:
			typeClass = String.class;

			break;
		}

		return typeClass;
	}

	/**
	 * Add a column to this DbaseFileHeader. The type is one of (C N L or D)
	 * character, number, logical(true/false), or date. The Field length is the
	 * total length in bytes reserved for this column. The decimal count only
	 * applies to numbers(N), and floating point values (F), and refers to the
	 * number of characters to reserve after the decimal point. <B>Don't expect
	 * miracles from this...</B>
	 * 
	 * <PRE>
	 * 
	 * Field Type MaxLength ---------- --------- C 254 D 8 F 20 N 18
	 * 
	 * </PRE>
	 * 
	 * @param inFieldName
	 *            The name of the new field, must be less than 10 characters or
	 *            it gets truncated.
	 * @param inFieldType
	 *            A character representing the dBase field, ( see above ). Case
	 *            insensitive.
	 * @param inFieldLength
	 *            The length of the field, in bytes ( see above )
	 * @param inDecimalCount
	 *            For numeric fields, the number of decimal places to track.
	 */
	public void addColumn(String inFieldName, char inFieldType,
			int inFieldLength, int inDecimalCount) {
		/*
		 * if (inFieldLength <=0) { throw new DbaseFileException("field length <=
		 * 0"); }
		 */
		if (fields == null) {
			fields = new DbaseField[0];
		}

		int tempLength = 1; // the length is used for the offset, and there is a
		// * for deleted as the first byte
		DbaseField[] tempFieldDescriptors = new DbaseField[fields.length + 1];

		for (int i = 0; i < fields.length; i++) {
			fields[i].fieldDataAddress = tempLength;
			tempLength = tempLength + fields[i].fieldLength;
			tempFieldDescriptors[i] = fields[i];
		}

		tempFieldDescriptors[fields.length] = new DbaseField();
		tempFieldDescriptors[fields.length].fieldLength = inFieldLength;
		tempFieldDescriptors[fields.length].decimalCount = inDecimalCount;
		tempFieldDescriptors[fields.length].fieldDataAddress = tempLength;

		// set the field name
		String tempFieldName = inFieldName;

		if (tempFieldName == null) {
			tempFieldName = "NoName";
		}

		// Fix for GEOT-42, ArcExplorer will not handle field names > 10 chars
		// Sorry folks.
		if (tempFieldName.length() > 10) {
			tempFieldName = tempFieldName.substring(0, 10);
			warn("FieldName " + inFieldName
					+ " is longer than 10 characters, truncating to "
					+ tempFieldName);
		}

		tempFieldDescriptors[fields.length].fieldName = tempFieldName;

		// the field type
		if ((inFieldType == 'C') || (inFieldType == 'c')) {
			tempFieldDescriptors[fields.length].fieldType = 'C';

			if (inFieldLength > 254) {
				warn("Field Length for "
						+ inFieldName
						+ " set to "
						+ inFieldLength
						+ " Which is longer than 254, not consistent with dbase III");
			}
		} else if ((inFieldType == 'S') || (inFieldType == 's')) {
			tempFieldDescriptors[fields.length].fieldType = 'C';
			warn("Field type for "
					+ inFieldName
					+ " set to S which is flat out wrong people!, I am setting this to C, in the hopes you meant character.");

			if (inFieldLength > 254) {
				warn("Field Length for "
						+ inFieldName
						+ " set to "
						+ inFieldLength
						+ " Which is longer than 254, not consistent with dbase III");
			}

			tempFieldDescriptors[fields.length].fieldLength = 8;
		} else if ((inFieldType == 'D') || (inFieldType == 'd')) {
			tempFieldDescriptors[fields.length].fieldType = 'D';

			if (inFieldLength != 8) {
				warn("Field Length for " + inFieldName + " set to "
						+ inFieldLength + " Setting to 8 digets YYYYMMDD");
			}

			tempFieldDescriptors[fields.length].fieldLength = 8;
		} else if ((inFieldType == 'F') || (inFieldType == 'f')) {
			tempFieldDescriptors[fields.length].fieldType = 'F';

			if (inFieldLength > 20) {
				warn("Field Length for "
						+ inFieldName
						+ " set to "
						+ inFieldLength
						+ " Preserving length, but should be set to Max of 20 not valid for dbase IV, and UP specification, not present in dbaseIII.");
			}
		} else if ((inFieldType == 'N') || (inFieldType == 'n')) {
			tempFieldDescriptors[fields.length].fieldType = 'N';

			if (inFieldLength > 18) {
				warn("Field Length for "
						+ inFieldName
						+ " set to "
						+ inFieldLength
						+ " Preserving length, but should be set to Max of 18 for dbase III specification.");
			}

			if (inDecimalCount < 0) {
				warn("Field Decimal Position for " + inFieldName + " set to "
						+ inDecimalCount
						+ " Setting to 0 no decimal data will be saved.");
				tempFieldDescriptors[fields.length].decimalCount = 0;
			}

			if (inDecimalCount > (inFieldLength - 1)) {
				warn("Field Decimal Position for " + inFieldName + " set to "
						+ inDecimalCount + " Setting to " + (inFieldLength - 1)
						+ " no non decimal data will be saved.");
				tempFieldDescriptors[fields.length].decimalCount = inFieldLength - 1;
			}
		} else if ((inFieldType == 'L') || (inFieldType == 'l')) {
			tempFieldDescriptors[fields.length].fieldType = 'L';

			if (inFieldLength != 1) {
				warn("Field Length for " + inFieldName + " set to "
						+ inFieldLength
						+ " Setting to length of 1 for logical fields.");
			}

			tempFieldDescriptors[fields.length].fieldLength = 1;
		} else {
			// throw new DbaseFileException("Undefined field type "+inFieldType
			// + " For column "+inFieldName);
		}

		// the length of a record
		tempLength = tempLength
				+ tempFieldDescriptors[fields.length].fieldLength;

		// set the new fields.
		fields = tempFieldDescriptors;
		fieldCnt = fields.length;
		headerLength = MINIMUM_HEADER + (32 * fields.length);
		recordLength = tempLength;
	}

	/**
	 * Remove a column from this DbaseFileHeader.
	 * 
	 * @param inFieldName
	 *            The name of the field, will ignore case and trim.
	 * 
	 * @return index of the removed column, -1 if no found
	 * 
	 * @todo This is really ugly, don't know who wrote it, but it needs fixin...
	 */
	public int removeColumn(String inFieldName) {
		int retCol = -1;
		int tempLength = 1;
		DbaseField[] tempFieldDescriptors = new DbaseField[fields.length - 1];

		for (int i = 0, j = 0; i < fields.length; i++) {
			if (!inFieldName.equalsIgnoreCase(fields[i].fieldName.trim())) {
				// if this is the last field and we still haven't found the
				// named field
				if ((i == j) && (i == (fields.length - 1))) {
					System.err.println("Could not find a field named '"
							+ inFieldName + "' for removal");

					return retCol;
				}

				tempFieldDescriptors[j] = fields[i];
				tempFieldDescriptors[j].fieldDataAddress = tempLength;
				tempLength += tempFieldDescriptors[j].fieldLength;

				// only increment j on non-matching fields
				j++;
			} else {
				retCol = i;
			}
		}

		// set the new fields.
		fields = tempFieldDescriptors;
		headerLength = 33 + (32 * fields.length);
		recordLength = tempLength;

		return retCol;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param inWarn
	 *            DOCUMENT ME!
	 * 
	 * @todo addProgessListener handling
	 */
	private void warn(String inWarn) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning(inWarn);
		}
	}

	// Retrieve the length of the field at the given index

	/**
	 * Returns the field length in bytes.
	 * 
	 * @param inIndex
	 *            The field index.
	 * 
	 * @return The length in bytes.
	 */
	public int getFieldLength(int inIndex) {
		return fields[inIndex].fieldLength;
	}

	// Retrieve the location of the decimal point within the field.

	/**
	 * Get the decimal count of this field.
	 * 
	 * @param inIndex
	 *            The field index.
	 * 
	 * @return The decimal count.
	 */
	public int getFieldDecimalCount(int inIndex) {
		return fields[inIndex].decimalCount;
	}

	// Retrieve the Name of the field at the given index

	/**
	 * Get the field name.
	 * 
	 * @param inIndex
	 *            The field index.
	 * 
	 * @return The name of the field.
	 */
	public String getFieldName(int inIndex) {
		return fields[inIndex].fieldName;
	}

	// Retrieve the type of field at the given index

	/**
	 * Get the character class of the field.
	 * 
	 * @param inIndex
	 *            The field index.
	 * 
	 * @return The dbase character representing this field.
	 */
	public char getFieldType(int inIndex) {
		return fields[inIndex].fieldType;
	}

	/**
	 * Get the date this file was last updated.
	 * 
	 * @return The Date last modified.
	 */
	public Date getLastUpdateDate() {
		return date;
	}

	/**
	 * Return the number of fields in the records.
	 * 
	 * @return The number of fields in this table.
	 */
	public int getNumFields() {
		if (fields == null)
			return 0;
		return fields.length;
	}

	/**
	 * Return the number of records in the file
	 * 
	 * @return The number of records in this table.
	 */
	public int getNumRecords() {
		return recordCnt;
	}

	/**
	 * Get the length of the records in bytes.
	 * 
	 * @return The number of bytes per record.
	 */
	public int getRecordLength() {
		return recordLength;
	}

	/**
	 * Get the length of the header
	 * 
	 * @return The length of the header in bytes.
	 */
	public int getHeaderLength() {
		return headerLength;
	}

	/**
	 * Read the header data from the DBF file.
	 * 
	 * @param in
	 *            DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void readHeader(BigByteBuffer2 in) throws IOException {
		// type of file.
		myFileType = in.get();

		if (myFileType != 0x03) {
			throw new IOException("Unsupported DBF file Type "
					+ Integer.toHexString(myFileType));
		}

		// parse the update date information.
		int tempUpdateYear = (int) in.get();
		int tempUpdateMonth = (int) in.get();
		int tempUpdateDay = (int) in.get();
		tempUpdateYear = tempUpdateYear + 1900;

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, tempUpdateYear);
		c.set(Calendar.MONTH, tempUpdateMonth - 1);
		c.set(Calendar.DATE, tempUpdateDay);
		date = c.getTime();

		// read the number of records.
		in.order(ByteOrder.LITTLE_ENDIAN);
		recordCnt = in.getInt();

		// read the length of the header structure.
		headerLength = in.getShort();

		// read the length of a record
		recordLength = in.getShort();

		in.order(ByteOrder.BIG_ENDIAN);

		// skip the reserved bytes in the header.
		in.position(in.position() + 20);

		// calculate the number of Fields in the header
		fieldCnt = (headerLength - FILE_DESCRIPTOR_SIZE - 1)
				/ FILE_DESCRIPTOR_SIZE;

		// read all of the header records
		fields = new DbaseField[fieldCnt];

		for (int i = 0; i < fieldCnt; i++) {
			fields[i] = new DbaseField();

			// read the field name
			byte[] buffer = new byte[11];
			in.get(buffer);
			fields[i].fieldName = new String(buffer);

			// read the field type
			fields[i].fieldType = (char) in.get();

			// read the field data address, offset from the start of the record.
			fields[i].fieldDataAddress = in.getInt();

			// read the field length in bytes
			int tempLength = (int) in.get();

			if (tempLength < 0) {
				tempLength = tempLength + 256;
			}

			fields[i].fieldLength = tempLength;

			// read the field decimal count in bytes
			fields[i].decimalCount = (int) in.get();

			// read the reserved bytes.
			in.position(in.position() + 14);
		}

		// Last byte is a marker for the end of the field definitions.
		in.get();
	}

	/**
	 * Get the largest field size of this table.
	 * 
	 * @return The largt field size iiin bytes.
	 */
	public int getLargestFieldSize() {
		return largestFieldSize;
	}

	/**
	 * Set the number of records in the file
	 * 
	 * @param inNumRecords
	 *            The number of records.
	 */
	public void setNumRecords(int inNumRecords) {
		recordCnt = inNumRecords;
	}

	/**
	 * Write the header data to the DBF file.
	 * 
	 * @param out
	 *            A channel to write to. If you have an OutputStream you can
	 *            obtain the correct channel by using
	 *            java.nio.Channels.newChannel(OutputStream out).
	 * 
	 * @throws IOException
	 *             If errors occur.
	 */
	public void writeHeader(FileChannel out) throws IOException {
		// take care of the annoying case where no records have been added...
		if (headerLength == -1) {
			headerLength = MINIMUM_HEADER;
		}

		// Desde el principio
		out.position(0);

		ByteBuffer buffer = ByteBuffer.allocateDirect(headerLength);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		// write the output file type.
		buffer.put((byte) MAGIC);

		// write the date stuff
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		buffer.put((byte) (c.get(Calendar.YEAR) % 100));
		buffer.put((byte) (c.get(Calendar.MONTH) + 1));
		buffer.put((byte) (c.get(Calendar.DAY_OF_MONTH)));

		// write the number of records in the datafile.
		buffer.putInt(recordCnt);

		// write the length of the header structure.
		buffer.putShort((short) headerLength);

		// write the length of a record
		buffer.putShort((short) recordLength);

		// // write the reserved bytes in the header
		// for (int i=0; i<20; i++) out.writeByteLE(0);
		buffer.position(buffer.position() + 20);

		// write all of the header records
		int tempOffset = 0;

		if (fields != null) {
			for (int i = 0; i < fields.length; i++) {
				// write the field name
				for (int j = 0; j < 11; j++) {
					if (fields[i].fieldName.length() > j) {
						buffer.put((byte) fields[i].fieldName.charAt(j));
					} else {
						buffer.put((byte) 0);
					}
				}

				// write the field type
				buffer.put((byte) fields[i].fieldType);

				// // write the field data address, offset from the start of the
				// record.
				buffer.putInt(tempOffset);
				tempOffset += fields[i].fieldLength;

				// write the length of the field.
				buffer.put((byte) fields[i].fieldLength);

				// write the decimal count.
				buffer.put((byte) fields[i].decimalCount);

				// write the reserved bytes.
				// for (in j=0; jj<14; j++) out.writeByteLE(0);
				buffer.position(buffer.position() + 14);
			}
		}
		// write the end of the field definitions marker
		buffer.put((byte) 0x0D);

		buffer.position(0);

		int r = buffer.remaining();

		while ((r -= out.write(buffer)) > 0) {
			; // do nothing
		}
	}

	/**
	 * Get a simple representation of this header.
	 * 
	 * @return A String representing the state of the header.
	 */
	public String toString() {
		StringBuffer fs = new StringBuffer();

		for (int i = 0, ii = fields.length; i < ii; i++) {
			DbaseField f = fields[i];
			fs.append(f.fieldName + " " + f.fieldType + " " + f.fieldLength
					+ " " + f.decimalCount + " " + f.fieldDataAddress + "\n");
		}

		return "DB3 Header\n" + "Date : " + date + "\n" + "Records : "
				+ recordCnt + "\n" + "Fields : " + fieldCnt + "\n" + fs;
	}

	/**
	 * Crea un DbaseFile.
	 * 
	 * @return DbaseFileHeaderNIO
	 * 
	 * @throws IOException .
	 */
	public static DbaseFileHeaderNIO createNewDbaseHeader() throws IOException {
		DbaseFileHeaderNIO header = new DbaseFileHeaderNIO();

		for (int i = 0, ii = 1; i < ii; i++) {
			// AttributeType type = featureType.getAttributeType(i);
			Class colType = Integer.class;
			String colName = "ID";
			int fieldLen = 10;

			if (fieldLen <= 0) {
				fieldLen = 255;
			}

			// @todo respect field length
			if ((colType == Integer.class) || (colType == Short.class)
					|| (colType == Byte.class)) {
				header.addColumn(colName, 'N', Math.min(fieldLen, 10), 0);
			} else if (colType == Long.class) {
				header.addColumn(colName, 'N', Math.min(fieldLen, 19), 0);
			} else if ((colType == Double.class) || (colType == Float.class)
					|| (colType == Number.class)) {
				int l = Math.min(fieldLen, 33);
				int d = Math.max(l - 2, 0);
				header.addColumn(colName, 'N', l, d);
			} else if (java.util.Date.class.isAssignableFrom(colType)) {
				header.addColumn(colName, 'D', fieldLen, 0);
			} else if (colType == Boolean.class) {
				header.addColumn(colName, 'L', 1, 0);
			} else if (CharSequence.class.isAssignableFrom(colType)) {
				// Possible fix for GEOT-42 : ArcExplorer doesn't like 0 length
				// ensure that maxLength is at least 1
				header.addColumn(colName, 'C', Math.min(254, fieldLen), 0);
			} else if (Geometry.class.isAssignableFrom(colType)) {
				continue;
			} else {
				throw new IOException("Unable to write : " + colType.getName());
			}
		}

		return header;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param sds
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public static DbaseFileHeaderNIO createDbaseHeader(String[] fieldNames,
			int[] fieldTypes, int[] fieldLength) throws IOException {
		DbaseFileHeaderNIO header = new DbaseFileHeaderNIO();

		for (int i = 0, ii = fieldNames.length; i < ii; i++) {

			int type = fieldTypes[i];
			String colName = fieldNames[i];

			// /int fieldLen = ((DBFDriver)sds.getDriver()).getFieldLength(i);
			int fieldLen = fieldLength[i]; // TODO aqu el tamao no es
			// correcto hay que calcularlo,
			// ahora mismo est puesto a pin.
			int decimales = 5;

			// if (fieldLen <= 0) {
			// fieldLen = 255;
			// }
			// TODO [AZABALA] HE INTENTADO CREAR UN TIPO Types.BIGINT y
			// ha petado (por eso lo aado)
			if ((type == Types.DOUBLE) || (type == Types.FLOAT)
					|| (type == Types.INTEGER) || (type == Types.BIGINT))

				header.addColumn(colName, 'N', Math.min(fieldLen, 18),
						decimales);
			if (type == Types.DATE)
				header.addColumn(colName, 'D', fieldLen, 0);
			if ((type == Types.BIT) || (type == Types.BOOLEAN))
				header.addColumn(colName, 'L', 1, 0);
			if ((type == Types.VARCHAR) || (type == Types.CHAR)
					|| (type == Types.LONGVARCHAR))
				header.addColumn(colName, 'C', Math.min(254, fieldLen), 0);
		}

		return header;
	}

	/**
	 * Class for holding the information assicated with a record.
	 */
	class DbaseField {
		// Field Name
		String fieldName;

		// Field Type (C N L D or M)
		char fieldType;

		// Field Data Address offset from the start of the record.
		int fieldDataAddress;

		// Length of the data in bytes
		int fieldLength;

		// Field decimal count in Binary, indicating where the decimal is
		int decimalCount;
	}

	public void setFieldName(int j, String newName) {
		fields[j].fieldName = newName;
	}
}
