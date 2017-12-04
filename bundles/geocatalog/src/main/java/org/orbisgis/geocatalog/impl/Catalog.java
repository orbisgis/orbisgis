/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.geocatalog.impl;

import org.h2gis.api.DriverFunction;
import org.h2gis.utilities.JDBCUtilities;
import org.orbisgis.commons.utils.CollectionUtils;
import org.orbisgis.corejdbc.DataManager;
import org.orbisgis.dbjobs.api.DatabaseView;
import org.orbisgis.dbjobs.api.DriverFunctionContainer;
import org.orbisgis.dbjobs.jobs.DropTable;
import org.orbisgis.dbjobs.jobs.ExportInFileOperation;
import org.orbisgis.geocatalog.api.GeoCatalogMenu;
import org.orbisgis.geocatalog.api.PopupMenu;
import org.orbisgis.geocatalog.api.PopupTarget;
import org.orbisgis.geocatalog.api.TitleActionBar;
import org.orbisgis.geocatalog.icons.GeocatalogIcon;
import org.orbisgis.geocatalog.impl.actions.ActionOnSelection;
import org.orbisgis.geocatalog.impl.filters.IFilter;
import org.orbisgis.geocatalog.impl.filters.factories.NameContains;
import org.orbisgis.geocatalog.impl.filters.factories.NameNotContains;
import org.orbisgis.geocatalog.impl.filters.factories.SourceTypeIs;
import org.orbisgis.geocatalog.impl.renderer.DataSourceListCellRenderer;
import org.orbisgis.sif.common.ContainerItemProperties;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.components.filter.DefaultActiveFilter;
import org.orbisgis.sif.components.filter.FilterFactoryManager;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.beans.EventHandler;
import java.net.URI;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;

/**
 * This is the GeoCatalog panel. That Panel show the list of available
 * DataSource
 *
 * This is connected with the DataSource model.
 */
