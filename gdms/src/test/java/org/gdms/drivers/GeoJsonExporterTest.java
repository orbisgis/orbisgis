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
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
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
import java.io.FileReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonFactory.Feature;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import org.gdms.TestResourceHandler;
import static org.junit.Assert.*;

import org.gdms.TestBase;
import org.gdms.driver.geojson.GeoJsonExporter;

/**
 *
 * @author Antoine Gourlay
 */
public class GeoJsonExporterTest extends TestBase {

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithEdition(false);
        }

        @Test
        public void testSimpleExport() throws Exception {
                sm.getDriverManager().registerExporter(GeoJsonExporter.class);

                dsf.executeSQL("CREATE TABLE toto AS SELECT ST_GeomFromText('POINT(1 2 3)') AS the_geom, 42 AS id;");
                File f = File.createTempFile("gdms", ".json");
                sm.exportTo("toto", f);
                
                String s = new String(FileUtils.readFileToByteArray(f));
                String comp = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\","
                        + "\"geometry\":{\"type\":\"Point\",\"coordinates\":[1.0,2.0,3.0]},"
                        + "\"properties\":{\"id\":42}}]}";
                assertEquals(comp, s);
                
                f.delete();
        }
}
