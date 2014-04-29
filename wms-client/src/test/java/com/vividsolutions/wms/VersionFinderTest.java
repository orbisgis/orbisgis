/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. 
 * 
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 * 
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package com.vividsolutions.wms;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests for the VersionFinder class.
 * @author Alexis Guéganno - IRSTV FR CNRS 2488
 */
public class VersionFinderTest extends AbstractWMSTest {

    /**
     * Let's check that even if preferred version is not 1.3.0, we still get
     * 1.3.0 in the end.
     * @throws Exception 
     */
    @Test
    public void testFindVersion_1_3_0() throws Exception {
        Document doc = getDocument(CAPABILITIES_1_3_0);
        String vers = VersionFinder.findVersion(doc);
        assertTrue(WMService.WMS_1_3_0.equals(vers));
    }

}
