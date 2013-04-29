/*
 * The Unified Mapping Platform (JUMP) is an extensible, interactive GUI 
 * for visualizing and manipulating spatial features with geometry and attributes.
 *
 * Copyright (C) 2003 Vivid Solutions
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * For more information, contact:
 *
 * Vivid Solutions
 * Suite #1A
 * 2328 Government Street
 * Victoria BC  V8T 5G5
 * Canada
 *
 * (250)385-6040
 * www.vividsolutions.com
 */

// Changed by Uwe Dalluege, uwe.dalluege@rzcn.haw-hamburg.de
// to differ between LatLonBoundingBox and BoundingBox
// 2005-08-09

package com.vividsolutions.wms;

import java.io.IOException;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vividsolutions.wms.util.XMLTools;


/**
 * Pulls WMS objects out of the XML
 * @author Chris Hodgson chodgson@refractions.net
 * @author Michael Michaud michael.michaud@free.fr
 */
public class ParserWMS1_3 extends AbstractParser {
    
    
   /** 
    * Creates a Parser for dealing with WMS XML.
    */
    public ParserWMS1_3() {}
    
    
    protected String getRootPath() {
        return "WMS_Capabilities";
    }
    
    
    public Capabilities parseCapabilities(WMService service, Document doc) throws IOException {
        String title = getTitle(doc);
        MapLayer topLayer = wmsLayerFromNode(XMLTools.simpleXPath(doc, "WMS_Capabilities/Capability/Layer"));
        LinkedList<String> formatList = getFormatList(doc);
        String getMapURL = getMapURL(doc);
        String getFeatureInfoURL = getFeatureInfoURL(doc);
        return new Capabilities(service, title, topLayer, formatList, getMapURL, getFeatureInfoURL );
    }
    
    
    // get the supported file formats
    protected LinkedList<String> getFormatList(Document doc) throws IOException {
        
        LinkedList<String> formatList = new LinkedList<String>();
        final Node formatNode = XMLTools.simpleXPath(doc, "WMS_Capabilities/Capability/Request/GetMap");
        NodeList nl = formatNode.getChildNodes();
        for( int i=0; i < nl.getLength(); i++ ) {
            Node n = nl.item( i );
            if(n.getNodeType() == Node.ELEMENT_NODE && "Format".equals(n.getNodeName())) {
                String format = n.getFirstChild().getNodeValue();
                if (format.matches("^image/(png|jpeg|gif).*")) {
                    formatList.add(format);
                }
            }
        }
        return formatList;
    }
    
    // From WMS 1.1.x
    protected String getMapURL(Document doc) {
        final Node getMapNode = XMLTools.simpleXPath(doc, "WMS_Capabilities/Capability/Request/GetMap");
        String xp = "DCPType/HTTP/Get/OnlineResource";
        String xlink = "http://www.w3.org/1999/xlink";
        Element e = (Element) XMLTools.simpleXPath(getMapNode, xp);
        return e.getAttributeNS(xlink, "href");
    }
    
    
    // From WMS 1.1.x
    protected String getFeatureInfoURL(Document doc) {
        String xp = "WMS_Capabilities/Capability/Request/GetFeatureInfo/DCPType/HTTP/Get/OnlineResource";
        String xlink = "http://www.w3.org/1999/xlink";
        Element e = (Element) XMLTools.simpleXPath(doc, xp);
        return e == null ? "" : e.getAttributeNS(xlink, "href");
    }
    
    protected BoundingBox boundingBoxFromNode(Node n) throws Exception {
        try {
            NamedNodeMap nm = n.getAttributes();           
            String srs = "";
            srs = nm.getNamedItem(getSRSName()).getNodeValue();
            AxisOrder order = AxisOrder.getAxisOrder(srs.toUpperCase());
            if (order == AxisOrder.LATLON) {
                double miny = getCoord("minx", nm);
			    double minx = getCoord("miny", nm);
			    double maxy = getCoord("maxx", nm);
			    double maxx = getCoord("maxy", nm);
			    return new BoundingBox(srs, minx, miny, maxx, maxy);
			}
			else {
                double minx = getCoord("minx", nm);
			    double miny = getCoord("miny", nm);
			    double maxx = getCoord("maxx", nm);
			    double maxy = getCoord("maxy", nm);
			    return new BoundingBox(srs, minx, miny, maxx, maxy);
			}
        } catch( Exception e ) {
            // possible NullPointerException from getNamedItem returning a null
            // also possible NumberFormatException
            e.printStackTrace();
            throw new Exception("Invalid bounding box element node: " + e.toString() );
        }   
    }

    
    protected String getSRSName() {
        return "CRS";
    }
  
}
