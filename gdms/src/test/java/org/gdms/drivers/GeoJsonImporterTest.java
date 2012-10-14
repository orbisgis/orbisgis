/*
 * The GDMS library (Generic Datasources Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 * 
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2012 IRSTV FR CNRS 2488
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
package org.gdms.drivers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import org.gdms.driver.geojson.GeoJsonImporter;
import org.junit.Test;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Type;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.driver.geojson.DummyParser;
import org.gdms.driver.io.RowWriter;

/**
 *
 * @author Antoine Gourlay
 */
public class GeoJsonImporterTest {

        private static final GeometryFactory GF = new GeometryFactory();

        static {
                TestResourceHandler.init();
        }
        
        private JsonFactory createFactory() {
                return new JsonFactory().configure(Feature.ALLOW_COMMENTS, true).
                        configure(Feature.ALLOW_SINGLE_QUOTES, true).
                        configure(Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        }

        @Test
        public void testCoordinateParsing() throws Exception {
                JsonFactory f = createFactory();
                JsonParser jp = f.createJsonParser("{\"a\": [0.12, 1.42, 2.7]}");
                jp.nextToken(); // start object
                jp.nextToken(); // field name
                jp.nextToken(); // array
                DummyParser p = new DummyParser();
                Coordinate c = p.parseCoordinate(jp);

                assertTrue(new Coordinate(0.12, 1.42, 2.7).equals3D(c));

                jp = f.createJsonParser("{\"a\": [0.12, 1.42]}");
                jp.nextToken(); // start object
                jp.nextToken(); // field name
                jp.nextToken(); // array
                c = p.parseCoordinate(jp);

                assertTrue(new Coordinate(0.12, 1.42).equals2D(c));
        }

        private List<Value[]> parseFile(String fileName) throws IOException {
                JsonFactory f = createFactory();
                final File file = new File(TestResourceHandler.TESTRESOURCES, fileName);
                JsonParser jp = f.createJsonParser(file);

                final List<Value[]> vals = new ArrayList<Value[]>();
                DummyParser p = new DummyParser();
                Metadata met = p.metadata(jp);

                jp = f.createJsonParser(file);
                p.parse(jp, met, new RowWriter() {

                        @Override
                        public void addValues(Value[] row) throws DriverException {
                                vals.add(row);
                        }
                });

                return vals;
        }

        @Test
        public void testPointParsing() throws Exception {
                final List<Value[]> vals = parseFile("point.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createPoint(new Coordinate(102.0, 0.5, 42)).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testMultiPointParsing() throws Exception {
                final List<Value[]> vals = parseFile("multipoint.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createMultiPoint(
                        new Coordinate[]{
                                new Coordinate(102.0, 0.5, 42),
                                new Coordinate(-102.0, 0.5, 42)
                        }).equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testLineStringParsing() throws Exception {
                final List<Value[]> vals = parseFile("linestring.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createLineString(
                        new Coordinate[]{
                                new Coordinate(102.0, 0.5),
                                new Coordinate(103.0, 1),
                                new Coordinate(105.0, 2)
                        }).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testMultiLineStringParsing() throws Exception {
                final List<Value[]> vals = parseFile("multilinestring.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createMultiLineString(new LineString[]{
                                GF.createLineString(
                                new Coordinate[]{
                                        new Coordinate(102.0, 0.5),
                                        new Coordinate(103.0, 1),
                                        new Coordinate(105.0, 2)
                                }),
                                GF.createLineString(
                                new Coordinate[]{
                                        new Coordinate(102.0, 2.5),
                                        new Coordinate(103.0, 3),
                                        new Coordinate(105.0, 4)
                                })
                        }).equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testPolygonParsing() throws Exception {
                final List<Value[]> vals = parseFile("polygon.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createPolygon(
                        GF.createLinearRing(new Coordinate[]{
                                new Coordinate(100, 0),
                                new Coordinate(101, 0),
                                new Coordinate(101, 1),
                                new Coordinate(100, 1),
                                new Coordinate(100, 0),}), null).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testMultiPolygonParsing() throws Exception {
                final List<Value[]> vals = parseFile("multipolygon.json");

                assertEquals(1, vals.size());
                assertEquals(1, vals.get(0).length);
                assertTrue(GF.createMultiPolygon(new Polygon[]{
                                GF.createPolygon(
                                GF.createLinearRing(new Coordinate[]{
                                        new Coordinate(100, 0),
                                        new Coordinate(101, 0),
                                        new Coordinate(101, 1),
                                        new Coordinate(100, 1),
                                        new Coordinate(100, 0),}), null),
                                GF.createPolygon(
                                GF.createLinearRing(new Coordinate[]{
                                        new Coordinate(200, 0),
                                        new Coordinate(201, 0),
                                        new Coordinate(201, 1),
                                        new Coordinate(200, 1),
                                        new Coordinate(200, 0),}),
                                new LinearRing[]{
                                        GF.createLinearRing(new Coordinate[]{
                                                new Coordinate(200.5, 0),
                                                new Coordinate(200.9, 0),
                                                new Coordinate(200, 0.5),
                                                new Coordinate(200.5, 0)
                                        })}),}).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testFeatureParsing() throws Exception {
                final List<Value[]> vals = parseFile("feature.json");

                assertEquals(1, vals.size());
                assertEquals(3, vals.get(0).length);
                assertTrue(GF.createLineString(new Coordinate[]{
                                new Coordinate(100, 0),
                                new Coordinate(101, 0),
                                new Coordinate(101, 1),
                                new Coordinate(100, 1),}).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
                assertEquals(42, vals.get(0)[1].getAsInt());
                assertEquals("Toto", vals.get(0)[2].getAsString());
        }

        @Test
        public void testFeatureCollectionParsing() throws Exception {
                final List<Value[]> vals = parseFile("featurecollection.json");

                assertEquals(3, vals.size());
                for (Value[] v : vals) {
                        assertEquals(3, v.length);
                }

                assertTrue(GF.createPoint(new Coordinate(102, 0.5)).
                        equalsExact(vals.get(0)[0].getAsGeometry()));
                assertEquals(41, vals.get(0)[1].getAsInt());
                assertTrue(vals.get(0)[2].isNull());
                assertTrue(GF.createLineString(new Coordinate[]{
                                new Coordinate(102, 0),
                                new Coordinate(103, 1),
                                new Coordinate(104, 0),
                                new Coordinate(105, 1),}).equalsExact(vals.get(1)[0].getAsGeometry()));
                assertEquals(42, vals.get(1)[1].getAsInt());
                assertEquals("Toto", vals.get(1)[2].getAsString());
                assertTrue(GF.createPolygon(
                        GF.createLinearRing(new Coordinate[]{
                                new Coordinate(100, 0),
                                new Coordinate(101, 0),
                                new Coordinate(101, 1),
                                new Coordinate(100, 1),
                                new Coordinate(100, 0),}), null).
                        equalsExact(vals.get(2)[0].getAsGeometry()));
                assertEquals(43, vals.get(2)[1].getAsInt());
                assertEquals("Tutu", vals.get(2)[2].getAsString());
        }

        @Test
        public void testGeometryCollectionParsing() throws Exception {
                final List<Value[]> vals = parseFile("geometrycollection.json");

                assertEquals(1, vals.size());
                assertEquals(Type.GEOMETRYCOLLECTION, vals.get(0)[0].getType());

                GeometryCollection col = GF.createGeometryCollection(new Geometry[]{
                                GF.createPoint(new Coordinate(102, 0.5)),
                                GF.createLineString(new Coordinate[]{
                                        new Coordinate(102, 0),
                                        new Coordinate(103, 1),
                                        new Coordinate(104, 0),
                                        new Coordinate(105, 1),}),
                                GF.createPolygon(
                                GF.createLinearRing(new Coordinate[]{
                                        new Coordinate(100, 0),
                                        new Coordinate(101, 0),
                                        new Coordinate(101, 1),
                                        new Coordinate(100, 1),
                                        new Coordinate(100, 0),}), null)
                        });

                assertTrue(col.equalsExact(vals.get(0)[0].getAsGeometry()));
        }

        @Test
        public void testMetadata() throws Exception {
                JsonFactory f = new JsonFactory();
                JsonParser jp = f.createJsonParser(new File(TestResourceHandler.TESTRESOURCES, "featurecollection.json"));
                DummyParser p = new DummyParser();

                Metadata met = p.metadata(jp);
                assertEquals(3, met.getFieldCount());
                assertEquals("the_geom", met.getFieldName(0));
                assertEquals(Type.GEOMETRY, met.getFieldType(0).getTypeCode());
                assertEquals("gid", met.getFieldName(1));
                assertEquals(Type.INT, met.getFieldType(1).getTypeCode());
                assertEquals("name", met.getFieldName(2));
                assertEquals(Type.STRING, met.getFieldType(2).getTypeCode());
        }

        @Test
        public void testProbFile() throws Exception {
                JsonFactory f = new JsonFactory();
                JsonParser jp = f.createJsonParser(new File("/home/alexis/gitProjects/orbisgis-irstv/gdms/src/test/resources/org/gdms/drivers/Metadata-order.json"));
                DummyParser p = new DummyParser();
                Metadata met = p.metadata(jp);
                checkType(met, "the_geom", Type.POINT);
                checkType(met, "ID", Type.STRING);
                checkType(met, "ID_COM", Type.STRING);
                checkType(met, "ORIGIN_NOM", Type.STRING);
                checkType(met, "NATURE", Type.STRING);
                checkType(met, "NOM", Type.STRING);
                checkType(met, "IMPORTANCE", Type.STRING);
                assertTrue(true);
        }

        private void checkType(Metadata m, String name, int type) throws Exception{
                int i = m.getFieldIndex(name);
                assertTrue(m.getFieldType(i).getTypeCode() == type);
        }
}
