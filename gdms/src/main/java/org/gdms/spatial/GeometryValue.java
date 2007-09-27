package org.gdms.spatial;

import org.gdms.data.types.Type;
import org.gdms.data.values.AbstractValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

public class GeometryValue extends AbstractValue {

	private Geometry geom;

	private static final WKBWriter writer = new WKBWriter();

	private static final WKBReader reader = new WKBReader();

	public GeometryValue(Geometry g) {
		this.geom = g;
	}

	public String getStringValue(ValueWriter writer) {
		// TODO : question why not replace following 'this' by 'geom' ?
		return writer.getStatementString(this);
	}

	public int getType() {
		return Type.GEOMETRY;
	}

	public Geometry getGeom() {
		return geom;
	}

	public int doHashCode() {
		return geom.hashCode();
	}

	@Override
	public Value equals(Value obj) {
		if (obj instanceof StringValue) {
			StringValue str = (StringValue) obj;
			return ValueFactory.createValue(str.equals(this.geom.toText()));
		} else {
			return ValueFactory.createValue(geom
					.equalsExact((Geometry) ((GeometryValue) obj).geom));
		}
	}

	@Override
	public Value notEquals(Value value) throws IncompatibleTypesException {
		return equals(value).inversa();
	}

	public String toString() {
		return geom.toText();
	}

	public byte[] getBytes() {
		return writer.write(geom);
	}

	public static Value readBytes(byte[] buffer) {
		try {
			return new GeometryValue(reader.read(buffer));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}