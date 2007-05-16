package org.gdms.data.edition;

import java.util.ArrayList;

import org.gdms.data.values.Value;
import org.gdms.data.values.ValueCollection;
import org.gdms.data.values.ValueFactory;
import org.gdms.driver.DBDriver;
import org.gdms.driver.DriverException;


public class PKTransactionSupport extends RowTransactionSupport {

    private ArrayList<DeleteEditionInfo> deletedPKs = new ArrayList<DeleteEditionInfo>();
    private ArrayList<Boolean> original = new ArrayList<Boolean>();
    
    private ArrayList<EditionInfo> info = new ArrayList<EditionInfo>();
    private String tableName;
    private DBDriver driver;
    private ArrayList<DeleteEditionInfo> backDeletedPKs;
    private ArrayList<Boolean> backOriginal;
    private ArrayList<EditionInfo> backInfo;
    
    public PKTransactionSupport(PKEditableDataSource ds, String tableName,
            DBDriver driver, MetadataEditionSupport mes) throws DriverException {
        super(ds, mes);
        this.tableName = tableName;
        this.driver = driver;
    }

    private PKEditableDataSource getDS(){
        return (PKEditableDataSource) ds;
    }
    
    @Override
    public void beginTrans(long rc) throws DriverException {
        if (((PKEditableDataSource)ds).getPKNames().length ==0 ){
            throw new DriverException("No primary key was found");
        }
        super.beginTrans(rc);
        for (int i = 0; i < rc; i++) {
            original.add(Boolean.TRUE);
            info.add(new NoEditionInfo(((PKEditableDataSource)ds).getPKValue(i), i));
        }
    }

    @Override
    public void close() throws DriverException {
        super.close();
    }

    @Override
    public void restoreStatus() {
        deletedPKs = backDeletedPKs;
        original = backOriginal;
        info = backInfo;
        super.restoreStatus();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void saveStatus() {
        backDeletedPKs = (ArrayList<DeleteEditionInfo>) deletedPKs.clone();
        backOriginal = (ArrayList<Boolean>) original.clone();
        backInfo = (ArrayList<EditionInfo>) info.clone();
        super.saveStatus();
    }

    @Override
    public void deleteRow(long rowId) throws DriverException {

        super.deleteRow(rowId);
        boolean isOriginal = original.get((int) rowId);
        original.remove((int) rowId);
        if (isOriginal){
            EditionInfo ei = info.get((int) rowId);
            if (ei instanceof NoEditionInfo){
                DeleteEditionInfo dei = new DeleteEditionInfo(((NoEditionInfo)ei).getPk(),
                        getDS().getPKNames(), tableName, driver);
                deletedPKs.add(dei);
            } else if (ei instanceof UpdateEditionInfo) {
                DeleteEditionInfo dei = new DeleteEditionInfo(((UpdateEditionInfo)ei).getOriginalPK(),
                        getDS().getPKNames(), tableName, driver);
                deletedPKs.add(dei);
            } 
        }
        info.remove((int) rowId);
    }

    @Override
    public Value getFieldValue(long rowIndex, int fieldId) throws DriverException {
        return super.getFieldValue(rowIndex, fieldId);
    }

    @Override
    public long getRowCount() {
        return super.getRowCount();
    }
    
    @Override
    public long insertRow(Value[] values) throws DriverException {
        long index = super.insertRow(values);

        InsertEditionInfo iei = new InsertEditionInfo(tableName,
                internalBuffer, index, getDS().getFieldNames(), driver);
        original.add(Boolean.FALSE);
        info.add(iei);
        
        return index;
    }

    @Override
    public long insertRowAt(long rowIndex, Value[] values) throws DriverException {
        long index = super.insertRowAt(rowIndex, values);
        InsertEditionInfo iei = new InsertEditionInfo(tableName,
                internalBuffer, index, getDS().getFieldNames(), driver);
        original.add((int) rowIndex, Boolean.FALSE);
        info.add((int)rowIndex, iei);
        
        return index;
    }

    /**
     * Gets the values of the original row
     * 
     * @param rowIndex
     *            index of the row to be retrieved
     * 
     * @return Row values
     * 
     * @throws DriverException
     *             if the operation fails
     */
    private Value[] getOriginalRow(long rowIndex) throws DriverException {
        Value[] ret = new Value[ds.getFieldCount()];
        
        Integer[] original = mes.getOriginalFieldIndices();
        for (int i = 0; i < original.length; i++) {
            ret[i] = ds.getOriginalFieldValue(rowIndex, original[i]);
        }
        
        for (int i = original.length; i < ret.length; i++) {
            ret[i] = ValueFactory.createNullValue();
        }
    
        return ret;
    }

    public long setFieldValue(long row, int fieldId, Value value) throws DriverException {
        EditionInfo ei = info.get((int) row);
        
        Value[] originalRow = null;
        
        if (ei instanceof NoEditionInfo) {
            NoEditionInfo nei = (NoEditionInfo) ei;
            
            originalRow = getOriginalRow(nei.getIndex());
        }
        
        long index = super.setFieldValue(row, fieldId, value, originalRow);
        
        if (original.get((int)row)){
            /*
             * Avoid store two times an UpdateEditionInfo object 
             */
            if (ei instanceof NoEditionInfo) {
                ValueCollection pk = ((NoEditionInfo) ei).getPk();

                UpdateEditionInfo uei = new UpdateEditionInfo(tableName,getDS().getPKNames(),
                        getDS(), driver, (int) index, pk, internalBuffer);
                info.set((int) row, uei);
            }
        }
        
        return index;
    }

    public String getInstruction(int index) throws DriverException {
        EditionInfo ei = null;
        if (index < deletedPKs.size()){
            ei = deletedPKs.get(index);
        } else {
            ei = info.get(index - deletedPKs.size());
        }
        return ei.getSQL();
    }

    public int getInstructionCount() {
        return info.size() + deletedPKs.size();
    }

}
