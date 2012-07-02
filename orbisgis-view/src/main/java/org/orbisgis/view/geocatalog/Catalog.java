/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.geocatalog;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.List;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gdms.data.SourceAlreadyExistsException;
import org.gdms.data.db.DBSource;
import org.gdms.data.db.DBTableSourceDefinition;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.EventException;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.events.ListenerContainer;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.utils.FileUtils;
import org.orbisgis.view.components.filter.FilterFactoryManager;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.geocatalog.dialogs.OpenGdmsFilePanel;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.factories.NameContains;
import org.orbisgis.view.geocatalog.filters.factories.NameNotContains;
import org.orbisgis.view.geocatalog.filters.factories.SourceTypeIs;
import org.orbisgis.view.geocatalog.renderer.DataSourceListCellRenderer;
import org.orbisgis.view.geocatalog.sourceWizards.db.ConnectionPanel;
import org.orbisgis.view.geocatalog.sourceWizards.db.TableSelectionPanel;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief This is the GeoCatalog panel. That Panel show the list of available DataSource
 * 
 * This is connected with the SourceManager model.
 * @note If you want to add new functionality to data source items without change
 * this class you can use the eventSourceListPopupMenuCreating listener container
 * to add more items in the source list pop-up menu.
 */
public class Catalog extends JPanel implements DockingPanel {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(Catalog.class);
        private static final Logger LOGGER = Logger.getLogger(Catalog.class);
        private DockingPanelParameters dockingParameters = new DockingPanelParameters(); /*!< GeoCatalog docked panel properties */

        private JList sourceList;
        private SourceListModel sourceListContent;
        //The factory shown when the user click on new factory button
        private static final String DEFAULT_FILTER_FACTORY = "name_contains";
        //The popup menu event listener manager
        private ListenerContainer<MenuPopupEventData> eventSourceListPopupMenuCreating = new ListenerContainer<MenuPopupEventData>();
        private FilterFactoryManager<IFilter> filterFactoryManager;

        /**
         * For the Unit test purpose
         * @return The source list instance
         */
        public JList getSourceList() {
                return sourceList;
        }

        /**
         * The popup menu event listener manager
         * The popup menu is being created,
         * all listeners are able to feed the menu with custom functions
         * @return 
         */
        public ListenerContainer<MenuPopupEventData> getEventSourceListPopupMenuCreating() {
                return eventSourceListPopupMenuCreating;
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
                filterFactoryManager = new FilterFactoryManager<IFilter>();
                //Set the factory that must be shown when the user click on add filter button
                filterFactoryManager.setDefaultFilterFactory(DEFAULT_FILTER_FACTORY);
                //Set listener on filter change event, this event will update the filters
                filterFactoryManager.getEventFilterChange().addListener(sourceListContent,
                        EventHandler.create(Listener.class,
                        sourceListContent, //target of event
                        "setFilters", //target method
                        "source.getFilters" //target method argument
                        ));
                //Add the filter list at the top of the geocatalog
                add(filterFactoryManager.makeFilterPanel(false), BorderLayout.NORTH);
                //Create a toolbar to add a new filter
                JToolBar toolBar = new JToolBar();
                JButton button = new JButton(I18N.tr("Add filter"),OrbisGISIcon.getIcon("add_filter"));
                button.setToolTipText(I18N.tr("Add a new data source filter"));
                button.addActionListener(EventHandler.create(ActionListener.class,filterFactoryManager,"onAddFilter"));
                toolBar.add(button);
                toolBar.addSeparator();
                dockingParameters.setToolBar(toolBar);
                
                //Add the geocatalog specific filters
                registerFilterFactories();
        }

        /**
         * Use service to return the data manager
         * @return DataManager instance
         */
        private DataManager getDataManager() {
                return (DataManager)Services.getService(DataManager.class);
        }
        /**
         * DataSource URI drop
         * @param uriDrop Uniform Resource Identifier
         */
        public void onDropURI(List<URI> uriDrop) {
                for(URI uri : uriDrop) {
                        getDataManager().getSourceManager().nameAndRegister(uri);
                }
        }
        /**
         * For JUnit purpose, return the filter factory manager
         * @return Instance of filterFactoryManager
         */
        public FilterFactoryManager<IFilter> getFilterFactoryManager() {
                return filterFactoryManager;
        }

