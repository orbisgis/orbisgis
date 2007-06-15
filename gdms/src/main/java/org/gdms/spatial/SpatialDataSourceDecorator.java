package org.gdms.spatial;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.CRSConstraint;
import org.gdms.data.types.ConstraintNames;
import org.gdms.data.types.Type;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialDataSourceDecorator extends AbstractDataSourceDecorator
		implements SpatialDataSource {

	private HashMap<FID, LongValue> fidRow = new HashMap<FID, LongValue>();

	private Set<LongValue> rows = new TreeSet<LongValue>(
			new LongValueComparator());

	private List<FID> fids = new ArrayList<FID>();

	private int spatialFieldIndex = -1;

	private int newFID;

	private Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	public SpatialDataSourceDecorator(DataSource dataSource)
			throws DriverException {
		super(dataSource);
	}

	public int getInt(FID fid, String fieldName) throws DriverException {
		return getInt(getRow(fid), fieldName);
	}

	public int getInt(FID fid, int fieldId) throws DriverException {
		return getInt(getRow(fid), fieldId);
	}

	public byte[] getBinary(FID fid, String fieldName) throws DriverException {
		return getBinary(getRow(fid), fieldName);
	}

	public byte[] getBinary(FID fid, int fieldId) throws DriverException {
		return getBinary(getRow(fid), fieldId);
	}

	public boolean getBoolean(FID fid, String fieldName) throws DriverException {
		return getBoolean(getRow(fid), fieldName);
	}

	public boolean getBoolean(FID fid, int fieldId) throws DriverException {
		return getBoolean(getRow(fid), fieldId);
	}

	public byte getByte(FID fid, String fieldName) throws DriverException {
		return getByte(getRow(fid), fieldName);
	}

	public byte getByte(FID fid, int fieldId) throws DriverException {
		return getByte(getRow(fid), fieldId);
	}

	public Date getDate(FID fid, String fieldName) throws DriverException {
		return getDate(getRow(fid), fieldName);
	}

	public Date getDate(FID fid, int fieldId) throws DriverException {
		return getDate(getRow(fid), fieldId);
	}

	public double getDouble(FID fid, String fieldName) throws DriverException {
		return getDouble(getRow(fid), fieldName);
	}

	public double getDouble(FID fid, int fieldId) throws DriverException {
		return getDouble(getRow(fid), fieldId);
	}

	public float getFloat(FID fid, String fieldName) throws DriverException {
		return getFloat(getRow(fid), fieldName);
	}

	public float getFloat(FID fid, int fieldId) throws DriverException {
		return getFloat(getRow(fid), fieldId);
	}

	public long getLong(FID fid, String fieldName) throws DriverException {
		return getLong(getRow(fid), fieldName);
	}

	public long getLong(FID fid, int fieldId) throws DriverException {
		return getLong(getRow(fid), fieldId);
	}

	public short getShort(FID fid, String fieldName) throws DriverException {
		return getShort(getRow(fid), fieldName);
	}

	public short getShort(FID fid, int fieldId) throws DriverException {
		return getShort(getRow(fid), fieldId);
	}

	public String getString(FID fid, String fieldName) throws DriverException {
		return getString(getRow(fid), fieldName);
	}

	public String getString(FID fid, int fieldId) throws DriverException {
		return getString(getRow(fid), fieldId);
	}

	public Timestamp getTimestamp(FID fid, String fieldName)
			throws DriverException {
		return getTimestamp(getRow(fid), fieldName);
	}

	public Timestamp getTimestamp(FID fid, int fieldId) throws DriverException {
		return getTimestamp(getRow(fid), fieldId);
	}

	public Time getTime(FID fid, String fieldName) throws DriverException {
		return getTime(getRow(fid), fieldName);
	}

	public Time getTime(FID fid, int fieldId) throws DriverException {
		return getTime(getRow(fid), fieldId);
	}

	public void setInt(FID fid, String fieldName, int value)
			throws DriverException {
		setInt(getRow(fid), fieldName, value);
	}

	public void setInt(FID fid, int fieldId, int value) throws DriverException {
		setInt(getRow(fid), fieldId, value);
	}

	public void setBinary(FID fid, String fieldName, byte[] value)
			throws DriverException {
		setBinary(getRow(fid), fieldName, value);
	}

	public void setBinary(FID fid, int fieldId, byte[] value)
			throws DriverException {
		setBinary(getRow(fid), fieldId, value);
	}

	public void setBoolean(FID fid, String fieldName, boolean value)
			throws DriverException {
		setBoolean(getRow(fid), fieldName, value);
	}

	public void setBoolean(FID fid, int fieldId, boolean value)
			throws DriverException {
		setBoolean(getRow(fid), fieldId, value);
	}

	public void setByte(FID fid, String fieldName, byte value)
			throws DriverException {
		setByte(getRow(fid), fieldName, value);
	}

	public void setByte(FID fid, int fieldId, byte value)
			throws DriverException {
		setByte(getRow(fid), fieldId, value);
	}

	public void setDate(FID fid, String fieldName, Date value)
			throws DriverException {
		setDate(getRow(fid), fieldName, value);
	}

	public void setDate(FID fid, int fieldId, Date value)
			throws DriverException {
		setDate(getRow(fid), fieldId, value);
	}

	public void setDouble(FID fid, String fieldName, double value)
			throws DriverException {
		setDouble(getRow(fid), fieldName, value);
	}

	public void setDouble(FID fid, int fieldId, double value)
			throws DriverException {
		setDouble(getRow(fid), fieldId, value);
	}

	public void setFloat(FID fid, String fieldName, float value)
			throws DriverException {
		setFloat(getRow(fid), fieldName, value);
	}

	public void setFloat(FID fid, int fieldId, float value)
			throws DriverException {
		setFloat(getRow(fid), fieldId, value);
	}

	public void setLong(FID fid, String fieldName, long value)
			throws DriverException {
		setLong(getRow(fid), fieldName, value);
	}

	public void setLong(FID fid, int fieldId, long value)
			throws DriverException {
		setLong(getRow(fid), fieldId, value);
	}

	public void setShort(FID fid, String fieldName, short value)
			throws DriverException {
		setInt(getRow(fid), fieldName, value);
	}

	public void setShort(FID fid, int fieldId, short value)
			throws DriverException {
		setShort(getRow(fid), fieldId, value);
	}

	public void setString(FID fid, String fieldName, String value)
			throws DriverException {
		setString(getRow(fid), fieldName, value);
	}

	public void setString(FID fid, int fieldId, String value)
			throws DriverException {
		setString(getRow(fid), fieldId, value);
	}

	public void setTimestamp(FID fid, String fieldName, Timestamp value)
			throws DriverException {
		setTimestamp(getRow(fid), fieldName, value);
	}

	public void setTimestamp(FID fid, int fieldId, Timestamp value)
			throws DriverException {
		setTimestamp(getRow(fid), fieldId, value);
	}

	public void setTime(FID fid, String fieldName, Time value)
			throws DriverException {
		setTime(getRow(fid), fieldName, value);
	}

	public void setTime(FID fid, int fieldId, Time value)
			throws DriverException {
		setTime(getRow(fid), fieldId, value);
	}

	public boolean isNull(FID fid, int fieldId) throws DriverException {
		return isNull(getRow(fid), fieldId);
	}

	public boolean isNull(FID fid, String fieldName) throws DriverException {
		return isNull(getRow(fid), fieldName);
	}

	public Value getFieldValue(FID fid, int fieldId) throws DriverException {
		return getFieldValue(getRow(fid), fieldId);
	}

	public void setFieldValue(FID fid, int fieldId, Value value)
			throws DriverException {
		setFieldValue(getRow(fid), fieldId, value);
	}

	public void commit() throws DriverException, FreeingResourcesException,
			NonEditableDataSourceException {
		getDataSource().commit();
		if (!getDataSource().isOpen()) {
			clean();
		}
	}

	public void deleteRow(long rowIndex) throws DriverException {
		FID fid = fids.remove((int) rowIndex);
		fidRow.remove(fid);
		Iterator<LongValue> it = rows.iterator();
		while (it.hasNext()) {
			LongValue lv = it.next();
			if (lv.getValue() >= rowIndex) {
				lv.setValue(lv.getValue() - 1);
			} else {
				break;
			}
		}

		getDataSource().deleteRow(rowIndex);
	}

	public FID getFID(long row) {
		return fids.get((int) row);
	}

	public Envelope getFullExtent() throws DriverException {
		Number[] xScope = getScope(ReadOnlyDriver.X);
		Number[] yScope = getScope(ReadOnlyDriver.Y);
		if ((xScope != null) && (yScope != null)) {
			return new Envelope(new Coordinate(xScope[0]
					.doubleValue(), yScope[0].doubleValue()),
					new Coordinate(xScope[1].doubleValue(), yScope[1]
							.doubleValue()));
		} else {
			return null;
		}
	}

	public Geometry getGeometry(long rowIndex) throws DriverException {
		if (getDataSource().isNull(rowIndex, getSpatialFieldIndex())) {
			return null;
		} else {
			return ((GeometryValue) getDataSource().getFieldValue(rowIndex,
					getSpatialFieldIndex())).getGeom();
		}
	}

	public Geometry getGeometry(FID fid) throws DriverException {
		return getGeometry(getRow(fid));
	}

	public long getRow(FID fid) {
		return fidRow.get(fid).getValue();
	}

	public int getSpatialFieldIndex() throws DriverException {
		if (spatialFieldIndex == -1) {
			Metadata m = getMetadata();
			for (int i = 0; i < m.getFieldCount(); i++) {
				if (m.getFieldType(i).getTypeCode() == Type.GEOMETRY) {
					spatialFieldIndex = i;
					break;
				}
			}
		}

		return spatialFieldIndex;
	}

	private void updateFIDMapping(int rowPosition) {
		FID fid = getNewUniqueTemporalFID();
		fids.add(rowPosition, fid);
		LongValue lv = ValueFactory.createValue((long) rowPosition);
		fidRow.put(fid, lv);
		Iterator<LongValue> it = rows.iterator();
		while (it.hasNext()) {
			lv = it.next();
			if (lv.getValue() >= rowPosition) {
				lv.setValue(lv.getValue() + 1);
			} else {
				break;
			}
		}
	}

	private FID getNewUniqueTemporalFID() {
		IntFID ret = new IntFID(newFID);
		newFID++;

		return ret;
	}

	public void insertEmptyRow() throws DriverException {
		int rowCount = (int) getRowCount();
		updateFIDMapping(rowCount);
		getDataSource().insertEmptyRow();
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		int rowCount = (int) index;
		updateFIDMapping(rowCount);
		getDataSource().insertEmptyRowAt(index);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		int rowCount = (int) getRowCount();
		updateFIDMapping(rowCount);
		getDataSource().insertFilledRow(values);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		updateFIDMapping((int) index);
		getDataSource().insertFilledRowAt(index, values);
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		getDataSource().setFieldValue(row, fieldId, value);
	}

	public void open() throws DriverException {
		getDataSource().open();

		initialize();
	}

	private void initialize() throws DriverException {
		fidRow.clear();
		rows.clear();
		fids.clear();

		ReadOnlyDriver driver = getDriver();

		if ((null != driver) && driver.hasFid()) {
			for (int row = 0; row < getRowCount(); row++) {
				FID fid = driver.getFid(row);
				LongValue lv = ValueFactory.createValue((long) row);
				fidRow.put(fid, lv);
				rows.add(lv);
				fids.add(fid);
			}
		} else {
			for (int row = 0; row < getRowCount(); row++) {
				FID fid = new IntFID(row);
				LongValue lv = ValueFactory.createValue((long) row);
				fidRow.put(fid, lv);
				rows.add(lv);
				fids.add(fid);
			}
		}
		newFID = (int) getRowCount();
	}

	public void cancel() throws DriverException, AlreadyClosedException {
		getDataSource().cancel();
		if (!getDataSource().isOpen()) {
			clean();
		}
	}

	private void clean() {
		fidRow.clear();
		rows.clear();
		fids.clear();
	}

	private class LongValueComparator implements Comparator<LongValue> {
		public int compare(LongValue o1, LongValue o2) {
			return (int) (o2.getValue() - o1.getValue());
		}
	}

	public String getDefaultGeometry() throws DriverException {
		return getMetadata().getFieldName(getSpatialFieldIndex());
	}

	public Geometry getGeometry(String fieldName, long rowIndex)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		Geometry ret = getGeometry(rowIndex);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	public Geometry getGeometry(String fieldName, FID featureId)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		Geometry ret = getGeometry(featureId);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	public void setDefaultGeometry(String fieldName) throws DriverException {
		spatialFieldIndex = getFieldIndexByName(fieldName);
	}

	public CoordinateReferenceSystem getCRS(String fieldName)
			throws DriverException {
		if (fieldName == null) {
			fieldName = getFieldName(getSpatialFieldIndex());
		}
		if (crsMap.containsKey(fieldName)) {
			return crsMap.get(fieldName);
		} else {
			// delegate to the driver layer
			CRSConstraint crsConstraint = (CRSConstraint) getMetadata()
					.getFieldType(getFieldIndexByName(fieldName))
					.getConstraint(ConstraintNames.CRS);
			if (null == crsConstraint) {
				// TODO ??? setCRS(NullCRS.singleton, fieldName);
				return NullCRS.singleton;
			} else {
				setCRS(crsConstraint.getCRS(), fieldName);
				return crsConstraint.getCRS();
			}
		}
	}

	public void setCRS(final CoordinateReferenceSystem crs,
			final String fieldName) {
		crsMap.put(fieldName, crs);
	}

	public void setGeometry(long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
	}

	public void setGeometry(FID fid, Geometry geom) throws DriverException {
		setFieldValue(fid, getSpatialFieldIndex(), ValueFactory
				.createValue(geom));
	}

	public void setGeometry(String fieldName, long rowIndex, Geometry geom)
			throws DriverException {
		setFieldValue(rowIndex, getFieldIndexByName(fieldName), ValueFactory
				.createValue(geom));
	}

	public void setGeometry(String fieldName, FID featureId, Geometry geom)
			throws DriverException {
		setFieldValue(featureId, getFieldIndexByName(fieldName), ValueFactory
				.createValue(geom));
	}
}