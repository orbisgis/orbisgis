package org.orbisgis.view.toc.actions.cui.freqChart.dataModel;

import java.util.EventObject;

/**
 * Data changed event
 * @author sennj
 */
public class DataChanged extends EventObject {

    /** the type of the data change */
    public DataChangedType dataType;

    public enum DataChangedType {

        DATACHANGE
    }

    /**
     * DataChanged constructor
     * @param source
     * @param datachangedtype
     */
    public DataChanged(Object source, DataChangedType datachangedtype) {
        super(source);
        this.dataType = datachangedtype;
    }
}
