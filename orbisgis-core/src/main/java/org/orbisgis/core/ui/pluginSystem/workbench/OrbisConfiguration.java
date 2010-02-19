package org.orbisgis.core.ui.pluginSystem.workbench;

import javax.swing.AbstractButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.orbisgis.core.Services;
import org.orbisgis.core.images.IconNames;
import org.orbisgis.core.ui.editors.map.tool.Automaton;
import org.orbisgis.core.ui.editors.map.tools.CompassTool;
import org.orbisgis.core.ui.editors.map.tools.EditionSelectionTool;
import org.orbisgis.core.ui.editors.map.tools.FencePolygonTool;
import org.orbisgis.core.ui.editors.map.tools.InfoTool;
import org.orbisgis.core.ui.editors.map.tools.LineTool;
import org.orbisgis.core.ui.editors.map.tools.MesureLineTool;
import org.orbisgis.core.ui.editors.map.tools.MesurePolygonTool;
import org.orbisgis.core.ui.editors.map.tools.MultilineTool;
import org.orbisgis.core.ui.editors.map.tools.MultipointTool;
import org.orbisgis.core.ui.editors.map.tools.MultipolygonTool;
import org.orbisgis.core.ui.editors.map.tools.PanTool;
import org.orbisgis.core.ui.editors.map.tools.PickCoordinatesPointTool;
import org.orbisgis.core.ui.editors.map.tools.PointTool;
import org.orbisgis.core.ui.editors.map.tools.PolygonTool;
import org.orbisgis.core.ui.editors.map.tools.SelectionTool;
import org.orbisgis.core.ui.editors.map.tools.VertexAditionTool;
import org.orbisgis.core.ui.editors.map.tools.VertexDeletionTool;
import org.orbisgis.core.ui.editors.map.tools.ZoomInTool;
import org.orbisgis.core.ui.editors.map.tools.ZoomOutTool;
import org.orbisgis.core.ui.pluginSystem.PlugInContext;
import org.orbisgis.core.ui.plugins.actions.ExitPlugIn;
import org.orbisgis.core.ui.plugins.actions.SavePlugIn;
import org.orbisgis.core.ui.plugins.actions.ScalePlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.ClearMapSelectionPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.DeleteMapSelectionPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.ExportMapAsImagePlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.ExportMapAsPDFPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.FullExtentPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.RedoMapPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.ShowXYPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.UndoMapPlugIn;
import org.orbisgis.core.ui.plugins.editors.mapEditor.ZoomToSelectedFeaturesPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.AddFieldPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.AddValuePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ChangeFieldNamePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ClearTableSelectionPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.CreateSourceFromSelectionPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.DeleteTableSelectionPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.NewRowTablePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.RedoTablePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.RemoveFieldPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.SelectAllPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.SelectEqualPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.SelectNonePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.SelectionTableUpPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.SetNullPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ShowFieldInfoPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ShowFieldStatisticsPlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.UndoTablePlugIn;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ZoomToLayerFromTable;
import org.orbisgis.core.ui.plugins.editors.tableEditor.ZoomToSelectedPlugIn;
import org.orbisgis.core.ui.plugins.help.AboutOrbisGISPlugIn;
import org.orbisgis.core.ui.plugins.help.OnlineHelpOrbisGISPlugIn;
import org.orbisgis.core.ui.plugins.properties.ConfigurationPlugIn;
import org.orbisgis.core.ui.plugins.toc.CreateGroupPlugIn;
import org.orbisgis.core.ui.plugins.toc.EditLegendPlugIn;
import org.orbisgis.core.ui.plugins.toc.GroupLayersPlugIn;
import org.orbisgis.core.ui.plugins.toc.RemoveLayerPlugIn;
import org.orbisgis.core.ui.plugins.toc.RevertLayerPlugIn;
import org.orbisgis.core.ui.plugins.toc.SaveInDataBasePlugIn;
import org.orbisgis.core.ui.plugins.toc.SaveInFilePlugIn;
import org.orbisgis.core.ui.plugins.toc.SaveLayerPlugIn;
import org.orbisgis.core.ui.plugins.toc.SetActivePlugIn;
import org.orbisgis.core.ui.plugins.toc.SetInactivePlugIn;
import org.orbisgis.core.ui.plugins.toc.ShowInTablePlugIn;
import org.orbisgis.core.ui.plugins.toc.ZoomToLayerPlugIn;
import org.orbisgis.core.ui.plugins.views.MapEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.TableEditorPlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.ConvertXYZDemGeocatalogPlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogClearPlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogCreateFileSourcePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogDeleteSourcePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogSaveInDataBasePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogSaveInFilePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.GeocatalogShowTablePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.NewGeocatalogFilePlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.NewGeocognitionDBPlugIn;
import org.orbisgis.core.ui.plugins.views.geocatalog.WMSGeocatalogPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionAddMapPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionClearPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionNewFolderPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionNewRegisteredSQLArtifactPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionNewSymbolPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionRegisterBuiltInCustomQueryPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionRegisterBuiltInFunctionPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionUnRegisterBuiltInCustomQueryPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.GeocognitionUnRegisterBuiltInFunctionPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.OpenGeocognitionPlugIn;
import org.orbisgis.core.ui.plugins.views.geocognition.RemoveGeocognitionPlugIn;
import org.orbisgis.core.ui.plugins.workspace.ChangeWorkspacePlugIn;
import org.orbisgis.core.ui.plugins.workspace.SaveWorkspacePlugIn;
import org.orbisgis.core.ui.windows.mainFrame.OrbisGISFrame;

