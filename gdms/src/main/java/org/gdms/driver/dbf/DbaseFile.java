/*
 * Created on 16-feb-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.gdms.driver.dbf;

/**
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.gdms.driver.shapefile.BigByteBuffer2;

/**
 * Class to read and write data to a dbase III format file. Creation date:
 * (5/15/2001 5:15:13 PM)
 */
public class DbaseFile {
	// Header information for the DBase File
	private DbaseFileHeader myHeader;

	private RandomAccessFile raf;

	private FileChannel channel;

	private BigByteBuffer2 buffer;

	private FileChannel.MapMode mode;

	private FieldFormatter formatter = new FieldFormatter();

	private int posActual = -1;

	private int recordOffset;

	private ByteBuffer cachedRecord = null;

	private byte[] bytesCachedRecord = null;

	private final Number NULL_NUMBER = new Integer(0);

	private final String NULL_STRING = "";

	private final String NULL_DATE = "        ";

	private Charset chars;

	/** Utility for formatting Dbase fields. */
	public static class FieldFormatter {
		private StringBuffer buffer = new StringBuffer(255);

		private NumberFormat numFormat = NumberFormat
				.getNumberInstance(Locale.US);

		private Calendar calendar = Calendar.getInstance(Locale.US);

		private String emtpyString;

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

			emtpyString = sb.toString();
		}

