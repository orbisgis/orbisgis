/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */
package org.gdms.data.types;

import org.gdms.TestBase;
import org.junit.Test;
import org.junit.Before;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;


import org.gdms.Geometries;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DriverException;
import org.gdms.driver.memory.MemoryDataSetDriver;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import java.util.*;

import static org.junit.Assert.*;

public class ConstraintTest extends TestBase {

        private Type type;
        private Value[] validValues = new Value[0];
        private Value[] invalidValues = new Value[0];
        private Value binaryValue = ValueFactory.createValue(new byte[]{2, 3, 4,
                        5});
        private Value booleanValue = ValueFactory.createValue(true);
        private Value byteValue = ValueFactory.createValue((byte) 3);
        private Value dateValue = ValueFactory.createValue(new Date());
        private Value doubleValue = ValueFactory.createValue(4.4d);
        private Value floatValue = ValueFactory.createValue(3.3f);
        private Value geomValue = ValueFactory.createValue(Geometries.getPoint());
        private Value ptValue = ValueFactory.createValue(Geometries.getPoint());
        private Value lineValue = ValueFactory.createValue(Geometries.getLinestring());
        private Value polyValue = ValueFactory.createValue(Geometries.getPolygon());
        private Value multiPtValue = ValueFactory.createValue(Geometries.getMultiPoint3D());
        private Value multiLineValue = ValueFactory.createValue(Geometries.getMultilineString());
        private Value multiPolyValue = ValueFactory.createValue(Geometries.getMultiPolygon2D());
        private Value geomColValue = ValueFactory.createValue(Geometries.getGeometryCollection());
        private Value intValue = ValueFactory.createValue(3);
        private Value longValue = ValueFactory.createValue(4L);
        private Value shortValue = ValueFactory.createValue((short) 3);
        private Value stringValue = ValueFactory.createValue("string");
        private Value timeValue = ValueFactory.createValue(new Time(2));
        private Value timestampValue = ValueFactory.createValue(new Timestamp(2));
        private ValueCollection collectionValue = ValueFactory.createValue(new Value[0]);

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
        }

        @Test
        public void testLength() throws Exception {
                setType(TypeFactory.createType(Type.STRING, new LengthConstraint(4)));
                setValidValues(ValueFactory.createValue("1234"), ValueFactory.createValue(""), ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue("12345"));
                doEdition();
        }

        @Test
        public void testDefaultStringValue() throws Exception {
                setType(TypeFactory.createType(Type.STRING,
                        new DefaultStringConstraint("erwan")));
                setValidValues(ValueFactory.createValue("erwan"), ValueFactory.createValue("erwan"), ValueFactory.createValue("erwan"));
                setInvalidValues(ValueFactory.createValue("bocher"));
                doEdition();
        }

        @Test
        public void testMax() throws Exception {
                setType(TypeFactory.createType(Type.INT, new MinConstraint(-10),
                        new MaxConstraint(10)));
                setValidValues(ValueFactory.createValue(-10), ValueFactory.createValue(10), ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue(-11), ValueFactory.createValue(11));
                doEdition();
        }

        @Test
        public void testNotNull() throws Exception {
                setType(TypeFactory.createType(Type.INT, new NotNullConstraint()));
                setValidValues(ValueFactory.createValue(0));
                setInvalidValues(ValueFactory.createNullValue());
                doEdition();
        }

        @Test
        public void testAutoIncrement() throws Exception {
                AutoIncrementConstraint constraint = new AutoIncrementConstraint();
                checkOnlyCanSetAndAddNull(constraint);
        }

        @Test
        public void testReadOnly() throws Exception {
                ReadOnlyConstraint constraint = new ReadOnlyConstraint();
                checkOnlyCanSetAndAddNull(constraint);
        }

        /**
         * Cannot set a value Cannot insert a new row with values different than
         * null
         * 
         * @param constraint
         * @throws DriverException
         */
        private void checkOnlyCanSetAndAddNull(Constraint constraint)
                throws DriverException {
                Value three = ValueFactory.createValue(3);
                Value nullV = ValueFactory.createNullValue();
                setType(TypeFactory.createType(Type.INT, constraint));
                DataSource ds = getDataSource();
                ds.open();
                ds.insertFilledRow(new Value[]{nullV});
                assertNull(ds.check(0, nullV));
                assertTrue(ds.check(0, three) != null);
                try {
                        ds.insertFilledRow(new Value[]{three});
                        fail();
                } catch (DriverException e) {
                }
                try {
                        ds.setFieldValue(0, 0, three);
                        fail();
                } catch (DriverException e) {
                }
                ds.setFieldValue(0, 0, nullV);
                ds.close();
        }

        @Test
        public void testGeometryType() throws Exception {
                setType(TypeFactory.createType(Type.POINT));
                setValidValues(ValueFactory.createValue(Geometries.getPoint()),
                        ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue(new GeometryFactory().createGeometryCollection(new Geometry[0])),
                        ValueFactory.createValue(Geometries.getMultiPoint3D()));
                doEdition();
        }

        @Test
        public void testDimension3DConstraint() throws Exception {
                setType(TypeFactory.createType(Type.GEOMETRY,
                        new Dimension3DConstraint(3)));
                setValidValues(ValueFactory.createValue(Geometries.getPoint3D()),
                        ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue(Geometries.getMultiPolygon2D()));
                doEdition();
        }

        @Test
        public void testDimension2DConstraint() throws Exception {
                setType(TypeFactory.createType(Type.GEOMETRY,
                        new GeometryDimensionConstraint(0)));
                setValidValues(ValueFactory.createValue(Geometries.getPoint3D()),
                        ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue(Geometries.getMultiPolygon2D()));
                doEdition();
        }

        @Test
        public void testPrecision() throws Exception {
                setType(TypeFactory.createType(Type.DOUBLE, new PrecisionConstraint(3)));
                setValidValues(ValueFactory.createValue(123), ValueFactory.createValue(12.3), ValueFactory.createValue(0.13),
                        ValueFactory.createNullValue(), ValueFactory.createValue(0.123), ValueFactory.createValue(12345));
                setInvalidValues(ValueFactory.createValue(0.1234), ValueFactory.createValue(123.4567));
                doEdition();
        }

        @Test
        public void testScale() throws Exception {
                setType(TypeFactory.createType(Type.DOUBLE, new ScaleConstraint(3)));
                setValidValues(ValueFactory.createValue(123), ValueFactory.createValue(12.322), ValueFactory.createValue(0.133),
                        ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue(0.1323), ValueFactory.createValue(1244.1235));
                doEdition();
        }

        @Test
        public void testPattern() throws Exception {
                setType(TypeFactory.createType(Type.STRING, new PatternConstraint(
                        "[hc]+at")));
                setValidValues(ValueFactory.createValue("hat"), ValueFactory.createValue("cat"), ValueFactory.createNullValue());
                setInvalidValues(ValueFactory.createValue("hate"), ValueFactory.createValue("at"));
                doEdition();
        }

        @Test
        public void testUnique() throws Exception {
                setType(TypeFactory.createType(Type.INT, new UniqueConstraint()));
                checkUniqueness();
        }

        @Test
        public void testPK() throws Exception {
                setType(TypeFactory.createType(Type.INT, new PrimaryKeyConstraint()));
                checkUniqueness();
        }

        @Test
        public void testAddWrongTypeBinary() throws Exception {
                setType(TypeFactory.createType(Type.BINARY));
                setValidValues(binaryValue);
                setInvalidValues(booleanValue, byteValue, dateValue, doubleValue,
                        floatValue, geomValue, intValue, longValue, shortValue,
                        stringValue, timeValue, timestampValue, collectionValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeBoolean() throws Exception {
                setType(TypeFactory.createType(Type.BOOLEAN));
                setValidValues(booleanValue);
                setInvalidValues(binaryValue, byteValue, dateValue, doubleValue,
                        floatValue, geomValue, intValue, longValue, shortValue,
                        timeValue, timestampValue, collectionValue, stringValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeCollection() throws Exception {
                setType(TypeFactory.createType(Type.COLLECTION));
                setValidValues(collectionValue);
                setInvalidValues(binaryValue, booleanValue, byteValue, dateValue,
                        doubleValue, floatValue, geomValue, intValue, longValue,
                        shortValue, stringValue, timeValue, timestampValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeDate() throws Exception {
                setType(TypeFactory.createType(Type.DATE));
                setValidValues(timeValue, dateValue, timestampValue);
                setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
                        geomValue, stringValue, collectionValue, ValueFactory.createValue("1980-09-05"),
                        byteValue, intValue, longValue,shortValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeGeometry() throws Exception {
                setType(TypeFactory.createType(Type.GEOMETRY));
                setValidValues(geomValue,ptValue, lineValue, polyValue, multiPtValue, 
                        multiLineValue, multiPolyValue, geomColValue);
                setInvalidValues(ValueFactory.createValue("POINT (0 0)"));
                doEdition();
        }

        @Test
        public void testAddWrongTypePoint() throws Exception {
                setType(TypeFactory.createType(Type.POINT));
                setValidValues(geomValue,ptValue);
                setInvalidValues(ValueFactory.createValue("POINT (0 0)"), lineValue,
                        polyValue, multiPtValue, multiLineValue, multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeLine() throws Exception {
                setType(TypeFactory.createType(Type.LINESTRING));
                setValidValues(lineValue);
                String str = lineValue.toString();
                setInvalidValues(ValueFactory.createValue(str), geomValue,ptValue ,
                        polyValue, multiPtValue, multiLineValue, multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypePolygon() throws Exception {
                setType(TypeFactory.createType(Type.POLYGON));
                setValidValues(polyValue);
                String str = polyValue.toString();
                setInvalidValues(ValueFactory.createValue(str), geomValue,ptValue ,
                        lineValue, multiPtValue, multiLineValue, multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeMultiPoint() throws Exception {
                setType(TypeFactory.createType(Type.MULTIPOINT));
                setValidValues(multiPtValue);
                String str = multiPtValue.toString();
                setInvalidValues(ValueFactory.createValue(str), geomValue,ptValue ,
                        lineValue, polyValue, multiLineValue, multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeMultiLine() throws Exception {
                setType(TypeFactory.createType(Type.MULTILINESTRING));
                setValidValues(multiLineValue);
                String str = multiLineValue.toString();
                setInvalidValues(ValueFactory.createValue(str), geomValue,ptValue ,
                        lineValue, polyValue, multiPtValue, multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeMultiPolygon() throws Exception {
                setType(TypeFactory.createType(Type.MULTIPOLYGON));
                setValidValues(multiPolyValue);
                String str = multiPolyValue.toString();
                setInvalidValuesGeom(ValueFactory.createValue(str), geomValue,ptValue ,
                        lineValue, polyValue, multiPtValue, multiLineValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeGeometryCollection() throws Exception {
                setType(TypeFactory.createType(Type.GEOMETRYCOLLECTION));
                setValidValues(multiPolyValue, multiPtValue, multiLineValue, geomColValue);
                String str = multiPolyValue.toString();
                setInvalidValuesGeom(ValueFactory.createValue(str), geomValue,ptValue ,
                        lineValue, polyValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeString() throws Exception {
                setType(TypeFactory.createType(Type.STRING));
                setValidValues(binaryValue, booleanValue, byteValue, dateValue,
                        doubleValue, floatValue, geomValue, intValue, longValue,
                        shortValue, stringValue, timeValue, timestampValue,
                        collectionValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeTime() throws Exception {
                setType(TypeFactory.createType(Type.TIME));
                setValidValues(dateValue, timeValue, timestampValue);
                setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
                        geomValue, stringValue, collectionValue, ValueFactory.createValue("1980-09-05 12:00:20"),
                        byteValue, intValue,
                        longValue, shortValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeTimestamp() throws Exception {
                setType(TypeFactory.createType(Type.TIMESTAMP));
                setValidValues(dateValue, timeValue, timestampValue);
                setInvalidValues(binaryValue, booleanValue, doubleValue, floatValue,
                        geomValue, stringValue, collectionValue, ValueFactory.createValue("1980-09-05 12:00:20"),
                        byteValue, intValue,
                        longValue, shortValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeByte() throws Exception {
                setType(TypeFactory.createType(Type.BYTE));
                checkNumber();
        }

        private void checkNumber() throws Exception {
                setValidValues(byteValue, intValue, longValue, shortValue, doubleValue, floatValue);
                setInvalidValues(binaryValue, booleanValue, dateValue, 
                        geomValue, stringValue, timeValue, timestampValue,
                        collectionValue,
                        ptValue, lineValue, polyValue, multiPtValue, multiLineValue,
                        multiPolyValue, geomColValue);
                doEdition();
        }

        @Test
        public void testAddWrongTypeShort() throws Exception {
                setType(TypeFactory.createType(Type.SHORT));
                checkNumber();
        }

        @Test
        public void testAddWrongTypeInt() throws Exception {
                setType(TypeFactory.createType(Type.INT));
                checkNumber();
        }

        @Test
        public void testAddWrongTypeLong() throws Exception {
                setType(TypeFactory.createType(Type.LONG));
                checkNumber();
        }

        @Test
        public void testAddWrongTypeFloat() throws Exception {
                setType(TypeFactory.createType(Type.FLOAT));
                checkNumber();
        }

        @Test
        public void testAddWrongTypeDouble() throws Exception {
                setType(TypeFactory.createType(Type.DOUBLE));
                checkNumber();
        }

        @Test
        public void testTypeCasting() throws Exception {
                assertTrue(TypeFactory.canBeCastTo(Type.BYTE, Type.SHORT));
                assertTrue(TypeFactory.canBeCastTo(Type.SHORT, Type.INT));
                assertTrue(TypeFactory.canBeCastTo(Type.INT, Type.LONG));
                assertTrue(TypeFactory.canBeCastTo(Type.LONG, Type.FLOAT));
                assertTrue(TypeFactory.canBeCastTo(Type.FLOAT, Type.DOUBLE));
                assertTrue(TypeFactory.canBeCastTo(Type.INT, Type.DOUBLE));
                assertTrue(TypeFactory.canBeCastTo(Type.INT, Type.LONG));

                assertTrue(TypeFactory.canBeCastTo(Type.DATE, Type.TIME));
                assertTrue(TypeFactory.canBeCastTo(Type.TIME, Type.TIMESTAMP));
        }

        private void checkUniqueness() throws DriverException {
                DataSource ds = getDataSource();
                ds.open();
                ds.insertFilledRow(new Value[]{ValueFactory.createValue(2)});
                try {
                        ds.insertFilledRow(new Value[]{ValueFactory.createValue(2)});
                        fail();
                } catch (DriverException e) {
                }
                ds.insertFilledRow(new Value[]{ValueFactory.createValue(3)});
                try {
                        ds.setFieldValue(ds.getRowCount() - 1, 0, ValueFactory.createValue(2));
                        fail();
                } catch (DriverException e) {
                }
        }

        private List<Value> getInvalidValuesGeom(){
                LinkedList<Value> ll = new LinkedList<Value>();
                ll.add(binaryValue);
                ll.add(booleanValue);
                ll.add(byteValue);
                ll.add(dateValue);
                ll.add(doubleValue);
                ll.add(floatValue);
                ll.add(intValue);
                ll.add(longValue);
                ll.add(shortValue);
                ll.add(stringValue);
                ll.add(timeValue);
                ll.add(timestampValue);
                ll.add(collectionValue);
                return ll;
        }

        private void setInvalidValuesGeom(Value... values){
                List<Value> ll = getInvalidValuesGeom();
                List<Value> plus = Arrays.asList(values);
                ll.addAll(plus);
                invalidValues = ll.toArray(new Value[0]);
        }

        private void setValidValues(Value... values) {
                this.validValues = values;
        }

        private void setInvalidValues(Value... values) {
                this.invalidValues = values;
        }

        private void setType(Type type) {
                this.type = type;
        }

        /**
         * Given the set of valid and invalid values, this method checks that 
         * they can (or can not) be inserted in the datasource.
         * @throws Exception 
         */
        private void doEdition() throws Exception {
                DataSource dataSource = getDataSource();
                dataSource.open();
                for (Value value : validValues) {
                        dataSource.insertFilledRow(new Value[]{value});
                        dataSource.setFieldValue(dataSource.getRowCount() - 1, 0, value);
                        assertNull(dataSource.check(0, value));
                }
                for (Value value : invalidValues) {
                        try {
                                assertTrue(dataSource.check(0, value) != null);
                                dataSource.insertFilledRow(new Value[]{value});
                                fail();
                        } catch (DriverException e) {
                        } catch (IncompatibleTypesException e) {
                        }
                        try {
                                assertTrue(dataSource.check(0, value) != null);
                                dataSource.setFieldValue(0, 0, value);
                                fail();
                        } catch (DriverException e) {
                        } catch (IncompatibleTypesException e) {
                        }
                }
                dataSource.commit();
                dataSource.close();
        }

        private DataSource getDataSource() throws DriverException {
                MemoryDataSetDriver omd = new MemoryDataSetDriver(
                        new String[]{"string"}, new Type[]{type});
                DataSource dataSource = dsf.getDataSource(omd, "main");
                return dataSource;
        }
}
