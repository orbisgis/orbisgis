package org.gdms;

import com.vividsolutions.jts.geom.Geometry;
import org.gdms.data.values.Value;

/**
 *
 * @author Antoine Gourlay
 */
public class GdmsTestData extends TestData<Value, Geometry> {

        public GdmsTestData(String name, boolean write, int driver, long rowCount, boolean isDB, String noPKField, boolean hasRepeatedRows) {
                super(name, write, driver, rowCount, isDB, noPKField, hasRepeatedRows);
        }

        
}
