package org.orbisgis.view.sqlconsole.ui;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.h2gis.network.graph_creator.ST_ShortestPath;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * @author Adam Gouge
 */
public class FunctionElementTest {
    private static Connection connection;
    private static DataSource dataSource;

    @BeforeClass
    public static void tearUp() throws Exception {
        dataSource = SpatialH2UT.createDataSource("FunctionElementTest", false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        connection.close();
    }

    @Test
    public void testH2SignatureGeneration() throws SQLException {
        FunctionElement f = new FunctionElement("ST_SHORTESTPATH",
                DatabaseMetaData.procedureReturnsResult,
                ST_ShortestPath.REMARKS,
                dataSource);
        assertEquals("ST_SHORTESTPATH(VARCHAR, VARCHAR, INTEGER, INTEGER)\n"
                        + "ST_SHORTESTPATH(VARCHAR, VARCHAR, VARCHAR, INTEGER, INTEGER)",
                f.getSQLCommand());
    }
}

