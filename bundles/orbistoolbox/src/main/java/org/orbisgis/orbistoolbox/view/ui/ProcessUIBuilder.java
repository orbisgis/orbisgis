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
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;

import javax.swing.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Class Building the UI to configure a process before executing it.
 * The UI is build according the the map containing the DataDescription linked to its DataUI.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessUIBuilder {

    private Map<Class<? extends DataDescription>, DataUI> dataUIMap;

    public ProcessUIBuilder(){
        dataUIMap = new HashMap<>();
        dataUIMap.put(LiteralData.class, new LiteralDataUI());
    }

    /**
     * Build the UI of the given process according to the given data.
     * @param p Process to use.
     * @param dataMap Data to use.
     * @return The UI for the configuration of the process.
     */
    public JComponent buildUI(Process p, Map<URI, Object> dataMap){
        JPanel panel = new JPanel(new MigLayout());



        for(Input i : p.getInput()){
            JPanel inputPanel = new JPanel(new MigLayout());
            inputPanel.setBorder(BorderFactory.createTitledBorder(i.getTitle()));
            JLabel inputAbstrac = new JLabel(i.getAbstrac());
            inputPanel.add(inputAbstrac, "wrap");
            DataUI dataUI = dataUIMap.get(i.getDataDescription().getClass());
            if(dataUI!=null) {
                inputPanel.add(dataUI.createUI(i, dataMap), "wrap");
            }
            panel.add(inputPanel, "wrap");
        }

        return panel;
    }
}
