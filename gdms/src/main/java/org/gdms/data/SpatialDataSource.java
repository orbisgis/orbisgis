package org.gdms.data;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.FID;

import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;

/**
 * DataSource with spatial capabilities
 */
public interface SpatialDataSource extends DataSource {

	int POINT = 0;

	int MULTIPOINT = 1;

	int LINESTRING = 2;

	int MULTILINESTRING = 3;

	int POLYGON = 4;

	int MULTIPOLYGON = 5;

	int ANY = 6;

	/**
	 * Gets the full extent of the data accessed
	 *
	 * @return Rectangle2D
	 *
	 * @throws DriverException
	 *             if the operation fails
	 */
	public Envelope getFullExtent() throws DriverException;

	/**
	 * Builds a spatial index on the default geometry.
	 *
	 * @throws DriverException
	 *             If the build fails
	 */
	public void buildIndex() throws DriverException;

	/**
	 * Clears the spatial index of the default geometry
	 *
	 * @throws DriverException
	 */
	public void clearIndex();

	/**
	 * Clears the spatial index on the specified spatial field
	 *
	 * @param spatialField
	 * @throws DriverException
	 */
	public void clearIndex(String spatialField) throws DriverException;

	/**
	 * Queries the index of the default spatial field
	 *
	 * @param query
	 *            argument
	 *
	 * @return
	 * @throws DriverException
	 */
	public List<FID> queryIndex(Envelope area) throws DriverException;

	/**
	 * @return true if the default spatial field in this data source is
	 *         spatially indexed and false otherwise
	 * @throws DriverException
	 */
	public boolean isIndexed() throws DriverException;

	/**
	 * Returns the index of the field containing spatial data
	 *
	 * @return
	 * @throws DriverException
	 */
	public int getSpatialFieldIndex() throws DriverException;

	/**
	 * Gets the default geometry of the DataSource as a JTS geometry or null
	 * if the row doesn't have a geometry value
	 *
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public com.vividsolutions.jts.geom.Geometry getGeometry(long rowIndex)
			throws DriverException;

	/**
	 * Gets the default geometry of the DataSource as a JTS geometry.
	 *
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public com.vividsolutions.jts.geom.Geometry getGeometry(FID fid)
			throws DriverException;

	public int getInt(FID fid, String fieldName) throws DriverException;

	public int getInt(FID fid, int fieldId) throws DriverException;

	public byte[] getBinary(FID fid, String fieldName) throws DriverException;

	public byte[] getBinary(FID fid, int fieldId) throws DriverException;

	public boolean getBoolean(FID fid, String fieldName) throws DriverException;

	public boolean getBoolean(FID fid, int fieldId) throws DriverException;

	public byte getByte(FID fid, String fieldName) throws DriverException;

	public byte getByte(FID fid, int fieldId) throws DriverException;

	public Date getDate(FID fid, String fieldName) throws DriverException;

	public Date getDate(FID fid, int fieldId) throws DriverException;

	public double getDouble(FID fid, String fieldName) throws DriverException;

	public double getDouble(FID fid, int fieldId) throws DriverException;

	public float getFloat(FID fid, String fieldName) throws DriverException;

	public float getFloat(FID fid, int fieldId) throws DriverException;

	public long getLong(FID fid, String fieldName) throws DriverException;

	public long getLong(FID fid, int fieldId) throws DriverException;

	public short getShort(FID fid, String fieldName) throws DriverException;

	public short getShort(FID fid, int fieldId) throws DriverException;

	public String getString(FID fid, String fieldName) throws DriverException;

	public String getString(FID fid, int fieldId) throws DriverException;

	public Timestamp getTimestamp(FID fid, String fieldName)
			throws DriverException;

	public Timestamp getTimestamp(FID fid, int fieldId) throws DriverException;

	public Time getTime(FID fid, String fieldName) throws DriverException;

	public Time getTime(FID fid, int fieldId) throws DriverException;

	public void setInt(FID fid, String fieldName, int value)
			throws DriverException;

	public void setInt(FID fid, int fieldId, int value) throws DriverException;

	public void setBinary(FID fid, String fieldName, byte[] value)
			throws DriverException;

	public void setBinary(FID fid, int fieldId, byte[] value)
			throws DriverException;

	public void setBoolean(FID fid, String fieldName, boolean value)
			throws DriverException;

	public void setBoolean(FID fid, int fieldId, boolean value)
			throws DriverException;

	public void setByte(FID fid, String fieldName, byte value)
			throws DriverException;

	public void setByte(FID fid, int fieldId, byte value)
			throws DriverException;

	public void setDate(FID fid, String fieldName, Date value)
			throws DriverException;

	public void setDate(FID fid, int fieldId, Date value)
			throws DriverException;

	public void setDouble(FID fid, String fieldName, double value)
			throws DriverException;

	public void setDouble(FID fid, int fieldId, double value)
			throws DriverException;

	public void setFloat(FID fid, String fieldName, float value)
			throws DriverException;

	public void setFloat(FID fid, int fieldId, float value)
			throws DriverException;

	public void setLong(FID fid, String fieldName, long value)
			throws DriverException;

	public void setLong(FID fid, int fieldId, long value)
			throws DriverException;

	public void setShort(FID fid, String fieldName, short value)
			throws DriverException;

	public void setShort(FID fid, int fieldId, short value)
			throws DriverException;

	public void setString(FID fid, String fieldName, String value)
			throws DriverException;

	public void setString(FID fid, int fieldId, String value)
			throws DriverException;

	public void setTimestamp(FID fid, String fieldName, Timestamp value)
			throws DriverException;

	public void setTimestamp(FID fid, int fieldId, Timestamp value)
			throws DriverException;

	public void setTime(FID fid, String fieldName, Time value)
			throws DriverException;

	public void setTime(FID fid, int fieldId, Time value)
			throws DriverException;

	public boolean isNull(FID fid, int fieldId) throws DriverException;

	public boolean isNull(FID fid, String fieldName) throws DriverException;

	/**
	 * Gets the row in the DataSource where the feature with fid is stored
	 *
	 * @param fid
	 *            feature id
	 *
	 * @return the row where the featureId is or -1 if there isn't any feature
	 *         with such a fid
	 */
	public long getRow(FID fid);

