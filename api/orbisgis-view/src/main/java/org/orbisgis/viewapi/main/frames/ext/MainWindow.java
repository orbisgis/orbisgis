package org.orbisgis.viewapi.main.frames.ext;

import javax.swing.JFrame;

/**
 * Methods that can be accessed by plug-ins
 * @author Nicolas Fortin
 */
public interface MainWindow {
    /**
     * As long as your MainFrameAction is alive (disposeActions has not been called)
     * the returned JFrame instance can be used.
     * @return The main frame instance
     */
    JFrame getMainFrame();
}