public class OrbisConfiguration implements Setup {

	// OrbisGIS main ToolBar & OrbisGIS main menu
	private ExitPlugIn exitPlugIn = new ExitPlugIn();
	private SavePlugIn savePlugIn = new SavePlugIn();
	// Scale panel plugin is a swing component to execute action on map editor
	private ScalePlugIn scalePlugIn = new ScalePlugIn();
	private SaveWorkspacePlugIn saveWorkspacePlugIn = new SaveWorkspacePlugIn();
	private ChangeWorkspacePlugIn changeWorkspacePlugIn = new ChangeWorkspacePlugIn();
	private ConfigurationPlugIn configuration = new ConfigurationPlugIn();
	private AboutOrbisGISPlugIn aboutOrbisGIS = new AboutOrbisGISPlugIn();
	private OnlineHelpOrbisGISPlugIn onlineHelpOrbisGIS = new OnlineHelpOrbisGISPlugIn();

	// TOC
	private EditLegendPlugIn editLegendPlugIn = new EditLegendPlugIn();
	private ShowInTablePlugIn showInTablePlugIn = new ShowInTablePlugIn();
	private SaveInFilePlugIn saveInFilePlugIn = new SaveInFilePlugIn();
	private SaveInDataBasePlugIn saveInDataBasePlugIn = new SaveInDataBasePlugIn();
	private GroupLayersPlugIn groupLayersPlugIn = new GroupLayersPlugIn();
	private RemoveLayerPlugIn removeLayerPlugIn = new RemoveLayerPlugIn();
	private SetActivePlugIn setActivePlugIn = new SetActivePlugIn();
	private SetInactivePlugIn setInactivePlugIn = new SetInactivePlugIn();
	private RevertLayerPlugIn revertLayerPlugIn = new RevertLayerPlugIn();
	private SaveLayerPlugIn saveLayerPlugIn = new SaveLayerPlugIn();
	private CreateGroupPlugIn createGroupPlugIn = new CreateGroupPlugIn();
	private ZoomToLayerPlugIn zoomToLayerPlugIn = new ZoomToLayerPlugIn();

