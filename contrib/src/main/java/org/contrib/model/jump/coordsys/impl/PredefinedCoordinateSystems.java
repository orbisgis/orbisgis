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

package org.contrib.model.jump.coordsys.impl;

import org.contrib.model.jump.coordsys.CoordinateSystem;
import org.contrib.model.jump.coordsys.Radius;
import org.contrib.model.jump.coordsys.Spheroid;

import com.vividsolutions.jts.util.Assert;



/**
 * Provides a number of named coordinate systems.
 */
public class PredefinedCoordinateSystems {
    public static final CoordinateSystem BC_ALBERS_NAD_83 = new CoordinateSystem("BC Albers",
            42102,
            new Albers() {

                {
                    setSpheroid(new Spheroid(new Radius(Radius.GRS80)));
                    setParameters(-126.0, 50.0, 58.5, 45.0, 1000000.0, 0.0);
                }
            });
    public static final CoordinateSystem GEOGRAPHICS_WGS_84 = new CoordinateSystem("Geographics",
            4326, new LatLong());
    public static final CoordinateSystem UTM_07N_WGS_84 = createUTMNorth(7);
    public static final CoordinateSystem UTM_08N_WGS_84 = createUTMNorth(8);
    public static final CoordinateSystem UTM_09N_WGS_84 = createUTMNorth(9);
    public static final CoordinateSystem UTM_10N_WGS_84 = createUTMNorth(10);
    public static final CoordinateSystem UTM_11N_WGS_84 = createUTMNorth(11);

    private PredefinedCoordinateSystems() {
    }

    private static CoordinateSystem createUTMNorth(final int zone) {
        Assert.isTrue(1 <= zone && zone <= 60);
        //Pad with zero to facilitate sorting [Jon Aquino]
        return new CoordinateSystem("UTM " + (zone < 10 ? "0" : "") + zone + "N", 32600 + zone,
            new UniversalTransverseMercator() {
                {
                    setSpheroid(new Spheroid(new Radius(Radius.GRS80)));
                    setParameters(zone);
                }
            });
    }


    public static CoordinateSystem getCoordinateSystem( int epsgCode ) {
        CoordinateSystem cs = null;

        if ( epsgCode == GEOGRAPHICS_WGS_84.getEPSGCode() ) {
            cs = GEOGRAPHICS_WGS_84;
        } else if ( epsgCode == BC_ALBERS_NAD_83.getEPSGCode() ) {
            cs = BC_ALBERS_NAD_83;
        } else if ( epsgCode == UTM_07N_WGS_84.getEPSGCode() ) {
            cs = UTM_07N_WGS_84;
        } else if ( epsgCode == UTM_08N_WGS_84.getEPSGCode() ) {
            cs = UTM_08N_WGS_84;
        } else if ( epsgCode == UTM_09N_WGS_84.getEPSGCode() ) {
            cs = UTM_09N_WGS_84;
        } else if ( epsgCode == UTM_10N_WGS_84.getEPSGCode() ) {
            cs = UTM_10N_WGS_84;
        } else if ( epsgCode == UTM_11N_WGS_84.getEPSGCode() ) {
            cs = UTM_11N_WGS_84;
        } else {
            // don't do an assertion - it should be alright if the EPSG code
            // is one of the predefined ones.
        }

        return cs;
    }

}
