package org.orbisgis.view.map.ext;

import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.tool.ToolManager;
import org.orbisgis.view.util.PropertyHost;

/**
 * @author Nicolas Fortin
 */
public interface MapEditorExtension extends EditorDockable, PropertyHost {
    public static final String PROP_MAP_ELEMENT = "mapElement";
    public static final String PROP_TOOL_MANAGER = "toolManager";

    /**
     * Get the loaded map element
     * @return
     */
    public MapElement getMapElement();

    /**
     *  Get the Manager of Automatons
     * @return Manager of Automatons or null if the ToolManager is not instancied
     */
    public ToolManager getToolManager();
}
