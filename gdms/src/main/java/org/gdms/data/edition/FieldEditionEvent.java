package org.gdms.data.edition;

import org.gdms.data.InternalDataSource;
import org.gdms.data.GDBMSEvent;

public class FieldEditionEvent extends GDBMSEvent {

    private int fieldIndex;

    public FieldEditionEvent(int fieldIndex, InternalDataSource ds) {
        super(ds);
        this.fieldIndex = fieldIndex;
    }

    public int getFieldIndex() {
        return fieldIndex;
    }


}
