package org.gdms.spatial;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

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
	 * Returns the index of the field containing spatial data
	 *
	 * @return
	 * @throws DriverException
	 */
	public int getSpatialFieldIndex() throws DriverException;

	/**
	 * Gets the default geometry of the DataSource as a JTS geometry or null if
	 * the row doesn't have a geometry value
	 *
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public com.vividsolutions.jts.geom.Geometry getGeometry(long rowIndex)
			throws DriverException;

	/**
	 * Sets the default geometry of the DataSource to a JTS geometry
	 *
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public void setGeometry(long rowIndex, Geometry geom)
			throws DriverException;

	/**
	 * Set the field name for the getGeometry(int) method. If this method is not
	 * called, the default geometry is the first spatial field
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
	public Geometry getGeometry(String fieldName, long rowIndex)
			throws DriverException;

	/**
	 * Set the geometry in the specified field in the specified row of the data
	 * source
	 *
	 * @param fieldName
	 * @param rowIndex
	 * @return
	 * @throws DriverException
	 */
	public void setGeometry(String fieldName, long rowIndex, Geometry geom)
			throws DriverException;

	/**
	 * Returns the CRS of the geometric field that is given as parameter
	 *
	 * @param fieldName
	 *
	 * @return
	 * @throws DriverException
	 */
	CoordinateReferenceSystem getCRS(final String fieldName)
			throws DriverException;

	/**
	 * Sets the CRS of the geometric field that is given as 2nd parameter
	 *
	 * @param crs
	 * @param fieldName
	 */
	void setCRS(final CoordinateReferenceSystem crs, final String fieldName);
}