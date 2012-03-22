package org.orbisgis.core.ui.plugins.ows.ui;

/**
 * Stands for a UI asynchronous command.
 * @author cleglaun
 */
public interface OwsAsyncCommand {
    
    /**
     * Should do the task that updates a data model related to a UI component
     * (typically a JComboBox or a JList).
     */
    public void doJob();
}
