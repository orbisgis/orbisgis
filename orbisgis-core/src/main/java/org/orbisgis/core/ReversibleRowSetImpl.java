package org.orbisgis.core;

import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.api.ReversibleRowSet;

import javax.sql.DataSource;
import javax.swing.event.UndoableEditListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicolas Fortin
 */
public class ReversibleRowSetImpl extends ReadRowSetImpl implements ReversibleRowSet {
    private final List<UndoableEditListener> undoListenerList = new ArrayList<>();

    public ReversibleRowSetImpl(DataSource dataSource, TableLocation location) {
        super(dataSource, location);
    }


    @Override
    public void addUndoableEditListener(UndoableEditListener listener) {
        undoListenerList.add(listener);
    }

    @Override
    public void removeUndoableEditListener(UndoableEditListener listener) {
        undoListenerList.remove(listener);
    }
}
