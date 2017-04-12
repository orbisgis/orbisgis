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
import net.opengis.ows._2.AllowedValues;
import net.opengis.ows._2.RangeType;
import net.opengis.ows._2.ValueType;
import net.opengis.wps._2_0.*;
import net.opengis.wps._2_0.DescriptionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.*;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.EventHandler;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * DataUI implementation for LiteralData.
 * This class generate an interactive UI dedicated to the configuration of a LiteralData.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/
public class LiteralDataUI implements DataUI {

    /** Size constants **/
    private static final int MAX_ROW_NUMBER = 5;
    private static final int MIN_ROW_NUMBER = 1;

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String DATA_FIELD_PROPERTY = "DATA_FIELD_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String TYPE_PROPERTY = "TYPE_PROPERTY";
    private static final String BOOLEAN_PROPERTY = "BOOLEAN_PROPERTY";
    private static final String TEXT_AREA_PROPERTY = "TEXT_AREA_PROPERTY";
    private static final String VERTICAL_BAR_PROPERTY = "VERTICAL_BAR_PROPERTY";
    private static final String LITERAL_DATA_PROPERTY = "LITERAL_DATA_PROPERTY";
    private static final String ORIENTATION_PROPERTY = "ORIENTATION_PROPERTY";
    private static final String DEFAULT_VALUE_PROPERTY = "DEFAULT_VALUE_PROPERTY";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LiteralDataUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> uriDefaultValueMap = new HashMap<>();
        DataDescriptionType dataDescription = null;
        URI identifier = URI.create(inputOrOutput.getIdentifier().getValue());

        //Gets the dataDescription
        if(inputOrOutput instanceof InputDescriptionType){
            InputDescriptionType input = (InputDescriptionType) inputOrOutput;
            dataDescription = input.getDataDescription().getValue();
        }
        if(inputOrOutput instanceof OutputDescriptionType){
            OutputDescriptionType output = (OutputDescriptionType) inputOrOutput;
            dataDescription = output.getDataDescription().getValue();
        }

