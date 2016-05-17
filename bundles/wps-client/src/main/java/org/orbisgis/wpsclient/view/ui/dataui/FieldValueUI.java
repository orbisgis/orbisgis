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
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsservice.model.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.math.BigInteger;
import java.net.URI;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * DataUI implementation for FieldValue.
 * This class generate an interactive UI dedicated to the configuration of a FieldValue.
 * The interface generated will be used in the ProcessEditor.
 *
 * @author Sylvain PALOMINOS
 **/

public class FieldValueUI implements DataUI{

    /** Size constants **/
    private static final int MAX_JLIST_ROW_COUNT = 10;
    private static final int MIN_JLIST_ROW_COUNT = 1;

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String FIELD_VALUE_PROPERTY = "FIELD_VALUE_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";
    private static final String LAYERUI_PROPERTY = "LAYERUI_PROPERTY";

    /** WpsClient using the generated UI. */
    private WpsClient wpsClient;

    @Override
    public void setWpsClient(WpsClient wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill, ins 0, gap 0"));
        FieldValue fieldValue = null;
        //Retrieve the FieldValue and if it is optional
        boolean isOptional = false;
        if(inputOrOutput instanceof InputDescriptionType){
            fieldValue = (FieldValue)((InputDescriptionType)inputOrOutput).getDataDescription().getValue();
            if(((InputDescriptionType)inputOrOutput).getMinOccurs().equals(new BigInteger("0"))){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            fieldValue = (FieldValue)((OutputDescriptionType)inputOrOutput).getDataDescription().getValue();
        }

        if(fieldValue == null){
            return panel;
        }
        //Build and set the JList containing all the field values
        JList<String> list = new JList<>(new DefaultListModel<String>());
        if(fieldValue.getMuliSelection()){
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(MIN_JLIST_ROW_COUNT);
        list.putClientProperty(URI_PROPERTY, URI.create(inputOrOutput.getIdentifier().getValue()));
        list.putClientProperty(FIELD_VALUE_PROPERTY, fieldValue);
        list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "refreshList", "source", "mouseEntered"));
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));
        list.setToolTipText(inputOrOutput.getAbstract().get(0).getValue());

        //Adds a WaitLayerUI which will be displayed when the toolbox is loading the data
        JScrollPane listScroller = new JScrollPane(list);
        WaitLayerUI layerUI = new WaitLayerUI();
        JLayer<JComponent> layer = new JLayer<>(listScroller, layerUI);
        panel.add(layer, "growx, wrap");
        list.putClientProperty(LAYERUI_PROPERTY, layerUI);

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon(ToolBoxIcon.FIELD_VALUE);
    }

    /**
     * When the jList is exited, reset the tooltipText delay.
     * @param source JComboBox.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JList<String> list = (JList)source;
        Object tooltipText = list.getClientProperty(TOOLTIP_TEXT_PROPERTY);
        if(tooltipText != null) {
            list.setToolTipText((String)tooltipText);
        }
        Object delay = list.getClientProperty(INITIAL_DELAY_PROPERTY);
        if(delay != null){
            ToolTipManager.sharedInstance().setInitialDelay((int)delay);
        }
    }

    /**
     * Update the JList according to if DataField parent.
     * @param source the source JList.
     */
    public void refreshList(Object source){
        //Instantiate a worker which will do the list update in a separeted swing thread
        FieldValueWorker worker = new FieldValueWorker((JList)source);
        ExecutorService executorService = wpsClient.getExecutorService();
        if(executorService != null){
            executorService.execute(worker);
        }
        else{
            worker.execute();
        }
    }

    public void onListSelection(Object source){
        JList list = (JList)source;
        URI uri = (URI)list.getClientProperty(URI_PROPERTY);
        HashMap<URI, Object> dataMap = (HashMap<URI, Object>)list.getClientProperty(DATA_MAP_PROPERTY);
        List<String> listValues = new ArrayList<>();

        if(list.getSelectedIndices().length == 0){
            dataMap.put(uri, null);
            return;
        }
        else {
            for (int i : list.getSelectedIndices()) {
                listValues.add(list.getModel().getElementAt(i).toString().replaceAll("'", "''"));
            }
        }
        dataMap.put(uri, listValues.toArray(new String[listValues.size()]));
    }

    /**
     * SwingWorker doing the list update in a separated Swing Thread
     */
    private class FieldValueWorker extends SwingWorkerPM{
        private JList<String> list;

        public FieldValueWorker(JList<String> list){
            this.list = list;
        }

        @Override
        protected Object doInBackground() throws Exception {
            WaitLayerUI layerUI = (WaitLayerUI)list.getClientProperty(LAYERUI_PROPERTY);
            FieldValue fieldValue = (FieldValue)list.getClientProperty(FIELD_VALUE_PROPERTY);
            HashMap<URI, Object> dataMap = (HashMap<URI, Object>)list.getClientProperty(DATA_MAP_PROPERTY);
            boolean isOptional = (boolean)list.getClientProperty(IS_OPTIONAL_PROPERTY);
            //If the DataField related to the FieldValue has been modified, reload the dataField values
            if(fieldValue.isDataFieldModified()) {
                fieldValue.setDataFieldModified(false);
                String tableName = null;
                String fieldName = null;
                if(fieldValue.getDataFieldIdentifier().toString().contains("$")){
                    String[] split = fieldValue.getDataFieldIdentifier().toString().split("\\$");
                    if(split.length == 4) {
                        tableName = split[1]+"."+split[2];
                        fieldName = split[3];
                    }
                    else if(split.length == 3){
                        tableName = split[1];
                        fieldName = split[2];
                    }
                    else{
                        return null;
                    }
                }
                else if (dataMap.get(fieldValue.getDataStoreIdentifier()) != null){
                    tableName = ((URI) dataMap.get(fieldValue.getDataStoreIdentifier())).getSchemeSpecificPart();
                    fieldName = dataMap.get(fieldValue.getDataFieldIdentifier()).toString();
                }
                if(tableName != null && fieldName != null) {
                    layerUI.start();
                    //First retrieve the good field name with the good case.
                    List<String> fieldList = wpsClient.getWpsService().getTableFieldList(tableName,
                            new ArrayList<DataType>(), new ArrayList<DataType>());
                    for(String field : fieldList){
                        if(field.equalsIgnoreCase(fieldName)){
                            fieldName = field;
                        }
                    }
                    //Retrieve the rowSet reading the table from the wpsService.
                    DefaultListModel<String> model = (DefaultListModel<String>) list.getModel();
                    model.removeAllElements();
                    List<String> listFields = wpsClient.getWpsService().getFieldValueList(tableName, fieldName);
                    Collections.sort(listFields);
                    for (String field : listFields) {
                        model.addElement(field);
                    }
                    if (listFields.size() < MAX_JLIST_ROW_COUNT) {
                        list.setVisibleRowCount(listFields.size());
                    } else {
                        list.setVisibleRowCount(MAX_JLIST_ROW_COUNT);
                    }
                    if (!isOptional && list.getModel().getSize() > 0) {
                        list.setSelectedIndex(0);
                    }
                    layerUI.stop();
                }
            }

            //If the jList doesn't contains any values, it mean that the DataField hasn't been well selected.
            //So show a tooltip text to warn the user.
            if(list.getModel().getSize() == 0) {
                list.putClientProperty(INITIAL_DELAY_PROPERTY, ToolTipManager.sharedInstance().getInitialDelay());
                list.putClientProperty(TOOLTIP_TEXT_PROPERTY, list.getToolTipText());
                ToolTipManager.sharedInstance().setInitialDelay(0);
                ToolTipManager.sharedInstance().setDismissDelay(2500);
                String fieldValueStr = fieldValue.getDataFieldIdentifier().toString();
                if(fieldValueStr.contains("$")){
                    String[] split = fieldValueStr.split("\\$");
                    if(split.length == 3){
                        fieldValueStr = split[1]+"."+split[2];
                    }
                    else if(split.length == 4){
                        fieldValueStr = split[1]+"."+split[2]+"."+split[3];
                    }
                    list.setToolTipText("First configure the DataField : " + fieldValueStr);
                }
                else {
                    list.setToolTipText("First configure the DataField : " +
                            fieldValueStr.substring(fieldValueStr.lastIndexOf(":") + 1));
                }
                ToolTipManager.sharedInstance().mouseMoved(
                        new MouseEvent(list,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
            }
            return null;
        }
    }

    /**
     * Wait layer displayed on doing an update on the list.
     */
    class WaitLayerUI extends LayerUI<JComponent> implements ActionListener {
        private boolean mIsRunning;
        private boolean mIsFadingOut;
        private Timer mTimer;

        private int mAngle;
        private int mFadeCount;
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
            int w1 = metrics.stringWidth("Loading");
            int w2 = metrics.stringWidth("fields");
            int h1 = metrics.getHeight();
            g2.drawString("Loading", cx - w1 / 2, cy - h1 / 2);
            g2.drawString("source", cx - w2 / 2, cy + h1 / 2);
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
