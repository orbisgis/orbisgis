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

// Changed by Uwe Dalluege, uwe.dalluege@rzcn.haw-hamburg.de
// to differ between LatLonBoundingBox and BoundingBox
// 2005-08-09

package com.vividsolutions.wms;

import java.io.IOException;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.vividsolutions.wms.util.XMLTools;


/**
 * Pulls WMS objects out of the XML
 * @author Chris Hodgson chodgson@refractions.net
 * @author Michael Michaud michael.michaud@free.fr
 */
public class ParserWMS1_1 extends AbstractParser {
    
    
   /** 
    * Creates a Parser for dealing with WMS XML.
    */
    public ParserWMS1_1() {}
    
    
    protected String getRootPath() {
        return "WMT_MS_Capabilities";
    }
    
    
    public Capabilities parseCapabilities(WMService service, Document doc) throws IOException {
        String title = getTitle(doc);
        MapLayer topLayer = wmsLayerFromNode(XMLTools.simpleXPath(doc, "WMT_MS_Capabilities/Capability/Layer"));
        LinkedList<String> formatList = getFormatList(doc);
        String getMapURL = getMapURL(doc);
        String getFeatureInfoURL = getFeatureInfoURL(doc);
        return new Capabilities(service, title, topLayer, formatList, getMapURL, getFeatureInfoURL );
    }
    
    
    // From WMS 1.1.x
    protected String getMapURL(Document doc) {
        final Node getMapNode = XMLTools.simpleXPath(doc, "WMT_MS_Capabilities/Capability/Request/GetMap");
        String xp = "DCPType/HTTP/Get/OnlineResource";
        String xlink = "http://www.w3.org/1999/xlink";
        Element e = (Element) XMLTools.simpleXPath(getMapNode, xp);
        return e.getAttributeNS(xlink, "href");
    }
    
    
    // From WMS 1.1.x
    protected String getFeatureInfoURL(Document doc) {
        String xp = "WMT_MS_Capabilities/Capability/Request/GetFeatureInfo/DCPType/HTTP/Get/OnlineResource";
        String xlink = "http://www.w3.org/1999/xlink";
        Element e = (Element) XMLTools.simpleXPath(doc, xp);
        return e == null ? "" : e.getAttributeNS(xlink, "href");
    }

    
    protected String getSRSName() {
        return "SRS";
    }
  
}
