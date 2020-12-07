package org.orbisgis.datastore.postgis

import org.apache.commons.dbcp.BasicDataSource
import org.geotools.data.Query
import org.geotools.jdbc.JDBCDataStore
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

/**
 * Test class dedicated to {@link org.orbisgis.datastore.postgis.POSTGIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class POSTGISTest {

    private static final def POSTGIS_SYS_PROP = "test.postgis"

    // Some Database configuration data
    private static final def PASSWORD = "orbisgis"
    private static final def USER = "orbisgis"
    private static final def ESTIMATED_EXTENDS = true
    private static final def FETCH_SIZE = 100
    private static final def BATCH_INSERT_SIZE = 50
    private static final def PORT = 5432
    private static final def DATABASE = "orbisgis_db"
    private static final def HOST = "localhost"

    private static JDBCDataStore ds
    private static final def dbProperties = [database: DATABASE,
                                             user    : USER,
                                             passwd  : PASSWORD,
                                             host    : HOST,
                                             port    : PORT]

    @BeforeAll
    static void beforeAll() {
        ds = POSTGIS.open(dbProperties)
        if (ds) {
            ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
            System.setProperty(POSTGIS_SYS_PROP, "true");
        } else {
            System.setProperty(POSTGIS_SYS_PROP, "false");
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void openClosureTest() {
        def ds = POSTGIS.open {
            password PASSWORD
            user USER
            database DATABASE
            host HOST
            port PORT
            estimatedExtends ESTIMATED_EXTENDS
            fetchSize FETCH_SIZE
            batchInsertSize BATCH_INSERT_SIZE
        }
        assert ds
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:postgresql://$HOST:$PORT/$DATABASE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void openMapTest() {

        def params = [database           : DATABASE,
                      user               : USER,
                      passwd             : PASSWORD,
                      host               : HOST,
                      port               : PORT,
                      "Estimated extends": ESTIMATED_EXTENDS,
                      "fetch size"       : FETCH_SIZE,
                      "Batch insert size": BATCH_INSERT_SIZE]
        def ds = POSTGIS.open(params)
        assert ds
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:postgresql://$HOST:$PORT/$DATABASE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    //TODO move this test to the jdbc-utils module when the feature mechanisms will be add.
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void createReadFeatureSource() {
        def fs = ds.getFeatureSource("geotable")
        assert fs
        assert fs.getCount(Query.ALL) == 2
    }
}
