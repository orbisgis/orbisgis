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

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolbox.model.RawData;
import org.orbisgis.sif.UIFactory;
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
import java.util.HashMap;
import java.util.Map;

/**
 * DataUI associated to the RawData type.
 *
 * @author Sylvain PALOMINOS
 **/

public class RawDataUI implements DataUI {

    private WpsClient wpsClient;

    public void setWpsClient(WpsClient wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        //Create the component
        JComponent component = new JPanel();
        component.setLayout(new FlowLayout(FlowLayout.LEFT));
        boolean isOptional = false;

        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        jtf.setColumns(25);
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty("dataMap", dataMap);
        jtf.getDocument().putProperty("uri", inputOrOutput.getIdentifier());
        //add the listener to display the full file path when the text box is selected.
        jtf.addMouseListener(EventHandler.create(MouseListener.class, this, "onSelected", "", "mouseClicked"));
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this, "saveDocumentText", "document"));

        OpenFilePanel filePanel = null;
        if(inputOrOutput instanceof Input){
            filePanel = new OpenFilePanel("RawDataUI.File", "Select File");
            if(((Input)inputOrOutput).getMinOccurs() == 0){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof Output){
            filePanel = new SaveFilePanel("RawDataUI.File", "Select File");
        }
        filePanel.addFilter(new String[]{"*"}, "All File");
        filePanel.loadState();

        if(dataMap.get(inputOrOutput.getIdentifier()) != null)
            jtf.setText(dataMap.get(inputOrOutput.getIdentifier()).toString());
        else {
            jtf.setText(filePanel.getCurrentDirectory().getAbsolutePath());
        }

        component.add(jtf, "growx");
        //Create the button Browse
        JButton button = new JButton("Browse");
        //"Save" the sourceCA and the JTextField in the button
        button.putClientProperty("dataMap", dataMap);
        button.putClientProperty("uri", inputOrOutput.getIdentifier());
        button.putClientProperty("JTextField", jtf);
        button.putClientProperty("filePanel", filePanel);
        button.putClientProperty("isOptional", isOptional);
        //Add the listener for the click on the button
        button.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));

        component.add(button);
        return component;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("undefined");
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
            boolean isOptional = (boolean)document.getProperty("isOptional");
            String name = document.getText(0, document.getLength());
            if(isOptional && name.isEmpty()){
                name = null;
            }
            dataMap.put(uri, name);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(RawData.class).error(e.getMessage());
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
