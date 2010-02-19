package org.orbisgis.core.ui.pluginSystem.workbench;

import org.orbisgis.core.language.I18N;

public abstract class Names {

	/**************************************************** Toolbars Names *******************************************************/
	public static final String TOOLBAR_NAME = I18N.get("toolbar.main");
	public static final String TOOLBAR_NAVIGATION = I18N
			.get("toolbar.navigation");
	public static final String TOOLBAR_INFO = I18N.get("toolbar.info");
	public static final String TOOLBAR_FENCE = I18N.get("toolbar.fence");
	public static final String TOOLBAR_MESURE = I18N.get("toolbar.mesure");
	public static final String TOOLBAR_DRAWING = I18N.get("toolbar.drawing");
	public static final String TOOLBAR_SELECTION = I18N.get("toolbar.selection");
	public static final String TOOLBAR_MAP = I18N.get("toolbar.edition.map");
	public static final String TOOLBAR_TABLE = I18N
			.get("toolbar.edition.table");
	public static final String TOOLBAR_RASTER = I18N.get("toolbar.raster");
	/**************************************************** MAIN MENU ************************************************************/
	// MAIN MENU
	// File
	public static final String FILE = I18N.get("ui.menu.file");
	// Help
	public static final String HELP = I18N.get("ui.menu.help");
	// View
	public static final String VIEW = I18N.get("ui.menu.view");

	// FILE SUB MENUS
	// Exit
	public static final String EXIT = I18N.get("ui.menu.file.text.exit");	
	// Save
	public static final String SAVE = I18N.get("ui.menu.file.text.save");	
	// Change Workspace
	public static final String CHANGE_WS = I18N
			.get("ui.menu.file.text.changeWorkspace");	
	// Save Workspace
	public static final String SAVE_WS = I18N
			.get("ui.menu.file.text.saveWorkspace");	

	// HELP SUB MENUS
	// About
	public static final String ABOUT = I18N.get("ui.menu.help.text.about");
	// Online
	public static final String ONLINE = I18N.get("ui.menu.help.text.online");
	public static final String ONLINE_URL = I18N.get("ui.menu.help.text.online.url");

	// VIEW SUB MENUS
	// Editors
	public static final String EDITORS = I18N.get("ui.menu.view.text.editors");
	// Output
	public static final String OUTPUT = I18N.get("ui.menu.view.text.output");
	// Memory
	public static final String MEMORY = I18N.get("ui.menu.view.text.memory");
	// Geocognition
	public static final String GEOCOGNITION = I18N
			.get("ui.menu.view.text.geocognition");	
	// Beanshell
	public static final String BEANSHELL = I18N
			.get("ui.menu.view.text.beanshell");
	// Information
	public static final String INFORMATION = I18N
			.get("ui.menu.view.text.information");
	// Geocatalog
	public static final String GEOCATALOG = I18N
			.get("ui.menu.view.text.geocatalog");
	// SQL Consol
	public static final String SQLCONSOLE = I18N
			.get("ui.menu.view.text.sqlconsole");
	// Geomark
	public static final String GEOMARK = I18N.get("ui.menu.view.text.geomark");
	// Configuration panel	
	public static final String CONFIGURATION = I18N.get("org.orbisgis.core.ui.plugins.properties.configuration");	
	// TOC
	public static final String TOC = I18N.get("ui.menu.view.text.toc");
	/**************************************************** MENU & POPUPMENU ********************************************************/
	// PopupMenu TOC
	// Edit Legend
	public static final String POPUP_TOC_LEGEND_PATH = I18N
			.get("ui.popupmenu.TOC.legend.path1");
	public static final String POPUP_TOC_LEGEND_GROUP = I18N
			.get("ui.popupmenu.TOC.legend.group");

	// Export...
	public static final String POPUP_TOC_EXPORT_PATH1 = I18N
			.get("ui.popupmenu.TOC.export.path1");
	public static final String POPUP_TOC_EXPORT_GROUP = I18N
			.get("ui.popupmenu.TOC.export.group");	

