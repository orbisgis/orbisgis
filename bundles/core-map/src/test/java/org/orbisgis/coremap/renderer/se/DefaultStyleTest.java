package org.orbisgis.coremap.renderer.se;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.layerModel.OwsMapContext;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class DefaultStyleTest {
    private static Connection connection;
    private static DataManager dataManager;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SpatialH2UT.createDataSource(DefaultStyleTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
        dataManager = new DataManagerImpl(dataSource);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
        dataManager.dispose();
    }

    private DataManager getDataManager() {
        return dataManager;
    }

    private Connection getConnection() {
        return connection;
    }

    @Test
    public void LineStringDefaultStyle() throws Exception {
        try(Statement st = getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS GTABLE");
            st.execute("CREATE TABLE GTABLE(the_geom GEOMETRY)");
            st.execute("INSERT INTO GTABLE VALUES ('POLYGON ((1 1, 3 3, 4 4, 1 1))')");
            st.execute("INSERT INTO GTABLE VALUES ('LINESTRING (1 1, 3 3)')");
        }
        MapContext mc = new OwsMapContext(getDataManager());
        ILayer layer = mc.createLayer("GTABLE");
        layer.open();
        assertEquals(1, layer.getStyles().size());
        assertTrue(layer.getStyle(0).getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0) instanceof LineSymbolizer);
    }

    @Test
    public void PolygonDefaultStyle() throws Exception {
        try(Statement st = getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS GTABLE");
            st.execute("CREATE TABLE GTABLE(the_geom GEOMETRY)");
            st.execute("INSERT INTO GTABLE VALUES ('POLYGON ((1 1, 3 3, 4 4, 1 1))')");
        }
        MapContext mc = new OwsMapContext(getDataManager());
        ILayer layer = mc.createLayer("GTABLE");
        layer.open();
        assertEquals(1, layer.getStyles().size());
        assertTrue(layer.getStyle(0).getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0) instanceof AreaSymbolizer);
    }

    @Test
    public void MultiPointDefaultStyle() throws Exception {
        try(Statement st = getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS GTABLE");
            st.execute("CREATE TABLE GTABLE(the_geom GEOMETRY)");
            st.execute("INSERT INTO GTABLE VALUES ('MULTIPOINT ((1 1))')");
            st.execute("INSERT INTO GTABLE VALUES ('MULTIPOINT ((1 1), (3 3), (4 4), (1 1))')");
        }
        MapContext mc = new OwsMapContext(getDataManager());
        ILayer layer = mc.createLayer("GTABLE");
        layer.open();
        assertEquals(1, layer.getStyles().size());
        Symbolizer symb = layer.getStyle(0).getRules().get(0).getCompositeSymbolizer().getSymbolizerList().get(0);
        assertTrue(symb instanceof PointSymbolizer);
        // Should draw all points
        assertTrue(((PointSymbolizer) symb).isOnVertex());
    }
}