	/**
	 * Gets values by its feature id
	 *
	 * @param fid
	 * @param i
	 * @return
	 * @throws DriverException
	 */
	public Value getFieldValue(FID fid, int i) throws DriverException;

	/**
	 * Sets values by its feature id
	 *
	 * @param fid
	 * @param i
	 * @return
	 * @throws DriverException
	 */
	public void setFieldValue(FID fid, int i, Value value)
			throws DriverException;

	/**
	 * Gets the feature id of the rowth row of the DataSource
	 *
	 * @param row
	 * @return
	 */
	public FID getFID(long row);

	/**
	 * Set the field name for the getGeometry(int) method. If this method
	 * is not called, the default geometry is the first spatial field
	 *
	 * @param fieldName
	 * @throws DriverException
	 */
	public void setDefaultGeometry(String fieldName) throws DriverException;

	/**
	 * Returns the name of the field which is the default geometry. If the data
	 * source contains only one spatial field, the default geometry is that
	 * field initially
	 *
	 * @return
	 * @throws DriverException
	 */
	public String getDefaultGeometry() throws DriverException;

	/**
	 * Get the geometry in the specified field in the specified row of the data
	 * source
	 *
	 * @param fieldName
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public Geometry getGeometry(String fieldName, long rowIndex) throws DriverException;

	/**
	 * Get the geometry in the specified field of the specified feature
	 *
	 * @param fieldName
	 * @param featureId
	 * @return
	 * @throws DriverException
	 */
	public Geometry getGeometry(String fieldName, FID featureId) throws DriverException;

	/**
	 * Builds a spatial index in the specified field. The field has to be of a
	 * spatial type
	 *
	 * @param string
	 * @throws DriverException
	 */
	public void buildIndex(String fieldName) throws DriverException;

	/**
	 * Returns true if the specified field has a spatial index, built on a
	 * previous call to buildIndex
	 *
	 * @param fieldName
	 * @return
	 * @throws DriverException
	 */
	public boolean isIndexed(String fieldName) throws DriverException;

	/**
	 * Queries the index in the specified field
	 *
	 * @param fieldName
	 * @param fullExtent
	 * @return
	 * @throws DriverException
	 */
	public List<FID> queryIndex(String fieldName, Envelope fullExtent) throws DriverException;

}
