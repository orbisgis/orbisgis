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
package org.gdms.sql.customQuery.spatial.raster.convert;

import org.gdms.AbstractRasterProcessingTest;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.sql.customQuery.QueryManager;
import org.gdms.sql.customQuery.spatial.raster.convert.RasterToPolygons;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class RasterToPolygonsTest extends AbstractRasterProcessingTest {
	static {
		QueryManager.registerQuery(RasterToPolygons.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();

		dsf.remove("outDs");
	}

	public void testEvaluate() throws Exception {
		dsf.getSourceManager().register("outDs",
				"select RasterToPolygons(raster) from georastersource;");
		final SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(
				dsf.getDataSource("outDs"));

		sds.open();
		final long rowCount = sds.getRowCount();
		final int fieldCount = sds.getFieldCount();
		assertTrue(3 == fieldCount);
		assertTrue(geoRaster.getWidth() * geoRaster.getHeight() >= rowCount);
		assertTrue(9 == rowCount);

		for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
			final int id = sds.getFieldValue(rowIndex, 0).getAsInt();
			final Polygon polygon = (Polygon) sds.getGeometry(rowIndex);
			final Point point = polygon.getCentroid();
			final float height = sds.getFieldValue(rowIndex, 2).getAsFloat();

			final int c = (int) Math.round((point.getX() - xUlcorner)
					/ pixelSize_X);
			final int r = (int) Math.round((point.getY() - yUlcorner)
					/ pixelSize_Y);
			final int i = r * geoRaster.getWidth() + c;

			assertTrue(floatingPointNumbersEquality(pixels[i], height));
			assertTrue(floatingPointNumbersEquality(polygon.getArea(), Math
					.abs(pixelSize_X * pixelSize_Y)));
		}
		sds.close();

		print(sds);
	}
}