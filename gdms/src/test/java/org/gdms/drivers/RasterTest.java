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
package org.gdms.drivers;

import java.io.File;

import junit.framework.TestCase;

import org.gdms.BaseTest;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.data.metadata.DefaultMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.ObjectMemoryDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Envelope;

public class RasterTest extends TestCase {

	private DataSourceFactory dsf;

	public void testProducedRasterEnvelope() throws Exception {
		DataSource ds = dsf.getDataSource("raster");
		ds.open();
		SpatialDataSourceDecorator sds = new SpatialDataSourceDecorator(ds);
		Envelope env = sds.getFullExtent();
		assertTrue(env.getWidth() > 0);
		assertTrue(env.getHeight() > 0);
		ds.close();
	}

	public void testSQLResultSourceType() throws Exception {
		int type = dsf.getSourceManager().getSource("raster").getType();
		assertTrue((type & SourceManager.RASTER) > 0);
	}

	public void setUp() throws Exception {
		byte[] rasterData = new byte[4];
		RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 1, 1, 2, 2);
		GeoRaster gr = GeoRasterFactory.createGeoRaster(rasterData,
				rasterMetadata);

		dsf = new DataSourceFactory();
		dsf.setTempDir("src/test/resources/backup");
		DefaultMetadata metadata = new DefaultMetadata(new Type[] { TypeFactory
				.createType(Type.RASTER) }, new String[] { "raster" });
		ObjectMemoryDriver omd = new ObjectMemoryDriver(metadata);
		omd.addValues(new Value[] { ValueFactory.createValue(gr) });
		dsf.getSourceManager().register("raster", omd);
	}

	public void testOpenJPG() throws Exception {
		File file = new File("src/test/resources/sample.jpg");
		testOpen(file);
	}

	public void testOpenPNG() throws Exception {
		File file = new File("src/test/resources/sample.png");
		testOpen(file);
	}

	public void testOpenASC() throws Exception {
		File file = new File(BaseTest.internalData + "sample.asc");
		testOpen(file);
	}

	public void testOpenTIFF() throws Exception {
		File file = new File(BaseTest.internalData
				+ "littlelehavre.tif");
		testOpen(file);
	}

	private void testOpen(File file) throws Exception {
		GeoRaster gr = GeoRasterFactory.createGeoRaster(file.getAbsolutePath());
		gr.open();
		int rasterType = gr.getType();
		DataSource ds = dsf.getDataSource(file);
		ds.open();
		Metadata metadata = ds.getMetadata();
		Type fieldType = metadata.getFieldType(0);
		assertTrue(fieldType.getIntConstraint(Constraint.RASTER_TYPE) == rasterType);
		ds.getFieldValue(0, 0);
		ds.close();
	}
}
