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
package org.gdms.sql.function;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.gdms.TestBase;
import org.gdms.data.DataSource;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Antoine Gourlay, Erwan Bocher
 */
public class CRSFunctionTest extends TestBase {
        
        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
        }
        
        
        //TODO : this test must be fixed in the future
        //@Test
        public void testSetCRSdata() throws Exception {
                dsf.executeSQL("CREATE TABLE init AS SELECT * FROM ST_RandomGeometry('point', 2);");
                DataSource  ds = dsf.getDataSourceFromSQL("SELECT ST_SetSRID(the_geom, 'EPSG:27572') from init;");
                ds.open();
                assertTrue(ds.getCRS().getName().toString().equalsIgnoreCase("EPSG:27572"));                
                ds.close();
        }
        
        @Test
        public void testGetSetCRSOntheFly() throws Exception {
                dsf.executeSQL("CREATE TABLE init AS SELECT * FROM ST_RandomGeometry('point', 10);");
                DataSource ds = dsf.getDataSourceFromSQL("SELECT ST_SRID(the_geom) from init;");
                
                ds.open();
                assertTrue(ds.isNull(0, 0));
                ds.close();
                
                ds = dsf.getDataSourceFromSQL("SELECT ST_SRID(ST_SetSRID(the_geom, 'EPSG:27572')) from init;");
                
                ds.open();
                for (int i = 0; i < ds.getRowCount(); i++) {
                        assertTrue( ds.getString(i, 0).contains("EPSG:27572"));
                }
                ds.close();
        }
        
        
        @Test
        public void testST_Transform() throws Exception {
                dsf.executeSQL("CREATE TABLE init AS SELECT 'POINT(584173.736059813 2594514.82833411)'::GEOMETRY as the_geom;");
                DataSource ds = dsf.getDataSourceFromSQL("SELECT *  from init;");
                //EPSG:27572;584173.736059813;2594514.82833411;EPSG:4326;;0.01
                
                WKTReader wKTReader = new WKTReader();
                Geometry targetGeom = wKTReader.read("POINT(2.114551393 50.345609791)");
                ds.open();
                assertTrue(!ds.isNull(0, 0));
                ds.close();
                
                ds = dsf.getDataSourceFromSQL("SELECT ST_TRANSFORM(ST_SetSRID(the_geom, 'EPSG:27572'), 'EPSG:4326') from init;");
                
                ds.open();
                
                assertTrue(ds.getGeometry(0).equalsExact(targetGeom, 0.01));
                
                ds.close();
        }
}
