package org.orbisgis.view.map.ext;

import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.map.tool.Automaton;

/**
 * @author Nicolas Fortin
 */
public interface MapEditorExtension extends EditorDockable {
    /**
     * Change the current tool to the provided one.
     * @param automaton New tool to use.Null will set the default tool of MapEditor.
     */
    public void setTool(Automaton automaton);

    /**
     * Get the current loaded tool.
     * @param automaton Loaded tool
     * @return Automaton instance
     */
    public Automaton getCurrentTool();
}
