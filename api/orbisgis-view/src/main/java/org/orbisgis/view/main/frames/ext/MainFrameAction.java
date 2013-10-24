package org.orbisgis.view.main.frames.ext;

import org.orbisgis.view.components.actions.ActionFactoryService;

/**
 * Implement this interface to define additional menu items.
 * If this interface is modified, increment the package version trough package-info.java .
 * @author Nicolas Fortin
 */
public interface MainFrameAction  extends ActionFactoryService<MainWindow> {
    //Main menu keys
    public static final String MENU_FILE = "file";
    public static final String MENU_EXIT = "exitapp";
    public static final String MENU_TOOLS = "tools";
    public static final String MENU_CONFIGURE = "configure";
    public static final String MENU_SAVE = "save";
}
