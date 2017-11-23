package org.orbisgis.tablegui.impl.jobs;

import org.orbisgis.tablegui.impl.TableEditor;

/**
 * Close TableEditor in Swing Thread
 *
 * @author Nicolas Fortin
 */
public class CloseTableEditorJob implements Runnable {
    private final TableEditor tableEditor;

    private CloseTableEditorJob(TableEditor tableEditor) {
        this.tableEditor = tableEditor;
    }

    @Override
    public void run() {
        tableEditor.getDockingParameters().setVisible(false);
    }
}
