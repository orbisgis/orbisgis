package org.orbisgis.core.ui.preferences.lookandfeel;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class OrbisGISIcon {

	// GENERAL

	public static final ImageIcon ORBISGIS_LOGOMINI = IconLoader
			.getIcon("mini_orbisgis.png");

	public static final ImageIcon ORBISGIS_SPLASH = IconLoader
			.getIcon("logo_orbisgis.png");

	/**
	 * ************************************************** MAIN MENU
	 * ***********************************************************
	 */
	// FILE SUB MENUS
	// Exit
	public static final ImageIcon EXIT_ICON = IconLoader.getIcon("exit.png");
	// Save
	public static final ImageIcon SAVE_ICON = IconLoader.getIcon("disk.png");
	// Change Workspace
	public static final ImageIcon OPEN_WINDOW = IconLoader
			.getIcon("application_go.png");

	// HELP SUB MENUS
	// About
	public static final ImageIcon ABOUT_ICON = null;
	// Online
	public static final ImageIcon ONLINE_ICON = null;

	// VIEW SUB MENUS
	// Editors
	public static final ImageIcon EDITORS_ICON = IconLoader
			.getIcon("documentviewer.png");
	// Output
	public static final ImageIcon OUTPUT_ICON = IconLoader
			.getIcon("format-justify-fill.png");
	// Memory
	public static final ImageIcon MEMORY_ICON = IconLoader
			.getIcon("utilities-system-monitor.png");
	// Geocognition
	public static final ImageIcon GEOCOGNITION_ICON = ORBISGIS_LOGOMINI;
	// Beanshell
	public static final ImageIcon BEANSHELL_ICON = IconLoader
			.getIcon("page_white_cup.png");
	// Information
	public static final ImageIcon GEOINFORMATION = IconLoader
			.getIcon("information_geo.png");
	// Geocatalog
	public static final ImageIcon GEOCATALOG_ICON = IconLoader
			.getIcon("geocatalog.png");
	// SQL Consol
	public static final ImageIcon SQLCONSOLE_ICON = IconLoader
			.getIcon("script_code.png");
	// Geomark
	public static final ImageIcon GEOMARK_ICON = IconLoader
			.getIcon("world_add.png");

	/**
	 * ************************************************** MENU & POPUPMENU
	 * *******************************************************
	 */
	// PopupMenu TOC
	// Edit Legend
	public static final ImageIcon EDIT_LEGEND = IconLoader
			.getIcon("palette.png");
	// Table
	public static final ImageIcon SHOW_ATTRIBUTES = IconLoader
			.getIcon("openattributes.png");
	// Group layers
	public static final ImageIcon GROUP_LAYERS = IconLoader
			.getIcon("arrow_in.png");
	// Create layer
	public static final ImageIcon POPUP_TOC_LAYERS_CREATE_ICON = IconLoader
			.getIcon("layers.png");
	// Set Inactive
	public static final ImageIcon LAYER_STOPEDITION = IconLoader
			.getIcon("stop.png");
	// Revert
	public static final ImageIcon REVERT = IconLoader
			.getIcon("arrow_refresh.png");
	// Zoom to layer
	public static final ImageIcon ZOOM = IconLoader.getIcon("magnifier.png");

	// PopupMenu Geocognition

	public static final ImageIcon CLEAR = IconLoader.getIcon("bin_closed.png");

	// New Registered SQL Artifact
	public static final ImageIcon GEOCOGNITION_REG_SQL = IconLoader
			.getIcon("builtinfunctionmap.png");
	// Register built in function
	public static final ImageIcon GEOCOGNITION_REG_BUILT = IconLoader
			.getIcon("builtinfunctionmap.png");
	// Unregister built in function
	public static final ImageIcon GEOCOGNITION_UNREG_BUILT = IconLoader
			.getIcon("builtinfunctionmaperror");
	// Register built in custom query
	public static final ImageIcon GEOCOGNITION_REG_BUILT_QUERY = IconLoader
			.getIcon("builtincustomquerymap.png");
	// Register built in custom query
	public static final ImageIcon GEOCOGNITION_UNREG_BUILT_QUERY = IconLoader
			.getIcon("builtincustomquerymaperror");

	// PopupMenu Geocatalog
	public static final ImageIcon GEOCATALOG_WMS = IconLoader
			.getIcon("server_connect.png");
	// Database
	public static final ImageIcon GEOCATALOG_DB = IconLoader
			.getIcon("database_add.png");
	// File
	public static final ImageIcon GEOCATALOG_FILE = IconLoader
			.getIcon("page_white_add.png");
	// Convert XYZ
	public static final ImageIcon GEOCATALOG_CONVERT_XYZ = IconLoader
			.getIcon("cog_go.png");

	// Popup Table editor
	// Add row
	public static final ImageIcon TABLE_ADDROW = IconLoader
			.getIcon("add_row.png");
	// select equals rows
	public static final ImageIcon TABLE_SELECT_SAME_VALUE = IconLoader
			.getIcon("selectsame_row.png");
	// Select all rows
	public static final ImageIcon TABLE_SELECT_ALL_ROW = IconLoader
			.getIcon("selectall_row.png");
	// Move selection up
	public static final ImageIcon TABLE_ROW_UP = IconLoader
			.getIcon("arrow_up.png");
	// Change field name
	public static final ImageIcon TABLE_CHANGEFIELDNAME = IconLoader
			.getIcon("text_replace.png");
	// Remove field
	public static final ImageIcon TABLE_REMOVEFIELD = IconLoader
			.getIcon("delete_field.png");
	// Add field
	public static final ImageIcon TABLE_ADDFIELD = IconLoader
			.getIcon("add_field.png");
	// Show field statistics
	public static final ImageIcon TABLE_SHOWFIELDSTAT = IconLoader
			.getIcon("sum.png");
	// Show field statistics
	public static final ImageIcon TABLE_ADDVALUE = IconLoader
			.getIcon("table_add.png");

	/**
	 * Configuration panel
	 * 
	 */

	public static final ImageIcon PREFERENCES_SYSTEM = IconLoader
			.getIcon("preferences-system.png");
	/**
	 * ************************************************** EDITION TOOLS
	 * ***********************************************************
	 */
	// Edition Tools IconsMAIN MENU
	public static final ImageIcon ZOOMIN = IconLoader.getIcon("zoom_in.gif");
	public static final ImageIcon ZOOMOUT = IconLoader.getIcon("zoom_out.png");
	public static final ImageIcon PAN = IconLoader.getIcon("pan.png");
	public static final ImageIcon INFO = IconLoader.getIcon("information.png");
	public static final ImageIcon SELECT = IconLoader.getIcon("select.png");
	public static final ImageIcon PICKPOINT = IconLoader
			.getIcon("coordinate_capture.png");
	public static final ImageIcon FENCE = IconLoader
			.getIcon("shape_polygon_edit.png");
	public static final ImageIcon MESUREAREA = IconLoader
			.getIcon("mesurearea.png");
	public static final ImageIcon MESURELINE = IconLoader
			.getIcon("mesurelength.png");
	public static final ImageIcon MESUREANGLE = IconLoader.getIcon("angle.png");
	public static final ImageIcon POLYGON = IconLoader.getIcon("polygon.png");
	public static final ImageIcon POINT = IconLoader.getIcon("point.png");
	public static final ImageIcon MULTIPOINT = IconLoader
			.getIcon("multipoint.png");
	public static final ImageIcon LINE = IconLoader.getIcon("line.png");
	public static final ImageIcon MULTILINE = IconLoader
			.getIcon("multiline.png");
	public static final ImageIcon MULTIPOLYGON = IconLoader
			.getIcon("multipolygon.png");
	public static final ImageIcon VERTEX_ADD = IconLoader
			.getIcon("vertexadition.png");
	public static final ImageIcon VERTEX_DELETE = IconLoader
			.getIcon("vertexdeletion.png");
	/**
	 * ************************************************** OTHERS
	 * ***********************************************************
	 */
	// Others icons
	// general icons
	public static final ImageIcon UNDO_ICON = IconLoader
			.getIcon("edit-undo.png");
	public static final ImageIcon REDO_ICON = IconLoader
			.getIcon("edit-redo.png");
	// Map & Table icons
	public static final ImageIcon TABLE_CREATE_SRC_ICON = IconLoader
			.getIcon("table_go.png");
	public static final ImageIcon ZOOM_SELECTED = IconLoader
			.getIcon("zoom_selected.png");
	public static final ImageIcon FULL_EXTENT = IconLoader.getIcon("world.png");
	public static final ImageIcon TOOLBAR_PROJECTION = IconLoader
			.getIcon("crs.png");

	/**
	 * ************************************************** GENERAL
	 * ***********************************************************
	 */

	public static final ImageIcon SAVE = IconLoader.getIcon("save.png");

	public static final ImageIcon ADD = IconLoader.getIcon("add.png");
	public static final ImageIcon DEL = IconLoader.getIcon("delete.png");

	public static final ImageIcon WARNING = IconLoader.getIcon("warning.gif");
	public static final ImageIcon ERROR = IconLoader.getIcon("error.gif");

	public static final ImageIcon CANCEL = IconLoader.getIcon("cancel.png");

	public static final ImageIcon PENCIL = IconLoader.getIcon("pencil.png");

	public static final ImageIcon FOLDER = IconLoader.getIcon("folder.png");

	public static final ImageIcon FOLDER_USER = IconLoader
			.getIcon("folder_user.png");

	public static final ImageIcon SPINNER_UP = IconLoader
			.getIcon("spinner_up.png");
	public static final ImageIcon SPINNER_DOWN = IconLoader
			.getIcon("spinner_down.png");

	public static final ImageIcon TREE_PLUS = IconLoader.getIcon("plus.gif");
	public static final ImageIcon TREE_MINUS = IconLoader.getIcon("minus.gif");

	public static final ImageIcon REMOVE = IconLoader.getIcon("remove.png");

	public static final ImageIcon BTN_DROPDOWN = IconLoader
			.getIcon("btn_dropdown.gif");

	public static final ImageIcon TABLE_REFRESH = IconLoader
			.getIcon("table_refresh.png");

	public static final ImageIcon GO_UP = IconLoader.getIcon("go-up.png");
	public static final ImageIcon GO_DOWN = IconLoader.getIcon("go-down.png");

	public static final ImageIcon MAP = IconLoader.getIcon("map.png");

	public static final ImageIcon FILTER = IconLoader.getIcon("filter.png");

	public static final ImageIcon PICTURE_ADD = IconLoader.getIcon("add.png");
	public static final ImageIcon PICTURE_DEL = IconLoader
			.getIcon("remove.png");
	public static final ImageIcon PICTURE_EDI = IconLoader.getIcon("pencil.png");

	public static final ImageIcon TABLE = IconLoader.getIcon("table.png");

	public static final ImageIcon TABLE_MULTIPLE = IconLoader
			.getIcon("table_multiple.png");

	public static final ImageIcon EYE = IconLoader.getIcon("eye.png");

	public static final ImageIcon DATABASE = IconLoader.getIcon("database.png");

	public static final ImageIcon SERVER_CONNECT = IconLoader
			.getIcon("server_connect.png");

	public static final ImageIcon LAYERS = IconLoader.getIcon("layers.png");

	public static final ImageIcon LAYER_MIXE = IconLoader
			.getIcon("layermixe.png");

	public static final ImageIcon LAYER_POLYGON = IconLoader
			.getIcon("layerpolygon.png");

	public static final ImageIcon LAYER_LINE = IconLoader
			.getIcon("layerline.png");

	public static final ImageIcon LAYER_POINT = IconLoader
			.getIcon("layerpoint.png");

	public static final ImageIcon LAYER_RGB = IconLoader
			.getIcon("layerrgb.png");

	public static final ImageIcon RASTER = IconLoader.getIcon("raster.png");

	public static final ImageIcon SCRIPT_CODE = IconLoader
			.getIcon("script_code.png");

	public static final ImageIcon PALETTE = IconLoader.getIcon("palette.png");

	public static final ImageIcon COMPLETION_LOCAL = IconLoader
			.getIcon("completion_local.png");

	public static final ImageIcon COMPLETION_MEMBER = IconLoader
			.getIcon("completion_member.png");

	public static final ImageIcon COMPLETION_CLASS = IconLoader
			.getIcon("completion_class.png");

	public static final ImageIcon COMPLETION_INTER = IconLoader
			.getIcon("completion_interface.png");

	public static final ImageIcon IMAGE = IconLoader.getIcon("image.png");

	public static final ImageIcon GEOFILE = IconLoader.getIcon("geofile.png");

	public static final ImageIcon BUILT_QUERY = IconLoader
			.getIcon("builtincustomquerymap.png");

	public static final ImageIcon BUILT_QUERY_ERR = IconLoader
			.getIcon("builtincustomquerymaperror.png");

	public static final ImageIcon BUILT_FUNCTION = IconLoader
			.getIcon("builtinfunctionmap.png");

	public static final ImageIcon WORLD_ADD = IconLoader
			.getIcon("world_add.png");

	public static final ImageIcon WORLD_DEL = IconLoader
			.getIcon("world_delete.png");

	public static final ImageIcon EDIT_CLEAR = IconLoader
			.getIcon("edit-clear.png");

	public static final ImageIcon RASTERINFO = IconLoader
			.getIcon("rasterinfo.png");

	public static final ImageIcon WATERSHED = IconLoader
			.getIcon("watershed.png");

	public static final ImageIcon WAND = IconLoader.getIcon("wizard.png");

	public static final Icon RASTERALGEBRA = IconLoader
			.getIcon("calculator.png");

}
