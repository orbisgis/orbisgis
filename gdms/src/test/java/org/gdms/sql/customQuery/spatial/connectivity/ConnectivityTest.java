/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *
 * Copyright (C) 2007-2008 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
 * Copyright (C) 2011 Erwan BOCHER, Antoine GOURLAY, Alexis GUEGANNO, Maxence LAURENT, Gwendall PETIT
 *
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
package org.gdms.sql.customQuery.spatial.connectivity;

import com.vividsolutions.jts.io.WKTReader;
import junit.framework.TestCase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.ObjectDriver;
import org.gdms.driver.generic.GenericObjectDriver;
import org.gdms.sql.customQuery.spatial.geometry.connectivity.ST_BlockIdentity;

public class ConnectivityTest extends TestCase {

        private static DataSourceFactory dsf = new DataSourceFactory();

        public void testST_BlockIdentity() throws Exception {

                WKTReader wktr = new WKTReader();
                // first datasource
                final GenericObjectDriver driver1 = new GenericObjectDriver(
                        new String[]{"the_geom", "id", "block_id"},
                        new Type[]{TypeFactory.createType(Type.GEOMETRY),
                                TypeFactory.createType(Type.INT),
                                TypeFactory.createType(Type.INT)
                        });

                // insert all filled rows...
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 80 223, 80 300, 156 300, 156 223, 80 223 ))")),
                                ValueFactory.createValue(1), ValueFactory.createValue(1)});
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 156 276.28712871287127, 239 273, 216 221, 145 207, 143.4 223, 156 223, 156 276.28712871287127 ))")),
                                ValueFactory.createValue(2), ValueFactory.createValue(1)});
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 189.5695615514334 215.78836424957842, 182 130, 114 192, 118.82222222222222 223, 143.4 223, 145 207, 189.5695615514334 215.78836424957842 ))")),
                                ValueFactory.createValue(3), ValueFactory.createValue(1)});
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 289 154, 289 195, 330 195, 330 154, 289 154 ))")),
                                ValueFactory.createValue(4), ValueFactory.createValue(2)});
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 330 180.52631578947367, 373 176, 340 115, 320 148, 319.29411764705884 154, 330 154, 330 180.52631578947367 ))")),
                                ValueFactory.createValue(5), ValueFactory.createValue(2)});
                driver1.addValues(new Value[]{ValueFactory.createValue(wktr.read("POLYGON (( 252 39, 252 98, 356 98, 356 39, 252 39 ))")),
                                ValueFactory.createValue(6), ValueFactory.createValue(3)});

                ST_BlockIdentity blockIdentity = new ST_BlockIdentity();
                DataSource[] tables = new DataSource[]{dsf.getDataSource(driver1)};
                ObjectDriver evaluate = blockIdentity.evaluate(dsf, tables, new Value[]{ValueFactory.createValue("the_geom")}, null);

                assertTrue(evaluate.getRowCount() == driver1.getRowCount());

                for (int i = 0; i < driver1.getRowCount(); i++) {
                        assertTrue(driver1.getFieldValue(i, 2).getAsInt() == evaluate.getFieldValue(i, 1).getAsInt());
                }


        }
}
