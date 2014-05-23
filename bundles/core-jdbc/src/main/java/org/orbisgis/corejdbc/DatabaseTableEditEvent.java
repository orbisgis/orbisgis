package org.orbisgis.corejdbc;

/**
 * @author Nicolas Fortin
 */
public class DatabaseTableEditEvent  extends java.util.EventObject {
    public DatabaseTableEditEvent(String tableName) {
        super(tableName);
    }
    public String getTableName() {
        return source.toString();
    }
}
