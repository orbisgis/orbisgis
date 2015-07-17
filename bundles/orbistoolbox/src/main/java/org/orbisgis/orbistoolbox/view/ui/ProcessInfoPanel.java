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

package org.orbisgis.orbistoolbox.view.ui;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.model.DataType;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple panel containing the basic information about a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessInfoPanel extends JPanel {

    private JLabel titleContentLabel;
    private JLabel abstracContentLabel;
    private JPanel inputPanel;
    private JPanel outputPanel;

    public ProcessInfoPanel(){
        super();
        this.setLayout(new MigLayout());

        titleContentLabel = new JLabel();
        abstracContentLabel = new JLabel();

        JPanel processPanel = new JPanel(new MigLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("Title :"));
        processPanel.add(titleContentLabel, "wrap, align left");
        abstracContentLabel.setFont(abstracContentLabel.getFont().deriveFont(Font.ITALIC));
        processPanel.add(abstracContentLabel, "wrap, align left");


        inputPanel = new JPanel(new MigLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Inputs :"));
        //inputPanel.add(inputListContentLabel, "align left");

        outputPanel = new JPanel(new MigLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Outputs :"));
        //outputPanel.add(outputListContentLabel, "align left");

        this.add(processPanel, "growx, wrap");
        this.add(inputPanel, "growx, wrap");
        this.add(outputPanel, "growx, wrap");
    }

    public void updateComponent(){
        this.revalidate();
    }

    public void setTitle(String title) {
        if(title == null){
            titleContentLabel.setText("\t-\t");
        }
        else{
            titleContentLabel.setText(title);
        }
    }

    public void setAbstrac(String abstrac) {
        if(abstrac == null){
            abstracContentLabel.setText("\t-\t");
        }
        else{
            abstracContentLabel.setText(abstrac);
        }
    }

    public void setInputList(List<String> inputList, List<DataType> dataTypeList, List<String> abstractList) {
        inputPanel.removeAll();
        if(inputList == null){
            inputPanel.add(new JLabel("-"), "align center, wrap");
        }
        else{
            for(int i = 0; i< inputList.size(); i++){
                if(dataTypeList.get(i) == null) {
                    inputPanel.add(new JLabel(ToolBoxIcon.getIcon("undefined")));
                }
                else {
                    switch (dataTypeList.get(i)) {
                        case STRING:
                            inputPanel.add(new JLabel(ToolBoxIcon.getIcon("string")));
                            break;
                        case UNSIGNED_BYTE:
                        case SHORT:
                        case LONG:
                        case BYTE:
                        case INTEGER:
                        case DOUBLE:
                        case FLOAT:
                            inputPanel.add(new JLabel(ToolBoxIcon.getIcon("number")));
                            break;
                        case BOOLEAN:
                            inputPanel.add(new JLabel(ToolBoxIcon.getIcon("boolean")));
                            break;
                        default:
                            break;
                    }
                }
                inputPanel.add(new JLabel(inputList.get(i)), "align left, wrap");
                if(abstractList.get(i) != null) {
                    JLabel abstrac = new JLabel(abstractList.get(i));
                    abstrac.setFont(abstrac.getFont().deriveFont(Font.ITALIC));
                    inputPanel.add(abstrac, "span 2, wrap");
                }
                else {
                    inputPanel.add(new JLabel("-"), "align center, span 2, wrap");
                }
            }
        }
    }

    public void setOutputList(List<String> outputList, List<DataType> dataTypeList, List<String> abstractList) {
        outputPanel.removeAll();
        if(outputList == null){
            outputPanel.add(new JLabel("-"), "align center, wrap");
        }
        else{
            for(int i = 0; i< outputList.size(); i++){
                if(dataTypeList.get(i) == null) {
                    outputPanel.add(new JLabel(ToolBoxIcon.getIcon("undefined")));
                }
                else {
                    switch (dataTypeList.get(i)) {
                        case STRING:
                            outputPanel.add(new JLabel(ToolBoxIcon.getIcon("string")));
                            break;
                        case UNSIGNED_BYTE:
                        case SHORT:
                        case LONG:
                        case BYTE:
                        case INTEGER:
                        case DOUBLE:
                        case FLOAT:
                            outputPanel.add(new JLabel(ToolBoxIcon.getIcon("number")));
                            break;
                        case BOOLEAN:
                            outputPanel.add(new JLabel(ToolBoxIcon.getIcon("boolean")));
                            break;
                        default:
                            outputPanel.add(new JLabel(ToolBoxIcon.getIcon("undefined")));
                            break;
                    }
                }
                outputPanel.add(new JLabel(outputList.get(i)), "align left, wrap");
                if(abstractList.get(i) != null) {
                    JLabel abstrac = new JLabel(abstractList.get(i));
                    abstrac.setFont(abstrac.getFont().deriveFont(Font.ITALIC));
                    outputPanel.add(abstrac, "span 2, wrap");
                }
                else {
                    outputPanel.add(new JLabel("-"), "align center, span 2, wrap");
                }
            }
        }
    }
}
