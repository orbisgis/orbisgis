



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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Pulls WMS objects out of the XML
 * @author Chris Hodgson chodgson@refractions.net
 */
public class Parser {
  private static Logger LOG = Logger.getLogger(Parser.class);
  /**
   * Creates a Parser for dealing with WMS XML.
   */

  public Parser() {
  }

  /**
   * Parses the WMT_MS_Capabilities XML from the given InputStream into
   * a Capabilities object.
   * @param service the WMService from which this MapDescriptor is derived
   * @param inStream the inputStream containing the WMT_MS_Capabilities XML to parse
   * @return the MapDescriptor object created from the specified XML InputStream
   */

  public Capabilities parseCapabilities( WMService service, InputStream inStream ) throws IOException {
      if ( WMService.WMS_1_1_1.equals( service.getVersion() )
                      || WMService.WMS_1_1_0.equals( service.getVersion() ) ){
          return parseCapabilities_1_1_1(service, inStream);
      }

      return parseCapabilities_1_0_0(service, inStream);
  }

  /**
   * Traverses the DOM tree underneath the specified Node and generates
   * a corresponding WMSLayer object tree. The returned WMSLayer will be set to
   * have the specified parent.
   * @param layerNode a DOM Node which is a <layer> XML element
   * @return a WMSLayer with complete subLayer tree that corresponds
   *         to the DOM Node provided
   */
  public MapLayer wmsLayerFromNode( Node layerNode ) {
    String name = null;
    String title = null;
    LinkedList<String> srsList = new LinkedList<String>();
    LinkedList<MapLayer> subLayers = new LinkedList<MapLayer>();
    BoundingBox bbox = null;

// I think, bbox is LatLonBoundingBox.
// I need a new variable for BoundingBox.
// It must be a list because in the OGC document
// stands that Layers may have zero or more <BoundingBox> [uwe dalluege]
//    BoundingBox boundingBox = null;
    ArrayList<BoundingBox> boundingBoxList = new ArrayList<BoundingBox> ( );

    NodeList nl = layerNode.getChildNodes();

    for( int i = 0; i< nl.getLength(); i++ ) {
      Node n = nl.item( i );
      try {

        if( n.getNodeType() == Node.ELEMENT_NODE ) {
          if( n.getNodeName().equals( "Name" ) ) {
            name = ((CharacterData)n.getFirstChild()).getData();
          } else if( n.getNodeName().equals( "Title" ) ) {
            title = ((CharacterData)n.getFirstChild()).getData();

          } else if( n.getNodeName().equals( "SRS" ) ) {
            String srsStr = ((CharacterData)n.getFirstChild()).getData();
            // split the srs String on spaces
            while( srsStr.length() > 0 ) {
              int ws = srsStr.indexOf( ' ' );
              if( ws > 0 ) {
                srsList.add( srsStr.substring( 0, ws ) );
                srsStr = srsStr.substring( ws + 1 );
              } else {
                if( srsStr.length() > 0 ) {
                  srsList.add( srsStr );
                  srsStr = "";
                }
              }
            }
          } else if( n.getNodeName().equals( "LatLonBoundingBox" ) ) {
              bbox = boundingBoxFromNode( n );
              boundingBoxList.add ( bbox );

// Check for BoundingBox [uwe dalluege]
          } else if( n.getNodeName( ).equals( "BoundingBox" ) ) {
              bbox = boundingBoxFromNode( n );
              boundingBoxList.add ( bbox );

          } else if( n.getNodeName().equals( "Layer" ) ) {
            subLayers.add( wmsLayerFromNode( n ) );
          }
        }
      } catch( Exception e ) {
          e.printStackTrace();
        LOG.error( "Exception caught in wmsLayerFromNode(): " + e.toString() );
      }
    }

// call the new constructor with boundingBoxList in MapLayer [uwe dalluege]
    return new MapLayer
    	( name, title, srsList, subLayers, bbox, boundingBoxList );
  }