		public String getFieldString(int size, String s) {
			buffer.replace(0, size, emtpyString);
			buffer.setLength(size);

			if (s != null) {
				buffer.replace(0, size, s);
				if (s.length() <= size) {
					for (int i = s.length(); i < size; i++) {
						buffer.append(' ');
					}
				}
			}

			buffer.setLength(size);
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
				buffer.replace(0, 8, emtpyString);
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

	// Retrieve number of records in the DbaseFile
	public int getRecordCount() {
		return myHeader.getNumRecords();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getFieldCount() {
		return myHeader.getNumFields();
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public boolean getBooleanFieldValue(int rowIndex, int fieldId) {
		int recordOffset = (myHeader.getRecordLength() * rowIndex)
				+ myHeader.getHeaderLength() + 1;

		// Se calcula el offset del campo
		int fieldOffset = 0;

		for (int i = 0; i < (fieldId - 1); i++) {
			fieldOffset += myHeader.getFieldLength(i);
		}

		buffer.position(recordOffset + fieldOffset);

		char bool = (char) buffer.get();

		return ((bool == 't') || (bool == 'T') || (bool == 'Y') || (bool == 'y'));
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param rowIndex
	 *            DOCUMENT ME!
	 * @param fieldId
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 * @throws UnsupportedEncodingException
	 */
	public String getStringFieldValue(int rowIndex, int fieldId)
			throws UnsupportedEncodingException {
		int fieldOffset = myHeader.getFieldDescription(fieldId).myFieldDataAddress;
		byte[] data = new byte[myHeader.getFieldLength(fieldId)];
		if (rowIndex != posActual) {
			recordOffset = (myHeader.getRecordLength() * rowIndex)
					+ myHeader.getHeaderLength() + 1;

			/*
			 * System.err.println("getStringFieldValue: rowIndex = " +
			 * rowIndex); System.err.println("recordOffset = " + recordOffset + "
			 * fieldOffset=" + fieldOffset);
			 */
			buffer.position(recordOffset);
			buffer.get(bytesCachedRecord);
			cachedRecord = ByteBuffer.wrap(bytesCachedRecord);
			posActual = rowIndex;

		}
		cachedRecord.position(fieldOffset);
		cachedRecord.get(data);

		return new String(data, chars.name());

	}

	public void setFieldValue(int rowIndex, int fieldId, Object obj)
			throws IOException {
		int fieldOffset = myHeader.getFieldDescription(fieldId).myFieldDataAddress;
		String str = fieldString(obj, fieldId);
		byte[] data = new byte[myHeader.getFieldLength(fieldId)];
		recordOffset = (myHeader.getRecordLength() * rowIndex)
				+ myHeader.getHeaderLength() + 1;

		ByteBuffer aux = ByteBuffer.wrap(data);
		aux.put(str.getBytes(chars.name()));
//		raf.seek(recordOffset + fieldOffset);
//		raf.writeBytes(str);
		aux.flip();
		channel.write(aux, recordOffset + fieldOffset);
		channel.force(true);


	}


	/**
	 * Retrieve the name of the given column.
	 *
	 * @param inIndex
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getFieldName(int inIndex) {
		return myHeader.getFieldName(inIndex).trim();
	}

	/**
	 * Retrieve the type of the given column.
	 *
	 * @param inIndex
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public char getFieldType(int inIndex) {
		return myHeader.getFieldType(inIndex);
	}

	/**
	 * Retrieve the length of the given column.
	 *
	 * @param inIndex
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getFieldLength(int inIndex) {
		return myHeader.getFieldLength(inIndex);
	}

	/*
	 * Retrieve the value of the given column as string.
	 *
	 * @param idField DOCUMENT ME! @param idRecord DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * public Object getFieldValue(int idField, long idRecord) throws
	 * IOException { Object[] tmpReg = getRecord(idRecord); return
	 * tmpReg[idField]; }
	 */
	/*
	 * DOCUMENT ME!
	 *
	 * @param idField DOCUMENT ME! @param idRecord DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * public double getFieldValueAsDouble(int idField, int idRecord) throws
	 * IOException { Object[] tmpReg = getRecord(idRecord); return (double)
	 * Double.parseDouble(tmpReg[idField].toString()); }
	 */

	/**
	 * Retrieve the location of the decimal point.
	 *
	 * @param inIndex
	 *            DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public int getFieldDecimalLength(int inIndex) {
		return myHeader.getFieldDecimalCount(inIndex);
	}

	/**
	 * read the DBF file into memory.
	 *
	 * @param file
	 *            DOCUMENT ME!
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void open(File file) throws IOException {
		/*
		 * 01h DOS USA code page 437 02h DOS Multilingual code page 850 03h
		 * Windows ANSI code page 1252 04h Standard Macintosh 64h EE MS-DOS code
		 * page 852 65h Nordic MS-DOS code page 865 66h Russian MS-DOS code page
		 * 866 67h Icelandic MS-DOS 68h Kamenicky (Czech) MS-DOS 69h Mazovia
		 * (Polish) MS-DOS 6Ah Greek MS-DOS (437G) 6Bh Turkish MS-DOS 96h
		 * Russian Macintosh 97h Eastern European Macintosh 98h Greek Macintosh
		 * C8h Windows EE code page 1250 C9h Russian Windows CAh Turkish Windows
		 * CBh Greek Windows
		 */
		if (file.canWrite()) {
			try {
				raf = new RandomAccessFile(file, "rw");
				mode = FileChannel.MapMode.READ_WRITE;
			} catch (FileNotFoundException e) {
				raf = new RandomAccessFile(file, "r");
				mode = FileChannel.MapMode.READ_ONLY;
			}
		} else {
			raf = new RandomAccessFile(file, "r");
			mode = FileChannel.MapMode.READ_ONLY;
		}
		channel = raf.getChannel();

		// buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0,
		// channel.size());
		buffer = new BigByteBuffer2(channel, mode);

		// create the header to contain the header information.
		myHeader = new DbaseFileHeader();
		myHeader.readHeader(buffer);
		switch (myHeader.getLanguageID()) {
		case 0x01:
			chars = Charset.forName("US-ASCII");
			break;
		case 0x02:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x03:
			chars = Charset.forName("windows-1252");
			break;
		case 0x04:
			chars = Charset.forName("mac");
			break;
		case 0x64:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x65:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x66:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x67:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x68:
			chars = Charset.forName("greek");
			break;
		case 0x69:
			chars = Charset.forName("ISO-8859-1");
			break;
		case 0x6A:
			chars = Charset.forName("greek");
			break;
		case 0x6B:
			chars = Charset.forName("ISO-8859-1");
			break;

		default:
			chars = Charset.forName("ISO-8859-1");
		}
		bytesCachedRecord = new byte[myHeader.getRecordLength()];
	}

	/**
	 * Removes all data from the dataset
	 *
	 * @throws IOException
	 *             DOCUMENT ME!
	 */
	public void close() throws IOException {
		raf.close();
		channel.close();
		buffer = null;
	}

	public FileChannel getWriteChannel() {
		return channel;
	}

	private String fieldString(Object obj, final int col) {
		String o;
		final int fieldLen = myHeader.getFieldLength(col);
		switch (myHeader.getFieldType(col)) {
		case 'C':
		case 'c':
			o = formatter.getFieldString(fieldLen, (obj == null) ? NULL_STRING
					: ((String) obj));
			break;
		case 'L':
		case 'l':
			o = (obj == null) ? "F"
					: ((Boolean) obj).booleanValue() == true ? "T" : "F";
			break;
		case 'M':
		case 'G':
			o = formatter.getFieldString(fieldLen, (obj == null) ? NULL_STRING
					: ((String) obj));
			break;
		case 'N':
		case 'n':
		case 'F':
		case 'f':
			Number number = null;
			if (obj == null) {
				number = NULL_NUMBER;
			} else {
				Number gVal = (Number) obj;
				number = new Double(gVal.doubleValue());
			}
			o = formatter.getFieldString(fieldLen, myHeader
					.getFieldDecimalCount(col), number);
			break;
		case 'D':
		case 'd':
			if (obj == null)
				o = NULL_DATE;
			else
				o = formatter.getFieldString(((Date) obj));
			break;
		default:
			throw new RuntimeException("Unknown type "
					+ myHeader.getFieldType(col));
		}

		return o;
	}

}
