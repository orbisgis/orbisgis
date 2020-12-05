package org.orbisgis.datastore.h2gis

import org.apache.commons.dbcp.BasicDataSource
import org.geotools.data.Query
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * Test class dedicated to {@link org.orbisgis.datastore.h2gis.H2GIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class H2GISTest {

    @Test
    void openClosureTest() {
        def file = new File("./target/database1.mv.db")
        file.delete()
        assert !file.exists()

        def ds = H2GIS.open {
            password "pwd"
            user "user"
            database "./target/database1"
            autoServer true
            estimatedExtends true
            fetchSize 100
            batchInsertSize 50
        }
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert "pwd" == bds.password
        assert "user" == bds.username
        assert "jdbc:h2:./target/database1;AUTO_SERVER=TRUE" == bds.url.toString()
        assert 100 == ds.fetchSize
        assert 50 == ds.batchInsertSize
    }

    @Test
    void openMapTest() {
        def file = new File("./target/database2.mv.db")
        file.delete()
        assert !file.exists()

        def params = [passwd:"pwd",
                      user:"user",
                      database:"./target/database2",
                      autoserver:true,
                      "Estimated extends":true,
                      "fetch size":100,
                      "Batch insert size":50]
        def ds = H2GIS.open(params)
        assert ds
        assert file.exists()
        def bds = ds.getDataSource().unwrap(BasicDataSource)
        assert "pwd" == bds.password
        assert "user" == bds.username
        assert "jdbc:h2:./target/database2;AUTO_SERVER=TRUE" == bds.url.toString()
        assert 100 == ds.fetchSize
        assert 50 == ds.batchInsertSize
    }

    @Test
    void openH2GISFromPath() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
    }

    @Test
    void createReadFeatureSource() {
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

    @Test
    void eachRowSQL() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        ds.eachRow "SELECT THE_GEOM FROM geotable", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void firstRows() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        println ds.firstRow("select count(*) as nb from geotable").nb
    }

    @Test
    void queryH2GISMetaData() {
        def ds = H2GIS.open("./target/db_h2gis")
        assert ds
        ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
        def concat = ""
        ds.rows "SELECT * FROM geotable", { meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("GEOTABLE 2\n", concat)
    }
}
