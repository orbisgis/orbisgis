package org.orbisgis.plugins.core.ui.workbench;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JToggleButton;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.orbisgis.plugins.core.Services;
import org.orbisgis.plugins.core.ui.PlugInContext;
import org.orbisgis.plugins.core.ui.actions.ExitPlugIn;
import org.orbisgis.plugins.core.ui.actions.SavePlugIn;
import org.orbisgis.plugins.core.ui.actions.ScalePlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.ClearMapSelectionPlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.DeleteMapSelectionPlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.FullExtentPlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.RedoMapPlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.UndoMapPlugIn;
import org.orbisgis.plugins.core.ui.editors.MapEditor.ZoomToSelectedFeaturesPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.AddFieldPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.AddValuePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ChangeFieldNamePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ClearTableSelectionPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.CreateSourceFromSelectionPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.DeleteTableSelectionPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.NewRowTablePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.RedoTablePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.RemoveFieldPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.SelectAllPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.SelectEqualPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.SelectNonePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.SelectionTableUpPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.SetNullPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ShowFieldInfoPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ShowFieldStatisticsPlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.UndoTablePlugIn;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ZoomToLayerFromTable;
import org.orbisgis.plugins.core.ui.editors.TableEditor.ZoomToSelectedPlugIn;
import org.orbisgis.plugins.core.ui.editors.map.tool.Automaton;
import org.orbisgis.plugins.core.ui.editors.map.tools.EditionSelectionTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.FencePolygonTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.InfoTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.LineTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.MesureLineTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.MesurePolygonTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.MultilineTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.MultipointTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.MultipolygonTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.PanTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.PointTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.PolygonTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.SelectionTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.VertexAditionTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.VertexDeletionTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.ZoomInTool;
import org.orbisgis.plugins.core.ui.editors.map.tools.ZoomOutTool;
import org.orbisgis.plugins.core.ui.export.ExportMapAsImagePlugIn;
import org.orbisgis.plugins.core.ui.export.ExportMapAsPDFPlugIn;
import org.orbisgis.plugins.core.ui.geocatalog.ConvertXYZDemGeocatalogPlugIn;
import org.orbisgis.plugins.core.ui.geocatalog.GeocatalogClear;
import org.orbisgis.plugins.core.ui.geocatalog.GeocatalogCreateFileSource;
import org.orbisgis.plugins.core.ui.geocatalog.GeocatalogDeleteSource;
import org.orbisgis.plugins.core.ui.geocatalog.GeocatalogShowTable;
import org.orbisgis.plugins.core.ui.geocatalog.NewGeocatalogFile;
import org.orbisgis.plugins.core.ui.geocatalog.NewGeocognitionDB;
import org.orbisgis.plugins.core.ui.geocatalog.WMSGeocatalogPlugIn;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionAddMapPlugIn;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionClearPlugIn;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionNewFolder;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionNewRegisteredSQLArtifact;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionNewSymbol;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionRegisterBuiltInCustomQuery;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionRegisterBuiltInFunction;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionUnRegisterBuiltInCustomQuery;
import org.orbisgis.plugins.core.ui.geocognition.GeocognitionUnRegisterBuiltInFunction;
import org.orbisgis.plugins.core.ui.geocognition.OpenGeocognitionPlugIn;
import org.orbisgis.plugins.core.ui.geocognition.RemoveGeocognitionPlugIn;
import org.orbisgis.plugins.core.ui.help.AboutOrbisGIS;
import org.orbisgis.plugins.core.ui.help.OnlineHelpOrbisGIS;
import org.orbisgis.plugins.core.ui.properties.Configuration;
import org.orbisgis.plugins.core.ui.registers.RasterAndD8Register;
import org.orbisgis.plugins.core.ui.toc.CreateGroupPlugIn;
import org.orbisgis.plugins.core.ui.toc.EditLegendPlugIn;
import org.orbisgis.plugins.core.ui.toc.GroupLayersPlugIn;
import org.orbisgis.plugins.core.ui.toc.RemoveLayerPlugIn;
import org.orbisgis.plugins.core.ui.toc.SaveInFilePlugIn;
import org.orbisgis.plugins.core.ui.toc.SetActivePlugIn;
import org.orbisgis.plugins.core.ui.toc.SetInactivePlugIn;
import org.orbisgis.plugins.core.ui.toc.ShowInTablePlugIn;
import org.orbisgis.plugins.core.ui.toc.ZoomToLayerPlugIn;
import org.orbisgis.plugins.core.ui.views.MapEditorPlugIn;
import org.orbisgis.plugins.core.ui.views.TableEditorPlugIn;
import org.orbisgis.plugins.core.ui.windows.mainFrame.OrbisGISFrame;
import org.orbisgis.plugins.core.ui.workspace.ChangeWorkspacePlugIn;
import org.orbisgis.plugins.core.ui.workspace.SaveWorkspacePlugIn;

public class OrbisConfiguration implements Setup {

	// OrbisGIS main ToolBar & OrbisGIS main menu
	private ExitPlugIn exitPlugIn = new ExitPlugIn();
	private SavePlugIn savePlugIn = new SavePlugIn();
	// Scale panel plugin is a swing component to execute action on map editor
	private ScalePlugIn scalePlugIn = new ScalePlugIn();
	private SaveWorkspacePlugIn saveWorkspacePlugIn = new SaveWorkspacePlugIn();
	private ChangeWorkspacePlugIn changeWorkspacePlugIn = new ChangeWorkspacePlugIn();
	private Configuration configuration = new Configuration();
	private AboutOrbisGIS aboutOrbisGIS = new AboutOrbisGIS();
	private OnlineHelpOrbisGIS onlineHelpOrbisGIS = new OnlineHelpOrbisGIS();

