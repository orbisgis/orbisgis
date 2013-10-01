package org.orbisgis.view.geocatalog;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.sputilities.TableLocation;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.VectorialFilter;

import javax.sql.DataSource;
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
            st.execute("create table userTable1 ( id integer primary key, pt POINT)");
        }
        SourceListModel sourceListModel = new SourceListModel(dataSource);
        List<IFilter> filters = new ArrayList<>();
        filters.add(new VectorialFilter());
        sourceListModel.setFilters(filters);
        assertEquals(1, sourceListModel.getSize());
        assertEquals("USERTABLE1", TableLocation.parse(sourceListModel.getElementAt(0).getKey()).getTable());
        assertEquals("", TableLocation.parse(sourceListModel.getElementAt(0).getLabel()).getSchema());
    }
}
