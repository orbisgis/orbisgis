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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Calendar;

import org.gdms.data.WarningListener;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.ReadBufferManager;

/**
 * A DbaseFileReader is used to read a dbase III format file. <br>
 * The general use of this class is: <CODE><PRE>
 * 
 * FileChannel in = new FileInputStream("thefile.dbf").getChannel();
 * DbaseFileReader r = new DbaseFileReader( in ) Object[] fields = new
 * Object[r.getHeader().getNumFields()]; while (r.hasNext()) {
 * r.readEntry(fields); // do stuff } r.close();
 * 
 * </PRE></CODE> For consumers who wish to be a bit more selective with their
 * reading of rows, the Row object has been added. The semantics are the same as
 * using the readEntry method, but remember that the Row object is always the
 * same. The values are parsed as they are read, so it pays to copy them out (as
 * each call to Row.read() will result in an expensive String parse). <br>
 * <b>EACH CALL TO readEntry OR readRow ADVANCES THE FILE!</b><br>
 * An example of using the Row method of reading: <CODE><PRE>
 * 
 * FileChannel in = new FileInputStream("thefile.dbf").getChannel();
 * DbaseFileReader r = new DbaseFileReader( in ) int fields =
 * r.getHeader().getNumFields(); while (r.hasNext()) { DbaseFileReader.Row row =
 * r.readRow(); for (int i = 0; i < fields; i++) { // do stuff Foo.bar(
 * row.read(i) ); } } r.close();
 * 
 * </PRE></CODE>
 * 
 * @author Ian Schneider
 * @source $URL:
 *         http://svn.geotools.org/geotools/tags/2.3.1/plugin/shapefile/src
 *         /org/geotools/data/shapefile/dbf/DbaseFileReader.java $
 */
public class DbaseFileReader {

	DbaseFileHeader header;

	ReadBufferManager buffer;

	FileChannel channel;

	CharBuffer charBuffer;

	CharsetDecoder decoder;

	char[] fieldTypes;

	int[] fieldLengths;

	int cnt = 1;

	protected int currentOffset = 0;

	/**
	 * Creates a new instance of DBaseFileReader
	 * 
	 * @param channel
	 *            The readable channel to use.
	 * @throws IOException
	 *             If an error occurs while initializing.
	 */
	public DbaseFileReader(FileChannel channel, WarningListener listener)
			throws IOException {
		this.channel = channel;

		header = new DbaseFileHeader();
		header.readHeader(channel, listener);

		init();
	}

	private void init() throws IOException {
		buffer = new ReadBufferManager(channel);

		this.currentOffset = header.getHeaderLength();

		// The entire file is in little endian
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		// Set up some buffers and lookups for efficiency
		fieldTypes = new char[header.getNumFields()];
		fieldLengths = new int[header.getNumFields()];
		for (int i = 0, ii = header.getNumFields(); i < ii; i++) {
			fieldTypes[i] = header.getFieldType(i);
			fieldLengths[i] = header.getFieldLength(i);
		}

		charBuffer = CharBuffer.allocate(header.getRecordLength() - 1);
		Charset chars = Charset.forName("ISO-8859-1");
		decoder = chars.newDecoder();
	}

	/**
	 * Get the header from this file. The header is read upon instantiation.
	 * 
	 * @return The header associated with this file or null if an error
	 *         occurred.
	 */
	public DbaseFileHeader getHeader() {
		return header;
	}

	/**
	 * Clean up all resources associated with this reader.<B>Highly
	 * recomended.</B>
	 * 
	 * @throws IOException
	 *             If an error occurs.
	 */
	public void close() throws IOException {
		if (channel != null) {
			if (channel.isOpen()) {
				channel.close();
			}
		}

		buffer = null;
		channel = null;
		charBuffer = null;
		decoder = null;
		header = null;
	}

	/**
	 * Query the reader as to whether there is another record.
	 * 
	 * @return True if more records exist, false otherwise.
	 */
	public boolean hasNext() {
		return cnt < header.getNumRecords() + 1;
	}