	public static final String TOC_EXPORT_SAVEIN_DB = I18N
			.get("ui.popupmenu.TOC.export.saveindb");
	
	public static final String TOC_EXPORT_SAVEIN_FILE = I18N
	.get("ui.popupmenu.TOC.export.saveinfile");
	// Table
	public static final String POPUP_TOC_TABLE_PATH1 = I18N
			.get("ui.popupmenu.TOC.table.path1");
	public static final String POPUP_TOC_TABLE_GROUP = I18N
			.get("ui.popupmenu.TOC.table.group");
	// Group layers
	public static final String POPUP_TOC_LAYERS_GROUP_PATH1 = I18N
			.get("ui.popupmenu.TOC.layers.group.path1");
	public static final String POPUP_TOC_LAYERS_GROUP_GROUP = I18N
			.get("ui.popupmenu.TOC.layers.group.group");
	// Remove layer
	public static final String POPUP_TOC_LAYERS_REMOVE_PATH1 = I18N
			.get("ui.popupmenu.TOC.layers.remove.path1");
	public static final String POPUP_TOC_LAYERS_REMOVE_GROUP = I18N
			.get("ui.popupmenu.TOC.layers.remove.group");
	// Create layer
	public static final String POPUP_TOC_LAYERS_CREATE_PATH1 = I18N
			.get("ui.popupmenu.TOC.layers.create.path1");
	public static final String POPUP_TOC_LAYERS_CREATE_GROUP = I18N
			.get("ui.popupmenu.TOC.layers.create.group");
	// Set active
	public static final String POPUP_TOC_ACTIVE_PATH1 = I18N
			.get("ui.popupmenu.TOC.active.path1");
	public static final String POPUP_TOC_ACTIVE_GROUP = I18N
			.get("ui.popupmenu.TOC.active.group");
	// Set Inactive
	public static final String POPUP_TOC_INACTIVE_PATH1 = I18N
			.get("ui.popupmenu.TOC.inactive.path1");
	public static final String POPUP_TOC_INACTIVE_GROUP = I18N
			.get("ui.popupmenu.TOC.inactive.group");
	//Revert 
	public static final String POPUP_TOC_REVERT_PATH1 = I18N
			.get("ui.popupmenu.TOC.revert.path1");
	//Save
	public static final String POPUP_TOC_SAVE_PATH1 = I18N
			.get("ui.popupmenu.TOC.save.path1");
	// Zoom to layer
	public static final String POPUP_TOC_ZOOM_PATH1 = I18N
			.get("ui.popupmenu.TOC.zoom.path1");
	public static final String POPUP_TOC_ZOOM_GROUP = I18N
			.get("ui.popupmenu.TOC.zoom.group");

	// PopupMenu Geocognition
	// Open
	public static final String POPUP_GEOCOGNITION_OPEN_PATH1 = I18N
			.get("ui.popupmenu.geocognition.open.path1");
	public static final String POPUP_GEOCOGNITION_OPEN_GROUP = I18N
			.get("ui.popupmenu.geocognition.open.group");
	// Remove
	public static final String POPUP_GEOCOGNITION_REMOVE_PATH1 = I18N
			.get("ui.popupmenu.geocognition.remove.path1");
	public static final String POPUP_GEOCOGNITION_REMOVE_GROUP = I18N
			.get("ui.popupmenu.geocognition.remove.group");
	// Clear
	public static final String POPUP_GEOCOGNITION_CLEAR_PATH1 = I18N
			.get("ui.popupmenu.geocognition.clear.path1");
	public static final String POPUP_GEOCOGNITION_CLEAR_GROUP = I18N
			.get("ui.popupmenu.geocognition.clear.group");

