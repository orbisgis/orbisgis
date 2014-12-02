/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.utils;

import java.io.File;
import java.net.URI;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author Antoine Gourlay
 */
public class FileUtilsTest {
        
        @Test
        public void testNameForURI() {
                File f = new File("/home/me/toto.shp");
                assertEquals("toto", FileUtils.getNameFromURI(f.toURI()));
                
                URI u = URI.create("http://toto.com/wms?hello=toto&tableName=mytable");
                assertEquals("mytable", FileUtils.getNameFromURI(u));
                
                URI exoticURI = URI.create("pgsql://poulpe.heig-vd.ch:5432/scapdata/g4districts98");
                assertEquals("g4districts98", FileUtils.getNameFromURI(exoticURI));
                
                URI uJDBC = URI.create("postgresql://127.0.0.1:5432/gisdb?user=postgres&password=postgres&schema=gis_schema&table=bat");
                assertEquals("bat", FileUtils.getNameFromURI(uJDBC));
                
                u = URI.create("jdbc://toto.com:4567/mydb?tableName=helloworld");
                assertEquals("helloworld", FileUtils.getNameFromURI(u));

                u = URI.create("jdbc:h2:/home/user/OrbisGIS/database?catalog=&schema=&table=LANDCOVER2000");
                assertEquals("LANDCOVER2000", FileUtils.getNameFromURI(u));

                u = URI.create("../src/test/resources/data/landcover2000.shp");
                assertEquals("landcover2000.shp", FileUtils.getNameFromURI(u));
        }
}
