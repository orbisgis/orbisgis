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
package org.gdms.sql.strategies;

import java.util.HashSet;
import java.util.Set;

import org.gdms.SourceTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.DigestUtilities;
import org.gdms.data.object.ObjectSourceDefinition;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.BooleanValue;
import org.gdms.data.values.NullValue;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.data.values.ValueWriter;
import org.gdms.driver.memory.ObjectMemoryDriver;

/**
 * @author Fernando Gonzalez Cortes
 */
public class SQLTest extends SourceTest {
	public static DataSource d;
//
//	public void testParserBug() throws Exception {
//		String sql = "select _field from table;";
//		SQLEngine eng = new SQLEngine(new ByteArrayInputStream(sql.getBytes()));
//		eng.SQLStatement();
//	}
//
	private void testIsClause(String ds) throws Exception {
		String fieldName = super.getContainingNullFieldNameFor(ds);
		DataSource d = dsf.executeSQL("select * from " + ds + " where "
				+ fieldName + " is null;");
		d.open();
		int index = d.getFieldIndexByName(fieldName);
		for (int i = 0; i < d.getRowCount(); i++) {
			assertTrue(d.isNull(i, index));
		}
		d.cancel();
	}

	public void testIsClause() throws Exception {
		String[] resources = super.getResourcesWithNullValues();
		for (String resource : resources) {
			testIsClause(resource);
		}
	}

	private void testBetweenClause(String ds) throws Exception {
		String numericField = super.getNumericFieldNameFor(ds);
		double low = super.getMinimumValueFor(ds, numericField);
		double high = super.getMaximumValueFor(ds, numericField) + low / 2;
		DataSource d = dsf.executeSQL("select * from " + ds + " where "
				+ numericField + " between " + low + " and " + high + ";");
		d.open();
		for (int i = 0; i < d.getRowCount(); i++) {
			double fieldValue = d.getDouble(i, numericField);
			assertTrue((low <= fieldValue) && (fieldValue <= high));
		}
		d.cancel();
	}

	public void testBetweenClause() throws Exception {
		String[] resources = super.getResourcesWithNumericField();
		for (String resource : resources) {
			testBetweenClause(resource);
		}
	}

	private void testInClause(String ds) throws Exception {
		String numericField = super.getNumericFieldNameFor(ds);
		double low = super.getMinimumValueFor(ds, numericField);
		double high = super.getMaximumValueFor(ds, numericField);
		DataSource d = dsf.executeSQL("select * from " + ds + " where "
				+ numericField + " in (" + low + ", " + high + ");");

		d.open();
		for (int i = 0; i < d.getRowCount(); i++) {
			double fieldValue = d.getDouble(i, numericField);
			assertTrue((low == fieldValue) || (fieldValue == high));
		}
		d.cancel();
	}

	public void testInClause() throws Exception {
		String[] resources = super.getResourcesWithNumericField();
		for (String resource : resources) {
			testInClause(resource);
		}
	}

	private void testAggregate(String ds) throws Exception {
		String numericField = super.getNumericFieldNameFor(ds);
		double low = super.getMinimumValueFor(ds, numericField);
		double high = (super.getMaximumValueFor(ds, numericField) + low) / 2;

		DataSource d = dsf.executeSQL("select count(" + numericField
				+ ") from " + ds + " where " + numericField + " < " + high
				+ ";");

		DataSource original = dsf.getDataSource(ds);
		original.open();
		int count = 0;
		for (int i = 0; i < original.getRowCount(); i++) {
			double fieldValue = original.getDouble(i, numericField);
			if (fieldValue < high) {
				count++;
			}
		}
		original.cancel();

		d.open();
		assertTrue(count == d.getDouble(0, 0));
		d.cancel();
	}

	public void testAggregate() throws Exception {
		String[] resources = super.getResourcesWithNumericField();
		for (String resource : resources) {
			testAggregate(resource);
		}
	}

	private void testTwoTimesTheSameAggregate(String ds) throws Exception {
		String numericField = super.getNumericFieldNameFor(ds);
		double low = super.getMinimumValueFor(ds, numericField);
		double high = super.getMaximumValueFor(ds, numericField) + low / 2;

		DataSource d = dsf.executeSQL("select count(" + numericField
				+ "), count(" + numericField + ") from " + ds + " where "
				+ numericField + " < " + high + ";");

		d.open();
		assertTrue(equals(d.getFieldValue(0, 0), d.getFieldValue(0, 1)));
		d.cancel();
	}

