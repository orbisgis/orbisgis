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
package org.gdms.sql.function;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.types.Type;
import org.gdms.data.types.TypeFactory;
import org.gdms.data.values.Value;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DataSet;
import org.gdms.driver.memory.MemoryDataSetDriver;
import org.gdms.sql.FunctionTest;
import org.gdms.sql.function.spatial.raster.convert.ST_RasterToPolygons;
import org.gdms.sql.function.spatial.raster.properties.ST_PixelValue;
import org.grap.model.GeoRaster;
import org.grap.model.GeoRasterFactory;
import org.grap.model.RasterMetadata;
import org.junit.Test;
import org.orbisgis.progress.NullProgressMonitor;
import static org.junit.Assert.*;

/**
 *
 * @author Erwan Bocher
 */
public class RasterFunctionTest extends FunctionTest {

        @Test
        public void testRasterToPolygons() throws Exception {

                MemoryDataSetDriver mdsd = new MemoryDataSetDriver(new String[]{"raster"},
                        new Type[]{
                                TypeFactory.createType(Type.RASTER)});

                float[] pixels = new float[]{1, 1, 0, -9999};

                RasterMetadata rasterMetadata = new RasterMetadata(1, 2, 1, 1, 2, 2, -9999);
                GeoRaster georaster = GeoRasterFactory.createGeoRaster(pixels, rasterMetadata);
                mdsd.addValues(new Value[]{ValueFactory.createValue(georaster)});

                DataSet[] tables = new DataSet[]{mdsd};

                ST_RasterToPolygons sT_RasterToPolygons = new ST_RasterToPolygons();
                DataSet result = sT_RasterToPolygons.evaluate(dsf, tables, new Value[]{ValueFactory.createValue("raster")}, new NullProgressMonitor());

                assertTrue(result.getRowCount() == 2);

                assertTrue(result.getGeometry(0, 1).equals(wktReader.read("POLYGON ((0.5 3.5, 1.5 3.5, 1.5 2.5, 0.5 2.5, 0.5 3.5))")));
                assertTrue(result.getGeometry(1, 1).equals(wktReader.read("POLYGON ((0.5 2.5, 2.5 2.5, 2.5 1.5, 0.5 1.5, 0.5 2.5))")));

        }

        @Test
        public void testST_PixelValue() throws Exception {
                float[] pixels = new float[]{1, 1, 0, -9999};
                RasterMetadata rasterMetadata = new RasterMetadata(1, 2, 1, 1, 2, 2, -9999);
                GeoRaster georaster = GeoRasterFactory.createGeoRaster(pixels, rasterMetadata);
                Geometry point = wktReader.read("POINT(1 2)");

                ST_PixelValue sT_PixelValue = new ST_PixelValue();
                Value result = sT_PixelValue.evaluate(dsf, new Value[]{ValueFactory.createValue(georaster), ValueFactory.createValue(point)});

                assertTrue((result.getAsFloat() - 1) < 10E6);
        }
        
        @Test
        public void testST_PixelValue2() throws Exception {
                float[] pixels = new float[]{1, 1, 0, -9999};
                RasterMetadata rasterMetadata = new RasterMetadata(1, 2, 1, 1, 2, 2, -9999);
                GeoRaster georaster = GeoRasterFactory.createGeoRaster(pixels, rasterMetadata);
                Geometry point = wktReader.read("POINT(1 2)");
                ST_PixelValue sT_PixelValue = new ST_PixelValue();
                Value result = sT_PixelValue.evaluate(dsf, new Value[]{ValueFactory.createValue(georaster), ValueFactory.createValue(1), ValueFactory.createValue(point)});

                assertTrue((result.getAsFloat() - 1) < 10E6);
        }
}
