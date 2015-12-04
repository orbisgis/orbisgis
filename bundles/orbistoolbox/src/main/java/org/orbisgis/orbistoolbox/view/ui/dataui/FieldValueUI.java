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
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * UI generator associated to the FieldValue
 *
 * @author Sylvain PALOMINOS
 **/

public class FieldValueUI implements DataUI{
    private static final int MAX_JLIST_ROW_COUNT = 10;
    private static final int MIN_JLIST_ROW_COUNT = 1;

    private ToolBox toolBox;

    public void setToolBox(ToolBox toolBox){
        this.toolBox = toolBox;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));
        FieldValue fieldValue = null;
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

        if(inputOrOutput.getResume().isEmpty()){
            panel.add(new JLabel(inputOrOutput.getTitle()), "growx, wrap");
        }
        else {
            panel.add(new JLabel("Select " + inputOrOutput.getResume()), "growx, wrap");
        }
        JList list = new JList(new DefaultListModel());
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
        list.putClientProperty("uri", inputOrOutput.getIdentifier());
        list.putClientProperty("fieldValue", fieldValue);
        list.putClientProperty("dataMap", dataMap);
        list.putClientProperty("isOptional", isOptional);
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "refreshList", "source", "mouseEntered"));
        list.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("fieldvalue");
    }

    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JList<String> list = (JList)source;
        if(list.getSelectedIndices().length == 0) {
            list.setToolTipText((String)list.getClientProperty("toolTipText"));
            ToolTipManager.sharedInstance().setInitialDelay((int)list.getClientProperty("initialDelay"));
        }
    }

    public void refreshList(Object source){
        JList list = (JList)source;
        FieldValue fieldValue = (FieldValue)list.getClientProperty("fieldValue");
        HashMap<URI, Object> dataMap = (HashMap)list.getClientProperty("dataMap");
        if(fieldValue.isDataFieldModified()) {
            fieldValue.setDataFieldModified(false);
            String tableName = dataMap.get(fieldValue.getDataStoreIdentifier()).toString();
            String fieldName = dataMap.get(fieldValue.getDataFieldIdentifier()).toString();
            DefaultListModel<String> model = (DefaultListModel<String>)list.getModel();
            model.removeAllElements();
            List<String> listFields = ToolBox.getFieldValueList(tableName, fieldName);
            for (String field : listFields) {
                model.addElement(field);
            }
            if(listFields.size() < MAX_JLIST_ROW_COUNT){
                list.setVisibleRowCount(listFields.size());
            }
            else{
                list.setVisibleRowCount(MAX_JLIST_ROW_COUNT);
            }
        }

        if(list.getSelectedIndices().length == 0) {
            list.putClientProperty("initialDelay", ToolTipManager.sharedInstance().getInitialDelay());
            list.putClientProperty("toolTipText", list.getToolTipText());
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(2500);
            String fieldValueStr = fieldValue.getDataFieldIdentifier().toString();
            list.setToolTipText("First configure the DataField : " + fieldValueStr.substring(fieldValueStr.lastIndexOf(":")+1));
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(list,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
        }
        else{
            list.setSelectedIndices(new int[]{0});
        }
        list.revalidate();
    }

    public void onListSelection(Object source){
        JList list = (JList)source;
        List<String> listValues = new ArrayList<>();

        if(list.getSelectedIndices().length == 0){
            listValues = null;
        }
        else {
            for (int i : list.getSelectedIndices()) {
                listValues.add(list.getModel().getElementAt(i).toString());
            }
        }
        URI uri = (URI)list.getClientProperty("uri");
        HashMap<URI, Object> dataMap = (HashMap)list.getClientProperty("dataMap");
        dataMap.put(uri, listValues);
    }
}
