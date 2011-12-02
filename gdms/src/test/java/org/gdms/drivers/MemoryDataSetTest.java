/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Adelin PIAU, Gwendall PETIT
 *
 * Copyright (C) 2010 Erwan BOCHER,  Alexis GUEGANNO, Antoine GOURLAY, Gwendall PETIT
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.gdms.drivers;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Test;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.DriverUtilities;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.junit.Before;

import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class MemoryDataSetTest {

        private WKTReader wktReader;

        @Before
        public void setUp() throws Exception {
                wktReader = new WKTReader();
        }

        @Test
        public void testFullExtent() throws Exception {
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});

                assertTrue(driver1.getFullExtent().equals(polygon.getEnvelopeInternal()));
        }

        @Test
        public void testFullExtent2() throws Exception {
                Polygon polygon = (Polygon) wktReader.read("POLYGON((0 0, 1 0, 1 0, 2 10, 0 0 ))");

                // first datasource
                final MemoryDataSetDriver driver1 = new MemoryDataSetDriver(
                        new String[]{"the_geom"},
                        new Type[]{TypeFactory.createType(Type.POLYGON)});
                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(polygon)});


                DataSet[] tables = new DataSet[]{driver1};

                assertTrue(DriverUtilities.getFullExtent(tables[0]).equals(polygon.getEnvelopeInternal()));
        }
}
