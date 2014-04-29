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
package org.orbisgis.core.geometryUtils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;
import org.junit.Test;
import org.orbisgis.core.geometryUtils.filter.CoordinateSequenceDimensionFilter;

import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class GeometryTypeUtilTest {

        public WKTReader wKTReader = new WKTReader();

        @Test
        public void testDimensionSequence() throws Exception {
                Geometry geom = wKTReader.read("POINT(0 0)");
                CoordinateSequenceDimensionFilter cd = new CoordinateSequenceDimensionFilter();
                geom.apply(cd);
                assertTrue(cd.getDimension() == 2);
                geom = wKTReader.read("LINESTRING(0 0, 0 0 1)");
                cd = new CoordinateSequenceDimensionFilter();
                geom.apply(cd);
                assertTrue(cd.getDimension() == 3);
        }
}
