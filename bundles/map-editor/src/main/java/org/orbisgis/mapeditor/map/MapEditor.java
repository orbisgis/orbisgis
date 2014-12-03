/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.mapeditor.map;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.TreeExpansionListener;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.orbisgis.core.Services;
import org.orbisgis.core_export.MapImageWriter;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.coremap.layerModel.ILayer;
import org.orbisgis.coremap.layerModel.LayerException;
import org.orbisgis.coremap.layerModel.MapContext;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.map.TransformListener;
import org.orbisgis.coremap.renderer.ResultSetProviderFactory;
import org.orbisgis.mapeditor.map.ext.MapEditorAction;
import org.orbisgis.mapeditor.map.ext.MapEditorExtension;
import org.orbisgis.mapeditor.map.icons.MapEditorIcons;
import org.orbisgis.mapeditor.map.jobs.ReadMapContextJob;
import org.orbisgis.mapeditor.map.mapsManager.MapsManager;
import org.orbisgis.mapeditor.map.mapsManager.TreeLeafMapContextFile;
import org.orbisgis.mapeditor.map.mapsManager.TreeLeafMapElement;
import org.orbisgis.mapeditor.map.tool.ToolManager;
import org.orbisgis.mapeditor.map.tool.TransitionException;
import org.orbisgis.mapeditor.map.toolbar.ActionAutomaton;
import org.orbisgis.mapeditor.map.tools.*;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.commons.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.ColorPicker;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.sif.multiInputPanel.*;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.ZoomToSelection;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionDockingListener;
import org.orbisgis.view.edition.EditableTransferListener;
import org.orbisgis.view.table.jobs.CreateSourceFromSelection;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.viewapi.edition.EditableSource;
import org.orbisgis.viewapi.edition.EditorManager;
import org.orbisgis.viewapi.workspace.ViewWorkspace;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The Map Editor Panel
 */
public class MapEditor extends JPanel implements TransformListener, MapEditorExtension   {
    private static final I18n I18N = I18nFactory.getI18n(MapEditor.class);
    private static final Logger GUILOGGER = Logger.getLogger("gui."+MapEditor.class);
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L;
    private MapControl mapControl = new MapControl();
    private MapContext mapContext = null;
    private MapElement mapElement;
    private DockingPanelParameters dockingPanelParameters;
    private MapTransferHandler dragDropHandler;
    private MapStatusBar mapStatusBar = new MapStatusBar();
    //This timer will fetch the cursor component coordinates
    //Then translate to the map coordinates and send it to
    //the MapStatusBar
    private AtomicBoolean processingCursor = new AtomicBoolean(false);
    private Point lastCursorPosition = new Point();
    private AtomicBoolean initialised = new AtomicBoolean(false);
    private MapsManager mapsManager;
    private JLayeredPane layeredPane = new JLayeredPane();
    private ComponentListener sizeListener = EventHandler.create(ComponentListener.class,this,"updateMapControlSize",null,"componentResized");
    private PropertyChangeListener modificationListener = EventHandler.create(PropertyChangeListener.class,this,"onMapModified");
    private PropertyChangeListener activeLayerListener = EventHandler.create(PropertyChangeListener.class,this,"onActiveLayerChange","");
    private ActionCommands actions = new ActionCommands();
    private MapEditorPersistence mapEditorPersistence = new MapEditorPersistence();
    private DataManager dataManager;
    private ViewWorkspace viewWorkspace;
    private EditorManager editorManager;
    private Map<ResultSetProviderFactory, Action> rsFactories = new HashMap<>();

    private boolean userChangedWidth = false;
    private boolean userChangedHeight = false;

    /**
     * Constructor
     */
    public MapEditor(ViewWorkspace viewWorkspace, DataManager dataManager,EditorManager editorManager) {
        super(new BorderLayout());
        this.editorManager = editorManager;
        this.mapsManager = new MapsManager(viewWorkspace.getMapContextPath(),dataManager, editorManager);
        this.viewWorkspace = viewWorkspace;
        this.dataManager = dataManager;
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName("map_editor");
        updateMapLabel();
        dockingPanelParameters.setTitleIcon(MapEditorIcons.getIcon("map"));
        dockingPanelParameters.setMinimizable(false);
        dockingPanelParameters.setExternalizable(false);
        dockingPanelParameters.setCloseable(false);
        dockingPanelParameters.setLayout(mapEditorPersistence);
        dockingPanelParameters.setVisible(true);
        layeredPane.add(mapControl,1);
        layeredPane.add(mapsManager, 0);
        mapsManager.setVisible(false);
        mapsManager.setMapsManagerPersistence(mapEditorPersistence.getMapsManagerPersistence());
        // when the layout is loaded, this editor will load the map element linked with this layout
        mapEditorPersistence.addPropertyChangeListener(MapEditorPersistence.PROP_DEFAULTMAPCONTEXT,EventHandler.create(PropertyChangeListener.class,this,"onSerialisationMapChange"));
        add(layeredPane, BorderLayout.CENTER);
        add(mapStatusBar, BorderLayout.PAGE_END);
        //Declare Tools of Map Editors
        //Add the tools in the docking Panel title
        createActions();
        dockingPanelParameters.setDockActions(actions.getActions());
        // Tools that will be created later will also be set in the docking panel
        // thanks to this listener
        actions.addPropertyChangeListener(new ActionDockingListener(dockingPanelParameters));
        //Set the Drop target
        dragDropHandler = new MapTransferHandler();
        this.setTransferHandler(dragDropHandler);
    }

