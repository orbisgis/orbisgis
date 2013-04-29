



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

package com.vividsolutions.wms.util;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Provides some simple XML utilities for the WMS implementation to use.
 *
 * @author Chris Hodgson chodgson@refractions.net
 */

public class XMLTools {
  /**
   * Recursively prints out the DOM structure underneath a Node.
   * The prefix parameter is used in the recursive call to indent properly,
   * but it can also be used in the initial call to provide an initial prefix
   * or indentation.
   * @param n the Node to print out
   * @param prefix the prefix to use
   */
  public static void printNode( Node n, String prefix ) {
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
  public static Node simpleXPath( Node parent, String xpath ) {
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
