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
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenPanel;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.RawData;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.IOException;
import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * DataUI associated to the RawData type.
 * This class generate an interactive UI dedicated to the configuration of a RawData.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class RawDataUI implements DataUI {

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String OPEN_PANEL_PROPERTY = "OPEN_PANEL_PROPERTY";
    private static final String MULTI_SELECTION_PROPERTY = "MULTI_SELECTION_PROPERTY";
    private static final String EXCLUDED_TYPE_LIST_PROPERTY = "EXCLUDED_TYPE_LIST_PROPERTY";
    private static final String DEFAULT_VALUE_PROPERTY = "DEFAULT_VALUE_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String RAW_DATA_PROPERTY = "RAW_DATA_PROPERTY";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(RawDataUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {

        RawData rawData = null;
        String action = null;
        boolean isOptional = false;
        String panelName = "";
        if(inputOrOutput instanceof InputDescriptionType){
            rawData = (RawData) ((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            action = OpenPanel.ACTION_SAVE;
            isOptional = ((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"));
            panelName = I18N.tr("Specify a file or a folder");
        }

        //Create the main panel
        JComponent component = new JPanel(new MigLayout("fill"));
        if(rawData == null){
            return component;
        }
        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        jtf.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        jtf.getDocument().putProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        if(rawData.getDefaultValues() != null) {
            jtf.getDocument().putProperty(DEFAULT_VALUE_PROPERTY, rawData.getDefaultValues());
        }
        else{
            jtf.getDocument().putProperty(DEFAULT_VALUE_PROPERTY, new String[]{});
        }
        jtf.getDocument().putProperty(IS_OPTIONAL_PROPERTY, isOptional);
        jtf.getDocument().putProperty(TEXT_FIELD_PROPERTY, jtf);
        jtf.getDocument().putProperty(RAW_DATA_PROPERTY, rawData);
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this,
                "saveDocumentText", "document"));


        String dataAccepted;
        if(rawData.isDirectory() && !rawData.isFile()) {
            dataAccepted = OpenPanel.ACCEPT_DIRECTORY;
        }
        else if(!rawData.isDirectory() && rawData.isFile()) {
            dataAccepted = OpenPanel.ACCEPT_FILE;
        }
        else {
            dataAccepted = OpenPanel.ACCEPT_BOTH;
        }

        OpenPanel openPanel = new OpenPanel("RawData.OpenPanel", panelName, action, dataAccepted);
        openPanel.setConfirmOverwrite(false);
        openPanel.loadState();
        if(rawData.getFileTypes() == null || rawData.getFileTypes().length == 0) {
            openPanel.setAcceptAllFileFilterUsed(true);
        }
        else{
            for(String type : rawData.getFileTypes()){
                openPanel.addFilter(type, type);
            }
            openPanel.setAcceptAllFileFilterUsed(true);
            openPanel.setCurrentFilter(0);
        }

        openPanel.setSingleSelection(!rawData.multiSelection());


        Object defaultValuesObject = dataMap.get(URI.create(inputOrOutput.getIdentifier().getValue()));
        if(defaultValuesObject != null && defaultValuesObject instanceof String[]) {
            String txt = "";
            if(rawData.multiSelection()) {
                for(String str : (String[])defaultValuesObject){
                    if(!txt.isEmpty()){
                        txt+=" ";
                    }
                    txt+="\""+str+"\"";
                }
            }
            else{
                txt = ((String[])defaultValuesObject)[0];
            }
            jtf.setText(txt);
        }
        else {
            jtf.setText(openPanel.getCurrentDirectory().getAbsolutePath());
        }
        component.add(jtf, "growx");

        JPanel buttonPanel = new JPanel(new MigLayout());
        //Create the button Browse
        JButton browseButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.BROWSE));
        //"Save" the sourceCA and the JTextField in the button
        browseButton.putClientProperty(MULTI_SELECTION_PROPERTY, rawData.multiSelection());
        browseButton.putClientProperty(TEXT_FIELD_PROPERTY, jtf);
        browseButton.putClientProperty(OPEN_PANEL_PROPERTY, openPanel);
        browseButton.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        browseButton.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        browseButton.putClientProperty(EXCLUDED_TYPE_LIST_PROPERTY, rawData.getExcludedTypes());
        browseButton.setBorderPainted(false);
        browseButton.setContentAreaFilled(false);
        browseButton.setMargin(new Insets(0, 0, 0, 0));
        browseButton.setToolTipText(I18N.tr("Browse"));
        //Add the listener for the click on the button
        browseButton.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));
        buttonPanel.add(browseButton);
        if(orientation.equals(Orientation.VERTICAL)) {
            //Create the button Browse
            JButton pasteButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.PASTE));
            //"Save" the sourceCA and the JTextField in the button
            pasteButton.putClientProperty(TEXT_FIELD_PROPERTY, jtf);
            pasteButton.setBorderPainted(false);
            pasteButton.setContentAreaFilled(false);
            pasteButton.setToolTipText(I18N.tr("Paste the clipboard"));
            pasteButton.setMargin(new Insets(0, 0, 0, 0));
            //Add the listener for the click on the button
            pasteButton.addActionListener(EventHandler.create(ActionListener.class, this, "onPaste", ""));
            buttonPanel.add(pasteButton);
        }

        component.add(buttonPanel, "dock east");

        return component;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        RawData rawData = null;
        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            rawData = (RawData)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            isOptional = ((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"));
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            rawData = (RawData)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(rawData.getDefaultValues() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), rawData.getDefaultValues());
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.RAW_DATA);
    }

    public void onPaste(ActionEvent ae){
        Object sourceObj = ae.getSource();
        if(sourceObj instanceof JButton){
            JButton pasteButton = (JButton) sourceObj;
            JTextField textField = (JTextField) pasteButton.getClientProperty(TEXT_FIELD_PROPERTY);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            //odd: the Object param of getContents is not currently used
            Transferable contents = clipboard.getContents(null);
            boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
            if (hasTransferableText) {
                try {
                    textField.setText((String)contents.getTransferData(DataFlavor.stringFlavor));
                }
                catch (UnsupportedFlavorException | IOException ignored){
                }
            }
        }
    }

    /**
     * Opens an LoadPanel to permit to the user to select the file to load.
     * @param event
     */
    public void openLoadPanel(ActionEvent event){
        JButton source = (JButton) event.getSource();
        OpenPanel openPanel = (OpenPanel) source.getClientProperty(OPEN_PANEL_PROPERTY);
        List<String> excludedTypeList = (List<String>) source.getClientProperty(EXCLUDED_TYPE_LIST_PROPERTY);
        if (UIFactory.showDialog(openPanel, true, true)) {
            openPanel.saveState();
            JTextField textField = (JTextField) source.getClientProperty(TEXT_FIELD_PROPERTY);
            boolean multiSelection = (boolean) source.getClientProperty((MULTI_SELECTION_PROPERTY));
            if(multiSelection){
                String str = "";
                String displayedStr = "";
                for(File f : openPanel.getSelectedFiles()){
                    String extension = null;
                    if(f.getName().lastIndexOf(".") != -1){
                        extension = f.getName().substring(f.getName().lastIndexOf(".")+1);
                    }
                    if(extension == null || excludedTypeList == null || !excludedTypeList.contains(extension)) {
                        if (str.isEmpty()) {
                            str += f.getAbsolutePath();
                            displayedStr += "\"" + f.getAbsolutePath() + "\"";
                        } else {
                            str += "\t" + f.getAbsolutePath();
                            displayedStr += " \"" + f.getAbsolutePath() + "\"";
                        }
                    }
                }
                Map<URI, Object> dataMap = (Map<URI, Object>) source.getClientProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) source.getClientProperty(URI_PROPERTY);
                dataMap.put(uri, str);
                textField.setText(displayedStr);
            }
            else {
                File f = openPanel.getSelectedFile();
                String extension = null;
                if(f.getName().lastIndexOf(".") != -1){
                    extension = f.getName().substring(f.getName().lastIndexOf(".")+1);
                }
                if(extension == null || excludedTypeList == null || !excludedTypeList.contains(extension)) {
                    textField.setText(f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentText(Document document){
        try {
            String name = document.getText(0, document.getLength());
            boolean isOptional = (boolean) document.getProperty(IS_OPTIONAL_PROPERTY);
            String[] defaultValue = (String[]) document.getProperty(DEFAULT_VALUE_PROPERTY);
            if(name.isEmpty() && !isOptional && defaultValue.length != 0){
                final JTextField jtf = (JTextField) document.getProperty(TEXT_FIELD_PROPERTY);
                String text = "";
                for(String str : defaultValue){
                    if (!text.isEmpty()) {
                        text += " ";
                    }
                    text += "\"" + str + "\"";
                }
                final String finalText = text;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        jtf.setText(finalText);
                    }
                });
            }
            else {
                Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) document.getProperty(URI_PROPERTY);
                if(name.isEmpty() && isOptional){
                    dataMap.put(uri, null);
                }
                else {
                    if (name.contains("\"")) {
                        name = name.replaceAll("\" \"", "\t").replaceAll("\"", "");
                    }
                    dataMap.put(uri, name);
                }
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(RawDataUI.class).error(e.getMessage());
        }
    }
}