    public void addResultSetProviderFactory(ResultSetProviderFactory resultSetProviderFactory) {
        Action rsAction = new DefaultAction("RSF_"+resultSetProviderFactory.getName(),
                resultSetProviderFactory.getName(),null,
                EventHandler.create(ActionListener.class, this, "onChangeRendererData", ""))
                .setMenuGroup(true).setParent(MapEditorAction.A_DATA_PROVIDERS)
                .setButtonGroup(MapEditorAction.TOGGLE_GROUP_DATA_PROVIDERS);
        rsFactories.put(resultSetProviderFactory, rsAction);
        actions.addAction(rsAction);
    }

    public void removeResultSetProviderFactory(ResultSetProviderFactory resultSetProviderFactory) {
        actions.removeAction(rsFactories.remove(resultSetProviderFactory));
    }

    public void onChangeRendererData(ActionEvent ae) {

    }

    private void updateMapLabel() {
                if (mapElement == null) {
                        dockingPanelParameters.setTitle(I18N.tr("Map"));
                } else {
                        if (mapElement.isModified()) {
                                dockingPanelParameters.setTitle(I18N.tr("Map Editor \"{0}\" [Modified]",mapElement.getMapContext().getTitle()));
                        } else {
                                dockingPanelParameters.setTitle(I18N.tr("Map Editor \"{0}\"",mapElement.getMapContext().getTitle()));
                        }
                }
        }

    /**
     * The user update the scale field
     * @param pce update event
     * @throws PropertyVetoException If the property entered is incorrect
     */
    public void onUserSetScaleDenominator(PropertyChangeEvent pce) throws PropertyVetoException {
            long newScale = (Long)pce.getNewValue();
            if(newScale<1) {
                    throw new PropertyVetoException(I18N.tr("The value of the scale denominator must be equal or greater than 1"),pce);
            }
            mapControl.getMapTransform().setScaleDenominator(newScale);
    }
        /**
         * Notifies this component that it now has a parent component. When this
         * method is invoked, the chain of parent components is set up with
         * KeyboardAction event listeners.
         */
        @Override
        public void addNotify() {
                super.addNotify();
                if (!initialised.getAndSet(true)) {
                        addComponentListener(sizeListener);
                        // Read the default map context file
                        initMapContext();
                        //Register listener
                        dragDropHandler.getTransferEditableEvent().addListener(this, EventHandler.create(EditableTransferListener.class, this, "onDropEditable", "editableList"));
                        mapControl.addMouseMotionListener(EventHandler.create(MouseMotionListener.class, this, "onMouseMove", "point", "mouseMoved"));
                        mapStatusBar.addVetoableChangeListener(
                                MapStatusBar.PROP_USER_DEFINED_SCALE_DENOMINATOR,
                                EventHandler.create(VetoableChangeListener.class, this,
                                "onUserSetScaleDenominator", ""));
                        // When the tree is expanded update the manager size
                        mapsManager.getTree().addComponentListener(sizeListener);
                        mapsManager.getTree().addTreeExpansionListener(EventHandler.create(TreeExpansionListener.class,this,"updateMapControlSize"));

                }
        }

        private void initMapContext() {
                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);


