package org.orbisgis.view.toc.actions.cui.freqChart.dataModel;

import java.util.EventObject;

/**
 * DataChanged
 * @author sennj
 */
public class DataChanged extends EventObject {

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
