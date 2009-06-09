
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

package org.gdms.model;

import java.util.Collection;
import java.util.Iterator;

import com.vividsolutions.jts.geom.Geometry;


/**
 * Utility functions to create different kinds of FeatureDatasets
 */
public class FeatureDatasetFactory {
  /**
   * Creates a FeatureCollection from a Collection of {@link Geometry}s
   * @param geoms a collection of {@link Geometry}s
   */
  public static FeatureCollection createFromGeometry(Collection geoms) {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);

    FeatureCollection fc = new FeatureDataset(featureSchema);

    for (Iterator i = geoms.iterator(); i.hasNext();) {
      Feature feature = new BasicFeature(fc.getFeatureSchema());
      feature.setGeometry((Geometry) i.next());
      fc.add(feature);
    }

    return fc;
  }

  /**
   * Creates a FeatureCollection from a Collection of {@link Geometry}s
   * and adds an attribute containing the length of the Geometry.  The attribute
   * name is given by the argument <code>attrName</code>
   * @param geoms a collection of {@link Geometry}s
   * @param attrName the name to use for the length attribute
   */
  public static FeatureDataset createFromGeometryWithLength(
      Collection geoms, String attrName) {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    featureSchema.addAttribute(attrName, AttributeType.DOUBLE);

    FeatureDataset fc = new FeatureDataset(featureSchema);

    for (Iterator i = geoms.iterator(); i.hasNext();) {
      Feature feature = new BasicFeature(fc.getFeatureSchema());
      Geometry g = (Geometry) i.next();
      feature.setGeometry(g);
      feature.setAttribute(attrName, new Double(g.getLength()));
      fc.add(feature);
    }

    return fc;
  }

  /**
   * Creates a {@link FeatureDataset} from a Collection of {@link Geometry}s
   * using the given {@link FeatureSchema}
   *
   * @param geoms a collection of {@link Geometry}s
   * @param featureSchema the schema to use for the feature collection
   *
   * @return a FeatureDataset containing features for the geometries
   */
  public static FeatureDataset createFromGeometry(Collection geoms, FeatureSchema featureSchema) {
    FeatureDataset fc = new FeatureDataset(featureSchema);
    for (Iterator i = geoms.iterator(); i.hasNext();) {
      Feature feature = new BasicFeature(fc.getFeatureSchema());
      feature.setGeometry((Geometry) i.next());
      fc.add(feature);
    }
    return fc;
  }

  /**
   * Creates a FeatureCollection from a Collection of {@link Geometry}s
   * and adds optional attributes containing the length and area of the Geometry.
   *
   * @param geoms a collection of {@link Geometry}s
   * @param attrNameLen the name to use for the length attribute (or <code>null</code> if not required)
   * @param attrNameArea the name to use for the length attribute (or <code>null</code> if not required)
   *
   */
  public static FeatureDataset createFromGeometryWithLengthAndArea(
      Collection geoms, String attrNameLen, String attrNameArea) {
    FeatureSchema featureSchema = new FeatureSchema();
    featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
    if (attrNameLen != null)
      featureSchema.addAttribute(attrNameLen, AttributeType.DOUBLE);
    if (attrNameArea != null)
      featureSchema.addAttribute(attrNameArea, AttributeType.DOUBLE);

    FeatureDataset fc = new FeatureDataset(featureSchema);

    for (Iterator i = geoms.iterator(); i.hasNext();) {
      Feature feature = new BasicFeature(fc.getFeatureSchema());
      Geometry g = (Geometry) i.next();
      feature.setGeometry(g);
      if (attrNameLen != null)
        feature.setAttribute(attrNameLen, new Double(g.getLength()));
      if (attrNameArea != null)
        feature.setAttribute(attrNameArea, new Double(g.getArea()));
      fc.add(feature);
    }
    return fc;
  }
}