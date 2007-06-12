package org.gdms;

import java.io.File;

import org.gdms.data.DataSourceDefinition;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;

import com.vividsolutions.jts.geom.Geometry;

public abstract class TestData {

	public static final int HSQLDB = 1;

	public static final int CSV = 2;

	public static final int DBF = 4;

	public static final int SHAPEFILE = 8;

	public static final int NONE = 0;

	protected String name;

	private long rowCount;

	private boolean isDB;

	private String noPKField;

	private boolean hasRepeatedRows;

	private DataSourceDefinition definition;

	/* Optional attributes */
	private PKInfo info;

	private String stringField;

	private String nullField;

	private NumericInfo numericInfo;

	private String spatialField;

	private Geometry[] newGeometry;

	private boolean write;

	private int driver;

	public TestData(String name, boolean write, int driver, long rowCount,
			boolean isDB, String noPKField, boolean hasRepeatedRows,
			DataSourceDefinition def) {
		super();
		this.name = name;
		this.write = write;
		this.driver = driver;
		this.rowCount = rowCount;
		this.isDB = isDB;
		this.noPKField = noPKField;
		this.hasRepeatedRows = hasRepeatedRows;
		this.definition = def;
	}

	/**
	 * Creates a backup source of this test data, registers it in the specified
	 * DataSourceFactory and returns the name of the backup. Any file backup
	 * should be done in backupDir
	 * 
	 * @param backupDir
	 * @param dsf
	 * @return
	 * @throws Exception
	 */
	public abstract String backup(File backupDir, DataSourceFactory dsf)
			throws Exception;

	public class NumericInfo {
		private String numericFieldName;

		private double min;

		private double max;

		public NumericInfo(String numericFieldName, double min, double max) {
			super();
			this.numericFieldName = numericFieldName;
			this.min = min;
			this.max = max;
		}

		public double getMax() {
			return max;
		}

		public double getMin() {
			return min;
		}

		public String getNumericFieldName() {
			return numericFieldName;
		}
	}

	public class PKInfo {
		private String pkField;

		private Value newPK;

		public PKInfo(String pkField, Value newPK) {
			super();
			this.pkField = pkField;
			this.newPK = newPK;
		}

		public Value getNewPK() {
			return newPK;
		}

		public String getPkField() {
			return pkField;
		}

	}

	public boolean hasRepeatedRows() {
		return hasRepeatedRows;
	}

	public boolean isDB() {
		return isDB;
	}

	public String getNoPKField() {
		return noPKField;
	}

	public long getRowCount() {
		return rowCount;
	}

	public PKInfo getPKInfo() {
		return info;
	}

	public void setPKInfo(String pkField, Value newPK) {
		this.info = new PKInfo(pkField, newPK);
	}

	public Geometry[] getNewGeometry() {
		return newGeometry;
	}

	public void setNewGeometry(String spatialFieldName, Geometry[] newGeometry) {
		this.newGeometry = newGeometry;
		this.spatialField = spatialFieldName;
	}

	public String getSpatialField() {
		return spatialField;
	}

	public String getNullField() {
		return nullField;
	}

	public void setNullField(String nullField) {
		this.nullField = nullField;
	}

	public NumericInfo getNumericInfo() {
		return numericInfo;
	}

	public void setNumericInfo(String fieldName, double min, double max) {
		this.numericInfo = new NumericInfo(fieldName, min, max);
	}

	public String getStringField() {
		return stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public String getName() {
		return name;
	}

	public DataSourceDefinition getDefinition() {
		return definition;
	}

	public int getDriver() {
		return driver;
	}

	public boolean isWrite() {
		return write;
	}
}