        if (dataDescription instanceof LiteralDataType) {
            //Find in the dataDescription the default LiteralDataDomain an retrieve its default value
            for (LiteralDataDomainType ldda : ((LiteralDataType) dataDescription).getLiteralDataDomain()) {
                if (ldda.isSetDefaultValue()) {
                    //If the default value is a Range, get the minimum as default value
                    if (ldda.getDefaultValue().isSetValue()) {
                        uriDefaultValueMap.put(identifier, (ldda.getDefaultValue().getValue()));
                    }
                }
            }
        }
        return uriDefaultValueMap;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        DataDescriptionType dataDescription = null;
        if(inputOrOutput instanceof InputDescriptionType){
            dataDescription = ((InputDescriptionType) inputOrOutput).getDataDescription().getValue();
        }
        if(inputOrOutput instanceof OutputDescriptionType){
            dataDescription = ((OutputDescriptionType) inputOrOutput).getDataDescription().getValue();
        }
        if(dataDescription instanceof LiteralDataType) {
            LiteralDataType ld = (LiteralDataType)dataDescription;
            DataType dataType = DataType.STRING;
            //TODO manage the dataType with the icons
            /*if(ld.getValue() != null && ld.getValue().getDataType()!= null) {
                dataType = ld.getValue().getDataType();
            }*/
            switch (dataType) {
                case STRING:
                    return ToolBoxIcon.getIcon(ToolBoxIcon.STRING);
                case UNSIGNED_BYTE:
                case SHORT:
                case LONG:
                case BYTE:
                case INTEGER:
                case DOUBLE:
                case FLOAT:
                    return ToolBoxIcon.getIcon(ToolBoxIcon.NUMBER);
                case BOOLEAN:
                    return ToolBoxIcon.getIcon(ToolBoxIcon.BOOLEAN);
                default:
                    return ToolBoxIcon.getIcon(ToolBoxIcon.UNDEFINED);
            }
        }
        return ToolBoxIcon.getIcon(ToolBoxIcon.UNDEFINED);
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));

        //If the descriptionType is an input, add a comboBox to select the input type and according to the type,
        // add a second JComponent to write the input value
        if(inputOrOutput instanceof InputDescriptionType){
            InputDescriptionType input = (InputDescriptionType)inputOrOutput;
            LiteralDataType literalData = (LiteralDataType)input.getDataDescription().getValue();
            //JComboBox with the input type
            JComboBox<String> comboBox = new JComboBox<>();
            comboBox.addItem(literalData.getLiteralDataDomain().get(0).getDataType().getValue());

            //JPanel containing the component to set the input value
            JComponent dataFieldComp = new JPanel(new MigLayout("fill, ins 0, gap 0"));
            URI uri = URI.create(input.getIdentifier().getValue());
            comboBox.putClientProperty(LITERAL_DATA_PROPERTY, literalData);
            comboBox.putClientProperty(DATA_FIELD_PROPERTY, dataFieldComp);
            comboBox.putClientProperty(URI_PROPERTY, uri);
            comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
            comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, input.getMinOccurs().equals(new BigInteger("0")));
            comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, input.getAbstract().get(0).getValue());
            comboBox.putClientProperty(ORIENTATION_PROPERTY, orientation);
            comboBox.addActionListener(EventHandler.create(ActionListener.class, this, "onBoxChange", "source"));
            comboBox.setBackground(Color.WHITE);
            comboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

            Object defaultValueObject = null;
            for(LiteralDataType.LiteralDataDomain domain : literalData.getLiteralDataDomain()){
                if(domain.isDefault() && domain.getDefaultValue() != null){
                    defaultValueObject = domain.getDefaultValue().getValue();
                }
            }

            dataMap.put(uri, defaultValueObject);
            onBoxChange(comboBox);
            if(input.getMinOccurs().equals(new BigInteger("0"))) {
                dataMap.remove(uri);
            }

            if(comboBox.getItemCount() > 1){
                panel.add(comboBox, "growx, wrap");
            }
            panel.add(dataFieldComp, "growx, wrap");
            return panel;
        }
        return null;
    }

    /**
     * Call on selecting the type of data to use.
     * For each type registered in the JComboBox adapts the jdbcTableField panel.
     * Also add a listener to save the data value set by the user
     * @param source The comboBox containing the data type to use.
     */
    public void onBoxChange(Object source){
        JComboBox comboBox = (JComboBox) source;
        Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty(DATA_MAP_PROPERTY);
        URI uri = (URI) comboBox.getClientProperty(URI_PROPERTY);
        boolean isOptional = (boolean)comboBox.getClientProperty(IS_OPTIONAL_PROPERTY);
        LiteralDataType literalData = (LiteralDataType)comboBox.getClientProperty(LITERAL_DATA_PROPERTY);
        String tooltip = (String)comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY);
        Orientation orientation = (Orientation)comboBox.getClientProperty(ORIENTATION_PROPERTY);
        String s = (String) comboBox.getSelectedItem();
        JComponent dataComponent;
        switch(DataType.valueOf(s.toUpperCase())) {
            case BOOLEAN:
                //Instantiate the component
                dataComponent = new JPanel(new MigLayout("ins 0, gap 0"));
                JRadioButton falseButton = new JRadioButton(I18N.tr("FALSE"));
                JRadioButton trueButton = new JRadioButton(I18N.tr("TRUE"));
                falseButton.setToolTipText(comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY).toString());
                falseButton.setToolTipText(tooltip);
                trueButton.setToolTipText(comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY).toString());
                trueButton.setToolTipText(tooltip);
                ButtonGroup group = new ButtonGroup();
                group.add(falseButton);
                group.add(trueButton);
                dataComponent.add(trueButton);
                dataComponent.add(falseButton);
                //Put the data type, the dataMap and the uri as properties
                falseButton.putClientProperty(TYPE_PROPERTY, DataType.BOOLEAN);
                falseButton.putClientProperty(DATA_MAP_PROPERTY, dataMap);
                falseButton.putClientProperty(URI_PROPERTY, uri);
                falseButton.putClientProperty(BOOLEAN_PROPERTY, false);
                trueButton.putClientProperty(TYPE_PROPERTY, DataType.BOOLEAN);
                trueButton.putClientProperty(DATA_MAP_PROPERTY, dataMap);
                trueButton.putClientProperty(URI_PROPERTY, uri);
                trueButton.putClientProperty(BOOLEAN_PROPERTY, true);
                dataComponent.putClientProperty(TYPE_PROPERTY, DataType.BOOLEAN);
                dataComponent.putClientProperty(DATA_MAP_PROPERTY, dataMap);
                dataComponent.putClientProperty(URI_PROPERTY, uri);
                //Set the default value and adds the listener for saving the value set by the user
                falseButton.setSelected(true);
                if (dataMap.get(uri) != null) {
                    if (Boolean.parseBoolean(dataMap.get(uri).toString())) {
                        trueButton.setSelected(true);
                    }
                    dataComponent.putClientProperty(BOOLEAN_PROPERTY, dataMap.get(uri));
                }
                else{
                    dataComponent.putClientProperty(BOOLEAN_PROPERTY, false);
                }
                falseButton.addActionListener(EventHandler.create(
                        ActionListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                trueButton.addActionListener(EventHandler.create(
                        ActionListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                onDataChanged(dataComponent);
                break;
            case BYTE:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner byteSpinner = new JSpinner(new SpinnerNumberModel(0, Byte.MIN_VALUE, Byte.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                byteSpinner.putClientProperty(TYPE_PROPERTY, DataType.BYTE);
                byteSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                byteSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                byteSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    byteSpinner.setValue(Byte.parseByte(dataMap.get(uri).toString()));
                } else {
                    byteSpinner.setValue(0);
                }
                byteSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                JComboBox allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 0) {
                    allowedValuesBox.putClientProperty("spinner", byteSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(byteSpinner, "growx, wrap");
                onDataChanged(byteSpinner);

                break;
            case INTEGER:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner intSpinner = new JSpinner(new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                intSpinner.putClientProperty(TYPE_PROPERTY, DataType.INTEGER);
                intSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                intSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                intSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    intSpinner.setValue(Integer.parseInt(dataMap.get(uri).toString()));
                } else {
                    intSpinner.setValue(0);
                }
                intSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", intSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(intSpinner, "growx, span");
                onDataChanged(intSpinner);
                break;
            case LONG:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner longSpinner = new JSpinner(new SpinnerNumberModel(0, Long.MIN_VALUE, Long.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                longSpinner.putClientProperty(TYPE_PROPERTY, DataType.LONG);
                longSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                longSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                longSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    longSpinner.setValue(Long.parseLong(dataMap.get(uri).toString()));
                } else {
                    longSpinner.setValue(0);
                }
                longSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", longSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(longSpinner, "growx, wrap");
                onDataChanged(longSpinner);
                break;
            case SHORT:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner shortSpinner = new JSpinner(new SpinnerNumberModel(0, Short.MIN_VALUE, Short.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                shortSpinner.putClientProperty(TYPE_PROPERTY, DataType.SHORT);
                shortSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                shortSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                shortSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    shortSpinner.setValue(Short.parseShort(dataMap.get(uri).toString()));
                } else {
                    shortSpinner.setValue(0);
                }
                shortSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", shortSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(shortSpinner, "growx, wrap");
                onDataChanged(shortSpinner);
                break;
            case UNSIGNED_BYTE:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner uByteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Character.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                uByteSpinner.putClientProperty(TYPE_PROPERTY, DataType.UNSIGNED_BYTE);
                uByteSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                uByteSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                uByteSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    uByteSpinner.setValue(dataMap.get(uri).toString().charAt(0));
                } else {
                    uByteSpinner.setValue(0);
                }
                uByteSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", uByteSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(uByteSpinner, "growx, wrap");
                onDataChanged(uByteSpinner);
                break;
            case DOUBLE:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner doubleSpinner = new JSpinner(new SpinnerNumberModel(0D, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1));
                //Put the data type, the dataMap and the uri as properties
                doubleSpinner.putClientProperty(TYPE_PROPERTY, DataType.DOUBLE);
                doubleSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                doubleSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                doubleSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    doubleSpinner.setValue(Double.parseDouble(dataMap.get(uri).toString()));
                } else {
                    doubleSpinner.setValue(0);
                }
                doubleSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", doubleSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(doubleSpinner, "growx, wrap");
                onDataChanged(doubleSpinner);
                break;
            case FLOAT:
                dataComponent = new JPanel(new MigLayout("fill"));
                //Instantiate the component
                JSpinner floatSpinner = new JSpinner(new SpinnerNumberModel(0F, Float.MIN_VALUE, Float.MAX_VALUE, 1));
                //Put the data type, the dataMap and the uri as properties
                floatSpinner.putClientProperty(TYPE_PROPERTY, DataType.FLOAT);
                floatSpinner.putClientProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                floatSpinner.putClientProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                floatSpinner.setToolTipText(tooltip);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null && !dataMap.get(uri).toString().isEmpty()) {
                    floatSpinner.setValue(Float.parseFloat(dataMap.get(uri).toString()));
                } else {
                    floatSpinner.setValue(0);
                }
                floatSpinner.addChangeListener(EventHandler.create(
                        ChangeListener.class,
                        this,
                        "onDataChanged",
                        "source"));
                //If there is more than one literal data domain, adds a JComboBox to allow the user to select the
                // desired one.
                allowedValuesBox = createDomainComboBox(literalData);
                if (allowedValuesBox != null && allowedValuesBox.getItemCount() > 1) {
                    allowedValuesBox.putClientProperty("spinner", floatSpinner);
                    allowedValuesBox.addItemListener(
                            EventHandler.create(ItemListener.class, this, "onDomainSelected", ""));
                    dataComponent.add(allowedValuesBox, "dock west");
                    //Run a false event to simulated the selection of the selected value of the JComboBox
                    ItemEvent event = new ItemEvent(
                            allowedValuesBox, 0, allowedValuesBox.getSelectedItem(), ItemEvent.SELECTED);
                    onDomainSelected(event);
                }
                dataComponent.add(floatSpinner, "growx, wrap");
                onDataChanged(floatSpinner);
                break;
            case STRING:
            default:
                //Instantiate the component
                CustomTextArea textArea = new CustomTextArea();
                textArea.setToolTipText(tooltip);
                textArea.setLineWrap(true);
                textArea.setRows(MIN_ROW_NUMBER);
                //Put the data type, the dataMap and the uri as properties
                Document doc = textArea.getDocument();
                doc.putProperty(DATA_MAP_PROPERTY, comboBox.getClientProperty(DATA_MAP_PROPERTY));
                doc.putProperty(URI_PROPERTY, comboBox.getClientProperty(URI_PROPERTY));
                doc.putProperty(IS_OPTIONAL_PROPERTY, isOptional);
                String defaultValue = "";
                for(LiteralDataType.LiteralDataDomain domain : literalData.getLiteralDataDomain()){
                    if(domain.isDefault() && domain.getDefaultValue() != null && domain.getDefaultValue().getValue() != null){
                        defaultValue = domain.getDefaultValue().getValue();
                    }
                }
                doc.putProperty(DEFAULT_VALUE_PROPERTY, defaultValue);
                //Set the default value and adds the listener for saving the value set by the user
                if (dataMap.get(uri) != null) {
                    textArea.setText((String) dataMap.get(uri));
                } else {
                    textArea.setText("");
                }
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
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.getViewport().addChangeListener(EventHandler.create(
                        ChangeListener.class, this, "onViewportStateChange", ""));
                scrollPane.getViewport().putClientProperty(TEXT_AREA_PROPERTY, textArea);
                scrollPane.getViewport().putClientProperty(VERTICAL_BAR_PROPERTY, scrollPane.getVerticalScrollBar());
                //scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                JPanel panel = new JPanel(new MigLayout("fill"));
                panel.add(scrollPane, "growx");
                if (orientation.equals(Orientation.VERTICAL)){
                    JButton paste = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.PASTE));
                    paste.putClientProperty(TEXT_AREA_PROPERTY, textArea);
                    paste.addActionListener(EventHandler.create(ActionListener.class, this, "onPaste", ""));
                    paste.setBorderPainted(false);
                    paste.setContentAreaFilled(false);
                    paste.setToolTipText(I18N.tr("Paste the clipboard"));
                    panel.add(paste, "dock east");
                }
                dataComponent = panel;
                break;
        }
        dataComponent.setToolTipText(comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY).toString());
        //Adds to the jdbcTableField the dataComponent
        JPanel panel = (JPanel) comboBox.getClientProperty(DATA_FIELD_PROPERTY);
        panel.removeAll();
        panel.add(dataComponent, "growx, wrap");
        if(isOptional) {
            dataMap.remove(uri);
        }
    }

    private class CustomTextArea extends JTextArea {

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return new Dimension(1, (int)super.getPreferredScrollableViewportSize().getHeight()); }

        @Override
        public boolean getScrollableTracksViewportWidth() { return true; }
    }

    /**
     * Create a JComboBox containing all the available literal data domain to allow the user to select the desired one.
     * The JComboBox contains strings which are composed this way :
     * - In the case of a ValueType : "value"
     * - In the case of a RangeType : "min;value;max;spacing". If there is no "value" defined, uses the "min".
     * @param literalData LiteralData used to build the JComboBox.
     * @return A JComboBox containing the literalData domain.
     */
    private JComboBox<String> createDomainComboBox(LiteralDataType literalData){
        //If there is no domain, return null;
        if(literalData.getLiteralDataDomain().isEmpty()) {
            return null;
        }
        JComboBox<String> allowedValuesBox = new JComboBox<>();
        //For each domain, add an item to the JComboBox
        for(LiteralDataType.LiteralDataDomain literalDataDomain : literalData.getLiteralDataDomain()){
            if(literalDataDomain != null){
                if(literalDataDomain.getAllowedValues() != null){
                    //For each allowed values, if there are not empty, add the item to the JComboBox
                    AllowedValues allowedValues = literalDataDomain.getAllowedValues();
                    if(!allowedValues.getValueOrRange().isEmpty()) {
                        //Test each value
                        for(Object value : allowedValues.getValueOrRange()){
                            //If the value is a ValueType, add the item "value"
                            if(value instanceof ValueType){
                                String str = ((ValueType)value).getValue();
                                allowedValuesBox.addItem(str);
                            }
                            //If the value is a RangeType, add the item "min;value;max;spacing".
                            // If there is no "value" defined, uses the "min".
                            if(value instanceof RangeType){
                                RangeType range = (RangeType)value;
                                String defaultValue = range.getMinimumValue().getValue();
                                if(!literalDataDomain.getDefaultValue().getValue().isEmpty()){
                                    defaultValue = literalDataDomain.getDefaultValue().getValue();
                                }
                                String str = range.getMinimumValue().getValue()+";"+
                                        defaultValue+";"+
                                        range.getMaximumValue().getValue()+";"+
                                        range.getSpacing().getValue();
                                allowedValuesBox.addItem(str);
                            }
                        }
                    }
                }
            }
        }
        return allowedValuesBox;
    }


    /**
     * Action done on clicking on the paste button.
     * @param ae ActionEvent get on clicking on the paste button.
     */
    public void onPaste(ActionEvent ae){
        JTextArea textArea = ((JTextArea)((JButton)ae.getSource()).getClientProperty(TEXT_AREA_PROPERTY));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                textArea.setText((String)contents.getTransferData(DataFlavor.stringFlavor));
            }
            catch (UnsupportedFlavorException | IOException ignored){
            }
        }
    }

    /**
     * Call when the state of the viewport of the JScrollPane of the textArea state change.
     * It uses the vertical bar properties to detect when the user need more lines to write.
     * @param e ChangeEvent.
     */
    public void onViewportStateChange(ChangeEvent e){
        JViewport vp = (JViewport)e.getSource();
        JTextArea textArea = (JTextArea)vp.getClientProperty(TEXT_AREA_PROPERTY);
        JScrollBar vertical = (JScrollBar)vp.getClientProperty(VERTICAL_BAR_PROPERTY);
        if(textArea.getRows()<MAX_ROW_NUMBER && vertical.getValue()>0 && vertical.getMaximum()>vertical.getVisibleAmount()){
            textArea.setRows(MAX_ROW_NUMBER);
        }
    }

    /**
     * Call if the TextArea for the String type is changed and save the new text in the dataMap.
     * @param document TextArea document.
     */
    public void onDocumentChanged(Document document){

        Map<URI, Object> dataMap = (Map<URI, Object>) document.getProperty(DATA_MAP_PROPERTY);
        URI uri = (URI) document.getProperty(URI_PROPERTY);
        boolean isOptional = (boolean) document.getProperty(IS_OPTIONAL_PROPERTY);
        String defaultValue = (String) document.getProperty(DEFAULT_VALUE_PROPERTY);
        try {
            String text = document.getText(0, document.getLength());
            if(text .isEmpty() && isOptional && !defaultValue.isEmpty()){
                text = defaultValue;
            }
            else if(text.isEmpty()){
                text = null;
            }
            dataMap.put(uri, text);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(LiteralDataUI.class).error(e.getMessage());
            dataMap.put(uri, "");
        }
    }

    /**
     * Call if the JComponent where the value is defined is changed and save the new value in the dataMap.
     * @param source Source JComponent.
     */
    public void onDataChanged(Object source){
        Map<URI, Object> dataMap = (Map<URI, Object>) ((JComponent)source).getClientProperty(DATA_MAP_PROPERTY);
        URI uri = (URI) ((JComponent)source).getClientProperty(URI_PROPERTY);

        switch((DataType)((JComponent)source).getClientProperty(TYPE_PROPERTY)){
            case BOOLEAN:
                dataMap.put(uri, ((JComponent)source).getClientProperty(BOOLEAN_PROPERTY));
                break;
            case INTEGER:
            case BYTE:
            case LONG:
            case SHORT:
            case UNSIGNED_BYTE:
            case DOUBLE:
            case FLOAT:
                JSpinner spinner = (JSpinner)source;
                dataMap.put(uri, spinner.getValue());
                break;
        }
    }

    /**
     * When a domain is selected, sets the JSpinner model with the min, max, spacing and value from the domein.
     * @param event Event thrown when a domain is selected.
     */
    public void onDomainSelected(ItemEvent event){
        //Get the domain JComboBox
        if(event.getSource() instanceof JComboBox){
            JComboBox domainSpinner = (JComboBox)event.getSource();
            Object object = domainSpinner.getClientProperty("spinner");
            //Get the value JSpinner
            if(object instanceof JSpinner){
                JSpinner valueSpinner = (JSpinner)object;
                SpinnerModel model = null;
                //If there is only one value (in case of a ValueType), use it as value, min and max.
                String[] parsed = event.getItem().toString().split(";");
                if(parsed.length == 1){
                    parsed = new String[]{parsed[0],parsed[0],parsed[0],parsed[0]};
                }
                //According to the data type, create the model for the spinner
                switch((DataType)valueSpinner.getClientProperty(TYPE_PROPERTY)){
                    case INTEGER:
                        model = new SpinnerNumberModel(
                                Integer.parseInt(parsed[0]),
                                Integer.parseInt(parsed[1]),
                                Integer.parseInt(parsed[2]),
                                Integer.parseInt(parsed[3]));
                        break;
                    case BYTE:
                        model = new SpinnerNumberModel(
                                Byte.valueOf(parsed[0]),
                                Byte.valueOf(parsed[1]),
                                Byte.valueOf(parsed[2]),
                                Byte.valueOf(parsed[3]));
                        break;
                    case LONG:
                        model = new SpinnerNumberModel(
                                Long.parseLong(parsed[0]),
                                Long.parseLong(parsed[1]),
                                Long.parseLong(parsed[2]),
                                Long.parseLong(parsed[3]));
                        break;
                    case SHORT:
                        model = new SpinnerNumberModel(
                                Short.parseShort(parsed[0]),
                                Short.parseShort(parsed[1]),
                                Short.parseShort(parsed[2]),
                                Short.parseShort(parsed[3]));
                        break;
                    case UNSIGNED_BYTE:
                        model = new SpinnerNumberModel(
                                parsed[0].charAt(0),
                                parsed[1].charAt(0),
                                parsed[2].charAt(0),
                                parsed[3].charAt(0));
                        break;
                    case DOUBLE:
                        model = new SpinnerNumberModel(
                                Double.parseDouble(parsed[0]),
                                Double.parseDouble(parsed[1]),
                                Double.parseDouble(parsed[2]),
                                Double.parseDouble(parsed[3]));
                        break;
                    case FLOAT:
                        model = new SpinnerNumberModel(
                                Float.parseFloat(parsed[0]),
                                Float.parseFloat(parsed[1]),
                                Float.parseFloat(parsed[2]),
                                Float.parseFloat(parsed[3]));
                        break;
                }
                if(model != null) {
                    valueSpinner.setModel(model);
                }
                onDataChanged(valueSpinner);
            }
        }
    }
}
