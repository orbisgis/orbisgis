package org.orbisgis.osm_utils.utils

import org.h2gis.functions.factory.H2GISDBFactory
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

import java.sql.Connection

class DataUtilsTest {
    private static Connection CONNECTION
    private static String DB_NAME

    @BeforeAll
    static void beforeAll() {
        DB_NAME = (this.simpleName.postfix()).toUpperCase()
        CONNECTION = H2GISDBFactory.createSpatialDataBase("./target/" + DB_NAME)
    }

    @Test
    void loadTest() {
        def prefix = "A".postfix().toUpperCase()
        def filePath = new File("/home/sylvain/Documents/Workspace/orbisgis/osm-utils/src/test/resources/org/orbisgis/osm_utils/utils/sample.osm").absolutePath
        assert DataUtils.load(CONNECTION, prefix, filePath)
        def tables = CONNECTION.getTableNames(null, null, null, null)
        def tablePrefix = DB_NAME + ".PUBLIC." + prefix

        assert tables.contains(tablePrefix + "_NODE")
        assert 5 == CONNECTION.getRowCount(prefix + "_NODE")

        assert tables.contains(tablePrefix + "_NODE_TAG")
        assert 2 == CONNECTION.getRowCount(prefix + "_NODE_TAG")

        assert tables.contains(tablePrefix + "_WAY")
        assert 1 == CONNECTION.getRowCount(prefix + "_WAY")

        assert tables.contains(tablePrefix + "_WAY_TAG")
        assert 1 == CONNECTION.getRowCount(prefix + "_WAY_TAG")

        assert tables.contains(tablePrefix + "_WAY_NODE")
        assert 3 == CONNECTION.getRowCount(prefix + "_WAY_NODE")

        assert tables.contains(tablePrefix + "_RELATION")
        assert 1 == CONNECTION.getRowCount(prefix + "_RELATION")

        assert tables.contains(tablePrefix + "_RELATION_TAG")
        assert 2 == CONNECTION.getRowCount(prefix + "_RELATION_TAG")

        assert tables.contains(tablePrefix + "_NODE_MEMBER")
        assert 2 == CONNECTION.getRowCount(prefix + "_NODE_MEMBER")

        assert tables.contains(tablePrefix + "_WAY_MEMBER")
        assert 1 == CONNECTION.getRowCount(prefix + "_WAY_MEMBER")

        assert tables.contains(tablePrefix + "_RELATION_MEMBER")
        assert 0 == CONNECTION.getRowCount(prefix + "_RELATION_MEMBER")
    }

    @Test
    void badLoadTest() {
        def prefix = "A".postfix()
        def filePath = new File("/home/sylvain/Documents/Workspace/orbisgis/osm-utils/src/test/resources/org/orbisgis/osm_utils/utils/sample.osm").absolutePath
        assert !DataUtils.load(null, prefix, filePath)
        assert !DataUtils.load(CONNECTION, null, filePath)
        assert !DataUtils.load(null, null, filePath)
        assert !DataUtils.load(CONNECTION, prefix, null)
        assert !DataUtils.load(null, prefix, null)
        assert !DataUtils.load(CONNECTION, null, null)
        assert !DataUtils.load(null, null, null)

        assert !DataUtils.load(CONNECTION, "é#%ø£µ**/", filePath)
    }
}