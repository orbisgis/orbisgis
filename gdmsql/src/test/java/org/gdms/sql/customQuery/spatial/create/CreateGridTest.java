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
package org.gdms.sql.customQuery.spatial.create;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.gdms.data.AlreadyClosedException;
import org.gdms.data.DataSource;
import org.gdms.data.values.Value;
import org.gdms.driver.DriverException;
import org.gdms.sql.FunctionTest;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import static org.junit.Assert.*;

public class CreateGridTest extends FunctionTest {

        @Before
        public void setUp() throws Exception {
                super.setUp();
        }

        @After
        public void tearDown() throws Exception {
                if (dsf.getSourceManager().exists("ds1ppp")) {
                        dsf.getSourceManager().remove("ds1ppp");
                }
                if (dsf.getSourceManager().exists("ds1pp")) {
                        dsf.getSourceManager().remove("ds1pp");
                }
                if (dsf.getSourceManager().exists("ds1p")) {
                        dsf.getSourceManager().remove("ds1p");
                }
        }

        private void check(final DataSource dataSource, final boolean checkCentroid)
                throws AlreadyClosedException, DriverException {
                dataSource.open();
                final long rowCount = dataSource.getRowCount();
                final int fieldCount = dataSource.getFieldCount();
                assertEquals(4, rowCount);
                assertEquals(2, fieldCount);
                for (long rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                        final Value[] fields = dataSource.getRow(rowIndex);
                        final Geometry geom = fields[0].getAsGeometry();
                        final int id = fields[1].getAsInt();
                        assertTrue(geom instanceof Polygon);
                        assertTrue(Math.abs(1 - geom.getArea()) < 0.000001);
                        assertEquals(4, geom.getLength(), 0);
                        assertEquals(5, geom.getNumPoints());
                        if (checkCentroid) {
                                assertEquals(0.5 + (id - 1) / 2,  geom.getCentroid().getCoordinate().x, 0);
                                assertEquals(0.5 + (id - 1) % 2,  geom.getCentroid().getCoordinate().y, 0);
                        }

                        for (int fieldIndex = 0; fieldIndex < fieldCount; fieldIndex++) {
                                System.out.print(fields[fieldIndex].toString() + ", ");
                        }
                        System.out.println();
                }
                dataSource.close();
        }

        @Test
        public final void testEvaluate() throws Exception {
                dsf.register("ds1p",
                        "select * from st_creategrid(ds1, 1.0, 1);");
                check(dsf.getDataSource("ds1p"), true);

                dsf.register("ds1pp",
                        "select * from st_creategrid(ds1,1,1,0);");
                check(dsf.getDataSource("ds1pp"), true);

                dsf.register("ds1ppp",
                        "select * from st_creategrid(ds1, 1,1,90);");
                check(dsf.getDataSource("ds1ppp"), false);
        }
}