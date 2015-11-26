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
import java.awt.*;
import java.awt.event.FocusListener;
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

    private ToolBox toolBox;

    public void setToolBox(ToolBox toolBox){
        this.toolBox = toolBox;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));
        FieldValue fieldValue = null;
        if(inputOrOutput instanceof Input){
            fieldValue = (FieldValue)((Input)inputOrOutput).getDataDescription();
        }
        else if(inputOrOutput instanceof Output){
            fieldValue = (FieldValue)((Output)inputOrOutput).getDataDescription();
        }

        if(fieldValue == null){
            return panel;
        }

        if(inputOrOutput.getResume().isEmpty()){
            panel.add(new JLabel(inputOrOutput.getTitle()), "wrap");
        }
        else {
            panel.add(new JLabel("Select " + inputOrOutput.getResume()), "wrap");
        }
        JList list = new JList(new DefaultListModel());
        if(fieldValue.getMuliSelection()){
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        }
        else {
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setMinimumSize(new Dimension(250, 80));
        panel.add(listScroller);
        list.putClientProperty("uri", inputOrOutput.getIdentifier());
        list.putClientProperty("fieldValue", fieldValue);
        list.putClientProperty("dataMap", dataMap);
        list.addFocusListener(EventHandler.create(FocusListener.class, this, "onGainingFocus", "source"));
        list.addListSelectionListener(EventHandler.create(ListSelectionListener.class, this, "onListSelection", "source"));

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("fieldValue");
    }

    public void onGainingFocus(Object source){
        JList list = (JList)source;
        FieldValue fieldValue = (FieldValue)list.getClientProperty("fieldValue");
        HashMap<URI, Object> dataMap = (HashMap)list.getClientProperty("dataMap");
        if(fieldValue.isDataFieldModified()) {
            fieldValue.setDataFieldModified(false);
            String tableName = dataMap.get(fieldValue.getDataStoreIdentifier()).toString();
            String fieldName = dataMap.get(fieldValue.getDataFieldIdentifier()).toString();
            DefaultListModel model = (DefaultListModel)list.getModel();
            model.removeAllElements();
            for (String field : ToolBox.getFieldValueList(tableName, fieldName)) {
                model.addElement(field);
            }
        }
        list.revalidate();
    }

    public void onListSelection(Object source){
        JList list = (JList)source;
        List<String> listValues = new ArrayList<>();
        for(int i : list.getSelectedIndices()){
            listValues.add(list.getModel().getElementAt(i).toString());
        }
        URI uri = (URI)list.getClientProperty("uri");
        HashMap<URI, Object> dataMap = (HashMap)list.getClientProperty("dataMap");
        dataMap.put(uri, listValues);
    }
}
