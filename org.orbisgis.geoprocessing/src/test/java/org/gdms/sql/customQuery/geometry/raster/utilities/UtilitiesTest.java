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
package org.gdms.sql.customQuery.geometry.raster.utilities;

import java.io.File;

import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.FunctionManager;
import org.gdms.sql.function.spatial.mixed.ToEnvelope;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

public class UtilitiesTest extends FunctionTest {
	public static String externalRasterData = new String(
			"../../datas2tests/grid/sample.asc");
	private DataSourceFactory dsf;
	private Geometry rasterEnvelope;

	static {
		FunctionManager.addFunction(ToEnvelope.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		dsf = new DataSourceFactory();
		File gdmsFile = new File(externalRasterData);
		dsf.getSourceManager().register("sample", gdmsFile);

		WKTReader wktr = new WKTReader();
		rasterEnvelope = wktr
				.read("POLYGON ((634592 5588395, 634592 5592875, 639252 5592875, 639252 5588395, 634592 5588395))");

	}

	public void testRasterEnvelope() throws Exception {
		dsf.getSourceManager().register("outDs",
				"select Envelope(raster) from sample ;");

		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(dsf
				.getDataSource("outDs"));
		sds.open();

		assertTrue(sds.getGeometry(0).equals(rasterEnvelope));
	}
}