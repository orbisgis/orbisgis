package org.orbisgis.datastore.postgis

import org.geotools.data.Query
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertNotNull

/**
 * Test class dedicated to {@link org.orbisgis.datastore.postgis.POSTGIS}
 *
 * @author Erwan Bocher (CNRS 2020)
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class POSTGISTest {

    def static dbProperties = [dbtype : "postgis",
                               database: 'orbisgis_db',
                               user        : 'orbisgis',
                               passwd    : 'orbisgis',
                               host         : 'localhost',
                               port: 5432

    ]
    static POSTGIS ds;

    @BeforeAll
    static void init() {
        ds = POSTGIS.open(dbProperties)
        if(ds){
            ds.execute("""
                DROP TABLE IF EXISTS geotable;
                CREATE TABLE geotable (id int, the_geom geometry(point));
                INSERT INTO geotable VALUES (1, 'POINT(10 10)'::GEOMETRY), (2, 'POINT(1 1)'::GEOMETRY);
        """)
            System.setProperty("test.postgis",true);
        }
        else{
            System.setProperty("test.postgis",false);
        }
    }


    @Test
    @EnabledIfSystemProperty(named = "test.postgis", matches = "true")
    void loadPostGIS() {
        assertNotNull(ds)
    }

    @Test
    void createReadFeatureSource() {
        def fs = ds.getFeatureSource("geotable")
        assert fs
        assert fs.getCount(Query.ALL)==2
    }

    @Test
    void eachRowSQL() {
        def concat = ""
        ds.eachRow "SELECT THE_GEOM FROM geotable", { row -> concat += "$row.the_geom\n" }
        assertEquals("POINT (10 10)\nPOINT (1 1)\n", concat)
    }

    @Test
    void firstRows() {
        def concat = ""
        println ds.firstRow("select count(*) as nb from geotable").nb
    }

    @Test
    void queryH2GISMetaData() {
        def concat = ""
        ds.rows "SELECT * FROM geotable", { meta ->
            concat += "${meta.getTableName(1)} $meta.columnCount\n"
        }
        assertEquals("GEOTABLE 2\n", concat)
    }
}
