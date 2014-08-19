package org.orbisgis.index;

import org.h2.mvstore.MVStore;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.orbisgis.mapeditorapi.Index;
import org.orbisgis.mapeditorapi.IndexProvider;
import org.osgi.service.component.annotations.Component;

import java.io.File;

/**
 * Iterate over statements in document
 * @author Nicolas Fortin
 */
@Component
public class MVRTreeProvider implements IndexProvider {
    private static final int PAGE_SPLIT_SIZE = 1000;

    @Override
    public <T> Index<T> createIndex(File file, Class<T> valueType) {
        MVStore s = new MVStore.Builder().
                fileName(file.getAbsolutePath()).pageSplitSize(PAGE_SPLIT_SIZE).open();
        MVRTreeMap<T> r = s.openMap("data",
                new MVRTreeMap.Builder<T>().dimensions(2));
        return new MVRTreeIndex<>(r);
    }
}
