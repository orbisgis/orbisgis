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

package org.orbisgis.toolboxeditor.dataui;

import net.miginfocom.swing.MigLayout;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.renderers.JPanelListRenderer;
import org.orbisgis.toolboxeditor.ToolboxWpsClient;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.DataType;
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
import org.orbiswps.server.model.JDBCColumn;
import org.orbiswps.server.model.JDBCValue;

/**
 * DataUI implementation for JDBCColumn.
 * This class generate an interactive UI dedicated to the configuration of a JDBCColumn.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 * @author Erwan Bocher
 **/

public class JDBCColumnUI implements DataUI {

    /** Size constants **/
    private static final int MAX_JLIST_ROW_COUNT_VERTICAL = 5;
    private static final int MAX_JLIST_ROW_COUNT_HORIZONTAL = 3;
    private static final int MIN_JLIST_ROW_COUNT = 1;

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_COLUMN_PROPERTY = "DATA_COLUMN_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String DEFAULT_ITEM_PROPERTY = "DEFAULT_ITEM_PROPERTY";
    private static final String COLUMN_TITLE_PROPERTY = "COLUMN_TITLE_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String NULL_ITEM = "NULL_ITEM";
    private static final String MAX_JLIST_ROW_COUNT = "MAX_JLIST_ROW_COUNT";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(JDBCColumnUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        JDBCColumn jdbcColumn = null;
        boolean isOptional = false;
        //Retrieve the JDBCTable
        //If it is an input, find if it is optional.
        if(inputOrOutput instanceof InputDescriptionType){
            jdbcColumn = (JDBCColumn)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            return null;
        }

        if(jdbcColumn == null){
            return panel;
        }
        if(jdbcColumn.isMultiSelection()){
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
            list.putClientProperty(DATA_COLUMN_PROPERTY, jdbcColumn);
            list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            list.putClientProperty(COLUMN_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
            list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onItemSelected", "source"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
            list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
            list.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

            if(!isOptional && dataMap.containsKey(uri)) {
                Object obj = dataMap.get(uri);
                if(obj instanceof String[]){
                    String[] columns = (String[]) obj;
                    int[] indexes = new int[columns.length];
                    int i=0;
                    for(String column : columns){
                        model.add(0, new ContainerItem<Object>(column, column));
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
            jdbcColumn.setSourceModified(true);
        }
        else {
            //ComboBox the column list
            JComboBox<ContainerItem<Object>> comboBox = new JComboBox<>();
            comboBox.setRenderer(new JPanelListRenderer());
            comboBox.setBackground(Color.WHITE);
            ContainerItem<Object> defaultItem = new ContainerItem<Object>(I18N.tr("Select a column"), I18N.tr("Select a column"));
            URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
            comboBox.putClientProperty(URI_PROPERTY, uri);
            comboBox.putClientProperty(DATA_COLUMN_PROPERTY, jdbcColumn);
            comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
            comboBox.putClientProperty(DEFAULT_ITEM_PROPERTY, defaultItem);
            comboBox.putClientProperty(COLUMN_TITLE_PROPERTY, URI.create(inputOrOutput.getTitle().get(0).getValue().replaceAll("[^a-zA-Z0-9_]", "_")));
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
        JDBCColumn jdbcColumn = null;
        if(inputOrOutput instanceof InputDescriptionType){
            jdbcColumn = (JDBCColumn)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            jdbcColumn = (JDBCColumn)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(jdbcColumn.getDefaultValues() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), jdbcColumn.getDefaultValues());
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.JDBC_COLUMN);
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
            JDBCColumn jdbcColumn = (JDBCColumn) comboBox.getClientProperty(DATA_COLUMN_PROPERTY);
            HashMap<URI, Object> dataMap = (HashMap) comboBox.getClientProperty(DATA_MAP_PROPERTY);
            URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
            boolean isOptional = (boolean) comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
            ContainerItem<Object> defaultItem = (ContainerItem<Object>)comboBox.getClientProperty(DEFAULT_ITEM_PROPERTY);
            //If the JDBCTable related to the JDBCColumn has been modified, reload the jdbcColumn values
            if (jdbcColumn.isSourceModified() || (comboBox.getSelectedItem() != null && comboBox.getSelectedItem().equals(defaultItem))) {
                Object obj = dataMap.get(uri);
                comboBox.removeItem(defaultItem);
                jdbcColumn.setSourceModified(false);
                comboBox.removeAllItems();
                List<ContainerItem<Object>> listContainer = populateWithColumns(jdbcColumn, dataMap);
                for(ContainerItem<Object> container : listContainer){
                    comboBox.addItem(container);
                }
                if(isOptional){
                    comboBox.addItem(new ContainerItem<Object>(NULL_ITEM, NULL_ITEM));
                }
                //Try to select the good column
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
                    String title = comboBox.getClientProperty(COLUMN_TITLE_PROPERTY).toString().toUpperCase();
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
                String jdbcTableColumnStr = jdbcColumn.getJDBCTableIdentifier().toString();
                comboBox.setToolTipText(I18N.tr("First configure the JDBCTable {0}.",
                        jdbcTableColumnStr.substring(jdbcTableColumnStr.lastIndexOf(":") + 1)));
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(comboBox, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }

            comboBox.revalidate();
        }
        else if(source instanceof JList){
            JList<ContainerItem<Object>> list = (JList) source;
            JDBCColumn jdbcColumn = (JDBCColumn) list.getClientProperty(DATA_COLUMN_PROPERTY);
            int maxRow = (int) list.getClientProperty(MAX_JLIST_ROW_COUNT);
            URI uri = (URI) list.getClientProperty(URI_PROPERTY);
            Map<URI, Object> dataMap = (Map) list.getClientProperty(DATA_MAP_PROPERTY);
            DefaultListModel<ContainerItem<Object>> model = (DefaultListModel<ContainerItem<Object>>)list.getModel();
            //If the JDBCTable related to the JDBCColumn has been modified, reload the jdbcColumn values
            if (jdbcColumn.isSourceModified()) {
                jdbcColumn.setSourceModified(false);
                model.removeAllElements();
                if (dataMap.get(jdbcColumn.getJDBCTableIdentifier()) != null) {
                    List<ContainerItem<Object>> listContainer = populateWithColumns(jdbcColumn, dataMap);
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
                String jdbcTableColumnStr = jdbcColumn.getJDBCTableIdentifier().toString();
                if(jdbcTableColumnStr.contains("$")){
                    String[] split = jdbcTableColumnStr.split("\\$");
                    if(split.length == 2){
                        jdbcTableColumnStr = split[1];
                    }
                    else if(split.length == 3){
                        jdbcTableColumnStr = split[1]+"."+split[2];
                    }
                    list.setToolTipText(I18N.tr("First configure the JDBCColumn {0}", jdbcTableColumnStr));
                }
                else {
                    list.setToolTipText(I18N.tr("First configure the JDBCTable {0}",
                            jdbcTableColumnStr.substring(jdbcTableColumnStr.lastIndexOf(":") + 1)));
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
                JDBCColumn jdbcColumn = (JDBCColumn) comboBox.getClientProperty(DATA_COLUMN_PROPERTY);
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
                //Tells to the jdbcValues that the jdbcColumn has been modified
                if(jdbcColumn.getJDBCValueList() != null) {
                    for (JDBCValue jdbcValue : jdbcColumn.getJDBCValueList()) {
                        jdbcValue.setJDBCColumnModified(true);
                    }
                }
            }
        }
        else if(source instanceof JList){
            JList<ContainerItem<Object>> list = (JList<ContainerItem<Object>>)source;
            ListModel<ContainerItem<Object>> model = list.getModel();
            if(model.getSize()>0){
                String columnList = "";
                for(int i = 0; i<list.getSelectedIndices().length; i++){
                    if(columnList.isEmpty()){
                        columnList = model.getElementAt(i).getLabel();
                    }
                    else{
                        columnList += "\t"+model.getElementAt(i).getLabel();
                    }
                }
                Map<URI, Object> dataMap = (Map<URI, Object>) list.getClientProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) list.getClientProperty(URI_PROPERTY);
                boolean isOptional = (boolean)list.getClientProperty(IS_OPTIONAL_PROPERTY);
                dataMap.remove(uri);
                if (isOptional && columnList.isEmpty()) {
                    dataMap.put(uri, null);
                }
                else{
                    dataMap.put(uri, columnList);
                }
            }
        }
    }

    /**
     * Populate the given comboBox with the table columns name list.
     * Also display the columns information like if it is spatial or not, the SRID, the dimension ...
     */
    private List<ContainerItem<Object>> populateWithColumns(JDBCColumn jdbcColumn, Map<URI, Object> dataMap){
        //Retrieve the table name list
        List<ContainerItem<Object>> listContainer = new ArrayList<>();
        String tableName = null;
        if(jdbcColumn.getJDBCTableIdentifier().toString().contains("$")){
            String[] split = jdbcColumn.getJDBCTableIdentifier().toString().split("\\$");
            if(split.length == 3){
                tableName = split[1]+"."+split[2];
            }
            else if(split.length == 2){
                tableName = split[1];
            }
        }
        else if(dataMap.get(jdbcColumn.getJDBCTableIdentifier()) != null){
            tableName = dataMap.get(jdbcColumn.getJDBCTableIdentifier()).toString();
        }
        if(tableName == null){
            listContainer.add(new ContainerItem<Object>(I18N.tr("Select a column"), I18N.tr("Select a column")));
            return listContainer;
        }
        List<Map<ToolboxWpsClient.JdbcProperties, Object>> informationList = wpsClient.getColumnInformation(tableName);
        //If there is tables, retrieve their information to format the display in the comboBox
        if(informationList != null && !informationList.isEmpty()){
            for (Map<ToolboxWpsClient.JdbcProperties, Object> informationMap : informationList) {
                String columnName = (String)informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_NAME);
                boolean isColumnExcluded = false;
                if(jdbcColumn.getExcludedNameList() != null){
                    for(String excludedName : jdbcColumn.getExcludedNameList()){
                        if(excludedName.toLowerCase().equals(columnName.toLowerCase())){
                            isColumnExcluded = true;
                        }
                    }
                }
                if(!isColumnExcluded && jdbcColumn.getDataTypeList() != null){
                    String columnType = (String)informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_TYPE);
                    boolean isValid = false;
                    for(DataType dataType : jdbcColumn.getDataTypeList()){
                        if(DataType.testDBType(dataType, columnType)){
                            isValid = true;
                        }
                    }
                    if(!isValid){
                        isColumnExcluded = true;
                    }
                }
                if(!isColumnExcluded && jdbcColumn.getExcludedTypeList() != null){
                    String columnType = (String)informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_TYPE);
                    for(DataType excludedType : jdbcColumn.getExcludedTypeList()){
                        if(DataType.testDBType(excludedType, columnType)){
                            isColumnExcluded = true;
                        }
                    }
                }
                if(!isColumnExcluded){
                    //Retrieve the table information
                    //If there is information, use it to improve the table display in the comboBox
                    JPanel columnPanel = new JPanel(new MigLayout("ins 0, gap 0"));
                    if (!informationMap.isEmpty()) {
                        //Sets the spatial icon
                        String columnType = (String)informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_TYPE);
                        columnPanel.add(new JLabel(ToolBoxIcon.getIcon(columnType.toLowerCase())));
                        columnPanel.add(new JLabel(columnName));
                        //Sets the SRID label
                        int srid = (int) informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_SRID);
                        if (srid != 0) {
                            columnPanel.add(new JLabel(I18N.tr(" [EPSG:" + srid + "]")));
                        }
                        //Sets the dimension label
                        int dimension = (int) informationMap.get(ToolboxWpsClient.JdbcProperties.COLUMN_DIMENSION);
                        if (dimension != 2 && dimension != 0) {
                            columnPanel.add(new JLabel(I18N.tr(" "+dimension + "D")));
                        }
                    } else {
                        columnPanel.add(new JLabel(columnName));
                    }
                    listContainer.add(new ContainerItem<Object>(columnPanel, columnName));
                }
            }
        }
        return listContainer;
    }
}
