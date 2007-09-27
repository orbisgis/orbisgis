/*
 *    GeoTools - OpenSource mapping toolkit
 *    http://geotools.org
 *    (C) 2002-2006, Geotools Project Managment Committee (PMC)
 *    (C) 2002, Centre for Computational Geography
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    This file is based on an origional contained in the GISToolkit project:
 *    http://gistoolkit.sourceforge.net/
 */
package org.gdms.driver.dbf;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.gdms.data.WarningListener;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.NumericValue;
import org.gdms.driver.WriteBufferManager;

/**
 * A DbaseFileReader is used to read a dbase III format file. The general use of
 * this class is: <CODE><PRE>
 *
 * DbaseFileHeader header = ... WritableFileChannel out = new
 * FileOutputStream("thefile.dbf").getChannel(); DbaseFileWriter w = new
 * DbaseFileWriter(header,out); while ( moreRecords ) { w.write( getMyRecord() ); }
 * w.close();
 *
 * </PRE></CODE> You must supply the <CODE>moreRecords</CODE> and <CODE>getMyRecord()</CODE>
 * logic...
 *
 * @author Ian Schneider
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src/org/geotools/data/shapefile/dbf/DbaseFileWriter.java $
 */
public class DbaseFileWriter {

	private DbaseFileHeader header;

	private DbaseFileWriter.FieldFormatter formatter = new DbaseFileWriter.FieldFormatter();

	private FileChannel channel;

	private WriteBufferManager buffer;

	private final Number NULL_NUMBER = new Integer(0);

	private final String NULL_STRING = "";

	private final Date NULL_DATE = new Date();

	/**
	 * Create a DbaseFileWriter using the specified header and writing to the
	 * given channel.
	 *
	 * @param header
	 *            The DbaseFileHeader to write.
	 * @param out
	 *            The Channel to write to.
	 * @throws IOException
	 *             If errors occur while initializing.
	 */
	public DbaseFileWriter(DbaseFileHeader header, FileChannel out)
			throws IOException {
		header.writeHeader(out);
		this.header = header;
		this.channel = out;
		init();
	}

	private void init() throws IOException {
		buffer = new WriteBufferManager(this.channel);
	}

	/**
	 * Write a single dbase record.
	 *
	 * @param record
	 *            The entries to write.
	 * @param rowNumber
	 * @param warningListener
	 * @throws IOException
	 *             If IO error occurs.
	 * @throws DbaseFileException
	 *             If the entry doesn't comply to the header.
	 */
	public void write(Object[] record, int rowNumber,
			WarningListener warningListener) throws IOException,
			DbaseFileException {

		if (record.length != header.getNumFields()) {
			throw new DbaseFileException("Wrong number of fields "
					+ record.length + " expected " + header.getNumFields());
		}

		// put the 'not-deleted' marker
		buffer.put((byte) ' ');

		for (int i = 0; i < header.getNumFields(); i++) {
			String fieldString;
			try {
				fieldString = fieldString(record[i], i);
			} catch (ClassCastException e) {
				warningListener.throwWarning("Incompatible types at record "
						+ rowNumber + " field " + i);
				continue;
			}
			if (header.getFieldLength(i) != fieldString.getBytes().length) {
				warningListener.throwWarning("truncated content at row "
						+ rowNumber + " field " + i);
				buffer.put(new byte[header.getFieldLength(i)]);
			} else {
				buffer.put(fieldString.getBytes());
			}

		}
	}

	private String fieldString(Object obj, final int col) {
		String o;
		if (obj instanceof NullValue) {
			obj = null;
		}
		final int fieldLen = header.getFieldLength(col);
		switch (header.getFieldType(col)) {
		case 'C':
		case 'c':
			o = formatter.getFieldString(fieldLen, obj == null ? NULL_STRING
					: obj.toString());
			break;
		case 'L':
		case 'l':
			o = (obj == null ? "F" : ((BooleanValue) obj).getValue() ? "T"
					: "F");
			// o = formatter.getFieldString(
			// fieldLen,
			// o
			// );
			break;
		case 'M':
		case 'G':
			o = formatter.getFieldString(fieldLen, obj == null ? NULL_STRING
					: obj.toString());
			break;
		case 'N':
		case 'n':
			// int?
			if (header.getFieldDecimalCount(col) == 0) {

				o = formatter.getFieldString(fieldLen, 0,
						(Number) (obj == null ? NULL_NUMBER
								: ((NumericValue) obj).doubleValue()));
				break;
			}
		case 'F':
		case 'f':
			o = formatter.getFieldString(fieldLen, header
					.getFieldDecimalCount(col),
					(Number) (obj == null ? NULL_NUMBER : ((NumericValue) obj)
							.doubleValue()));
			break;
		case 'D':
		case 'd':
			o = formatter.getFieldString((Date) (obj == null ? NULL_DATE
					: ((DateValue) obj).getValue()));
			break;
		default:
			throw new RuntimeException("Unknown type "
					+ header.getFieldType(col));
		}

		return o;
	}

