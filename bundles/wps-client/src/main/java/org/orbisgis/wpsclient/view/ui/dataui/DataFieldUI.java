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
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.wpsclient.WpsClient;
import org.orbisgis.wpsclient.view.utils.ToolBoxIcon;
import org.orbisgis.sif.common.ContainerItem;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.EventHandler;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * User Interface generator for the DataField input/output.
 *
 * @author Sylvain PALOMINOS
 **/

public class DataFieldUI implements DataUI{

    private WpsClient wpsClient;

    public void setWpsClient(WpsClient wpsClient){
        this.wpsClient = wpsClient;
    }

    @Override
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap) {
        JPanel panel = new JPanel(new MigLayout("fill"));
        DataField dataField = null;
        boolean isOptional = false;
        //Retrieve the DataField
        //If it is an input, find if it is optional.
        if(inputOrOutput instanceof Input){
            dataField = (DataField)((Input)inputOrOutput).getDataDescription();
            if(((Input)inputOrOutput).getMinOccurs() == 0){
                isOptional = true;
            }
        }
        else if(inputOrOutput instanceof Output){
            dataField = (DataField)((Output)inputOrOutput).getDataDescription();
        }

        if(dataField == null){
            return panel;
        }

        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setBackground(Color.WHITE);
        String defaultItem = "Select a field";
        comboBox.addItem(defaultItem);
        comboBox.putClientProperty("uri", inputOrOutput.getIdentifier());
        comboBox.putClientProperty("dataField", dataField);
        comboBox.putClientProperty("dataMap", dataMap);
        comboBox.putClientProperty("isOptional", isOptional);
        comboBox.putClientProperty("defaultItem", defaultItem);
        comboBox.putClientProperty("fieldTitle", inputOrOutput.getTitle());
        comboBox.addItemListener(EventHandler.create(ItemListener.class, this, "onItemSelected", "source"));
        comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxEntered", "source", "mouseEntered"));
        comboBox.addPopupMenuListener(EventHandler.create(PopupMenuListener.class, this, "onComboBoxEntered", "source"));
        comboBox.addMouseListener(EventHandler.create(MouseListener.class, this, "onComboBoxExited", "source", "mouseExited"));
        comboBox.setToolTipText(inputOrOutput.getResume());
        panel.add(comboBox, "growx, wrap");

        if(isOptional){
            String nullItem = "";
            comboBox.addItem(nullItem);
        }

        return panel;
    }

    @Override
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput) {
        return new HashMap<>();
    }

    @Override
    public ImageIcon getIconFromData(DescriptionType inputOrOutput) {
        return ToolBoxIcon.getIcon("datafield");
    }

    /**
     * When the comboBox is exited, reset the tooltipText delay.
     * @param source JComboBox.
     */
    public void onComboBoxExited(Object source){
        //Retrieve the client properties
        JComboBox<ContainerItem<String>> comboBox = (JComboBox)source;
        if(comboBox.getItemCount() == 0) {
            comboBox.setToolTipText((String)comboBox.getClientProperty("toolTipText"));
            ToolTipManager.sharedInstance().setInitialDelay((int)comboBox.getClientProperty("initialDelay"));
        }
    }

    /**
     * Update the JComboBox according to if DataStore parent.
     * @param source the source JComboBox.
     */
    public void onComboBoxEntered(Object source){
        JComboBox<String> comboBox = (JComboBox)source;
        String defaultItem = comboBox.getClientProperty("defaultItem").toString();
        comboBox.removeItem(defaultItem);
        DataField dataField = (DataField)comboBox.getClientProperty("dataField");
        HashMap<URI, Object> dataMap = (HashMap)comboBox.getClientProperty("dataMap");
        boolean isOptional = (boolean)comboBox.getClientProperty("isOptional");
        //If the DataStore related to the DataField has been modified, reload the dataField values
        if(dataField.isSourceModified()) {
            dataField.setSourceModified(false);
            String tableName = ((URI) dataMap.get(dataField.getDataStoreIdentifier())).getFragment();
            comboBox.removeAllItems();
            for (String field : WpsService.getTableFieldList(tableName, dataField.getFieldTypeList())) {
                comboBox.addItem(field);
            }
            if(isOptional) {
                comboBox.addItem("");
            }
        }

        //If the comboBox doesn't contains any values, it mean that the DataStore hasn't been well selected.
        //So show a tooltip text to warn the user.
        if(comboBox.getItemCount() == 0) {
            comboBox.putClientProperty("initialDelay", ToolTipManager.sharedInstance().getInitialDelay());
            comboBox.putClientProperty("toolTipText", comboBox.getToolTipText());
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().setDismissDelay(2500);
            String dataFieldStr = dataField.getDataStoreIdentifier().toString();
            comboBox.setToolTipText("First configure the DataStore : " +
                    dataFieldStr.substring(dataFieldStr.lastIndexOf(":")+1));
            ToolTipManager.sharedInstance().mouseMoved(
                    new MouseEvent(comboBox,MouseEvent.MOUSE_MOVED,System.currentTimeMillis(),0,0,0,0,false));
        }
        //Else try to select the good field
        else{
            String title = comboBox.getClientProperty("fieldTitle").toString().toUpperCase();
            for(int i = 0; i < comboBox.getItemCount(); i++) {
                if(title.contains(comboBox.getItemAt(i))){
                    comboBox.setSelectedIndex(i);
                    break;
                }
            }
        }

        comboBox.revalidate();
    }

    public void onItemSelected(Object source){
        if(source instanceof JComboBox){
            JComboBox<String> comboBox = (JComboBox)source;
            if(comboBox.getSelectedItem() != null) {
                DataField dataField = (DataField) comboBox.getClientProperty("dataField");
                Map<URI, Object> dataMap = (Map<URI, Object>) comboBox.getClientProperty("dataMap");
                URI uri = (URI) comboBox.getClientProperty("uri");
                boolean isOptional = (boolean)comboBox.getClientProperty("isOptional");
                dataMap.remove(uri);
                if (isOptional && comboBox.getSelectedItem().toString().isEmpty()) {
                    dataMap.put(uri, null);
                }
                else{
                    dataMap.put(uri, comboBox.getSelectedItem());
                }
                //Tells to the fieldValues that the datafield has been modified
                for (FieldValue fieldValue : dataField.getListFieldValue()) {
                    fieldValue.setDataFieldModified(true);
                }
            }
        }
    }
}
