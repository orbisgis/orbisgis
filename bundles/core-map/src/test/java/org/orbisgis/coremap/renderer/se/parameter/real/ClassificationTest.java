package org.orbisgis.coremap.renderer.se.parameter.real;

import org.h2gis.h2spatial.ut.SpatialH2UT;
import org.h2gis.h2spatialext.CreateSpatialExtension;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orbisgis.coremap.renderer.classification.ClassificationUtils;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * @author Nicolas Fortin
 */
public class ClassificationTest {
    private static Connection connection;

    @BeforeClass
    public static void tearUpClass() throws Exception {
        DataSource dataSource = SpatialH2UT.createDataSource(ClassificationTest.class.getSimpleName(), false);
        connection = dataSource.getConnection();
        CreateSpatialExtension.initSpatialExtension(connection);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        connection.close();
    }

    private Connection getConnection() {
        return connection;
    }

    @Test
    public void testMinMax() throws SQLException, ParameterException {
        try(Statement st = getConnection().createStatement()) {
            st.execute("DROP TABLE IF EXISTS TEST");
            st.execute("CREATE TABLE TEST(val double)");
            st.execute("INSERT INTO TEST VALUES (null), (1), (4), (-50), (83), (22)");
            double[] minMax = ClassificationUtils.getMinAndMax(connection, "TEST", "VAL");
            assertEquals(-50, minMax[0], 1e-12);
            assertEquals(83, minMax[1], 1e-12);
        }
    }
}
