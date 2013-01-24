package org.orbisgis.view.sqlconsole.ui.ext;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 * OrbisGIS plugin have access to this interface.
 * Do not break backward compatibility.
 * @author Nicolas Fortin
 */
public interface SQLConsoleEditor {
    /**
     * Give access to the editor in the console.
     * @return Component instance
     */
    RSyntaxTextArea getTextArea();
}
