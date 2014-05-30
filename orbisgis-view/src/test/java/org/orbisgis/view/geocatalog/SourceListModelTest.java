package org.orbisgis.view.geocatalog;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.internal.DataManagerImpl;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.VectorialFilter;

import javax.sql.DataSource;
import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test db model.
 * @author Nicolas Fortin
 */
public class SourceListModelTest {
    private static Connection connection;
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SpatialH2UT.createDataSource("SourceListModelTest", true);
        connection = dataSource.getConnection();
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testSourceList() throws SQLException {
        try( Statement st = connection.createStatement()) {
            st.execute("DROP TABLE IF EXISTS userTable1");
            st.execute("create table userTable1 ( id integer primary key, pt POINT)");
            st.execute("create schema myschema");
            st.execute("create table myschema.userTable2 ( id integer primary key, pt POINT)");
            st.execute("create table `TABLE.USERTABLE3` ( id integer primary key, pt POINT)");
        }
        DataManager dataManager = new DataManagerImpl(dataSource);
        SourceListModel sourceListModel = new SourceListModel(dataManager);
        sourceListModel.readDatabase();
        sourceListModel.doFilter();
        List<IFilter> filters = new ArrayList<>();
        filters.add(new VectorialFilter());
        sourceListModel.setFilters(filters);
        assertEquals(3, sourceListModel.getSize());
        assertEquals("\"TABLE.USERTABLE3\"", sourceListModel.getElementAt(0).getLabel());
        assertEquals("USERTABLE1", TableLocation.parse(sourceListModel.getElementAt(1).getKey()).getTable());
        assertEquals("MYSCHEMA.USERTABLE2", sourceListModel.getElementAt(2).getLabel());
    }
}
