package org.orbisgis.datastore.h2gis

import org.apache.commons.dbcp.BasicDataSource
import org.geotools.data.Query
import org.junit.jupiter.api.Test

/**
 * Test class dedicated to {@link org.orbisgis.datastore.h2gis.H2GIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class H2GISTest {

    // Some Database configuration data
    private static final def PASSWORD = "psw"
    private static final def USER = "user"
    private static final def PATH = "./target/database"
    private static final def AUTO_SERVER = true
    private static final def ESTIMATED_EXTENDS = true
    private static final def FETCH_SIZE = 100
    private static final def BATCH_INSERT_SIZE = 50

    @Test
    void openClosureTest() {
        def file = new File("./target/database1.mv.db")
        file.delete()
        assert !file.exists()

        def ds = H2GIS.open {
            password PASSWORD
            user USER
            database "${PATH}1"
            autoServer AUTO_SERVER
            estimatedExtends ESTIMATED_EXTENDS
            fetchSize FETCH_SIZE
            batchInsertSize BATCH_INSERT_SIZE
        }
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:h2:${PATH}1;AUTO_SERVER=TRUE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    void openMapTest() {
        def file = new File("${PATH}2.mv.db")
        file.delete()
        assert !file.exists()

        def params = [passwd                : PASSWORD,
                      user                  : USER,
                      database              : "${PATH}2",
                      autoserver            : AUTO_SERVER,
                      "Estimated extends"   : ESTIMATED_EXTENDS,
                      "fetch size"          : FETCH_SIZE,
                      "Batch insert size"   : BATCH_INSERT_SIZE]
        def ds = H2GIS.open(params)
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert PASSWORD == bds.password
        assert USER == bds.username
        assert "jdbc:h2:${PATH}2;AUTO_SERVER=TRUE" == bds.url.toString()
        assert FETCH_SIZE == ds.fetchSize
        assert BATCH_INSERT_SIZE == ds.batchInsertSize
    }

    @Test
    void openPathTest() {
        def ds = H2GIS.open(PATH)
        assert ds
        assert new File("${PATH}.mv.db").exists()
    }

    @Test
    //TODO move this test to the jdbc-utils module when the feature mechanisms will be add.
    void createReadFeatureSourceTest() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def fs = ds.getFeatureSource("GEOTABLE")
        assert fs
        assert fs.getCount(Query.ALL)==2
    }
}
