/**
 * The GDMS library (Generic Datasource Management System)
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
package org.gdms.sql.function.spatial.tin.create;

import com.vividsolutions.jts.geom.Geometry;
import java.util.ArrayList;
import java.util.Iterator;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.FunctionTest;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;

/**
 *
 * @author Erwan Bocher
 */
public class TestCreateTIN extends FunctionTest {

    @Test
    public void ST_TIN_POINTS() throws Exception {
        Geometry geom = wktReader.read("POINT(0 0)");
        Geometry geom2 = wktReader.read("POINT(10 0)");
        Geometry geom3 = wktReader.read("POINT(10 10)");

        // datasource
        final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                new String[]{"the_geom"},
                new Type[]{TypeFactory.createType(Type.POINT)});
        // insert all filled rows...
        driver1.addValues(new Value[]{ValueFactory.createValue(geom)});
        driver1.addValues(new Value[]{ValueFactory.createValue(geom2)});
        driver1.addValues(new Value[]{ValueFactory.createValue(geom3)});

        Value[] values = new Value[]{
            ValueFactory.createValue(false), ValueFactory.createValue(false)};

        DataSet[] tables = new DataSet[]{driver1};

        ST_TIN st_tin = new ST_TIN();
        DataSet result = st_tin.evaluate(dsf, tables, values, new NullProgressMonitor());

        assertTrue(result.getRowCount() == 1);

        assertTrue(result.getGeometry(0, 0).equals(wktReader.read("POLYGON((0 0, 10 0, 10 10, 0 0))")));
    }

    @Test
    public void ST_TIN_LINES() throws Exception {
        Geometry geom = wktReader.read("LINESTRING(0 0, 10 0)");
        Geometry geom2 = wktReader.read("LINESTRING(0 5, 10 10)");

        // datasource
        final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                new String[]{"the_geom"},
                new Type[]{TypeFactory.createType(Type.LINESTRING)});
        // insert all filled rows...
        driver1.addValues(new Value[]{ValueFactory.createValue(geom)});
        driver1.addValues(new Value[]{ValueFactory.createValue(geom2)});

        Value[] values = new Value[]{
            ValueFactory.createValue(false), ValueFactory.createValue(false)};

        DataSet[] tables = new DataSet[]{driver1};

        ST_TIN st_tin = new ST_TIN();
        DataSet result = st_tin.evaluate(dsf, tables, values, new NullProgressMonitor());

        assertTrue(result.getRowCount() == 2);

        ArrayList<Geometry> geometries = new ArrayList<Geometry>();
        geometries.add(wktReader.read("POLYGON((0 5, 0 0, 10 0, 0 5))"));
        geometries.add(wktReader.read("POLYGON((10 0, 0 5, 10 10, 10 0))"));
        assertTrue(checkResult(result, geometries));
    }

    @Test
    public void ST_TIN_LINES_INTERSECTION() throws Exception {
        Geometry geom = wktReader.read("LINESTRING(0 10 , 10 10)");
        Geometry geom2 = wktReader.read("LINESTRING(3 0, 3 20)");

        // datasource
        final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                new String[]{"the_geom"},
                new Type[]{TypeFactory.createType(Type.LINESTRING)});
        // insert all filled rows...
        driver1.addValues(new Value[]{ValueFactory.createValue(geom)});
        driver1.addValues(new Value[]{ValueFactory.createValue(geom2)});

        Value[] values = new Value[]{
            ValueFactory.createValue(true), ValueFactory.createValue(false)};

        DataSet[] tables = new DataSet[]{driver1};

        ST_TIN st_tin = new ST_TIN();
        DataSet result = st_tin.evaluate(dsf, tables, values, new NullProgressMonitor());

        assertTrue(result.getRowCount() == 4);

        ArrayList<Geometry> geometries = new ArrayList<Geometry>();
        geometries.add(wktReader.read("POLYGON ((0 10, 3 0, 3 10, 0 10))"));
        geometries.add(wktReader.read("POLYGON ((3 10, 0 10, 3 20, 3 10))"));
        geometries.add(wktReader.read("POLYGON ((3 0, 3 10, 10 10, 3 0))"));
        geometries.add(wktReader.read("POLYGON ((3 10, 3 20, 10 10, 3 10))"));
        assertTrue(checkResult(result, geometries));
    }

    public boolean checkResult(DataSet dataSet, ArrayList<Geometry> geometries) {
        int inputContains = geometries.size();

        Iterator<Value[]> it = dataSet.iterator();
        int contains = 0;
        while (it.hasNext()) {
            Value[] values = it.next();
            Geometry geom = values[0].getAsGeometry();
            if (geometries.contains(geom)) {
                contains++;
            }
        }
        return contains == inputContains ? true : false;
    }
}
