package org.gdms.spatial;

import org.gdms.data.values.AbstractValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;

public class GeometryValue extends AbstractValue implements Value {

	private Geometry geom;

	public GeometryValue(Geometry g) {
		this.geom = g;
	}

	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(this);
	}

	public int getType() {
		return PTTypes.GEOMETRY;
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

}
