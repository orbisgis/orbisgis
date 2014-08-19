/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.index;

import com.vividsolutions.jts.geom.Envelope;
import org.h2.mvstore.rtree.MVRTreeMap;
import org.h2.mvstore.rtree.SpatialKey;
import org.orbisgis.mapeditorapi.Index;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * H2 Index implementation bridge
 * @author Nicolas Fortin
 */
public class MVRTreeIndex<T> implements Index<T> {
    private MVRTreeMap<T> treeMap;
    private AtomicLong id = new AtomicLong(0);

    public MVRTreeIndex(MVRTreeMap<T> treeMap) {
        this.treeMap = treeMap;
    }

    @Override
    public void insert(Envelope envelope, T value) {
        treeMap.add(spatialKeyFromEnvelope(envelope), value);
    }

    private SpatialKey spatialKeyFromEnvelope(Envelope envelope) {
        return new SpatialKey(id.getAndAdd(1), (float)envelope.getMinX(), (float)envelope.getMaxX(),
            (float)envelope.getMinY(), (float)envelope.getMaxY());
    }

    @Override
    public Iterator<T> query(Envelope envelope) {
        MVRTreeMap.RTreeCursor cursor = treeMap.findIntersectingKeys(spatialKeyFromEnvelope(envelope));
        return new ValueIterator<>(treeMap, cursor);
    }

    @Override
    public void close() throws Exception {
        treeMap.clear();
    }

    /**
     * Iterate over results
     * @param <T> Map value type
     */
    private static class ValueIterator<T> implements Iterator<T> {
        private MVRTreeMap<T> treeMap;
        private MVRTreeMap.RTreeCursor cursor;

        private ValueIterator(MVRTreeMap<T> treeMap, MVRTreeMap.RTreeCursor cursor) {
            this.treeMap = treeMap;
            this.cursor = cursor;
        }

        @Override
        public boolean hasNext() {
            return cursor.hasNext();
        }

        @Override
        public T next() {
            return treeMap.get(cursor.next());
        }

        @Override
        public void remove() {
            cursor.remove();
        }
    }
}