	public void testTwoTimesTheSameAggregate() throws Exception {
		String[] resources = super.getResourcesWithNumericField();
		for (String resource : resources) {
			testTwoTimesTheSameAggregate(resource);
		}
	}

	private void testOrderByAsc(String ds) throws Exception {
		String fieldName = super.getNoPKFieldFor(ds);
		String sql = "select * from " + ds + " order by " + fieldName + " asc;";

		DataSource resultDataSource = dsf.executeSQL(sql);
		resultDataSource.open();
		int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
		for (int i = 1; i < resultDataSource.getRowCount(); i++) {
			Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex);
			Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
			if (v1.getType() != Type.NULL) {
				assertTrue(((BooleanValue) v1.lessEqual(v2)).getValue());
			}
		}
		resultDataSource.cancel();

	}

	public void testOrderByAsc() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testOrderByAsc(resource);
		}
	}

	private void testOrderByDesc(String ds) throws Exception {
		String fieldName = super.getNoPKFieldFor(ds);
		String sql = "select * from " + ds + " order by " + fieldName
				+ " desc;";

		DataSource resultDataSource = dsf.executeSQL(sql);
		resultDataSource.open();
		int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
		for (int i = 1; i < resultDataSource.getRowCount(); i++) {
			Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex);
			Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
			if (v2.getType() != Type.NULL) {
				assertTrue(((BooleanValue) v1.greaterEqual(v2)).getValue());
			}
		}
		resultDataSource.cancel();

	}

	public void testOrderByDesc() throws Exception {
		String[] resources = super.getSmallResources();
		for (String resource : resources) {
			testOrderByDesc(resource);
		}
	}

	private void testOrderByWithNullValues(String ds) throws Exception {
		String fieldName = super.getContainingNullFieldNameFor(ds);
		String sql = "select * from " + ds + " order by " + fieldName + " asc;";

		DataSource resultDataSource = dsf.executeSQL(sql);
		resultDataSource.open();
		int fieldIndex = resultDataSource.getFieldIndexByName(fieldName);
		boolean[] nullValues = new boolean[(int) resultDataSource.getRowCount()];
		for (int i = 1; i < resultDataSource.getRowCount(); i++) {
			if (resultDataSource.isNull(i, fieldIndex)) {
				nullValues[i] = true;
			} else {
				nullValues[i] = false;
				if (!resultDataSource.isNull(i - 1, fieldIndex)) {
					Value v1 = resultDataSource
							.getFieldValue(i - 1, fieldIndex);
					Value v2 = resultDataSource.getFieldValue(i, fieldIndex);
					assertTrue(((BooleanValue) v1.lessEqual(v2)).getValue());
				}
			}
		}
		nullValues[0] = resultDataSource.isNull(0, fieldIndex);

		for (int i = 1; i < nullValues.length - 1; i++) {
			assertFalse("All null together", !nullValues[i]
					&& nullValues[i - 1] && nullValues[i + 1]);
		}
		resultDataSource.cancel();
	}

	public void testOrderByWithNullValues() throws Exception {
		String[] resources = super.getResourcesWithNullValues();
		for (String resource : resources) {
			testOrderByWithNullValues(resource);
		}
	}

	public void testOrderByMultipleFields(String ds) throws Exception {
		String[] fields = super.getFieldNames(super.getAnyNonSpatialResource());
		String sql = "select * from " + ds + " order by " + fields[0] + ", "
				+ fields[1] + " asc;";

		DataSource resultDataSource = dsf.executeSQL(sql);
		resultDataSource.open();
		int fieldIndex1 = resultDataSource.getFieldIndexByName(fields[0]);
		int fieldIndex2 = resultDataSource.getFieldIndexByName(fields[1]);
		for (int i = 1; i < resultDataSource.getRowCount(); i++) {
			Value v1 = resultDataSource.getFieldValue(i - 1, fieldIndex1);
			Value v2 = resultDataSource.getFieldValue(i, fieldIndex1);
			if (((BooleanValue) v1.less(v2)).getValue()) {
				assertTrue(true);
			} else {
				v1 = resultDataSource.getFieldValue(i - 1, fieldIndex2);
				v2 = resultDataSource.getFieldValue(i, fieldIndex2);
				assertTrue(((BooleanValue) v1.lessEqual(v2)).getValue());
			}
		}
		resultDataSource.cancel();

	}

	private void testDistinct(String ds) throws Exception {
		String[] fields = super.getFieldNames(ds);
		DataSource d = dsf.executeSQL("select distinct " + fields[0] + " from "
				+ ds + " ;");

		d.open();
		int fieldIndex = d.getFieldIndexByName(fields[0]);
		Set<Value> valueSet = new HashSet<Value>();
		for (int i = 0; i < d.getRowCount(); i++) {
			assertTrue(!valueSet.contains(d.getFieldValue(i, fieldIndex)));
			valueSet.add(d.getFieldValue(i, fieldIndex));
		}
		d.cancel();
	}

	public void testDistinct() throws Exception {
		String[] resources = super.getResourcesWithRepeatedRows();
		for (String resource : resources) {
			testDistinct(resource);
		}
	}

	private void testDistinctManyFields(String ds) throws Exception {
		String[] fields = super.getFieldNames(ds);
		DataSource d = dsf.executeSQL("select distinct " + fields[0] + ", "
				+ fields[1] + " from " + ds + " ;");

		d.open();
		int fieldIndex1 = d.getFieldIndexByName(fields[0]);
		int fieldIndex2 = d.getFieldIndexByName(fields[1]);
		Set<Value> valueSet = new HashSet<Value>();
		for (int i = 0; i < fields.length; i++) {
			Value v1 = d.getFieldValue(i, fieldIndex1);
			Value v2 = d.getFieldValue(i, fieldIndex2);
			Value col = ValueFactory.createValue(new Value[] { v1, v2 });
			assertTrue(!valueSet.contains(col));
			valueSet.add(col);
		}
		d.cancel();
	}

	public void testDistinctManyFields() throws Exception {
		String[] resources = super.getResourcesWithRepeatedRows();
		for (String resource : resources) {
			testDistinctManyFields(resource);
		}
	}

	private void testDistinctAllFields(String ds) throws Exception {
		String[] fields = super.getFieldNames(ds);
		DataSource d = dsf.executeSQL("select distinct * from " + ds + " ;");

		d.open();
		Set<Value> valueSet = new HashSet<Value>();
		for (int i = 0; i < fields.length; i++) {
			Value col = ValueFactory.createValue(d.getRow(i));
			assertTrue(!valueSet.contains(col));
			valueSet.add(col);
		}
		d.cancel();
	}

	public void testDistinctAllFields() throws Exception {
		String[] resources = super.getResourcesWithRepeatedRows();
		for (String resource : resources) {
			testDistinctAllFields(resource);
		}
	}

	/**
	 * test a union query
	 *
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	private void testUnion(String ds) throws Exception {
		d = dsf.executeSQL("(select * from " + ds + ") union (select  * from "
				+ ds + ");");

		d.open();
		DataSource originalDS = dsf.getDataSource(ds);
		originalDS.open();
		for (int i = 0; i < originalDS.getRowCount(); i++) {
			String[] fieldNames = d.getFieldNames();
			Value[] row = d.getRow(0);
			String sql = "select * from " + d.getName() + " where ";
			sql += fieldNames[0] + "="
					+ row[0].getStringValue(ValueWriter.internalValueWriter);
			for (int j = 1; j < row.length; j++) {
				sql += " and " + fieldNames[j];
				if (row[j] instanceof NullValue) {
					sql += " is "
							+ row[j]
									.getStringValue(ValueWriter.internalValueWriter);
				} else {
					sql += "="
							+ row[j]
									.getStringValue(ValueWriter.internalValueWriter);
				}
			}

			/*
			 * We only test if there is an even number of equal rows
			 */
			DataSource testDS = dsf.executeSQL(sql);
			testDS.open();
			assertTrue((testDS.getRowCount() / 2) == (testDS.getRowCount() / 2.0));
			testDS.cancel();
		}
		originalDS.cancel();
		d.cancel();
	}

	public void testUnion() throws Exception {
		String[] resources = super.getResourcesSmallerThan(100);
		for (String resource : resources) {
			testUnion(resource);
		}
	}

	/**
	 * Tests a simple select query
	 *
	 * @throws Throwable
	 *             DOCUMENT ME!
	 */
	private void testSelect(String ds) throws Exception {
		String numericField = super.getNumericFieldNameFor(ds);
		double low = super.getMinimumValueFor(ds, numericField);
		double average = super.getMaximumValueFor(ds, numericField) + low / 2;
		DataSource d = dsf.executeSQL("select * from " + ds + " where "
				+ numericField + "<" + average + ";");

		d.open();
		int fieldIndex = d.getFieldIndexByName(numericField);
		for (int i = 0; i < d.getRowCount(); i++) {
			assertTrue(d.getDouble(i, fieldIndex) < average);
		}
		d.cancel();
	}

	public void testSelect() throws Exception {
		String[] resources = super.getResourcesWithNumericField();
		for (String resource : resources) {
			testSelect(resource);
		}
	}

	public void testSecondaryIndependence() throws Exception {
		DataSource d = dsf.executeSQL("select * from "
				+ super.getAnyNonSpatialResource() + ";",
				DataSourceFactory.EDITABLE);

		DataSource d2 = dsf.executeSQL("select * from " + d.getName() + ";");

		d.open();
		for (int i = 0; i < d.getRowCount();) {
			d.deleteRow(0);
		}
		d2.open();
		assertTrue(!d.getAsString().equals(d2.getAsString()));
		d2.getAsString();
		d2.cancel();
		d.cancel();
	}

	public void testGetDataSourceFactory() throws Exception {
		DataSource d = dsf.executeSQL("select * from "
				+ super.getAnyNonSpatialResource() + ";");

		DataSource d2 = dsf.executeSQL("select * from " + d.getName() + ";");

		assertTrue(dsf == d2.getDataSourceFactory());
	}

	public void testEquallyNamedColumnsInJoin() throws Exception {
		ObjectMemoryDriver omd1 = new ObjectMemoryDriver(new String[] { "id",
				"person" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.STRING) });
		ObjectMemoryDriver omd2 = new ObjectMemoryDriver(new String[] { "id",
				"person" }, new Type[] { TypeFactory.createType(Type.STRING),
				TypeFactory.createType(Type.STRING) });
		dsf.getSourceManager().register("obj1", new ObjectSourceDefinition(omd1));
		dsf.getSourceManager().register("obj2", new ObjectSourceDefinition(omd2));
		DataSource ds = dsf.getDataSource("obj1");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue("pepe"), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue("jean"), });
		ds.commit();
		ds = dsf.getDataSource("obj2");
		ds.open();
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("0"),
				ValueFactory.createValue("pepe"), });
		ds.insertFilledRow(new Value[] { ValueFactory.createValue("1"),
				ValueFactory.createValue("jean"), });
		ds.commit();

		ds = dsf.executeSQL("select * from obj1 o1, obj2 o2 where o1.id=o2.id");
		ds.open();
		assertTrue(ds.getRowCount() == 2);
		ds.cancel();
	}

	public void testCreate() throws Exception {
		String source = super.getAnySpatialResource();
		dsf.executeSQL("call register ('" + backupDir + "/"
				+ "testCreate.shp', 'newShape')");
		dsf.executeSQL("create table newShape as select * from " + source);
		DataSource newDs = dsf.getDataSource("newShape");
		DataSource sourceDs = dsf.getDataSource(source);
		newDs.open();
		sourceDs.open();
		byte[] d1 = DigestUtilities.getDigest(newDs);
		byte[] d2 = DigestUtilities.getDigest(sourceDs);
		assertTrue(DigestUtilities.equals(d1, d2));
		newDs.cancel();
		sourceDs.cancel();
	}

	public void testAliasInFunction() throws Exception {
		String dsName = super.getAnySpatialResource();
		String alias = "myalias";
		DataSource ds = dsf.executeSQL("select Buffer("
				+ super.getSpatialFieldName(dsName) + ", 20) as " + alias
				+ " from " + dsName);
		ds.open();
		assertTrue(ds.getFieldName(0).equals(alias));
		ds.cancel();
	}

	@Override
	protected void setUp() throws Exception {
		setWritingTests(false);
		super.setUp();
	}

}
