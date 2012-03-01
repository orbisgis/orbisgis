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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.gdms.source.SourceManager;
import org.orbisgis.base.events.EventException;
import org.orbisgis.base.events.ListenerContainer;
import org.orbisgis.utils.CollectionUtils;
import org.orbisgis.utils.I18N;
import org.orbisgis.view.components.ContainerItemKey;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.geocatalog.filters.ActiveFilter;
import org.orbisgis.view.geocatalog.filters.DataSourceFilterFactory;
import org.orbisgis.view.geocatalog.filters.IFilter;
import org.orbisgis.view.geocatalog.filters.factories.NameContains;
import org.orbisgis.view.geocatalog.filters.factories.NameNotContains;
import org.orbisgis.view.geocatalog.filters.factories.SourceTypeIs;
import org.orbisgis.view.geocatalog.renderer.DataSourceListCellRenderer;
import org.orbisgis.view.icons.OrbisGISIcon;


/**
 * @brief This is the GeoCatalog panel. That Panel show the list of avaible DataSource
 * 
 * This is connected with the SourceManager model.
 * @note If you want to add new functionnality to data source items without change
 * this class you can use the eventSourceListPopupMenuCreating listener container
 * to add more items in the source list popup menu.
 */
public class Catalog extends JPanel implements DockingPanel {
    private static final Logger LOGGER = Logger.getLogger(Catalog.class);
    private DockingPanelParameters dockingParameters = new DockingPanelParameters(); /*!< GeoCatalog docked panel properties */
    private JList sourceList;
    private SourceListModel sourceListContent;
    private JPanel filterListPanel;/*!< This panel contain the set of filters */
    //List of active filters
    private Map<Component,ActiveFilter> filterValues = Collections.synchronizedMap(new HashMap<Component,ActiveFilter>());
    //List of filter factories
    private Map<String,DataSourceFilterFactory> filterFactories = Collections.synchronizedMap(new HashMap<String,DataSourceFilterFactory>());
    //Factory index, this retrieve the factory name from an integer in
    //all JComboBox filter factories
    private List<ContainerItemKey> filterFactoriesComboLabels = new ArrayList<ContainerItemKey>();
    //The factory shown when the user click on new factory button
    private static final String DEFAULT_FILTER_FACTORY = "name_contains";
    //The popup menu event listener manager
    private ListenerContainer<MenuPopupEventData> eventSourceListPopupMenuCreating = new ListenerContainer<MenuPopupEventData>();
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
    public Catalog(SourceManager sourceManager) {
            super(new BorderLayout());
            dockingParameters.setTitle(I18N.getString("orbisgis.org.orbisgis.Catalog.title"));
            dockingParameters.setTitleIcon(OrbisGISIcon.getIcon("geocatalog"));
            //Add the filter list at the top of the geocatalog
            add(makeFilterPanel(), BorderLayout.NORTH);
            //Add the Source List in a Scroll Pane, 
            //then add the scroll pane in this panel
            add(new JScrollPane(makeSourceList(sourceManager)), BorderLayout.CENTER);
            registerFilterFactories();
    }
    
    /**
     * Add the built-ins filter factory
     */
    private void registerFilterFactories() {
        registerFilterFactory(new NameContains());
        registerFilterFactory(new SourceTypeIs());
        registerFilterFactory(new NameNotContains());
    }
    
