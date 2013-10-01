/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.h2gis.h2spatialapi.DriverFunction;
import org.orbisgis.core.Services;
import org.orbisgis.core.api.DataManager;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.sputilities.TableLocation;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.H2GISProgressMonitor;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionDockingListener;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.view.components.filter.DefaultActiveFilter;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.geocatalog.actions.ActionOnNonEmptySourceList;
import org.orbisgis.view.geocatalog.actions.ActionOnSelection;
import org.orbisgis.view.geocatalog.ext.GeoCatalogMenu;
import org.orbisgis.view.geocatalog.ext.PopupMenu;
import org.orbisgis.view.geocatalog.ext.PopupTarget;
import org.orbisgis.view.geocatalog.ext.TitleActionBar;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.factories.NameContains;
import org.orbisgis.view.geocatalog.filters.factories.NameNotContains;
import org.orbisgis.view.geocatalog.filters.factories.SourceTypeIs;
import org.orbisgis.view.geocatalog.io.ExportInFileOperation;
import org.orbisgis.view.geocatalog.renderer.DataSourceListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.TableEditableElement;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This is the GeoCatalog panel. That Panel show the list of available
 * DataSource
 *
 * This is connected with the SourceManager model. @note If you want to add new
 * functionality to data source items without change this class you can use the
 * eventSourceListPopupMenuCreating listener container to add more items in the
 * source list pop-up menu.
 */
public class Catalog extends JPanel implements DockingPanel,TitleActionBar,PopupTarget,DriverFunctionContainer {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Catalog.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private DockingPanelParameters dockingParameters = new DockingPanelParameters();
        /*
         * !< GeoCatalog docked panel properties
         */

        private JList sourceList;
        private SourceListModel sourceListContent;
        //The factory shown when the user click on new factory button
        private static final String DEFAULT_FILTER_FACTORY = "name_contains";
        private FilterFactoryManager<IFilter,DefaultActiveFilter> filterFactoryManager;
        private ActionCommands dockingActions = new ActionCommands();
        private ActionCommands popupActions = new ActionCommands();
        // Action trackers
        private MenuItemServiceTracker<PopupTarget,PopupMenu> popupActionTracker;
        private MenuItemServiceTracker<TitleActionBar,GeoCatalogMenu> dockingActionTracker;
        private ServiceTracker<DriverFunction, DriverFunction> driverFunctionTracker;
        private List<DriverFunction> fileDrivers = new LinkedList<>();

        /**
         * For the Unit test purpose
         *
         * @return The source list instance
         */
        public JList getSourceList() {
                return sourceList;
        }

        @Override
        public void addDriverFunction(DriverFunction driverFunction) {
            fileDrivers.add(driverFunction);
        }

        @Override
        public void removeDriverFunction(DriverFunction driverFunction) {
            fileDrivers.remove(driverFunction);
        }

