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
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.AbstractOpenPanel;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.OpenFolderPanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsservice.model.Input;
import org.orbisgis.wpsservice.model.Output;
import org.orbisgis.wpsservice.model.RawData;
import org.slf4j.LoggerFactory;

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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.io.IOException;
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

public class RawDataUI implements DataUI {

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String TEXT_FIELD_DATA = "TEXT_FIELD_DATA";
    private static final String OPEN_PANEL = "OPEN_PANEL";

    /** WpsClient using the generated UI. */
    private WpsClient wpsClient;

    @Override
    public void setWpsClient(WpsClient wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        //Create the main panel
        JComponent component = new JPanel(new MigLayout("fill"));
        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        jtf.getDocument().putProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this,
                "saveDocumentText", "document"));

        RawData rawData = null;
        AbstractOpenPanel openPanel = null;
        if(inputOrOutput instanceof Input){
            rawData = (RawData) ((Input)inputOrOutput).getDataDescription();
            if(rawData.isDirectory() && !rawData.isFile()) {
                openPanel = new OpenFolderPanel("RawDataUI.DirectoryInput", "Select Directory");
            }
            else if(!rawData.isDirectory() && rawData.isFile()) {
                openPanel = new OpenFilePanel("RawDataUI.FileInput", "Select File");
            }
            else {
                openPanel = new OpenPanel("RawDataUI.Input", "Select File or Directory");
            }
        }
        else if(inputOrOutput instanceof Output){
            rawData = (RawData) ((Output)inputOrOutput).getDataDescription();
            if(rawData.isDirectory() && !rawData.isFile()) {
                openPanel = new SaveFolderPanel("RawDataUI.DirectoryInput", "Select Directory");
            }
            else if(!rawData.isDirectory() && rawData.isFile()) {
                openPanel = new SaveFilePanel("RawDataUI.FileInput", "Select File");
            }
            else {
                openPanel = new SavePanel("RawDataUI.Input", "Select File or Directory");
            }
        }
        if(rawData == null){
            return component;
        }
        openPanel.loadState();


        if(dataMap.get(inputOrOutput.getIdentifier()) != null)
            jtf.setText(dataMap.get(inputOrOutput.getIdentifier()).toString());
        else {
            jtf.setText(openPanel.getCurrentDirectory().getAbsolutePath());
        }
        component.add(jtf, "growx");

        //Create the button Browse
        JButton button = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.BROWSE));
        //"Save" the sourceCA and the JTextField in the button
        button.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        button.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        button.putClientProperty(TEXT_FIELD_DATA, jtf);
        button.putClientProperty(OPEN_PANEL, openPanel);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        //Add the listener for the click on the button
        button.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));
        component.add(button);

        //Create the button Browse
        JButton pasteButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.PASTE));
        //"Save" the sourceCA and the JTextField in the button
        pasteButton.putClientProperty(TEXT_FIELD_DATA, jtf);
        pasteButton.setBorderPainted(false);
        pasteButton.setContentAreaFilled(false);
        //Add the listener for the click on the button
        pasteButton.addActionListener(EventHandler.create(ActionListener.class, this, "onPaste", ""));
        component.add(pasteButton);

        return component;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.getIcon(ToolBoxIcon.RAW_DATA));
    }

    public void onPaste(ActionEvent ae){
        Object sourceObj = ae.getSource();
        if(sourceObj instanceof JButton){
            JButton pasteButton = (JButton) sourceObj;
            JTextField textField = (JTextField) pasteButton.getClientProperty(TEXT_FIELD_DATA);
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
        JButton source = (JButton)event.getSource();
        OpenFilePanel openFilePanel = (OpenFilePanel)source.getClientProperty(OPEN_PANEL);
        if (UIFactory.showDialog(openFilePanel, true, true)) {
            JTextField textField = (JTextField)source.getClientProperty(TEXT_FIELD_DATA);
            textField.setText(openFilePanel.getSelectedFile().getName());
            Map<URI, Object> dataMap = (Map<URI, Object>)source.getClientProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)source.getClientProperty(URI_PROPERTY);
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
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)document.getProperty(URI_PROPERTY);
            String name = document.getText(0, document.getLength());
            dataMap.put(uri, name);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(RawData.class).error(e.getMessage());
        }
    }
}
