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

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.ComplexeData.RawData;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.components.OpenFilePanel;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class RawDataUI implements DataUI {
    @Override
    public JComponent createUI(Input input, Map<URI, Object> dataMap) {
        //Create the component
        JComponent component = new JPanel();
        component.setLayout(new FlowLayout(FlowLayout.LEFT));

        //component.add(new JLabel(sourceCA.getName()));
        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        jtf.setColumns(25);
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty("dataMap", dataMap);
        jtf.getDocument().putProperty("uri", input.getIdentifier());
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this, "saveDocumentText", "document"));

        if(dataMap.get(input.getIdentifier()) != null)
            jtf.setText(dataMap.get(input.getIdentifier()).toString());
        else {
            //Load the last path use in a sourceCA
            OpenFilePanel openFilePanel = new OpenFilePanel("RawDataUI.File", "Select File");
            openFilePanel.addFilter(new String[]{".shp"}, "Shape file");
            openFilePanel.addFilter(new String[]{"*"}, "All files");
            openFilePanel.loadState();
            jtf.setText(openFilePanel.getCurrentDirectory().getAbsolutePath());
        }

        component.add(jtf);
        //Create the button Browse
        JButton button = new JButton("Browse");
        //"Save" the sourceCA and the JTextField in the button
        button.putClientProperty("dataMap", dataMap);
        button.putClientProperty("uri", input.getIdentifier());
        button.putClientProperty("JTextField", jtf);
        //Add the listener for the click on the button
        button.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));

        component.add(button);
        return component;
    }
    @Override
    public JComponent createUI(Output output, Map<URI, Object> dataMap) {
        //Create the component
        JComponent component = new JPanel();
        component.setLayout(new FlowLayout(FlowLayout.LEFT));

        //component.add(new JLabel(sourceCA.getName()));
        //Display the SourceCA into a JTextField
        JTextField jtf = new JTextField();
        jtf.setColumns(25);
        //"Save" the CA inside the JTextField
        jtf.getDocument().putProperty("dataMap", dataMap);
        jtf.getDocument().putProperty("uri", output.getIdentifier());
        //add the listener for the text changes in the JTextField
        jtf.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class, this, "saveDocumentText", "document"));

        if(dataMap.get(output.getIdentifier()) != null)
            jtf.setText(dataMap.get(output.getIdentifier()).toString());
        else {
            //Load the last path use in a sourceCA
            OpenFilePanel openFilePanel = new OpenFilePanel("RawDataUI.File", "Select File");
            openFilePanel.addFilter(new String[]{".shp"}, "Shape file");
            openFilePanel.addFilter(new String[]{"*"}, "All files");
            openFilePanel.loadState();
            jtf.setText(openFilePanel.getCurrentDirectory().getAbsolutePath());
        }

        component.add(jtf);
        //Create the button Browse
        JButton button = new JButton("Browse");
        //"Save" the sourceCA and the JTextField in the button
        button.putClientProperty("dataMap", dataMap);
        button.putClientProperty("uri", output.getIdentifier());
        button.putClientProperty("JTextField", jtf);
        //Add the listener for the click on the button
        button.addActionListener(EventHandler.create(ActionListener.class, this, "openLoadPanel", ""));

        component.add(button);
        return component;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    /**
     * Opens an LoadPanel to permit to the user to select the file to load.
     * @param event
     */
    public void openLoadPanel(ActionEvent event){
        OpenFilePanel openFilePanel = new OpenFilePanel("ConfigurationAttribute.SourceCA", "Select source");
        openFilePanel.addFilter(new String[]{".shp"}, "Shape file");
        openFilePanel.addFilter(new String[]{"*"}, "All files");
        openFilePanel.loadState();
        if (UIFactory.showDialog(openFilePanel, true, true)) {
            JButton source = (JButton)event.getSource();
            Map<URI, Object> dataMap = (Map<URI, Object>)source.getClientProperty("dataMap");
            URI uri = (URI)source.getClientProperty("uri");
            dataMap.remove(uri);
            dataMap.put(uri, openFilePanel.getSelectedFile().getAbsolutePath());
            JTextField textField = (JTextField)source.getClientProperty("JTextField");
            textField.setText(openFilePanel.getSelectedFile().getName());
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
            if(new File(name).exists()) {
                dataMap.remove(uri);
                dataMap.put(uri, name);
            }
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(RawData.class).error(e.getMessage());
        }
    }
}
