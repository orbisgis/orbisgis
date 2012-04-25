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
package org.gdms.data.schema;

import org.gdms.data.types.GeometryDimensionConstraint;
import org.gdms.data.types.Type;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author ebocher
 */
public class MetadataUtilitiesTest {

    private DefaultMetadata basicMetadata;
    private DefaultMetadata geometryMetadata;
    private DefaultMetadata rasterMetadata;

    @Before
    public void setUp() throws Exception {
        basicMetadata = new DefaultMetadata();
        basicMetadata.addField("name", Type.STRING);
        basicMetadata.addField("surname", Type.STRING);
        basicMetadata.addField("location", Type.STRING);

        geometryMetadata = new DefaultMetadata();
        geometryMetadata.addField("the_geom", Type.GEOMETRY);
        geometryMetadata.addField("surname", Type.STRING);
        geometryMetadata.addField("location", Type.STRING);

        rasterMetadata = new DefaultMetadata();
        rasterMetadata.addField("raster", Type.RASTER);
        rasterMetadata.addField("surname", Type.STRING);
        rasterMetadata.addField("location", Type.STRING);
    }

    @Test
    public void testGetSpatialFieldIndex() throws Exception {
        assertTrue(MetadataUtilities.getSpatialFieldIndex(basicMetadata) == -1);
        assertTrue(MetadataUtilities.getSpatialFieldIndex(geometryMetadata) == 0);
        assertTrue(MetadataUtilities.getSpatialFieldIndex(rasterMetadata) == 0);

    }

    @Test
    public void testGetGeometryFieldIndex() throws Exception {
        assertTrue(MetadataUtilities.getGeometryFieldIndex(basicMetadata) == -1);
        assertTrue(MetadataUtilities.getGeometryFieldIndex(geometryMetadata) == 0);
        assertTrue(MetadataUtilities.getGeometryFieldIndex(rasterMetadata) == -1);
    }

    @Test
    public void testGetRasterFieldIndex() throws Exception {
        assertTrue(MetadataUtilities.getRasterFieldIndex(basicMetadata) == -1);
        assertTrue(MetadataUtilities.getRasterFieldIndex(geometryMetadata) == -1);
        assertTrue(MetadataUtilities.getRasterFieldIndex(rasterMetadata) == 0);
    }

    @Test
    public void testisGeometry() throws Exception {
        assertTrue(!MetadataUtilities.isGeometry(basicMetadata));
        assertTrue(MetadataUtilities.isGeometry(geometryMetadata));
        assertTrue(!MetadataUtilities.isGeometry(rasterMetadata));
    }

    @Test
    public void testisRaster() throws Exception {
        assertTrue(!MetadataUtilities.isRaster(basicMetadata));
        assertTrue(!MetadataUtilities.isRaster(geometryMetadata));
        assertTrue(MetadataUtilities.isRaster(rasterMetadata));
    }

    @Test
    public void testGeometryDimension() throws Exception {
        DefaultMetadata dm = new DefaultMetadata();
        dm.addField("name", Type.STRING);
        dm.addField("point", Type.POINT);
        dm.addField("MultiPoint", Type.MULTIPOINT);
        dm.addField("LineString", Type.LINESTRING);
        dm.addField("MultiLineString", Type.MULTILINESTRING);
        dm.addField("Polygon", Type.POLYGON);
        dm.addField("MultiPolygon", Type.MULTIPOLYGON);
        dm.addField("Geometry", Type.GEOMETRY);
        dm.addField("GeometryCollection", Type.GEOMETRYCOLLECTION);
        try {
            int ret = MetadataUtilities.getGeometryDimension(dm, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 1), 0);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 2), 0);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 3), 1);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 4), 1);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 5), 2);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 6), 2);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 7), -1);
        assertEquals(MetadataUtilities.getGeometryDimension(dm, 8), -1);

    }

    @Test
    public void testHumanGeometryDimension() throws Exception {
        DefaultMetadata dm = new DefaultMetadata();
        dm.addField("name", Type.STRING);
        dm.addField("point", Type.POINT);
        dm.addField("MultiPoint", Type.MULTIPOINT);
        dm.addField("LineString", Type.LINESTRING);
        dm.addField("MultiLineString", Type.MULTILINESTRING);
        dm.addField("Polygon", Type.POLYGON);
        dm.addField("MultiPolygon", Type.MULTIPOLYGON);
        dm.addField("Geometry", Type.GEOMETRY);
        dm.addField("GeometryCollection", Type.GEOMETRYCOLLECTION);
        try {
            int ret = MetadataUtilities.getGeometryDimension(dm, 0);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 1), GeometryDimensionConstraint.HUMAN_DIMENSION_POINT);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 2), GeometryDimensionConstraint.HUMAN_DIMENSION_POINT);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 3), GeometryDimensionConstraint.HUMAN_DIMENSION_CURVE);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 4), GeometryDimensionConstraint.HUMAN_DIMENSION_CURVE);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 5), GeometryDimensionConstraint.HUMAN_DIMENSION_SURFACE);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 6), GeometryDimensionConstraint.HUMAN_DIMENSION_SURFACE);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 7), GeometryDimensionConstraint.HUMAN_DIMENSION_UNKNOWN);
        assertEquals(MetadataUtilities.getHumanGeometryDimension(dm, 8), GeometryDimensionConstraint.HUMAN_DIMENSION_UNKNOWN);

    }
}
