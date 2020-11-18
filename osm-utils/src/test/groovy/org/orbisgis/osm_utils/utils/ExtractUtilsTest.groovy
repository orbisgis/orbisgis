package org.orbisgis.osm_utils.utils;

import org.junit.jupiter.api.Test;

/**
 * Test class dedicated to {@link ExtractUtils}.
 *
 * @author Sylvain PALOMINOS (UBS chaire GEOTERA 2020)
 */
class ExtractUtilsTest {

    private static final ExtractUtils extractUtils = new ExtractUtils();

    @Test
    void getColumnSelectorQueryTest() {
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('filter1','filter2','column1','column2','column3')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", ["filter1", "filter2"], ["column1", "column2", "column3"])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('filter1','filter2','column1','column2','column3')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", ["filter1", "filter2"], ["column1", "column2", "column3", null])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('filter1','filter2','column1','column2','column3')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", ["filter1", "filter2", null], ["column1", "column2", "column3"])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('column1','column2','column3')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", [], ["column1", "column2", "column3"])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('filter1','filter2')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", ["filter1", "filter2"], [])
        assert !extractUtils.getColumnSelectorQuery("", ["filter1", "filter2"], ["column1", "column2", "column3"])
        assert !extractUtils.getColumnSelectorQuery(null, ["filter1", "filter2"], ["column1", "column2", "column3"])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('column1','column2','column3')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", null, ["column1", "column2", "column3"])
        assert "SELECT distinct tag_key FROM osmTableTag WHERE tag_key IN ('filter1','filter2')"
                == extractUtils.getColumnSelectorQuery("osmTableTag", ["filter1", "filter2"], null)
    }

    @Test
    void getCountTagsQueryTest() {
        assert "SELECT count(*) AS count FROM osmTableTag WHERE tag_key IN ('tag1','tag2')"
                == extractUtils.getCountTagsQuery("osmTableTag", ["tag1", "tag2"])
        assert "SELECT count(*) AS count FROM osmTableTag WHERE tag_key IN ('tag1','tag2')"
                == extractUtils.getCountTagsQuery("osmTableTag", ["tag1", "tag2", null])
        assert "SELECT count(*) AS count FROM osmTableTag"
                == extractUtils.getCountTagsQuery("osmTableTag", [])
        assert !extractUtils.getCountTagsQuery(null, ["tag1", "tag2"])
        assert "SELECT count(*) AS count FROM osmTableTag" == extractUtils.getCountTagsQuery("osmTableTag", null)
    }

    @Test
    void createWhereFilterTest() {
        assert "(tag_key = 'tag1' AND tag_value IN ('value1','value2')) OR (tag_key = 'tag2' AND tag_value IN ('value')) OR (tag_key = 'tag3')"
                == extractUtils.createWhereFilter([tag1:["value1", "value2"], tag2:"value", tag3: null])
        assert "" == extractUtils.createWhereFilter(null)
    }
}
