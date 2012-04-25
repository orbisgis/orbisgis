/**
 * The GDMS library (Generic Datasource Management System)
 * is a middleware dedicated to the management of various kinds of
 * data-sources such as spatial vectorial data or alphanumeric. Based
 * on the JTS library and conform to the OGC simple feature access
 * specifications, it provides a complete and robust API to manipulate
 * in a SQL way remote DBMS (PostgreSQL, H2...) or flat files (.shp,
 * .csv...). It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 *
 * Team leader : Erwan BOCHER, scientific researcher,
 *
 * User support leader : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Pierre-Yves FADET, computer engineer, Thomas LEDUC,
 * scientific researcher, Fernando GONZALEZ CORTES, computer engineer, Maxence LAURENT,
 * computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Alexis GUEGANNO, Maxence LAURENT, Antoine GOURLAY
 *
 * Copyright (C) 2012 Erwan BOCHER, Antoine GOURLAY
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
package org.gdms.drivers;

import org.junit.Before;
import org.junit.Test;
import java.io.File;


import org.gdms.TestBase;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceFactory;
import org.gdms.data.schema.DefaultMetadata;
import org.gdms.data.schema.Metadata;
import org.gdms.data.types.Constraint;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.source.SourceManager;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;

import com.vividsolutions.jts.geom.Envelope;

import static org.junit.Assert.*;

import org.gdms.TestResourceHandler;

public class RasterTest extends TestBase {

        @Test
        public void testProducedRasterEnvelope() throws Exception {
                DataSource ds = dsf.getDataSource("raster");
                ds.open();
                Envelope env = ds.getFullExtent();
                assertTrue(env.getWidth() > 0);
                assertTrue(env.getHeight() > 0);
                ds.close();
        }

        @Test
        public void testSQLResultSourceType() throws Exception {
                int type = sm.getSource("raster").getType();
                assertTrue((type & SourceManager.RASTER) > 0);
        }

        @Before
        public void setUp() throws Exception {
                super.setUpTestsWithoutEdition();
                byte[] rasterData = new byte[4];
                RasterMetadata rasterMetadata = new RasterMetadata(0, 0, 1, 1, 2, 2);
                GeoRaster gr = GeoRasterFactory.createGeoRaster(rasterData,
                        rasterMetadata);

                DefaultMetadata metadata = new DefaultMetadata(new Type[]{TypeFactory.createType(Type.RASTER)}, new String[]{"raster"});
                MemoryDataSetDriver omd = new MemoryDataSetDriver(metadata);
                omd.addValues(new Value[]{ValueFactory.createValue(gr)});
                sm.register("raster", omd);
        }

        @Test
        public void testOpenJPG() throws Exception {
                File file = new File(TestResourceHandler.OTHERRESOURCES, "sample.jpg");
                testOpen(file);
        }

        @Test
        public void testOpenPNG() throws Exception {
                File file = new File(TestResourceHandler.TESTRESOURCES, "sample.png");
                testOpen(file);
        }

        @Test
        public void testOpenASC() throws Exception {
                File file = new File(TestResourceHandler.OTHERRESOURCES, "sample.asc");
                testOpen(file);
        }

        @Test
        public void testOpenTIFF() throws Exception {
                File file = new File(TestResourceHandler.TESTRESOURCES, "littlelehavre.tif");
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
                assertEquals(fieldType.getIntConstraint(Constraint.RASTER_TYPE), rasterType);
                ds.getFieldValue(0, 0);
                ds.close();
        }
}
