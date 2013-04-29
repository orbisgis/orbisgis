/*
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
package com.vividsolutions.wms;

import java.io.File;
import java.io.FileInputStream;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Alexis Guéganno - FR CNRS 2488
 */
public abstract class AbstractWMSTest {
    public static final String CAPABILITIES_1_3_0 = "src/test/resources/com/vividsolutions/wms/capabilities_1_3_0.xml";

    /**
     * Gets the DOM Document associated to the xml capabilities answer stored at
     * the given path.
     * @param path
     * @return
     * @throws Exception
     */
    public Document getDocument(String path) throws Exception{
        File f = new File(CAPABILITIES_1_3_0);
        FileInputStream fis = new FileInputStream(f);
        DOMParser domParser = new DOMParser();
        domParser.setFeature("http://xml.org/sax/features/validation", false);
        domParser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        domParser.parse(new InputSource(fis));
        return domParser.getDocument();
    }

}