	public static final String POPUP_GEOCOGNITION_ADD = I18N
			.get("ui.popupmenu.geocognition.add");
	// New Registered SQL Artifact
	public static final String POPUP_GEOCOGNITION_REG_SQL_PATH1 = I18N
			.get("ui.popupmenu.geocognition.reg.sql.path1");
	public static final String POPUP_GEOCOGNITION_REG_SQL_GROUP = I18N
			.get("ui.popupmenu.geocognition.reg.sql.group");
	// Add Map
	public static final String POPUP_GEOCOGNITION_ADD_MAP_PATH1 = I18N
			.get("ui.popupmenu.geocognition.add.map.path1");
	public static final String POPUP_GEOCOGNITION_ADD_MAP_GROUP = I18N
			.get("ui.popupmenu.geocognition.add.map.group");
	// Add folder
	public static final String POPUP_GEOCOGNITION_ADD_FOLDER_PATH1 = I18N
			.get("ui.popupmenu.geocognition.add.folder.path1");
	public static final String POPUP_GEOCOGNITION_ADD_FOLDER_GROUP = I18N
			.get("ui.popupmenu.geocognition.add.folder.group");
	// New Symbol
	public static final String POPUP_GEOCOGNITION_NEW_SYMBOL_PATH1 = I18N
			.get("ui.popupmenu.geocognition.new.symbol.path1");
	public static final String POPUP_GEOCOGNITION_NEW_SYMBOL_GROUP = I18N
			.get("ui.popupmenu.geocognition.new.symbol.group");
	// Register built in function
	public static final String POPUP_GEOCOGNITION_REG_BUILT_PATH1 = I18N
			.get("ui.popupmenu.geocognition.reg.built.path1");
	public static final String POPUP_GEOCOGNITION_REG_BUILT_GROUP = I18N
			.get("ui.popupmenu.geocognition.reg.built.group");
	// Unregister built in function
	public static final String POPUP_GEOCOGNITION_UNREG_BUILT_PATH1 = I18N
			.get("ui.popupmenu.geocognition.unreg.built.path1");
	public static final String POPUP_GEOCOGNITION_UNREG_BUILT_GROUP = I18N
			.get("ui.popupmenu.geocognition.unreg.built.group");
	// Register built in custom query
	public static final String POPUP_GEOCOGNITION_REG_BUILT_QUERY_PATH1 = I18N
			.get("ui.popupmenu.geocognition.reg.built.query.path1");
	public static final String POPUP_GEOCOGNITION_REG_BUILT_QUERY_GROUP = I18N
			.get("ui.popupmenu.geocognition.reg.built.query.group");
	// Register built in custom query
	public static final String POPUP_GEOCOGNITION_UNREG_BUILT_QUERY_PATH1 = I18N
			.get("ui.popupmenu.geocognition.unreg.built.query.path1");
	public static final String POPUP_GEOCOGNITION_UNREG_BUILT_QUERY_GROUP = I18N
			.get("ui.popupmenu.geocognition.unreg.built.query.group");

	// PopupMenu Geocatalog
	public static final String POPUP_GEOCATALOG_ADD = I18N
			.get("ui.popupmenu.geocatalog.add");
	// WMS
	public static final String POPUP_GEOCATALOG_WMS = I18N
			.get("ui.popupmenu.geocatalog.wms.path1");
	// Database
	public static final String POPUP_GEOCATALOG_DB = I18N
			.get("ui.popupmenu.geocatalog.db.path1");
	// File
	public static final String POPUP_GEOCATALOG_FILE = I18N
			.get("ui.popupmenu.geocatalog.file.path1");
	// Show Table
	public static final String POPUP_GEOCATALOG_TABLE = I18N
			.get("ui.popupmenu.geocatalog.table.path1");
	public static final String POPUP_GEOCATALOG_TABLE_GROUP = I18N
			.get("ui.popupmenu.geocatalog.table.group");
	// Delete source
	public static final String POPUP_GEOCATALOG_DELETE_SRC = I18N
			.get("ui.popupmenu.geocatalog.delete.src.path1");
	public static final String POPUP_GEOCATALOG_DELETE_SRC_GROUP = I18N
			.get("ui.popupmenu.geocatalog.delete.src.group");
	// Create source
	public static final String POPUP_GEOCATALOG_CREATE_SRC_PATH1 = I18N
			.get("ui.popupmenu.geocatalog.create.src.path1");
	public static final String POPUP_GEOCATALOG_CREATE_SRC_PATH2 = I18N
			.get("ui.popupmenu.geocatalog.create.src.path2");
	public static final String POPUP_GEOCATALOG_CREATE_SRC_GROUP = I18N
			.get("ui.popupmenu.geocatalog.create.src.group");
	// Convert XYZ
	public static final String POPUP_GEOCATALOG_CONVERT_XYZ = I18N
			.get("ui.popupmenu.geocatalog.convert.xyz.path1");
	// Clear
	public static final String POPUP_GEOCATALOG_CLEAR = I18N
			.get("ui.popupmenu.geocatalog.clear.path1");
	public static final String POPUP_GEOCATALOG_CLEAR_GROUP = I18N
			.get("ui.popupmenu.geocatalog.clear.group");

