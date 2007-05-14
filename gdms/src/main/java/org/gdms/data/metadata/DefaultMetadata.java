package org.gdms.data.metadata;

import org.gdms.data.driver.DriverException;
import org.gdms.data.driver.GDBMSDriver;
import org.gdms.spatial.PTTypes;


public class DefaultMetadata implements Metadata {

    private int[] fieldTypes;
    private String[] fieldNames;
    private boolean[] fieldReadOnly;
    private String[] fieldPrimaryKeys;

    public DefaultMetadata(int[] fieldTypes, String[] fieldNames,
            String[] fieldPrimaryKeys,
            boolean[] fieldReadOnly){
        this.fieldTypes = fieldTypes;
        this.fieldNames = fieldNames;
        this.fieldPrimaryKeys = fieldPrimaryKeys;
        this.fieldReadOnly = fieldReadOnly;
    }

    public DefaultMetadata(DriverMetadata driverMetadata, GDBMSDriver driver,
            boolean[] readOnly, String[] pks) throws DriverException {
        int fc = driverMetadata.getFieldCount();
        fieldTypes = new int[fc];
        fieldNames = new String[fc];
        for (int i = 0; i < driverMetadata.getFieldCount(); i++) {
            if (driverMetadata.getFieldType(i).equals(PTTypes.STR_GEOMETRY)) {
                fieldTypes[i] = PTTypes.GEOMETRY;
            } else {
                fieldTypes[i] = driver.getType(driverMetadata.getFieldType(i));
            }
            fieldNames[i] = driverMetadata.getFieldName(i);
        }

        fieldReadOnly = readOnly;
        fieldPrimaryKeys = pks;
    }

    public int getFieldCount() {
        return fieldTypes.length;
    }

    public int getFieldType(int fieldId) {
        return fieldTypes[fieldId];
    }

    public String getFieldName(int fieldId) {
        return fieldNames[fieldId];
    }

    public String[] getPrimaryKey() {
        if (fieldPrimaryKeys == null) {
            return new String[0];
        }
        return fieldPrimaryKeys;
    }

    public Boolean isReadOnly(int fieldId) {
        if (fieldReadOnly == null) {
            return false;
        }
        return fieldReadOnly[fieldId];
    }

}
