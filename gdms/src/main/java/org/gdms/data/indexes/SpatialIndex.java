package org.gdms.data.indexes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.NoSuchTableException;
import org.gdms.data.edition.OriginalDirection;
import org.gdms.data.edition.PhysicalDirection;
import org.gdms.data.indexes.quadtree.Quadtree;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.gdms.spatial.GeometryValue;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.hardcode.driverManager.DriverLoadException;
import com.vividsolutions.jts.geom.Geometry;

public class SpatialIndex implements SourceIndex {

	public static final String SPATIAL_INDEX = SpatialIndex.class.getName();

	private Quadtree index;

	private int fieldId;

	private String fieldName;

	public void buildIndex(DataSourceFactory dsf, String dataSourceName, String fieldName)
			throws DriverException, IncompatibleTypesException,
			DriverLoadException, NoSuchTableException,
			DataSourceCreationException {
		this.fieldName = fieldName;
		DataSource ds = dsf.getDataSource(dataSourceName, DataSourceFactory.NORMAL);
		ds.open();
		fieldId = ds.getFieldIndexByName(fieldName);
		if (ds.getMetadata().getFieldType(fieldId).getTypeCode() != Type.GEOMETRY) {
			throw new IncompatibleTypesException(fieldName + " is not spatial");
		}
		index = new Quadtree();
		for (int i = 0; i < ds.getRowCount(); i++) {
			Geometry g = ((GeometryValue) ds.getFieldValue(i, fieldId))
					.getGeom();
			if (g != null) {
				index.insert(g.getEnvelopeInternal(), new OriginalDirection(ds, i));
			}
		}
		ds.cancel();
	}

	@SuppressWarnings("unchecked")
	public DataSourceIndex getDataSourceIndex(DataSource ds) throws DriverException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(index);

			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(bos.toByteArray()));
			Quadtree newIndex = (Quadtree) ois.readObject();

			List<PhysicalDirection> all = newIndex.queryAll();
			for (PhysicalDirection direction : all) {
				((OriginalDirection)direction).setSource(ds);
			}

			DataSourceSpatialIndex ret = new DataSourceSpatialIndex(fieldId,
					fieldName, newIndex);

			return ret;
		} catch (IOException e) {
			throw new DriverException(e);
		} catch (ClassNotFoundException e) {
			throw new DriverException(e);
		}
	}

	public String getId() {
		return SPATIAL_INDEX;
	}
}