	// Export
	public static final String POPUP_GEOCATALOG_EXPORT = I18N
			.get("ui.popupmenu.geocatalog.export");	
	public static final String POPUP_GEOCATALOG_EXPORT_INFILE = I18N
			.get("ui.popupmenu.geocatalog.export.infile");
	public static final String POPUP_GEOCATALOG_EXPORT_INDB = I18N
			.get("ui.popupmenu.geocatalog.export.indb");
	
	
	//Table Editor
	public static final String EDITOR_TABLE_ID = I18N.get("ui.table.id");
	// Popup Table editor
	// set null
	public static final String POPUP_TABLE_SETNULL_PATH1 = I18N
			.get("ui.popupmenu.table.setnull.path1");
	public static final String POPUP_TABLE_SETNULL_GROUP = I18N
			.get("ui.popupmenu.table.setnull.group");
	// Clear selection
	public static final String POPUP_TABLE_CLEAR_PATH1 = I18N
			.get("ui.popupmenu.table.clear.path1");
	public static final String POPUP_TABLE_CLEAR_GROUP = I18N
			.get("ui.popupmenu.table.clear.group");
	// Add row
	public static final String POPUP_TABLE_ADDROW_PATH1 = I18N
			.get("ui.popupmenu.table.addrow.path1");
	public static final String POPUP_TABLE_ADDROW_GROUP = I18N
			.get("ui.popupmenu.table.addrow.group");
	// select equals rows
	public static final String POPUP_TABLE_EQUALS_PATH1 = I18N
			.get("ui.popupmenu.table.equals.path1");
	public static final String POPUP_TABLE_EQUALS_GROUP = I18N
			.get("ui.popupmenu.table.equals.group");
	// Select all rows
	public static final String POPUP_TABLE_ALL_PATH1 = I18N
			.get("ui.popupmenu.table.all.path1");
	public static final String POPUP_TABLE_ALL_GROUP = I18N
			.get("ui.popupmenu.table.all.group");
	// Select all rows
	public static final String POPUP_TABLE_NONE_PATH1 = I18N
			.get("ui.popupmenu.table.none.path1");
	public static final String POPUP_TABLE_NONE_GROUP = I18N
			.get("ui.popupmenu.table.none.group");
	// Move selection up
	public static final String POPUP_TABLE_UP_PATH1 = I18N
			.get("ui.popupmenu.table.up.path1");
	public static final String POPUP_TABLE_UP_GROUP = I18N
			.get("ui.popupmenu.table.up.group");
	// Remove row
	public static final String POPUP_TABLE_REMOVE_PATH1 = I18N
			.get("ui.popupmenu.table.remove.path1");
	public static final String POPUP_TABLE_REMOVE_GROUP = I18N
			.get("ui.popupmenu.table.remove.group");
	// Zoom to selected row
	public static final String POPUP_TABLE_ZOOMTOSELECTED_PATH1 = I18N
			.get("ui.popupmenu.table.zoomtoselected.path1");
	public static final String POPUP_TABLE_ZOOMTOSELECTED_GROUP = I18N
			.get("ui.popupmenu.table.zoomtoselected.group");
	// Zoom to layer
	public static final String POPUP_TABLE_ZOOMTOLAYER_PATH1 = I18N
			.get("ui.popupmenu.table.zoomtolayer.path1");
	public static final String POPUP_TABLE_ZOOMTOLAYER_GROUP = I18N
			.get("ui.popupmenu.table.zoomtolayer.group");
	// Change field name
	public static final String POPUP_TABLE_CHANGEFIELDNAME_PATH1 = I18N
			.get("ui.popupmenu.table.changefieldname.path1");
	public static final String POPUP_TABLE_CHANGEFIELDNAME_GROUP = I18N
			.get("ui.popupmenu.table.changefieldname.group");
	// Remove field
	public static final String POPUP_TABLE_REMOVEFIELD_PATH1 = I18N
			.get("ui.popupmenu.table.removefield.path1");
	public static final String POPUP_TABLE_REMOVEFIELD_GROUP = I18N
			.get("ui.popupmenu.table.removefield.group");
	// Add field
	public static final String POPUP_TABLE_ADDFIELD_PATH1 = I18N
			.get("ui.popupmenu.table.addfield.path1");
	public static final String POPUP_TABLE_ADDFIELD_GROUP = I18N
			.get("ui.popupmenu.table.addfield.group");
	// Show field info
	public static final String POPUP_TABLE_SHOWFIELD_PATH1 = I18N
			.get("ui.popupmenu.table.showfield.path1");
	public static final String POPUP_TABLE_SHOWFIELD_GROUP = I18N
			.get("ui.popupmenu.table.showfield.group");
	// Show field statistics
	public static final String POPUP_TABLE_SHOWFIELDSTAT_PATH1 = I18N
			.get("ui.popupmenu.table.showfieldstat.path1");
	public static final String POPUP_TABLE_SHOWFIELDSTAT_GROUP = I18N
			.get("ui.popupmenu.table.showfieldstat.group");
	// Show field statistics
	public static final String POPUP_TABLE_ADDVALUE_PATH1 = I18N
			.get("ui.popupmenu.table.addvalue.path1");
	public static final String POPUP_TABLE_ADDVALUE_GROUP = I18N
			.get("ui.popupmenu.table.addvalue.group");

