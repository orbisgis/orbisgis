package org.orbisgis.index;

import com.vividsolutions.jts.geom.Envelope;
import org.junit.Test;
import org.orbisgis.mapeditorapi.Index;
import org.orbisgis.mapeditorapi.IndexProvider;

import java.io.File;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Nicolas Fortin
 */
public class IndexProviderTest {

    @Test //(timeout = 500)
    public void testBounds() throws Exception {
        IndexProvider provider = new MVRTreeProvider();
        Index<String> index = provider.createIndex(new File("target/"), String.class);
        index.insert(new Envelope(3,4,2,3), "A");
        index.insert(new Envelope(4,5,6,7), "B");
        index.insert(new Envelope(6,7,3,4), "C");
        index.insert(new Envelope(7,8,1,2), "D");

        Iterator<String> itResult = index.query(new Envelope(3,6,2,3));
        assertTrue(itResult.hasNext());
        assertEquals("A", itResult.next());
        assertTrue(itResult.hasNext());
        assertEquals("C", itResult.next());
        assertFalse(itResult.hasNext());
    }
}
