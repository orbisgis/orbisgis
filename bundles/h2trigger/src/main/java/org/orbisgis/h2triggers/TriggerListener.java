package org.orbisgis.h2triggers;

/**
 * @author Nicolas Fortin
 */
public interface TriggerListener {

    void fire(String schemaName,String triggerName,String tableName);

}
