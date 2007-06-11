package org.gdms.spatial;

import java.util.ArrayList;
import java.util.List;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.DefaultTypeDefinition;
import org.gdms.data.types.InvalidTypeException;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeDefinition;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.ObjectReadWriteDriver;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class SeveralSpatialFieldsDriver implements ObjectReadWriteDriver {

	private List<Geometry> geoms1;

	private List<Geometry> geoms2;

	private List<Geometry> geoms3;

	private List<Value> alphaField;

	private Envelope envelope;

	public SeveralSpatialFieldsDriver() {

		GeometryFactory gf = new GeometryFactory();

		geoms1 = new ArrayList<Geometry>();
		geoms2 = new ArrayList<Geometry>();
		geoms3 = new ArrayList<Geometry>();

		geoms1.add(gf.createPoint(new Coordinate(0, 0)));
		geoms2.add(gf.createLineString(new Coordinate[] {
				new Coordinate(10, 0), new Coordinate(10, 10),
				new Coordinate(110, 10), new Coordinate(10, 0), }));
		geoms3.add(gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 30), new Coordinate(0, 310),
				new Coordinate(10, 310), new Coordinate(10, 30),
				new Coordinate(0, 30) }), new LinearRing[0]));

		geoms1.add(gf.createPoint(new Coordinate(10, 0)));
		geoms2.add(gf.createLineString(new Coordinate[] { new Coordinate(0, 0),
				new Coordinate(0, 10), new Coordinate(10, 10),
				new Coordinate(0, 0), }));
		geoms3.add(gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 0), new Coordinate(0, 10),
				new Coordinate(10, 10), new Coordinate(10, 0),
				new Coordinate(0, 0) }), new LinearRing[0]));

		geoms1.add(gf.createPoint(new Coordinate(0, 10)));
		geoms2.add(gf.createLineString(new Coordinate[] {
				new Coordinate(20, 0), new Coordinate(20, 10),
				new Coordinate(210, 10), new Coordinate(20, 0), }));
		geoms3.add(gf.createPolygon(gf.createLinearRing(new Coordinate[] {
				new Coordinate(0, 20), new Coordinate(0, 210),
				new Coordinate(10, 210), new Coordinate(10, 20),
				new Coordinate(0, 20) }), new LinearRing[0]));

		alphaField = new ArrayList<Value>();
		alphaField.add(ValueFactory.createValue(1));
		alphaField.add(ValueFactory.createValue(2));
		alphaField.add(ValueFactory.createValue(3));

		calculateEnvelope(geoms1);
		calculateEnvelope(geoms2);
		calculateEnvelope(geoms3);
	}

	private void calculateEnvelope(List<Geometry> geoms) {
		for (int i = 0; i < geoms.size(); i++) {
			if (envelope != null) {
				envelope.expandToInclude(geoms.get(i).getEnvelopeInternal());
			} else {
				envelope = geoms.get(i).getEnvelopeInternal();
			}
		}
	}

	public String[] getPrimaryKeys() {
		return null;
	}

	public boolean isReadOnly(int i) {
		return false;
	}

	public void start() throws DriverException {

	}

	public void stop() throws DriverException {

	}

	public void write(DataSource dataSource) throws DriverException {
		List<Geometry> geoms1 = new ArrayList<Geometry>();
		List<Geometry> geoms2 = new ArrayList<Geometry>();
		List<Geometry> geoms3 = new ArrayList<Geometry>();
		List<Value> alphaField = new ArrayList<Value>();
		for (int i = 0; i < dataSource.getRowCount(); i++) {
			add(geoms1, dataSource.getFieldValue(i, 0));
			add(geoms2, dataSource.getFieldValue(i, 1));
			add(geoms3, dataSource.getFieldValue(i, 2));
			alphaField.add(dataSource.getFieldValue(i, 3));
		}

		this.geoms1 = geoms1;
		this.geoms2 = geoms2;
		this.geoms3 = geoms3;
		this.alphaField = alphaField;
	}

	private void add(List<Geometry> geoms, Value fieldValue) {
		if (fieldValue instanceof NullValue) {
			geoms.add(null);
		} else {
			GeometryValue g = (GeometryValue) fieldValue;
			geoms.add(g.getGeom());
		}
	}

	// public String check(Field field, Value value) throws DriverException {
	// return null;
	// }
	//
	// public String[] getAvailableTypes() throws DriverException {
	// return null;
	// }

	public Metadata getMetadata() throws DriverException {
		final int fc = 4;
		final Type[] fieldsTypes = new Type[fc];
		final String[] fieldsNames = new String[] { "geom1", "geom2", "geom3",
				"alpha" };

		try {
			fieldsTypes[0] = TypeFactory.createType(Type.GEOMETRY, "GEOMETRY");
			fieldsTypes[1] = TypeFactory.createType(Type.GEOMETRY, "GEOMETRY");
			fieldsTypes[2] = TypeFactory.createType(Type.GEOMETRY, "GEOMETRY");
			fieldsTypes[3] = TypeFactory.createType(Type.STRING, "STRING");
		} catch (InvalidTypeException e) {
			throw new RuntimeException("Bug in the driver", e);
		}

		return new DefaultMetadata(fieldsTypes, fieldsNames);
		// Metadata ddm = new DefaultDriverMetadata();
		// ddm.addField("geom1", PTTypes.STR_GEOMETRY);
		// ddm.addField("geom2", PTTypes.STR_GEOMETRY);
		// ddm.addField("geom3", PTTypes.STR_GEOMETRY);
		// ddm.addField("alpha", "int");
		// return ddm;
	}

	// public String[] getParameters(String driverType) throws DriverException {
	// return null;
	// }

	public Number[] getScope(int dimension, String fieldName)
			throws DriverException {
		if (dimension == X) {
			return new Number[] { envelope.getMinX(), envelope.getMaxX() };
		} else if (dimension == Y) {
			return new Number[] { envelope.getMinY(), envelope.getMaxY() };
		} else {
			return null;
		}
	}

	public int getType(String driverType) {
		if ("int".equals(driverType)) {
			return Type.INT;
		} else {
			return Type.GEOMETRY;
		}
	}

	// public boolean isValidParameter(String driverType, String paramName,
	// String paramValue) {
	// return false;
	// }

	public void setDataSourceFactory(DataSourceFactory dsf) {

	}

	public String getName() {
		return "";
	}

	public Value getFieldValue(long rowIndex, int fieldId)
			throws DriverException {
		Value ret;
		if (fieldId == 0) {
			ret = getGeometry(geoms1, rowIndex);
		} else if (fieldId == 1) {
			ret = getGeometry(geoms2, rowIndex);
		} else if (fieldId == 2) {
			ret = getGeometry(geoms3, rowIndex);
		} else if (fieldId == 3) {
			ret = alphaField.get((int) rowIndex);
		} else {
			throw new RuntimeException();
		}

		return ret;
	}

	private Value getGeometry(List<Geometry> geoms, long rowIndex) {
		Geometry g = geoms.get((int) rowIndex);

		if (g != null) {
			return ValueFactory.createValue(g);
		} else {
			return ValueFactory.createNullValue();
		}
	}

	public long getRowCount() throws DriverException {
		return geoms1.size();
	}

	public FID getFid(long row) {
		return null;
	}

	public boolean hasFid() {
		return false;
	}

	public boolean isCommitable() {
		return true;
	}

	public CoordinateReferenceSystem getCRS(String fieldName)
			throws DriverException {
		// TODO Auto-generated method stub
		return null;
	}

	public TypeDefinition[] getTypesDefinitions() throws DriverException {
		try {
			return new TypeDefinition[] {
					new DefaultTypeDefinition("STRING", Type.STRING),
					new DefaultTypeDefinition("INTEGER", Type.INT),
					new DefaultTypeDefinition("GEOMETRY", Type.GEOMETRY) };
		} catch (InvalidTypeException e) {
			throw new DriverException("Invalid type");
		}
	}
}