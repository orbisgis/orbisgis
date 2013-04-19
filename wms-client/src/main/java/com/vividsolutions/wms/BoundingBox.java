



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


/**
 * Represents a bounding box in a specific projection.
 * A BoundingBox is immutable, so you must create a new BoundingBox object,
 * rather than modify the the values of an existing BoundingBox.
 * @author chodgson@refractions.net
 */
public class BoundingBox {
  public static final String LATLON="LatLon";

  private String srs;
  private double minx;
  private double miny;
  private double maxx;
  private double maxy;

  /**
   * Creates a new BoundingBox with the given SRS, minima and maxima.
   * @param srs a WMS-style SRS string such as "EPSG:1234", or the
   *             special string "LatLon" for a latitude/longitude box
   * @param minx the minimum x-value of the bounding box
   * @param miny the minimum y-value of the bounding box
   * @param maxx the maximum x-value of the bounding box
   * @param maxy the maximum y-value of the bounding box
   */
  public BoundingBox( String srs, double minx, double miny, double maxx, double maxy ) {
    this.srs = srs;
    this.minx = minx;
    this.miny = miny;
    this.maxx = maxx;
    this.maxy = maxy;
  }

  /**
   * Gets the SRS string.
   * @return the BoundingBox's SRS WMS-style string
   */
  public String getSRS() {
    return srs;
  }

  /**
   * Gets the BoundingBox's minimum x value.
   * @return the BoundingBox's minimum x value
   */
  public double getMinX() {
    return minx;
  }

  /**
   * Gets the BoundingBox's minimum y value.
   * @return the BoundingBox's minimum y value
   */
  public double getMinY() {
    return miny;
  }

  /**
   * Gets the BoundingBox's maximum x value.
   * @return the BoundingBox's maximum x value
   */
  public double getMaxX() {
    return maxx;
  }

  /**
   * Gets the BoundingBox's maximum y value.
   * @return the BoundingBox's maximum y value
   */
  public double getMaxY() {
    return maxy;
  }

}
