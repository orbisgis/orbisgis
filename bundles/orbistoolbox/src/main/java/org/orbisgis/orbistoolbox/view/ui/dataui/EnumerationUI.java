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
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Enumeration;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * UI generator associated to the Enumeration
 *
 * @author Sylvain PALOMINOS
 **/

public class EnumerationUI implements DataUI{
    private static final int JLIST_ROW_COUNT = 10;

    private ToolBox toolBox;

    public void setToolBox(ToolBox toolBox){
        this.toolBox = toolBox;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));
        //Get the enumeration object
        Enumeration enumeration = null;
        if(inputOrOutput instanceof Input){
            enumeration = (Enumeration)((Input)inputOrOutput).getDataDescription();
        }
        else if(inputOrOutput instanceof Output){
            enumeration = (Enumeration)((Output)inputOrOutput).getDataDescription();
        }

        if(enumeration == null){
            return panel;
        }
        //Adds the text label to the panel
        if(inputOrOutput.getResume().isEmpty()){
            panel.add(new JLabel(inputOrOutput.getTitle()), "growx, wrap");
        }
        else {
            panel.add(new JLabel("Select " + inputOrOutput.getResume()), "growx, wrap");
        }
        //Build the JList containing the data
        DefaultListModel<String> model = new DefaultListModel<>();
        for(String element : enumeration.getValues()){
            model.addElement(element);
        }
        JList<String> list = new JList<>(model);
        if(enumeration.isMultiSelection()){
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        //Select the default values
        List<Integer> selectedIndex = new ArrayList<>();
        for(String defaultValue : enumeration.getDefaultValues()){
            selectedIndex.add(model.indexOf(defaultValue));
        }
        int[] array = new int[selectedIndex.size()];
        for(int i=0; i<selectedIndex.size(); i++){
            array[i] = selectedIndex.get(i);
        }
        list.setSelectedIndices(array);
        //Configure the JList
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(JLIST_ROW_COUNT);
        JScrollPane listScroller = new JScrollPane(list);
        panel.add(listScroller, "growx, wrap");
        list.putClientProperty("uri", inputOrOutput.getIdentifier());
        list.putClientProperty("enumeration", enumeration);
        list.putClientProperty("dataMap", dataMap);
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));

        //case of the editable value
        if(enumeration.isEditable()){
            JTextField textField = new JTextField();
            textField.getDocument().putProperty("dataMap", dataMap);
            textField.getDocument().putProperty("uri", inputOrOutput.getIdentifier());
            textField.getDocument().putProperty("enumeration", enumeration);
            textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                    this,
                    "saveDocumentTextFile",
                    "document"));

            panel.add(textField, "growx, wrap");
            list.putClientProperty("textField", textField);
        }

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        Enumeration enumeration = null;
        if(inputOrOutput instanceof Input){
            enumeration = (Enumeration)((Input)inputOrOutput).getDataDescription();
        }
        else if(inputOrOutput instanceof Output){
            enumeration = (Enumeration)((Output)inputOrOutput).getDataDescription();
        }
        map.put(inputOrOutput.getIdentifier(), enumeration.getDefaultValues());
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("enumeration");
    }

    public void onListSelection(Object source){
        JList list = (JList)source;
        //If there is a textfield and if it contain a text, it means that their is a custom value and it will be used
        if(list.getClientProperty("textField")!=null){
            JTextField textField = (JTextField)list.getClientProperty("textField");
            if(!textField.getText().isEmpty()){
                return;
            }
        }
        List<String> listValues = new ArrayList<>();
        for(int i : list.getSelectedIndices()){
            listValues.add(list.getModel().getElementAt(i).toString());
        }
        URI uri = (URI)list.getClientProperty("uri");
        HashMap<URI, Object> dataMap = (HashMap)list.getClientProperty("dataMap");
        dataMap.put(uri, listValues);
    }

    public void saveDocumentTextFile(Document document){
        try {
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty("dataMap");
            URI uri = (URI)document.getProperty("uri");
            dataMap.remove(uri);
            List<String> list = new ArrayList<>();
            list.add(document.getText(0,document.getLength()));
            dataMap.put(uri, list);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(DataStore.class).error(e.getMessage());
        }
    }
}
