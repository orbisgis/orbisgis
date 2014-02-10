package org.orbisgis.viewapi.output.ext;

import javax.swing.*;

/**
 * Methods exposed to plugin-ins in order to expands functionality of Logging window
 * @author Nicolas Fortin
 */
public interface MainLogFrame {
    /**
     * Retrieve text pane
     * @return Text pane instance
     */
    JTextPane getLogTextPane(int index);

    /**
     * Retrieve tab label
     * @param index
     * @return Tab label
     */
    String getLogTabName(int index);

    /**
     * Get logging tab count
     * @return Logging tab count
     */
    int getTabCount();

}