    /**
     * Add a filter factory
     * @param filterFactory The filter factory instance
     */
    public final void registerFilterFactory(DataSourceFilterFactory filterFactory) {
        //Add filter factory in the HashMap
        filterFactories.put(filterFactory.getFactoryId(), filterFactory);
        
        //Add filter factory label and id in a list (for all GUI ComboBox)
        filterFactoriesComboLabels.add(new ContainerItemKey(filterFactory.getFactoryId(),filterFactory.getFilterLabel()));
        
        //TODO if some filters are already shown, refresh all factories combo box (Future Plugin-Filter ?)
    }
    /**
     * Remove all filters registered with the provided factory id
     * @param factoryId The factory id returned by DataSourceFilterFactory.getFactoryId
     */
    public void removeFilters(String factoryId) {
        //Collect all components to remove
        Stack<Component> filterPanelsToRemove = new Stack<Component>();
        for(Entry<Component,ActiveFilter> filter : filterValues.entrySet()) {
            if(filter.getValue().getFactoryId().equals(factoryId)) {
                //Found a filter panel registered by factoryId
                filterPanelsToRemove.add(filter.getKey());
            }
        }
        //Remove components
        while(!filterPanelsToRemove.isEmpty()) {
            onRemoveFilter(filterPanelsToRemove.pop());
        }
    }
    /**
     * Call by listeners when the user click on the Remove button 
     * or change the factory combobox value
     * @param filterPanel The filter panel instance
     */
    public void onRemoveFilter(Component filterPanel) {
        //Remove the filter value
        filterValues.remove(filterPanel);
        //Remove the filter panel from the GUI filter list panel
        filterListPanel.remove(filterPanel);
        filterListPanel.updateUI();
        //Update filters
        reloadFilters();
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
     * The user click on the menu item called "clear geocatalog"
     */
    public void onMenuClearGeoCatalog() {
        //User must validate this action
        int option = JOptionPane.showConfirmDialog(this,
                                I18N.getString("orbisgis.view.geocatalog.validateClearMessage"),
                                I18N.getString("orbisgis.view.geocatalog.validateClearTitle"),
                                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (option == JOptionPane.YES_OPTION) {
            sourceListContent.clearAllSourceExceptSystemTables();
        }
    }
    /**
     * Create a popup menu corresponding to the current state of source selection
     * @return A new popup menu
     */
    private JPopupMenu makePopupMenu() {
        JPopupMenu rootMenu = new JPopupMenu();
        //Clear catalog item added if the datasource manager is not empty
        if(!sourceListContent.isDataSourceManagerEmpty()) {
            JMenuItem clearCatalogItem = new JMenuItem(I18N.getString("orbisgis.view.geocatalog.clearGeoCatalogMenuItem"),
                                                OrbisGISIcon.getIcon("trash"));
            clearCatalogItem.addActionListener(EventHandler.create(ActionListener.class,
                                                                this,
                                                                "onMenuClearGeoCatalog"));
            rootMenu.add(clearCatalogItem);
        }
        //Add additionnal extern data source functions
        try {
            eventSourceListPopupMenuCreating.callListeners(new MenuPopupEventData(rootMenu, this));
        } catch (EventException ex) {
            //A listener cancel the creation of the popup menu
            LOGGER.warn(I18N.getString("orbisgis.view.geocatalog.listenerMenuThrown"),ex);
            return null;
        }        
        return rootMenu;
    }
    /**
     * The user click on add filter button
     */
    public void onAddFilter() {
        //Add the default filter
        addFilter(DEFAULT_FILTER_FACTORY, "");
    }
    /**
     * Add the swing filter component to the filter gui
     * This method will add a remove filter button,
     * a filter factory JComboBox and the specified component
     * @param newFilterComponent Returned by a DataSourceFilterFactory
     * @warning onFilterChanged Must retrieve the FilterFactoriesComboBox !
     */
    private void addFilterComponent(Component newFilterComponent,ActiveFilter activeFilter) {
        //the factory name
        JPanel filterPanel = new JPanel(new BorderLayout());
        //Create the remove button
        JButton removeButton = makeRemoveFilterButton();
        //Attach listener, will call the onRemoveFilter method
        //with the parent container as argument
        removeButton.addActionListener(
                EventHandler.create(
                ActionListener.class, this, "onRemoveFilter","source.parent")
        );
        //Add the button in the filter panel
        filterPanel.add(removeButton,BorderLayout.WEST);        
        //Create a layout to contain the factory and filter components
        JPanel factoryAndFilter = new JPanel(new BorderLayout());
        //Create the filter factory combobox
        factoryAndFilter.add(makeFilterFactoriesComboBox(activeFilter.getFactoryId()),BorderLayout.WEST);
        if(newFilterComponent!=null) {
            //Add the factory component in the filter panel
            factoryAndFilter.add(newFilterComponent,BorderLayout.CENTER);
        }
        filterPanel.add(factoryAndFilter,BorderLayout.CENTER);
        //Add the component in the filter list contents
        filterValues.put(filterPanel, activeFilter);
        //Add the component in the filter list GUI
        filterListPanel.add(filterPanel);   
        //Refresh the GUI
        filterListPanel.updateUI();
    }
    /**
     * Create a new filter in the UI filter list
     * @param filterFactoryId The factory identification
     * @param filterValue The filter value
     */
    public void addFilter(String filterFactoryId,String filterValue) {
        if(filterFactories.containsKey(filterFactoryId)) {
            //Retrieve the factory
            DataSourceFilterFactory filterFactory = filterFactories.get(filterFactoryId);
            //Make the active filter content instance
            ActiveFilter activeFilter = new ActiveFilter(filterFactoryId,filterValue);
            //If the filter value is modified reloadFilters must be called
            activeFilter.addPropertyChangeListener(ActiveFilter.PROP_CURRENTFILTERVALUE,
                    EventHandler.create(PropertyChangeListener.class, this,"onFilterChanged"));
            //Create the Swing component
            Component swingFiterField = filterFactory.makeFilterField(activeFilter);
            addFilterComponent(swingFiterField, activeFilter);
            //Update the filters
            reloadFilters();
        }
    }

    /**
     * Replace all FactoryComboBox by Labels
     * Navigation through components is quite difficult and verbose
     */
    private void replaceFactoryComboBoxByLabels() {
        boolean uiChange=false;
        for (Component removeButtonFactoryFilter : filterListPanel.getComponents()) {
            if(removeButtonFactoryFilter instanceof JPanel) {
                Component factoryAndFilter = ((BorderLayout)((JPanel)removeButtonFactoryFilter).getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if(factoryAndFilter!=null) {
                    Component factoryList = ((BorderLayout)((JPanel)factoryAndFilter).getLayout()).getLayoutComponent(BorderLayout.WEST);
                    if(factoryList==null || !(factoryList instanceof JComboBox || factoryList instanceof JLabel)) {
                        //Could not find Filter Factory list
                        LOGGER.debug("Update onFilterChanged according to the change on Filter Factory ComboBox panel layout");
                    } else {
                        if(factoryList instanceof JComboBox) {
                            String itemLabel = ((JComboBox)factoryList).getSelectedItem().toString();
                            //Remove the factory list
                            ((JPanel)factoryAndFilter).remove(factoryList);
                            //Place the Label
                            ((JPanel)factoryAndFilter).add(new JLabel(itemLabel), BorderLayout.WEST);
                            ((JPanel)factoryAndFilter).doLayout();
                            uiChange=true;
                        }
                    }                    
                }
            }
        }
        
        if(uiChange) {
            this.updateUI();
        }
    }
    /**
     * The input of a filter has been edited by the user
     */
    public void onFilterChanged() {
        //The user change the content of the filter
        //Then the user accept the current factories
        //Replace all factories by labels to free spaces
        replaceFactoryComboBoxByLabels();
        reloadFilters();
    }
    /**
     * Regenerate all filters from filters components
     * and update the List with the filters
     */
    public void reloadFilters() {
        List<IFilter> generatedFilters = new ArrayList<IFilter>();
        //For each active filter
        for(ActiveFilter activeFilter : filterValues.values()) {
            if(filterFactories.containsKey(activeFilter.getFactoryId())) {
                //Retrieve the factory
                DataSourceFilterFactory filterFactory = filterFactories.get(activeFilter.getFactoryId());
                //Ask the factory to build 
                IFilter generatedFilter = filterFactory.getFilter(activeFilter.getCurrentFilterValue());
                generatedFilters.add(generatedFilter);     
            }
        }
        //Set the filters and update the list
        sourceListContent.setFilters(generatedFilters);
    }
    /**
     * The user selected a filter factory
     * A new filter is shown with the selected filter factory
     * @param filterFactoryId The filter factory name
     */
    public void onChooseFilterFactory(String filterFactoryId) {
        //Add a new filter with an empty value
        addFilter(filterFactoryId,"");
    }
    /**
     * Create a new filter factories combo box
     * @return A new instance of filterFactoriesComboBox
     */ 
    private JComboBox makeFilterFactoriesComboBox(String selectedFactory) {
        //Set a unique data model for all filterFactoriesCombo
        JComboBox filterFactoriesCombo = new JComboBox(this.filterFactoriesComboLabels.toArray());
        //Select the factory
        filterFactoriesCombo.setSelectedItem(new ContainerItemKey(selectedFactory, ""));
        //Add a listener to remove the filter
        filterFactoriesCombo.addActionListener(
                EventHandler.create(ActionListener.class, this,
                "onRemoveFilter","source.parent.parent"));
        //Add a listener to add a new filter with the selected factory
        filterFactoriesCombo.addActionListener(
                EventHandler.create(ActionListener.class, this,
                "onChooseFilterFactory","source.selectedItem.getKey"));
        return filterFactoriesCombo;
    }
    /**
     * Build the remove filter button component
     * @return The button
     * @note listener are created in this function
     */
    private JButton makeRemoveFilterButton() {
        //Create a compact button
        JButton removeFilterButton = new JButton(OrbisGISIcon.getIcon("delete"));
        removeFilterButton.setToolTipText(I18N.getString("orbisgis.view.geocatalog.removefilter"));
        removeFilterButton.setMargin(new Insets(0, 0, 0, 0));
        removeFilterButton.setBorderPainted(false);
        removeFilterButton.setContentAreaFilled(false);
        return removeFilterButton;
    }
    /**
     * Build the add filter button component
     * @return The button
     * @note listener are created in this function
     */
    private Component makeAddFilterButton() {
        //This JPanel set the button at the top
        JPanel buttonAlignement = new JPanel(new BorderLayout());
        //Create a compact button
        JButton addFilterButton = new JButton(OrbisGISIcon.getIcon("add_filter"));
        addFilterButton.setMargin(new Insets(0, 0, 0, 0));
        addFilterButton.setBorderPainted(false);
        addFilterButton.setContentAreaFilled(false);
        buttonAlignement.add(addFilterButton,BorderLayout.NORTH);
        //Toottip
        addFilterButton.setToolTipText(I18N.getString("orbisgis.view.geocatalog.addfilter"));
        //Apply action listener
        addFilterButton.addActionListener( 
                EventHandler.create(ActionListener.class, this, "onAddFilter")                
                );        
        return buttonAlignement;
    }
    
    /**
     * Create the filter panel
     * @return The builded panel
     */
    private JPanel makeFilterPanel() {
        //This panel contain the button panel and the filter list panel
        JPanel buttonAndFilterList = new JPanel(new BorderLayout());
        //Add the toggle button
        buttonAndFilterList.add(makeAddFilterButton(), BorderLayout.LINE_START);
        //GridLayout with 1 column (vertical stack) and n(0) rows
        filterListPanel = new JPanel(new GridLayout(0,1));
        //Filter List must take all horizontal space
        //CENTER will expand the content to take all avaible place
        buttonAndFilterList.add(filterListPanel, BorderLayout.CENTER);
        //Add the AddFilter button and Filter list in the main filter panel
        return buttonAndFilterList;
    }
    /**
     * Create the Source List ui compenent
     */
    private JList makeSourceList(SourceManager sourceManager) {
        sourceList = new JList();
        //Set the list content renderer
        sourceList.setCellRenderer(new DataSourceListCellRenderer()); 
        //Add mouse listener for popup menu
        sourceList.addMouseListener(EventHandler.create(MouseListener.class,
                                    this,
                                    "onMouseActionOnSourceList",
                                    "")); //This method ask the event data as argument
        //Create the list content manager
        sourceListContent = new SourceListModel(sourceManager); 
        //Replace the default model by the GeoCatalog model
        sourceList.setModel(sourceListContent); 
        //Attach the content to the DataSource instance
        sourceListContent.setListeners();
        return sourceList;
    }
    /**
     * Free listeners, Catalog must not be reachable to let the Garbage Collector
     * free this instance
     */
    public void dispose() {
        sourceListContent.dispose();
    }
    /**
     * Give information on the behaviour of this panel related to the current
     * docking system
     * @return The panel parameter instance
     */
    public DockingPanelParameters getDockingParameters() {
        return dockingParameters;
    }

    /**
     * Return the content of the view.
     * @return An awt content to show in this panel
     */
    public Component getComponent() {
        return this;
    }
}
