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
import org.apache.commons.io.FilenameUtils;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsclient.view.utils.sif.JPanelListRenderer;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DataUI implementation for DataStore.
 * This class generate an interactive UI dedicated to the configuration of a DataStore.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreUI implements DataUI{

    /** Constant used to pass object as client property throw JComponents **/
    private static final String GEOCATALOG = "GEOCATALOG";
    private static final String FILE = "FILE";
    private static final String NONE = "NONE";
    private static final String COMPONENT_PROPERTY = "COMPONENT_PROPERTY";
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_STORE_PROPERTY = "DATA_STORE_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String GEOCATALOG_COMPONENT_PROPERTY = "GEOCATALOG_COMPONENT_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String FILE_COMPONENT_PROPERTY = "FILE_COMPONENT_PROPERTY";
    private static final String KEEP_SOURCE_PROPERTY = "KEEP_SOURCE_PROPERTY";
    private static final String LOAD_SOURCE_PROPERTY = "LOAD_SOURCE_PROPERTY";
    private static final String FILE_OPTIONS_PROPERTY = "FILE_OPTIONS_PROPERTY";
    private static final String DESCRIPTION_TYPE_PROPERTY = "DESCRIPTION_TYPE_PROPERTY";
    private static final String GEOCATALOG_COMBO_BOX_PROPERTY = "GEOCATALOG_COMBO_BOX_PROPERTY";
    private static final String FILE_PANEL_PROPERTY = "FILE_PANEL_PROPERTY";
    private static final String IS_OUTPUT_PROPERTY = "IS_OUTPUT_PROPERTY";
    private static final String POPUP_MENU_PROPERTY = "POPUP_MENU_PROPERTY";

    /** WpsClient using the generated UI. */
    private WpsClient wpsClient;

    @Override
    public void setWpsClient(WpsClient wpsClient){
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
        if(inputOrOutput instanceof Input){
            Input input = (Input)inputOrOutput;
            dataStore = (DataStore)input.getDataDescription();
            //As an input, the DataStore can be optional.
            if(input.getMinOccurs() == 0){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof Output){
            Output output = (Output)inputOrOutput;
            dataStore = (DataStore)output.getDataDescription();
        }
        else {
            //If inputOrOutput is not a input and not an output, exit
            return panel;
        }

        /** Build the ComboBox which contains all the DataStore available types. **/
        //The combo box will contains ContainerItems linking a JPanel with a table name
        JComboBox<ContainerItem<Object>> dataStoreTypeBox = new JComboBox<>();
        //This custom renderer will display in the comboBox a JLabel containing the type icon and the type name.
        dataStoreTypeBox.setRenderer(new JPanelListRenderer());
        //Adds all the available type to the comboBox
        if(dataStore.isGeocatalog()) {
            dataStoreTypeBox.addItem(new ContainerItem<Object>(
                    new JLabel("Geocatalog", ToolBoxIcon.getIcon(ToolBoxIcon.GEOCATALOG), SwingConstants.LEFT),
                    GEOCATALOG));
        }
        if(dataStore.isFile()) {
            dataStoreTypeBox.addItem(new ContainerItem<Object>(
                    new JLabel("File", ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE), SwingConstants.LEFT),
                    FILE));
        }
        if(isOptional) {
            dataStoreTypeBox.addItem(new ContainerItem<Object>(
                    new JLabel(""), NONE));
        }
        //Adds all the properties used on the type selection
        dataStoreTypeBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        dataStoreTypeBox.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        //Panel that will contain the JComponent belonging to the selected type (geocatalog, file ...)
        JComponent component  = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        dataStoreTypeBox.putClientProperty(COMPONENT_PROPERTY, component);
        //Adds the item selection listener to the comboBox
        dataStoreTypeBox.addItemListener(
                EventHandler.create(ItemListener.class, this, "onDataStoreTypeSelected", "source"));
        //Adds the comboBox to the main panel
        panel.add(dataStoreTypeBox, "dock west");
        //Add the component panel to the main panel
        panel.add(component, "grow");

        /**Instantiate the geocatalog optionPanel. **/
        //Instantiate the comboBox containing the table list
        JComboBox<ContainerItem<Object>> geocatalogComboBox = new JComboBox<>();
        JPanel geocatalogComponent = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        //If the DataStore is an input, uses a custom comboBox renderer to show an icon, the table name, the SRID ...
        if(inputOrOutput instanceof Input) {
            geocatalogComboBox.setRenderer(new JPanelListRenderer());
        }
        //If it is an output, just show the table names, and add the 'new table' item.
        else {
            geocatalogComboBox.insertItemAt(new ContainerItem<Object>("New_table", "New_table"), 0);
            geocatalogComboBox.setEditable(true);
            //Adds the listener for the comboBox edition
            Document doc = ((JTextComponent)geocatalogComboBox.getEditor().getEditorComponent()).getDocument();
            doc.putProperty(GEOCATALOG_COMBO_BOX_PROPERTY, geocatalogComboBox);
            doc.putProperty(DATA_MAP_PROPERTY, dataMap);
            doc.putProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
            doc.addDocumentListener(EventHandler.create(DocumentListener.class, this, "onNewTable", "document"));
        }
        //Populate the comboBox with the available tables.
        populateWithTable(geocatalogComboBox, dataStore.isSpatial(), inputOrOutput instanceof Output);
        //Adds the listener on combo box item selection
        geocatalogComboBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onGeocatalogTableSelected", "source"));
        //Adds the listener to refresh the table list.
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        geocatalogComboBox.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        geocatalogComboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        geocatalogComboBox.putClientProperty(DATA_STORE_PROPERTY, dataStore);
        geocatalogComboBox.putClientProperty(IS_OUTPUT_PROPERTY, inputOrOutput instanceof Output);
        geocatalogComboBox.setBackground(Color.WHITE);
        geocatalogComboBox.setToolTipText(inputOrOutput.getResume());
        geocatalogComponent.add(geocatalogComboBox, "span, grow");
        //Register the geocatalog combo box as a property in the DataStore type box
        dataStoreTypeBox.putClientProperty(GEOCATALOG_COMPONENT_PROPERTY, geocatalogComponent);
        if(geocatalogComboBox.getItemCount() > 0) {
            geocatalogComboBox.setSelectedIndex(0);
        }

        /**Instantiate the file optionPanel. **/
        //Panel containing the path text field, the browse button and the option icon
        JPanel optionPanelFile = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        //JTextField containing the file path.
        JTextField textField = new JTextField();
        textField.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        textField.getDocument().putProperty(DATA_STORE_PROPERTY, dataStore);
        textField.getDocument().putProperty(DESCRIPTION_TYPE_PROPERTY, inputOrOutput);
        //Listen the text field modification
        textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "onDocumentSet",
                "document"));
        textField.setToolTipText(inputOrOutput.getResume());

        optionPanelFile.add(textField, "span, grow");
        JPanel buttonPanel = new JPanel(new MigLayout("ins 0, gap 0"));
        //Sets the browse button look and feel
        JButton browseButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.BROWSE));
        browseButton.setBorderPainted(false);
        browseButton.setContentAreaFilled(false);
        browseButton.setMargin(new Insets(0, 0, 0, 0));
        browseButton.addActionListener(EventHandler.create(ActionListener.class, this, "onBrowse", ""));
        browseButton.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        browseButton.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        browseButton.putClientProperty(TEXT_FIELD_PROPERTY, textField);
        browseButton.putClientProperty(DATA_STORE_PROPERTY, dataStore);
        //Instantiate the file panel (OpenFilePanel or SaveFilePanel)
        OpenFilePanel filePanel;
        //If it is an input, the file panel is an Open one
        if(inputOrOutput instanceof Input){
            filePanel = new OpenFilePanel("DataStoreUI.File."+inputOrOutput.getIdentifier(), "Select File");
            filePanel.setAcceptAllFileFilterUsed(false);
            //Adds the format filters to the file panel
            for(Format format : dataStore.getFormats()){
                String ext = FormatFactory.getFormatExtension(format);
                String description = "";
                for(Map.Entry<String, String> entry : wpsClient.getWpsService().getImportableFormat(false).entrySet()){
                    if(entry.getKey().equalsIgnoreCase(ext)){
                        description = entry.getValue();
                    }
                }
                filePanel.addFilter(ext, description);
            }
        }
        //If it is an output, the file panel is an Save one
        else {
            filePanel = new SaveFilePanel("DataStoreUI.File."+inputOrOutput.getIdentifier(), "Save File");
            //Adds the format filters to the file panel
            for(Format format : dataStore.getFormats()){
                String ext = FormatFactory.getFormatExtension(format);
                String description = "";
                for(Map.Entry<String, String> entry : wpsClient.getWpsService().getExportableFormat(false).entrySet()){
                    if(entry.getKey().equalsIgnoreCase(ext)){
                        description = entry.getValue();
                    }
                }
                filePanel.addFilter(ext, description);
            }
        }
        dataStoreTypeBox.putClientProperty(TEXT_FIELD_PROPERTY, textField);
        filePanel.loadState();
        textField.setText(filePanel.getCurrentDirectory().getAbsolutePath());
        browseButton.putClientProperty(FILE_PANEL_PROPERTY, filePanel);
        buttonPanel.add(browseButton);
        //Sets the DataStore file option
        JLabel fileOptions = new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.OPTIONS));
        //If the DataStore is an input adds the load option.
        if(inputOrOutput instanceof Input) {
            fileOptions.putClientProperty(KEEP_SOURCE_PROPERTY, false);
            if(wpsClient.getWpsService().isH2()) {
                fileOptions.putClientProperty(LOAD_SOURCE_PROPERTY, false);
            }
        }
        else{
            fileOptions.putClientProperty(KEEP_SOURCE_PROPERTY, false);
        }
        fileOptions.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onFileOptionEntered", "", "mouseEntered"));
        fileOptions.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        fileOptions.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        fileOptions.putClientProperty(POPUP_MENU_PROPERTY, buildPopupMenu(fileOptions));
        textField.getDocument().putProperty(FILE_OPTIONS_PROPERTY, fileOptions);
        buttonPanel.add(fileOptions);
        optionPanelFile.add(buttonPanel, "dock east");
        dataStoreTypeBox.putClientProperty(FILE_COMPONENT_PROPERTY, optionPanelFile);

        /** Return the UI panel. **/
        onDataStoreTypeSelected(dataStoreTypeBox);
        return panel;
    }

    /**
     * Populate the given comboBox with the table name list.
     * Also display the tables information like if it is spatial or not, the SRID, the dimension ...
     * @param geocatalogComboBox The combo box to populate.
     * @param isSpatialDataStore True if the DataSTore is spatial, false otherwise.
     */
    private void populateWithTable(JComboBox<ContainerItem<Object>> geocatalogComboBox, boolean isSpatialDataStore,
                                   boolean isOptional){
        //Retrieve the table name list
        List<String> tableNameList;
        if(isSpatialDataStore) {
            tableNameList = wpsClient.getWpsService().getGeocatalogTableList(true);
        }
        else {
            tableNameList = wpsClient.getWpsService().getGeocatalogTableList(false);
        }
        //If there is tables, retrieve their information to format the display in the comboBox
        if(tableNameList != null && !tableNameList.isEmpty()){
            ContainerItem<Object> selectedItem = (ContainerItem<Object>)geocatalogComboBox.getSelectedItem();
            geocatalogComboBox.removeAllItems();
            for (String tableName : tableNameList) {
                //Retrieve the table information
                Map<String, Object> informationMap = wpsClient.getWpsService().getTableInformation(tableName);
                //If there is information, use it to improve the table display in the comboBox
                JPanel tablePanel = new JPanel(new MigLayout("ins 0, gap 0"));
                if (!informationMap.isEmpty()) {
                    //Sets the spatial icon
                    boolean isSpatial = (boolean) informationMap.get(LocalWpsService.TABLE_IS_SPATIAL);
                    if (isSpatial) {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.GEO_FILE)));
                    } else {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE)));
                    }
                    tablePanel.add(new JLabel(tableName));
                } else {
                    tablePanel.add(new JLabel(tableName));
                }
                geocatalogComboBox.addItem(new ContainerItem<Object>(tablePanel, tableName));
            }
            if(selectedItem != null) {
                for (int i = 0; i < geocatalogComboBox.getItemCount(); i++) {
                    if (geocatalogComboBox.getItemAt(i).getLabel().equals(selectedItem.getLabel())) {
                        geocatalogComboBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
        if(isOptional){
            geocatalogComboBox.insertItemAt(new ContainerItem<Object>("New_table", "New_table"), 0);
            geocatalogComboBox.setSelectedIndex(0);
        }
    }

    /**
     * Action done when the file selection text field is modified.
     * If the written file path is valid, start the import worker to load the file.
     * @param document Document of the text field.
     */
    public void onDocumentSet(Document document){
        try {
            File file = new File(document.getText(0, document.getLength()));
            if(file.getName().isEmpty() || file.isDirectory()){
                return;
            }
            new ImportWorker(document);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
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
        populateWithTable(comboBox, dataStore.isSpatial(), isOptional);
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
        for (DataField dataField : dataStore.getListDataField()) {
            dataField.setSourceModified(true);
        }
        Object oldValue = dataMap.get(uri);
        if(oldValue != null && oldValue instanceof URI){
            URI oldUri = ((URI)oldValue);
            if(oldUri.getScheme().equals("file")){
                wpsClient.getWpsService().removeTempTable(oldUri.getFragment());
            }
        }
        dataMap.put(uri, URI.create("geocatalog:"+tableName+"#"+tableName));
    }

    /**
     * When a new table name is set, save the table name into the data map.
     * @param document JComboBox document containing the new table name.
     */
    public void onNewTable(Document document){
        try {
            JComboBox<String> comboBox = (JComboBox<String>)document.getProperty(GEOCATALOG_COMBO_BOX_PROPERTY);
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)document.getProperty(URI_PROPERTY);
            String text = document.getText(0, document.getLength());
            text = text.replaceAll(" ", "_");
            if(!text.isEmpty()){
                dataMap.put(uri, URI.create("geocatalog:"+text.toUpperCase()+"#"+text.toUpperCase()));
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStoreUI.class).error(e.getMessage());
        }
    }

    /**
     * When a type is selected in the DataStore comboBox, show the corresponding panel.
     * @param source JComboBox containing the DataStore types.
     */
    public void onDataStoreTypeSelected(Object source){
        JComboBox<ContainerItem> comboBox = (JComboBox<ContainerItem>)source;
        JPanel component = (JPanel) comboBox.getClientProperty(COMPONENT_PROPERTY);
        component.removeAll();
        ContainerItem<Object> container = (ContainerItem)comboBox.getSelectedItem();

        if(container.getLabel().equals(GEOCATALOG)) {
            JPanel optionPanel = (JPanel) comboBox.getClientProperty(GEOCATALOG_COMPONENT_PROPERTY);
            component.add(optionPanel, "grow");
            component.repaint();
        }
        else if(container.getLabel().equals(FILE)) {
            HashMap<URI, Object> dataMap = (HashMap<URI, Object>)comboBox.getClientProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)comboBox.getClientProperty(URI_PROPERTY);
            dataMap.put(uri, null);
            JPanel optionPanel = (JPanel) comboBox.getClientProperty(FILE_COMPONENT_PROPERTY);
            component.add(optionPanel, "growx");
            component.repaint();
        }
        component.revalidate();
    }

    /**
     * Opens an LoadPanel to permit to the user to select the file to load.
     * @param event Event thrown on clicking on the browse button
     */
    public void onBrowse(ActionEvent event){
        //Open the file browse window
        JButton source = (JButton)event.getSource();
        OpenFilePanel openFilePanel = (OpenFilePanel)source.getClientProperty(FILE_PANEL_PROPERTY);
        if (UIFactory.showDialog(openFilePanel, true, true)) {
            JTextField textField = (JTextField) source.getClientProperty(TEXT_FIELD_PROPERTY);
            textField.setText(openFilePanel.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document Document of the file text field.
     */
    public void saveDocumentTextFile(Document document){
        URI selectedFileURI;
        try {
            DataStore dataStore = (DataStore)document.getProperty(DATA_STORE_PROPERTY);
            JComponent fileOptions = (JComponent) document.getProperty(FILE_OPTIONS_PROPERTY);
            DescriptionType inputOrOutput = (DescriptionType)document.getProperty(DESCRIPTION_TYPE_PROPERTY);
            File file = new File(document.getText(0, document.getLength()));
            if(inputOrOutput instanceof Input) {
                //If the file doesn't exists, show an error
                if(!file.exists()){
                    LoggerFactory.getLogger(DataStore.class).error("The file '"+file+"' doesn't exists.");
                    return;
                }
                //Retrieve the file loading properties
                boolean loadSource = false;
                boolean keepSource = false;
                if(fileOptions != null){
                    if(fileOptions.getClientProperty(LOAD_SOURCE_PROPERTY) != null) {
                        loadSource = (boolean) fileOptions.getClientProperty(LOAD_SOURCE_PROPERTY);
                    }
                    else{
                        loadSource = false;
                    }
                    keepSource = (boolean)fileOptions.getClientProperty(KEEP_SOURCE_PROPERTY);
                }
                //Load the selected file an retrieve the table name.
                String tableName = wpsClient.loadURI(file.toURI(), loadSource, inputOrOutput);
                if (tableName != null) {
                    String fileStr = file.toURI().toString();
                    if(keepSource){
                        fileStr += "$";
                    }
                    //Saves the table name in the URI into the uri fragment
                    selectedFileURI = URI.create(fileStr + "#" + tableName);
                    //Store the selection
                    Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty(DATA_MAP_PROPERTY);
                    URI uri = inputOrOutput.getIdentifier();
                    Object oldValue = dataMap.get(uri);
                    //If a file was previoulsy loaded, remove it
                    if(oldValue != null && oldValue instanceof URI){
                        URI oldUri = ((URI)oldValue);
                        if(oldUri.getScheme().equals("file")){
                            wpsClient.getWpsService().removeTempTable(oldUri.getFragment());
                        }
                    }
                    dataMap.put(uri, selectedFileURI);
                    //tells the dataField they should revalidate
                    for (DataField dataField : dataStore.getListDataField()) {
                        dataField.setSourceModified(true);
                    }
                }
                else{
                    for (DataField dataField : dataStore.getListDataField()) {
                        dataField.setSourceModified(false);
                    }
                }
            }
            if(inputOrOutput instanceof Output){
                //Retrieve the table name
                String tableName = wpsClient.getDataManager().findUniqueTableName(
                        FilenameUtils.getBaseName(file.getName()));
                tableName = tableName.replace("\"", "");
                //If the source should be keep, add a $ char at the end of the file path
                boolean keepSource = false;
                if(fileOptions != null){
                    keepSource = (boolean)fileOptions.getClientProperty(KEEP_SOURCE_PROPERTY);
                }
                String fileStr = file.toURI().toString();
                if(keepSource){
                    fileStr += "$";
                }
                //Saves the table name in the URI into the uri fragment
                selectedFileURI = URI.create(fileStr + "#" + tableName);
                //Store the selection
                Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty(DATA_MAP_PROPERTY);
                URI uri = inputOrOutput.getIdentifier();
                dataMap.put(uri, selectedFileURI);
            }
        } catch (BadLocationException|SQLException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
        }
    }

    private JPopupMenu buildPopupMenu(JComponent source){
        JPopupMenu popupMenu = new JPopupMenu();
        //The load source property
        if(source.getClientProperty(LOAD_SOURCE_PROPERTY) != null) {
            boolean loadSource = (boolean) source.getClientProperty(LOAD_SOURCE_PROPERTY);
            JCheckBoxMenuItem loadItem = new JCheckBoxMenuItem("Linked table", null, !loadSource) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                        doClick();
                        setArmed(true);
                        ((JComponent)this.getClientProperty(FILE_OPTIONS_PROPERTY))
                                .putClientProperty(LOAD_SOURCE_PROPERTY, !this.getState());
                    } else {
                        super.processMouseEvent(evt);
                    }
                }
            };
            loadItem.putClientProperty(FILE_OPTIONS_PROPERTY, source);
            popupMenu.add(loadItem);
        }
        //The keep source property
        if(source.getClientProperty(KEEP_SOURCE_PROPERTY) != null) {
            boolean keepSource = (boolean) source.getClientProperty(KEEP_SOURCE_PROPERTY);
            JCheckBoxMenuItem keepItem = new JCheckBoxMenuItem("Keep file in base", null, keepSource) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                        doClick();
                        setArmed(true);
                        ((JComponent) this.getClientProperty(FILE_OPTIONS_PROPERTY))
                                .putClientProperty(KEEP_SOURCE_PROPERTY, this.getState());
                    } else {
                        super.processMouseEvent(evt);
                    }
                    //if the checkBox is selected, dynamically updates the data of the file
                    Map<URI, Object> dataMap = (Map<URI, Object>) this.getClientProperty(DATA_MAP_PROPERTY);
                    URI uri = (URI) this.getClientProperty(URI_PROPERTY);
                    if(dataMap.containsKey(uri) && dataMap.get(uri) != null) {
                        URI fileUri = (URI)dataMap.get(uri);
                        String tableName = fileUri.getFragment();
                        if (this.getState()) {
                            String fileStr = new File(fileUri.getSchemeSpecificPart()).toURI().toString();
                            if(!fileStr.contains("$")) {
                                fileStr += "$";
                            }
                            //Saves the table name in the URI into the uri fragment
                            fileUri = URI.create(fileStr + "#" + tableName);
                        }
                        else{
                            String fileStr = new File(fileUri.getSchemeSpecificPart()).toURI().toString();
                            fileStr = fileStr.replaceAll("\\$", "");
                            //Saves the table name in the URI into the uri fragment
                            fileUri = URI.create(fileStr + "#" + tableName);
                        }
                        //Store the selection
                        dataMap.put(uri, fileUri);
                    }
                }
            };
            keepItem.putClientProperty(FILE_OPTIONS_PROPERTY, source);
            keepItem.putClientProperty(DATA_MAP_PROPERTY, source.getClientProperty(DATA_MAP_PROPERTY));
            keepItem.putClientProperty(URI_PROPERTY, source.getClientProperty(URI_PROPERTY));
            popupMenu.add(keepItem);
        }
        return popupMenu;
    }

    /**
     * When the mouse enter in the file option icon, display a popup menu with the options.
     * @param me Mouse event
     */
    public void onFileOptionEntered(MouseEvent me){
        JComponent source = (JComponent)me.getSource();
        JPopupMenu popupMenu = (JPopupMenu)source.getClientProperty(POPUP_MENU_PROPERTY);
        //Show the popup
        popupMenu.show(source, me.getX(), me.getY());
    }

    /**
     * SwingWorker extension which will load the file contained in the given document.
     */
    public class ImportWorker extends SwingWorkerPM {

        private Document document;

        public ImportWorker(Document document){
            this.document = document;
            this.execute();
        }

        @Override
        protected Object doInBackground() throws Exception {
            saveDocumentTextFile(document);
            return null;
        }
    }
}
