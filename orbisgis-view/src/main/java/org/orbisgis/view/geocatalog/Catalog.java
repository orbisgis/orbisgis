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
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import org.orbisgis.core.Services;
import org.orbisgis.coreapi.api.DataManager;
import org.orbisgis.coreapi.api.DriverFunctionContainer;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.sif.multiInputPanel.ComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.DirectoryComboBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.H2GISProgressMonitor;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionDockingListener;
import org.orbisgis.view.geocatalog.jobs.DropTable;
import org.orbisgis.view.geocatalog.jobs.ImportFiles;
import org.orbisgis.view.workspace.ViewWorkspace;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.view.components.filter.DefaultActiveFilter;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.viewapi.docking.DockingPanel;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.viewapi.edition.EditableElementException;
import org.orbisgis.view.geocatalog.actions.ActionOnNonEmptySourceList;
import org.orbisgis.view.geocatalog.actions.ActionOnSelection;
import org.orbisgis.viewapi.edition.EditorManager;
import org.orbisgis.viewapi.geocatalog.ext.GeoCatalogExt;
import org.orbisgis.viewapi.geocatalog.ext.GeoCatalogMenu;
import org.orbisgis.viewapi.geocatalog.ext.PopupMenu;
import org.orbisgis.viewapi.geocatalog.ext.PopupTarget;
import org.orbisgis.viewapi.geocatalog.ext.TitleActionBar;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.factories.NameContains;
import org.orbisgis.view.geocatalog.filters.factories.NameNotContains;
import org.orbisgis.view.geocatalog.filters.factories.SourceTypeIs;
import org.orbisgis.view.geocatalog.io.ExportInFileOperation;
import org.orbisgis.view.geocatalog.renderer.DataSourceListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.orbisgis.view.table.TableEditableElementImpl;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * This is the GeoCatalog panel. That Panel show the list of available
 * DataSource
 *
 * This is connected with the DataSource model.
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
        private DataManager dataManager;

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
        public Catalog(DataManager dataManager) {
                super(new BorderLayout());
                this.dataManager = dataManager;
                dockingParameters.setName("geocatalog");
                dockingParameters.setTitle(I18N.tr("GeoCatalog"));
                dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("geocatalog"));
                dockingParameters.setCloseable(true);
                //Add the Source List in a Scroll Pane,
                //then add the scroll pane in this panel
                add(new JScrollPane(makeSourceList()), BorderLayout.CENTER);
                //Init the filter factory manager
                filterFactoryManager = new FilterFactoryManager<>();
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
         * DataSource URI drop. Currently used on file drop by the {@link  SourceListTransferHandler}.
         *
         * @param uriDrop Uniform Resource Identifier
         */
        public void onDropURI(List<URI> uriDrop) {
                for (URI uri : uriDrop) {
                        try {
                            dataManager.registerDataSource(uri);
                            refreshSourceList();
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
                BackgroundManager manager = Services.getService(BackgroundManager.class);
                if(manager != null) {
                    String[] res = getSelectedSources();
                    for (String source : res) {
                            TableEditableElementImpl tableDocument = new TableEditableElementImpl(source, dataManager);
                            OpenTableEditor job = new OpenTableEditor(tableDocument);
                            manager.nonBlockingBackgroundOperation(job);
                    }
                }
        }

        @Override
        public DriverFunction getDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
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
            OpenFilePanel linkSourcePanel = new OpenFilePanel("Geocatalog.LinkFile" ,I18N.tr("Select the file to import"));
            for(DriverFunction driverFunction : fileDrivers) {
                try {
                    if(driverFunction.getImportDriverType() == type) {
                        for(String fileExt : driverFunction.getImportFormats()) {
                            linkSourcePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.debug(ex.getLocalizedMessage(), ex);
                }
            }
            linkSourcePanel.loadState();
            //Ask SIF to open the dialog
            if (UIFactory.showDialog(linkSourcePanel, true, true)) {
                // We can retrieve the files that have been selected by the user
                List<File> files = Arrays.asList(linkSourcePanel.getSelectedFiles());
                ImportFiles importFileJob = new ImportFiles(this, this, files, dataManager, type);
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(importFileJob);
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
         * The user can remove added source from the geocatalog
         */
        public void onMenuRemoveSource() {
            String[] res = getSelectedSources();
            int option = JOptionPane.showConfirmDialog(this,
                    I18N.tr("The following tables will be removed.\n{0}\n The content of theses tables will be permanently lost !\n Are you sure ?", StringUtils.join(res, "\n")),
                    I18N.tr("Delete GeoCatalog tables"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == JOptionPane.YES_OPTION) {
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.nonBlockingBackgroundOperation(new DropTable(dataManager.getDataSource(), res));
            }
        }

        /**
         * The user can export a source in a file.
         */
        public void onMenuSaveInfile() {
                String[] res = getSelectedSources();
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
                                savedFile, getDriverFromExt(FilenameUtils.getExtension(savedFile.getName()),
                                        DriverFunction.IMPORT_DRIVER_TYPE.COPY),dataManager.getDataSource()));
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
            addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE.COPY);
        }

        /**
         * The user can copy several files from a folder
         */
        public void onMenuImportFilesFromFolder() {
            addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE.COPY);
        }

        /**
         * The user can load several files from a folder
         */
        public void addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE type) {
            OpenFolderPanel folderSourcePanel = new OpenFolderPanel("Geocatalog.LinkFolder" ,I18N.tr("Select the folder to import"));
            for(DriverFunction driverFunction : fileDrivers) {
                try {
                    if(driverFunction.getImportDriverType() == type) {
                        for(String fileExt : driverFunction.getImportFormats()) {
                            folderSourcePanel.addFilter(fileExt, driverFunction.getFormatDescription(fileExt));
                        }
                    }
                } catch (Exception ex) {
                    LOGGER.debug(ex.getLocalizedMessage(), ex);
                }
            }
            folderSourcePanel.loadState();
                if (UIFactory.showDialog(folderSourcePanel, true, true)) {
                    File directory = folderSourcePanel.getSelectedFile();
                    Collection files = org.apache.commons.io.FileUtils.listFiles(directory,
                            new ImportFileFilter(folderSourcePanel.getSelectedFilter()), DirectoryFileFilter.DIRECTORY);
                    List<File> fileToLoad = new ArrayList<>(files.size());
                    for (Object file : files) {
                            if(file instanceof File) {
                                fileToLoad.add((File)file);
                            }
                    }
                    // for each folder, we apply the method processFolder.
                    // We use the filter selected by the user in the panel
                    // to succeed in this operation.
                    BackgroundManager bm = Services.getService(BackgroundManager.class);
                    bm.nonBlockingBackgroundOperation(new ImportFiles(this, this, fileToLoad, dataManager, type));
                }
        }

        private static class ImportFileFilter implements IOFileFilter {
            private FileFilter fileFilter;

            private ImportFileFilter(FileFilter fileFilter) {
                this.fileFilter = fileFilter;
            }

            @Override
            public boolean accept(File file) {
                return fileFilter.accept(file);
            }

            @Override
            public boolean accept(File dir, String name) {
                return accept(new File(dir, name));
            }
        }

        /**
         * The user can load several WMS layers from the same server.
         */
        public void onMenuAddWMSServer() {
                /*
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
            boolean isEmbeddedDataBase = true;
            try(Connection connection = dataManager.getDataSource().getConnection()) {
                DatabaseMetaData meta = connection.getMetaData();
                isEmbeddedDataBase = JDBCUtilities.isH2DataBase(meta) && !meta.getURL().startsWith("jdbc:h2:tcp:");
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            //Popup:Add
            if(isEmbeddedDataBase) {
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
                //popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_DB,I18N.tr("DataBase"),
                //        I18N.tr("Add one or more tables from a DataBase"),
                //        OrbisGISIcon.getIcon("database_add"),EventHandler.create(ActionListener.class,
                //        this,"onMenuAddFromDataBase"),null).setParent(PopupMenu.M_ADD));
            }
            //Popup:Add:WMS
            //popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_WMS,I18N.tr("WMS server"),
            //        I18N.tr("Add a WebMapService"),
            //        OrbisGISIcon.getIcon("server_connect"),EventHandler.create(ActionListener.class,
            //        this,"onMenuAddWMSServer"),null).setParent(PopupMenu.M_ADD));
            //Popup:Import
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT,I18N.tr("Import")).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_IMPORT));
            //Popup:Import:File
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FILE,I18N.tr("File"),
                    I18N.tr("Copy the content of a file from hard drive."),
                    OrbisGISIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFile"),null).setParent(PopupMenu.M_IMPORT));
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FOLDER,I18N.tr("Folder"),
                    I18N.tr("Add a set of file from an hard drive folder."),
                    OrbisGISIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFilesFromFolder"),null).setParent(PopupMenu.M_IMPORT));

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
            //Popup:Refresh
            popupActions.addAction(new DefaultAction(PopupMenu.M_REFRESH,I18N.tr("Refresh"),
                    I18N.tr("Read the content of the database"),
                    OrbisGISIcon.getIcon("arrow_refresh"),EventHandler.create(ActionListener.class,
                    this,"refreshSourceList"),null).setLogicalGroup(PopupMenu.GROUP_OPEN));
        }

        public void refreshSourceList() {
            sourceListContent.onDataManagerChange();
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
                sourceListContent = new SourceListModel(dataManager.getDataSource());
                //Replace the default model by the GeoCatalog model
                sourceList.setModel(sourceListContent);
                SourceListTransferHandler transferHandler = new SourceListTransferHandler(dataManager);
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

        private static class OpenTableEditor implements BackgroundJob {
            private TableEditableElement source;

            private OpenTableEditor(TableEditableElement source) {
                this.source = source;
            }

            @Override
            public void run(ProgressMonitor pm) {
                try {
                    source.open(pm);
                    EditorManager editorManager = Services.getService(EditorManager.class);
                    if(SwingUtilities.isEventDispatchThread()) {
                        editorManager.openEditable(source);
                    } else {
                        SwingUtilities.invokeLater(new OpenEditableInSwingThread(source, editorManager));
                    }
                } catch (EditableElementException ex) {
                    LOGGER.error(I18N.tr("Cannot open the table editor"),ex);
                }
            }

            @Override
            public String getTaskName() {
                return I18N.tr("Open source file");
            }
        }

        /**
         * Open window in swing process
         */
        private static class OpenEditableInSwingThread implements Runnable {
            EditableElement element;
            EditorManager editorManager;

            private OpenEditableInSwingThread(EditableElement element, EditorManager editorManager) {
                this.element = element;
                this.editorManager = editorManager;
            }

            @Override
            public void run() {
                editorManager.openEditable(element);
            }
        }
}
