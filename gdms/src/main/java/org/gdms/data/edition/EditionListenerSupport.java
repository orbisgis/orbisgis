package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.InternalDataSource;


public class EditionListenerSupport {
    private ArrayList<EditionListener> listeners = new ArrayList<EditionListener>();
    private int dispatchingMode = InternalDataSource.DISPATCH;   
    private MultipleEditionEvent multipleEditionEvent;
    private InternalDataSource dataSource;
    
    public EditionListenerSupport(InternalDataSource ds) {
        this.dataSource = ds;
    }
    
    public void addEditionListener(EditionListener listener) {
        listeners.add(listener);
    }
    
    public void removeEditionListener(EditionListener listener) {
        listeners.remove(listener);
    }
    
    public void callSetFieldValue(long rowIndex, int fieldIndex, boolean undoRedo) {
        EditionEvent event = new EditionEvent(rowIndex, fieldIndex, 
                EditionEvent.MODIFY, dataSource, undoRedo);
        manageEvent(event);
    }
    
    public void callDeleteRow(long rowIndex, boolean undoRedo) {
        EditionEvent event = new EditionEvent(rowIndex, -1, 
                EditionEvent.DELETE, dataSource, undoRedo);
        manageEvent(event);
    }
    
    public void callInsert(long rowIndex, boolean undoRedo) {
        EditionEvent event = new EditionEvent(rowIndex, -1, 
                EditionEvent.INSERT, dataSource, undoRedo);
        manageEvent(event);
    }
    
    private void manageEvent(EditionEvent event) {
        if (dispatchingMode == InternalDataSource.DISPATCH) {
            callModification(event);
        } else if (dispatchingMode == InternalDataSource.STORE) {
            multipleEditionEvent.addEvent(event);
        }
    }
    
    public void setDispatchingMode(int dispatchingMode) {
        int previousMode = this.dispatchingMode;
        this.dispatchingMode = dispatchingMode;
        if (previousMode == InternalDataSource.STORE) {
            callMultipleModification(multipleEditionEvent);
            multipleEditionEvent = null;
        } 
        
        if (dispatchingMode == InternalDataSource.STORE) {
            multipleEditionEvent = new MultipleEditionEvent();
        }
    }
    
    private void callModification(EditionEvent e){
        for (EditionListener listener : listeners) {
            listener.singleModification(e);
        }
    }

    private void callMultipleModification(MultipleEditionEvent e) {
        for (EditionListener listener : listeners) {
            listener.multipleModification(e);
        }
    }

    public int getDispatchingMode() {
        return dispatchingMode;
    }
}
