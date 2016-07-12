/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsclient.view.ui.dataui;

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.wpsclient.WpsClientImpl;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.sif.JPanelListRenderer;
import org.orbisgis.wpsservice.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * DataUI implementation for DataStore.
 * This class generate an interactive UI dedicated to the configuration of a DataStore.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreUI implements DataUI{

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_STORE_PROPERTY = "DATA_STORE_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String IS_OUTPUT_PROPERTY = "IS_OUTPUT_PROPERTY";

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.DATA_STORE);
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        //Main panel which contains all the UI
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        DataStore dataStore;
        boolean isOptional = false;
        /** Retrieve the DataStore from the DescriptionType. **/
        if(inputOrOutput instanceof InputDescriptionType){
            InputDescriptionType input = (InputDescriptionType)inputOrOutput;
            dataStore = (DataStore)input.getDataDescription().getValue();
            //As an input, the DataStore can be optional.
            if(input.getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else {
            //If inputOrOutput is not a input and not an output, exit
            return null;
        }

        /**Instantiate the geocatalog optionPanel. **/
        //Instantiate the comboBox containing the table list
        JComboBox<ContainerItem<Object>> geocatalogComboBox = new JComboBox<>();
        JPanel geocatalogComponent = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        //If the DataStore is an input, uses a custom comboBox renderer to show an icon, the table name, the SRID ...
        geocatalogComboBox.setRenderer(new JPanelListRenderer());
        //Populate the comboBox with the available tables.
        boolean isSpatial = false;
        if(dataStore.getDataStoreTypeList() != null){
            isSpatial = dataStore.getDataStoreTypeList().contains(DataType.GEOMETRY);
        }
        populateWithTable(geocatalogComboBox, isSpatial, false);
        //Adds the listener on combo box item selection
        geocatalogComboBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onGeocatalogTableSelected", "source"));
        //Adds the listener to refresh the table list.
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        geocatalogComboBox.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        geocatalogComboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        geocatalogComboBox.putClientProperty(DATA_STORE_PROPERTY, dataStore);
        geocatalogComboBox.putClientProperty(IS_OUTPUT_PROPERTY, false);
        geocatalogComboBox.setBackground(Color.WHITE);
        geocatalogComboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
        geocatalogComponent.add(geocatalogComboBox, "span, grow");
        //Adds the optional value
        if(isOptional){
            geocatalogComboBox.add(new JPanel());
        }
        //Register the geocatalog combo box as a property in the DataStore type box
        if(geocatalogComboBox.getItemCount() > 0) {
            geocatalogComboBox.setSelectedIndex(0);
        }
        panel.add(geocatalogComboBox, "grow");
        return panel;
    }

    /**
     * Populate the given comboBox with the table map get from the LocalWpsService (table name as key, if it is
     * spatial or not as value).
     * Once populated, the combo box will display an icon regarding if the table is spatial or not and the table name.
     * @param geocatalogComboBox The combo box to populate.
     * @param isSpatialDataStore True if the DataStore is spatial, false otherwise.
     * @param isOutput True if the DataStore is an output, false otherwise.
     */
    private void populateWithTable(JComboBox<ContainerItem<Object>> geocatalogComboBox, boolean isSpatialDataStore,
                                   boolean isOutput){
        //Retrieve the table map
        Map<String, Boolean> tableMap;
        if(isSpatialDataStore) {
            tableMap = wpsClient.getLocalWpsService().getGeocatalogTableList(true);
        }
        else {
            tableMap = wpsClient.getLocalWpsService().getGeocatalogTableList(false);
        }
        //If there is tables, build all the ContainerItem containing the JPanel representing a table
        ContainerItem<Object> selectedItem = (ContainerItem<Object>)geocatalogComboBox.getSelectedItem();
        geocatalogComboBox.removeAllItems();
        List<ContainerItem<Object>> containerItemList = new ArrayList<>();
        if(tableMap != null && !tableMap.isEmpty()){
            for (Map.Entry<String, Boolean> entry : tableMap.entrySet()) {
                JPanel tablePanel = new JPanel(new MigLayout("ins 0, gap 0"));
                //Sets the spatial icon regarding the entry value
                if (entry.getValue()) {
                    tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.GEO_FILE)));
                } else {
                    tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE)));
                }
                //Adds the table label contained in the entry key
                tablePanel.add(new JLabel(entry.getKey()));
                //Save the ContainerItem in the list
                containerItemList.add(new ContainerItem<Object>(tablePanel, entry.getKey()));
            }
            //Sort the ContainerItem by alphabetical order
            Collections.sort(containerItemList);
            //Adds all the ContainerItem to the comboBox
            for(ContainerItem<Object> containerItem : containerItemList){
                geocatalogComboBox.addItem(containerItem);
            }
            //If an item was selected, try to reselect it
            if(selectedItem != null) {
                for (int i = 0; i < geocatalogComboBox.getItemCount(); i++) {
                    if (geocatalogComboBox.getItemAt(i).getLabel().equals(selectedItem.getLabel())) {
                        geocatalogComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        //If it is an output, adds the newTable item
        if(isOutput){
            geocatalogComboBox.insertItemAt(new ContainerItem<Object>("New_table", "New_table"), 0);
            geocatalogComboBox.setSelectedIndex(0);
        }
    }

    /**
     * When the mouse enter in the JComboBox refreshes the table list.
     * If there is no sources listed in the JComboBox, shows a tooltip text to the user.
     * @param source Source JComboBox
     */
    public void onComboBoxEntered(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox)source;
        //Refreshes the list of tables displayed
        DataStore dataStore = (DataStore)comboBox.getClientProperty(DATA_STORE_PROPERTY);
        boolean isOptional = (boolean)comboBox.getClientProperty(IS_OUTPUT_PROPERTY);
        Object selectedItem = comboBox.getSelectedItem();
        boolean isSpatial = false;
        if(dataStore.getDataStoreTypeList() != null){
            isSpatial = dataStore.getDataStoreTypeList().contains(DataType.GEOMETRY);
        }
        populateWithTable(comboBox, isSpatial, isOptional);
        if(selectedItem != null){
            comboBox.setSelectedItem(selectedItem);
        }
        //if there is no table listed, shows a massage as a tooltip to the user
        if(comboBox.getItemCount() == 0) {
            comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
            comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(2500);
            comboBox.setToolTipText("First add a table to the Geocatalog");
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(comboBox,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
        }
    }

    /**
     * When the mouse leaves the JComboBox, reset the tooltip text and delay.
     * @param source JComboBox source.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<String>> comboBox = (JComboBox)source;
        Object tooltipText = comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY);
        if(tooltipText != null) {
            comboBox.setToolTipText((String)tooltipText);
        }
        Object delay = comboBox.getClientProperty(INITIAL_DELAY_PROPERTY);
        if(delay != null){
            ToolTipManager.sharedInstance().setInitialDelay((int)delay);
        }
    }

    /**
     * When a table is selected in the geocatalog field, empty the textField for a new table,
     * save the selected table and tell the child DataField that there is a modification.
     * @param source Source geocatalog JComboBox
     */
    public void onGeocatalogTableSelected(Object source){
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox<ContainerItem<Object>>) source;
        if(comboBox.getItemCount() == 0){
            return;
        }
        String tableName0 = comboBox.getItemAt(0).getLabel();
        //If the ComboBox is empty, don't do anything.
        //The process won't launch util the user sets the DataStore
        if(comboBox.getItemCount()>0 && tableName0.isEmpty()){
            return;
        }
        if(comboBox.getClientProperty(TEXT_FIELD_PROPERTY) != null){
            JTextField textField = (JTextField)comboBox.getClientProperty(TEXT_FIELD_PROPERTY);
            if(!textField.getText().isEmpty() && comboBox.getSelectedIndex() != comboBox.getItemCount()-1) {
                textField.setText("");
            }
            if(!textField.getText().isEmpty() && comboBox.getSelectedIndex() == comboBox.getItemCount()-1){
                return;
            }
        }
        //Retrieve the client properties
        Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty(DATA_MAP_PROPERTY);
        URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
        DataStore dataStore = (DataStore) comboBox.getClientProperty(DATA_STORE_PROPERTY);
        String tableName;
        if(comboBox.getSelectedItem() instanceof ContainerItem) {
            tableName = ((ContainerItem) comboBox.getSelectedItem()).getLabel();
        }
        else{
            tableName = comboBox.getSelectedItem().toString();
        }
        //Tells all the dataField linked that the data source is loaded
        if(dataStore.getListDataField() != null) {
            for (DataField dataField : dataStore.getListDataField()) {
                dataField.setSourceModified(true);
            }
        }
        dataMap.put(uri, tableName);
    }
}
