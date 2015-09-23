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
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;

/**
 * DataUI for GeoData
 *
 * @author Sylvain PALOMINOS
 **/

public class GeoDataUI implements DataUI{

    private static final int BROWSETEXTFIELD_WIDTH = 25;

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
        GeoData geoData = null;
        Map<String, String> extensionMap = null;
        //If the descriptionType is an input, add a comboBox to select the input type and according to the type,
        // add a second JComponent to write the input value
        if(inputOrOutput instanceof Input){
            Input input = (Input)inputOrOutput;
            geoData = (GeoData)input.getDataDescription();
            extensionMap = ToolBox.getImportableSpatialFormat();
        }
        if(inputOrOutput instanceof Output){
            Output output = (Output)inputOrOutput;
            geoData = (GeoData)output.getDataDescription();
            extensionMap = ToolBox.getExportableSpatialFormat();
        }
        if(geoData == null || extensionMap == null){
            return panel;
        }

        //JComboBox with the input type
        JComboBox<ContainerItem> comboBox = new JComboBox<>();
        for(Format format : geoData.getFormats()){
            String ext = FormatFactory.getFormatExtension(format);
            if(extensionMap.get(ext) != null) {
                comboBox.addItem(new ContainerItem<>(ext, extensionMap.get(ext)+" (*."+ext+")"));
            }
            else{
                comboBox.addItem(new ContainerItem<>(ext, ext));
            }
            if(format.isDefaultFormat()){
                comboBox.setSelectedIndex(comboBox.getItemCount()-1);
            }
        }
        panel.add(comboBox, "wrap");

        //JPanel containing the component to set the input value
        JComponent dataField = new JPanel();
        panel.add(dataField);

        comboBox.putClientProperty("inputOrOutput", inputOrOutput);
        comboBox.putClientProperty("dataField", dataField);
        comboBox.putClientProperty("uri", inputOrOutput.getIdentifier());
        comboBox.putClientProperty("dataMap", dataMap);
        comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onBoxChange", "source"));

        onBoxChange(comboBox);

        return panel;
    }

    /**
     * Call on selecting the type of data to use.
     * For each type registered in the JComboBox adapts the dataField panel.
     * Also add a listener to save the data value set by the user
     * @param source The comboBox containing the data type to use.
     */
    public void onBoxChange(Object source){
        JComboBox<ContainerItem> comboBox = (JComboBox<ContainerItem>) source;
        Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty("dataMap");
        URI uri = (URI) comboBox.getClientProperty("uri");
        DescriptionType descriptionType = (DescriptionType) comboBox.getClientProperty("inputOrOutput");
        ContainerItem container = (ContainerItem) comboBox.getSelectedItem();
        JComponent dataComponent;
        if(container.getKey().equals(FormatFactory.SQL_EXTENSION)){
            dataComponent = new JPanel(new BorderLayout());
            //Instantiate the component
            JComboBox<String> box = new JComboBox<>();
            for(String tableName : ToolBox.getTablesList()){
                box.addItem(tableName);
            }
            dataComponent.add(new JLabel("Select a table :"), BorderLayout.PAGE_START);
            dataComponent.add(box, BorderLayout.CENTER);
        }
        else{
            Map<String [], String> mapJson = new HashMap<>();
            mapJson.put(
                    new String[]{container.getKey().toString()},
                    container.getLabel().replace(" (*." + container.getKey() + ")", ""));
            dataComponent = createBrowsePanel(mapJson, dataMap, uri, descriptionType);
        }
        //Adds to the dataField the dataComponent
        JPanel panel = (JPanel) comboBox.getClientProperty("dataField");
        panel.removeAll();
        panel.add(dataComponent);
        panel.revalidate();
    }

    private JComponent createBrowsePanel(Map<String[], String> formatFilter,
                                    Map<URI, Object> dataMap,
                                    URI uri,
                                    DescriptionType inputOrOutput){
        //Create the component
        JPanel dataComponent = new JPanel();
        dataComponent.setLayout(new FlowLayout(FlowLayout.LEFT));
        //Adds the text field to display and write the file path
        JTextField jtf = new JTextField();
        jtf.setColumns(BROWSETEXTFIELD_WIDTH);
        jtf.getDocument().putProperty("dataMap", dataMap);
        jtf.getDocument().putProperty("uri", uri);
        //add the listener to display the full file path when the text box is selected.
        jtf.addMouseListener(EventHandler.create(MouseListener.class, this, "onSelected", "", "mouseClicked"));
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "saveDocumentText",
                "document"));

        OpenFilePanel filePanel = null;
        if(inputOrOutput instanceof Input){
            filePanel = new OpenFilePanel("RawDataUI.File."+uri, "Select File");
            filePanel.setAcceptAllFileFilterUsed(false);
        }
        else if(inputOrOutput instanceof Output){
            filePanel = new SaveFilePanel("RawDataUI.File."+uri, "Select File");
        }
        for(Map.Entry<String[], String> entry : formatFilter.entrySet()){
            filePanel.addFilter(entry.getKey(), entry.getValue());
        }
        filePanel.loadState();
        jtf.setText(filePanel.getCurrentDirectory().getAbsolutePath());

        dataComponent.add(jtf);
        //Create the button Browse
        JButton button = new JButton("Browse");
        button.putClientProperty("dataMap", dataMap);
        button.putClientProperty("uri", uri);
        button.putClientProperty("JTextField", jtf);
        button.putClientProperty("filePanel", filePanel);
        //Add the listener for the click on the button
        button.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));
        dataComponent.add(button);

        return dataComponent;
    }

    /**
     * Opens an LoadPanel to permit to the user to select the file to load.
     * @param event
     */
    public void openLoadPanel(ActionEvent event){
        JButton source = (JButton)event.getSource();
        OpenFilePanel openFilePanel = (OpenFilePanel)source.getClientProperty("filePanel");
        if (UIFactory.showDialog(openFilePanel, true, true)) {
            JTextField textField = (JTextField)source.getClientProperty("JTextField");
            textField.setText(openFilePanel.getSelectedFile().getName());
            Map<URI, Object> dataMap = (Map<URI, Object>)source.getClientProperty("dataMap");
            URI uri = (URI)source.getClientProperty("uri");
            dataMap.remove(uri);
            dataMap.put(uri, openFilePanel.getSelectedFile().getAbsolutePath());
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentText(Document document){
        try {
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty("dataMap");
            URI uri = (URI)document.getProperty("uri");
            String name = document.getText(0, document.getLength());
            dataMap.remove(uri);
            dataMap.put(uri, name);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(GeoData.class).error(e.getMessage());
        }
    }

    /**
     * When the textField is selected, display the full file path instead of the file name to allow the user to edit it.
     * @param me
     */
    public void onSelected(MouseEvent me){
        JTextField textField = (JTextField)me.getSource();
        Map<URI, Object> dataMap = (Map<URI, Object>)textField.getDocument().getProperty("dataMap");
        String path = dataMap.get(textField.getDocument().getProperty("uri")).toString();
        textField.setText(path);
    }
}
