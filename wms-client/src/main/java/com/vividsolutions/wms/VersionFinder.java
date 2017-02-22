/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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

import com.vividsolutions.wms.util.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Finds the actual version of a WMS GetCapabilities Document.
 * @author Alexis Guéganno - FR CNRS 2488
 */
public class VersionFinder {

    public VersionFinder() {
    }

    /**
     * Parses the WMT_MS_Capabilities XML from the given InputStream into a
     * Capabilities object.
     *
     * @param doc the document containing the WMT_MS_Capabilities XML to
     * parse
     * @return the MapDescriptor object created from the specified XML
     * InputStream
     */
    public static String findVersion(Document doc) {
        String foundVersion = WMService.WMS_1_1_1;
        Node simpleXPath = XMLTools.simpleXPath(doc, "WMT_MS_Capabilities");
        if (simpleXPath != null) {
            NamedNodeMap attributes = simpleXPath.getAttributes();
            Node namedItem = attributes.getNamedItem("version");
            if (WMService.WMS_1_1_1.equals(namedItem.getNodeValue())
                    || WMService.WMS_1_1_0.equals(namedItem.getNodeValue())) {
                foundVersion = WMService.WMS_1_1_1;
            } else {
                foundVersion = WMService.WMS_1_0_0;
            }
        }
        simpleXPath = XMLTools.simpleXPath(doc, "WMS_Capabilities");
        if (simpleXPath != null) {
            NamedNodeMap attributes = simpleXPath.getAttributes();
            Node namedItem = attributes.getNamedItem("version");
            if (WMService.WMS_1_3_0.equals(namedItem.getNodeValue())) {
                foundVersion = WMService.WMS_1_3_0;
            }
        }
        return foundVersion;
    }

}
