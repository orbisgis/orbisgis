package org.orbisgis.mainframe.api;

import javax.swing.JFrame;
import java.beans.VetoableChangeListener;

/**
 * Methods that can be accessed by plug-ins
 * @author Nicolas Fortin
 */
public interface MainWindow {
    // Vetoable properties
    public static final String WINDOW_VISIBLE = "WINDOW_VISIBLE";

    /**
     * As long as your MainFrameAction is alive (disposeActions has not been called)
     * the returned JFrame instance can be used.
     * @return The main frame instance
     */
    JFrame getMainFrame();

    /**
     * Add a VetoableChangeListener for a specific property.  The listener
     * will be invoked only when a call on fireVetoableChange names that
     * specific property.
     * The same listener object may be added more than once.  For each
     * property,  the listener will be invoked the number of times it was added
     * for that property.
     * If <code>propertyName</code> or <code>listener</code> is null, no
     * exception is thrown and no action is taken.
     *
     * @param propertyName  The name of the property to listen on.
     * @param listener  The VetoableChangeListener to be added
     */
    public void addVetoableChangeListener(
            String propertyName,
            VetoableChangeListener listener);

    /**
     * Remove a VetoableChangeListener from the listener list.
     * This removes a VetoableChangeListener that was registered
     * for all properties.
     * If <code>listener</code> was added more than once to the same event
     * source, it will be notified one less time after being removed.
     * If <code>listener</code> is null, or was never added, no exception is
     * thrown and no action is taken.
     *
     * @param listener  The VetoableChangeListener to be removed
     */
    public void removeVetoableChangeListener(VetoableChangeListener listener);


    public MainStatusBar getStatusBar();

}
