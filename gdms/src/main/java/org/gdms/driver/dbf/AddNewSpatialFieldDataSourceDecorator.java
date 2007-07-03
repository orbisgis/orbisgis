package org.gdms.driver.dbf;

import org.gdms.data.AbstractDataSourceDecorator;
import org.gdms.data.DataSource;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.GeometryConstraint;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

public class AddNewSpatialFieldDataSourceDecorator extends
		AbstractDataSourceDecorator implements DataSource {
	private final static Geometry ORIGIN = new GeometryFactory()
			.createPoint(new Coordinate(0, 0));

	private DefaultMetadata metadata;

	public AddNewSpatialFieldDataSourceDecorator(DataSource ds) {
		super(ds);
	}

	public Metadata getMetadata() throws DriverException {
		if (null == metadata) {
			metadata = new DefaultMetadata(getDataSource().getMetadata());
			try {
				metadata.addField(0, "dbfVirtualField", Type.GEOMETRY,
						new Constraint[] { new GeometryConstraint(
								GeometryConstraint.POINT_2D) });
			} catch (InvalidTypeException e) {
				throw new DriverException(e);
			}
		}
		return metadata;
	}

	public Value getFieldValue(final long rowIndex, final int fieldId)
			throws DriverException {
		if (0 == fieldId) {
			return new GeometryValue(ORIGIN);
		} else {
			return getDataSource().getFieldValue(rowIndex, fieldId - 1);
		}
	}
}