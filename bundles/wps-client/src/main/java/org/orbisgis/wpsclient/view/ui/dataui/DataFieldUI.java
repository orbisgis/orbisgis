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
import org.h2gis.utilities.SFSUtilities;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.sif.JPanelComboBoxRenderer;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.*;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * DataUI implementation for DataField.
 * This class generate an interactive UI dedicated to the configuration of a DataField.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataFieldUI implements DataUI{


    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_FIELD_PROPERTY = "DATA_FIELD_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String DEFAULT_ITEM_PROPERTY = "DEFAULT_ITEM_PROPERTY";
    private static final String FIELD_TITLE_PROPERTY = "FIELD_TITLE_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String NULL_ITEM = "NULL_ITEM";

    /** WpsClient using the generated UI. */
    private WpsClient wpsClient;

    public void setWpsClient(WpsClient wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        DataField dataField = null;
        boolean isOptional = false;
        //Retrieve the DataField
        //If it is an input, find if it is optional.
        if(inputOrOutput instanceof Input){
            dataField = (DataField)((Input)inputOrOutput).getDataDescription();
            if(((Input)inputOrOutput).getMinOccurs() == 0){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof Output){
            dataField = (DataField)((Output)inputOrOutput).getDataDescription();
        }

        if(dataField == null){
            return panel;
        }
        //ComboBox the field list
        JComboBox<ContainerItem<Object>> comboBox = new JComboBox<>();
        comboBox.setRenderer(new JPanelComboBoxRenderer());
        comboBox.setBackground(Color.WHITE);
        ContainerItem<Object> defaultItem = new ContainerItem<Object>("Select a field", "Select a field");
        comboBox.addItem(defaultItem);
        comboBox.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        comboBox.putClientProperty(DATA_FIELD_PROPERTY, dataField);
        comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
        comboBox.putClientProperty(DEFAULT_ITEM_PROPERTY, defaultItem);
        comboBox.putClientProperty(FIELD_TITLE_PROPERTY, inputOrOutput.getTitle());
        comboBox.addItemListener(EventHandler.create(ItemListener.class, this, "onItemSelected", "source"));
        comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        comboBox.addPopupMenuListener(EventHandler.create(PopupMenuListener.class, this, "onComboBoxEntered", "source"));
        comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        comboBox.setToolTipText(inputOrOutput.getResume());
        panel.add(comboBox, "growx, wrap");

        if(isOptional){
            comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
        }

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.DATA_FIELD);
    }

    /**
     * When the mouse leaves the comboBox, reset the tooltipText delay.
     * @param source The source JComboBox.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox)source;
        if(comboBox.getItemCount() == 0) {
            comboBox.setToolTipText((String)comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY));
            ToolTipManager.sharedInstance().setInitialDelay((int)comboBox.getClientProperty(INITIAL_DELAY_PROPERTY));
        }
    }

    /**
     * When the mouse entered the JComboBox, update it according to his DataStore parent.
     * @param source The source JComboBox.
     */
    public void onComboBoxEntered(Object source){
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox)source;
        String defaultItem = comboBox.getClientProperty(DEFAULT_ITEM_PROPERTY).toString();
        comboBox.removeItem(defaultItem);
        DataField dataField = (DataField)comboBox.getClientProperty(DATA_FIELD_PROPERTY);
        HashMap<URI, Object> dataMap = (HashMap)comboBox.getClientProperty(DATA_MAP_PROPERTY);
        boolean isOptional = (boolean)comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
        //If the DataStore related to the DataField has been modified, reload the dataField values
        if(dataField.isSourceModified()) {
            dataField.setSourceModified(false);
            comboBox.removeAllItems();
            if (dataMap.get(dataField.getDataStoreIdentifier()) != null) {
                populateWithFields(comboBox, isOptional);
            }
        }

        //If the comboBox doesn't contains any values, it mean that the DataStore hasn't been well selected.
        //So show a tooltip text to warn the user.
        if(comboBox.getItemCount() == 0) {
            comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
            comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(2500);
            String dataFieldStr = dataField.getDataStoreIdentifier().toString();
            comboBox.setToolTipText("First configure the DataStore : " +
                    dataFieldStr.substring(dataFieldStr.lastIndexOf(":")+1));
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(comboBox,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
        }
        //Else try to select the good field
        else{
            String title = comboBox.getClientProperty(FIELD_TITLE_PROPERTY).toString().toUpperCase();
            for(int i = 0; i < comboBox.getItemCount(); i++) {
                if(title.contains(comboBox.getItemAt(i).getLabel()) ||
                        comboBox.getItemAt(i).getLabel().contains(title)){
                    comboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        comboBox.revalidate();
    }

    /**
     * When an item of the comboBox is selected, save it in the data map
     * @param source The source JComboBox.
     */
    public void onItemSelected(Object source){
        if(source instanceof JComboBox){
            JComboBox<ContainerItem<String>> comboBox = (JComboBox)source;
            if(comboBox.getSelectedItem() != null) {
                ContainerItem<String> selectedItem = (ContainerItem<String>)comboBox.getSelectedItem();
                DataField dataField = (DataField) comboBox.getClientProperty(DATA_FIELD_PROPERTY);
                Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
                boolean isOptional = (boolean)comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
                dataMap.remove(uri);
                if (isOptional && selectedItem.getLabel().isEmpty()) {
                    dataMap.put(uri, null);
                }
                else{
                    dataMap.put(uri, selectedItem.getLabel());
                }
                //Tells to the fieldValues that the datafield has been modified
                for (FieldValue fieldValue : dataField.getListFieldValue()) {
                    fieldValue.setDataFieldModified(true);
                }
            }
        }
    }

    /**
     * Populate the given comboBox with the table fields name list.
     * Also display the fields information like if it is spatial or not, the SRID, the dimension ...
     * @param comboBox The combo box to populate.
     * @param isOptional True if the DataSTore is spatial, false otherwise.
     */
    private void populateWithFields(JComboBox<ContainerItem<Object>> comboBox, boolean isOptional){
        //Retrieve the table name list
        DataField dataField = (DataField)comboBox.getClientProperty(DATA_FIELD_PROPERTY);
        HashMap<URI, Object> dataMap = (HashMap)comboBox.getClientProperty(DATA_MAP_PROPERTY);
        String tableName = ((URI) dataMap.get(dataField.getDataStoreIdentifier())).getFragment();
        List<String> fieldNameList = wpsClient.getWpsService().getTableFieldList(tableName,
                dataField.getFieldTypeList(), dataField.getExcludedTypeList());
        //If there is tables, retrieve their information to format the display in the comboBox
        if(fieldNameList != null && !fieldNameList.isEmpty()){
            for (String fieldName : fieldNameList) {
                //Retrieve the table information
                Map<String, Object> informationMap =
                        wpsClient.getWpsService().getFieldInformation(tableName, fieldName);
                //If there is information, use it to improve the table display in the comboBox
                JPanel fieldPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                if (!informationMap.isEmpty()) {
                    //Sets the spatial icon
                    String geometryType = (String)informationMap.get(LocalWpsService.GEOMETRY_TYPE);
                    fieldPanel.add(new JLabel(ToolBoxIcon.getIcon(geometryType.toLowerCase())));
                    fieldPanel.add(new JLabel(fieldName));
                    //Sets the SRID label
                    int srid = (int) informationMap.get(LocalWpsService.TABLE_SRID);
                    if (srid != 0) {
                        fieldPanel.add(new JLabel("[EPSG:" + srid + "]"));
                    }
                    //Sets the dimension label
                    int dimension = (int) informationMap.get(LocalWpsService.TABLE_DIMENSION);
                    if (dimension != 2 && dimension != 0) {
                        fieldPanel.add(new JLabel(dimension + "D"));
                    }
                } else {
                    fieldPanel.add(new JLabel(fieldName));
                }
                comboBox.addItem(new ContainerItem<Object>(fieldPanel, fieldName));
            }
        }
        if(isOptional){
            comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
        }
    }
}
