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
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.wpsclient.WpsClientImpl;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.sif.JPanelListRenderer;
import org.orbisgis.wpsservice.LocalWpsServer;
import org.orbisgis.wpsservice.model.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.math.BigInteger;
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

    /** Size constants **/
    private static final int MAX_JLIST_ROW_COUNT_VERTICAL = 5;
    private static final int MAX_JLIST_ROW_COUNT_HORIZONTAL = 3;
    private static final int MIN_JLIST_ROW_COUNT = 1;

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
    private static final String MAX_JLIST_ROW_COUNT = "MAX_JLIST_ROW_COUNT";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(DataFieldUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        DataField dataField = null;
        boolean isOptional = false;
        //Retrieve the DataField
        //If it is an input, find if it is optional.
        if(inputOrOutput instanceof InputDescriptionType){
            dataField = (DataField)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            return null;
        }

        if(dataField == null){
            return panel;
        }
        if(dataField.isMultiSelection()){
            JList<ContainerItem<Object>> list = new JList<>();
            list.setModel(new DefaultListModel<ContainerItem<Object>>());
            list.setCellRenderer(new JPanelListRenderer());
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setLayoutOrientation(JList.VERTICAL);
            if(orientation.equals(Orientation.VERTICAL)){
                list.putClientProperty(MAX_JLIST_ROW_COUNT, MAX_JLIST_ROW_COUNT_VERTICAL);
            }
            else{
                list.putClientProperty(MAX_JLIST_ROW_COUNT, MAX_JLIST_ROW_COUNT_HORIZONTAL);
            }
            list.setVisibleRowCount(MIN_JLIST_ROW_COUNT);
            list.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
            list.putClientProperty(DATA_FIELD_PROPERTY, dataField);
            list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            list.putClientProperty(FIELD_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
            list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onItemSelected", "source"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
            list.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
            JScrollPane listScroller = new JScrollPane(list);
            panel.add(listScroller, "growx, wrap");
        }
        else {
            //ComboBox the field list
            JComboBox<ContainerItem<Object>> comboBox = new JComboBox<>();
            comboBox.setRenderer(new JPanelListRenderer());
            comboBox.setBackground(Color.WHITE);
            ContainerItem<Object> defaultItem = new ContainerItem<Object>(I18N.tr("Select a field"), I18N.tr("Select a field"));
            comboBox.addItem(defaultItem);
            comboBox.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
            comboBox.putClientProperty(DATA_FIELD_PROPERTY, dataField);
            comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            comboBox.putClientProperty(DEFAULT_ITEM_PROPERTY, defaultItem);
            comboBox.putClientProperty(FIELD_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
            comboBox.addItemListener(EventHandler.create(ItemListener.class, this, "onItemSelected", "source"));
            comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
            comboBox.addPopupMenuListener(EventHandler.create(PopupMenuListener.class, this, "onComboBoxEntered", "source"));
            comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
            comboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
            panel.add(comboBox, "growx, wrap");

            if (isOptional) {
                comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
            }
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
        if(source instanceof JComboBox) {
            //Retrieve the client properties
            JComboBox<ContainerItem<Object>> comboBox = (JComboBox) source;
            if (comboBox.getItemCount() == 0) {
                comboBox.setToolTipText((String) comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY));
                ToolTipManager.sharedInstance().setInitialDelay((int) comboBox.getClientProperty(INITIAL_DELAY_PROPERTY));
            }
        }
        else if(source instanceof JList){
            //Retrieve the client properties
            JList<ContainerItem<Object>> list = (JList) source;
            DefaultListModel<ContainerItem<Object>> model = (DefaultListModel<ContainerItem<Object>>)list.getModel();
            if (model.getSize() == 0) {
                list.setToolTipText((String) list.getClientProperty(TOOLTIP_TEXT_PROPERTY));
                ToolTipManager.sharedInstance().setInitialDelay((int) list.getClientProperty(INITIAL_DELAY_PROPERTY));
            }
            list.revalidate();
            list.repaint();
        }
    }

    /**
     * When the mouse entered the JComboBox, update it according to his DataStore parent.
     * @param source The source JComboBox.
     */
    public void onComboBoxEntered(Object source){
        if(source instanceof JComboBox) {
            JComboBox<ContainerItem<Object>> comboBox = (JComboBox) source;
            DataField dataField = (DataField) comboBox.getClientProperty(DATA_FIELD_PROPERTY);
            HashMap<URI, Object> dataMap = (HashMap) comboBox.getClientProperty(DATA_MAP_PROPERTY);
            boolean isOptional = (boolean) comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
            ContainerItem<Object> defaultItem = (ContainerItem<Object>)comboBox.getClientProperty(DEFAULT_ITEM_PROPERTY);
            //If the DataStore related to the DataField has been modified, reload the dataField values
            if (dataField.isSourceModified() || comboBox.getSelectedItem().equals(defaultItem)) {
                comboBox.removeItem(defaultItem);
                dataField.setSourceModified(false);
                comboBox.removeAllItems();
                List<ContainerItem<Object>> listContainer = populateWithFields(dataField, dataMap);
                for(ContainerItem<Object> container : listContainer){
                    comboBox.addItem(container);
                }
                if(isOptional){
                    comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
                }
                //Try to select the good field
                String title = comboBox.getClientProperty(FIELD_TITLE_PROPERTY).toString().toUpperCase();
                for (int i = 0; i < comboBox.getItemCount(); i++) {
                    if (title.contains(comboBox.getItemAt(i).getLabel()) ||
                            comboBox.getItemAt(i).getLabel().contains(title)) {
                        comboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }

            //If the comboBox doesn't contains any values, it mean that the DataStore hasn't been well selected.
            //So show a tooltip text to warn the user.
            if (comboBox.getItemCount() == 0) {
                comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                String dataFieldStr = dataField.getDataStoreIdentifier().toString();
                comboBox.setToolTipText(I18N.tr("First configure the DataStore {0}.",
                        dataFieldStr.substring(dataFieldStr.lastIndexOf(":") + 1)));
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(comboBox, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }

            comboBox.revalidate();
        }
        else if(source instanceof JList){
            JList<ContainerItem<Object>> list = (JList) source;
            DataField dataField = (DataField) list.getClientProperty(DATA_FIELD_PROPERTY);
            int maxRow = (int) list.getClientProperty(MAX_JLIST_ROW_COUNT);
            HashMap<URI, Object> dataMap = (HashMap) list.getClientProperty(DATA_MAP_PROPERTY);
            DefaultListModel<ContainerItem<Object>> model = (DefaultListModel<ContainerItem<Object>>)list.getModel();
            //If the DataStore related to the DataField has been modified, reload the dataField values
            if (dataField.isSourceModified()) {
                dataField.setSourceModified(false);
                model.removeAllElements();
                if (dataMap.get(dataField.getDataStoreIdentifier()) != null) {
                    List<ContainerItem<Object>> listContainer = populateWithFields(dataField, dataMap);
                    for(ContainerItem<Object> container : listContainer){
                        model.addElement(container);
                    }
                }
            }

            //If the comboBox doesn't contains any values, it mean that the DataStore hasn't been well selected.
            //So show a tooltip text to warn the user.
            if (model.getSize() == 0) {
                list.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                list.putClientProperty(TOOLTIP_TEXT_PROPERTY, list.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                String dataFieldStr = dataField.getDataStoreIdentifier().toString();
                if(dataFieldStr.contains("$")){
                    String[] split = dataFieldStr.split("\\$");
                    if(split.length == 2){
                        dataFieldStr = split[1];
                    }
                    else if(split.length == 3){
                        dataFieldStr = split[1]+"."+split[2];
                    }
                    list.setToolTipText(I18N.tr("First configure the DataField {0}", dataFieldStr));
                }
                else {
                    list.setToolTipText(I18N.tr("First configure the DataStore {0}",
                            dataFieldStr.substring(dataFieldStr.lastIndexOf(":") + 1)));
                }
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(list, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }
            else{
                if(model.getSize()>maxRow){
                    list.setVisibleRowCount(maxRow);
                }
                else{
                    list.setVisibleRowCount(model.getSize());
                }
            }
            list.revalidate();
            list.repaint();
        }
    }

    /**
     * When an item of the comboBox is selected, save it in the data map
     * @param source The source JComboBox.
     */
    public void onItemSelected(Object source){
        if(source instanceof JComboBox){
            JComboBox<ContainerItem<Object>> comboBox = (JComboBox)source;
            if(comboBox.getSelectedItem() != null) {
                ContainerItem<Object> selectedItem = (ContainerItem<Object>)comboBox.getSelectedItem();
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
                if(dataField.getListFieldValue() != null) {
                    for (FieldValue fieldValue : dataField.getListFieldValue()) {
                        fieldValue.setDataFieldModified(true);
                    }
                }
            }
        }
        else if(source instanceof JList){
            JList<ContainerItem<Object>> list = (JList<ContainerItem<Object>>)source;
            ListModel<ContainerItem<Object>> model = list.getModel();
            if(model.getSize()>0){
                String fieldList = "";
                for(int i = 0; i<list.getSelectedIndices().length; i++){
                    if(fieldList.isEmpty()){
                        fieldList = model.getElementAt(i).getLabel();
                    }
                    else{
                        fieldList += "\t"+model.getElementAt(i).getLabel();
                    }
                }
                DataField dataField = (DataField) list.getClientProperty(DATA_FIELD_PROPERTY);
                Map<URI, Object> dataMap = (Map<URI, Object>) list.getClientProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) list.getClientProperty(URI_PROPERTY);
                boolean isOptional = (boolean)list.getClientProperty(IS_OPTIONAL_PROPERTY);
                dataMap.remove(uri);
                if (isOptional && fieldList.isEmpty()) {
                    dataMap.put(uri, null);
                }
                else{
                    dataMap.put(uri, fieldList);
                }
            }
        }
    }

    /**
     * Populate the given comboBox with the table fields name list.
     * Also display the fields information like if it is spatial or not, the SRID, the dimension ...
     */
    private List<ContainerItem<Object>> populateWithFields(DataField dataField, HashMap<URI, Object> dataMap){
        //Retrieve the table name list
        List<ContainerItem<Object>> listContainer = new ArrayList<>();
        String tableName = null;
        if(dataField.getDataStoreIdentifier().toString().contains("$")){
            String[] split = dataField.getDataStoreIdentifier().toString().split("\\$");
            if(split.length == 3){
                tableName = split[1]+"."+split[2];
            }
            else if(split.length == 2){
                tableName = split[1];
            }
        }
        else if(dataMap.get(dataField.getDataStoreIdentifier()) != null){
            tableName = dataMap.get(dataField.getDataStoreIdentifier()).toString();
        }
        if(tableName == null){
            listContainer.add(new ContainerItem<Object>(I18N.tr("Select a field"), I18N.tr("Select a field")));
            return listContainer;
        }
        List<String> fieldNameList = wpsClient.getLocalWpsService().getTableFieldList(tableName,
                dataField.getFieldTypeList(), dataField.getExcludedTypeList());
        //If there is tables, retrieve their information to format the display in the comboBox
        if(fieldNameList != null && !fieldNameList.isEmpty()){
            for (String fieldName : fieldNameList) {
                //Retrieve the table information
                Map<String, Object> informationMap =
                        wpsClient.getLocalWpsService().getFieldInformation(tableName, fieldName);
                //If there is information, use it to improve the table display in the comboBox
                JPanel fieldPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                if (!informationMap.isEmpty()) {
                    //Sets the spatial icon
                    String geometryType = (String)informationMap.get(LocalWpsServer.GEOMETRY_TYPE);
                    fieldPanel.add(new JLabel(ToolBoxIcon.getIcon(geometryType.toLowerCase())));
                    fieldPanel.add(new JLabel(fieldName));
                    //Sets the SRID label
                    int srid = (int) informationMap.get(LocalWpsServer.TABLE_SRID);
                    if (srid != 0) {
                        fieldPanel.add(new JLabel(I18N.tr(" [EPSG:" + srid + "]")));
                    }
                    //Sets the dimension label
                    int dimension = (int) informationMap.get(LocalWpsServer.TABLE_DIMENSION);
                    if (dimension != 2 && dimension != 0) {
                        fieldPanel.add(new JLabel(I18N.tr(" "+dimension + "D")));
                    }
                } else {
                    fieldPanel.add(new JLabel(fieldName));
                }
                listContainer.add(new ContainerItem<Object>(fieldPanel, fieldName));
            }
        }
        return listContainer;
    }
}
