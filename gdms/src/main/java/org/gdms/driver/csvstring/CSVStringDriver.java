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

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.edition.Field;
import org.gdms.data.metadata.DefaultDriverMetadata;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.DriverException;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.FileDriver;
import org.gdms.spatial.FID;
import org.gdms.spatial.GeometryValue;

import com.hardcode.driverManager.Driver;


/**
 * Driver para ficheros csv, en el que la primera fila se toma como la que
 * define los nombres de los campos
 *
 * @author Fernando Gonzalez Cortes
 */
public class CSVStringDriver implements Driver, FileDriver, ValueWriter {
    private BufferedReader reader;
    private ArrayList<String[]> lineas;
    private ValueWriter vWriter = ValueWriter.internalValueWriter;

    /**
     * @see org.gdms.driver.Driver#getName()
     */
    public String getName() {
        return "csv string";
    }

    /**
     * @see org.gdbms.data.DataSource#getFieldName(int)
     */
    private String getFieldName(int fieldId) throws DriverException {
        String[] campos = (String[]) lineas.get(0);

        return campos[fieldId];
    }

    /**
     * @see org.gdbms.data.DataSource#getIntFieldValue(int, int)
     */
    public Value getFieldValue(long rowIndex, int fieldId)
        throws DriverException {
        String[] campos = (String[]) lineas.get((int) (rowIndex + 1));
        if (fieldId < campos.length) {
            if (campos[fieldId].equals("null")) {
                return null;
            }
        } else {
            return ValueFactory.createNullValue();
        }

        Value value = ValueFactory.createValue(campos[fieldId]);

        return value;
    }

    /**
     * @see org.gdbms.data.DataSource#getFieldCount()
     */
    private int getFieldCount() throws DriverException {
        String[] campos = (String[]) lineas.get(0);

        return campos.length;
    }

    /**
     * @see org.gdbms.data.DataSource#open(java.io.File)
     */
    public void open(File file) throws DriverException {
        try {
            reader = new BufferedReader(new FileReader(file));

            lineas = new ArrayList<String[]>();

            String aux;

            while ((aux = reader.readLine()) != null) {
                String[] campos = aux.split(";");
                lineas.add(campos);
            }
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdbms.data.DataSource#close(Connection)
     */
    public void close() throws DriverException {
        try {
            reader.close();
        } catch (IOException e) {
            throw new DriverException(e);
        }
    }

    /**
     * @see org.gdbms.data.DataSource#getRowCount()
     */
    public long getRowCount() {
        return lineas.size() - 1;
    }

    /**
     * @see org.gdms.data.driver.AlphanumericFileDriver#fileAccepted(java.io.File)
     */
    public boolean fileAccepted(File f) {
        return f.getAbsolutePath().toUpperCase().endsWith("CSV");
    }

    /**
     * @see org.gdms.data.driver.AlphanumericFileDriver#writeFile(org.gdms.data.edition.DataWare,
     *      java.io.File)
     */
    public void writeFile(File file, DataSource dataWare)
        throws DriverException {
        PrintWriter out;

        try {
            out = new PrintWriter(new FileOutputStream(file));

            Metadata metadata = dataWare.getDataSourceMetadata();
            String fieldRow = metadata.getFieldName(0);

            for (int i = 1; i < metadata.getFieldCount(); i++) {
                fieldRow += (";" + metadata.getFieldName(i));
            }

            out.println(fieldRow);

            for (int i = 0; i < dataWare.getRowCount(); i++) {
                String row = dataWare.getFieldValue(i, 0).getStringValue(this);

                for (int j = 1; j < metadata.getFieldCount(); j++) {
                    row += (";" +
                    dataWare.getFieldValue(i, j).getStringValue(this));
                }

                out.println(row);
            }

            out.close();
        } catch (FileNotFoundException e) {
            throw new DriverException(e);
        }
    }

    public void createSource(String path, DriverMetadata dsm) throws DriverException {
        try {
            File file = new File(path);

            file.getParentFile().mkdirs();
            file.createNewFile();

            PrintWriter out = new PrintWriter(new FileOutputStream(file));

            String header = dsm.getFieldName(0);
            for (int i = 1; i < dsm.getFieldCount(); i++) {
                header += ";" + dsm.getFieldName(i);
            }
            out.println(header);

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
        return vWriter.getNullStatementString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(boolean b) {
        return vWriter.getStatementString(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param binary DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(byte[] binary) {
        return vWriter.getStatementString(binary);
    }

    /**
     * DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Date d) {
        return vWriter.getStatementString(d);
    }

    /**
     * DOCUMENT ME!
     *
     * @param d DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(double d, int sqlType) {
        return vWriter.getStatementString(d, sqlType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(int i, int sqlType) {
        return vWriter.getStatementString(i, sqlType);
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(long i) {
        return vWriter.getStatementString(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @param str DOCUMENT ME!
     * @param sqlType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(String str, int sqlType) {
        return str;
    }

    /**
     * DOCUMENT ME!
     *
     * @param t DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Time t) {
        return t.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ts DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStatementString(Timestamp ts) {
        return ts.toString();
    }

    public void setDataSourceFactory(DataSourceFactory dsf) {
    }

    public Metadata getMetadata() throws DriverException {
        String[] fieldNames = lineas.get(0);
        int[] fieldTypes = new int[fieldNames.length];
        for (int i = 0; i < fieldTypes.length; i++) {
            fieldTypes[i] = Value.STRING;
        }

        return new DefaultMetadata(fieldTypes, fieldNames,
                null, null);
    }

    public String getStatementString(GeometryValue g) {
        return vWriter.getStatementString(g);
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
     * @see org.gdms.driver.ReadOnlyDriver#getDriverMetadata()
     */
    public DriverMetadata getDriverMetadata() throws DriverException {
        DefaultDriverMetadata ret = new DefaultDriverMetadata();
        for (int i = 0; i < getFieldCount(); i++) {
            ret.addField(getFieldName(i), "STRING");
        }

        return ret;
    }

    /**
     * @see org.gdms.driver.ReadOnlyDriver#getType(java.lang.String)
     */
    public int getType(String driverType) {
        return Value.STRING;
    }

    public String[] getAvailableTypes() throws DriverException {
        return new String[]{"STRING"};
    }

    public String[] getParameters(String driverType) throws DriverException {
        return new String[0];
    }

    public String check(Field field, Value value) throws DriverException {
        return null;
    }

    public boolean isReadOnly(int i) {
        return false;
    }

    public boolean isValidParameter(String driverType, String paramName, String paramValue) {
        return false;
    }

	public Number[] getScope(int dimension, String fieldName) throws DriverException {
		return null;
	}

	public FID getFid(long row) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasFid() {
		// TODO Auto-generated method stub
		return false;
	}

}