	/**
	 * Release resources associated with this writer. <B>Highly recommended</B>
	 *
	 * @throws IOException
	 *             If errors occur.
	 */
	public void close() throws IOException {
		// IANS - GEOT 193, bogus 0x00 written. According to dbf spec, optional
		// eof 0x1a marker is, well, optional. Since the original code wrote a
		// 0x00 (which is wrong anyway) lets just do away with this :)
		// - produced dbf works in OpenOffice and ArcExplorer java, so it must
		// be okay.
		// buffer.position(0);
		// buffer.put((byte) 0).position(0).limit(1);
		// write();
		buffer.flush();
		if (channel.isOpen()) {
			channel.close();
		}
		buffer = null;
		channel = null;
		formatter = null;
	}

	/** Utility for formatting Dbase fields. */
	public static class FieldFormatter {
		private StringBuffer buffer = new StringBuffer(255);

		private NumberFormat numFormat = NumberFormat
				.getNumberInstance(Locale.US);

		private Calendar calendar = Calendar.getInstance(Locale.US);

		private String emptyString;

		private static final int MAXCHARS = 255;

		public FieldFormatter() {
			// Avoid grouping on number format
			numFormat.setGroupingUsed(false);

			// build a 255 white spaces string
			StringBuffer sb = new StringBuffer(MAXCHARS);
			sb.setLength(MAXCHARS);
			for (int i = 0; i < MAXCHARS; i++) {
				sb.setCharAt(i, ' ');
			}

			emptyString = sb.toString();
		}

		public String getFieldString(int size, String s) {
			buffer.replace(0, size, emptyString);
			buffer.setLength(size);
			// international characters must be accounted for so size != length.
			int maxSize = size;
			if (s != null) {
				buffer.replace(0, size, s);
				int currentBytes = s.substring(0, Math.min(size, s.length()))
						.getBytes().length;
				if (currentBytes > size) {
					char[] c = new char[1];
					for (int index = size - 1; currentBytes > size; index--) {
						c[0] = buffer.charAt(index);
						String string = new String(c);
						buffer.deleteCharAt(index);
						currentBytes -= string.getBytes().length;
						maxSize--;
					}
				} else {
					if (s.length() < size) {
						maxSize = size - (currentBytes - s.length());
						for (int i = s.length(); i < size; i++) {
							buffer.append(' ');
						}
					}
				}
			}

			buffer.setLength(maxSize);

			return buffer.toString();
		}

		public String getFieldString(Date d) {

			if (d != null) {
				buffer.delete(0, buffer.length());

				calendar.setTime(d);
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH) + 1; // returns 0
				// based month?
				int day = calendar.get(Calendar.DAY_OF_MONTH);

				if (year < 1000) {
					if (year >= 100) {
						buffer.append("0");
					} else if (year >= 10) {
						buffer.append("00");
					} else {
						buffer.append("000");
					}
				}
				buffer.append(year);

				if (month < 10) {
					buffer.append("0");
				}
				buffer.append(month);

				if (day < 10) {
					buffer.append("0");
				}
				buffer.append(day);
			} else {
				buffer.setLength(8);
				buffer.replace(0, 8, emptyString);
			}

			buffer.setLength(8);
			return buffer.toString();
		}

		public String getFieldString(int size, int decimalPlaces, Number n) {
			buffer.delete(0, buffer.length());

			if (n != null) {
				numFormat.setMaximumFractionDigits(decimalPlaces);
				numFormat.setMinimumFractionDigits(decimalPlaces);
				numFormat.format(n, buffer, new FieldPosition(
						NumberFormat.INTEGER_FIELD));
			}

			int diff = size - buffer.length();
			if (diff >= 0) {
				while (diff-- > 0) {
					buffer.insert(0, ' ');
				}
			} else {
				buffer.setLength(size);
			}
			return buffer.toString();
		}
	}

}