	/**
	 * @param row
	 * @param column
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytes(int pos, int length) throws IOException {
		byte[] bytes = new byte[length];
		buffer.get(pos, bytes);
		return bytes;
	}

	public Value getFieldValue(int row, int column,
			WarningListener warningListener) throws IOException {
		int fieldPosition = getPositionFor(row, column);
		int fieldLength = getLengthFor(column);
		byte[] fieldBytes = getBytes(fieldPosition, fieldLength);
		ByteBuffer field = ByteBuffer.wrap(fieldBytes);

		charBuffer.clear();
		decoder.decode(field, charBuffer, true);
		charBuffer.flip();

		return readObject(0, column, warningListener);

	}

	private int getLengthFor(int column) {
		return header.getFieldLength(column);
	}

	private int getPositionFor(int row, int column) {
		int recordOffset = header.getHeaderLength() + row
				* header.getRecordLength() + 1;
		int fieldOffset = 0;
		for (int i = 0; i < column; i++) {
			fieldOffset += header.getFieldLength(i);
		}

		return fieldOffset + recordOffset;
	}

	private Value readObject(final int fieldOffset, final int fieldNum,
			WarningListener warningListener) throws IOException {
		final char type = fieldTypes[fieldNum];
		final int fieldLen = fieldLengths[fieldNum];
		Value object = null;

		// System.out.println( charBuffer.subSequence(fieldOffset,fieldOffset +
		// fieldLen));

		if (fieldLen > 0) {

			String numberString = extractNumberString(charBuffer, fieldOffset,
					fieldLen);
			switch (type) {
			// (L)logical (T,t,F,f,Y,y,N,n)
			case 'l':
			case 'L':
				switch (charBuffer.charAt(fieldOffset)) {

				case 't':
				case 'T':
				case 'Y':
				case 'y':
					object = ValueFactory.createValue(true);
					break;
				case 'f':
				case 'F':
				case 'N':
				case 'n':
					object = ValueFactory.createValue(false);
					break;
				default:

					throw new IOException("Unknown logical value : '"
							+ charBuffer.charAt(fieldOffset) + "'");
				}
				break;
			// (C)character (String)
			case 'c':
			case 'C':
				// oh, this seems like a lot of work to parse strings...but,
				// For some reason if zero characters ( (int) char == 0 ) are
				// allowed
				// in these strings, they do not compare correctly later on down
				// the
				// line....
				int start = fieldOffset;
				int end = fieldOffset + fieldLen - 1;
				// trim off whitespace and 'zero' chars
				while (start < end) {
					char c = charBuffer.get(start);
					if (c == 0 || Character.isWhitespace(c)) {
						start++;
					} else
						break;
				}
				while (end > start) {
					char c = charBuffer.get(end);
					if (c == 0 || Character.isWhitespace(c)) {
						end--;
					} else
						break;
				}
				// set up the new indexes for start and end
				charBuffer.position(start).limit(end + 1);
				String s = charBuffer.toString();
				// this resets the limit...
				charBuffer.clear();
				object = ValueFactory.createValue(s);
				break;
			// (D)date (Date)
			case 'd':
			case 'D':
				if (charBuffer.toString().equals("00000000")) {
					return ValueFactory.createNullValue();
				} else {
					try {
						String tempString = charBuffer.subSequence(fieldOffset,
								fieldOffset + 4).toString();
						int tempYear = Integer.parseInt(tempString);
						tempString = charBuffer.subSequence(fieldOffset + 4,
								fieldOffset + 6).toString();
						int tempMonth = Integer.parseInt(tempString) - 1;
						tempString = charBuffer.subSequence(fieldOffset + 6,
								fieldOffset + 8).toString();
						int tempDay = Integer.parseInt(tempString);
						Calendar cal = Calendar.getInstance();
						cal.clear();
						cal.set(Calendar.YEAR, tempYear);
						cal.set(Calendar.MONTH, tempMonth);
						cal.set(Calendar.DAY_OF_MONTH, tempDay);
						object = ValueFactory.createValue(cal.getTime());
					} catch (NumberFormatException nfe) {
						// todo: use progresslistener, this isn't a grave error.
					}
				}
				break;

			// (F)floating (Double)
			case 'n':
			case 'N':
				try {
					if (header.getFieldDecimalCount(fieldNum) == 0) {
						object = ValueFactory.createValue(Integer
								.parseInt(numberString));
						break;
					}
					// else will fall through to the floating point number
				} catch (NumberFormatException e) {

					// todo: use progresslistener, this isn't a grave error.

					// don't do this!!! the Double parse will be attemted as we
					// fall
					// through, so no need to create a new Object. -IanS
					// object = new Integer(0);

					// Lets try parsing a long instead...
					try {
						object = ValueFactory.createValue(Long
								.parseLong(numberString));
						break;
					} catch (NumberFormatException e2) {

					}
				}

			case 'f':
			case 'F': // floating point number
				try {

					object = ValueFactory.createValue(Double
							.parseDouble(numberString));
				} catch (NumberFormatException e) {
					// todo: use progresslistener, this isn't a grave error,
					// though it
					// does indicate something is wrong

					// okay, now whatever we got was truly undigestable. Lets go
					// with
					// a zero Double.
					object = ValueFactory.createValue(0.0d);
					warningListener
							.throwWarning("Unparseable numeric value. 0.0 used: "
									+ numberString);
				}
				break;
			default:
				throw new IOException("Invalid field type : " + type);
			}

		}
		return object;
	}

	/**
	 * @param charBuffer2
	 * @param fieldOffset
	 * @param fieldLen
	 */
	private final String extractNumberString(final CharBuffer charBuffer2,
			final int fieldOffset, final int fieldLen) {
		String thing = charBuffer2.subSequence(fieldOffset,
				fieldOffset + fieldLen).toString().trim();
		return thing;
	}

	public int getRecordCount() {
		return header.getNumRecords();
	}

	public int getFieldCount() {
		return header.getNumFields();
	}

}