        /**
         * Add the built-ins filter factory
         */
        private void registerFilterFactories() {
                filterFactoryManager.registerFilterFactory(new NameContains());
                filterFactoryManager.registerFilterFactory(new SourceTypeIs());
                filterFactoryManager.registerFilterFactory(new NameNotContains());
        }

        /**
         * The user click on the source list control
         * @param e The mouse event fired by the LI
         */
        public void onMouseActionOnSourceList(MouseEvent e) {
                //Manage selection of items before popping up the menu
                if (e.isPopupTrigger()) { //Right mouse button under linux and windows
                    int itemUnderMouse = -1; //Item under the position of the mouse event
                    //Find the Item under the position of the mouse cursor
                    for (int i = 0; i < sourceListContent.getSize(); i++) {
                            //If the coordinate of the cursor cover the cell bouding box
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
                        JPopupMenu popup = makePopupMenu();
                        if (popup != null) {
                                popup.show(e.getComponent(), e.getX(), e.getY());
            }

        }
        }

        /**
         * The user click on the menu item called "Add/File"
         * The user wants to open a file using the geocatalog.
         * It will open a panel dedicated to the selection of the wanted files. This
         * panel will then return the selected files.
         */
        public void onMenuAddFile() {
                SourceManager sourceManager = getDataManager().getSourceManager();
                //Create the SIF panel
                OpenGdmsFilePanel openDialog = new OpenGdmsFilePanel(I18N.tr("Select the file to add"),
                        sourceManager.getDriverManager());

                //Ask SIF to open the dialog
                if (UIFactory.showDialog(new UIPanel[]{openDialog})) {
                        // We can retrieve the files that have been selected by the user
                        File[] files = openDialog.getSelectedFiles();
                        for (int i = 0; i < files.length; i++) {
                                File file = files[i];
                                //If there is a driver compatible with
                                //this file extensions
                                if (sourceManager.getDriverManager().isFileSupported(file)) {
                                        //Try to add the data source
                                        try {
                                                String name = sourceManager.getUniqueName(FileUtils.getFileNameWithoutExtensionU(file));
                                                sourceManager.register(name, file);
                                        } catch (SourceAlreadyExistsException e) {
                                                LOGGER.error(I18N.tr("This source was already registered"), e);
                                        }
                                }
                        }
                }

        }

        /**
         * Connect to a database and add one or more tables in the geocatalog.
         */
        public void onMenuAddFromDataBase() {
                SourceManager sm = getDataManager().getSourceManager();
                final ConnectionPanel firstPanel = new ConnectionPanel(sm);
                final TableSelectionPanel secondPanel = new TableSelectionPanel(
                        firstPanel);

                if (UIFactory.showDialog(new UIPanel[]{firstPanel, secondPanel})) {
                        for (DBSource dBSource : secondPanel.getSelectedDBSources()) {
                                String name = sm.getUniqueName(dBSource.getTableName().toString());
                                sm.register(name, new DBTableSourceDefinition(dBSource));
                        }
                }
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
                        sourceListContent.clearAllSourceExceptSystemTables();
                }
        }

        /**
         * The user can remove added source from the geocatalog
         */
        public void onMenuRemoveSource() {
                SourceManager sm = getDataManager().getSourceManager();
                String[] res = getSelectedSources();
                for (String resource : res) {
                        try {
                                sm.remove(resource);
                        } catch (IllegalStateException e) {
                                LOGGER.error(I18N.tr("Cannot remove the source {0}",resource), e);

                        }
                }
        }