	// Geocognition popup
	private OpenGeocognitionPlugIn openGeocognitionPlugIn = new OpenGeocognitionPlugIn();
	private RemoveGeocognitionPlugIn removeGeocognitionPlugIn = new RemoveGeocognitionPlugIn();
	private GeocognitionAddMapPlugIn geocognitionAddMapPlugIn = new GeocognitionAddMapPlugIn();
	private GeocognitionNewRegisteredSQLArtifactPlugIn geocognitionNewRegisteredSQLArtifact = new GeocognitionNewRegisteredSQLArtifactPlugIn();
	private GeocognitionNewFolderPlugIn geocognitionNewFolder = new GeocognitionNewFolderPlugIn();
	private GeocognitionNewSymbolPlugIn geocognitionNewSymbol = new GeocognitionNewSymbolPlugIn();
	private GeocognitionRegisterBuiltInFunctionPlugIn geocognitionRegisterBuiltInFunction = new GeocognitionRegisterBuiltInFunctionPlugIn();
	private GeocognitionUnRegisterBuiltInFunctionPlugIn geocognitionUnRegisterBuiltInFunction = new GeocognitionUnRegisterBuiltInFunctionPlugIn();
	private GeocognitionRegisterBuiltInCustomQueryPlugIn geocognitionRegisterBuiltInCustomQuery = new GeocognitionRegisterBuiltInCustomQueryPlugIn();
	private GeocognitionUnRegisterBuiltInCustomQueryPlugIn geocognitionUnRegisterBuiltInCustomQuery = new GeocognitionUnRegisterBuiltInCustomQueryPlugIn();

	// Geocatalog popup
	private NewGeocatalogFilePlugIn newGeocatalogFile = new NewGeocatalogFilePlugIn();
	private NewGeocognitionDBPlugIn newGeocognitionDB = new NewGeocognitionDBPlugIn();
	private ConvertXYZDemGeocatalogPlugIn convertXYZDemGeocatalogPlugIn = new ConvertXYZDemGeocatalogPlugIn();
	private WMSGeocatalogPlugIn wMSGeocatalogPlugIn = new WMSGeocatalogPlugIn();
	private GeocognitionClearPlugIn geocognitionClearPlugIn = new GeocognitionClearPlugIn();
	private GeocatalogCreateFileSourcePlugIn geocatalogCreateFileSource = new GeocatalogCreateFileSourcePlugIn();
	private GeocatalogDeleteSourcePlugIn geocatalogDeleteSource = new GeocatalogDeleteSourcePlugIn();
	private GeocatalogClearPlugIn geocatalogClear = new GeocatalogClearPlugIn();
	private GeocatalogShowTablePlugIn geocatalogShowTable = new GeocatalogShowTablePlugIn();
	private GeocatalogSaveInFilePlugIn geocatalogSaveInFilePlugIn = new GeocatalogSaveInFilePlugIn();
	private GeocatalogSaveInDataBasePlugIn geocatalogSaveInDataBasePlugIn = new GeocatalogSaveInDataBasePlugIn();

	// Table editor Plugins
	// Row actions
	private ClearTableSelectionPlugIn clearTableSelectionPlugIn = new ClearTableSelectionPlugIn();
	private DeleteTableSelectionPlugIn deleteTableSelectionPlugIn = new DeleteTableSelectionPlugIn();
	private SelectionTableUpPlugIn selectionTableUpPlugIn = new SelectionTableUpPlugIn();
	private SetNullPlugIn setNullPlugIn = new SetNullPlugIn();
	private SelectEqualPlugIn selectEqualPlugIn = new SelectEqualPlugIn();
	private SelectAllPlugIn selectAllPlugIn = new SelectAllPlugIn();
	private SelectNonePlugIn selectNonePlugIn = new SelectNonePlugIn();
	private ZoomToSelectedPlugIn zoomToSelectedPlugIn = new ZoomToSelectedPlugIn();
	private ZoomToLayerFromTable zoomToLayerFromTable = new ZoomToLayerFromTable();
	// Column actions
	private ChangeFieldNamePlugIn changeFieldNamePlugIn = new ChangeFieldNamePlugIn();
	private RemoveFieldPlugIn removeFieldPlugIn = new RemoveFieldPlugIn();
	private AddFieldPlugIn addFieldPlugIn = new AddFieldPlugIn();
	private ShowFieldInfoPlugIn showFieldInfoPlugIn = new ShowFieldInfoPlugIn();
	private ShowFieldStatisticsPlugIn showFieldStatisticsPlugIn = new ShowFieldStatisticsPlugIn();
	private AddValuePlugIn addValuePlugIn = new AddValuePlugIn();
	// Popup and toolbars PlugIns for Table editor
	private UndoTablePlugIn undoTablePlugIn = new UndoTablePlugIn();
	private NewRowTablePlugIn newRowTablePlugIn = new NewRowTablePlugIn();
	private RedoTablePlugIn redoTablePlugIn = new RedoTablePlugIn();
	private CreateSourceFromSelectionPlugIn createSourceFromSelectionPlugIn = new CreateSourceFromSelectionPlugIn();

