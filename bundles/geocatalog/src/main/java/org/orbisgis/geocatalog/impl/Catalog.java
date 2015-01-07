/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier
 * SIG" team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
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
 * For more information, please consult: <http://www.orbisgis.org/> or contact
 * directly: info_at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;
import org.h2gis.h2spatialapi.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;
import org.orbisgis.core.Services;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.corejdbc.DriverFunctionContainer;
import org.orbisgis.corejdbc.MetaData;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.commons.utils.CollectionUtils;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.ActionDockingListener;
import org.orbisgis.view.geocatalog.jobs.DropTable;
import org.orbisgis.view.geocatalog.jobs.ImportFiles;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.view.components.actions.MenuItemServiceTracker;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactoryManager;
import org.orbisgis.viewapi.docking.DockingPanel;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.view.geocatalog.actions.ActionOnSelection;
import org.orbisgis.viewapi.edition.EditorManager;
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
        private EditorManager editorManager;
        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Catalog.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private DockingPanelParameters dockingParameters = new DockingPanelParameters();
        /*
         * !< GeoCatalog docked panel properties
         */

        private JList<ContainerItemProperties> sourceList;
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
        public JList<ContainerItemProperties> getSourceList() {
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
        public Catalog(DataManager dataManager, EditorManager editorManager) {
                super(new BorderLayout());
                this.dataManager = dataManager;
                this.editorManager = editorManager;
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
                popupActions.setAccelerators(sourceList);
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
            String[] res = getSelectedSources();
            for (String source : res) {
                    TableEditableElementImpl tableDocument = new TableEditableElementImpl(source, dataManager);
                    SwingUtilities.invokeLater(new OpenEditableInSwingThread(tableDocument, editorManager));
            }
        }

        @Override
        public DriverFunction getImportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
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

        @Override
        public DriverFunction getExportDriverFromExt(String ext,DriverFunction.IMPORT_DRIVER_TYPE type ) {
            for(DriverFunction driverFunction : fileDrivers) {
                if(driverFunction.getImportDriverType() == type) {
                    for(String fileExt : driverFunction.getExportFormats()) {
                        if(fileExt.equalsIgnoreCase(ext)) {
                            return driverFunction;
                        }
                    }
                }
            }
            return null;
        }

        private void importFile(DriverFunction.IMPORT_DRIVER_TYPE type, String panelMessage) {
            OpenFilePanel linkSourcePanel = new OpenFilePanel("Geocatalog.LinkFile" ,panelMessage);
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
                importFileJob.execute();
            }
        }

        /**
         * The user click on the menu item called "Import/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuImportFile() {
            importFile(DriverFunction.IMPORT_DRIVER_TYPE.COPY, I18N.tr("Select the file to import"));
        }

        /**
         * The user click on the menu item called "Add/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuAddLinkedFile() {
            importFile(DriverFunction.IMPORT_DRIVER_TYPE.LINK, I18N.tr("Select the file to open"));
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
            int countExternalTable = 0;
            int countSystemTable = 0;
            int countOther = 0;
            ArrayList<String> sources = new ArrayList<String>();
            List<String> reservedTables = java.util.Arrays.asList("spatial_ref_sys", "geography_columns", "geometry_columns", "raster_columns", "raster_overviews");
            try (Connection connection = dataManager.getDataSource().getConnection()) {
                List<ContainerItemProperties> selectedValues = getSourceList().getSelectedValuesList();
                for (ContainerItemProperties source : selectedValues) {
                    String tableName = source.getKey();
                    MetaData.TableType tableType = MetaData.getTableType(connection, tableName);
                    if (tableType.equals(MetaData.TableType.EXTERNAL)) {
                        countExternalTable++;
                        sources.add(tableName);
                    } else if (tableType.equals(MetaData.TableType.SYSTEM_TABLE)) {
                        countSystemTable++;
                    } else if (reservedTables.contains(tableName.toLowerCase())){
                        countSystemTable++;
                    }else {
                        sources.add(tableName);
                        countOther++;
                    }
                }
            } catch (SQLException ex) {
                LOGGER.error(ex.getLocalizedMessage(), ex);
            }
            //We display a warning because some SYSTEM_TABLE have been selected.
            if (countSystemTable > 0) {
                JOptionPane.showMessageDialog(this,
                        I18N.tr("Cannot remove permanently a table system."),
                        I18N.tr("Remove GeoCatalog tables"),
                        JOptionPane.WARNING_MESSAGE);
            } else {
                //We display the table type
                StringBuilder sb = new StringBuilder(I18N.tr("Do you want..."));
                if (countOther > 0) {
                    sb.append(I18N.trn("\n...to remove permanently {0} table", "\n...to remove permanently {0} tables", countOther, countOther));
                }
                if (countExternalTable > 0) {
                    sb.append(I18N.trn("\n...to disconnect {0} external table", "\n...to disconnect {0} external tables", countExternalTable, countExternalTable));
                }
                sb.append("?");
                int option = JOptionPane.showConfirmDialog(this,
                        sb.toString(),
                        I18N.tr("Delete GeoCatalog tables"),
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.YES_OPTION) {
                    new DropTable(dataManager.getDataSource(), sources.toArray(new String[sources.size()]), this).execute();
                }
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
                                new ExportInFileOperation(source,
                                savedFile, getExportDriverFromExt(FilenameUtils.getExtension(savedFile.getName()),
                                        DriverFunction.IMPORT_DRIVER_TYPE.COPY),dataManager.getDataSource()).execute();
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
            addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE.LINK, I18N.tr("Select the folder to open"));
        }

        /**
         * The user can copy several files from a folder
         */
        public void onMenuImportFilesFromFolder() {
            addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE.COPY, I18N.tr("Select the folder to import"));
        }

        /**
         * The user can load several files from a folder
         * @param type
         */
        public void addFilesFromFolder(DriverFunction.IMPORT_DRIVER_TYPE type, String message) {
            OpenFolderPanel folderSourcePanel = new OpenFolderPanel("Geocatalog.LinkFolder" ,message);
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
                    new ImportFiles(this, this, fileToLoad, dataManager, type).execute();
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
                        this,"onMenuAddLinkedFile"),KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
                       ).addStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)).setParent(PopupMenu.M_ADD));
                //Popup:Add:Folder
                popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_FOLDER,I18N.tr("Folder"),
                        I18N.tr("Add a set of file from an hard drive folder."),
                        OrbisGISIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                        this,"onMenuAddFilesFromFolder"),KeyStroke.getKeyStroke("ctrl alt O")).setParent(PopupMenu.M_ADD));

                //Popup:Add:DataBase
                //popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_DB,I18N.tr("DataBase"),
                //        I18N.tr("Add one or more tables from a DataBase"),
                //        OrbisGISIcon.getIcon("database_add"),EventHandler.create(ActionListener.class,
                //        this,"onMenuAddFromDataBase"),null).setParent(PopupMenu.M_ADD));
            }            
            //Popup:Import
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT,I18N.tr("Import")).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_IMPORT));
            //Popup:Import:File
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FILE,I18N.tr("File"),
                    I18N.tr("Copy the content of a file from hard drive."),
                    OrbisGISIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFile"),KeyStroke.getKeyStroke("ctrl I")).setParent(PopupMenu.M_IMPORT));
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FOLDER,I18N.tr("Folder"),
                    I18N.tr("Add a set of file from an hard drive folder."),
                    OrbisGISIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFilesFromFolder"),KeyStroke.getKeyStroke("ctrl alt I")).setParent(PopupMenu.M_IMPORT));

            //Popup:Save
            popupActions.addAction(new ActionOnSelection(
                        PopupMenu.M_SAVE,I18N.tr("Save"),
                        true,
                        getListSelectionModel()
                    ).setLogicalGroup(PopupMenu.GROUP_ADD));
            //Popup:Save:File
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_FILE,I18N.tr("File"),
                    I18N.tr("Save selected sources in files"),OrbisGISIcon.getIcon("page_white_save"),
                    EventHandler.create(ActionListener.class,this,"onMenuSaveInfile"),getListSelectionModel()).
                    setKeyStroke(KeyStroke.getKeyStroke("ctrl S")).setParent(PopupMenu.M_SAVE));
            //Popup:Save:Db
            //TODO Add linked table then transfer data
            //popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_DB,I18N.tr("Database"),
            //        I18N.tr("Save selected sources in a data base"),OrbisGISIcon.getIcon("database_save"),
            //        EventHandler.create(ActionListener.class,this,"onMenuSaveInDB"),getListSelectionModel()).setParent(PopupMenu.M_SAVE));
            //Popup:Open attributes
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_OPEN_ATTRIBUTES,I18N.tr("Open the attributes"),
                    I18N.tr("Open the data source table"),OrbisGISIcon.getIcon("table"),
                    EventHandler.create(ActionListener.class,this, "onMenuShowTable"),getListSelectionModel()).setKeyStroke(KeyStroke.getKeyStroke("ctrl T")).setLogicalGroup(PopupMenu.GROUP_OPEN));
            //Popup:Remove sources
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_REMOVE,I18N.tr("Remove the source"),
                    I18N.tr("Remove from this list the selected sources."),OrbisGISIcon.getIcon("remove"),
                    EventHandler.create(ActionListener.class,this,"onMenuRemoveSource"),getListSelectionModel())
                     .setKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)).setLogicalGroup(PopupMenu.GROUP_CLOSE));
            //Popup:Refresh
            popupActions.addAction(new DefaultAction(PopupMenu.M_REFRESH,I18N.tr("Refresh"),
                    I18N.tr("Read the content of the database"),
                    OrbisGISIcon.getIcon("refresh"),EventHandler.create(ActionListener.class,
                    this,"refreshSourceList"),KeyStroke.getKeyStroke("ctrl R")).setLogicalGroup(PopupMenu.GROUP_OPEN));
        }

        public void refreshSourceList() {
            sourceListContent.onDataManagerChange();
        }

        /**
         * Create the Source List ui component
         */
        private JList makeSourceList() {
                sourceList = new JList<>();
                //Set the list content renderer
                sourceList.setCellRenderer(new DataSourceListCellRenderer(sourceList));
                //Add mouse listener for popup menu
                sourceList.addMouseListener(EventHandler.create(MouseListener.class,
                        this,
                        "onMouseActionOnSourceList",
                        "")); //This method ask the event data as argument
                //Create the list content manager
                sourceListContent = new SourceListModel(dataManager);
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
                List<ContainerItemProperties> selectedValues = getSourceList().getSelectedValuesList();
                String[] sources = new String[selectedValues.size()];
                int i=0;
                for (ContainerItemProperties source : selectedValues) {
                        sources[i++] = source.getKey();
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
