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


import com.vividsolutions.wms.util.XMLTools;


/**
 * Pulls WMS objects out of the XML
 * @author Chris Hodgson chodgson@refractions.net
 * @author Michael Michaud michael.michaud@free.fr
 */
public class ParserWMS1_0 extends AbstractParser {
    
   
   /** 
    * Creates a Parser for dealing with WMS XML.
    */
    public ParserWMS1_0() {}
    
    protected String getRootPath() {
        return "WMT_MS_Capabilities";
    }
  
    
    public Capabilities parseCapabilities(WMService service, Document doc) throws IOException {
        String title = getTitle(doc);
        MapLayer topLayer = wmsLayerFromNode(XMLTools.simpleXPath(doc, "WMT_MS_Capabilities/Capability/Layer"));
        LinkedList<String> formatList = getFormatList(doc);
        return new Capabilities(service, title, topLayer, formatList);
    }

    
    protected String getSRSName() {
        return "SRS";
    }
  
}
