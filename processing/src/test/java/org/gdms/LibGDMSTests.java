/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.gdms;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.gdms.sql.customQuery.geometry.raster.convert.RasterToPointsTest;
import org.gdms.sql.customQuery.geometry.raster.convert.RasterToPolygonsTest;
import org.gdms.sql.function.spatial.geometry.ConvexHullTest;
import org.gdms.sql.function.spatial.geometry.ExtractTest;
import org.gdms.triangulation.sweepLine4CDT.CDTCircumCircleTest;
import org.gdms.triangulation.sweepLine4CDT.CDTSweepLineTest;
import org.gdms.triangulation.sweepLine4CDT.CDTTriangleTest;

public class LibGDMSTests extends TestSuite {
	public static Test suite() {
		TestSuite suite = new TestSuite("Tests for lib-gdms");
		// $JUnit-BEGIN$

		
		// TODO re-enable all tests
		
		
//		suite.addTestSuite(RasterToPointsTest.class);
//		suite.addTestSuite(RasterToPolygonsTest.class);

		suite.addTestSuite(ExtractTest.class);
		suite.addTestSuite(ConvexHullTest.class);

//		suite.addTestSuite(CDTCircumCircleTest.class);
//		suite.addTestSuite(CDTSweepLineTest.class);
//		suite.addTestSuite(CDTTriangleTest.class);
		// $JUnit-END$
		return suite;
	}
}