  /**
   * Creates a new BoundingBox object based on the DOM Node given.
   * @param n the DOM Node to create the Bounding box from, must be either a
   *          LatLonBoundingBox element or a BoundingBox element
   * @return a new BoundingBox object based on the DOM Node provided
   */
  public BoundingBox boundingBoxFromNode( Node n ) throws Exception {
    try {
      String srs = "";
      NamedNodeMap nm = n.getAttributes();

      if( n.getNodeName().equals( "LatLonBoundingBox" ) ) {
        srs = "LatLon";
      } else if( n.getNodeName().equals( "BoundingBox" ) ) {
        srs = nm.getNamedItem( "SRS" ).getNodeValue();
      } else {
          // don't bother...
//        throw new Exception( I18N.get("com.vividsolutions.wms.Parser.not-a-latlon-boundingbox-element") );
      }

      // could not parse when values equal "inf"
			//	double minx = Double.parseDouble(nm.getNamedItem("minx").getNodeValue());
			//	double miny = Double.parseDouble(nm.getNamedItem("miny").getNodeValue());
			//	double maxx = Double.parseDouble(nm.getNamedItem("maxx").getNodeValue());
			//	double maxy = Double.parseDouble(nm.getNamedItem("maxy").getNodeValue());

      // change "inf" values with +/-"Infinity"
      double minx;
      if (nm.getNamedItem("minx").getNodeValue().equals("inf")){
			minx = Double.NEGATIVE_INFINITY;
      } else {
			minx = Double.parseDouble(nm.getNamedItem("minx").getNodeValue());
      }

      double miny;
      if (nm.getNamedItem("miny").getNodeValue().equals("inf")){
			miny = Double.NEGATIVE_INFINITY;
      } else {
			miny = Double.parseDouble(nm.getNamedItem("miny").getNodeValue());
      }
      double maxx;

      if (nm.getNamedItem("maxx").getNodeValue().equals("inf")) {
			maxx = Double.POSITIVE_INFINITY;
      } else {
			maxx = Double.parseDouble(nm.getNamedItem("maxx").getNodeValue());
      }

      double maxy;
      if (nm.getNamedItem("maxy").getNodeValue().equals("inf")) {
			maxy = Double.POSITIVE_INFINITY;
      } else {
			maxy = Double.parseDouble(nm.getNamedItem("maxy").getNodeValue());
      }

      return new BoundingBox( srs, minx, miny, maxx, maxy );

    } catch( Exception e ) {
      // possible NullPointerException from getNamedItem returning a null
      // also possible NumberFormatException
      throw new Exception( "invalid bounding box element node"+": " + e.toString() );
    }
  }
  private Capabilities parseCapabilities_1_0_0( WMService service, InputStream inStream ) throws IOException {
      MapLayer topLayer = null;
      String title = null;
      LinkedList<String> formatList = new LinkedList<String>();
      Document doc;

      try {
        DOMParser parser = new DOMParser();
        parser.setFeature( "http://xml.org/sax/features/validation", false );
        parser.parse( new InputSource( inStream ) );
        doc = parser.getDocument();
        // DEBUG: printNode( doc, "" );
      } catch( SAXException saxe ) {
        throw new IOException( saxe.toString() );
      }

      // get the title
      try {
        title = ((CharacterData)simpleXPath( doc, "WMT_MS_Capabilities/Service/Title" ).getFirstChild()).getData();
      } catch (Exception e) {
        // possible NullPointerException if there is no firstChild()
        // also possible miscast causing an Exception

          // 	[uwe dalluege]
          throw new IOException( "Maybe wrong Capabilities Version! " );
      }

      // get the supported file formats
      Node formatNode = simpleXPath( doc, "WMT_MS_Capabilities/Capability/Request/Map/Format" );
      NodeList nl = formatNode.getChildNodes();
      for( int i=0; i < nl.getLength(); i++ ) {
        Node n = nl.item( i );
        if( n.getNodeType() == Node.ELEMENT_NODE ) {
          formatList.add( n.getNodeName() );
        }
      }

      // get the top layer
      topLayer = wmsLayerFromNode( simpleXPath( doc, "WMT_MS_Capabilities/Capability/Layer" ) );

      return new Capabilities( service, title, topLayer, formatList );
    }

  //UT TODO move this into a common method (
  // private Capabilities parseCapabilities( WMService service, InputStream inStream,
  	// String version)

