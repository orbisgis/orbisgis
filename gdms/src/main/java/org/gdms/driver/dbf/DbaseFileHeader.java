/*
 * Created on 16-feb-2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.gdms.driver.dbf;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Date;

import org.gdms.driver.shapefile.BigByteBuffer2;


/**
 * Class to represent the header of a Dbase III file. Creation date: (5/15/2001
 * 5:15:30 PM)
 */
public class DbaseFileHeader {
    // Constant for the size of a record
    private int FILE_DESCRIPTOR_SIZE = 32;

    // type of the file, must be 03h
    private int myFileType = 0x03;

    // Date the file was last updated.
    private Date myUpdateDate = new Date();

    // Number of records in the datafile
    private int myNumRecords = 0;

    // Length of the header structure
    private int myHeaderLength;

    // Length of the records
    private int myRecordLength;

    // Number of fields in the record.
    private int myNumFields;

    // collection of header records.
    private DbaseFieldDescriptor[] myFieldDescriptions;

	private byte myLanguageID;

    /**
     * DbaseFileHreader constructor comment.
     */
    public DbaseFileHeader() {
        super();
    }

    /** 
     * Add a column to this DbaseFileHeader. The type is one of (C N L or D)
     * character, number, logical(true/false), or date. The Field length is
     * the total length in bytes reserved for this column. The decimal count
     * only applies to numbers(N), and floating point values (F), and refers
     * to the number of characters to reserve after the decimal point.
     *
     * @param inFieldName DOCUMENT ME!
     * @param inFieldType DOCUMENT ME!
     * @param inFieldLength DOCUMENT ME!
     * @param inDecimalCount DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void addColumn(String inFieldName, char inFieldType,
        int inFieldLength, int inDecimalCount) throws Exception {
        if (inFieldLength <= 0) {
            inFieldLength = 1;
        }

        if (myFieldDescriptions == null) {
            myFieldDescriptions = new DbaseFieldDescriptor[0];
        }

        int tempLength = 1; // the length is used for the offset, and there is a * for deleted as the first byte
        DbaseFieldDescriptor[] tempFieldDescriptors = new DbaseFieldDescriptor[myFieldDescriptions.length +
            1];

        for (int i = 0; i < myFieldDescriptions.length; i++) {
            myFieldDescriptions[i].myFieldDataAddress = tempLength;
            tempLength = tempLength + myFieldDescriptions[i].myFieldLength;
            tempFieldDescriptors[i] = myFieldDescriptions[i];
        }

        tempFieldDescriptors[myFieldDescriptions.length] = new DbaseFieldDescriptor();
        tempFieldDescriptors[myFieldDescriptions.length].myFieldLength = inFieldLength;
        tempFieldDescriptors[myFieldDescriptions.length].myDecimalCount = inDecimalCount;
        tempFieldDescriptors[myFieldDescriptions.length].myFieldDataAddress = tempLength;

        // set the field name
        String tempFieldName = inFieldName;

        if (tempFieldName == null) {
            tempFieldName = "NoName";
        }

        if (tempFieldName.length() > 11) {
            tempFieldName = tempFieldName.substring(0, 11);
            warn("FieldName " + inFieldName +
                " is longer than 11 characters, truncating to " +
                tempFieldName);
        }

        tempFieldDescriptors[myFieldDescriptions.length].myFieldName = tempFieldName;

        // the field type
        if ((inFieldType == 'C') || (inFieldType == 'c')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'C';

            if (inFieldLength > 254) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength +
                    " Which is longer than 254, not consistent with dbase III");
            }
        } else if ((inFieldType == 'S') || (inFieldType == 's')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'C';
            warn("Field type for " + inFieldName +
                " set to S which is flat out wrong people!, I am setting this to C, in the hopes you meant character.");

            if (inFieldLength > 254) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength +
                    " Which is longer than 254, not consistent with dbase III");
            }

            tempFieldDescriptors[myFieldDescriptions.length].myFieldLength = 8;
        } else if ((inFieldType == 'D') || (inFieldType == 'd')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'D';

            if (inFieldLength != 8) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength + " Setting to 8 digets YYYYMMDD");
            }

            tempFieldDescriptors[myFieldDescriptions.length].myFieldLength = 8;
        } else if ((inFieldType == 'F') || (inFieldType == 'f')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'F';

            if (inFieldLength > 20) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength +
                    " Preserving length, but should be set to Max of 20 not valid for dbase IV, and UP specification, not present in dbaseIII.");
            }
        } else if ((inFieldType == 'N') || (inFieldType == 'n')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'N';

            if (inFieldLength > 18) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength +
                    " Preserving length, but should be set to Max of 18 for dbase III specification.");
            }

            if (inDecimalCount < 0) {
                warn("Field Decimal Position for " + inFieldName + " set to " +
                    inDecimalCount +
                    " Setting to 0 no decimal data will be saved.");
                tempFieldDescriptors[myFieldDescriptions.length].myDecimalCount = 0;
            }

            if (inDecimalCount > (inFieldLength - 1)) {
                warn("Field Decimal Position for " + inFieldName + " set to " +
                    inDecimalCount + " Setting to " + (inFieldLength - 1) +
                    " no non decimal data will be saved.");
                tempFieldDescriptors[myFieldDescriptions.length].myDecimalCount = inFieldLength -
                    1;
            }
        } else if ((inFieldType == 'L') || (inFieldType == 'l')) {
            tempFieldDescriptors[myFieldDescriptions.length].myFieldType = 'L';

            if (inFieldLength != 1) {
                warn("Field Length for " + inFieldName + " set to " +
                    inFieldLength +
                    " Setting to length of 1 for logical fields.");
            }

            tempFieldDescriptors[myFieldDescriptions.length].myFieldLength = 1;
        } else {
            throw new Exception("Undefined field type " + inFieldType +
                " For column " + inFieldName);
        }

        // the length of a record
        tempLength = tempLength +
            tempFieldDescriptors[myFieldDescriptions.length].myFieldLength;

        // set the new fields.
        myFieldDescriptions = tempFieldDescriptors;
        myHeaderLength = 33 + (32 * myFieldDescriptions.length);
        myNumFields = myFieldDescriptions.length;
        myRecordLength = tempLength;
    }

    /**
     * Remove a column from this DbaseFileHeader.
     *
     * @param inFieldName DOCUMENT ME!
     *
     * @return index of the removed column, -1 if no found
     */
    public int removeColumn(String inFieldName) {
        int retCol = -1;
        int tempLength = 1;
        DbaseFieldDescriptor[] tempFieldDescriptors = new DbaseFieldDescriptor[myFieldDescriptions.length -
            1];

        for (int i = 0, j = 0; i < myFieldDescriptions.length; i++) {
            if (!inFieldName.equalsIgnoreCase(
                        myFieldDescriptions[i].myFieldName.trim())) {
                // if this is the last field and we still haven't found the
                // named field
                if ((i == j) && (i == (myFieldDescriptions.length - 1))) {
                    System.err.println("Could not find a field named '" +
                        inFieldName + "' for removal");

                    return retCol;
                }

                tempFieldDescriptors[j] = myFieldDescriptions[i];
                tempFieldDescriptors[j].myFieldDataAddress = tempLength;
                tempLength += tempFieldDescriptors[j].myFieldLength;

                // only increment j on non-matching fields
                j++;
            } else {
                retCol = i;
            }
        }

        // set the new fields.
        myFieldDescriptions = tempFieldDescriptors;
        myHeaderLength = 33 + (32 * myFieldDescriptions.length);
        myNumFields = myFieldDescriptions.length;
        myRecordLength = tempLength;

        return retCol;
    }

