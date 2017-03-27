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

package org.orbisgis.wpsclient.impl.dataui;

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.renderers.JPanelListRenderer;
import org.orbisgis.wpsclient.impl.WpsClientImpl;
import org.orbisgis.wpsclient.api.dataui.DataUI;
import org.orbisgis.wpsclient.impl.utils.ToolBoxIcon;
import org.orbisgis.wpsserviceorbisgis.LocalWpsServer;
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
 * DataUI implementation for JDBCTableField.
 * This class generate an interactive UI dedicated to the configuration of a JDBCTableField.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class JDBCTableFieldUI implements DataUI {

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
    private static final I18n I18N = I18nFactory.getI18n(JDBCTableFieldUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        JDBCTableField jdbcTableField = null;
        boolean isOptional = false;
        //Retrieve the JDBCTable
        //If it is an input, find if it is optional.
        if(inputOrOutput instanceof InputDescriptionType){
            jdbcTableField = (JDBCTableField)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            return null;
        }

        if(jdbcTableField == null){
            return panel;
        }
        if(jdbcTableField.isMultiSelection()){
            JList<ContainerItem<Object>> list = new JList<>();
            DefaultListModel<ContainerItem<Object>> model = new DefaultListModel<>();
            list.setModel(model);
            list.setCellRenderer(new JPanelListRenderer());
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            list.setLayoutOrientation(JList.VERTICAL);
            int maxRow;
            if(orientation.equals(Orientation.VERTICAL)){
                maxRow = MAX_JLIST_ROW_COUNT_VERTICAL;
            }
            else{
                maxRow = MAX_JLIST_ROW_COUNT_HORIZONTAL;
            }
            list.putClientProperty(MAX_JLIST_ROW_COUNT, maxRow);
            list.setVisibleRowCount(MIN_JLIST_ROW_COUNT);
            URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
            list.putClientProperty(URI_PROPERTY, uri);
            list.putClientProperty(DATA_FIELD_PROPERTY, jdbcTableField);
            list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            list.putClientProperty(FIELD_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
            list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onItemSelected", "source"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
            list.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

            if(!isOptional && dataMap.containsKey(uri)) {
                Object obj = dataMap.get(uri);
                if(obj instanceof String[]){
                    String[] fields = (String[]) obj;
                    int[] indexes = new int[fields.length];
                    int i=0;
                    for(String field : fields){
                        model.add(0, new ContainerItem<Object>(field, field));
                        indexes[i] = i;
                        i++;
                    }
                    list.setSelectedIndices(indexes);
                }
                else {
                    model.add(0, new ContainerItem<>(dataMap.get(uri), dataMap.get(uri).toString()));
                    list.setSelectedIndex(0);
                }
                if(model.getSize()>maxRow){
                    list.setVisibleRowCount(maxRow);
                }
                else{
                    list.setVisibleRowCount(model.getSize());
                }
            }
            JScrollPane listScroller = new JScrollPane(list);
            panel.add(listScroller, "growx, wrap");
            jdbcTableField.setSourceModified(true);
        }
        else {
            //ComboBox the field list
            JComboBox<ContainerItem<Object>> comboBox = new JComboBox<>();
            comboBox.setRenderer(new JPanelListRenderer());
            comboBox.setBackground(Color.WHITE);
            ContainerItem<Object> defaultItem = new ContainerItem<Object>(I18N.tr("Select a field"), I18N.tr("Select a field"));
            URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
            comboBox.putClientProperty(URI_PROPERTY, uri);
            comboBox.putClientProperty(DATA_FIELD_PROPERTY, jdbcTableField);
            comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            comboBox.putClientProperty(DEFAULT_ITEM_PROPERTY, defaultItem);
            comboBox.putClientProperty(FIELD_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
            comboBox.addItemListener(EventHandler.create(ItemListener.class, this, "onItemSelected", "source"));
            comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
            comboBox.addPopupMenuListener(EventHandler.create(PopupMenuListener.class, this, "onComboBoxEntered", "source"));
            comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
            comboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
            if(!isOptional && dataMap.containsKey(uri)) {
                Object obj = dataMap.get(uri);
                if(obj instanceof String[]){
                    String str = ((String[])obj)[0];
                    ContainerItem<Object> item = new ContainerItem<Object>(str, str);
                    comboBox.addItem(item);
                    comboBox.setSelectedItem(item);
                }
                else {
                    ContainerItem<Object> item = new ContainerItem<>(dataMap.get(uri), dataMap.get(uri).toString());
                    comboBox.addItem(item);
                    comboBox.setSelectedItem(item);
                }
            }
            else{
                comboBox.addItem(defaultItem);
            }
            panel.add(comboBox, "growx, wrap");
        }

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        JDBCTableField jdbcTableField = null;
        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            jdbcTableField = (JDBCTableField)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            isOptional = ((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"));
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            jdbcTableField = (JDBCTableField)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(jdbcTableField.getDefaultValues() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), jdbcTableField.getDefaultValues());
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.JDBC_TABLE_FIELD);
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
     * When the mouse entered the JComboBox, update it according to his JDBCTable parent.
     * @param source The source JComboBox.
     */
    public void onComboBoxEntered(Object source){
        if(source instanceof JComboBox) {
            JComboBox<ContainerItem<Object>> comboBox = (JComboBox) source;
            JDBCTableField jdbcTableField = (JDBCTableField) comboBox.getClientProperty(DATA_FIELD_PROPERTY);
            HashMap<URI, Object> dataMap = (HashMap) comboBox.getClientProperty(DATA_MAP_PROPERTY);
            URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
            boolean isOptional = (boolean) comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
            ContainerItem<Object> defaultItem = (ContainerItem<Object>)comboBox.getClientProperty(DEFAULT_ITEM_PROPERTY);
            //If the JDBCTable related to the JDBCTableField has been modified, reload the jdbcTableField values
            if (jdbcTableField.isSourceModified() || (comboBox.getSelectedItem() != null && comboBox.getSelectedItem().equals(defaultItem))) {
                Object obj = dataMap.get(uri);
                comboBox.removeItem(defaultItem);
                jdbcTableField.setSourceModified(false);
                comboBox.removeAllItems();
                List<ContainerItem<Object>> listContainer = populateWithFields(jdbcTableField, dataMap);
                for(ContainerItem<Object> container : listContainer){
                    comboBox.addItem(container);
                }
                if(isOptional){
                    comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
                }
                //Try to select the good field
                boolean isSelection = false;
                if(obj != null && !obj.equals(defaultItem.getLabel())){
                    String str;
                    if(obj instanceof String[]){
                        str = ((String[])obj)[0];
                    }
                    else{
                        str = obj.toString();
                    }
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        if (str.toUpperCase().equals(comboBox.getItemAt(i).getLabel().toUpperCase()) ) {
                            comboBox.setSelectedIndex(i);
                            isSelection = true;
                            break;
                        }
                    }
                }
                if(!isSelection) {
                    String title = comboBox.getClientProperty(FIELD_TITLE_PROPERTY).toString().toUpperCase();
                    for (int i = 0; i < comboBox.getItemCount(); i++) {
                        if (title.contains(comboBox.getItemAt(i).getLabel()) ||
                                comboBox.getItemAt(i).getLabel().contains(title)) {
                            comboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }

            //If the comboBox doesn't contains any values, it mean that the JDBCTable hasn't been well selected.
            //So show a tooltip text to warn the user.
            if (comboBox.getItemCount() == 0) {
                comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                String jdbcTableFieldStr = jdbcTableField.getJDBCTableIdentifier().toString();
                comboBox.setToolTipText(I18N.tr("First configure the JDBCTable {0}.",
                        jdbcTableFieldStr.substring(jdbcTableFieldStr.lastIndexOf(":") + 1)));
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(comboBox, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }

            comboBox.revalidate();
        }
        else if(source instanceof JList){
            JList<ContainerItem<Object>> list = (JList) source;
            JDBCTableField jdbcTableField = (JDBCTableField) list.getClientProperty(DATA_FIELD_PROPERTY);
            int maxRow = (int) list.getClientProperty(MAX_JLIST_ROW_COUNT);
            URI uri = (URI) list.getClientProperty(URI_PROPERTY);
            Map<URI, Object> dataMap = (Map) list.getClientProperty(DATA_MAP_PROPERTY);
            DefaultListModel<ContainerItem<Object>> model = (DefaultListModel<ContainerItem<Object>>)list.getModel();
            //If the JDBCTable related to the JDBCTableField has been modified, reload the jdbcTableField values
            if (jdbcTableField.isSourceModified()) {
                jdbcTableField.setSourceModified(false);
                model.removeAllElements();
                if (dataMap.get(jdbcTableField.getJDBCTableIdentifier()) != null) {
                    List<ContainerItem<Object>> listContainer = populateWithFields(jdbcTableField, dataMap);
                    for(ContainerItem<Object> container : listContainer){
                        model.addElement(container);
                    }
                }
            }

            //If the comboBox doesn't contains any values, it mean that the JDBCTable hasn't been well selected.
            //So show a tooltip text to warn the user.
            if (model.getSize() == 0) {
                list.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                list.putClientProperty(TOOLTIP_TEXT_PROPERTY, list.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                String jdbcTableFieldStr = jdbcTableField.getJDBCTableIdentifier().toString();
                if(jdbcTableFieldStr.contains("$")){
                    String[] split = jdbcTableFieldStr.split("\\$");
                    if(split.length == 2){
                        jdbcTableFieldStr = split[1];
                    }
                    else if(split.length == 3){
                        jdbcTableFieldStr = split[1]+"."+split[2];
                    }
                    list.setToolTipText(I18N.tr("First configure the JDBCTableField {0}", jdbcTableFieldStr));
                }
                else {
                    list.setToolTipText(I18N.tr("First configure the JDBCTable {0}",
                            jdbcTableFieldStr.substring(jdbcTableFieldStr.lastIndexOf(":") + 1)));
                }
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(list, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }
            else{
                if(dataMap.containsKey(uri) && dataMap.get(uri) != null){
                    List<Integer> indexList = new ArrayList<>();
                    String[] elements = dataMap.get(uri).toString().split("\\t");
                    for (int i = 0; i < Math.min(model.getSize(), elements.length); i++) {
                        if (model.getElementAt(i).getLabel().toUpperCase().equals(elements[i].toUpperCase())) {
                            indexList.add(i);
                        }
                    }
                    int[] indexes = new int[indexList.size()];
                    for(int i=0; i<indexList.size(); i++){
                        indexes[i] = indexList.get(i);
                    }
                    list.setSelectedIndices(indexes);
                }
            }
            if(model.getSize()>maxRow){
                list.setVisibleRowCount(maxRow);
            }
            else{
                list.setVisibleRowCount(model.getSize());
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
                Object defaultItem = comboBox.getClientProperty(DEFAULT_ITEM_PROPERTY);
                JDBCTableField jdbcTableField = (JDBCTableField) comboBox.getClientProperty(DATA_FIELD_PROPERTY);
                Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
                boolean isOptional = (boolean)comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
                dataMap.remove(uri);
                if ((isOptional && selectedItem.getLabel().isEmpty()) || selectedItem.equals(defaultItem)) {
                    dataMap.put(uri, null);
                }
                else{
                    dataMap.put(uri, selectedItem.getLabel());
                }
                //Tells to the jdbcTableFieldValues that the jdbcTableField has been modified
                if(jdbcTableField.getJDBCTableFieldValueList() != null) {
                    for (JDBCTableFieldValue jdbcTableFieldValue : jdbcTableField.getJDBCTableFieldValueList()) {
                        jdbcTableFieldValue.setJDBCTableFieldModified(true);
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
                JDBCTableField jdbcTableField = (JDBCTableField) list.getClientProperty(DATA_FIELD_PROPERTY);
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
    private List<ContainerItem<Object>> populateWithFields(JDBCTableField jdbcTableField, Map<URI, Object> dataMap){
        //Retrieve the table name list
        List<ContainerItem<Object>> listContainer = new ArrayList<>();
        String tableName = null;
        if(jdbcTableField.getJDBCTableIdentifier().toString().contains("$")){
            String[] split = jdbcTableField.getJDBCTableIdentifier().toString().split("\\$");
            if(split.length == 3){
                tableName = split[1]+"."+split[2];
            }
            else if(split.length == 2){
                tableName = split[1];
            }
        }
        else if(dataMap.get(jdbcTableField.getJDBCTableIdentifier()) != null){
            tableName = dataMap.get(jdbcTableField.getJDBCTableIdentifier()).toString();
        }
        if(tableName == null){
            listContainer.add(new ContainerItem<Object>(I18N.tr("Select a field"), I18N.tr("Select a field")));
            return listContainer;
        }
        List<String> fieldNameList = wpsClient.getTableFieldList(tableName,
                jdbcTableField.getDataTypeList(), jdbcTableField.getExcludedTypeList());
        //If there is tables, retrieve their information to format the display in the comboBox
        if(fieldNameList != null && !fieldNameList.isEmpty()){
            for (String fieldName : fieldNameList) {
                boolean isNameExcluded = false;
                if(jdbcTableField.getExcludedNameList() != null){
                    for(String excludedName : jdbcTableField.getExcludedNameList()){
                        if(excludedName.toLowerCase().equals(fieldName.toLowerCase())){
                            isNameExcluded = true;
                        }
                    }
                }
                if(!isNameExcluded){
                    //Retrieve the table information
                    Map<String, Object> informationMap =
                            wpsClient.getFieldInformation(tableName, fieldName);
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
        }
        return listContainer;
    }
}
