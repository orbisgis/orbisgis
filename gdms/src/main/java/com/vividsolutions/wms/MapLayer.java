
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
// 2005-07-29

package com.vividsolutions.wms;

import java.util.*;

/**
 * Represents a WMS Layer.
 *
 * @author Chris Hodgson chodgson@refractions.net
 */
public class MapLayer {

  // immutable members
  private MapLayer parent;
  private String name;
  private String title;
  private ArrayList<String> srsList;
  private ArrayList<MapLayer> subLayers;
  private BoundingBox bbox;

// I think, bbox contains the information about LatLonBoundingBox
// (see Parser.java)
// I need a new list for <BoundingBox> [uwe dalluege]
  private ArrayList<BoundingBox> boundingBoxList;

  // user modifiable members
  private boolean enabled = false;

  /**
   * Creates a new instance of MapLayer
   */
  public MapLayer(String name, String title, Collection<String> srsList, Collection<MapLayer> subLayers, BoundingBox bbox) {
    this.parent = null;
    this.name = name;
    this.title = title;
    this.srsList = new ArrayList<String>( srsList );
    this.subLayers = new ArrayList<MapLayer>( subLayers );
    Iterator it = subLayers.iterator();
    while( it.hasNext() ) {
      ((MapLayer)it.next()).parent = this;
    }
    this.bbox = bbox;
  }

// ----------------------------------------------------- MapLayer MapLayer ( )
  /**
   * Creates a new instance of MapLayer with boundingBoxList [uwe dalluege]
   */
  public MapLayer
  	( String name, String title, Collection srsList, Collection subLayers,
  		BoundingBox bbox, ArrayList<BoundingBox> boundingBoxList )
  {
  	this ( name, title, srsList, subLayers, bbox );
  	this.boundingBoxList = boundingBoxList;
  }
// ----------------------------------------------------- MapLayer MapLayer ( )
// ---------------------------------------- MapLayer getAllBoundingBoxList ( )
/**
* @return All BoundingBoxes
* If there is no BoundingBox for this MapLayer the parent-BoundingBox
* will be taken.
* [uwe dalluege]
*/
  public List<BoundingBox> getAllBoundingBoxList ( )
  {
  	MapLayer mapLayer = this;
  	List<BoundingBox> allBoundingBoxList = this.getBoundingBoxList ( );

  	if ( allBoundingBoxList.size ( ) > 0 ){
        return allBoundingBoxList;
    }
// ---------------------------------------- MapLayer getAllBoundingBoxList ( )
		while ( mapLayer != null )
		{
			mapLayer = mapLayer.getParent ( );
			if ( mapLayer == null ){
                return allBoundingBoxList;
            }
			allBoundingBoxList = mapLayer.getBoundingBoxList ( );
			if ( allBoundingBoxList.size ( ) > 0 ) {
                return allBoundingBoxList;
            }
		}
// ---------------------------------------- MapLayer getAllBoundingBoxList ( )
  	return allBoundingBoxList;
  }
// ---------------------------------------- MapLayer getAllBoundingBoxList ( )

  /**
   * Returns the number of sub-layers that this MapLayer has.
   * @return the number of sub-layers that this MapLayer has
   */
  public int numSubLayers() {
    return subLayers.size();
  }

  /**
   * Returns the sub-layer at the specified index.
   * @param n the index of the sub-layer to return
   * @return the MapLayer sub-layer at the specified index
   */
  public MapLayer getSubLayer( int n ) {
    return subLayers.get( n );
  }

  /**
   * Gets a copy of the list of the sublayers of this layer.
   * @return a copy of the Arraylist containing all the sub-layers of this layer
   */
  public List<MapLayer> getSubLayerList() {
    return (List<MapLayer>)subLayers.clone();
  }

  /**
   * Returns a list of all the layers in order of a root-left-right traversal of
   * the layer tree.
   * @return a list of all the layers in order of a root-left-right traversal of
   * the layer tree.
   */
  public List<MapLayer> getLayerList() {
    ArrayList<MapLayer> list = new ArrayList<MapLayer>();
    list.add( this );
    Iterator it = subLayers.iterator();
    while( it.hasNext() ) {
      list.addAll( ((MapLayer)it.next()).getLayerList() );
    }
    return list;
  }

