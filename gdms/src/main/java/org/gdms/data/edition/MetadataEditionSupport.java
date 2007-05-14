package org.gdms.data.edition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.gdms.data.driver.DriverException;
import org.gdms.data.metadata.DriverMetadata;
import org.gdms.data.metadata.Metadata;
import org.gdms.spatial.PTTypes;


public class MetadataEditionSupport {

    protected EditableDataSource ds;
    private Metadata originalMetadata;
    protected ArrayList<Field> fields;
    private MetadataEditionListenerSupport mels;

    public MetadataEditionSupport(EditableDataSource ids) {
        this.ds = ids;
        mels = new MetadataEditionListenerSupport(ids);
    }

    public Metadata getDataSourceMetadata() {
        return new ModifiedMetadata();
    }

    public DriverMetadata getDriverMetadata() {
        return new ModifiedDriverMetadata();
    }

    protected Metadata getOriginalMetadata() throws DriverException {
        if (originalMetadata == null) {
            originalMetadata = ds.getOriginalMetadata();

        }

        return originalMetadata;
    }

    protected ArrayList<Field> getFields() throws DriverException {
        if (fields == null) {
            fields = new ArrayList<Field>();
            for (int i = 0; i < getOriginalMetadata().getFieldCount(); i++) {
                String driverType = ds.getOriginalDriverMetadata().getFieldType(i);
                int type = ds.getType(driverType);
                fields.add(new Field(i, getOriginalMetadata().getFieldName(i),
                        driverType, type,
                        ds.getOriginalDriverMetadata().getParamNames(i),
                        ds.getOriginalDriverMetadata().getParamValues(i)));
            }
        }

        return fields;
    }

    public void addField(String name, String type, String[] paramNames,
            String[] paramValues) throws DriverException {
        Field newField = new Field(-1, name, type, ds.getType(type), paramNames, paramValues);

        getFields().add(newField);

        mels.callAddField(getFields().size() - 1);
    }

    public void removeField(int index) throws DriverException{
        getFields().remove(index);

        mels.callRemoveField(index);
    }

    public void setFieldName(int index, String name) throws DriverException{
        getFields().get(index).setName(name);

        mels.callModifyField(index);
    }

    public int getFieldCount() throws DriverException {
        return getFields().size();
    }

    public int getOriginalFieldCount() throws DriverException {
        return getOriginalMetadata().getFieldCount();
    }

    public int getFieldIndexByName(String name) throws DriverException {
        for (int i = 0; i < getFields().size(); i++) {
            if (getFields().get(i).getName().equalsIgnoreCase(name)){
                return i;
            }
        }

        return -1;
    }

    public class ModifiedMetadata implements Metadata {

        public int getFieldCount() throws DriverException {
            return getFields().size();
        }

        public int getFieldType(int fieldId) throws DriverException {
            return getFields().get(fieldId).getType();
        }

        public String getFieldName(int fieldId) throws DriverException {
            return getFields().get(fieldId).getName();
        }

        public String[] getPrimaryKey() throws DriverException {
            return getOriginalMetadata().getPrimaryKey();
        }

        public Boolean isReadOnly(int fieldId) throws DriverException {
            Field f = getFields().get(fieldId);
            int oi = f.getOriginalIndex();
            if (oi != -1) {
                return getOriginalMetadata().isReadOnly(oi);
            } else {
                return false;
            }
        }

    }

    public class ModifiedDriverMetadata implements DriverMetadata {

        public int getFieldCount() throws DriverException {
            return getFields().size();
        }

        public String getFieldName(int fieldId) throws DriverException {
            return getFields().get(fieldId).getName();
        }

        public String getFieldType(int fieldId) throws DriverException {
            return getFields().get(fieldId).getDriverType();
        }

        public String getFieldParam(int fieldId, String paramName) throws DriverException {
            return getFields().get(fieldId).getParams().get(paramName);
        }

        public String[] getParamNames(int fieldId) throws DriverException {
            HashMap<String, String> hash = getFields().get(fieldId).getParams();
            return getStrings(hash.keySet().iterator(), hash.size());
        }

        public String[] getParamValues(int fieldId) throws DriverException {
            HashMap<String, String> hash = getFields().get(fieldId).getParams();
            return getStrings(hash.values().iterator(), hash.size());
        }

        public HashMap<String, String> getFieldParams(int fieldId) throws DriverException {
            return getFields().get(fieldId).getParams();
        }

        public String[] getPrimaryKeys() throws DriverException {
            return ds.getOriginalDriverMetadata().getPrimaryKeys();
        }

    }

    public void addMetadataEditionListener(MetadataEditionListener listener) {
        mels.addEditionListener(listener);
    }

    public void removeMetadataEditionListener(MetadataEditionListener listener) {
        mels.removeEditionListener(listener);
    }

    /**
     * Gets where in the edited DataSource each original field is
     *
     * @return
     * @throws DriverException
     */
    Integer[] getOriginalFieldIndices() throws DriverException {
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i = 0; i < getFields().size(); i++) {
            int oi = getFields().get(i).getOriginalIndex();
            if (oi != -1){
                ret.add(new Integer(oi));
            }
        }

        return ret.toArray(new Integer[0]);
    }

    int getOriginalFieldIndex(int fieldId) throws DriverException {
        return getFields().get(fieldId).getOriginalIndex();
    }

    public void start() {
        fields = null;
        originalMetadata = null;
    }

    public static String[] getStrings(Iterator<String> i, int tam) {
        String[] ret = new String[tam];
        int index = 0;
        while (i.hasNext()) {
            ret[index] = i.next();
            index++;
        }

        return ret;
    }

    public Field getField(int fieldId) throws DriverException {
        return getFields().get(fieldId);
    }

    public int getSpatialFieldIndex() throws DriverException {
        for (int i = 0; i < getFields().size(); i++) {
            if (getFields().get(i).getType() == PTTypes.GEOMETRY) {
                return i;
            }
        }

        throw new RuntimeException("This method only can be invoked on a SpatialDataSource");
    }

}
