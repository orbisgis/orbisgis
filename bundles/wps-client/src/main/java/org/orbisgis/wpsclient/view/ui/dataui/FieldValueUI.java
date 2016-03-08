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
import org.orbisgis.corejdbc.ReadRowSet;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.FieldValueListModel;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsservice.model.FieldValue;
import org.orbisgis.wpsservice.model.Input;
import org.orbisgis.wpsservice.model.Output;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;

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
    private static final int MIN_WIDTH_CELL = 1;
    private static final int MAX_HEIGHT_CELL = 15;

    /** Constant used to pass object as client property throw JComponents **/
    private static final String DATA_MAP_PROPERTY = "DATA_MAP_PROPERTY";
    private static final String URI_PROPERTY = "URI_PROPERTY";
    private static final String FIELD_VALUE_PROPERTY = "FIELD_VALUE_PROPERTY";
    private static final String IS_OPTIONAL_PROPERTY = "IS_OPTIONAL_PROPERTY";
    private static final String INITIAL_DELAY_PROPERTY = "INITIAL_DELAY_PROPERTY";
    private static final String TOOLTIP_TEXT_PROPERTY = "TOOLTIP_TEXT_PROPERTY";

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
        if(inputOrOutput instanceof Input){
            fieldValue = (FieldValue)((Input)inputOrOutput).getDataDescription();
            if(((Input)inputOrOutput).getMinOccurs() == 0){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof Output){
            fieldValue = (FieldValue)((Output)inputOrOutput).getDataDescription();
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
        JScrollPane listScroller = new JScrollPane(list);
        panel.add(listScroller, "growx, wrap");
        list.putClientProperty(URI_PROPERTY, inputOrOutput.getIdentifier());
        list.putClientProperty(FIELD_VALUE_PROPERTY, fieldValue);
        list.putClientProperty(DATA_MAP_PROPERTY, dataMap);
        list.putClientProperty(IS_OPTIONAL_PROPERTY, isOptional);
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "refreshList", "source", "mouseEntered"));
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));
        list.setToolTipText(inputOrOutput.getResume());

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
        JList<String> list = (JList<String>)source;
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
                    return;
                }
            }
            else if (dataMap.get(fieldValue.getDataStoreIdentifier()) != null){
                tableName = ((URI) dataMap.get(fieldValue.getDataStoreIdentifier())).getSchemeSpecificPart();
                fieldName = dataMap.get(fieldValue.getDataFieldIdentifier()).toString();
            }
            if(tableName != null && fieldName != null) {
                ReadRowSet readRowSet = wpsClient.getWpsService().getFieldAndReadRowSet(tableName, fieldName);
                if(readRowSet != null) {
                    FieldValueListModel model = new FieldValueListModel(readRowSet, fieldName);
                    list.setModel(model);
                    list.setFixedCellWidth(MIN_WIDTH_CELL);
                    list.setFixedCellHeight(MAX_HEIGHT_CELL);

                    if (model.getSize() < MAX_JLIST_ROW_COUNT) {
                        list.setVisibleRowCount(model.getSize());
                    } else {
                        list.setVisibleRowCount(MAX_JLIST_ROW_COUNT);
                    }
                }
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
        //list.revalidate();
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
}
