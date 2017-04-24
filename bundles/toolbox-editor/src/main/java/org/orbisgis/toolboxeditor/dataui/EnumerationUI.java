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
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.Enumeration;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * DataUI implementation for Enumeration.
 * This class generate an interactive UI dedicated to the configuration of a Enumeration.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class EnumerationUI implements DataUI {
    private static final int JLIST_VERTICAL_MAX_ROW_COUNT = 10;
    private static final int JLIST_HORIZONTAL_MAX_ROW_COUNT = 3;

    /** Constant used to pass object as client property throw JComponents **/
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String ENUMERATION_PROPERTY = "ENUMERATION_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    private static final String IS_MULTISELECTION_PROPERTY = "IS_MULTISELECTION_PROPERTY";
    private static final String LIST_PROPERTY = "LIST_PROPERTY";
    private static final String MOUSE_LISTENER_PROPERTY = "MOUSE_LISTENER_PROPERTY";

    private static final String DEFAULT_TEXT = "User custom values";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(EnumerationUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        //Get the enumeration object
        Enumeration enumeration = null;
        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            enumeration = (Enumeration)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            return null;
        }

        if(enumeration == null){
            return panel;
        }
        //Build the JList containing the data
        DefaultListModel<ContainerItem<String>> model = new DefaultListModel<>();
        if(enumeration.getValuesNames() != null &&
                enumeration.getValuesNames().length > 0 &&
                enumeration.getValuesNames().length == enumeration.getValues().length){
            for(int i=0; i<enumeration.getValues().length; i++){
                model.addElement(new ContainerItem<>(enumeration.getValues()[i],
                        enumeration.getValuesNames()[i].getStrings()[0].getValue()));
            }
        }
        else{
            for(String element : enumeration.getValues()){
                model.addElement(new ContainerItem<>(element, element));
            }
        }
        JList<ContainerItem<String>> list = new JList<>(model);
        if(enumeration.isMultiSelection()){
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        //Select the default values
        List<Integer> selectedIndex = new ArrayList<>();
        if(enumeration.getDefaultValues() != null) {
            for (String defaultValue : enumeration.getDefaultValues()) {
                for (int i = 0; i < model.getSize(); i++) {
                    if (model.get(i).getKey().equals(defaultValue)) {
                        selectedIndex.add(i);
                    }
                }
            }
        }
        else{
            selectedIndex.add(0);
        }
        int[] array = new int[selectedIndex.size()];
        for (int i = 0; i < selectedIndex.size(); i++) {
            array[i] = selectedIndex.get(i);
        }
        list.setSelectedIndices(array);
        if(!isOptional) {
            dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
        }
        //Configure the JList
        list.setLayoutOrientation(JList.VERTICAL);
        int maxRowCount;
        if(orientation.equals(Orientation.VERTICAL)){
            maxRowCount = JLIST_VERTICAL_MAX_ROW_COUNT;
        }
        else{
            if(enumeration.isEditable()) {
                maxRowCount = JLIST_HORIZONTAL_MAX_ROW_COUNT - 1;
            }
            else{
                maxRowCount = JLIST_HORIZONTAL_MAX_ROW_COUNT;
            }
        }
        if(enumeration.getValues().length < maxRowCount){
            list.setVisibleRowCount(enumeration.getValues().length);
        }
        else {
            list.setVisibleRowCount(maxRowCount);
        }
        JScrollPane listScroller = new JScrollPane(list);
        panel.add(listScroller, "growx, wrap");
        //Sets the List properties
        list.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        list.putClientProperty(ENUMERATION_PROPERTY, enumeration);
        list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
        list.putClientProperty(IS_MULTISELECTION_PROPERTY, enumeration.isMultiSelection());
        list.putClientProperty(ENUMERATION_PROPERTY, enumeration);
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));
        list.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

        //case of the editable value
        if(enumeration.isEditable()){
            //Sets the text field
            JTextField textField = new JTextField();
            textField.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
            textField.getDocument().putProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
            textField.getDocument().putProperty(ENUMERATION_PROPERTY, enumeration);
            textField.getDocument().putProperty(LIST_PROPERTY, list);
            textField.getDocument().putProperty(IS_MULTISELECTION_PROPERTY, enumeration.isMultiSelection());
            textField.getDocument().putProperty(IS_OPTIONAL_PROPERTY, isOptional);
            textField.setText(I18N.tr(DEFAULT_TEXT));
            textField.setForeground(Color.gray);
            MouseListener mouseListener = EventHandler.create(MouseListener.class, this,
                    "onMouseClicked", "source", "mouseClicked");
            textField.putClientProperty(MOUSE_LISTENER_PROPERTY, mouseListener);
            textField.addMouseListener(mouseListener);
            textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                    this,
                    "saveDocumentTextFile",
                    "document"));
            textField.setToolTipText(I18N.tr("Coma separated custom value(s)"));

            panel.add(textField, "growx, wrap");
            list.putClientProperty(TEXT_FIELD_PROPERTY, textField);
        }

        onListSelection(list);

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        Enumeration enumeration = null;
        if(inputOrOutput instanceof InputDescriptionType){
            enumeration = (Enumeration)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            enumeration = (Enumeration)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        //Enumeration can not be null as inputOrOutput can only be an Input or an Output
        if(enumeration.getDefaultValues() != null && enumeration.getDefaultValues().length != 0) {
            if (enumeration.isMultiSelection()) {
                map.put(URI.create(inputOrOutput.getIdentifier().getValue()), enumeration.getDefaultValues());
            } else {
                map.put(URI.create(inputOrOutput.getIdentifier().getValue()), enumeration.getDefaultValues()[0]);
            }
        }
        else{
            if(enumeration.getValues().length != 0) {
                map.put(URI.create(inputOrOutput.getIdentifier().getValue()), enumeration.getValues()[0]);
            }
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.ENUMERATION);
    }

    /**
     * Action done when the mouse click the JTextField.
     * @param source Source JComponent which is clicked.
     */
    public void onMouseClicked(Object source){
        if(source instanceof JTextField){
            JTextField textField = ((JTextField) source);
            textField.setForeground(Color.black);
            textField.setText("");
            MouseListener mouseListener = (MouseListener)textField.getClientProperty(MOUSE_LISTENER_PROPERTY);
            textField.removeMouseListener(mouseListener);
        }
    }

    /**
     * When an item from the JList is selected, register it in the data map.
     * @param source Source JList.
     */
    public void onListSelection(Object source){
        JList<ContainerItem<String>> list = (JList<ContainerItem<String>>)source;
        List<String> listValues = new ArrayList<>();
        URI uri = (URI) list.getClientProperty(URI_PROPERTY);
        HashMap<URI, Object> dataMap = (HashMap) list.getClientProperty(DATA_MAP_PROPERTY);
        boolean isMultiSelection = (boolean)list.getClientProperty(IS_MULTISELECTION_PROPERTY);
        boolean isOptional = (boolean)list.getClientProperty(IS_OPTIONAL_PROPERTY);
        Enumeration enumeration = (Enumeration) list.getClientProperty(ENUMERATION_PROPERTY);
        //If there is a textfield and if it contain a text, add the coma separated values
        if (list.getClientProperty(TEXT_FIELD_PROPERTY) != null) {
            JTextField textField = (JTextField) list.getClientProperty(TEXT_FIELD_PROPERTY);
            if (!textField.getText().isEmpty()) {
                if(!isMultiSelection && list.getSelectedIndices().length != 0 && !textField.getText().equals(DEFAULT_TEXT)){
                    textField.setText("");
                }
                else {
                    listValues.add(textField.getText());
                }
            }
        }
        //Add the selected JList values
        for (int i : list.getSelectedIndices()) {
            listValues.add(list.getModel().getElementAt(i).getKey());
        }
        //if no values are selected, put null is isOptional, or select the first value if not
        if(listValues.isEmpty()){
            if(isOptional) {
                dataMap.put(uri, null);
            }
            else{
                int[] indices = new int[enumeration.getDefaultValues().length];
                for(int i=0; i<enumeration.getDefaultValues().length; i++){
                    for(int j=0; j<list.getModel().getSize(); j++){
                        if(list.getModel().getElementAt(j).getLabel().equals(enumeration.getDefaultValues()[i])){
                            indices[i] = j;
                        }
                    }
                }
                list.setSelectedIndices(indices);
            }
        }
        else {
            if(isMultiSelection){
                String str = "";
                for(String s : listValues){
                    if(! str.isEmpty()){
                        str+="\t";
                    }
                    str+=s;
                }
                dataMap.put(uri, str);
            }
            else {
                dataMap.put(uri, listValues.get(0));
            }
        }
    }

    /**
     * Saves the enumeration value set in the text field (available if the enumeration is editable).
     * @param document Document of the JTextField
     */
    public void saveDocumentTextFile(Document document){
        try {
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)document.getProperty(URI_PROPERTY);
            JList<ContainerItem<String>> list = (JList<ContainerItem<String>>) document.getProperty(LIST_PROPERTY);
            boolean isMultiSelection = (boolean)document.getProperty(IS_MULTISELECTION_PROPERTY);
            boolean isOptional = (boolean)document.getProperty(IS_OPTIONAL_PROPERTY);
            String text = document.getText(0, document.getLength());
            //If not optional and there is no text and no element select, then, select the first element.
            if(!isOptional){
                if(text.isEmpty() && list.getSelectedIndices().length == 0) {
                    list.setSelectedIndices(new int[]{0});
                }
            }
            //If there is a text and the multi selection is not allowed, empty the list selection
            if(!text.isEmpty() && !isMultiSelection){
                list.clearSelection();
            }
            List<String> listValues = new ArrayList<>();
            if(!isMultiSelection && !text.isEmpty()){
                text = text.replaceAll(",", "\t");
                listValues.add(text.split(",")[0]);
            }
            else {
                dataMap.remove(uri);
                if(!text.isEmpty()) {
                    text = text.replaceAll(",", "\t");
                    Collections.addAll(listValues, text.split(","));
                }
                for (int i : list.getSelectedIndices()) {
                    listValues.add(list.getModel().getElementAt(i).getKey());
                }
            }
            String str = "";
            for(String value : listValues){
                if(!str.isEmpty()){
                    str += "\t";
                }
                str += value;
            }
            dataMap.put(uri, str);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(EnumerationUI.class).error(e.getMessage());
        }
    }
}