    /**
     * DOCUMENT ME!
     *
     * @param inWarn DOCUMENT ME!
     */
    private void warn(String inWarn) {
        //TODO Descomentar esto cuando tenga la clase warning support
        //    	warnings.warn(inWarn);
    }

    /**
     * Return the Field Descriptor for the given field.
     *
     * @param inIndex DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DbaseFieldDescriptor getFieldDescription(int inIndex) {
        return myFieldDescriptions[inIndex];
    }

    // Retrieve the length of the field at the given index
    public int getFieldLength(int inIndex) {
        return myFieldDescriptions[inIndex].myFieldLength;
    }

    // Retrieve the location of the decimal point within the field.
    public int getFieldDecimalCount(int inIndex) {
        return myFieldDescriptions[inIndex].myDecimalCount;
    }

    // Retrieve the Name of the field at the given index
    public String getFieldName(int inIndex) {
        return myFieldDescriptions[inIndex].myFieldName;
    }

    // Retrieve the type of field at the given index
    public char getFieldType(int inIndex) {
        return myFieldDescriptions[inIndex].myFieldType;
    }

    /**
     * Return the date this file was last updated.
     *
     * @return DOCUMENT ME!
     */
    public Date getLastUpdateDate() {
        return myUpdateDate;
    }

    /**
     * Return the number of fields in the records.
     *
     * @return DOCUMENT ME!
     */
    public int getNumFields() {
        return myNumFields;
    }

    /**
     * Return the number of records in the file
     *
     * @return DOCUMENT ME!
     */
    public int getNumRecords() {
        return myNumRecords;
    }

    /**
     * Return the length of the records in bytes.
     *
     * @return DOCUMENT ME!
     */
    public int getRecordLength() {
        return myRecordLength;
    }

    /**
     * Return the length of the header
     *
     * @return DOCUMENT ME!
     */
    public int getHeaderLength() {
        return myHeaderLength;
    }

