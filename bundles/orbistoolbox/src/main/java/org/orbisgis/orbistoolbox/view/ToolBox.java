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

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.orbisgis.orbistoolbox.controller.ProcessManager;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.sif.components.actions.ActionCommands;
import org.orbisgis.sif.components.actions.ActionDockingListener;
import org.orbisgis.sif.components.actions.DefaultAction;
import org.orbisgis.sif.docking.DockingPanel;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.beans.EventHandler;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

@Component(service = DockingPanel.class)
public class ToolBox implements DockingPanel {

    private static final String ADD_SOURCE = "ADD_SOURCE";

    private DockingPanelParameters parameters;
    private ActionCommands dockingActions;
    private ProcessManager processManager;

    private ToolBoxPanel toolBoxPanel;

    @Activate
    public void init(){
        toolBoxPanel = new ToolBoxPanel(this);
        processManager = new ProcessManager();

        dockingActions = new ActionCommands();

        parameters = new DockingPanelParameters();
        parameters.setName("orbistoolbox");
        parameters.setTitle("OrbisToolBox");
        parameters.setTitleIcon(ToolBoxIcon.getIcon("orbistoolbox"));
        parameters.setCloseable(true);

        dockingActions.addAction(
                new DefaultAction(
                        ADD_SOURCE,
                        "Add source",
                        "Add a local source",
                        ToolBoxIcon.getIcon("add_source"),
                        EventHandler.create(ActionListener.class, toolBoxPanel, "addSource"),
                        null
                )
        );

        parameters.setDockActions(dockingActions.getActions());
        dockingActions.addPropertyChangeListener(new ActionDockingListener(parameters));
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return parameters;
    }

    @Override
    public JComponent getComponent() {
        return toolBoxPanel;
    }

    public void selectProcess(File f){
        Process p = processManager.getProcess(f);
        List<String> inputList = new ArrayList<>();
        List<String> outputList = new ArrayList<>();
        for(Input i : p.getInput()){
            String type = "";
            if(i.getDataDescription() instanceof LiteralData){
                for(LiteralDataDomain ldd : ((LiteralData) i.getDataDescription()).getLiteralDomainType()){
                    type += ldd.getDataType().name().toLowerCase() + " ";
                }
                type += ": ";
            }
            else{
                type = i.getDataDescription().getClass().getSimpleName() + " : ";
            }
            inputList.add(type + i.getTitle());
        }
        for(Output o : p.getOutput()){
            String type = "";
            if(o.getDataDescription() instanceof LiteralData){
                for(LiteralDataDomain ldd : ((LiteralData) o.getDataDescription()).getLiteralDomainType()){
                    type += ldd.getDataType() + " ";
                }
                type += ": ";
            }
            else{
                type = o.getDataDescription().getClass().getSimpleName() + " : ";
            }
            outputList.add(type + o.getTitle());
        }
        toolBoxPanel.setProcessInfo(p.getTitle(), p.getAbstrac(), inputList, outputList);
    }
}