        /**
         * Default constructor
         */
        public Catalog() {
                super(new BorderLayout());
                dockingParameters.setName("geocatalog");
                dockingParameters.setTitle(I18N.tr("GeoCatalog"));
                dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("geocatalog"));
                dockingParameters.setCloseable(true);
                //Add the Source List in a Scroll Pane,
                //then add the scroll pane in this panel
                add(new JScrollPane(makeSourceList()), BorderLayout.CENTER);
                //Init the filter factory manager
                filterFactoryManager = new FilterFactoryManager<IFilter,DefaultActiveFilter>();
                //Set the factory that must be shown when the user click on add filter button
                filterFactoryManager.setDefaultFilterFactory(DEFAULT_FILTER_FACTORY);
                //Set listener on filter change event, this event will update the filters
                FilterFactoryManager.FilterChangeListener refreshFilterListener = EventHandler.create(FilterFactoryManager.FilterChangeListener.class,
                        sourceListContent, //target of event
                        "setFilters", //target method
                        "source.getFilters" //target method argument
                        );
                filterFactoryManager.getEventFilterChange().addListener(sourceListContent,refreshFilterListener);
                filterFactoryManager.getEventFilterFactoryChange().addListener(sourceListContent,refreshFilterListener);
                //Add the filter list at the top of the geocatalog
                add(filterFactoryManager.makeFilterPanel(false), BorderLayout.NORTH);
                //Create an action to add a new filter
                dockingActions.addAction(new DefaultAction(GeoCatalogMenu.M_ADD_FILTER,
                        I18N.tr("Add filter"), I18N.tr("Add a new data source filter"), OrbisGISIcon.getIcon("add_filter"),
                        EventHandler.create(ActionListener.class, filterFactoryManager, "onAddFilter"),
                        null));
                // Set the built-in actions to docking frame
                dockingParameters.setDockActions(dockingActions.getActions());
                // Add a listener to put additional actions to this docking
                dockingActions.addPropertyChangeListener(new ActionDockingListener(dockingParameters));
                //Add the geocatalog specific filters
                registerFilterFactories();
                // Register built-ins popup actions
                createPopupActions();
        }

        public void registeTrackers(BundleContext hostContext) {
            popupActionTracker = new MenuItemServiceTracker<PopupTarget, PopupMenu>(hostContext,PopupMenu.class,
                    popupActions,this);
            dockingActionTracker = new MenuItemServiceTracker<TitleActionBar, GeoCatalogMenu>(hostContext,
                    GeoCatalogMenu.class,dockingActions,this);
            DriverFunctionTracker driverFunctionTrackerCustom = new DriverFunctionTracker(hostContext, this);
            driverFunctionTracker = new ServiceTracker<>(hostContext, DriverFunction.class ,driverFunctionTrackerCustom);
            // Begin the track
            popupActionTracker.open();
            dockingActionTracker.open();
            driverFunctionTracker.open();
        }
        /**
         * Get the actions related to frame title.
         * @return actions related to frame title.
         */
        public ActionCommands getActionsDocking() {
            return dockingActions;
        }

        /**
         * Get the actions of PopupMenu related to Source list items.
         * @return actions of PopupMenu related to Source list items.
         */
        public ActionCommands getActionsPopup() {
            return popupActions;
        }

        /**
         * Use service to return the data manager
         *
         * @return DataManager instance
         */
        private DataManager getDataManager() {
                return Services.getService(DataManager.class);
        }

        /**
         * DataSource URI drop. Currently used on file drop by the {@link  SourceListTransferHandler}.
         *
         * @param uriDrop Uniform Resource Identifier
         */
        public void onDropURI(List<URI> uriDrop) {
                DataManager src = getDataManager();
                for (URI uri : uriDrop) {
                        try {
                            src.registerDataSource(uri);
                        } catch (SQLException ex) {
                            LOGGER.error("Cannot load dropped data source", ex);
                        }
                }
        }

        /**
         * For JUnit purpose, return the filter factory manager
         *
         * @return Instance of filterFactoryManager
         */
        public FilterFactoryManager<IFilter,DefaultActiveFilter> getFilterFactoryManager() {
                return filterFactoryManager;
        }

        /**
         * Add the built-ins filter factory
         */
        private void registerFilterFactories() {
                filterFactoryManager.registerFilterFactory(new NameContains());
                filterFactoryManager.registerFilterFactory(new NameNotContains());
                filterFactoryManager.registerFilterFactory(new SourceTypeIs());
        }

        /**
         * The user click on the source list control
         *
         * @param e The mouse event fired by the LI
         */
        public void onMouseActionOnSourceList(MouseEvent e) {
                //Manage selection of items before popping up the menu
                if (e.isPopupTrigger()) { //Right mouse button under linux and windows
                        int itemUnderMouse = -1; //Item under the position of the mouse event
                        //Find the Item under the position of the mouse cursor
                        for (int i = 0; i < sourceListContent.getSize(); i++) {
                                //If the coordinate of the cursor cover the cell bounding box
                                if (sourceList.getCellBounds(i, i).contains(e.getPoint())) {
                                        itemUnderMouse = i;
                                        break;
                                }
                        }
                        //Retrieve all selected items index
                        int[] selectedItems = sourceList.getSelectedIndices();
                        //If there are a list item under the mouse
                        if ((selectedItems != null) && (itemUnderMouse != -1)) {
                                //If the item under the mouse was not previously selected
                                if (!CollectionUtils.contains(selectedItems, itemUnderMouse)) {
                                        //Control must be pushed to add the list item to the selection
                                        if (e.isControlDown()) {
                                                sourceList.addSelectionInterval(itemUnderMouse, itemUnderMouse);
                                        } else {
                                                //Unselect the other items and select only the item under the mouse
                                                sourceList.setSelectionInterval(itemUnderMouse, itemUnderMouse);
                                        }
                                }
                        } else if (itemUnderMouse == -1) {
                                //Unselect all items
                                sourceList.clearSelection();
                        }
                        //Selection are ready, now create the popup menu
                        JPopupMenu popup = new JPopupMenu();
                        popupActions.copyEnabledActions(popup);
                        if (popup.getComponentCount()>0) {
                                popup.show(e.getComponent(), e.getX(), e.getY());
                        }

                }
        }

        /**
         * The user select one or more data source and request to open
         * the table editor
         */
        public void onMenuShowTable() {
                String[] res = getSelectedSources();
                EditorManager editorManager = Services.getService(EditorManager.class);
                for (String source : res) {
                        TableEditableElement tableDocument = new TableEditableElement(source);
                        editorManager.openEditable(tableDocument);
                }
        }

        private DriverFunction getDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
            for(DriverFunction driverFunction : fileDrivers) {
                if(driverFunction.getImportDriverType() == type) {
                    for(String fileExt : driverFunction.getImportFormats()) {
                        if(fileExt.equalsIgnoreCase(ext)) {
                            return driverFunction;
                        }
                    }
                }
            }
            return null;
        }

        private void importFile(DriverFunction.IMPORT_DRIVER_TYPE type) {
            OpenFilePanel linkSourcePanel = new OpenFilePanel("Geocatalog.LinkFile" ,I18N.tr("Select the file to "));
            for(DriverFunction driverFunction : fileDrivers) {
                if(driverFunction.getImportDriverType() == type) {
                    for(String fileExt : driverFunction.getImportFormats()) {
                        linkSourcePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                    }
                }
            }
            linkSourcePanel.loadState();
            //Ask SIF to open the dialog
            if (UIFactory.showDialog(linkSourcePanel, true, true)) {
                // We can retrieve the files that have been selected by the user
                File[] files = linkSourcePanel.getSelectedFiles();
                for (File file : files) {
                    String ext = FilenameUtils.getExtension(file.getName());
                    DriverFunction driverFunction = getDriverFromExt(ext, type);
                    if(driverFunction == null) {
                        //When opening file in geocatalog, cannot found a driver able to load ex:.JPG extension
                        LOGGER.error(I18N.tr("No driver found for {0} extension", ext));
                    } else {
                        ImportFile importFileJob = new ImportFile(driverFunction, file, FileUtils.getNameFromURI(file.toURI()));
                        BackgroundManager bm = Services.getService(BackgroundManager.class);
                        bm.nonBlockingBackgroundOperation(importFileJob);
                    }
                }
            }
        }

        /**
         * The user click on the menu item called "Import/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuImportFile() {
            importFile(DriverFunction.IMPORT_DRIVER_TYPE.COPY);
        }

        /**
         * The user click on the menu item called "Add/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuAddLinkedFile() {
            importFile(DriverFunction.IMPORT_DRIVER_TYPE.LINK);
            /*
                //Create the SIF panel
                OpenGdmsFilePanel openDialog = new OpenGdmsFilePanel(I18N.tr("Select the file to add"),
                        sourceManager.getDriverManager());
                openDialog.loadState();
                //Ask SIF to open the dialog
                if (UIFactory.showDialog(openDialog, true, true)) {
                        // We can retrieve the files that have been selected by the user
                        File[] files = openDialog.getSelectedFiles();
                        for (File file : files) {
                            //If there is a driver compatible with
                            //this file extensions
                            if (sourceManager.getDriverManager().isDriverFileSupported(file)) {
                                //Try to add the data source
                                try {
                                    String name = sourceManager.getUniqueName(FilenameUtils.removeExtension(file.getName()));
                                    sourceManager.register(name, file);
                                } catch (SourceAlreadyExistsException e) {
                                    LOGGER.error(I18N.tr("This source was already registered"), e);
                                }
                            }
                        }
                }
                */

        }

        /**
         * Connect to a database and add one or more tables in the geocatalog.
         */
        public void onMenuAddFromDataBase() {
                /*
                SourceManager sm = getDataManager().getSourceManager();
                TableImportPanel tableImportPanel = new TableImportPanel(sm);
                tableImportPanel.setVisible(true);
                */
        }

        /**
         * The user click on the menu item called "clear geocatalog"
         */
        public void onMenuClearGeoCatalog() {
                //User must validate this action
                int option = JOptionPane.showConfirmDialog(this,
                        I18N.tr("All data source of the GeoCatalog will be removed. Are you sure ?"),
                        I18N.tr("Clear the GeoCatalog"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                        try {
                            sourceListContent.clearAllSourceExceptSystemTables();
                        } catch (SQLException ex) {
                            LOGGER.error(I18N.tr("Could not remove tables"), ex);
                        }
                }
        }

        /**
         * The user can remove added source from the geocatalog
         */
        public void onMenuRemoveSource() {
            String[] res = getSelectedSources();
            int option = JOptionPane.showConfirmDialog(this,
                    I18N.tr("The following tables will be removed.\n{0}\n The content of theses tables will be permanently lost !\n Are you sure ?", StringUtils.join(res, "\n")),
                    I18N.tr("Delete GeoCatalog tables"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                DataSource ds = Services.getService(DataSource.class);
                try(Connection connection = ds.getConnection()) {
                    connection.setAutoCommit(false);
                    for (String resource : res) {
                        TableLocation tableLocation = TableLocation.parse(resource);
                        try(Statement st = connection.createStatement()) {
                            st.execute(String.format("drop table %s", tableLocation));

                        } catch (SQLException ex) {
                            LOGGER.error(I18N.tr("Cannot remove the source {0}", resource), ex);
                            connection.rollback();
                            return;
                        }
                    }
                    connection.commit();
                } catch (SQLException ex) {
                    LOGGER.error(I18N.trc("Tables are database tables, drop means delete tables", "Cannot drop the tables"), ex);
                }
            }
        }

        /**
         * The user can export a source in a file.
         */
        public void onMenuSaveInfile() {
                String[] res = getSelectedSources();
                DataSource ds = Services.getService(DataSource.class);
                for (String source : res) {
                        final SaveFilePanel outfilePanel = new SaveFilePanel(
                                "Geocatalog.SaveInFile",
                                I18N.tr("Save the source : {0}", source));
                        for(DriverFunction driverFunction : fileDrivers) {
                            for(String fileExt : driverFunction.getExportFormats()) {
                                outfilePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                            }
                        }
                        outfilePanel.loadState();
                        if (UIFactory.showDialog(outfilePanel, true, true)) {
                                final File savedFile = outfilePanel.getSelectedFile().getAbsoluteFile();
                                BackgroundManager bm = Services.getService(BackgroundManager.class);
                                bm.backgroundOperation(new ExportInFileOperation(source,
                                savedFile, getDriverFromExt(FilenameUtils.getExtension(savedFile.getName()), DriverFunction.IMPORT_DRIVER_TYPE.COPY)));
                        }

                }

        }

        /**
         * The user can save a source in a database
         */
        public void onMenuSaveInDB() {
                /*
                DataManager dm = Services.getService(DataManager.class);
                SourceManager sm = dm.getSourceManager();
                String[] res = getSelectedSources();
                TableExportPanel tableExportPanel = new TableExportPanel(res, sm);
                tableExportPanel.setVisible(true);
                */
        }

        /**
         * The user can load several files from a folder
         */
        public void onMenuAddFilesFromFolder() {
                /*
                final OpenGdmsFolderPanel folderPanel = new OpenGdmsFolderPanel(I18N.tr("Add files from a folder"));
                folderPanel.loadState();
                if (UIFactory.showDialog(folderPanel, true, true)) {
                        File[] files = folderPanel.getSelectedFiles();
                        for (final File file : files) {
                                // for each folder, we apply the method processFolder.
                                // We use the filter selected by the user in the panel
                                // to succeed in this operation.
                                BackgroundManager bm = Services.getService(BackgroundManager.class);
                                bm.backgroundOperation(new BackgroundJob() {

                                        @Override
                                        public String getTaskName() {
                                                return I18N.tr("Add from folder");
                                        }

                                        @Override
                                        public void run(org.orbisgis.progress.ProgressMonitor pm) {
                                                processFolder(file, folderPanel.getSelectedFilter(), pm);
                                        }
                                });

                        }
                }
                */
        }

        /**
         * The user can load several WMS layers from the same server.
         */
        public void onMenuAddWMSServer() {
                /*
                DataManager dm = Services.getService(DataManager.class);
                SourceManager sm = dm.getSourceManager();
                SRSPanel srsPanel = new SRSPanel();
                LayerConfigurationPanel layerConfiguration = new LayerConfigurationPanel(srsPanel);
                WMSConnectionPanel wmsConnection = new WMSConnectionPanel(layerConfiguration);
                if (UIFactory.showDialog(new UIPanel[]{wmsConnection,
                                layerConfiguration, srsPanel})) {
                        WMService service = wmsConnection.getServiceDescription();
                        Capabilities cap = service.getCapabilities();
                        MapImageFormatChooser mfc = new MapImageFormatChooser(service.getVersion());
                        mfc.setTransparencyRequired(true);
                        String validImageFormat = mfc.chooseFormat(cap.getMapFormats());
                        if (validImageFormat == null) {
                                LOGGER.error(I18N.tr("Cannot find a valid image format for this WMS server"));
                        } else {
                                Object[] layers = layerConfiguration.getSelectedLayers();
                                for (Object layer : layers) {
                                        String layerName = ((MapLayer) layer).getName();
                                        String uniqueLayerName = layerName;
                                        if (sm.exists(layerName)) {
                                                uniqueLayerName = sm.getUniqueName(layerName);
                                        }
                                        URI origin = URI.create(service.getServerUrl());
                                        StringBuilder url = new StringBuilder(origin.getQuery());
                                        url.append("SERVICE=WMS&REQUEST=GetMap");
                                        String version = service.getVersion();
                                        url.append("&VERSION=").append(version);
                                        if(WMService.WMS_1_3_0.equals(version)){
                                            url.append("&CRS=");
                                        } else {
                                            url.append("&SRS=");
                                        }
                                        url.append(srsPanel.getSRS());
                                        url.append("&LAYERS=").append(layerName);
                                        url.append("&FORMAT=").append(validImageFormat);
                                        try{
                                            URI streamUri = new URI(origin.getScheme(), origin.getUserInfo(),origin.getHost(), origin.getPort(),
                                                origin.getPath(), url.toString(), origin.getFragment());
                                            WMSStreamSource wmsSource = new WMSStreamSource(streamUri);
                                            StreamSourceDefinition streamSourceDefinition = new StreamSourceDefinition(wmsSource);
                                            sm.register(uniqueLayerName, streamSourceDefinition);
                                        } catch (UnsupportedEncodingException uee){
                                            LOGGER.error(I18N.tr("Can't read the given URI: "+uee.getCause()));
                                        } catch (URISyntaxException use){
                                            LOGGER.error(I18N.tr("The given URI contains illegal character"),use);
                                        }
                                }
                        }
                }
                */
        }

        /**
         * the method that actually process the content of a directory, or a
         * file. If the file is acceptable by the FileFilter, it is processed
         *
         * @param file File or Folder to register
         * @param pm Progress manager
         */
        private void processFolder(File file, FileFilter filter, org.orbisgis.progress.ProgressMonitor pm) {
                /*
                if (file.isDirectory()) {
                        pm.startTask(file.getName(), 100);
                        File[] files = file.listFiles();
                        if (files != null) {
                            for (File content : files) {
                                    if (pm.isCancelled()) {
                                            break;
                                    }
                                    processFolder(content, filter, pm);
                            }
                        }
                        pm.endTask();
                } else {
                        DataManager dm = Services.getService(DataManager.class);
                        DriverManager dr = dm.getSourceManager().getDriverManager();
                        if (filter.accept(file) && dr.isDriverFileSupported(file)) {
                                SourceManager sourceManager = dm.getSourceManager();
                                try {
                                        String name = sourceManager.getUniqueName(FilenameUtils.removeExtension(file.getName()));
                                        sourceManager.register(name, file);
                                } catch (SourceAlreadyExistsException e) {
                                        LOGGER.error(I18N.tr("The source is already registered : "), e);
                                }
                        }
                }
                */
        }
        private void createPopupActions() {
            //Popup:Add
            popupActions.addAction(new DefaultAction(PopupMenu.M_ADD,I18N.tr("Add")).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_ADD));
            //Popup:Add:File
            popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_FILE,I18N.tr("File"),
                    I18N.tr("Add a file from hard drive."),
                    OrbisGISIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuAddLinkedFile"),null).setParent(PopupMenu.M_ADD));
            //Popup:Add:Folder
            popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_FOLDER,I18N.tr("Folder"),
                    I18N.tr("Add a set of file from an hard drive folder."),
                    OrbisGISIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuAddFilesFromFolder"),null).setParent(PopupMenu.M_ADD));

            //Popup:Add:DataBase
            popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_DB,I18N.tr("DataBase"),
                    I18N.tr("Add one or more tables from a DataBase"),
                    OrbisGISIcon.getIcon("database_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuAddFromDataBase"),null).setParent(PopupMenu.M_ADD));
            //Popup:Add:WMS
            popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_WMS,I18N.tr("WMS server"),
                    I18N.tr("Add a WebMapService"),
                    OrbisGISIcon.getIcon("server_connect"),EventHandler.create(ActionListener.class,
                    this,"onMenuAddWMSServer"),null).setParent(PopupMenu.M_ADD));
            //Popup:Import
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT,I18N.tr("Import")).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_IMPORT));
            //Popup:Import:File
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FILE,I18N.tr("File"),
                    I18N.tr("Copy the content of a file from hard drive."),
                    OrbisGISIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFile"),null).setParent(PopupMenu.M_IMPORT));

            //Popup:Save
            popupActions.addAction(new ActionOnSelection(
                        PopupMenu.M_SAVE,I18N.tr("Save"),
                        true,
                        getListSelectionModel()
                    ).setLogicalGroup(PopupMenu.GROUP_ADD));
            //Popup:Save:File
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_FILE,I18N.tr("File"),
                    I18N.tr("Save selected sources in files"),OrbisGISIcon.getIcon("page_white_save"),
                    EventHandler.create(ActionListener.class,this,"onMenuSaveInfile"),getListSelectionModel()).setParent(PopupMenu.M_SAVE));
            //Popup:Save:Db
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_DB,I18N.tr("Database"),
                    I18N.tr("Save selected sources in a data base"),OrbisGISIcon.getIcon("database_save"),
                    EventHandler.create(ActionListener.class,this,"onMenuSaveInDB"),getListSelectionModel()).setParent(PopupMenu.M_SAVE));
            //Popup:Open attributes
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_OPEN_ATTRIBUTES,I18N.tr("Open the attributes"),
                    I18N.tr("Open the data source table"),OrbisGISIcon.getIcon("openattributes"),
                    EventHandler.create(ActionListener.class,this, "onMenuShowTable"),getListSelectionModel()).setLogicalGroup(PopupMenu.GROUP_OPEN));
            //Popup:Remove sources
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_REMOVE,I18N.tr("Remove the source"),
                    I18N.tr("Remove from this list the selected sources."),OrbisGISIcon.getIcon("remove"),
                    EventHandler.create(ActionListener.class,this,"onMenuRemoveSource"),getListSelectionModel())
                        .setLogicalGroup(PopupMenu.GROUP_CLOSE));
            //Clear Geo-catalog
            popupActions.addAction(new ActionOnNonEmptySourceList(PopupMenu.M_CLEAR_CATALOG,I18N.tr("Clear the GeoCatalog"),
                    I18N.tr("Remove all sources in this list"),OrbisGISIcon.getIcon("bin_closed"),
                    EventHandler.create(ActionListener.class,this,"onMenuClearGeoCatalog"), sourceListContent).setLogicalGroup(PopupMenu.GROUP_CLOSE));
        }

        /**
         * Create the Source List ui component
         */
        private JList makeSourceList() {
                sourceList = new JList();
                //Set the list content renderer
                sourceList.setCellRenderer(new DataSourceListCellRenderer(sourceList));
                //Add mouse listener for popup menu
                sourceList.addMouseListener(EventHandler.create(MouseListener.class,
                        this,
                        "onMouseActionOnSourceList",
                        "")); //This method ask the event data as argument
                //Create the list content manager
                DataSource dataSource = Services.getService(DataSource.class);
                sourceListContent = new SourceListModel(dataSource);
                //Replace the default model by the GeoCatalog model
                sourceList.setModel(sourceListContent);
                SourceListTransferHandler transferHandler = new SourceListTransferHandler();
                //Call the method this.onDropURI when the user drop uri(like files) on the list control
                transferHandler.getDropListenerHandler().addListener(this,
                        EventHandler.create(SourceListTransferHandler.DropUriListener.class, this, "onDropURI", "uriList"));
                sourceList.setTransferHandler(transferHandler);
                sourceList.setDragEnabled(true);
                //Attach the content to the DataSource instance
                sourceListContent.setListeners();
                return sourceList;
        }

        /**
         * Free listeners, Catalog must not be reachable to let the Garbage
         * Collector free this instance
         */
        public void dispose() {
                //Remove listeners linked with the source list content
                filterFactoryManager.getEventFilterChange().clearListeners();
                filterFactoryManager.getEventFilterFactoryChange().clearListeners();
                sourceListContent.dispose();
                // Close trackers
                if(popupActionTracker!=null) {
                    popupActionTracker.close();
                }
                if(dockingActionTracker!=null) {
                    dockingActionTracker.close();
                }
                if(driverFunctionTracker!=null) {
                    driverFunctionTracker.close();
                }
        }

        @Override
        public String[] getSelectedSources() {
                Object[] selectedValues = getSourceList().getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        sources[i] = selectedValues[i].toString();
                }
                return sources;
        }

        /**
         * Give information on the behaviour of this panel related to the
         * current docking system
         *
         * @return The panel parameter instance
         */
        @Override
        public DockingPanelParameters getDockingParameters() {
                return dockingParameters;
        }

        @Override
        public JComponent getComponent() {
                return this;
        }

        @Override
        public ListSelectionModel getListSelectionModel() {
            return sourceList.getSelectionModel();
        }


        private static class ImportFile implements BackgroundJob {
            private DriverFunction driverFunction;
            private File file;
            private String tableName;

            private ImportFile(DriverFunction driverFunction, File file, String tableName) {
                this.driverFunction = driverFunction;
                this.file = file;
                this.tableName = tableName;
            }

            @Override
            public String getTaskName() {
                return I18N.tr("Import file");
            }

            @Override
            public void run(ProgressMonitor pm) {
                DataSource ds = Services.getService(DataSource.class);
                try(Connection connection = ds.getConnection()) {
                    driverFunction.importFile(connection, tableName ,file, new H2GISProgressMonitor(pm));
                } catch (SQLException | IOException ex) {
                    LOGGER.error(I18N.tr("Cannot import the file"), ex);
                }
            }
        }
}