  /**
   * Gets the title of this MapLayer.
   * The title of a layer should be used for display purposes.
   * @return the title of this Layer
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the name of this Layer.
   * The name of a layer is its 'back-end', ugly name, which generally
   * shouldn't need to be used by others but is available anyway.
   * Layers which do not have any data associated with them, such as container
   * or grouping layers, might not have a name, in which case null will be
   * returned.
   * @return the name of the layer, or null if it doesn't have a name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the parent MapLayer of this MapLayer.
   * @return the parent layer of this MapLayer, or null if the layer has no parent.
   */
  public MapLayer getParent() {
    return parent;
  }

  /**
   * Gets the LatLonBoundingBox for this layer.
   * If this layer doesn't have a LatLonBoundingBox specified, we recursively
   * ask the parent layer for its bounding box. The WMS spec says that each
   * layer should either have its own LatLonBoundingBox, or inherit one from
   * its parent, so this recursive call should be successful. If not, null is
   * returned. However, if a bounding box is returned, it will have the
   * SRS string "LatLon".
   * Note that the BoundingBox is not necessarily "tight".
   * @return the BoundingBox for this layer, or null if the BBox is unknown
   */
  public BoundingBox getBoundingBox() {

    if( bbox != null ) {
      return bbox;
    }
    if( parent != null ) {
      return parent.getBoundingBox();
    }
    return null;
  }


//----------------------------------------- MapLayer getLatLonBoundingBox ( )
  /**
   * I think this name is better [uwe dalluege]
   * Gets the LatLonBoundingBox for this layer.
   * If this layer doesn't have a LatLonBoundingBox specified, we recursively
   * ask the parent layer for its bounding box. The WMS spec says that each
   * layer should either have its own LatLonBoundingBox, or inherit one from
   * its parent, so this recursive call should be successful. If not, null is
   * returned. However, if a bounding box is returned, it will have the
   * SRS string "LatLon".
   * Note that the BoundingBox is not necessarily "tight".
   * @return the BoundingBox for this layer, or null if the BBox is unknown
   */
  public BoundingBox getLatLonBoundingBox() {

    if( bbox != null ) {
      return bbox;
    }
    if( parent != null ) {
      return parent.getBoundingBox();
    }
    return null;
  }
// ----------------------------------------- MapLayer getLatLonBoundingBox ( )
//-------------------------------------------- MapLayer getBoundingBoxList ( )
  /**
   * Gets the BoundingBoxList for this Layer
   * @return the BoundingBoxList containing the BoundingBoxes
   */
// ------------------------------------------- MapLayer getBoundingBoxList ( )
  public List<BoundingBox> getBoundingBoxList ( )
  {// [uwe dalluege]
  	return (List<BoundingBox>) boundingBoxList.clone ( );
  }
//--------------------------------------------- MapLayer getBoundingBoxList ( )

  /**
   * Returns a copy of the list of supported SRS's. Each SRS is a string in the
   * format described by the WMS specification, such as "EPSG:1234".
   * @return a copy of the list of supported SRS's
   */
  public List<String> getSRSList() {
    return (List<String>)srsList.clone();
  }
  //<<TODO>>I'd like to return generic Lists, rather than concrete ArrayLists.
  //Or even better, Collections, since order is not significant (I think) [Jon Aquino]
  /**
   * @return a list of the SRS list of this MapLayer and its ancestors
   */
  public Collection<String> getFullSRSList() {
    // Change TreeSet to LinkedHashSet in order to preserve the natural order
    // with layer SRS first ans parent SRS second
    Set<String> fullSRSList  = new LinkedHashSet<String>(getSRSList());
    if (parent != null){
        fullSRSList.addAll(parent.getFullSRSList());
    }
    return fullSRSList;
  }

  /**
   * Returns a somewhat nicely-formatted string representing all of the details of
   * this layer and its sub-layers (recursively).
   * @return a somewhat nicely-formatted string representing all of the details of
   * this layer and its sub-layers (recursively).
   */
  @Override
  public String toString() {
    StringBuffer s = new StringBuffer( "WMSLayer {\n"
        + "  name: \"" + name + "\"\n"
        + "  title: \"" + title + "\"\n"
        + "  srsList: " + srsList.toString() + "\n"
        + "  subLayers: [\n" );
    for( int i = 0; i < subLayers.size(); i++ ) {
      s.append( subLayers.get( i ).toString()).append(", ");
    }
    s.append( "  ]\n  bbox: " );
    if( bbox != null ) {
      s.append( bbox.toString() );
    } else {
      s.append( "null" );
    }
    s.append( "\n}\n" );
    return s.toString();
  }
}
