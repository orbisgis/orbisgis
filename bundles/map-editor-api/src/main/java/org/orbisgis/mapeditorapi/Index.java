package org.orbisgis.mapeditorapi;

import com.vividsolutions.jts.geom.Envelope;

import java.util.Iterator;

/**
 * Index instance
 * @author Nicolas Fortin
 */
public interface Index<T> extends AutoCloseable {
    /**
     * Insert a value in the index
     * @param envelope Value position
     * @param value Value
     */
    void insert(Envelope envelope, T value);

    /**
     * Query index using an envelope
     * @param envelope Query bounds
     * @return Iterator of found elements
     */
    Iterator<T> query(Envelope envelope);
}
