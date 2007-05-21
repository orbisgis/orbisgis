package org.gdms.spatial;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.gdms.data.AbstractDataSource;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.FreeingResourcesException;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MetadataEditionListener;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.persistence.Memento;
import org.gdms.data.persistence.MementoException;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.index.quadtree.Quadtree;

public class SpatialDataSourceDecorator extends AbstractDataSource implements
		SpatialDataSource {

	private DataSource dataSource;

	private Map<String, Quadtree> indices;

	private boolean recalculateExtent;

	private HashMap<FID, LongValue> fidRow = new HashMap<FID, LongValue>();

	private TreeSet<LongValue> rows = new TreeSet<LongValue>(
			new LongValueComparator());

	private ArrayList<FID> fids = new ArrayList<FID>();

	private Envelope editionFullExtent;

	private int spatialFieldIndex = -1;

	private int newFID;

	private Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	public SpatialDataSourceDecorator(DataSource dataSource)
			throws DriverException {
		this.dataSource = dataSource;
	}

	public void buildIndex() throws DriverException {
		Quadtree index = new Quadtree();
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			Geometry g = getGeometry(i);
			if (g != null) {
				index.insert(g.getEnvelopeInternal(), getFID(i));
			}
		}

		indices.put(getDataSourceMetadata()
				.getFieldName(getSpatialFieldIndex()), index);
	}

	public void clearIndex() {
		indices = null;
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

	@SuppressWarnings("unchecked")
	public List<FID> queryIndex(Envelope area) throws DriverException {
		return getDefaultIndex().query(area);
	}

	private Quadtree getDefaultIndex() throws DriverException {
		return indices.get(getDataSourceMetadata().getFieldName(
				getSpatialFieldIndex()));
	}

	public void addEditionListener(EditionListener listener) {
		dataSource.addEditionListener(listener);
	}

	public void addField(String name, String driverType) throws DriverException {
		dataSource.addField(name, driverType);
	}

	public void addField(String name, String driverType, String[] paramNames,
			String[] paramValues) throws DriverException {
		dataSource.addField(name, driverType);
	}

	public void addMetadataEditionListener(MetadataEditionListener listener) {
		dataSource.addMetadataEditionListener(listener);
	}

	public String check(int fieldId, Value value) throws DriverException {
		return dataSource.check(fieldId, value);
	}

	public void commitTrans() throws DriverException,
			FreeingResourcesException, NonEditableDataSourceException {
		dataSource.commitTrans();
		if (!dataSource.isOpen()) {
			clean();
		}
	}

	public void deleteRow(long rowIndex) throws DriverException {
		recalculateExtent = true;
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

		for (int i = 0; i < getDataSourceMetadata().getFieldCount(); i++) {
			if (getDataSourceMetadata().getFieldType(i) == PTTypes.GEOMETRY) {
				Value v = getFieldValue(rowIndex, i);
				if ((getIndexFor(i) != null) && (!(v instanceof NullValue))) {
					Geometry g = ((GeometryValue) v).getGeom();
					getIndexFor(i).remove(g.getEnvelopeInternal(), fid);
				}

			}
		}
		dataSource.deleteRow(rowIndex);
	}

	public Metadata getDataSourceMetadata() throws DriverException {
		return dataSource.getDataSourceMetadata();
	}

	public int getDispatchingMode() {
		return dataSource.getDispatchingMode();
	}

	public ReadOnlyDriver getDriver() {
		return dataSource.getDriver();
	}

	public DriverMetadata getDriverMetadata() throws DriverException {
		return dataSource.getDriverMetadata();
	}

	public int getFieldIndexByName(String fieldName) throws DriverException {
		return dataSource.getFieldIndexByName(fieldName);
	}

	public FID getFID(long row) {
		return fids.get((int) row);
	}

	public Envelope getFullExtent() throws DriverException {
		if (recalculateExtent) {
			editionFullExtent = null;
			for (int i = 0; i < dataSource.getRowCount(); i++) {
				Value v = dataSource.getFieldValue(i, getSpatialFieldIndex());
				if (!(v instanceof NullValue)) {
					Envelope r = ((GeometryValue) v).getGeom()
							.getEnvelopeInternal();
					if (editionFullExtent == null) {
						editionFullExtent = r;
					} else {
						editionFullExtent.expandToInclude(r.getMinX(), r
								.getMinY());
						editionFullExtent.expandToInclude(r.getMaxX(), r
								.getMaxY());
					}
				}
			}

			if (editionFullExtent == null) {
				return new Envelope(0, 0, 0, 0);
			}

			recalculateExtent = false;
		}

		return editionFullExtent;
	}

	public Geometry getGeometry(long rowIndex) throws DriverException {
		if (dataSource.isNull(rowIndex, getSpatialFieldIndex())) {
			return null;
		} else {
			return ((GeometryValue) dataSource.getFieldValue(rowIndex,
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
			Metadata m = getDataSourceMetadata();
			for (int i = 0; i < m.getFieldCount(); i++) {
				if (m.getFieldType(i) == PTTypes.GEOMETRY) {
					spatialFieldIndex = i;
					break;
				}
			}
		}

		return spatialFieldIndex;
	}

	public boolean isIndexed() throws DriverException {
		return getDefaultIndex() != null;
	}

	public long[] getWhereFilter() throws IOException {
		return dataSource.getWhereFilter();
	}

	private void updateFullExtent(Geometry g) throws DriverException {
		if (editionFullExtent == null) {
			recalculateExtent = true;
		}
		Envelope r = g.getEnvelopeInternal();
		Envelope newFullExtent = getFullExtent();
		newFullExtent.expandToInclude(r.getMinX() - 1, r.getMinY() - 1);
		newFullExtent.expandToInclude(r.getMaxX() + 1, r.getMaxY() + 1);
		editionFullExtent = newFullExtent;
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

	private void updateIndices(Value[] row, int rowPosition)
			throws DriverException {
		Metadata dataSourceMetadata = getDataSourceMetadata();
		for (int i = 0; i < dataSourceMetadata.getFieldCount(); i++) {
			if (dataSourceMetadata.getFieldType(i) == PTTypes.GEOMETRY) {
				Value newGeometry = row[i];
				if (newGeometry instanceof NullValue) {
					/*
					 * The index cannot hold null geometries
					 */
					return;
				} else {
					Geometry g = ((GeometryValue) newGeometry).getGeom();

					updateFullExtent(g);

					Quadtree qt = getIndexFor(i);
					if (qt != null) {
						qt.insert(g.getEnvelopeInternal(), getFID(rowPosition));
					}
				}

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
		dataSource.insertEmptyRow();
	}

	public void insertEmptyRowAt(long index) throws DriverException {
		int rowCount = (int) index;
		updateFIDMapping(rowCount);
		dataSource.insertEmptyRowAt(index);
	}

	public void insertFilledRow(Value[] values) throws DriverException {
		int rowCount = (int) getRowCount();
		updateFIDMapping(rowCount);
		updateIndices(values, rowCount);
		dataSource.insertFilledRow(values);
	}

	public void insertFilledRowAt(long index, Value[] values)
			throws DriverException {
		updateFIDMapping((int) index);
		updateIndices(values, (int) index);
		dataSource.insertFilledRowAt(index, values);
	}

	public boolean isModified() {
		return dataSource.isModified();
	}

	public boolean isOpen() {
		return dataSource.isOpen();
	}

	public void removeEditionListener(EditionListener listener) {
		dataSource.removeEditionListener(listener);
	}

	public void removeField(int index) throws DriverException {
		dataSource.removeField(index);
	}

	public void removeMetadataEditionListener(MetadataEditionListener listener) {
		dataSource.removeMetadataEditionListener(listener);
	}

	public void saveData(DataSource ds) throws DriverException {
		dataSource.saveData(ds);
	}

	public void setDispatchingMode(int dispatchingMode) {
		dataSource.setDispatchingMode(dispatchingMode);
	}

	public void setFieldName(int index, String name) throws DriverException {
		dataSource.setFieldName(index, name);
	}

	public void setFieldValue(long row, int fieldId, Value value)
			throws DriverException {
		if (getDataSourceMetadata().getFieldType(fieldId) == PTTypes.GEOMETRY) {
			Value oldGeometry = getFieldValue(row, fieldId);
			Value newGeometry = value;
			if (!(oldGeometry instanceof NullValue)) {
				Quadtree index = getIndexFor(fieldId);
				if (index != null) {
					Geometry g = ((GeometryValue) oldGeometry).getGeom();
					index.remove(g.getEnvelopeInternal(), getFID((int) row));
				}
			}

			if (!(newGeometry instanceof NullValue)) {
				recalculateExtent = true;

				Quadtree index = getIndexFor(fieldId);
				if (index != null) {
					Geometry g = ((GeometryValue) newGeometry).getGeom();
					index.insert(g.getEnvelopeInternal(), getFID((int) row));
				}
			}
		}
		dataSource.setFieldValue(row, fieldId, value);
	}

	private Quadtree getIndexFor(int fieldId) throws DriverException {
		return indices.get(getDataSourceMetadata().getFieldName(fieldId));
	}

	public void beginTrans() throws DriverException {
		dataSource.beginTrans();

		initialize();
	}

	private void initialize() throws DriverException {
		fidRow.clear();
		rows.clear();
		fids.clear();
		for (int i = 0; i < getRowCount(); i++) {
			FID fid = new IntFID(i);
			LongValue lv = ValueFactory.createValue((long) i);
			fidRow.put(fid, lv);
			rows.add(lv);
			fids.add(fid);
		}
		newFID = (int) getRowCount();

		ReadOnlyDriver driver = getDriver();
		if (driver != null) {
			Number[] xScope = getScope(ReadOnlyDriver.X,
					getFieldNames()[getSpatialFieldIndex()]);
			Number[] yScope = getScope(ReadOnlyDriver.Y,
					getFieldNames()[getSpatialFieldIndex()]);

			if ((xScope != null) && (yScope != null)) {
				editionFullExtent = new Envelope(new Coordinate(xScope[0]
						.doubleValue(), yScope[0].doubleValue()),
						new Coordinate(xScope[1].doubleValue(), yScope[1]
								.doubleValue()));
			} else {
				recalculateExtent = true;
			}
		} else {
			recalculateExtent = true;
		}

		indices = new HashMap<String, Quadtree>();
	}

	public void rollBackTrans() throws DriverException, AlreadyClosedException {
		dataSource.rollBackTrans();
		if (!dataSource.isOpen()) {
			clean();
		}
	}

	private void clean() {
		fidRow.clear();
		rows.clear();
		fids.clear();
		if (indices != null) {
			indices = null;
		}
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		return dataSource.getFieldValue(rowIndex, fieldId);
	}

	public long getRowCount() throws DriverException {
		return dataSource.getRowCount();
	}

	private class LongValueComparator implements Comparator<LongValue> {
		public int compare(LongValue o1, LongValue o2) {
			return (int) (o2.getValue() - o1.getValue());
		}
	}

	public void buildIndex(String fieldName) throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		buildIndex();
		setDefaultGeometry(defaultGeometry);
	}

	public void clearIndex(String spatialField) throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(spatialField);
		buildIndex();
		setDefaultGeometry(defaultGeometry);
	}

	public String getDefaultGeometry() throws DriverException {
		return getDataSourceMetadata().getFieldName(getSpatialFieldIndex());
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

	public boolean isIndexed(String fieldName) throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		boolean ret = isIndexed();
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	public List<FID> queryIndex(String fieldName, Envelope fullExtent)
			throws DriverException {
		String defaultGeometry = getDefaultGeometry();
		setDefaultGeometry(fieldName);
		List<FID> ret = queryIndex(fullExtent);
		setDefaultGeometry(defaultGeometry);

		return ret;
	}

	public void setDefaultGeometry(String fieldName) throws DriverException {
		spatialFieldIndex = getFieldIndexByName(fieldName);
	}

	public boolean canRedo() {
		return dataSource.canRedo();
	}

	public boolean canUndo() {
		return dataSource.canUndo();
	}

	public void redo() throws DriverException {
		dataSource.redo();
	}

	public void undo() throws DriverException {
		dataSource.undo();
	}

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		return dataSource.getScope(dimension, fieldName);
	}

	public String getAlias() {
		return dataSource.getAlias();
	}

	public DataSourceFactory getDataSourceFactory() {
		return dataSource.getDataSourceFactory();
	}

	public Memento getMemento() throws MementoException {
		return dataSource.getMemento();
	}

	public String getName() {
		return dataSource.getName();
	}

	public boolean isEditable() {
		return dataSource.isEditable();
	}

	public void setDataSourceFactory(DataSourceFactory dsf) {
		dataSource.setDataSourceFactory(dsf);
	}

	public CoordinateReferenceSystem getCRS(final String fieldName)
			throws DriverException {
		if (crsMap.containsKey(fieldName)) {
			return crsMap.get(fieldName);
		} else {
			// delegate to the driver layer
			final CoordinateReferenceSystem driverCrs = dataSource.getDriver()
					.getCRS(fieldName);
			if (null == driverCrs) {
				// TODO ??? setCRS(NullCRS.singleton, fieldName);
				return NullCRS.singleton;
			} else {
				setCRS(driverCrs, fieldName);
				return driverCrs;
			}
		}
	}

	public void setCRS(final CoordinateReferenceSystem crs,
			final String fieldName) {
		crsMap.put(fieldName, crs);
	}
}