	// Map editor PlugIn
	private FullExtentPlugIn fullExtentPlugIn = new FullExtentPlugIn();
	private ClearMapSelectionPlugIn clearMapSelectionPlugIn = new ClearMapSelectionPlugIn();
	private ZoomToSelectedFeaturesPlugIn zoomToSelectedFeaturesPlugIn = new ZoomToSelectedFeaturesPlugIn();
	private UndoMapPlugIn undoMapPlugIn = new UndoMapPlugIn();
	private RedoMapPlugIn redoMapPlugIn = new RedoMapPlugIn();
	private DeleteMapSelectionPlugIn deleteMapSelectionPlugIn = new DeleteMapSelectionPlugIn();

	// right click on Map
	private ExportMapAsImagePlugIn exportMasAsImagePlugIn = new ExportMapAsImagePlugIn();
	private ExportMapAsPDFPlugIn exportMapAsPDFPlugIn = new ExportMapAsPDFPlugIn();
	private ShowXYPlugIn showXYPlugIn = new ShowXYPlugIn();

	// private I18NTestPlugIn i18NTestPlugIn = new I18NTestPlugIn();

	public void setup(WorkbenchContext workbenchContext) throws Exception {
		// load Main Menu
		configureMainMenus(workbenchContext);
		// get Plugin context
		PlugInContext plugInContext = workbenchContext.createPlugInContext();
		// i18NTestPlugIn.initialize(plugInContext);
		// Initialize default tool for Map editor
		Automaton defaultTool = new ZoomInTool();
		// Record default tool
		Services.registerService(Automaton.class,
				"Gives default tool to the editor", defaultTool);
		// Initialize one Map editor without geocognition elment
		MapEditorPlugIn mapEditorPlugIn = new MapEditorPlugIn();
		mapEditorPlugIn.initialize(plugInContext, defaultTool);
		// Initialize table editor
		TableEditorPlugIn tableEditorPlugIn = new TableEditorPlugIn();
		tableEditorPlugIn.initialize(plugInContext);
		// load toolbars (Main toolbar, table toolexceptionbar, Map toolbar)
		configureToolBar(plugInContext);
		// load main frame with default tool selected (Zoom in tool)
		OrbisGISFrame frame = workbenchContext.getWorkbench().getFrame();
		add(defaultTool, "zoom_in.png", frame.getNavigationToolBar());
		// load views (Geocognition, Toc, editors, Beanshell ....)
		OrbisGISConfiguration.loadOrbisGISPlugIns(workbenchContext);
		// Initialize buttons PlugIns in toolbar & Mains menu
		configureTools(plugInContext);
		// load popup
		configurePopup(plugInContext);
		// why set TableEditor & MapEditor popup? Currently popup is build after
		// right click event!
		// Because there are several Map & Table editors so I record popup for
		// all instances
		frame.setTableMenuTreePopup();
		frame.setMapMenuTreePopup();
	}

