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

package com.vividsolutions.wms;

import com.vividsolutions.jts.geom.Envelope;


/**
 * Represents a bounding box in a specific projection.
 * A BoundingBox is immutable, so you must create a new BoundingBox object,
 * rather than modify the the values of an existing BoundingBox.
 * WARNING : until WMS 3.1, xmin, ymin represent minimum longitude and
 * minimum latitude. From WMS 1.3.0, xmin represents min value for the first
 * axis of the CoordinateSystem and ymin the min value for the second axis.
 * This means that for EPSG:4326, we have
 * WMS 1.1.x : -180, -90, +180, +90
 * WMS 1.3.0 : -90, -180, +90, +180
 * @author chodgson@refractions.net
 */
public class BoundingBox {
  public static final String LATLON="LatLon";
  public static final String GEOGRAPHICS="Geographics";
  public static final String GEOGRAPHICS_EPSG="EPSG:4326";
   
  private String srs;
  // by default, use longitude, latitude order, as per WMS 1.0.x and 1.1.x
  private AxisOrder axisOrder = AxisOrder.LONLAT;
  private double westBound;
  private double southBound;
  private double eastBound;
  private double northBound;
  
  public BoundingBox( String srs, Envelope envelope ) {
    this.srs = srs;
    axisOrder = AxisOrder.getAxisOrder(srs);
    this.westBound  = envelope.getMinX();
    this.southBound = envelope.getMinY();
    this.eastBound  = envelope.getMaxX();
    this.northBound = envelope.getMaxY();
  }
  
  public BoundingBox( String srs, double westBound, double southBound, double eastBound, double northBound ) {
    this.srs = srs;
    axisOrder = AxisOrder.getAxisOrder(srs);
    this.westBound  = westBound;
    this.southBound = southBound;
    this.eastBound  = eastBound;
    this.northBound = northBound;
  }
  
  /**
   * Gets the SRS string.
   * @return the BoundingBox's SRS WMS-style string
   */
  public String getSRS() {
    return srs;
  }
  
  public AxisOrder getAxisOrder() {
     return axisOrder;
  }
  
  /**
   * Gets the BoundingBox's minimum westing value.
   * @return the BoundingBox's minimum westing value
   */
  public double getWestBound() {
    return westBound;
  }
  
  /**
   * Gets the BoundingBox's minimum southing value.
   * @return the BoundingBox's minimum southing value
   */
  public double getSouthBound() {
    return southBound;
  }
  
  /**
   * Gets the BoundingBox's maximum easting value.
   * @return the BoundingBox's maximum easting value
   */
  public double getEastBound() {
    return eastBound;
  }
  
  /**
   * Gets the BoundingBox's maximum northing value.
   * @return the BoundingBox's maximum northing value
   */
  public double getNorthBound() {
    return northBound;
  }
  
  public String getBBox(String wmsVersion) {
      if (axisOrder.equals(AxisOrder.LONLAT) || 
              wmsVersion.equals(WMService.WMS_1_0_0) || 
              wmsVersion.equals(WMService.WMS_1_1_0) || 
              wmsVersion.equals(WMService.WMS_1_1_1)) {
          return "BBOX=" + westBound + "," + southBound + "," + eastBound + "," + northBound;
      } else {
          return "BBOX=" + southBound + "," + westBound + "," + northBound + "," + eastBound;
      }
  }
  
  public Envelope getEnvelope() {
      return new Envelope(westBound, eastBound, southBound, northBound);
  }
  
  public String toString() {
      return axisOrder == AxisOrder.LATLON ?
          "BBOX(" + getSRS() + ", " + southBound + ", " + westBound + ", " + northBound + ", " + eastBound + ")"
          :"BBOX(" + getSRS() + ", " + westBound + ", " + southBound + ", " + eastBound + ", " + northBound + ")";
  }
  
}