    /**
     * Read the header data from the DBF file.
     *
     * @param in DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void readHeader(BigByteBuffer2 in) throws IOException {
        // type of file.
        myFileType = in.get();

        if (myFileType != 0x03) {
            throw new IOException("Unsupported DBF file Type " +
                Integer.toHexString(myFileType));
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
        myUpdateDate = c.getTime();

        // read the number of records.
        in.order(ByteOrder.LITTLE_ENDIAN);
        myNumRecords = in.getInt();

        // read the length of the header structure.
        myHeaderLength = in.getShort();

        // read the length of a record
        myRecordLength = in.getShort(); //posicon 0h

        in.order(ByteOrder.BIG_ENDIAN);

        // skip the reserved bytes in the header.
        // in.position(in.position() + 20);

        // Leemos el byte de language
        in.position(29);
        myLanguageID = in.get();

        // Posicionamos para empezar a leer los campos.
        in.position(32);

        // calculate the number of Fields in the header
        myNumFields = (myHeaderLength - FILE_DESCRIPTOR_SIZE - 1) / FILE_DESCRIPTOR_SIZE;

        // read all of the header records
        myFieldDescriptions = new DbaseFieldDescriptor[myNumFields];
        int fieldOffset = 0;

        for (int i = 0; i < myNumFields; i++) {
            myFieldDescriptions[i] = new DbaseFieldDescriptor();

            // read the field name
            byte[] buffer = new byte[11];
            in.get(buffer);
            myFieldDescriptions[i].myFieldName = new String(buffer);

            // read the field type
            myFieldDescriptions[i].myFieldType = (char) in.get();

            // read the field data address, offset from the start of the record.
            myFieldDescriptions[i].myFieldDataAddress = in.getInt();

            // read the field length in bytes
            int tempLength = (int) in.get();

            if (tempLength < 0) {
                tempLength = tempLength + 256;
            }

            myFieldDescriptions[i].myFieldLength = tempLength;

            // read the field decimal count in bytes
            myFieldDescriptions[i].myDecimalCount = (int) in.get();

            // NUEVO: Calculamos los offsets aqu para no
            // tener que recalcular cada vez que nos piden
            // algo.
            myFieldDescriptions[i].myFieldDataAddress = fieldOffset;
            fieldOffset += tempLength;
            // Fin NUEVO
            // read the reserved bytes.
            in.position(in.position() + 14);
        }

        // Last byte is a marker for the end of the field definitions.
        in.get();
    }

    /**
     * Set the number of records in the file
     *
     * @param inNumRecords DOCUMENT ME!
     */
    protected void setNumRecords(int inNumRecords) {
        myNumRecords = inNumRecords;
    }

    /*
     * Write the header data to the DBF file.
     *
     * @param out DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     *
           public void writeHeader(LEDataOutputStream out) throws Exception {
               // write the output file type.
               out.writeByte(myFileType);
               // write the date stuff
               Calendar c = Calendar.getInstance();
               c.setTime(new Date());
               out.writeByte(c.get(Calendar.YEAR) - 1900);
               out.writeByte(c.get(Calendar.MONTH) + 1);
               out.writeByte(c.get(Calendar.DAY_OF_MONTH));
               // write the number of records in the datafile.
               out.writeInt(myNumRecords);
               // write the length of the header structure.
               out.writeShort(myHeaderLength);
               // write the length of a record
               out.writeShort(myRecordLength);
               // write the reserved bytes in the header
               for (int i = 0; i < 20; i++)
                   out.writeByte(0);
               // write all of the header records
               int tempOffset = 0;
               for (int i = 0; i < myFieldDescriptions.length; i++) {
                   // write the field name
                   for (int j = 0; j < 11; j++) {
                       if (myFieldDescriptions[i].myFieldName.length() > j) {
                           out.writeByte((int) myFieldDescriptions[i].myFieldName.charAt(
                                   j));
                       } else {
                           out.writeByte(0);
                       }
                   }
                   // write the field type
                   out.writeByte(myFieldDescriptions[i].myFieldType);
                   // write the field data address, offset from the start of the record.
                   out.writeInt(tempOffset);
                   tempOffset += myFieldDescriptions[i].myFieldLength;
                   // write the length of the field.
                   out.writeByte(myFieldDescriptions[i].myFieldLength);
                   // write the decimal count.
                   out.writeByte(myFieldDescriptions[i].myDecimalCount);
                   // write the reserved bytes.
                   for (int j = 0; j < 14; j++)
                       out.writeByte(0);
               }
               // write the end of the field definitions marker
               out.writeByte(0x0D);
           }
     */

    /**
     * Class for holding the information assicated with a record.
     */
    class DbaseFieldDescriptor {
        // Field Name
        String myFieldName;

        // Field Type (C N L D F or M)
        char myFieldType;

        // Field Data Address offset from the start of the record.
        int myFieldDataAddress;

        // Length of the data in bytes
        int myFieldLength;

        // Field decimal count in Binary, indicating where the decimal is
        int myDecimalCount;
    }

	public byte getLanguageID() {
		return myLanguageID;
	}
}