	private void configurePopup(PlugInContext context) {
		try {
			// TOC popup
			editLegendPlugIn.initialize(context);
			showInTablePlugIn.initialize(context);
			saveInFilePlugIn.initialize(context);
			saveInDataBasePlugIn.initialize(context);
			groupLayersPlugIn.initialize(context);
			removeLayerPlugIn.initialize(context);
			setActivePlugIn.initialize(context);
			setInactivePlugIn.initialize(context);
			createGroupPlugIn.initialize(context);
			zoomToLayerPlugIn.initialize(context);
			revertLayerPlugIn.initialize(context);
			saveLayerPlugIn.initialize(context);

			// Geocognition popup
			openGeocognitionPlugIn.initialize(context);
			removeGeocognitionPlugIn.initialize(context);
			geocognitionClearPlugIn.initialize(context);
			geocognitionNewRegisteredSQLArtifact.initialize(context);
			geocognitionAddMapPlugIn.initialize(context);
			geocognitionNewFolder.initialize(context);
			geocognitionNewSymbol.initialize(context);
			geocognitionRegisterBuiltInFunction.initialize(context);
			geocognitionUnRegisterBuiltInFunction.initialize(context);
			geocognitionRegisterBuiltInCustomQuery.initialize(context);
			geocognitionUnRegisterBuiltInCustomQuery.initialize(context);

			// Geocatalog popup
			newGeocatalogFile.initialize(context);
			wMSGeocatalogPlugIn.initialize(context);
			newGeocognitionDB.initialize(context);
			convertXYZDemGeocatalogPlugIn.initialize(context);
			wMSGeocatalogPlugIn.initialize(context);
			geocatalogCreateFileSource.initialize(context);
			geocatalogDeleteSource.initialize(context);
			geocatalogClear.initialize(context);
			geocatalogShowTable.initialize(context);
			geocatalogSaveInFilePlugIn.initialize(context);
			geocatalogSaveInDataBasePlugIn.initialize(context);

			// Table Editor PlugIn popup
			setNullPlugIn.initialize(context);
			selectEqualPlugIn.initialize(context);
			selectAllPlugIn.initialize(context);
			selectNonePlugIn.initialize(context);
			zoomToSelectedPlugIn.initialize(context);
			zoomToLayerFromTable.initialize(context);
			changeFieldNamePlugIn.initialize(context);
			removeFieldPlugIn.initialize(context);
			addFieldPlugIn.initialize(context);
			showFieldInfoPlugIn.initialize(context);
			showFieldStatisticsPlugIn.initialize(context);
			addValuePlugIn.initialize(context);

			// Map editor : right click on Map
			exportMasAsImagePlugIn.initialize(context);
			exportMapAsPDFPlugIn.initialize(context);
			showXYPlugIn.initialize(context);

		} catch (Exception e) {
			Services.getErrorManager().error(Names.ERROR_POPUP_ADD, e);
		}
	}

	private void configureTools(PlugInContext plugInContext) throws Exception {

		OrbisGISFrame frame = plugInContext.getWorkbenchContext()
				.getWorkbench().getFrame();
		
		// Main Toolbar & Main menu -> sont chargés par le PlugIn
		// lib/ext/MainToolBarPlugIn.jar
		exitPlugIn.initialize(plugInContext);
		savePlugIn.initialize(plugInContext);
		// Main menus
		saveWorkspacePlugIn.initialize(plugInContext);
		changeWorkspacePlugIn.initialize(plugInContext);
		configuration.initialize(plugInContext);
		aboutOrbisGIS.initialize(plugInContext);
		onlineHelpOrbisGIS.initialize(plugInContext);

		// Map Toolbar
		clearMapSelectionPlugIn.initialize(plugInContext);
		undoMapPlugIn.initialize(plugInContext);
		redoMapPlugIn.initialize(plugInContext);
		deleteMapSelectionPlugIn.initialize(plugInContext);
		add(new ZoomOutTool(), "zoom_out.png", frame.getNavigationToolBar());
		// ZoomIn/ZoomOut
		add(new PanTool(), "pan.png", frame.getNavigationToolBar());
		// Tools in Navigation toolbar
		fullExtentPlugIn.initialize(plugInContext); // after Zoom out for group
		// Tools in Info toolbar
		add(new InfoTool(), "information.png", frame.getInfoToolBar());
		add(new SelectionTool(), "select.png", frame.getInfoToolBar());
		add(new FencePolygonTool(), "shape_polygon_edit.png", frame
				.getDrawingToolBar());
		add(new PickCoordinatesPointTool(), "coordinate_capture.png", frame
				.getDrawingToolBar());
		add(new MesurePolygonTool(), "mesurearea.png", frame.getMesureToolBar());
		add(new MesureLineTool(), "mesurelength.png", frame.getMesureToolBar());
		add(new CompassTool(), "angle.png", frame.getMesureToolBar());
		// Tool in Edition Map Toolbar
		add(new PolygonTool(), "polygon.png", frame.getEditionMapToolBar());
		add(new EditionSelectionTool(), "select.png", frame
				.getEditionMapToolBar());
		add(new PointTool(), "point.png", frame.getEditionMapToolBar());
		add(new MultipointTool(), "multipoint.png", frame
				.getEditionMapToolBar());
		add(new LineTool(), "line.png", frame.getEditionMapToolBar());
		add(new MultilineTool(), "multiline.png", frame.getEditionMapToolBar());
		add(new MultipolygonTool(), "multipolygon.png", frame
				.getEditionMapToolBar());
		add(new VertexAditionTool(), "vertexadition.png", frame
				.getEditionMapToolBar());
		add(new VertexDeletionTool(), "vertexdeletion.png", frame
				.getEditionMapToolBar());

		// Info Toolbar
		clearTableSelectionPlugIn.initialize(plugInContext);
		zoomToSelectedFeaturesPlugIn.initialize(plugInContext);

		// Table Edition Toolbar
		undoTablePlugIn.initialize(plugInContext);
		redoTablePlugIn.initialize(plugInContext);
		selectionTableUpPlugIn.initialize(plugInContext);
		deleteTableSelectionPlugIn.initialize(plugInContext);
		newRowTablePlugIn.initialize(plugInContext);
		createSourceFromSelectionPlugIn.initialize(plugInContext);

		scalePlugIn.initialize(plugInContext);
	}

