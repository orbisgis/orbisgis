/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). GDMS is produced  by the geomatic team of the IRSTV
 * Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALES CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALES CORTES, Thomas LEDUC
 *
 * This file is part of GDMS.
 *
 * GDMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GDMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GDMS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-developers/>
 *    <http://listes.cru.fr/sympa/info/orbisgis-users/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data.values;

import org.gdms.data.types.Type;
import org.gdms.sql.instruction.IncompatibleTypesException;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

class GeometryValue extends AbstractValue {

	private Geometry geom;

	private static final WKBWriter writer = new WKBWriter();

	private static final WKBReader reader = new WKBReader();

	public GeometryValue(Geometry g) {
		this.geom = g;
	}

	public String getStringValue(ValueWriter writer) {
		return writer.getStatementString(geom);
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
		if (obj.getType() == Type.STRING) {
			return ValueFactory.createValue(obj.equals(this.geom.toText()));
		} else {
			return ValueFactory.createValue(geom.equalsExact(obj
					.getAsGeometry()));
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

	public Value toType(int typeCode) throws IncompatibleTypesException {
		if (typeCode == getType()) {
			return this;
		} else {
			throw new IncompatibleTypesException("Cannot cast value to type: "
					+ typeCode);
		}
	}

	@Override
	public Geometry getAsGeometry() throws IncompatibleTypesException {
		return geom;
	}
}