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
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.*;
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
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * DataUI associated to the RawData type.
 * This class generate an interactive UI dedicated to the configuration of a RawData.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class GeometryUI implements DataUI {

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String DEFAULT_VALUE_PROPERTY = "DEFAULT_VALUE_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;
    private static final I18n I18N = I18nFactory.getI18n(GeometryUI.class);

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        //Create the main panel
        JComponent component = new JPanel(new MigLayout("fill"));

        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            isOptional = ((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"));
        }

        GeometryData geometryData = null;
        if(inputOrOutput instanceof InputDescriptionType){
            geometryData = (GeometryData) ((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        //If the DescriptionType is an output, there is nothing to show, so exit
        if(geometryData == null){
            return null;
        }

        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        jtf.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
        jtf.getDocument().putProperty(URI_PROPERTY, uri);
        if(geometryData.getDefaultValue() != null) {
            jtf.getDocument().putProperty(DEFAULT_VALUE_PROPERTY, geometryData.getDefaultValue());
        }
        else{
            jtf.getDocument().putProperty(DEFAULT_VALUE_PROPERTY, "");
        }
        jtf.getDocument().putProperty(IS_OPTIONAL_PROPERTY, isOptional);
        jtf.getDocument().putProperty(TEXT_FIELD_PROPERTY, jtf);
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this,
                "saveDocumentText", "document"));
        if(isOptional) {
            if (dataMap.containsKey(uri)) {
                jtf.setText(dataMap.get(uri).toString());
            }
        }

        component.add(jtf, "growx");

        if(orientation.equals(Orientation.VERTICAL)) {
            JPanel buttonPanel = new JPanel(new MigLayout());

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

            component.add(buttonPanel, "dock east");
        }

        return component;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        GeometryData geometryData = null;
        if(inputOrOutput instanceof InputDescriptionType){
            geometryData = (GeometryData)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            geometryData = (GeometryData)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(geometryData.getDefaultValue() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), geometryData.getDefaultValue());
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.RAW_DATA);
    }

    /**
     * Action do on clicking on the paste button.
     * @param ae Action event fired.
     */
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
     * Save the text contained by the Document in the dataMap set as property.
     * @param document
     */
    public void saveDocumentText(Document document){
        try {
            String name = document.getText(0, document.getLength());
            boolean isOptional = (boolean) document.getProperty(IS_OPTIONAL_PROPERTY);
            final String defaultValue = (String) document.getProperty(DEFAULT_VALUE_PROPERTY);
            if(name.isEmpty() && !isOptional && !defaultValue.isEmpty()){
                final JTextField textField = (JTextField) document.getProperty(TEXT_FIELD_PROPERTY);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        textField.setText(defaultValue);
                    }
                });
            }
            else {
                Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty(DATA_MAP_PROPERTY);
                URI uri = (URI) document.getProperty(URI_PROPERTY);
                if(isOptional && name.isEmpty()){
                    dataMap.put(uri, null);
                }
                else {
                    dataMap.put(uri, name);
                }
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(GeometryUI.class).error(e.getMessage());
        }
    }
}
