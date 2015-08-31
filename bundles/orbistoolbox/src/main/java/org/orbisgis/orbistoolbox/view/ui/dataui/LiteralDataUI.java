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
        Map<URI, Object> map = new HashMap<>();

        if(inputOrOutput instanceof Input) {
            List<Input> inputList = new ArrayList<>();

            if (((Input)inputOrOutput).getInput() != null) {
                inputList = ((Input)inputOrOutput).getInput();
            }
            inputList.add((Input)inputOrOutput);

            for (Input i : inputList) {
                if (i.getDataDescription() instanceof LiteralData) {
                    for (LiteralDataDomain ldda : ((LiteralData) i.getDataDescription()).getLiteralDomainType()) {
                        if (ldda.isDefaultDomain()) {
                            if (ldda.getDefaultValue() instanceof Range) {
                                map.put(i.getIdentifier(), ((Range) ldda.getDefaultValue()).getMinimumValue());
                            } else if (ldda.getDefaultValue() instanceof Value) {
                                map.put(i.getIdentifier(), ((Value) ldda.getDefaultValue()).getValue());
                            }
                        }
                    }
                }
            }
        }

        else if(inputOrOutput instanceof Output) {
            List<Output> outputList = new ArrayList<>();

            if (((Output)inputOrOutput).getOutput() != null) {
                outputList = ((Output)inputOrOutput).getOutput();
            }
            outputList.add((Output) inputOrOutput);

            for (Output o : outputList) {
                if (o.getDataDescription() instanceof LiteralData) {
                    for (LiteralDataDomain ldda : ((LiteralData) o.getDataDescription()).getLiteralDomainType()) {
                        if (ldda.isDefaultDomain()) {
                            if (ldda.getDefaultValue() instanceof Range) {
                                map.put(o.getIdentifier(), ((Range) ldda.getDefaultValue()).getMinimumValue());
                            } else if (ldda.getDefaultValue() instanceof Value) {
                                map.put(o.getIdentifier(), ((Value) ldda.getDefaultValue()).getValue());
                            }
                        }
                    }
                }
            }
        }
        return map;
    }

    @Override
    public JComponent createUI(Output output, Map<URI, Object> dataMap) {
        return null;
    }

    @Override
    public JComponent createUI(Input input, Map<URI, Object> dataMap) {
        LiteralData literalData = (LiteralData)input.getDataDescription();
        JComboBox<String> comboBox = new JComboBox<>();
        for(LiteralDataDomain ldd : literalData.getLiteralDomainType()){
            comboBox.addItem(ldd.getDataType().name().toLowerCase());
        }
        JPanel panel = new JPanel(new MigLayout("fill"));
        panel.add(comboBox, "wrap");

        JComponent dataField = new JPanel();
        panel.add(dataField);

        comboBox.putClientProperty("dataField", dataField);
        comboBox.putClientProperty("uri", input.getIdentifier());
        comboBox.putClientProperty("dataMap", dataMap);
        comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onBoxChange", "source"));

        onBoxChange(comboBox);

        return panel;
    }

    /**
     * Call on selecting the type of data to use.
     * @param source The comboBox containing the data type to use.
     */
    public void onBoxChange(Object source){
        Map<URI, Object> dataMap = (Map<URI, Object>) ((JComponent) source).getClientProperty("dataMap");;
        URI uri = (URI) ((JComponent) source).getClientProperty("uri");
        String s = (String) ((JComboBox)source).getSelectedItem();
        JComponent dataComponent;
        switch(DataType.valueOf(s.toUpperCase())){
            case BOOLEAN:
                dataComponent = new JComboBox<Boolean>();
                ((JComboBox<Boolean>)dataComponent).addItem(Boolean.TRUE);
                ((JComboBox<Boolean>)dataComponent).addItem(Boolean.FALSE);
                dataComponent.putClientProperty("type", DataType.BOOLEAN);
                dataComponent.putClientProperty("dataMap",dataMap);
                dataComponent.putClientProperty("uri", uri);

                if(dataMap.get(uri).equals(Boolean.TRUE) || dataMap.get(uri).equals(Boolean.FALSE)){
                    ((JComboBox<Boolean>)dataComponent).setSelectedItem(dataMap.get(uri));
                }

                ((JComboBox<Boolean>)dataComponent).addActionListener(EventHandler.create(
                        ActionListener.class,
                        this,
                        "onDataChanged",
                        "source"));

                break;
            case BYTE:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.BYTE);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case INTEGER:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.INTEGER);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case LONG:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Long.MIN_VALUE, Long.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.LONG);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case SHORT:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.SHORT);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case UNSIGNED_BYTE:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, 0, Character.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.UNSIGNED_BYTE);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case DOUBLE:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Double.MIN_VALUE, Double.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.DOUBLE);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case FLOAT:
                dataComponent = new JSpinner(new SpinnerNumberModel(0, Float.MIN_VALUE, Float.MAX_VALUE, 1));
                dataComponent.putClientProperty("type", DataType.FLOAT);
                dataComponent.putClientProperty("dataMap",((JComboBox) source).getClientProperty("dataMap"));
                dataComponent.putClientProperty("uri",((JComboBox) source).getClientProperty("uri"));
                ((JSpinner)dataComponent).setValue(dataMap.get(uri));
                ((JSpinner)dataComponent).addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                break;
            case STRING:
            default:
                dataComponent = new JTextArea(6, 20);
                dataComponent.setBorder(BorderFactory.createLineBorder(Color.lightGray));
                ((JTextArea)dataComponent).getDocument().putProperty("dataMap", ((JComboBox) source).getClientProperty("dataMap"));
                ((JTextArea)dataComponent).getDocument().putProperty("uri", ((JComboBox) source).getClientProperty("uri"));
                ((JTextArea)dataComponent).setText((String)dataMap.get(uri));
                ((JTextArea)dataComponent).getDocument().addDocumentListener(EventHandler.create(
                        DocumentListener.class,
                        this,
                        "onDocumentChanged",
                        "document",
                        "insertUpdate"));
                ((JTextArea)dataComponent).getDocument().addDocumentListener(EventHandler.create(
                        DocumentListener.class,
                        this,
                        "onDocumentChanged",
                        "document",
                        "removeUpdate"));
                break;
        }
        JPanel panel = (JPanel) ((JComboBox) source).getClientProperty("dataField");
        panel.removeAll();
        panel.add(dataComponent);
    }

    /**
     * Call if the TextArea for the String type is changed.
     * @param document
     */
    public void onDocumentChanged(Document document){

        Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty("dataMap");
        URI uri = (URI) document.getProperty("uri");
        dataMap.remove(uri);
        try {
            dataMap.put(uri, document.getText(0, document.getLength()));
        } catch (BadLocationException e) {
            e.printStackTrace();
            dataMap.put(uri, "");
        }
    }

    /**
     * Call if the JComponent where the value is defined is changed.
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
