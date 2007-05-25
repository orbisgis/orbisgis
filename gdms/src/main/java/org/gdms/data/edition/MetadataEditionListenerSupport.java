package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.InternalDataSource;


public class MetadataEditionListenerSupport {
    private InternalDataSource dataSource;
    private ArrayList<MetadataEditionListener> listeners = new ArrayList<MetadataEditionListener>();
    
    public MetadataEditionListenerSupport(InternalDataSource ds) {
        this.dataSource = ds;
    }

    public void callAddField(int fieldIndex) {
        FieldEditionEvent e = new FieldEditionEvent(
                fieldIndex,
                dataSource);
        
        for (MetadataEditionListener listener : listeners) {
            listener.fieldAdded(e);
        }
    }

    public void callRemoveField(int fieldIndex) {
        FieldEditionEvent e = new FieldEditionEvent(
                fieldIndex,
                dataSource);
        
        for (MetadataEditionListener listener : listeners) {
            listener.fieldRemoved(e);
        }
    }

    public void callModifyField(int fieldIndex) {
        FieldEditionEvent e = new FieldEditionEvent(
                fieldIndex,
                dataSource);
        
        for (MetadataEditionListener listener : listeners) {
            listener.fieldModified(e);
        }
    }

    public void addEditionListener(MetadataEditionListener listener) {
        listeners.add(listener);
    }

    public void removeEditionListener(MetadataEditionListener listener) {
        listeners.remove(listener);
    }

}
