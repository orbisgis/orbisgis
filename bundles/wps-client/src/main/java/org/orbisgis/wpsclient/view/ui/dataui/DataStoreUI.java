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
import org.orbisgis.wpsclient.view.utils.sif.JPanelComboBoxRenderer;
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
import java.util.Map;
import java.util.List;

/**
 * DataUI implementation for DataStore.
 * This class generate an interactive UI dedicated to the configuration of a DataStore.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreUI implements DataUI{

    /** constant size of the text fields **/
    private static final String GEOCATALOG = "GEOCATALOG";
    private static final String FILE = "FILE";
    private static final String NONE = "NONE";
    private static final String COMPONENT_PROPERTY = "COMPONENT_PROPERTY";
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_STORE_PROPERTY = "DATA_STORE_PROPERTY";
    private static final String DESCRIPTION_TYPE_PROPERTY = "DESCRIPTION_TYPE_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String GEOCATALOG_COMPONENT_PROPERTY = "GEOCATALOG_COMPONENT_PROPERTY";
    private static final String FILE_COMPONENT_PROPERTY = "FILE_COMPONENT_PROPERTY";

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
        JComboBox<ContainerItem<Object>> dataStoreTypeBox = new JComboBox<>();
        dataStoreTypeBox.setRenderer(new JPanelComboBoxRenderer());
        //Adds all the available type to the comboBox
        if(dataStore.isGeocatalog()) {
            dataStoreTypeBox.addItem(new ContainerItem<Object>(
                    new JLabel("Geocatalog", ToolBoxIcon.getIcon(ToolBoxIcon.GEOCATALOG), SwingConstants.LEFT), GEOCATALOG));
        }
        if(dataStore.isFile()) {
            dataStoreTypeBox.addItem(new ContainerItem<Object>(
                    new JLabel("File", ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE), SwingConstants.LEFT), FILE));
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
        //Adds the item selection listener
        dataStoreTypeBox.addItemListener(
                EventHandler.create(ItemListener.class, this, "onDataStoreTypeSelected", "source"));
        //Adds the comboBox to the main panel
        panel.add(dataStoreTypeBox, "dock west");
        //Add the component panel to the main panel
        panel.add(component, "grow");
        JComboBox<ContainerItem<Object>> geocatalogComboBox = new JComboBox<>();
        /**Instantiate the geocatalog optionPanel. **/
        JPanel geocatalogComponent = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        if(inputOrOutput instanceof Input) {
            geocatalogComboBox.setRenderer(new JPanelComboBoxRenderer());
        }
        else {
            String newTable = "New table";
            geocatalogComboBox.insertItemAt(new ContainerItem<Object>(newTable, newTable), 0);
            geocatalogComboBox.setSelectedIndex(0);
            geocatalogComboBox.setEditable(true);
            Document doc = ((JTextComponent)geocatalogComboBox.getEditor().getEditorComponent()).getDocument();
            doc.putProperty("comboBox", geocatalogComboBox);
            doc.addDocumentListener(EventHandler.create(DocumentListener.class, this, "onNewTable", "document"));
        }
        geocatalogComboBox.putClientProperty(DESCRIPTION_TYPE_PROPERTY, inputOrOutput);
        //Populate the comboBox with the available tables.
        populateWithTable(geocatalogComboBox, dataStore.isSpatial(), inputOrOutput);

        geocatalogComboBox.addActionListener(
                EventHandler.create(ActionListener.class, this, "onGeocatalogTableSelected", "source"));
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        geocatalogComboBox.addMouseListener(
                EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        geocatalogComboBox.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        geocatalogComboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        geocatalogComboBox.putClientProperty(DATA_STORE_PROPERTY, dataStore);
        geocatalogComboBox.setBackground(Color.WHITE);
        geocatalogComboBox.setToolTipText(inputOrOutput.getResume());
        JPanel tableSelection = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        tableSelection.add(geocatalogComboBox, "grow, span");
        geocatalogComponent.add(tableSelection, "span, grow");
        dataStoreTypeBox.putClientProperty(GEOCATALOG_COMPONENT_PROPERTY, geocatalogComponent);

        /**Instantiate the file optionPanel. **/
        JPanel optionPanelFile = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        JTextField textField = new JTextField();
        textField.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        textField.getDocument().putProperty(DESCRIPTION_TYPE_PROPERTY, inputOrOutput);
        textField.getDocument().putProperty(DATA_STORE_PROPERTY, dataStore);
        textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "onDocumentSet",
                "document"));
        textField.setToolTipText(inputOrOutput.getResume());

        optionPanelFile.add(textField, "span, grow");
        JPanel buttonPanel = new JPanel(new MigLayout("ins 0, gap 0"));
        JButton browseButton = new JButton(ToolBoxIcon.getIcon("browse"));
        browseButton.setBorderPainted(false);
        browseButton.setContentAreaFilled(false);
        browseButton.setMargin(new Insets(0, 0, 0, 0));
        browseButton.addActionListener(EventHandler.create(ActionListener.class, this, "onBrowse", ""));
        browseButton.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        browseButton.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        browseButton.putClientProperty(TEXT_FIELD_PROPERTY, textField);
        browseButton.putClientProperty(DATA_STORE_PROPERTY, dataStore);
        OpenFilePanel filePanel;
        //If it is an input, the file panel is an Open one
        if(inputOrOutput instanceof Input){
            filePanel = new OpenFilePanel("DataStoreUI.File."+inputOrOutput.getIdentifier(), "Select File");
            filePanel.setAcceptAllFileFilterUsed(false);
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
        browseButton.putClientProperty("filePanel", filePanel);
        buttonPanel.add(browseButton);
        if(inputOrOutput instanceof Input) {
            JLabel fileOptions = new JLabel(ToolBoxIcon.getIcon("options"));
            fileOptions.putClientProperty("keepSource", false);
            if(wpsClient.getWpsService().isH2()) {
                fileOptions.putClientProperty("loadSource", false);
            }
            fileOptions.addMouseListener(EventHandler.create(MouseListener.class, this, "onFileOption", ""));
            textField.getDocument().putProperty("fileOptions", fileOptions);
            buttonPanel.add(fileOptions);
        }

        optionPanelFile.add(buttonPanel, "dock east");
        dataStoreTypeBox.putClientProperty(FILE_COMPONENT_PROPERTY, optionPanelFile);

        /** Return the UI panel. **/
        onDataStoreTypeSelected(dataStoreTypeBox);
        return panel;
    }

    private void populateWithTable(JComboBox<ContainerItem<Object>> geocatalogComboBox, boolean isSpatialDataStore,
                                   DescriptionType inputOrOutput){
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
            for (String tableName : tableNameList) {
                //Retrieve the table information
                Map<String, Object> informationMap = wpsClient.getWpsService().getTableInformations(tableName);
                //If there is information, use it to improve the table display in the comboBox
                JPanel tablePanel = new JPanel(new MigLayout("ins 0, gap 0"));
                if (!informationMap.isEmpty()) {
                    boolean isSpatial = (boolean) informationMap.get(LocalWpsService.TABLE_IS_SPATIAL);
                    if (isSpatial) {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.GEO_FILE)));
                    } else {
                        tablePanel.add(new JLabel(ToolBoxIcon.getIcon(ToolBoxIcon.FLAT_FILE)));
                    }
                    tablePanel.add(new JLabel(tableName));
                    int srid = (int) informationMap.get(LocalWpsService.TABLE_SRID);
                    if (srid != 0) {
                        tablePanel.add(new JLabel("[EPSG:" + srid + "]"));
                    }
                    int dimension = (int) informationMap.get(LocalWpsService.TABLE_DIMENSION);
                    if (dimension != 2 && dimension != 0) {
                        tablePanel.add(new JLabel(dimension + "D"));
                    }
                } else {
                    tablePanel.add(new JLabel(tableName));
                }
                geocatalogComboBox.addItem(new ContainerItem<Object>(tablePanel, tableName));
            }
        }
    }

    public void onDocumentSet(Document document){
        ImportWorker importWorker = new ImportWorker();
        importWorker.setDocument(document);
    }

    /**
     * When the mouse enter in the JComboBox and their is no sources listed in the JComboBox,
     * it shows a tooltip text to the user.
     * @param source Source JComboBox
     */
    public void onComboBoxEntered(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox)source;
        if(comboBox.getItemCount() == 0) {
            comboBox.putClientProperty("initialDelay", ToolTipManager.sharedInstance().getInitialDelay());
            comboBox.putClientProperty("toolTipText", comboBox.getToolTipText());
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(2500);
            comboBox.setToolTipText("First add a table to the Geocatalog");
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(comboBox,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
        }
        DataStore dataStore = (DataStore)comboBox.getClientProperty(DATA_STORE_PROPERTY);
        DescriptionType inputOrOutput = (DescriptionType)comboBox.getClientProperty(DESCRIPTION_TYPE_PROPERTY);
        Object selectedItem = comboBox.getSelectedItem();
        populateWithTable(comboBox, dataStore.isSpatial(), inputOrOutput);
        if(selectedItem != null){
            comboBox.setSelectedItem(selectedItem);
        }
    }

    /**
     * When the mouse leaves the JComboBox, reset the tooltip text delay.
     * @param source JComboBox source.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<String>> comboBox = (JComboBox)source;
        Object tooltipText = comboBox.getClientProperty("toolTipText");
        if(tooltipText != null) {
            comboBox.setToolTipText((String)tooltipText);
        }
        Object delay = comboBox.getClientProperty("initialDelay");
        if(delay != null){
            ToolTipManager.sharedInstance().setInitialDelay((int)delay);
        }
    }

    /**
     * When a table is selected in the geocatalog field, empty the textField for a new table,
     * save the selectedtable,
     * tell the child DataField that there is a modification.
     * @param source Source geocatalog JComboBox
     */
    public void onGeocatalogTableSelected(Object source){
        JComboBox<ContainerItem<Object>> comboBox = (JComboBox<ContainerItem<Object>>) source;
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
        String tableName = ((ContainerItem)comboBox.getSelectedItem()).getLabel();
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
        tableName = tableName.replaceAll(" ", "_");
        dataMap.put(uri, URI.create("geocatalog:"+tableName+"#"+tableName));
    }

    /**
     * When a new table name is set, empty the comboBox and save the table name.
     * @param document
     */
    public void onNewTable(Document document){
        try {
            JComboBox<String> comboBox = (JComboBox<String>)document.getProperty("comboBox");
            Map<URI, Object> dataMap = (Map<URI, Object>)comboBox.getClientProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)comboBox.getClientProperty("uri");
            String text = document.getText(0, document.getLength());
            text = text.replaceAll(" ", "_");
            if(!text.isEmpty()){
                dataMap.put(uri, URI.create("geocatalog:"+text.toUpperCase()+"#"+text.toUpperCase()));
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStoreUI.class).error(e.getMessage());
        }
    }

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
     * @param event
     */
    public void onBrowse(ActionEvent event){
        //Open the file browse window
        JButton source = (JButton)event.getSource();
        OpenFilePanel openFilePanel = (OpenFilePanel)source.getClientProperty("filePanel");
        if (UIFactory.showDialog(openFilePanel, true, true)) {
            JTextField textField = (JTextField) source.getClientProperty("JTextField");
            textField.setText(openFilePanel.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentTextFile(Document document){
        URI selectedFileURI;
        try {
            DataStore dataStore = (DataStore)document.getProperty("dataStore");
            JComponent fileOptions = (JComponent) document.getProperty("fileOptions");
            DescriptionType inputOrOutput = (DescriptionType)document.getProperty("inputOrOutput");
            File file = new File(document.getText(0, document.getLength()));
            if(file.getName().isEmpty() || file.isDirectory()){
                return;
            }
            if(inputOrOutput instanceof Input) {
                if(!file.exists()){
                    return;
                }
                boolean loadSource = false;
                boolean keepSource = false;
                if(fileOptions != null){
                    if(fileOptions.getClientProperty("loadSource") != null) {
                        loadSource = (boolean) fileOptions.getClientProperty("loadSource");
                    }
                    else{
                        loadSource = false;
                    }
                    keepSource = (boolean)fileOptions.getClientProperty("keepSource");
                }
                //Load the selected file an retrieve the table name.
                String tableName = wpsClient.loadURI(file.toURI(), loadSource);
                if (tableName != null) {
                    String fileStr = file.toURI().toString();
                    if(keepSource){
                        fileStr += "$";
                    }
                    //Saves the table name in the URI into the uri fragment
                    selectedFileURI = URI.create(fileStr + "#" + tableName);
                    //Store the selection
                    Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty("dataMap");
                    URI uri = inputOrOutput.getIdentifier();
                    Object oldValue = dataMap.get(uri);
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
                String tableName = wpsClient.getDataManager().findUniqueTableName(FilenameUtils.getBaseName(file.getName()));
                tableName = tableName.replace("\"", "");
                selectedFileURI = URI.create(file.toURI().toString() + "#" + tableName);
                //Store the selection
                Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty("dataMap");
                URI uri = inputOrOutput.getIdentifier();
                dataMap.remove(uri);
                dataMap.put(uri, selectedFileURI);
            }
        } catch (BadLocationException|SQLException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
        }
    }

    /**
     * When the file option icon is hovered, display a popup menu with the options.
     * @param me
     */
    public void onFileOption(MouseEvent me){
        JComponent source = (JComponent)me.getSource();
        JPopupMenu popupMenu = new JPopupMenu();
        if(source.getClientProperty("loadSource") != null) {
            boolean loadSource = (boolean) source.getClientProperty("loadSource");
            JCheckBoxMenuItem loadItem = new JCheckBoxMenuItem("Linked table", null, !loadSource) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                        doClick();
                        setArmed(true);
                        ((JComponent)this.getClientProperty("fileOption")).putClientProperty("loadSource", !this.getState());
                    } else {
                        super.processMouseEvent(evt);
                    }
                }
            };
            loadItem.putClientProperty("fileOption", source);
            popupMenu.add(loadItem);
        }
        if(source.getClientProperty("keepSource") != null) {
            boolean keepSource = (boolean) source.getClientProperty("keepSource");
            JCheckBoxMenuItem keepItem = new JCheckBoxMenuItem("Keep file in base", null, keepSource) {
                @Override
                protected void processMouseEvent(MouseEvent evt) {
                    if (evt.getID() == MouseEvent.MOUSE_RELEASED && contains(evt.getPoint())) {
                        doClick();
                        setArmed(true);
                        ((JComponent) this.getClientProperty("fileOption")).putClientProperty("keepSource", !this.getState());
                    } else {
                        super.processMouseEvent(evt);
                    }
                }
            };
            keepItem.putClientProperty("fileOption", source);
            popupMenu.add(keepItem);
        }
        popupMenu.show(source, me.getX(), me.getY());
    }

    /**
     * SwingWorker extension which will load the the selected datasource.
     */
    public class ImportWorker extends SwingWorkerPM {

        private Document document;

        public void setDocument(Document document){
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
