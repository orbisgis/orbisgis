/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms.data;

import junit.framework.TestCase;

import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.values.ValueFactory;

public class GettersTest extends TestCase {

	private DataSourceFactory dsf;

	public void testAllGeters() throws Exception {
		AllTypesObjectDriver test = new AllTypesObjectDriver();
		DataSource d = dsf.getDataSource("alltypes");
		d.open();
		assertTrue(ValueFactory.createValue(d.getBinary(0, 0)).equals(
				test.getFieldValue(0, 0)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getBinary(0, "binary")).equals(
				test.getFieldValue(0, 0)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getBoolean(0, 1)).equals(
				test.getFieldValue(0, 1)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getBoolean(0, "boolean")).equals(
				test.getFieldValue(0, 1)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getByte(0, 2)).equals(
				test.getFieldValue(0, 2)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getByte(0, "byte")).equals(
				test.getFieldValue(0, 2)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getDate(0, 3)).equals(
				test.getFieldValue(0, 3)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getDate(0, "date")).equals(
				test.getFieldValue(0, 3)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getDouble(0, 4)).equals(
				test.getFieldValue(0, 4)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getDouble(0, "double")).equals(
				test.getFieldValue(0, 4)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getFloat(0, 5)).equals(
				test.getFieldValue(0, 5)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getFloat(0, "float")).equals(
				test.getFieldValue(0, 5)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getInt(0, 6)).equals(
				test.getFieldValue(0, 6)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getInt(0, "int")).equals(
				test.getFieldValue(0, 6)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getLong(0, 7)).equals(
				test.getFieldValue(0, 7)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getLong(0, "long")).equals(
				test.getFieldValue(0, 7)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getShort(0, 8)).equals(
				test.getFieldValue(0, 8)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getShort(0, "short")).equals(
				test.getFieldValue(0, 8)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getString(0, 9)).equals(
				test.getFieldValue(0, 9)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getString(0, "string")).equals(
				test.getFieldValue(0, 9)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getTimestamp(0, 10)).equals(
				test.getFieldValue(0, 10)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getTimestamp(0, "timestamp"))
				.equals(test.getFieldValue(0, 10)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getTime(0, 11)).equals(
				test.getFieldValue(0, 11)).getAsBoolean());
		assertTrue(ValueFactory.createValue(d.getTime(0, "time")).equals(
				test.getFieldValue(0, 11)).getAsBoolean());
		d.close();
	}

	public void testSetters() throws Exception {
		DataSource d = dsf.getDataSource("alltypes");
		d.open();
		d.setBinary(0, 0, d.getFieldValue(1, 0).getAsBinary());
		d.setBinary(0, "binary", d.getFieldValue(1, 0).getAsBinary());
		d.setBoolean(0, 1, d.getFieldValue(1, 1).getAsBoolean());
		d.setBoolean(0, "boolean", d.getFieldValue(1, 1).getAsBoolean());
		d.setByte(0, 2, d.getFieldValue(1, 2).getAsByte());
		d.setByte(0, "byte", d.getFieldValue(1, 2).getAsByte());
		d.setDate(0, 3, d.getFieldValue(1, 3).getAsDate());
		d.setDate(0, "date", d.getFieldValue(1, 3).getAsDate());
		d.setDouble(0, 4, d.getFieldValue(1, 4).getAsDouble());
		d.setDouble(0, "double", d.getFieldValue(1, 4).getAsDouble());
		d.setFloat(0, 5, d.getFieldValue(1, 5).getAsFloat());
		d.setFloat(0, "float", d.getFieldValue(1, 5).getAsFloat());
		d.setInt(0, 6, d.getFieldValue(1, 6).getAsInt());
		d.setInt(0, "int", d.getFieldValue(1, 6).getAsInt());
		d.setLong(0, 7, d.getFieldValue(1, 7).getAsLong());
		d.setLong(0, "long", d.getFieldValue(1, 7).getAsLong());
		d.setShort(0, 8, d.getFieldValue(1, 8).getAsShort());
		d.setShort(0, "short", d.getFieldValue(1, 8).getAsShort());
		d.setString(0, 9, d.getFieldValue(1, 9).getAsString());
		d.setString(0, "string", d.getFieldValue(1, 9).getAsString());
		d.setTimestamp(0, "timestamp", d.getFieldValue(1, 10).getAsTimestamp());
		d.setTime(0, 11, d.getFieldValue(1, 11).getAsTime());
		d.setTime(0, "time", d.getFieldValue(1, 11).getAsTime());

		for (int i = 0; i < d.getMetadata().getFieldCount(); i++) {
			assertTrue(d.getFieldValue(0, i).equals(d.getFieldValue(1, i))
					.getAsBoolean());
		}
		d.close();
	}

	@Override
	protected void setUp() throws Exception {
		dsf = new DataSourceFactory();
		dsf.getSourceManager().register("alltypes",
				new ObjectSourceDefinition(new AllTypesObjectDriver()));
		super.setUp();
	}
}