                File serialisedMapContextPath = new File(viewWorkspace.getMapContextPath() + File.separator, mapEditorPersistence.getDefaultMapContext());
                if(!serialisedMapContextPath.exists()) {
                        createDefaultMapContext(dataManager);
                } else {
                        TreeLeafMapElement defaultMap = (TreeLeafMapElement)mapsManager.getFactoryManager().create(serialisedMapContextPath);
                        try {
                                MapElement mapElementToLoad = defaultMap.getMapElement(new NullProgressMonitor(), dataManager);
                                backgroundManager.backgroundOperation(new ReadMapContextJob(mapElementToLoad, editorManager));
                        } catch(IllegalArgumentException ex) {
                                //Map XML is invalid
                                GUILOGGER.warn(I18N.tr("Fail to load the map context, starting with an empty map context"),ex);
                                createDefaultMapContext(dataManager);
                        }
                }
        }

       /**
        * Create the default map context, create it if the map folder is empty
        */
        private void createDefaultMapContext(DataManager dataManager) {
                BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);

                //Load the map context
                File mapContextFolder = new File(viewWorkspace.getMapContextPath());
                if (!mapContextFolder.exists()) {
                        mapContextFolder.mkdir();
                }
                File mapContextFile = new File(mapContextFolder, I18N.tr("MyMap.ows"));

                if (!mapContextFile.exists()) {
                        //Create an empty map context
                        TreeLeafMapContextFile.createEmptyMapContext(mapContextFile, dataManager);
                }
                MapElement editableMap = new MapElement(mapContextFile, dataManager);
                backgroundManager.backgroundOperation(new ReadMapContextJob(editableMap, editorManager));
        }

        /**
         * Compute the appropriate components bounds for MapControl
         * and MapsManager and apply theses bounds
         */
        public void updateMapControlSize() {
                SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                                doUpdateMapControlSize();
                        }
                });
        }
        private void doUpdateMapControlSize() {
                mapControl.setBounds(0,0,layeredPane.getWidth(),layeredPane.getHeight());
                if(mapsManager.isVisible()) {
                        Dimension mapsManagerPreferredSize = mapsManager.getMinimalComponentDimension();
                        int hPos = layeredPane.getWidth() - Math.min(mapsManagerPreferredSize.width,layeredPane.getWidth());
                        mapsManager.setBounds(hPos,0,Math.min(mapsManagerPreferredSize.width,layeredPane.getWidth()),Math.min(mapsManagerPreferredSize.height,layeredPane.getHeight()));
                        mapsManager.revalidate();
                }
        }
    /**
     * The user Drop a list of Editable
     * @param editableList Load the editable elements in the current map context
     */
    public void onDropEditable(EditableElement[] editableList) {
        BackgroundManager bm = Services.getService(BackgroundManager.class);
        //Load the layers in the background
        bm.nonBlockingBackgroundOperation(new DropDataSourceProcess(editableList));
    }

    /**
     * Load a new map context
     * @param element Editable to load
     */
    private void loadMap(MapElement element) {
        MapElement oldMapElement = mapElement;
        ToolManager oldToolManager = getToolManager();
        removeListeners();
        mapElement = element;
        if(element!=null) {
            try {
                mapContext = (MapContext) element.getObject();
                mapContext.addPropertyChangeListener(MapContext.PROP_ACTIVELAYER, activeLayerListener);
                mapControl.setMapContext(mapContext);
                mapControl.getMapTransform().setExtent(mapContext.getBoundingBox());
                mapControl.setElement(this);
                mapControl.initMapControl(new PanTool());
                // Update the default map context path with the relative path
                URI rootDir =(new File(viewWorkspace.getMapContextPath()+File.separator)).toURI();
                String relative = rootDir.relativize(element.getMapContextFile().toURI()).getPath();
                mapEditorPersistence.setDefaultMapContext(relative);
                // Set the loaded map hint to the MapCatalog
                mapsManager.setLoadedMap(element.getMapContextFile());
                // Update the editor label with the new editable name
                updateMapLabel();
                mapElement.addPropertyChangeListener(MapElement.PROP_MODIFIED, modificationListener);
                repaint();
            } catch (IllegalStateException ex) {
                GUILOGGER.error(ex);
            } catch (TransitionException ex) {
                GUILOGGER.error(ex);
            }
        } else {
            // Load null MapElement
            mapControl.setMapContext(null);
        }
        firePropertyChange(PROP_TOOL_MANAGER,oldToolManager,getToolManager());
        firePropertyChange(PROP_MAP_ELEMENT,oldMapElement, mapElement);
    }

    /**
     * The DefaultMapContext property of {@link MapEditorPersistence} has been updated, load this map
     */
    public void onSerialisationMapChange() {
        String fileName = mapEditorPersistence.getDefaultMapContext();
        File mapFilePath = new File(viewWorkspace.getMapContextPath(),fileName);
        if(!mapFilePath.exists()) {
            return;
        }
        if(mapElement!=null && mapFilePath.equals(mapElement.getMapContextFile())) {
            return;
        }
        MapElement mapElement = new MapElement(mapFilePath, dataManager);
        BackgroundManager backgroundManager = Services.getService(BackgroundManager.class);
        backgroundManager.backgroundOperation(new ReadMapContextJob(mapElement, editorManager));
    }
    /**
     * MouseMove event on the MapControl
     * @param mousePosition x,y position of the event relative to the MapControl component.
     */
    public void onMouseMove(Point mousePosition) {
            lastCursorPosition.setLocation(mousePosition);
            if(mapElement!=null) {
                if(!processingCursor.getAndSet(true)) {
                    CursorCoordinateProcessing run = new CursorCoordinateProcessing(mapStatusBar,processingCursor,mapControl.getMapTransform(),lastCursorPosition);
                    run.execute();
                }
            }
    }

        /**
         * Free MapEditor resources
         **/
        public void dispose() {
            removeListeners();
            getMapControl().closing();
            loadMap(null);
        }

        @Override
        public MapElement getMapElement() {
                return mapElement;
        }

    /**
     * MapEditor tools declaration
     */
    private void createActions() {
        // Navigation tools
        actions.addAction(new ActionAutomaton(MapEditorAction.A_ZOOM_IN,new ZoomInTool(),this).setLogicalGroup("navigation"));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_ZOOM_OUT,new ZoomOutTool(),this).setLogicalGroup("navigation"));
        actions.addAction(new DefaultAction(MapEditorAction.A_FULL_EXTENT,I18N.tr("Full extent"),
                MapEditorIcons.getIcon("zoom_extent"),EventHandler.create(ActionListener.class,this,"onFullExtent"))
                .setToolTipText(I18N.tr("Zoom to show all geometries")).setLogicalGroup("navigation"));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_PAN,new PanTool(),this).setLogicalGroup("navigation"));
       
        // Selection tools
        actions.addAction(new ActionAutomaton(MapEditorAction.A_INFO_TOOL,new InfoTool(),this)
                .addTrackedMapContextProperty(MapContext.PROP_SELECTEDLAYERS)
                .addTrackedMapContextProperty(MapContext.PROP_SELECTEDSTYLES).setLogicalGroup("selection"));
        
        actions.addAction(new ActionAutomaton(MapEditorAction.A_SELECTION,new SelectionTool(),this)
                .addTrackedMapContextProperty(MapContext.PROP_SELECTEDLAYERS)
                .addTrackedMapContextProperty(MapContext.PROP_SELECTEDSTYLES).setLogicalGroup("selection"));
        
        //Clear selection group
        actions.addAction(new DefaultAction(MapEditorAction.A_CLEAR_SELECTION_GROUP,I18N.tr("Clear selection tools")).setMenuGroup(true));
        actions.addAction(new DefaultAction(MapEditorAction.A_CLEAR_ALL_SELECTION, I18N.tr("Clear all selection"),
                MapEditorIcons.getIcon("edit-clear_all"),EventHandler.create(ActionListener.class,this,"onClearAllSelection"))
                .setToolTipText(I18N.tr("Clear all selected geometries of all layers")).setParent(MapEditorAction.A_CLEAR_SELECTION_GROUP));
        actions.addAction(new DefaultAction(MapEditorAction.A_CLEAR_LAYER_SELECTION, I18N.tr("Clear selected layers"),
                MapEditorIcons.getIcon("edit-clear"),EventHandler.create(ActionListener.class,this,"onClearLayerSelection"))
                .setToolTipText(I18N.tr("Clear all selected geometries of the selected layers")).setParent(MapEditorAction.A_CLEAR_SELECTION_GROUP));
        
        
        //Zoom to selection group
        actions.addAction(new DefaultAction(MapEditorAction.A_ZOOM_SELECTION_GROUP,I18N.tr("Zoom to tools")).setMenuGroup(true));
        actions.addAction(new DefaultAction(MapEditorAction.A_ZOOM_ALL_SELECTION, I18N.tr("Zoom to all selection"),
                MapEditorIcons.getIcon("zoom_selected_all"),EventHandler.create(ActionListener.class,this,"onZoomToAllSelection"))
                .setToolTipText(I18N.tr("Zoom to all selected geometries")).setParent(MapEditorAction.A_ZOOM_SELECTION_GROUP));
        actions.addAction(new DefaultAction(MapEditorAction.A_ZOOM_LAYER_SELECTION, I18N.tr("Zoom to layer selection"),
                MapEditorIcons.getIcon("zoom_selected"),EventHandler.create(ActionListener.class,this,"onZoomToLayerSelection"))
                .setToolTipText(I18N.tr("Zoom to selected geometries of the selected layers")).setParent(MapEditorAction.A_ZOOM_SELECTION_GROUP));
        
        
        actions.addAction(new DefaultAction(MapEditorAction.A_DATA_SOURCE_FROM_SELECTION, I18N.tr("Create datasource from selection"),
                MapEditorIcons.getIcon("table_go"),
                EventHandler.create(ActionListener.class,this,"onCreateDataSourceFromSelection"))
                .setToolTipText(I18N.tr("Create a datasource from the current selection")).setLogicalGroup("selection"));

        // Measure tools
        actions.addAction(new DefaultAction(MapEditorAction.A_MEASURE_GROUP,I18N.tr("Mesure tools")).setMenuGroup(true));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_MEASURE_LINE,new MesureLineTool(),this)
                .setParent(MapEditorAction.A_MEASURE_GROUP));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_MEASURE_POLYGON,new MesurePolygonTool(),this)
                .setParent(MapEditorAction.A_MEASURE_GROUP));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_COMPASS,new CompassTool(),this)
                .setParent(MapEditorAction.A_MEASURE_GROUP));

        // Drawing tools
        actions.addAction(new DefaultAction(MapEditorAction.A_DRAWING_GROUP,I18N.tr("Graphic tools")).setMenuGroup(true));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_FENCE,new FencePolygonTool(),this)
                .setParent(MapEditorAction.A_DRAWING_GROUP));
        actions.addAction(new ActionAutomaton(MapEditorAction.A_PICK_COORDINATES, new PickCoordinatesPointTool(),this)
                .setParent(MapEditorAction.A_DRAWING_GROUP));

        // Maps manager
        actions.addAction(new DefaultAction(MapEditorAction.A_MAP_TREE, I18N.tr("Maps manager"),
                MapEditorIcons.getIcon("map_manager"),
                EventHandler.create(ActionListener.class,this,"onShowHideMapsTree"))
                .setToolTipText(I18N.tr("Show/Hide Maps Manager")));
        actions.addAction(new DefaultAction(MapEditorAction.A_MAP_EXPORT_IMAGE, I18N.tr("Export map as image"),
                MapEditorIcons.getIcon("export_image"),
                EventHandler.create(ActionListener.class,this,"onExportMapRendering"))
                .setToolTipText(I18N.tr("Export image as file")));

        // Cache control
        actions.addAction(new DefaultAction(MapEditorAction.A_MAP_CLEAR_CACHE, I18N.tr("Refresh"),
                MapEditorIcons.getIcon("refresh"), EventHandler.create(ActionListener.class, this ,"onClearCache")));

        // Parameters
        //actions.addAction(new DefaultAction(MapEditorAction.A_PARAMETERS,I18N.tr("Configuration"),
        //        MapEditorIcons.getIcon("config")).setMenuGroup(true));
        //actions.addAction(new DefaultAction(MapEditorAction.A_DATA_PROVIDERS,I18N.tr("Data query"),
        //        MapEditorIcons.getIcon("table_go")).setMenuGroup(true).setParent(MapEditorAction.A_PARAMETERS));
    }

    /**
     * @return The manager of docking actions.
     */
    public ActionCommands getActionCommands() {
        return actions;
    }    

    /**
     * User want to see table updates on MapEditor
     */
    public void onClearCache() {
        mapControl.clearCache();
        // Redraw
        mapControl.invalidateImage();
    }

    /**
     * User click on the Show/Hide maps tree
     */
    public void onShowHideMapsTree() {
            if(!mapsManager.isVisible()) {
                    mapsManager.setVisible(true);
                    updateMapControlSize();
            } else {
                    mapsManager.setVisible(false);
            }
    }

    /**
     * The user click on the button Full Extent
     */
    public void onFullExtent() {
        mapControl.getMapTransform().setExtent(mapContext.getLayerModel().getEnvelope());
    }

    /**
     * The use want to export the rendering into a file.
     */
    public void onExportMapRendering() {
        // Show Dialog to select image size
        final String WIDTH_T = "width";
        final String HEIGHT_T = "height";
        final String RATIO_CHECKBOX_T = "ratio checkbox";
        final String TRANSPARENT_BACKGROUND_T = "background";
        final String DPI_T = "dpi";
        final int textWidth = 8;
        final MultiInputPanel inputPanel = new MultiInputPanel(I18N.tr("Export parameters"));

        inputPanel.addInput(TRANSPARENT_BACKGROUND_T,
                "",
                "True",
                new CheckBoxChoice(true, "<html>" + I18N.tr("Transparent\nbackground") + "</html>"));

        inputPanel.addInput(DPI_T,
                I18N.tr("DPI"),
                String.valueOf((int) (MapImageWriter.MILLIMETERS_BY_INCH / MapImageWriter.DEFAULT_PIXEL_SIZE)),
                new TextBoxType(textWidth));

        TextBoxType tbWidth = new TextBoxType(textWidth); 
        inputPanel.addInput(WIDTH_T,
                I18N.tr("Width (pixels)"),
                String.valueOf(mapControl.getImage().getWidth()),
                tbWidth);
        TextBoxType tbHeight = new TextBoxType(textWidth);
        inputPanel.addInput(HEIGHT_T,
                I18N.tr("Height (pixels)"),
                String.valueOf(mapControl.getImage().getHeight()),
                tbHeight);

        tbHeight.getComponent().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                userChangedHeight = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateWidth();
            }

            private void updateWidth() {
                if (userChangedHeight) {
                    if (inputPanel.getInput(RATIO_CHECKBOX_T).equals("true")) {
                        // Change image width to keep ratio
                        final String heightString = inputPanel.getInput(HEIGHT_T);
                        if (!heightString.isEmpty()) {
                            try {
                                final Envelope adjExtent = mapControl.getMapTransform().getAdjustedExtent();
                                final double ratio = adjExtent.getWidth() / adjExtent.getHeight();
                                final int height = Integer.parseInt(heightString);
                                final long newWidth = Math.round(height * ratio);
                                inputPanel.setValue(WIDTH_T, String.valueOf(newWidth));
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                }
                userChangedWidth = false;
            }
        });

        tbWidth.getComponent().addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                userChangedWidth = true;
            }

            @Override
            public void focusLost(FocusEvent e) {
                updateHeight();
            }

            private void updateHeight() {
                if (userChangedWidth) {
                    if (inputPanel.getInput(RATIO_CHECKBOX_T).equals("true")) {
                        // Change image height to keep ratio
                        final String widthString = inputPanel.getInput(WIDTH_T);
                        if (!widthString.isEmpty()) {
                            try {
                                final Envelope adjExtent = mapControl.getMapTransform().getAdjustedExtent();
                                final double ratio = adjExtent.getHeight() / adjExtent.getWidth();
                                final int width = Integer.parseInt(widthString);
                                final long newHeight = Math.round(width * ratio);
                                inputPanel.setValue(HEIGHT_T, String.valueOf(newHeight));
                            } catch (NumberFormatException e) {
                            }
                        }
                    }
                }
                userChangedHeight = false;
            }
        });

        inputPanel.addInput(RATIO_CHECKBOX_T, "",
                new CheckBoxChoice(true, I18N.tr("Keep ratio")));

        inputPanel.addValidation(new MIPValidationInteger(WIDTH_T, I18N.tr("Width (pixels)")));
        inputPanel.addValidation(new MIPValidationInteger(HEIGHT_T, I18N.tr("Height (pixels)")));
        inputPanel.addValidation(new MIPValidationInteger(DPI_T, I18N.tr("DPI")));

        JButton refreshButton = new JButton(I18N.tr("Reset extent"));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputPanel.setValue(WIDTH_T, String.valueOf(mapControl.getImage().getWidth()));
                inputPanel.setValue(HEIGHT_T, String.valueOf(mapControl.getImage().getHeight()));
            }
        });
        
        // Show the dialog and get the user's choice.
        int userChoice = JOptionPane.showOptionDialog(this,
                inputPanel.getComponent(),
                I18N.tr("Export map as image"),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                MapEditorIcons.getIcon("map_catalog"),
                new Object[]{I18N.tr("OK"), I18N.tr("Cancel"), refreshButton},
                null);

        // If the user clicked OK, then show the save image dialog.
        if (userChoice == JOptionPane.OK_OPTION) {
            MapImageWriter mapImageWriter = new MapImageWriter(mapContext.getLayerModel());
            mapImageWriter.setPixelSize(MapImageWriter.MILLIMETERS_BY_INCH / Double.valueOf(inputPanel.getInput(DPI_T)));
            // If the user want a background color, let him choose one
            if(!Boolean.valueOf(inputPanel.getInput(TRANSPARENT_BACKGROUND_T))) {
                ColorPicker colorPicker = new ColorPicker(Color.white);
                if(!UIFactory.showDialog(colorPicker, true, true)) {
                    return;
                }
                mapImageWriter.setBackgroundColor(colorPicker.getColor());
            }
            // Save the picture in which location
            final SaveFilePanel outfilePanel = new SaveFilePanel(
                    "MapEditor.ExportInFile",
                    I18N.tr("Save the map as image : " + mapContext.getTitle()));
            outfilePanel.addFilter("png", I18N.tr("Portable Network Graphics"));
            outfilePanel.addFilter("tiff", I18N.tr("Tagged Image File Format"));
            outfilePanel.addFilter("jpg", I18N.tr("Joint Photographic Experts Group"));
            outfilePanel.addFilter("pdf", I18N.tr("Portable Document Format"));
            outfilePanel.loadState(); // Load last use path
            // Show save into dialog
            if (UIFactory.showDialog(outfilePanel, true, true)) {
                File outFile = outfilePanel.getSelectedFile();
                String fileName = FilenameUtils.getExtension(outFile.getName());
                if (fileName.equalsIgnoreCase("png")) {
                    mapImageWriter.setFormat(MapImageWriter.Format.PNG);
                } else if (fileName.equalsIgnoreCase("jpg")) {
                    mapImageWriter.setFormat(MapImageWriter.Format.JPEG);
                } else if (fileName.equalsIgnoreCase("pdf")) {
                    mapImageWriter.setFormat(MapImageWriter.Format.PDF);
                } else {
                    mapImageWriter.setFormat(MapImageWriter.Format.TIFF);
                }
                mapImageWriter.setBoundingBox(mapContext.getBoundingBox());
                int width = Integer.valueOf(inputPanel.getInput(WIDTH_T));
                int height = Integer.valueOf(inputPanel.getInput(HEIGHT_T));
                
                mapImageWriter.setWidth(width);
                mapImageWriter.setHeight(height);
                ExportRenderingIntoFile renderingIntoFile = new ExportRenderingIntoFile(mapImageWriter, outFile);
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(renderingIntoFile);
            }
        }
    }

    /**
     * The edited layer of the loaded Map Context has been set.
     * @param evt Event raised by the MapContext
     */
    public void onActiveLayerChange(PropertyChangeEvent evt) {
        if(getToolManager()!=null) {
            getToolManager().activeLayerChanged(evt);
            getToolManager().checkToolStatus();
        }
    }
    /**
     * The user click on the button clear selection.
     * The selected layers are cleaned.
     */
    public void onClearLayerSelection() {
        ILayer[] selectedLayers = getMapControl().getToolManager().getSelectedLayerAndStyle();
        // Loop through all selected layers.
        if (selectedLayers == null || selectedLayers.length == 0) {
            GUILOGGER.warn(I18N.tr("Please select a layer or a style in the TOC."));
        } else {
            for (ILayer layer : selectedLayers) {
                if (!layer.acceptsChilds()) {
                    if (!layer.getSelection().isEmpty()) {
                        layer.setSelection(new HashSet<Long>());
                    }
                }
            }
        }
    }
    
    /**
     * The user click on the button, all selection are cleaned in the layers
     */
    public void onClearAllSelection() {
        for (ILayer layer : mapContext.getLayers()) {
            if (!layer.acceptsChilds()) {
                if (!layer.getSelection().isEmpty()) {
                    layer.setSelection(new HashSet<Long>());
                }
            }
        }
    }

    /**
     * The user click on the button Zoom to selection
     */
    public void onZoomToAllSelection() {
        ArrayList<ILayer> selectedLayers = new ArrayList<ILayer>();
        for (ILayer iLayer : mapContext.getLayers()) {
            if (!iLayer.getSelection().isEmpty()) {
                selectedLayers.add(iLayer);
            }
        }
        if (!selectedLayers.isEmpty()) {
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.backgroundOperation(new ZoomToSelection(mapContext, selectedLayers.toArray(new ILayer[selectedLayers.size()])));
        } else {
            GUILOGGER.warn(I18N.tr("There is any selection available."));
        }
    }
    
    /**
     * Zoom on selected geometries of each selected layers
     */    
    public void onZoomToLayerSelection(){
        ILayer[] selectedLayers = getMapControl().getToolManager().getSelectedLayerAndStyle();
        // Loop through all selected layers.
        if (selectedLayers == null || selectedLayers.length == 0) {
            GUILOGGER.warn(I18N.tr("Please select a layer or a style in the TOC"));
        } else {
            BackgroundManager bm = Services.getService(BackgroundManager.class);
            bm.backgroundOperation(new ZoomToSelection(mapContext, selectedLayers));
        }
    }
    


    /**
     * The user can export the selected features into a new datasource
     */
    public void onCreateDataSourceFromSelection() {
        // Get the selected layer(s)
        ILayer[] selectedLayers = getMapControl().getToolManager().getSelectedLayerAndStyle();
        // Loop through all selected layers.
        if (selectedLayers == null || selectedLayers.length == 0) {
            GUILOGGER.warn(I18N.tr("No layers are selected."));
        } else {
            for (ILayer layer : selectedLayers) {
                Set<Long> selection = layer.getSelection();
                // If there is a nonempty selection, then ask the user to name it.
                if (!selection.isEmpty()) {
                    try {
                        String newName = CreateSourceFromSelection.showNewNameDialog(
                                this,dataManager.getDataSource(), layer.getTableReference());
                        // If newName is not null, then the user clicked OK and
                        // entered a valid name.
                        if (newName != null) {
                            BackgroundManager bm = Services.getService(
                                    BackgroundManager.class);
                            bm.backgroundOperation(new CreateSourceFromSelection(dataManager.getDataSource(),selection,
                                    layer.getTableReference(), newName));
                        }
                    } catch (SQLException ex) {
                        GUILOGGER.error(ex.getLocalizedMessage(), ex);
                    }
                } else {
                    GUILOGGER.warn(I18N.tr("Layer {0} has no selected geometries.",layer.getName()));
                }
            }
        }
    }

    
    /**
     * Give information on the behaviour of this panel related to the current
     * docking system
     * @return The panel parameter instance
     */
    @Override
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * @return {@link MapControl} linked to this {@code MapEditor}.
     */
    public MapControl getMapControl(){
            return mapControl;
    }

    @Override
    public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {
            //Update the scale in the MapEditor status bar
            mapStatusBar.setScaleDenominator(mapTransform.getScaleDenominator());
    }

    @Override
    public void imageSizeChanged(int oldWidth, int oldHeight, MapTransform mapTransform) {
        //do nothing
    }

        @Override
        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        @Override
        public EditableElement getEditableElement() {
                return mapElement;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
                if(editableElement instanceof MapElement) {
                        loadMap((MapElement)editableElement);
                }
        }

    /**
     * Remove the listeners on the current loaded document
     */
    private void removeListeners() {
            if(mapElement!=null) {
                    mapElement.removePropertyChangeListener(modificationListener);
                    mapElement.getMapContext().removePropertyChangeListener(activeLayerListener);
            }
            if(mapControl!=null && mapControl.getToolManager()!=null) {
                mapControl.getToolManager().removeToolListener(null);
            }
    }

    /**
     * The editable modified state is switching
     */
    public void onMapModified() {
            SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                                updateMapLabel();
                                mapsManager.updateDiskTree();
                                if(getToolManager()!=null) {
                                    getToolManager().updateToolsStatus();
                                }
                        }
                });
    }

    @Override
    public ToolManager getToolManager() {
        return mapControl.getToolManager();
    }

    /**
     * This task draw the image into an external file
     */
    private static class ExportRenderingIntoFile  implements BackgroundJob {
        private MapImageWriter mapImageWriter;
        private File outFile;

        private ExportRenderingIntoFile(MapImageWriter mapImageWriter, File outFile) {
            this.mapImageWriter = mapImageWriter;
            this.outFile = outFile;
        }

        @Override
        public String getTaskName() {
            return I18N.tr("Drawing into file");
        }

        @Override
        public void run(ProgressMonitor pm) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(outFile);
                mapImageWriter.write(fileOutputStream, pm);
            } catch (IOException ex) {
                GUILOGGER.error("Error while saving map editor image", ex);
            }
        }
    }

    /**
     * This task is created when the user Drag Source from GeoCatalog
     * to the map directly. The layer is created directly on the root.
     */
    private class DropDataSourceProcess implements BackgroundJob {
        private EditableElement[] editableList;

        public DropDataSourceProcess(EditableElement[] editableList) {
            this.editableList = editableList.clone();
        }

        @Override
        public void run(ProgressMonitor progress) {
            ProgressMonitor pm = progress.startTask(editableList.length);
            ILayer dropLayer = mapContext.getLayerModel();
            int i=0;
            for(EditableElement eElement : editableList) {
                if(eElement instanceof EditableSource) {
                    try {
                        EditableSource edit = (EditableSource) eElement;
                        dropLayer.addLayer(mapContext.createLayer(edit.getTableReference()));
                    } catch (LayerException e) {
                        //This layer can not be inserted, we continue to the next layer
                        GUILOGGER.warn(I18N.tr("Unable to create and drop the layer"),e);
                    }
                } else if(eElement instanceof MapElement) {
                        final MapElement mapElement = (MapElement)eElement;
                        mapElement.open(pm);
                        SwingUtilities.invokeLater(new Runnable() {
                                                @Override
                                                public void run() {
                                                        editorManager.openEditable(mapElement);
                                                }
                                        });
                        return;
                }
            }
        }

        @Override
        public String getTaskName() {
            return I18N.tr("Load the data source droped into the map editor.");
        }

    }

    /**
     * Compute the cursor projection Coordinate
     */
    private static class CursorCoordinateProcessing extends SwingWorker<Point2D,Point2D> {
        MapStatusBar mapStatusBar;
        AtomicBoolean processingCursorPosition;
        MapTransform mapTransform;
        Point mousePosition;

        private CursorCoordinateProcessing(MapStatusBar mapStatusBar, AtomicBoolean processingCursorPosition, MapTransform mapTransform, Point mousePosition) {
            this.mapStatusBar = mapStatusBar;
            this.processingCursorPosition = processingCursorPosition;
            this.mapTransform = mapTransform;
            this.mousePosition = mousePosition;
        }

        @Override
        public String toString() {
            return "MapEditor#CursorCoordinateProcessing";
        }

        @Override
        protected Point2D doInBackground() throws Exception {
            return mapTransform.toMapPoint(mousePosition.x, mousePosition.y);
        }
        @Override
        protected void done() {
            super.done();
            try {
                mapStatusBar.setCursorCoordinates(get());
            } catch (Exception ex) {
                GUILOGGER.error(ex.getLocalizedMessage(),ex);
            } finally {
                processingCursorPosition.set(false);
            }
        }
    }
}
