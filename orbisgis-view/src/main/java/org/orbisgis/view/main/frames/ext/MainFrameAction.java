package org.orbisgis.view.main.frames.ext;

import org.orbisgis.view.components.actions.ActionFactoryService;

/**
 * @author Nicolas Fortin
 */
public interface MainFrameAction  extends ActionFactoryService<MainWindow> {
    //Main menu keys
    public static final String MENU_FILE = "file";
    public static final String MENU_EXIT = "exitapp";
    public static final String MENU_TOOLS = "tools";
    public static final String MENU_CONFIGURE = "configure";
    public static final String MENU_LOOKANDFEEL = "lookAndFeel";
    public static final String MENU_WINDOWS = "windows";
}