	//Map Editor
	public static final String EDITOR_MAP_ID = I18N.get("ui.map.id");
	// Popup Map editor
	// Export Map as image
	public static final String POPUP_MAP_EXPORT_IMG = I18N
			.get("ui.popupmenu.map.export.img.path1");
	public static final String POPUP_MAP_EXPORT_IMG_GROUP = I18N
			.get("ui.popupmenu.map.export.img.group");
	// Export Map As PDF
	public static final String POPUP_MAP_EXPORT_PDF = I18N
			.get("ui.popupmenu.map.export.pdf.path1");
	public static final String POPUP_MAP_EXPORT_PDF_GROUP = I18N
			.get("ui.popupmenu.map.export.pdf.group");
	//Show coordinate
	public static final String POPUP_MAP_SHOW_XY = I18N
	.get("ui.popupmenu.map.show.xy.path1");
	/**************************************************** EDITION TOOLS *****************************************************/
	/**************************************************** OTHERS ************************************************************/	
	// Workbench context actions
	public static final String ACTION_MENU_FILE = I18N.get("action.menu.main");
	public static final String ACTION_MENU_VIEW = I18N.get("action.menu.view");
	// Error messages
	public static final String ERROR_POPUP_ADD = I18N.get("error.popup.add");
	// Edit legend plugin
	public static final String ERROR_EDIT_LEGEND_EDITOR = I18N
			.get("error.edit.legend.editor");
	public static final String ERROR_EDIT_LEGEND_DRIVER = I18N
			.get("error.edit.legend.driver");
	public static final String ERROR_EDIT_LEGEND_LAYER = I18N
			.get("error.edit.legend.layer");
}
