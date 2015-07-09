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

package org.orbisgis.orbistoolbox.view;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;

import javax.swing.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 **/

public class ProcessUIBuilder {

    private Map<Class<? extends DataDescription>, DataUI> dataUIMap;

    public ProcessUIBuilder(){
        dataUIMap = new HashMap<>();
        dataUIMap.put(LiteralData.class, new LiteralDataUI());
    }


    public JComponent buildUI(Process p, Map<URI, Object> dataMap){
        JPanel panel = new JPanel(new MigLayout());

        JLabel title = new JLabel(p.getTitle());
        panel.add(title, "wrap");

        JLabel abstrac = new JLabel(p.getAbstrac());
        panel.add(abstrac, "wrap");


        JLabel inputs = new JLabel("Inputs : ");
        panel.add(inputs, "wrap");

        for(Input i : p.getInput()){
            JLabel inputTitle = new JLabel(i.getTitle());
            panel.add(inputTitle, "wrap");
            JLabel inputAbstrac = new JLabel(i.getAbstrac());
            panel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIMap.get(i.getDataDescription().getClass());
            if(dataUI!=null) {
                panel.add(dataUI.createUI(i, dataMap), "wrap");
            }
        }

        JLabel outputs = new JLabel("Outputs : ");
        panel.add(outputs, "wrap");

        for(Output o : p.getOutput()){
            JLabel inputTitle = new JLabel(o.getTitle());
            panel.add(inputTitle, "wrap");
            JLabel inputAbstrac = new JLabel(o.getAbstrac());
            panel.add(inputAbstrac, "wrap");
        }

        return panel;
    }
}
