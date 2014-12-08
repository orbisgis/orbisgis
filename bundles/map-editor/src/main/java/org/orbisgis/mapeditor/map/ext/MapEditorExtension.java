package org.orbisgis.mapeditor.map.ext;

import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.sif.docking.PropertyHost;
import org.orbisgis.sif.edition.EditorDockable;
import org.orbisgis.mapeditor.map.tool.ToolManager;

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
