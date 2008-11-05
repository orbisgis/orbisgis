
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

package org.contrib.algorithm.qa;

import java.util.Iterator;

import org.contrib.model.jump.model.AttributeType;
import org.contrib.model.jump.model.BasicFeature;
import org.contrib.model.jump.model.Feature;
import org.contrib.model.jump.model.FeatureCollection;
import org.contrib.model.jump.model.FeatureDataset;
import org.contrib.model.jump.model.FeatureSchema;
import org.contrib.model.jump.ui.StringUtil;
import org.orbisgis.progress.IProgressMonitor;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Computes various statistics for selected layers.
 */
public class FeatureStatisticsPlugIn {
    public static final String nPtsAttr = "nPts";
    public static final String nHolesAttr = "nHoles";
    public static final String nCompsAttr = "nComponents";
    public static final String areaAttr = "area";
    public static final String lengthAttr = "length";
    public static final String typeAttr = "type";
    private static final String jtsGeometryClassPackagePrefix = "com.vividsolutions.jts.geom";
	private IProgressMonitor pm;

    public FeatureStatisticsPlugIn(IProgressMonitor pm) {
    	this.pm = pm;
    }

    
    
    public static FeatureSchema getStatisticsSchema() {
        FeatureSchema featureSchema = new FeatureSchema();
        featureSchema.addAttribute("GEOMETRY", AttributeType.GEOMETRY);
        featureSchema.addAttribute(nPtsAttr, AttributeType.INTEGER);
        featureSchema.addAttribute(nHolesAttr, AttributeType.INTEGER);
        featureSchema.addAttribute(nCompsAttr, AttributeType.INTEGER);
        featureSchema.addAttribute(areaAttr, AttributeType.DOUBLE);
        featureSchema.addAttribute(lengthAttr, AttributeType.DOUBLE);

        return featureSchema;
    }

    /**
     * Removes the JTS geometry package prefix from a classname
     * @param fullClassName
     * @return the simplified class name
     */
    public static String removeGeometryPackage(String fullClassName) {
        if (fullClassName.startsWith(jtsGeometryClassPackagePrefix)) {
            return StringUtil.classNameWithoutQualifiers(fullClassName);
        } else {
            return fullClassName;
        }
    }


    public FeatureDataset featureStatistics(final FeatureCollection fc) {
        FeatureSchema statsSchema = getStatisticsSchema();
        FeatureDataset statsFC = new FeatureDataset(statsSchema);
        
        pm.startTask("Building statistics");
        int k =0;
        for (Iterator i = fc.iterator(); i.hasNext();) {
            k++;
            pm.progressTo(k);
        	Feature f = (Feature) i.next();
            Geometry g = f.getGeometry();
            double area = g.getArea();
            double length = g.getLength();

            // these both need work - need to recurse into geometries
            int comps = 1;

            if (g instanceof GeometryCollection) {
                comps = ((GeometryCollection) g).getNumGeometries();
            }

            Coordinate[] pts = g.getCoordinates();
            int holes = 0;

            if (g instanceof Polygon) {
                holes = ((Polygon) g).getNumInteriorRing();
            }

            Feature statsf = new BasicFeature(statsSchema);

            // this aliases the geometry of the input feature, but this shouldn't matter,
            // since if geometries are edited they should be completely replaced
            statsf.setAttribute("GEOMETRY", g);
            statsf.setAttribute(nPtsAttr, new Integer(pts.length));
            statsf.setAttribute(nHolesAttr, new Integer(holes));
            statsf.setAttribute(nCompsAttr, new Integer(comps));
            statsf.setAttribute(areaAttr, new Double(area));
            statsf.setAttribute(lengthAttr, new Double(length));
            statsFC.add(statsf);
        }
        pm.endTask();

        return statsFC;
    }
}
