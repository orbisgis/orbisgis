package org.orbisgis.mapeditorapi;

import org.orbisgis.coremap.layerModel.OwsMapContext;

import java.io.File;

/**
 * @author Nicolas Fortin
 */
public class MapElementTest {
    public void testInstanciation() {
        MapElement mapElement = new MapElement(new OwsMapContext(null), new File("target/testmapcontext.ows"));
    }
}
