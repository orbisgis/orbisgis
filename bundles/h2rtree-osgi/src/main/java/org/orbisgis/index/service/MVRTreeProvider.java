package org.orbisgis.index.service;

import org.orbisgis.mapeditorapi.Index;
import org.orbisgis.mapeditorapi.IndexProvider;
import org.osgi.service.component.annotations.Component;
import org.orbisgis.index.impl.MVRTreeIndex;

import java.io.File;

/**
 * Iterate over statements in document
 * @author Nicolas Fortin
 */
@Component
public class MVRTreeProvider implements IndexProvider {

    @Override
    public <T> Index<T> createIndex(File file, Class<T> valueType) {
        return new MVRTreeIndex<>(file);
    }
}
