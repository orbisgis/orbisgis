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
import com.vividsolutions.jts.geom.Envelope;

/**
 * Represents a WMS Layer.
 *
 * @author Chris Hodgson chodgson@refractions.net
 * @author Uwe Dalluege, uwe.dalluege@rzcn.haw-hamburg.de
 * @author Michael Michaud michael.michaud@free.fr
 */
public class MapLayer {

    // immutable members
    private MapLayer parent;
    private String name;
    private String title;
  private ArrayList<String> srsList;
    private ArrayList<MapLayer> subLayers;
    // Default bounding box in geographic coordinates
    private BoundingBox bbox;  
    private Map<String,BoundingBox> boundingBoxMap;
  
    // user modifiable members
    private boolean enabled = false;
  
    /**
   * * Creates a new instance of MapLayer
   * @param name Name of the layer
   * @param title Title of the layer
   * @param srsList List of supported SRS
   * @param subLayers List of children
   * @param latLon LatLong bounding box as defined in WMS 1.1.1
     */
  public MapLayer(String name, String title, Collection<String> srsList,
          Collection<MapLayer> subLayers, BoundingBox latLon) {
        this.parent = null;
        this.name = name;
        this.title = title;
        this.srsList = new ArrayList<String>( srsList );
        this.subLayers = new ArrayList<MapLayer>( subLayers );
        for(MapLayer ml : subLayers){
            ml.parent = this;
        }
    }

    /**
     * Creates a new instance of MapLayer with boundingBoxMap [uwe dalluege]
     * @param name The name of the layer
     * @param title The title of the layer
     * @param srsList The collection of SRS/CRS that are explicitly associated to this layer
     * @param subLayers The children of this layer
     * @param bbox The geographic bounding box of the layer
     * @param boundingBoxMap The BoundingBox mapped to their declared SRS/CRS.
     * @throws NullPointerException When one of the expected collection/map that
     * should be given in argument of this constructor is null.
     */
    public MapLayer ( String name, String title, Collection<String> srsList, Collection<MapLayer> subLayers,
  		BoundingBox bbox, Map<String,BoundingBox> boundingBoxMap) {
        this ( name, title, srsList, subLayers, bbox );
  	    this.boundingBoxMap = boundingBoxMap;
    }
  
  
    /**
     * @return All BoundingBoxes associated to this layer.
     * If there is no BoundingBox for this MapLayer the parent-BoundingBox
     * will be taken.
     * [uwe dalluege]
     */  
    public List<BoundingBox> getAllBoundingBoxList(){
        if(boundingBoxMap != null){
            Collection<BoundingBox> values = boundingBoxMap.values();
            if(values != null && !values.isEmpty()){
                return new ArrayList<BoundingBox>(values);
            }
        }
        //We didn't find anything, let's check the parents...
        return parent == null ? new ArrayList<BoundingBox>() : parent.getAllBoundingBoxList();
    }

  
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
        return (MapLayer)subLayers.get( n );
    }

    /**
     * Gets a copy of the list of the sublayers of this layer.
     * @return a copy of the Arraylist containing all the sub-layers of this layer
     */
    public ArrayList<MapLayer> getSubLayerList() {
        return (ArrayList<MapLayer>)subLayers.clone();
    }

    /**
     * Returns a list of all the layers in order of a root-left-right traversal of
     * the layer tree.
     * @return a list of all the layers in order of a root-left-right traversal of
     * the layer tree.
     */
    public ArrayList getLayerList() {
        ArrayList list = new ArrayList();
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
  
    /**
     * Return the bounding box defined for this MapLayer in this SRS.
     * If not found, the bounding box is searched in this Layer's children,
     * then in its the parents.
     * If not found, return the whole earth in LonLat SRS.
     */
    public BoundingBox getBoundingBox(String srs) {
        Envelope envelope = getBoundingBox(srs, this, new Envelope());
        MapLayer p = this;
        while (envelope.getMinX() > envelope.getMaxX() && p.getParent() != null) {
            p = p.getParent();
            if(p != null){
                BoundingBox bb = p.getBoundingBoxMap().get(srs);
                if (bb != null) {
                    // if this layer has a bounding box for this srs, return its envelope
                    envelope.expandToInclude(bb.getEnvelope());
                    return new BoundingBox(srs, envelope);
                }
            }
            
        }
        return new BoundingBox(srs, envelope);
    }

    /**
     * Return the envelope of this layer in the wished srs if a BoundingBox in
     * this srs exists. Else if, layer's children are scanned recursively.
     * @param srs The expected SRS
     * @param lyr The original Layer
     * @param env The original envelope. It will be used as a basis and will be expanded to match the bounding box
     *            of the layer.
     * @return The envelope of the layer in the given srs or the expended Envelop of all the children of {@code lyr}
     *         if no explicit bounding box was found for {@code lyr} in the provided srs.
     */
    public static Envelope getBoundingBox(String srs, MapLayer lyr, Envelope env) {
        BoundingBox bb = lyr.getBoundingBoxMap().get(srs);
        if(bb!=null){
            //this layer has a bounding box for this srs, return its envelope
            env.expandToInclude(bb.getEnvelope());
        } else {
            for (MapLayer child : lyr.getSubLayerList()) {
                env.expandToInclude(getBoundingBox(srs, child, env));
            }
        }
        return env;
    }
  
   
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
  
  
    /**
     * Gets the BoundingBoxList for this Layer
     * @return the BoundingBoxList containing the BoundingBoxes
     */
    public Map<String,BoundingBox> getBoundingBoxMap() {// [uwe dalluege]
        return new HashMap<String,BoundingBox>(boundingBoxMap);
    }

  
    /**
     * Returns a copy of the list of supported SRS's. Each SRS is a string in the
     * format described by the WMS specification, such as "EPSG:1234".
     * @return a copy of the list of supported SRS's
     */
    public ArrayList getSRSList() {
      return (ArrayList)srsList.clone();
    }
    
    //<<TODO>>I'd like to return generic Lists, rather than concrete ArrayLists.
    //Or even better, Collections, since order is not significant (I think) [Jon Aquino]
    /**
     * @return a list of the SRS list of this MapLayer and its ancestors
     */
    public Collection getFullSRSList() {
        // Change TreeSet to LinkedHashSet in order to preserve the natural order
        // with layer SRS first ans parent SRS second
        Set fullSRSList  = new LinkedHashSet(getSRSList());
        if (parent != null) fullSRSList.addAll(parent.getFullSRSList());
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
    StringBuilder s = new StringBuilder( "WMSLayer {\n  name: \"");
        s.append(name);
        s.append("\"\n  title: \"");
        s.append(title);
        s.append("\"\n  srsList: ");
        s.append(srsList.toString());
        s.append("\n subLayers: [\n" );
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
