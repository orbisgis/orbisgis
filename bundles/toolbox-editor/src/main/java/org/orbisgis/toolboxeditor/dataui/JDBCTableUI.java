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
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.renderers.JPanelListRenderer;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbisgis.toolboxeditor.utils.WaitLayerUI;
import org.orbiswps.server.model.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * DataUI implementation for JDBCTable.
 * This class generate an interactive UI dedicated to the configuration of a JDBCTable.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class JDBCTableUI implements DataUI {

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_STORE_PROPERTY = "DATA_STORE_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String IS_OUTPUT_PROPERTY = "IS_OUTPUT_PROPERTY";
    private static final String LAYERUI_PROPERTY = "LAYERUI_PROPERTY";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(JDBCTableUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        JDBCTable jdbcTable = null;
        if(inputOrOutput instanceof InputDescriptionType){
            jdbcTable = (JDBCTable)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            jdbcTable = (JDBCTable)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(jdbcTable.getDefaultValue() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), jdbcTable.getDefaultValue());
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.JDBC_TABLE);
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        //Main panel which contains all the UI
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        JDBCTable jdbcTable;
        boolean isOptional = false;
        /** Retrieve the JDBCTable from the DescriptionType. **/
        if(inputOrOutput instanceof InputDescriptionType){
            InputDescriptionType input = (InputDescriptionType)inputOrOutput;
            jdbcTable = (JDBCTable)input.getDataDescription().getValue();
            //As an input, the JDBCTable can be optional.
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
        JComboBox<ContainerItem<Object>> tableComboBox = new JComboBox<>();
        //If the JDBCTable is an input, uses a custom comboBox renderer to show an icon, the table name, the SRID ...
        tableComboBox.setRenderer(new JPanelListRenderer());
        //Populate the comboBox with the available tables.
        boolean isSpatial = false;
        if(jdbcTable.getDataTypeList() != null){
            for(DataType dataType : jdbcTable.getDataTypeList()) {
                if(DataType.isSpatialType(dataType)) {
                    isSpatial = true;
                }
            }
        }
        //Adds the listener on combo box item selection
        tableComboBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onGeocatalogTableSelected", "source"));
        //Adds the listener to refresh the table list.
        tableComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        tableComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
        tableComboBox.putClientProperty(URI_PROPERTY, uri);
        tableComboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        tableComboBox.putClientProperty(DATA_STORE_PROPERTY, jdbcTable);
        tableComboBox.putClientProperty(IS_OUTPUT_PROPERTY, false);
        tableComboBox.setBackground(Color.WHITE);
        tableComboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
        WaitLayerUI layerUI = new WaitLayerUI();
        JLayer<JComponent> layer = new JLayer<>(tableComboBox, layerUI);
        panel.add(layer, "grow");
        tableComboBox.putClientProperty(LAYERUI_PROPERTY, layerUI);
        populateWithTable(tableComboBox, jdbcTable.getDataTypeList(), jdbcTable.getExcludedTypeList(),
                false, isSpatial);
        //Adds the optional value
        if(isOptional){
            tableComboBox.add(new JPanel());
        }
        //Register the geocatalog combo box as a property in the JDBCTable type box
        if(!isOptional) {
            if (tableComboBox.getItemCount() > 0) {
                if (dataMap.containsKey(uri)) {
                    boolean isItemSelected = false;
                    for (int i = 0; i < tableComboBox.getItemCount(); i++) {
                        if (tableComboBox.getItemAt(i).getLabel().equals(dataMap.get(uri))) {
                            tableComboBox.setSelectedIndex(i);
                            isItemSelected = true;
                        }
                    }
                    if (!isItemSelected) {
                        tableComboBox.setSelectedIndex(0);
                    }
                }
                else{
                    tableComboBox.setSelectedIndex(0);
                }
                dataMap.put(uri, tableComboBox.getItemAt(tableComboBox.getSelectedIndex()).getLabel());
            }
        }
        return panel;
    }

    /**
     * Populate the given comboBox with the table map get from the LocalWpsService (table name as key, if it is
     * spatial or not as value).
     * Once populated, the combo box will display an icon regarding if the table is spatial or not and the table name.
     * @param geocatalogComboBox The combo box to populate.
     * @param dataTypes Type of field accepted. If empty, accepts all the field.
     * @param excludedTypes Type of field excluded.
     * @param isOutput True if the JDBCTable is an output, false otherwise.
     * @param isSpatial True if the wanted table have to be spatial, false otherwise.
     */
    private void populateWithTable(JComboBox<ContainerItem<Object>> geocatalogComboBox,
                                   List<DataType> dataTypes,
                                   List<DataType> excludedTypes,
                                   boolean isOutput,
                                   boolean isSpatial){
        TableWorker worker = new TableWorker(geocatalogComboBox, dataTypes, excludedTypes, isOutput, isSpatial);
        ExecutorService executorService = wpsClient.getExecutorService();
        if(executorService != null){
            executorService.execute(worker);
        }
        else{
            worker.execute();
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
        if(comboBox.getItemCount() == 0) {
            //Refreshes the list of tables displayed
            JDBCTable jdbcTable = (JDBCTable) comboBox.getClientProperty(DATA_STORE_PROPERTY);
            boolean isOptional = (boolean) comboBox.getClientProperty(IS_OUTPUT_PROPERTY);
            Object selectedItem = comboBox.getSelectedItem();
            boolean isSpatial = false;
            if (jdbcTable.getDataTypeList() != null) {
                for (DataType dataType : jdbcTable.getDataTypeList()) {
                    if (DataType.isSpatialType(dataType)) {
                        isSpatial = true;
                    }
                }
            }
            populateWithTable(comboBox, jdbcTable.getDataTypeList(), jdbcTable.getExcludedTypeList(), isOptional,
                    isSpatial);
            if (selectedItem != null) {
                comboBox.setSelectedItem(selectedItem);
            }
            //if there is no table listed, shows a massage as a tooltip to the user
            if (comboBox.getItemCount() == 0) {
                comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                comboBox.setToolTipText(I18N.tr("First add a table to the Geocatalog"));
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(comboBox, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, 0, 0, 0, false));
            }
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
     * save the selected table and tell the child JDBCTableField that there is a modification.
     * @param source Source geocatalog JComboBox
     */
    public void onGeocatalogTableSelected(Object source){
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox<ContainerItem<Object>>) source;
        if(comboBox.getItemCount() == 0){
            return;
        }
        String tableName0 = comboBox.getItemAt(0).getLabel();
        //If the ComboBox is empty, don't do anything.
        //The process won't launch util the user sets the JDBCTable
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
        JDBCTable jdbcTable = (JDBCTable) comboBox.getClientProperty(DATA_STORE_PROPERTY);
        String tableName;
        if(comboBox.getSelectedItem() instanceof ContainerItem) {
            tableName = ((ContainerItem) comboBox.getSelectedItem()).getLabel();
        }
        else{
            tableName = comboBox.getSelectedItem().toString();
        }
        //Tells all the JDBCColumn linked that the data source is loaded
        if(jdbcTable.getListJDBCColumn() != null) {
            for (JDBCColumn jdbcColumn : jdbcTable.getListJDBCColumn()) {
                jdbcColumn.setSourceModified(true);
            }
        }
        dataMap.put(uri, tableName);
    }

    /**
     * SwingWorker doing the table list update in a separated Swing Thread
     */
    private class TableWorker extends SwingWorkerPM {
        private JComboBox<ContainerItem<Object>> tableComboBox;
        private List<DataType> dataTypes;
        private List<DataType> excludedTypes;
        private boolean isOutput;
        private boolean isSpatial;

        public TableWorker(JComboBox<ContainerItem<Object>> tableComboBox,
                           List<DataType> dataTypes,
                           List<DataType> excludedTypes,
                           boolean isOutput,
                           boolean isSpatial){
            this.tableComboBox = tableComboBox;
            this.dataTypes = dataTypes;
            this.excludedTypes = excludedTypes;
            this.isOutput = isOutput;
            this.isSpatial = isSpatial;
        }

        @Override
        protected Object doInBackground() throws Exception {
            WaitLayerUI layerUI = (WaitLayerUI) tableComboBox.getClientProperty(LAYERUI_PROPERTY);
            layerUI.start();
            //Retrieve the table map
            List<String> tableList;
            if((dataTypes == null || dataTypes.isEmpty()) && isSpatial){
                dataTypes = new ArrayList<>();
                dataTypes.add(DataType.GEOMETRY);
            }
            tableList = wpsClient.getTableList(dataTypes, excludedTypes);
            //If there is tables, build all the ContainerItem containing the JPanel representing a table
            ContainerItem<Object> selectedItem = (ContainerItem<Object>) tableComboBox.getSelectedItem();
            tableComboBox.removeAllItems();
            List<ContainerItem<Object>> containerItemList = new ArrayList<>();
            if(tableList != null && !tableList.isEmpty()){
                for (String tableName : tableList) {
                    JPanel tablePanel = new JPanel(new MigLayout("ins 0, gap 0"));
                    //Sets the spatial icon regarding the entry value
                    if (isSpatial) {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.GEO_FILE)));
                    } else {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE)));
                    }
                    //Adds the table label contained in the entry key
                    tablePanel.add(new JLabel(tableName));
                    //Save the ContainerItem in the list
                    containerItemList.add(new ContainerItem<Object>(tablePanel, tableName));
                }
                //Sort the ContainerItem by alphabetical order
                Collections.sort(containerItemList);
                //Adds all the ContainerItem to the comboBox
                for(ContainerItem<Object> containerItem : containerItemList){
                    tableComboBox.addItem(containerItem);
                }
                //If an item was selected, try to reselect it
                if(selectedItem != null) {
                    for (int i = 0; i < tableComboBox.getItemCount(); i++) {
                        if (tableComboBox.getItemAt(i).getLabel().equals(selectedItem.getLabel())) {
                            tableComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            //If it is an output, adds the newTable item
            if(isOutput){
                tableComboBox.insertItemAt(new ContainerItem<Object>(I18N.tr("New_table"), I18N.tr("New_table")), 0);
                tableComboBox.setSelectedIndex(0);
            }
            tableComboBox.revalidate();
            tableComboBox.repaint();
            layerUI.stop();
            return null;
        }
    }
}
