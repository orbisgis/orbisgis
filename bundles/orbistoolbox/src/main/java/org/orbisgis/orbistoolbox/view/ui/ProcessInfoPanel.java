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

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple panel containing the basic information about a process.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessInfoPanel extends JPanel {

    private String title;
    private String abstrac;
    private List<String> inputList;
    private List<String> outputList;

    private JLabel titleContentLabel;
    private JLabel abstracContentLabel;
    private JLabel inputListContentLabel;
    private JLabel outputListContentLabel;

    public ProcessInfoPanel(){
        super();
        this.setLayout(new MigLayout("", "", "[] 3 [] 15 [] 3 [] 15 [] 3 [] 15 [] 3 [] 15"));
        inputList = new ArrayList<>();
        outputList = new ArrayList<>();

        JLabel titleLabel = new JLabel("Title :");
        titleContentLabel = new JLabel();
        JLabel abstracLabel = new JLabel("Abstract :");
        abstracContentLabel = new JLabel();
        JLabel inputListLabel = new JLabel("Inputs :");
        inputListContentLabel = new JLabel();
        JLabel outputListLabel = new JLabel("Outputs :");
        outputListContentLabel = new JLabel();

        setTitle(null);
        setAbstrac(null);
        setInputList(null);
        setOutputList(null);

        updateComponent();

        this.add(titleLabel, "wrap");
        this.add(titleContentLabel, "wrap");
        this.add(abstracLabel, "wrap");
        this.add(abstracContentLabel, "wrap");
        this.add(inputListLabel, "wrap");
        this.add(inputListContentLabel, "wrap");
        this.add(outputListLabel, "wrap");
        this.add(outputListContentLabel, "wrap");
    }

    public void updateComponent(){
        titleContentLabel.setText(title);
        abstracContentLabel.setText(abstrac);
        String input = "";
        for(String s : inputList){
            input += s+"\n";
        }
        inputListContentLabel.setText(input);
        String output = "";
        for(String s : outputList){
            output += s+"\n";
        }
        outputListContentLabel.setText(output);
    }

    public void setTitle(String title) {
        if(title == null){
            this.title = "-";
        }
        else{
            this.title = title;
        }
    }

    public void setAbstrac(String abstrac) {
        if(abstrac == null){
            this.abstrac = "-";
        }
        else{
            this.abstrac = abstrac;
        }
    }

    public void setInputList(List<String> inputList) {
        if(inputList == null){
            this.inputList.add("-");
        }
        else{
            while(!this.inputList.isEmpty()){
                this.inputList.remove(0);
            }
            this.inputList = inputList;
        }
    }

    public void setOutputList(List<String> outputList) {
        if(outputList == null){
            this.outputList.add("-");
        }
        else{
            while(!this.outputList.isEmpty()){
                this.outputList.remove(0);
            }
            this.outputList = outputList;
        }
    }
}
