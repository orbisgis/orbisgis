/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...).
 *
 * Gdms is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV FR CNRS 2488
 *
 * This file is part of Gdms.
 *
 * Gdms is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * Gdms is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Gdms. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info@orbisgis.org
 */

package org.gdms.sql.function.spatial.tin.analysis;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

import com.vividsolutions.jts.geom.Coordinate;
import junit.framework.TestCase;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.gdms.sql.function.FunctionException;

/**
 * Unit test of ST_TriangleContouring
 */
public class ST_TriangleContouringTest  extends TestCase{
    
    public ST_TriangleContouringTest() {
    }

    @Test
    public void testContouringTriangle() throws FunctionException {
        int subdividedTri = 0;
        //Input Data, a Triangle
        TriMarkers triangleData = new TriMarkers(new Coordinate(7,2),
                                                 new Coordinate(13,4),
                                                 new Coordinate(5,7),
                                                 2885245,2765123,12711064
                                                );
        //Iso ranges
        String isolevels_str = "31622, 100000, 316227, 1000000, 3162277, 1e+7, 31622776, 1e+20";
        LinkedList<Double> iso_lvls = new LinkedList<Double>();
        for (String isolvl : isolevels_str.split(",")) {
                iso_lvls.add(Double.valueOf(isolvl));
        }
        //Split the triangle into multiple triangles
        Map<Short,Deque<TriMarkers>> triangleToDriver=ST_TriangleContouring.processTriangle(triangleData, iso_lvls);
        for(Map.Entry<Short,Deque<TriMarkers>> entry : triangleToDriver.entrySet()) {
           subdividedTri+=entry.getValue().size();
        }
        assertTrue(subdividedTri==5);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
}