  private Capabilities parseCapabilities_1_1_1( WMService service, InputStream inStream ) throws IOException {
      MapLayer topLayer = null;
      String title = null;
      String getMapURL, getFeatureInfoURL;
      LinkedList<String> formatList = new LinkedList<String>();
      Document doc;

      try {
          DOMParser parser = new DOMParser();
          parser.setFeature( "http://xml.org/sax/features/validation", false );
          parser.setFeature( "http://apache.org/xml/features/nonvalidating/load-external-dtd", false );
          //was throwing java.io.UTFDataFormatException: Invalid byte 2 of 3-byte UTF-8 sequence.
//          parser.parse( new InputSource( inStream ) );
          InputStreamReader ireader = new InputStreamReader( inStream );

          parser.parse( new InputSource( ireader ) );
          doc = parser.getDocument();

      } catch( SAXException saxe ) {
        throw new IOException( saxe.toString() );
      }

      // throw error if the xml is not a capability answer
      if ( simpleXPath( doc, "WMT_MS_Capabilities") == null) {
        DOMImplementationRegistry registry;
        String str = "";
        try {
          registry = DOMImplementationRegistry.newInstance();
//          DOMImplementationList list = registry.getDOMImplementationList("LS");
//          for (int i = 0; i < list.getLength(); i++) {
//            System.out.println(list.item(i));
//          }

          DOMImplementationLS impl = (DOMImplementationLS) registry
              .getDOMImplementation("LS");
          LSSerializer writer = impl.createLSSerializer();
          str = writer.writeToString(doc);
        } catch (Exception e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
        throw new WMSException("Unexpected answer from server. Missing node <WMT_MS_Capabilities>.", str);
      }

      // get the title
      try {
        title = ((CharacterData)simpleXPath( doc, "WMT_MS_Capabilities/Service/Title" ).getFirstChild()).getData();
      } catch (NullPointerException e) {
        title = "not available";
      }

      // get the supported file formats			// UT was "WMT_MS_Capabilities/Capability/Request/Map/Format"
      final Node formatNode = simpleXPath( doc, "WMT_MS_Capabilities/Capability/Request/GetMap" );

      NodeList nl = formatNode.getChildNodes();
      for( int i=0; i < nl.getLength(); i++ ) {
        Node n = nl.item( i );
        if( n.getNodeType() == Node.ELEMENT_NODE && "Format".equals( n.getNodeName() )) {
            formatList.add( n.getFirstChild().getNodeValue() );
        }
      }

      // get the possible URLs
      String xp = "DCPType/HTTP/Get/OnlineResource";
      String xlink = "http://www.w3.org/1999/xlink";
      Element e = (Element) simpleXPath(formatNode, xp);
      getMapURL = e.getAttributeNS(xlink, "href");

      xp = "WMT_MS_Capabilities/Capability/Request/GetFeatureInfo/DCPType/HTTP/Get/OnlineResource";
      e = (Element) simpleXPath(doc, xp);
      if (e != null) {
            getFeatureInfoURL = e.getAttributeNS(xlink, "href");
        } else {
            getFeatureInfoURL = "";
        }

      // get the top layer
      topLayer = wmsLayerFromNode( simpleXPath( doc, "WMT_MS_Capabilities/Capability/Layer" ) );

      return new Capabilities( service, title, topLayer, formatList, getMapURL, getFeatureInfoURL );
    }
  /**
   * Recursively prints out the DOM structure underneath a Node.
   * The prefix parameter is used in the recursive call to indent properly,
   * but it can also be used in the initial call to provide an initial prefix
   * or indentation.
   * @param n the Node to print out
   * @param prefix the prefix to use
   */
  public void printNode( Node n, String prefix ) {
    System.out.println( prefix + n.toString() );
    NodeList nl = n.getChildNodes();
    for( int i = 0; i < nl.getLength(); i++ ) {
      printNode( nl.item( i ), prefix + "  " );
    }
  }

  /**
   * A very simple XPath implementation.
   * Recursively drills down into the DOM tree, starting at the given parent
   * Node, following the provided XPath. The XPath string is a slash-delimited
   * list of element names to drill down into, the node with the last name in
   * the list is returned
   * @param parent the parent node to search into
   * @param xpath the simplified XPath search string
   * @return the Node found at the end of the search, or null if the search
   * failed to find the specified node.
   */
  public Node simpleXPath( Node parent, String xpath ) {
    String name;
    String nextPath = null;
    if( xpath.indexOf( '/' ) > 0 ) {
      name = xpath.substring( 0, xpath.indexOf( '/' ) );
      nextPath = xpath.substring( xpath.indexOf( '/' ) + 1 );
    } else {
      name = xpath;
    }
    NodeList nl = parent.getChildNodes();
    for( int i = 0; i < nl.getLength(); i++ ) {
      Node n = nl.item( i );
      if( n.getNodeType() == Node.ELEMENT_NODE && n.getNodeName().equals( name ) ) {
        if( nextPath == null ) {
          return n;
        } else {
          return simpleXPath( n, nextPath );
        }
      }
    }
    return null;
  }

}