@Component(service = DockingPanel.class, immediate = true)
public class Catalog extends JPanel implements DockingPanel, TitleActionBar, PopupTarget, DatabaseView {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Catalog.class);
        private static final Logger LOGGER = LoggerFactory.getLogger(Catalog.class);
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
        private DriverFunctionContainer driverFunctionContainer;
        private DataManager dataManager;
        private ExecutorService executorService = null;

        /**
         * For the Unit test purpose
         *
         * @return The source list instance
         */
        public JList<ContainerItemProperties> getSourceList() {
                return sourceList;
        }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC)
    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void unsetExecutorService(ExecutorService executorService) {
        this.executorService = null;
    }

    @Reference
    public void setDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = driverFunctionContainer;
    }

    public void unsetDriverFunctionContainer(DriverFunctionContainer driverFunctionContainer) {
        this.driverFunctionContainer = null;
    }

        /**
         * Default constructor
         */
        public Catalog() {
            super(new BorderLayout());
        }

        @Reference
        public void setDataManager(DataManager dataManager) {
            this.dataManager = dataManager;
        }

        public void unsetDataManager(DataManager dataManager) {
            this.dataManager = null;
        }

        /**
         * Initialise panel
         */
        @Activate
        public void init() {
                dockingParameters.setName("geocatalog");
                dockingParameters.setTitle(I18N.tr("GeoCatalog"));
                dockingParameters.setTitleIcon(GeocatalogIcon.getIcon("geocatalog"));
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
                        I18N.tr("Add filter"), I18N.tr("Add a new data source filter"), GeocatalogIcon.getIcon("add_filter"),
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

        @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
        public void addGeoCatalogMenu(GeoCatalogMenu geoCatalogMenu) {
            dockingActions.addActionFactory(geoCatalogMenu, this);
        }

        public void removeGeoCatalogMenu(GeoCatalogMenu geoCatalogMenu) {
            dockingActions.removeActionFactory(geoCatalogMenu);
        }

        @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
        public void addPopupMenu(PopupMenu popupMenu) {
            popupActions.addActionFactory(popupMenu, this);
        }

        public void removePopupMenu(PopupMenu popupMenu) {
            popupActions.removeActionFactory(popupMenu);
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
         * The user click on the menu item called "Import/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuImportFile() {
            driverFunctionContainer.importFile(this, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
        }

        /**
         * The user click on the menu item called "Add/File" The user wants to
         * open a file using the geocatalog. It will open a panel dedicated to
         * the selection of the wanted files. This panel will then return the
         * selected files.
         */
        public void onMenuAddLinkedFile() {
            driverFunctionContainer.importFile(this, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
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
            List<String> sources = new ArrayList<>();
            List<ContainerItemProperties> selectedValues = getSourceList().getSelectedValuesList();
            for (ContainerItemProperties source : selectedValues) {
                sources.add(source.getKey());
            }
            if(!sources.isEmpty()) {
                DropTable dropTable = DropTable.onMenuRemoveSource(dataManager.getDataSource(), sources, this, this);
                if(dropTable != null) {
                    executeJob(dropTable);
                }
            }
    }

        private void executeJob(SwingWorker worker) {
            if(executorService == null) {
                worker.execute();
            } else {
                executorService.execute(worker);
            }
        }

        /**
         * The user can export a source in a file.
         */
        public void onMenuSaveInfile() {
            List<String> sources = Arrays.asList(getSelectedSources());
            ExportInFileOperation exportJob = ExportInFileOperation.saveInfile(dataManager.getDataSource(), sources, driverFunctionContainer);
            if(exportJob != null) {
                executeJob(exportJob);
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
            driverFunctionContainer.addFilesFromFolder(this, DriverFunction.IMPORT_DRIVER_TYPE.LINK);
        }

        /**
         * The user can copy several files from a folder
         */
        public void onMenuImportFilesFromFolder() {
            driverFunctionContainer.addFilesFromFolder(this, DriverFunction.IMPORT_DRIVER_TYPE.COPY);
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
                popupActions.addAction(new DefaultAction(PopupMenu.M_ADD,I18N.tr("Link to")).setMenuGroup(true).setLogicalGroup(PopupMenu.GROUP_ADD));
                //Popup:Add:File
                popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_FILE,I18N.tr("File"),
                        I18N.tr("Link a file from hard drive."),
                        GeocatalogIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                        this,"onMenuAddLinkedFile"),KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)
                       ).addStroke(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)).setParent(PopupMenu.M_ADD));
                //Popup:Add:Folder
                popupActions.addAction(new DefaultAction(PopupMenu.M_ADD_FOLDER,I18N.tr("Folder"),
                        I18N.tr("Link a set of file from an hard drive folder."),
                        GeocatalogIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
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
                    GeocatalogIcon.getIcon("page_white_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFile"),KeyStroke.getKeyStroke("ctrl I")).setParent(PopupMenu.M_IMPORT));
            popupActions.addAction(new DefaultAction(PopupMenu.M_IMPORT_FOLDER,I18N.tr("Folder"),
                    I18N.tr("Add a set of file from an hard drive folder."),
                    GeocatalogIcon.getIcon("folder_add"),EventHandler.create(ActionListener.class,
                    this,"onMenuImportFilesFromFolder"),KeyStroke.getKeyStroke("ctrl alt I")).setParent(PopupMenu.M_IMPORT));

            //Popup:Save
            popupActions.addAction(new ActionOnSelection(
                        PopupMenu.M_SAVE,I18N.tr("Save"),
                        true,
                        getListSelectionModel()
                    ).setLogicalGroup(PopupMenu.GROUP_ADD));
            //Popup:Save:File
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_FILE,I18N.tr("File"),
                    I18N.tr("Save selected sources in files"),GeocatalogIcon.getIcon("page_white_save"),
                    EventHandler.create(ActionListener.class,this,"onMenuSaveInfile"),getListSelectionModel()).
                    setKeyStroke(KeyStroke.getKeyStroke("ctrl S")).setParent(PopupMenu.M_SAVE));
            //Popup:Save:Db
            //TODO Add linked table then transfer data
            //popupActions.addAction(new ActionOnSelection(PopupMenu.M_SAVE_DB,I18N.tr("Database"),
            //        I18N.tr("Save selected sources in a data base"),OrbisGISIcon.getIcon("database_save"),
            //        EventHandler.create(ActionListener.class,this,"onMenuSaveInDB"),getListSelectionModel()).setParent(PopupMenu.M_SAVE));
            //Popup:Remove sources
            popupActions.addAction(new ActionOnSelection(PopupMenu.M_REMOVE,I18N.tr("Remove the source"),
                    I18N.tr("Remove from this list the selected sources."),GeocatalogIcon.getIcon("remove"),
                    EventHandler.create(ActionListener.class,this,"onMenuRemoveSource"),getListSelectionModel())
                     .setKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)).setLogicalGroup(PopupMenu.GROUP_CLOSE));
            //Popup:Refresh
            popupActions.addAction(new DefaultAction(PopupMenu.M_REFRESH,I18N.tr("Refresh"),
                    I18N.tr("Read the content of the database"),
                    GeocatalogIcon.getIcon("refresh"),EventHandler.create(ActionListener.class,
                    this,"refreshSourceList"),KeyStroke.getKeyStroke("ctrl R")).setLogicalGroup(PopupMenu.GROUP_OPEN));
        }

        @Override
        public void onDatabaseUpdate(String entity, String... identifier) {
            refreshSourceList();
        }

        @Override
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
}
