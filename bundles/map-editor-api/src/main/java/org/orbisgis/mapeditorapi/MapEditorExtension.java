package org.orbisgis.mapeditorapi;

import org.orbisgis.sif.docking.PropertyHost;
import org.orbisgis.sif.edition.EditorDockable;

/**
 * @author Nicolas Fortin
 */
public interface MapEditorExtension extends EditorDockable, PropertyHost {
    public static final String PROP_MAP_ELEMENT = "mapElement";
    public static final String PROP_TOOL_MANAGER = "toolManager";

    /**
     * @return loaded map element
     */
    public MapElement getMapElement();

    /**
     * @return Internal data of maps manager
     */
    public MapsManagerData getMapsManagerData();
}
