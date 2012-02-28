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
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.util.Map.Entry;
import java.util.*;
import javax.swing.*;
import org.gdms.source.SourceManager;
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
 */
public class Catalog extends JPanel implements DockingPanel {
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
    /**
     * For the Unit test purpose
     * @return The source list instance
     */
    public JList getSourceList() {
        return sourceList;
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
        
        //TODO if some filters are shows, refresh all factories combo box
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
     */
    private void addFilterComponent(Component newFilterComponent,ActiveFilter activeFilter) {
        //TODO replace the last filter combobox by a JLabel that contain
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
                    EventHandler.create(PropertyChangeListener.class, this,"reloadFilters"));
            //Create the Swing component
            Component swingFiterField = filterFactory.makeFilterField(activeFilter);
            addFilterComponent(swingFiterField, activeFilter);
            //Update the filters
            reloadFilters();
        }
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
