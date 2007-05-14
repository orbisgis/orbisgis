package org.gdms.data.edition;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.gdms.data.driver.DBDriver;
import org.gdms.data.driver.DriverException;


public class DBMetadataEditionSupport extends MetadataEditionSupport {

    private DBDriver dbDriver;

    private String tableName;

    private ArrayList<String> fieldsToDelete = new ArrayList<String>();

    public DBMetadataEditionSupport(EditableDataSource ids, String tableName,
            DBDriver dbDriver) {
        super(ids);
        this.dbDriver = dbDriver;
        this.tableName = tableName;
    }

    void commit(Connection con) throws SQLException {
        for (int i = 0; i < fields.size(); i++) {
            Field f = fields.get(i);
            if (f.getOriginalIndex() == -1) {
                dbDriver.execute(con, getAddColumnStatement(f, tableName));
            }
        }

        for (int i = 0; i < fieldsToDelete.size(); i++) {
            dbDriver.execute(con, getRemoveColumnStatement(fieldsToDelete
                    .get(i), tableName));
        }
    }

    @Override
    public void removeField(int index) throws DriverException {
        if (isPK(getFields().get(index))) {
            throw new DriverException("Cannot remove the primary key");
        }

        Field toDelete = fields.get(index);

        if (toDelete.getOriginalIndex() != -1) {
            fieldsToDelete.add(toDelete.getName());
        }
        super.removeField(index);
    }

    protected boolean isPK(Field field) throws DriverException {
        PKEditableDataSource pkds = (PKEditableDataSource) ds;
        String[] pks = pkds.getOriginalDriverMetadata().getPrimaryKeys();
        for (int i = 0; i < pks.length; i++) {
            if (pks[i].equals(field.getName())) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Basic implementation for those types created in this way: "ALTER TABLE " +
     * tableName + " ADD " + name + " " + driverType
     * 
     * @param name
     * @param tableName
     * @param driverType
     * @return
     */
    private String getAddColumnStatement(Field field, String tableName) {
        return "ALTER TABLE "
                + tableName
                + " ADD "
                + field.getName()
                + " "
                + dbDriver.getTypeInAddColumnStatement(field.getDriverType(),
                        field.getParams());
    }

    private String getRemoveColumnStatement(String name, String tableName) {
        return "ALTER TABLE " + tableName + " DROP COLUMN " + name;
    }

    public void start() {
        super.start();
        fieldsToDelete.clear();
    }

    @Override
    public void setFieldName(int index, String name) throws DriverException {
        if (isPK(getFields().get(index))) {
            throw new DriverException("Cannot change primary key name");
        }
        super.setFieldName(index, name);
    }
}
