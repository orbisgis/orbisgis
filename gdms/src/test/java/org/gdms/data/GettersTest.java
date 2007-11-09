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
package org.gdms.data;

import junit.framework.TestCase;

import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.BinaryValue;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.ByteValue;
import org.gdms.data.values.DateValue;
import org.gdms.data.values.DoubleValue;
import org.gdms.data.values.FloatValue;
import org.gdms.data.values.IntValue;
import org.gdms.data.values.LongValue;
import org.gdms.data.values.ShortValue;
import org.gdms.data.values.StringValue;
import org.gdms.data.values.TimeValue;
import org.gdms.data.values.TimestampValue;
import org.gdms.data.values.ValueFactory;

public class GettersTest extends TestCase {

	private DataSourceFactory dsf;

	public void testAllGeters() throws Exception {
		AllTypesObjectDriver test = new AllTypesObjectDriver();
		DataSource d = dsf.getDataSource("alltypes");
		d.open();
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getBinary(0, 0))
				.equals(test.getFieldValue(0, 0))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getBinary(0, "binary")).equals(test.getFieldValue(0, 0)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getBoolean(0, 1))
				.equals(test.getFieldValue(0, 1))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getBoolean(0, "boolean")).equals(test.getFieldValue(0, 1)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getByte(0, 2))
				.equals(test.getFieldValue(0, 2))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getByte(0, "byte")).equals(test.getFieldValue(0, 2)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getDate(0, 3))
				.equals(test.getFieldValue(0, 3))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getDate(0, "date")).equals(test.getFieldValue(0, 3)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getDouble(0, 4))
				.equals(test.getFieldValue(0, 4))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getDouble(0, "double")).equals(test.getFieldValue(0, 4)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getFloat(0, 5))
				.equals(test.getFieldValue(0, 5))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getFloat(0, "float")).equals(test.getFieldValue(0, 5)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getInt(0, 6))
				.equals(test.getFieldValue(0, 6))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getInt(0, "int"))
				.equals(test.getFieldValue(0, 6))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getLong(0, 7))
				.equals(test.getFieldValue(0, 7))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getLong(0, "long")).equals(test.getFieldValue(0, 7)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getShort(0, 8))
				.equals(test.getFieldValue(0, 8))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getShort(0, "short")).equals(test.getFieldValue(0, 8)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getString(0, 9))
				.equals(test.getFieldValue(0, 9))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getString(0, "string")).equals(test.getFieldValue(0, 9)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getTimestamp(0, 10)).equals(test.getFieldValue(0, 10)))
				.getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getTimestamp(0, "timestamp")).equals(
				test.getFieldValue(0, 10))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(d.getTime(0, 11))
				.equals(test.getFieldValue(0, 11))).getValue());
		assertTrue(((BooleanValue) ValueFactory.createValue(
				d.getTime(0, "time")).equals(test.getFieldValue(0, 11)))
				.getValue());
		d.cancel();
	}

	public void testSetters() throws Exception {
		DataSource d = dsf.getDataSource("alltypes");
		d.open();
		d.setBinary(0, 0, ((BinaryValue) d.getFieldValue(1, 0)).getValue());
		d.setBinary(0, "binary", ((BinaryValue) d.getFieldValue(1, 0))
				.getValue());
		d.setBoolean(0, 1, ((BooleanValue) d.getFieldValue(1, 1)).getValue());
		d.setBoolean(0, "boolean", ((BooleanValue) d.getFieldValue(1, 1))
				.getValue());
		d.setByte(0, 2, ((ByteValue) d.getFieldValue(1, 2)).getValue());
		d.setByte(0, "byte", ((ByteValue) d.getFieldValue(1, 2)).getValue());
		d.setDate(0, 3, ((DateValue) d.getFieldValue(1, 3)).getValue());
		d.setDate(0, "date", ((DateValue) d.getFieldValue(1, 3)).getValue());
		d.setDouble(0, 4, ((DoubleValue) d.getFieldValue(1, 4)).getValue());
		d.setDouble(0, "double", ((DoubleValue) d.getFieldValue(1, 4))
				.getValue());
		d.setFloat(0, 5, ((FloatValue) d.getFieldValue(1, 5)).getValue());
		d.setFloat(0, "float", ((FloatValue) d.getFieldValue(1, 5)).getValue());
		d.setInt(0, 6, ((IntValue) d.getFieldValue(1, 6)).getValue());
		d.setInt(0, "int", ((IntValue) d.getFieldValue(1, 6)).getValue());
		d.setLong(0, 7, ((LongValue) d.getFieldValue(1, 7)).getValue());
		d.setLong(0, "long", ((LongValue) d.getFieldValue(1, 7)).getValue());
		d.setShort(0, 8, ((ShortValue) d.getFieldValue(1, 8)).getValue());
		d.setShort(0, "short", ((ShortValue) d.getFieldValue(1, 8)).getValue());
		d.setString(0, 9, ((StringValue) d.getFieldValue(1, 9)).getValue());
		d.setString(0, "string", ((StringValue) d.getFieldValue(1, 9))
				.getValue());
		d.setTimestamp(0, 10, ((TimestampValue) d.getFieldValue(1, 10))
				.getValue());
		d.setTimestamp(0, "timestamp",
				((TimestampValue) d.getFieldValue(1, 10)).getValue());
		d.setTime(0, 11, ((TimeValue) d.getFieldValue(1, 11)).getValue());
		d.setTime(0, "time", ((TimeValue) d.getFieldValue(1, 11)).getValue());

		for (int i = 0; i < d.getMetadata().getFieldCount(); i++) {
			assertTrue(((BooleanValue) d.getFieldValue(0, i).equals(
					d.getFieldValue(1, i))).getValue());
		}
		d.cancel();
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.getSourceManager().register("alltypes",
				new ObjectSourceDefinition(new AllTypesObjectDriver()));
		super.setUp();
	}
}
