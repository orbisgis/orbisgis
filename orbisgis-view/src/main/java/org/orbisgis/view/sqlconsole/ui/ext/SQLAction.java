package org.orbisgis.view.sqlconsole.ui.ext;

import org.orbisgis.view.components.actions.MenuItemService;
import org.orbisgis.view.sqlconsole.ui.SQLConsolePanel;

/**
 * @author Nicolas Fortin
 */
public interface SQLAction extends MenuItemService<SQLConsolePanel> {
    // Action, MENU IDs
    public static final String A_EXECUTE = "M_EXECUTE";
    public static final String A_CLEAR = "M_CLEAR";
    public static final String A_OPEN = "M_OPEN";
    public static final String A_SAVE = "M_SAVE";
    public static final String A_SEARCH = "M_SEARCH";
    public static final String A_FORMAT = "M_SEARCH";
    public static final String A_QUOTE = "M_QUOTE";
    public static final String A_UNQUOTE = "M_UNQUOTE";
    public static final String A_SQL_LIST = "M_SQL_LIST";
}
