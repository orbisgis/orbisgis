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
package org.contrib.model.jump.coordsys;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.contrib.model.jump.Blackboard;
import org.contrib.model.jump.coordsys.impl.PredefinedCoordinateSystems;

/**
 * Implements a registry for {@link CoordinateSystem}s.
 */
public class CoordinateSystemRegistry {
    private CoordinateSystemRegistry() {
        add(PredefinedCoordinateSystems.BC_ALBERS_NAD_83);
        add(PredefinedCoordinateSystems.GEOGRAPHICS_WGS_84);
        add(CoordinateSystem.UNSPECIFIED);
        add(PredefinedCoordinateSystems.UTM_07N_WGS_84);
        add(PredefinedCoordinateSystems.UTM_08N_WGS_84);
        add(PredefinedCoordinateSystems.UTM_09N_WGS_84);
        add(PredefinedCoordinateSystems.UTM_10N_WGS_84);
        add(PredefinedCoordinateSystems.UTM_11N_WGS_84);  
        
    }
    public void add(CoordinateSystem coordinateSystem) {
        nameToCoordinateSystemMap.put(coordinateSystem.getName(), coordinateSystem);
    }
    public Collection getCoordinateSystems() {
        return Collections.unmodifiableCollection(nameToCoordinateSystemMap.values());
    }
    public CoordinateSystem get(String name) {
        return (CoordinateSystem) nameToCoordinateSystemMap.get(name);
    }
    private HashMap nameToCoordinateSystemMap = new HashMap();
    public static CoordinateSystemRegistry instance(Blackboard blackboard) {
        String COORDINATE_SYSTEMS_KEY = CoordinateSystemRegistry.class.getName() +
            " - COORDINATE SYSTEMS";

        if (blackboard.get(COORDINATE_SYSTEMS_KEY) == null) {            
            blackboard.put(COORDINATE_SYSTEMS_KEY, new CoordinateSystemRegistry());
        }

        return (CoordinateSystemRegistry) blackboard.get(COORDINATE_SYSTEMS_KEY);
    }
}
