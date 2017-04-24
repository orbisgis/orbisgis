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
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.sif.common.ContainerItem;
import org.orbisgis.toolboxeditor.WpsClientImpl;
import org.orbisgis.toolboxeditor.utils.ToolBoxIcon;
import org.orbiswps.server.model.BoundingBoxData;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.LayerUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * DataUI implementation for BoundingBoxData.
 * This class generate an interactive UI dedicated to the configuration of a BoundingBoxData.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class BoundingBoxDataUI implements DataUI {

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String BOUNDING_BOX_PROPERTY = "BOUNDING_BOX_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String LAYERUI_PROPERTY = "LAYERUI_PROPERTY";
    private static final String DEFAULT_ELEMENT_PROPERTY = "DEFAULT_ELEMENT_PROPERTY";
    private static final String REFRESH_LIST_LISTENER_PROPERTY = "REFRESH_LIST_LISTENER_PROPERTY";
    private static final String COMBOBOX_PROPERTY = "COMBOBOX_PROPERTY";
    private static final String TEXT_FIELD_PROPERTY = "TEXT_FIELD_PROPERTY";
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(BoundingBoxDataUI.class);

    /** WpsClient using the generated UI. */
    private WpsClientImpl wpsClient;

    @Override
    public void setWpsClient(WpsClientImpl wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        BoundingBoxData boundingBoxData = null;
        //Retrieve the JDBCTableFieldValue and if it is optional
        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            boundingBoxData = (BoundingBoxData)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            return null;
        }

        if(boundingBoxData == null){
            return panel;
        }
        JTextField textField = new JTextField();
        JComboBox<ContainerItem<Object>> comboBox= new JComboBox<>();
        //Build an set the text field for the bounding box coordinate
        textField.setToolTipText(I18N.tr("Enter the coma separated bounding box coordinate : minX,minY,maxX,maxY"));
        textField.getDocument().putProperty(DATA_MAP_PROPERTY, dataMap);
        textField.getDocument().putProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        textField.getDocument().putProperty(COMBOBOX_PROPERTY, comboBox);
        textField.getDocument().putProperty(IS_OPTIONAL_PROPERTY, isOptional);
        textField.getDocument().addDocumentListener(EventHandler.create(DocumentListener.class,
                this,
                "saveDocumentText",
                "document"));
        //Build and set the jComboBox containing all the SRID
        comboBox.setToolTipText(I18N.tr("Select the SRID of the bounding box"));
        if(boundingBoxData.getDefaultCrs() != null) {
            ContainerItem<Object> defaultElement = new ContainerItem<Object>(boundingBoxData.getDefaultCrs(),
                    boundingBoxData.getDefaultCrs());
            comboBox.putClientProperty(DEFAULT_ELEMENT_PROPERTY, defaultElement);
            comboBox.addItem(defaultElement);
        }
        URI uri = URI.create(inputOrOutput.getIdentifier().getValue());
        comboBox.putClientProperty(URI_PROPERTY, uri);
        comboBox.putClientProperty(BOUNDING_BOX_PROPERTY, boundingBoxData);
        comboBox.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        comboBox.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
        if(boundingBoxData.getSupportedCrs().length == 0) {
            MouseListener refreshListListener = EventHandler.create(MouseListener.class, this, "refreshList", "source",
                    "mouseEntered");
            comboBox.putClientProperty(REFRESH_LIST_LISTENER_PROPERTY, refreshListListener);
            comboBox.addMouseListener(refreshListListener);
        }
        else{
            for(String crs : boundingBoxData.getSupportedCrs()){
                if(! crs.equals(boundingBoxData.getDefaultCrs())) {
                    comboBox.addItem(new ContainerItem<Object>(crs, crs));
                }
            }
        }
        comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source",
                "mouseExited"));
        comboBox.addItemListener(EventHandler.create(ItemListener.class, this, "onItemSelection", ""));
        comboBox.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

        //Create the button Browse
        JButton pasteButton = new JButton(ToolBoxIcon.getIcon(ToolBoxIcon.PASTE));
        //"Save" the sourceCA and the JTextField in the button
        pasteButton.putClientProperty(TEXT_FIELD_PROPERTY, textField);
        pasteButton.setBorderPainted(false);
        pasteButton.setContentAreaFilled(false);
        pasteButton.setToolTipText(I18N.tr("Paste the clipboard"));
        pasteButton.setMargin(new Insets(0, 0, 0, 0));
        //Add the listener for the click on the button
        pasteButton.addActionListener(EventHandler.create(ActionListener.class, this, "onPaste", ""));


        //Adds a WaitLayerUI which will be displayed when the toolbox is loading the data

        WaitLayerUI layerUI = new WaitLayerUI();
        JLayer<JComponent> layer = new JLayer<>(comboBox, layerUI);
        panel.add(textField, "growx");
        panel.add(pasteButton, "dock east, growx");
        panel.add(layer, "dock east");
        comboBox.putClientProperty(LAYERUI_PROPERTY, layerUI);

        if(boundingBoxData.getDefaultValue() != null){
            textField.setText(boundingBoxData.getDefaultValue());
        }

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        Map<URI, Object> map = new HashMap<>();
        BoundingBoxData boundingBoxData = null;
        if(inputOrOutput instanceof InputDescriptionType){
            boundingBoxData = (BoundingBoxData)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            boundingBoxData = (BoundingBoxData)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }
        if(boundingBoxData.getDefaultCrs() != null) {
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), boundingBoxData.getDefaultCrs());
        }
        else{
            if(boundingBoxData.getSupportedCrs() != null && boundingBoxData.getSupportedCrs().length != 0) {
                map.put(URI.create(inputOrOutput.getIdentifier().getValue()), boundingBoxData.getSupportedCrs()[0]);
            }
        }
        return map;
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.JDBC_VALUE);
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
     * When the jList is exited, reset the tooltipText delay.
     * @param source JComboBox.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JComboBox<String> comboBox = (JComboBox)source;
        Object tooltipText = comboBox.getClientProperty(TOOLTIP_TEXT_PROPERTY);
        if(tooltipText != null) {
            comboBox.setToolTipText((String)tooltipText);
        }
        Object delay = comboBox.getClientProperty(INITIAL_DELAY_PROPERTY);
        if(delay != null){
            ToolTipManager.sharedInstance().setInitialDelay((int)delay);
        }
    }

    /**
     * Update the JList according to if JDBCTableField parent.
     * @param source the source JList.
     */
    public void refreshList(Object source){
        JComboBox comboBox = (JComboBox)source;
        //Instantiate a worker which will do the list update in a separeted swing thread
        FieldValueWorker worker = new FieldValueWorker(comboBox);
        ExecutorService executorService = wpsClient.getExecutorService();
        if(executorService != null){
            executorService.execute(worker);
        }
        else{
            worker.execute();
        }
        MouseListener listener = (MouseListener)comboBox.getClientProperty(REFRESH_LIST_LISTENER_PROPERTY);
        comboBox.removeMouseListener(listener);
    }

    /**
     * Action done on selecting an item.
     * @param event Item event fired.
     */
    public void onItemSelection(ItemEvent event){
        JComboBox comboBox = (JComboBox)event.getSource();
        URI uri = (URI)comboBox.getClientProperty(URI_PROPERTY);
        HashMap<URI, Object> dataMap = (HashMap<URI, Object>)comboBox.getClientProperty(DATA_MAP_PROPERTY);
        dataMap.put(uri, event.getItem());
    }

    /**
     * SwingWorker doing the list update in a separated Swing Thread
     */
    private class FieldValueWorker extends SwingWorkerPM{
        private JComboBox<ContainerItem<Object>> comboBox;

        public FieldValueWorker(JComboBox<ContainerItem<Object>> comboBox){
            this.comboBox = comboBox;
        }

        @Override
        protected Object doInBackground() throws Exception {
            WaitLayerUI layerUI = (WaitLayerUI)comboBox.getClientProperty(LAYERUI_PROPERTY);
            Object defaultElementObject = comboBox.getClientProperty(DEFAULT_ELEMENT_PROPERTY);
            ContainerItem<Object> defaultElement = null;
            if(defaultElementObject != null){
                defaultElement = (ContainerItem<Object>)defaultElementObject;
            }
            this.setTaskName(I18N.tr("Updating the SRID values"));
            layerUI.start();
            comboBox.removeAllItems();
            List<String> listFields = wpsClient.getSRIDList();
            Collections.sort(listFields);
            int index = 0;
            boolean selectedFound = false;
            for (String field : listFields) {
                if(defaultElement != null && field.equals(defaultElement.getLabel())){
                    selectedFound = true;
                }
                comboBox.addItem(new ContainerItem<Object>(field, field));
                if(!selectedFound) {
                    index++;
                }
            }
            layerUI.stop();


            //If the jList doesn't contains any values or contains only the default item,
            // it mean that the JDBCTableField hasn't been well selected.
            //So show a tooltip text to warn the user.
            if( comboBox.getItemCount() == 0 ||
                    (comboBox.getItemCount() == 1 && (comboBox.getItemAt(0).equals(defaultElement))) ) {
                comboBox.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                comboBox.putClientProperty(TOOLTIP_TEXT_PROPERTY, comboBox.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                comboBox.setToolTipText(I18N.tr("No SRID found."));
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(comboBox,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
            }
            comboBox.setSelectedIndex(index);
            comboBox.repaint();
            return null;
        }
    }

    /**
     * Saves the bounding box value set in the text field.
     * @param document Document of the JTextField
     */
    public void saveDocumentText(Document document){
        try {
            Map<URI, Object> dataMap = (Map<URI, Object>)document.getProperty(DATA_MAP_PROPERTY);
            URI uri = (URI)document.getProperty(URI_PROPERTY);
            JComboBox<ContainerItem<String>> comboBox = (JComboBox<ContainerItem<String>>) document.getProperty(COMBOBOX_PROPERTY);
            String text = comboBox.getSelectedItem().toString() + ";" + document.getText(0, document.getLength());
            dataMap.put(uri, text);
        } catch (BadLocationException e) {
            LoggerFactory.getLogger(BoundingBoxData.class).error(e.getMessage());
        }
    }

    /**
     * Wait layer displayed on doing an update on the list.
     */
    private class WaitLayerUI extends LayerUI<JComponent> implements ActionListener {
        /** Indicates if the layer is running. */
        private boolean mIsRunning;
        /** Indicates if the layer is fading. */
        private boolean mIsFadingOut;
        /** Timer used to refresh the layer. */
        private Timer mTimer;
        /** Angle of the drawn spinner. */
        private int mAngle;
        /** Fade count used to fade the spinner. */
        private int mFadeCount;
        /** Maximum value of the fade count. */
        private int mFadeLimit = 15;

        @Override
        public void paint(Graphics g, JComponent c) {
            int w = c.getWidth();
            int h = c.getHeight();

            // Paint the view.
            super.paint (g, c);

            if (!mIsRunning) {
                return;
            }

            Graphics2D g2 = (Graphics2D)g.create();

            float fade = (float)mFadeCount / (float)mFadeLimit;
            // Gray it out.
            Composite urComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_OVER, .5f * fade));
            g2.fillRect(0, 0, w, h);
            g2.setComposite(urComposite);

            // Paint the wait indicator.
            int s = Math.min(w, h) / 5;
            int cx = w / 2;
            int cy = h / 2;
            g2.setPaint(Color.white);
            //"Loading source" painting
            Font font = g2.getFont().deriveFont(Font.PLAIN, s / 3);
            g2.setFont(font);
            FontMetrics metrics = g2.getFontMetrics(font);
            int w1 = metrics.stringWidth(I18N.tr("Loading"));
            int w2 = metrics.stringWidth(I18N.tr("fields"));
            int h1 = metrics.getHeight();
            g2.drawString(I18N.tr("Loading"), cx - w1 / 2, cy - h1 / 2);
            g2.drawString(I18N.tr("source"), cx - w2 / 2, cy + h1 / 2);
            //waiter painting
            g2.setStroke(new BasicStroke(s / 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.rotate(Math.PI * mAngle / 180, cx, cy);
            for (int i = 1; i < 12; i++) {
                float scale = (11.0f - (float)i) / 11.0f;
                g2.drawLine(cx + s, cy, cx + s * 2, cy);
                g2.rotate(-Math.PI / 6, cx, cy);
                g2.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, scale * fade));
            }
            Toolkit.getDefaultToolkit().sync();
            g2.dispose();
        }

        /**
         * Method called each time the refresh timer is woken up.
         * @param e Unused.
         */
        public void actionPerformed(ActionEvent e) {
            if (mIsRunning) {
                firePropertyChange("tick", 0, 1);
                mAngle += 3;
                if (mAngle >= 360) {
                    mAngle = 0;
                }
                if (mIsFadingOut) {
                    if (--mFadeCount <= 0) {
                        mIsRunning = false;
                        mTimer.stop();
                    }
                }
                else if (mFadeCount < mFadeLimit) {
                    mFadeCount++;
                }
            }
        }

        /**
         * Starts the layer an launch the drawing.
         */
        public void start() {
            if (mIsRunning) {
                return;
            }

            // Run a thread for animation.
            mIsRunning = true;
            mIsFadingOut = false;
            mFadeCount = 0;
            int fps = 24;
            int tick = 1000 / fps;
            mTimer = new Timer(tick, this);
            mTimer.start();
        }


        /**
         * Stops the layer an stops the drawing.
         */
        public void stop() {
            mIsFadingOut = true;
        }

        @Override
        public void applyPropertyChange(PropertyChangeEvent pce, JLayer l) {
            if ("tick".equals(pce.getPropertyName())) {
                l.repaint();
            }
        }
    }
}
