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

package org.orbisgis.orbistoolbox.view.ui.dataui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.controller.processexecution.utils.FormatFactory;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.sif.multiInputPanel.CheckBoxChoice;
import org.orbisgis.sif.multiInputPanel.MultiInputPanel;
import org.orbisgis.sif.multiInputPanel.TextBoxType;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.*;

/**
 * DataUI for DataStore
 *
 * @author Sylvain PALOMINOS
 **/

public class DataStoreUI implements DataUI{

    /** constant size of the text fields **/
    private static final int TEXTFIELD_WIDTH = 25;

    private ToolBox toolBox;

    public void setToolBox(ToolBox toolBox){
        this.toolBox = toolBox;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("geodata");
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));
        DataStore dataStore = null;
        Map<String, String> extensionMap = null;
        //If the descriptionType is an input, add a comboBox to select the input type and according to the type,
        // add a second JComponent to write the input value
        if(inputOrOutput instanceof Input){
            Input input = (Input)inputOrOutput;
            dataStore = (DataStore)input.getDataDescription();
            extensionMap = ToolBox.getImportableFormat(true);
        }
        if(inputOrOutput instanceof Output){
            Output output = (Output)inputOrOutput;
            dataStore = (DataStore)output.getDataDescription();
            extensionMap = ToolBox.getExportableFormat(true);
        }
        if(dataStore == null || extensionMap == null){
            return panel;
        }
        panel.add(new JLabel("Select "+inputOrOutput.getResume()), "cell 0 0 6 1");

        ButtonGroup group = new ButtonGroup();

        /**Instantiate the geocatalog radioButton and its optionPanel**/
        JRadioButton geocatalog = new JRadioButton("Geocatalog");
        JPanel optionPanelGeocatalog = new JPanel(new MigLayout());
        JComboBox<String> comboBox;
        if(dataStore.isSpatial()) {
            comboBox = new JComboBox<>(ToolBox.getGeocatalogTableList(true).toArray(new String[]{}));
        }
        else {
            comboBox = new JComboBox<>(ToolBox.getGeocatalogTableList(false).toArray(new String[]{}));
        }
        comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onGeocatalogTableSelected", "source"));
        comboBox.putClientProperty("uri", inputOrOutput.getIdentifier());
        comboBox.putClientProperty("dataMap", dataMap);
        comboBox.putClientProperty("dataStore", dataStore);
        optionPanelGeocatalog.add(new JLabel("Geocatalog :"));
        optionPanelGeocatalog.add(comboBox);
        geocatalog.putClientProperty("optionPanel", optionPanelGeocatalog);
        geocatalog.addActionListener(EventHandler.create(ActionListener.class, this, "onRadioSelected", "source"));

        /**Instantiate the file radioButton and its optionPanel**/
        JRadioButton file = new JRadioButton("File");
        JPanel optionPanelFile = new JPanel(new MigLayout());
        optionPanelFile.add(new JLabel("file :"));
        JTextField textField = new JTextField(TEXTFIELD_WIDTH);
        textField.getDocument().putProperty("dataMap", dataMap);
        textField.getDocument().putProperty("uri", inputOrOutput.getIdentifier());
        textField.getDocument().putProperty("dataStore", dataStore);
        textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "saveDocumentTextFile",
                "document"));

        optionPanelFile.add(textField);
        JButton browseButton = new JButton("Browse");
        browseButton.addActionListener(EventHandler.create(ActionListener.class, this, "onBrowse", ""));
        browseButton.putClientProperty("uri", inputOrOutput.getIdentifier());
        browseButton.putClientProperty("dataMap", dataMap);
        browseButton.putClientProperty("JTextField", textField);
        browseButton.putClientProperty("dataStore", dataStore);
        OpenFilePanel filePanel;
        if(inputOrOutput instanceof Input){
            filePanel = new OpenFilePanel("RawDataUI.File."+inputOrOutput.getIdentifier(), "Select File");
            filePanel.setAcceptAllFileFilterUsed(false);
            for(Format format : dataStore.getFormats()){
                String ext = FormatFactory.getFormatExtension(format);
                String description = "";
                for(Map.Entry<String, String> entry : ToolBox.getImportableFormat(false).entrySet()){
                    if(entry.getKey().equalsIgnoreCase(ext)){
                        description = entry.getValue();
                    }
                }
                filePanel.addFilter(ext, description);
            }
        }
        else {
            filePanel = new SaveFilePanel("RawDataUI.File."+inputOrOutput.getIdentifier(), "Select File");
            for(Format format : dataStore.getFormats()){
                String ext = FormatFactory.getFormatExtension(format);
                String description = "";
                for(Map.Entry<String, String> entry : ToolBox.getExportableFormat(false).entrySet()){
                    if(entry.getKey().equalsIgnoreCase(ext)){
                        description = entry.getValue();
                    }
                }
                filePanel.addFilter(ext, description);
            }
        }
        filePanel.loadState();
        textField.setText(filePanel.getCurrentDirectory().getAbsolutePath());
        browseButton.putClientProperty("filePanel", filePanel);

        optionPanelFile.add(browseButton);
        file.putClientProperty("optionPanel", optionPanelFile);
        file.addActionListener(EventHandler.create(ActionListener.class, this, "onRadioSelected", "source"));

        /**Instantiate the dataBase radioButton and its optionPanel**/
        JRadioButton database = new JRadioButton("Database");
        JPanel optionPanelDataBase = new JPanel(new MigLayout());
        optionPanelDataBase.add(new JLabel("database :"));
        JTextField parametersTextField = new JTextField(TEXTFIELD_WIDTH);
        parametersTextField.getDocument().putProperty("dataMap", dataMap);
        parametersTextField.getDocument().putProperty("uri", inputOrOutput.getIdentifier());
        parametersTextField.getDocument().putProperty("dataStore", dataStore);
        parametersTextField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "saveDocumentTextDataBase",
                "document"));
        optionPanelDataBase.add(parametersTextField);
        JButton parametersButton = new JButton("Parameters");
        parametersButton.putClientProperty("textField", parametersTextField);
        parametersButton.addActionListener(EventHandler.create(ActionListener.class, this, "onParameters", "source"));
        optionPanelDataBase.add(parametersButton);
        database.putClientProperty("optionPanel", optionPanelDataBase);
        database.addActionListener(EventHandler.create(ActionListener.class, this, "onRadioSelected", "source"));

        geocatalog.setEnabled(dataStore.isGeocatalog());
        file.setEnabled(dataStore.isFile());
        database.setEnabled(dataStore.isDataBase());

        group.add(geocatalog);
        group.add(file);
        group.add(database);
        panel.add(geocatalog, "cell 0 1 2 1");
        panel.add(file, "cell 2 1 2 1");
        panel.add(database, "cell 4 1 2 1");

        JComponent dataField = new JPanel();
        geocatalog.putClientProperty("dataField", dataField);
        file.putClientProperty("dataField", dataField);
        database.putClientProperty("dataField", dataField);
        panel.add(dataField, "cell 0 2 6 1");

        return panel;
    }

    public void onGeocatalogTableSelected(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<String>> comboBox = (JComboBox)source;
        Map<URI, Object> dataMap = (Map<URI, Object>)comboBox.getClientProperty("dataMap");
        URI uri = (URI)comboBox.getClientProperty("uri");
        DataStore dataStore = (DataStore)comboBox.getClientProperty("dataStore");
        //Tells all the dataField linked that the data source is loaded
        for(DataField dataField : dataStore.getListDataField()){
            dataField.setSourceModifiedd(true);
        }
        dataMap.remove(uri);
        dataMap.put(uri, comboBox.getSelectedItem());
    }

    public void onParameters(Object source){
        if(source instanceof JButton){
            JButton parametersButton = (JButton)source;
            JTextField textField = (JTextField)parametersButton.getClientProperty("textField");
            MultiInputPanel multiInputPanel = new MultiInputPanel("JDBC parameters");
            TextBoxType textBoxDriver = new TextBoxType(TEXTFIELD_WIDTH);
            TextBoxType textBoxJDBCUrl = new TextBoxType(TEXTFIELD_WIDTH);
            CheckBoxChoice checkBoxPasswd = new CheckBoxChoice(false);
            TextBoxType textBoxSchema = new TextBoxType(TEXTFIELD_WIDTH);
            TextBoxType textBoxTable = new TextBoxType(TEXTFIELD_WIDTH);
            multiInputPanel.addInput("driver", "Driver :", "driver string", textBoxDriver);
            multiInputPanel.addInput("jdbcUrl", "JDBC Url :", "jdbc url", textBoxJDBCUrl);
            multiInputPanel.addInput("passwd", "Requires password :", checkBoxPasswd);
            multiInputPanel.addInput("schema", "Schema :", "schema", textBoxSchema);
            multiInputPanel.addInput("table", "Table :", "table", textBoxTable);

            if(UIFactory.showDialog(multiInputPanel, true, true)){
                URI uri = URI.create(textBoxJDBCUrl.getValue()+"?auth="+checkBoxPasswd.getValue()+
                        ";driver="+textBoxDriver.getValue()+";schema="+textBoxSchema.getValue()+
                        "#"+textBoxTable.getValue());
                textField.setText(uri.toString());
            }
        }
    }

    public void onRadioSelected(Object source){
        if(source instanceof JRadioButton){
            JRadioButton radioButton = (JRadioButton)source;
            if(radioButton.isSelected()) {
                JPanel optionPanel = (JPanel) radioButton.getClientProperty("optionPanel");
                JPanel dataField = (JPanel) radioButton.getClientProperty("dataField");
                dataField.removeAll();
                dataField.setLayout(new BorderLayout());
                dataField.add(optionPanel, BorderLayout.CENTER);
                dataField.revalidate();
            }
        }
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
            DataStore dataStore = (DataStore) source.getClientProperty("dataStore");
            URI selectedFileURI = openFilePanel.getSelectedFile().toURI();
            //Load the selected file an retrieve the table name.
            String tableName = loadDataStore(selectedFileURI);
            if(tableName != null) {
                //Set the UI with the selected value
                JTextField textField = (JTextField) source.getClientProperty("JTextField");
                textField.setText(openFilePanel.getSelectedFile().getName());
                Map<URI, Object> dataMap = (Map<URI, Object>) source.getClientProperty("dataMap");
                //Store the selection
                URI uri = (URI) source.getClientProperty("uri");
                dataMap.remove(uri);
                dataMap.put(uri, tableName);
                //tells the dataField they should revalidate
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(true);
                }
            }
            else{
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(false);
                }
            }
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentTextFile(Document document){
        try {
            DataStore dataStore = (DataStore)document.getProperty("dataStore");
            File file = new File(document.getText(0, document.getLength()));
            //Load the selected file an retrieve the table name.
            String tableName = loadDataStore(file.toURI());
            if(tableName != null) {
                //Store the selection
                Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty("dataMap");
                URI uri = (URI)document.getProperty("uri");
                dataMap.remove(uri);
                dataMap.put(uri, tableName);
                //tells the dataField they should revalidate
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(false);
                }
            }
            else{
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(true);
                }
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentTextDataBase(Document document){
        try {
            DataStore dataStore = (DataStore)document.getProperty("dataStore");
            URI dataBaseURI = URI.create(document.getText(0, document.getLength()));
            //Load the selected file an retrieve the table name.
            String tableName = loadDataStore(dataBaseURI);
            if(tableName != null) {
                //Store the selection
                Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty("dataMap");
                URI uri = (URI)document.getProperty("uri");
                dataMap.remove(uri);
                dataMap.put(uri, tableName);
                //tells the dataField they should revalidate
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(false);
                }
            }
            else{
                for (DataField dataField : dataStore.getListDataField()) {
                    dataField.setSourceModifiedd(true);
                }
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
        }
    }

    /**
     * Load the given data source.
     * @param dataStoreURI Uri of the data source to load.
     * @return The table name of the data source.
     */
    private String loadDataStore(URI dataStoreURI){
        File f = new File(dataStoreURI);
        if(f.isFile()) {
            return toolBox.loadFile(f);
        }
        return null;
    }
}
