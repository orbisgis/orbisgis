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
import org.orbisgis.orbistoolbox.model.LiteralDataDomain;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * UI for the definition of the LiteralData inputs.
 *
 * @author Sylvain PALOMINOS
 **/

public class LiteralDataUI implements DataUI {

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> uriDefaultValueMap = new HashMap<>();
        List<DescriptionType> descriptionTypeList = new ArrayList<>();
        DataDescription dataDescription = null;
        URI identifier;

        //Gets the descriptionType list if the argument is an input
        if(inputOrOutput instanceof Input){
            Input input = (Input) inputOrOutput;
            if(input.getInput() != null){
                descriptionTypeList.addAll(input.getInput());
            }
            else{
                descriptionTypeList.add(input);
            }
        }

        //Gets the descriptionType list if the argument is an output
        if(inputOrOutput instanceof Output){
            Output output = (Output) inputOrOutput;
            if(output.getOutput() != null){
                descriptionTypeList.addAll(output.getOutput());
            }
            else{
                descriptionTypeList.add(output);
            }
        }

        //Fill the map
        for(DescriptionType dt : descriptionTypeList){
            identifier = dt.getIdentifier();
            if(dt instanceof Input){
                dataDescription = ((Input) dt).getDataDescription();
            }
            else if(dt instanceof  Output){
                dataDescription = ((Output) dt).getDataDescription();
            }
            //Find in the dataDescription the default LiteralDataDomain an retrieve its default value
            if (dataDescription instanceof LiteralData) {
                for (LiteralDataDomain ldda : ((LiteralData) dataDescription).getLiteralDomainType()) {
                    if (ldda.isDefaultDomain()) {
                        //If the default value is a Range, get the minimum as default value
                        if (ldda.getDefaultValue() instanceof Range) {
                            uriDefaultValueMap.put(identifier, ((Range) ldda.getDefaultValue()).getMinimumValue());
                        } else if (ldda.getDefaultValue() instanceof Value) {
                            uriDefaultValueMap.put(identifier, ((Value) ldda.getDefaultValue()).getValue());
                        }
                    }
                }
            }
        }
        return uriDefaultValueMap;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        DataDescription dataDescription = null;
        if(inputOrOutput instanceof Input){
            dataDescription = ((Input) inputOrOutput).getDataDescription();
        }
        if(inputOrOutput instanceof Output){
            dataDescription = ((Output) inputOrOutput).getDataDescription();
        }
        if(dataDescription instanceof LiteralData) {
            LiteralData ld = (LiteralData)dataDescription;
            DataType dataType = DataType.STRING;
            if(ld.getValue() != null && ld.getValue().getDataType()!= null) {
                dataType = ld.getValue().getDataType();
            }
            switch (dataType) {
                case STRING:
                    return ToolBoxIcon.getIcon("string");
                case UNSIGNED_BYTE:
                case SHORT:
                case LONG:
                case BYTE:
                case INTEGER:
                case DOUBLE:
                case FLOAT:
                    return ToolBoxIcon.getIcon("number");
                case BOOLEAN:
                    return ToolBoxIcon.getIcon("boolean");
                default:
                    return ToolBoxIcon.getIcon("undefined");
            }
        }
        return ToolBoxIcon.getIcon("undefined");
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));

        //If the descriptionType is an input, add a comboBox to select the input type and according to the type,
        // add a second JComponent to write the input value
        if(inputOrOutput instanceof Input){
            Input input = (Input)inputOrOutput;
            LiteralData literalData = (LiteralData)input.getDataDescription();
            //JComboBox with the input type
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem(literalData.getValue().getDataType().name());
            /*
            for(LiteralDataDomain ldd : literalData.getLiteralDomainType()){
                comboBox.addItem(ldd.getDataType().name().toLowerCase());
            }
            */
            panel.add(comboBox, "wrap");

            //JPanel containing the component to set the input value
            JComponent dataField = new JPanel();
            panel.add(dataField);

            comboBox.putClientProperty("dataField", dataField);
            comboBox.putClientProperty("uri", input.getIdentifier());
            comboBox.putClientProperty("dataMap", dataMap);
            comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onBoxChange", "source"));

            onBoxChange(comboBox);
        }
        else{

        }
        return panel;
    }

    /**
     * Call on selecting the type of data to use.
     * For each type registered in the JComboBox adapts the dataField panel.
     * Also add a listener to save the data value set by the user
     * @param source The comboBox containing the data type to use.
     */
    public void onBoxChange(Object source){
        JComboBox comboBox = (JComboBox) source;
        Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty("dataMap");
        URI uri = (URI) comboBox.getClientProperty("uri");
        String s = (String) comboBox.getSelectedItem();
        JComponent dataComponent;
        switch(DataType.valueOf(s.toUpperCase())){
            case BOOLEAN:
                //Instantiate the component
                dataComponent = new JComboBox<Boolean>();
                ((JComboBox)dataComponent).addItem(Boolean.TRUE);
                ((JComboBox)dataComponent).addItem(Boolean.FALSE);
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.BOOLEAN);
                dataComponent.putClientProperty("dataMap",dataMap);
                dataComponent.putClientProperty("uri", uri);
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri).equals(Boolean.TRUE) || dataMap.get(uri).equals(Boolean.FALSE)){
                    ((JComboBox)dataComponent).setSelectedItem(dataMap.get(uri));
                }
                ((JComboBox)dataComponent).addActionListener(EventHandler.create(
                        ActionListener.class,
                        this,
                        "onDataChanged",
                        "source"));

                break;
            case BYTE:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.BYTE);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case INTEGER:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.INTEGER);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case LONG:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.LONG);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case SHORT:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.SHORT);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case UNSIGNED_BYTE:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0, 0, Character.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.UNSIGNED_BYTE);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case DOUBLE:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0D, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.DOUBLE);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri", comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case FLOAT:
                //Instantiate the component
                dataComponent = new JSpinner(new SpinnerNumberModel(0F, Float.MIN_VALUE, Float.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                dataComponent.putClientProperty("type", DataType.FLOAT);
                dataComponent.putClientProperty("dataMap",comboBox.getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                if(dataMap.get(uri)!=null) {
                    ((JSpinner) dataComponent).setValue(dataMap.get(uri));
                }
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case STRING:
            default:
                //Instantiate the component
                dataComponent = new JTextArea(6, 20);
                dataComponent.setBorder(BorderFactory.createLineBorder(Color.lightGray));
                //Put the data type, the dataMap and the uri as properties
                Document doc = ((JTextArea) dataComponent).getDocument();
                doc.putProperty("dataMap", comboBox.getClientProperty("dataMap"));
                doc.putProperty("uri", comboBox.getClientProperty("uri"));
                //Set the default value and adds the listener for saving the value set by the user
                ((JTextArea)dataComponent).setText((String)dataMap.get(uri));
                doc.addDocumentListener(EventHandler.create(
                        DocumentListener.class,
                        this,
                        "onDocumentChanged",
                        "document",
                        "insertUpdate"));
                doc.addDocumentListener(EventHandler.create(
                        DocumentListener.class,
                        this,
                        "onDocumentChanged",
                        "document",
                        "removeUpdate"));
                break;
        }
        //Adds to the dataField the dataComponent
        JPanel panel = (JPanel) comboBox.getClientProperty("dataField");
        panel.removeAll();
        panel.add(dataComponent);
    }

    /**
     * Call if the TextArea for the String type is changed and save the new text in the dataMap.
     * @param document
     */
    public void onDocumentChanged(Document document){

        Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty("dataMap");
        URI uri = (URI) document.getProperty("uri");
        dataMap.remove(uri);
        try {
            dataMap.put(uri, document.getText(0, document.getLength()));
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(LiteralDataUI.class).error(e.getMessage());
            dataMap.put(uri, "");
        }
    }

    /**
     * Call if the JComponent where the value is defined is changed and save the new value in the dataMap
     * @param source
     */
    public void onDataChanged(Object source){
        Map<URI, Object> dataMap = (Map<URI, Object>) ((JComponent)source).getClientProperty("dataMap");
        URI uri = (URI) ((JComponent)source).getClientProperty("uri");

        switch((DataType)((JComponent)source).getClientProperty("type")){
            case BOOLEAN:
                JComboBox<Boolean> comboBox = (JComboBox<Boolean>)source;
                dataMap.remove(uri);
                dataMap.put(uri, comboBox.getSelectedItem());
                break;
            case BYTE:
            case INTEGER:
            case LONG:
            case SHORT:
            case UNSIGNED_BYTE:
            case DOUBLE:
            case FLOAT:
                JSpinner spinner = (JSpinner)source;
                dataMap.remove(uri);
                dataMap.put(uri, spinner.getValue());
                break;
        }
    }
}
