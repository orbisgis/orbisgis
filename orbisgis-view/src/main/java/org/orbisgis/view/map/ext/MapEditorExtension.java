package org.orbisgis.view.map.ext;

import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.map.tool.ToolManager;

/**
 * @author Nicolas Fortin
 */
public interface MapEditorExtension extends EditorDockable {

    /**
     * Get the loaded map context
     * @return
     */
    public MapContext getMapContext();

    /**
     *  Get the Manager of Automatons
     * @return Manager of Automatons or null if the ToolManager is not instancied
     */
    public ToolManager getToolManager();
}