	// TOC
	private EditLegendPlugIn editLegendPlugIn = new EditLegendPlugIn();
	private ShowInTablePlugIn showInTablePlugIn = new ShowInTablePlugIn();
	private SaveInFilePlugIn saveInFilePlugIn = new SaveInFilePlugIn();
	private GroupLayersPlugIn groupLayersPlugIn = new GroupLayersPlugIn();
	private RemoveLayerPlugIn removeLayerPlugIn = new RemoveLayerPlugIn();
	private SetActivePlugIn setActivePlugIn = new SetActivePlugIn();
	private SetInactivePlugIn setInactivePlugIn = new SetInactivePlugIn();
	private CreateGroupPlugIn createGroupPlugIn = new CreateGroupPlugIn();
	private ZoomToLayerPlugIn zoomToLayerPlugIn = new ZoomToLayerPlugIn();

	// Geocognition popup
	private OpenGeocognitionPlugIn openGeocognitionPlugIn = new OpenGeocognitionPlugIn();
	private RemoveGeocognitionPlugIn removeGeocognitionPlugIn = new RemoveGeocognitionPlugIn();
	private GeocognitionAddMapPlugIn geocognitionAddMapPlugIn = new GeocognitionAddMapPlugIn();
	private GeocognitionNewRegisteredSQLArtifact geocognitionNewRegisteredSQLArtifact = new GeocognitionNewRegisteredSQLArtifact();
	private GeocognitionNewFolder geocognitionNewFolder = new GeocognitionNewFolder();
	private GeocognitionNewSymbol geocognitionNewSymbol = new GeocognitionNewSymbol();
	private GeocognitionRegisterBuiltInFunction geocognitionRegisterBuiltInFunction = new GeocognitionRegisterBuiltInFunction();
	private GeocognitionUnRegisterBuiltInFunction geocognitionUnRegisterBuiltInFunction = new GeocognitionUnRegisterBuiltInFunction();
	private GeocognitionRegisterBuiltInCustomQuery geocognitionRegisterBuiltInCustomQuery = new GeocognitionRegisterBuiltInCustomQuery();
	private GeocognitionUnRegisterBuiltInCustomQuery geocognitionUnRegisterBuiltInCustomQuery = new GeocognitionUnRegisterBuiltInCustomQuery();

	// Geocatalog popup
	private NewGeocatalogFile newGeocatalogFile = new NewGeocatalogFile();
	private NewGeocognitionDB newGeocognitionDB = new NewGeocognitionDB();
	private ConvertXYZDemGeocatalogPlugIn convertXYZDemGeocatalogPlugIn = new ConvertXYZDemGeocatalogPlugIn();
	private WMSGeocatalogPlugIn wMSGeocatalogPlugIn = new WMSGeocatalogPlugIn();
	private GeocognitionClearPlugIn geocognitionClearPlugIn = new GeocognitionClearPlugIn();
	private GeocatalogCreateFileSource geocatalogCreateFileSource = new GeocatalogCreateFileSource();
	private GeocatalogDeleteSource geocatalogDeleteSource = new GeocatalogDeleteSource();
	private GeocatalogClear geocatalogClear = new GeocatalogClear();
	private GeocatalogShowTable geocatalogShowTable = new GeocatalogShowTable();

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
	// register
	private RasterAndD8Register rasterAndD8Register = new RasterAndD8Register();

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
			groupLayersPlugIn.initialize(context);
			removeLayerPlugIn.initialize(context);
			setActivePlugIn.initialize(context);
			setInactivePlugIn.initialize(context);
			createGroupPlugIn.initialize(context);
			zoomToLayerPlugIn.initialize(context);

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
		// Tools in Navigation toolbar
		add(new ZoomOutTool(), "zoom_out.png", frame.getNavigationToolBar());
		fullExtentPlugIn.initialize(plugInContext); // after Zoom out for group
		// ZoomIn/ZoomOut
		add(new PanTool(), "pan.png", frame.getNavigationToolBar());
		// Tools in Info toolbar
		add(new InfoTool(), "information.png", frame.getInfoToolBar());
		add(new SelectionTool(), "select.png", frame.getInfoToolBar());
		add(new FencePolygonTool(), "shape_polygon_edit.png", frame
				.getFenceToolBar());
		add(new MesurePolygonTool(), "mesurearea.png", frame.getMesureToolBar());
		add(new MesureLineTool(), "mesurelength.png", frame.getMesureToolBar());
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
		// add(new HelloTool(), "zoom_in.png", frame.getInfoToolBar());

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

		// TODO
		scalePlugIn.initialize(plugInContext);
		rasterAndD8Register.initialize(plugInContext);
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
		WorkbenchToolBar wbFence = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_FENCE);
		wbToolBar.add(wbFence);
		WorkbenchToolBar wbMesureArea = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_MESURE);
		wbToolBar.add(wbMesureArea);
		WorkbenchToolBar wbEditionMap = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_MAP);
		wbToolBar.add(wbEditionMap);
		WorkbenchToolBar wbEditionTable = new WorkbenchToolBar(wbContext,
				Names.TOOLBAR_TABLE);
		wbToolBar.add(wbEditionTable);
		/*
		 * WorkbenchToolBar wbMesureDistance= new
		 * WorkbenchToolBar(wbContext,"Mesure distance...");
		 * wbToolBar.add(wbMesureDistance);
		 */
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
		JMenu helpMenu = (JMenu) FeatureInstaller.installMnemonic(new JMenu(
				Names.HELP), menuBar);
		menuBar.add(helpMenu);
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
	}

	private JToggleButton add(Automaton tool, String icon,
			WorkbenchToolBar wbToolbar) {
		return wbToolbar.addAutomaton(tool, icon);
	}
}
