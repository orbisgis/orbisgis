package org.gdms;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverUtilities;

import com.vividsolutions.jts.geom.Geometry;

public class TestData {

	public static final int HSQLDB = 1;

	public static final int CSV = 2;

	public static final int DBF = 4;

	public static final int SHAPEFILE = 8;

	public static final int NONE = 0;

	public static final int H2 = 16;

	protected String name;

	private long rowCount;

	private boolean isDB;

	private String noPKField;

	private boolean hasRepeatedRows;

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
			boolean isDB, String noPKField, boolean hasRepeatedRows) {
		super();
		this.name = name;
		this.write = write;
		this.driver = driver;
		this.rowCount = rowCount;
		this.isDB = isDB;
		this.noPKField = noPKField;
		this.hasRepeatedRows = hasRepeatedRows;
	}

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

	public int getDriver() {
		return driver;
	}

	public boolean isWrite() {
		return write;
	}

	public void copyGroup(final File prefix, File dir) throws IOException {
		File[] dbFiles = prefix.getParentFile().listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.getName().startsWith(prefix.getName());
			}
		});

		for (int i = 0; i < dbFiles.length; i++) {
			DriverUtilities.copy(dbFiles[i],
					new File(dir, dbFiles[i].getName()));
		}
	}

}