	private void configureToolBar(PlugInContext plugInContext) {
		// get WorkbenchFrame
		OrbisGISFrame frame = plugInContext.getWorkbenchContext()
				.getWorkbench().getFrame();
		WorkbenchContext wbContext = plugInContext.getWorkbenchContext();
		WorkbenchToolBar wbToolBar = frame.getWorkbenchToolBar();
		// Add Toolbars
		WorkbenchToolBar wbMain = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_NAME);
		wbToolBar.add(wbMain);
		WorkbenchToolBar wbNavigation = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_NAVIGATION);
		wbToolBar.add(wbNavigation);
		WorkbenchToolBar wbInfo = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_INFO);
		wbToolBar.add(wbInfo);
		WorkbenchToolBar wbSelection = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_SELECTION);
		wbToolBar.add(wbSelection);
		
		WorkbenchToolBar wbMesureArea = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_MESURE, IconNames.MESURELINE_ICON, true);
		wbToolBar.add(wbMesureArea);
		WorkbenchToolBar wbDrawing = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_DRAWING, IconNames.FENCE_ICON, true);
		wbToolBar.add(wbDrawing);
		WorkbenchToolBar wbEditionMap = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_MAP);
		wbToolBar.add(wbEditionMap);
		WorkbenchToolBar wbEditionTable = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_TABLE);
		wbToolBar.add(wbEditionTable);

	}

	private void configureMainMenus(final WorkbenchContext workbenchContext)
			throws Exception {
		JMenuBar menuBar = workbenchContext.getWorkbench().getFrame()
				.getActionMenuBar();
		JMenu defaultMenu = (JMenu) FeatureInstaller.installMnemonic(new JMenu(
				Names.FILE), menuBar);
		defaultMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuSelected(MenuEvent e) {
				workbenchContext.setLastAction(Names.ACTION_MENU_FILE);
			}
		});
		menuBar.add(defaultMenu);
		JMenu viewMenu = (JMenu) FeatureInstaller.installMnemonic(new JMenu(
				Names.VIEW), menuBar);
		viewMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuSelected(MenuEvent e) {
				workbenchContext.setLastAction(Names.ACTION_MENU_VIEW);
			}
		});
		menuBar.add(viewMenu);

		JMenu helpMenu = (JMenu) FeatureInstaller.installMnemonic(new JMenu(
				Names.HELP), menuBar);
		menuBar.add(helpMenu);
	}

	private AbstractButton add(Automaton tool, String icon,
			WorkbenchToolBar wbToolbar) {
		return wbToolbar.addAutomaton(tool, icon);
	}
}