        /**
         * Create a popup menu corresponding to the current state of source selection
         * @return A new popup menu
         */
        private JPopupMenu makePopupMenu() {
                JPopupMenu rootMenu = new JPopupMenu();
                SourceManager sm = getDataManager().getSourceManager();
                //Popup:ClearGeocatalog (added if the datasource manager is not empty)
                if (!sm.isEmpty(true)) {
                        JMenuItem clearCatalogItem = new JMenuItem(I18N.tr("Clear the GeoCatalog"),
                                OrbisGISIcon.getIcon("bin_closed"));
                        clearCatalogItem.addActionListener(EventHandler.create(ActionListener.class,
                                this,
                                "onMenuClearGeoCatalog"));
                        rootMenu.add(clearCatalogItem);
                }
                
                //Add function to remove a source
                if(!sourceList.isSelectionEmpty()) {
                    JMenuItem removeSourceItem = new JMenuItem(
                                    I18N.tr("Remove the source"),
                                    OrbisGISIcon.getIcon("remove"));
                    removeSourceItem.addActionListener(EventHandler.create(ActionListener.class,
                            this,
                            "onMenuRemoveSource"));
                    rootMenu.add(removeSourceItem);
                }
                //Popup:Add
                JMenu addMenu = new JMenu(I18N.tr("Add"));
                rootMenu.addSeparator();
                rootMenu.add(addMenu);
                //Popup:Add:File
                JMenuItem addFileItem = new JMenuItem(
                        I18N.tr("File"),
                        OrbisGISIcon.getIcon("page_white_add"));
                addFileItem.addActionListener(EventHandler.create(ActionListener.class,
                        this,
                        "onMenuAddFile"));
                addMenu.add(addFileItem);

                //Add the database panel
                addFileItem = new JMenuItem(
                        I18N.tr("DataBase"),
                        OrbisGISIcon.getIcon("database_add"));
                addFileItem.addActionListener(EventHandler.create(ActionListener.class,
                        this,
                        "onMenuAddFromDataBase"));
                addMenu.add(addFileItem); 


                //////////////////////////////
                //Plugins
                //Add additionnal extern data source functions
                try {
                        eventSourceListPopupMenuCreating.callListeners(new MenuPopupEventData(rootMenu, this));
                } catch (EventException ex) {
                        //A listener cancel the creation of the popup menu
                        LOGGER.warn(I18N.tr("An external code stop the creation of the PopUp menu"), ex);
                        return null;
                }
                return rootMenu;
        }

        /**
         * Create the Source List ui component
         */
        private JList makeSourceList() {
                sourceList = new JList();
                //Set the list content renderer
                sourceList.setCellRenderer(new DataSourceListCellRenderer());
                //Add mouse listener for popup menu
                sourceList.addMouseListener(EventHandler.create(MouseListener.class,
                        this,
                        "onMouseActionOnSourceList",
                        "")); //This method ask the event data as argument
                //Create the list content manager
                sourceListContent = new SourceListModel();
                //Replace the default model by the GeoCatalog model
                sourceList.setModel(sourceListContent);
                SourceListTransferHandler transferHandler = new SourceListTransferHandler();
                //Call the method this.onDropURI when the user drop uri(like files) on the list control
                transferHandler.getDropListenerHandler().addListener(this,
                        EventHandler.create(Listener.class, this, "onDropURI","uriList"));
                sourceList.setTransferHandler(transferHandler);
                sourceList.setDragEnabled(true);
                //Attach the content to the DataSource instance
                sourceListContent.setListeners();
                return sourceList;
        }

        /**
         * Free listeners, Catalog must not be reachable to let the Garbage Collector
         * free this instance
         */
        public void dispose() {
                //Remove listeners linked with the source list content
                filterFactoryManager.getEventFilterChange().clearListeners();
                sourceListContent.dispose();
        }

        /**
         * Return the names of the selected sources in the geocatalog.
         * @return 
         */
        public String[] getSelectedSources() {
                Object[] selectedValues = getSourceList().getSelectedValues();
                String[] sources = new String[selectedValues.length];
                for (int i = 0; i < sources.length; i++) {
                        sources[i] = selectedValues[i].toString();
                }
                return sources;
        }

        /**
         * Give information on the behaviour of this panel related to the current
         * docking system
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
}
