/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.coremap.renderer.se;

import org.h2gis.functions.factory.H2GISDBFactory;
import org.h2gis.functions.factory.H2GISFunctions;
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
        DataSource dataSource = H2GISDBFactory.createDataSource(DefaultStyleTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        H2GISFunctions.load(connection);
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
