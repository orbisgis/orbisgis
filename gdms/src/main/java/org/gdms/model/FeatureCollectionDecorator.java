package org.gdms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.NonEditableDataSourceException;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ReadOnlyDriver;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.sql.strategies.IncompatibleTypesException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class FeatureCollectionDecorator extends AbstractDataSourceDecorator
		implements FeatureCollection {

	private int spatialFieldIndex = -1;

	private Map<String, CoordinateReferenceSystem> crsMap = new HashMap<String, CoordinateReferenceSystem>();

	private int index = 0;

	public FeatureCollectionDecorator(DataSource internalDataSource) {
		super(internalDataSource);
	}

	@Override
	public void addAll(Collection features) {

	}

	@Override
	public void clear() {

	}

	@Override
	public Envelope getEnvelope() {
		try {
			Number[] xScope = getScope(ReadOnlyDriver.X);

			Number[] yScope = getScope(ReadOnlyDriver.Y);
			if ((xScope != null) && (yScope != null)) {
				return new Envelope(new Coordinate(xScope[0].doubleValue(),
						yScope[0].doubleValue()), new Coordinate(xScope[1]
						.doubleValue(), yScope[1].doubleValue()));
			} else {
				return null;
			}
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public FeatureSchema getFeatureSchema() {

		try {
			return new FeatureSchemaAdapter(getDataSource(),
					getSpatialFieldIndex());
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the name of the field which is the default geometry. If the data
	 * source contains only one spatial field, the default geometry is that
	 * field initially
	 *
	 * @return
	 * @throws DriverException
	 */

	public String getDefaultGeometry() throws DriverException {
		return getMetadata().getFieldName(getSpatialFieldIndex());
	}

	/**
	 * Returns the index of the field containing spatial data
	 *
	 * @return
	 * @throws DriverException
	 */
	public int getSpatialFieldIndex() throws DriverException {
		if (spatialFieldIndex == -1) {
			Metadata m = getMetadata();
			for (int i = 0; i < m.getFieldCount(); i++) {
				int typeCode = m.getFieldType(i).getTypeCode();
				if ((typeCode == Type.GEOMETRY) || (typeCode == Type.RASTER)) {
					spatialFieldIndex = i;
					break;
				}
			}
		}

		return spatialFieldIndex;
	}

	@Override
	public List getFeatures() {
		try {
			return new FeatureListAdapter(getDataSource(),
					getSpatialFieldIndex());
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterator iterator() {
		try {
			return new FeatureIterator(getDataSource(), getSpatialFieldIndex());
		} catch (DriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List query(Envelope envelope) {
		if (!envelope.intersects(getEnvelope())) {
			return new ArrayList();
		}

		ArrayList queryResult = new ArrayList();

		for (Iterator i = iterator(); i.hasNext();) {
			Feature feature = (Feature) i.next();

			if (feature.getGeometry().getEnvelopeInternal()
					.intersects(envelope)) {
				queryResult.add(feature);
			}
		}

		return queryResult;
	}

	@Override
	public Collection remove(Envelope env) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAll(Collection features) {
		// TODO Auto-generated method stub

	}

	public int size() {
		try {
			return (int) getRowCount();
		} catch (DriverException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void add(Feature feature) {
		try {
			getDataSource().insertFilledRow(
					FeatureCollectionModelUtils.getValues(feature));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IncompatibleTypesException e) {
			e.printStackTrace();
		} catch (DriverException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void remove(Feature feature) {
		// TODO Auto-generated method stub

	}

}
