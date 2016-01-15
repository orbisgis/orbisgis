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

package org.orbisgis.orbistoolbox.view.utils.editor.log;

import net.miginfocom.swing.MigLayout;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolbox.view.utils.ToolBoxIcon;
import org.orbisgis.orbistoolbox.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.sif.docking.DockingLocation;
import org.orbisgis.sif.docking.DockingPanelParameters;
import org.orbisgis.sif.edition.EditableElement;
import org.orbisgis.sif.edition.EditorDockable;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * UI for the configuration and the run of a WPS process.
 * It extends the EditorDockable interface to be able to be docked in OrbisGIS.
 *
 * @author Sylvain PALOMINOS
 */
public class LogEditor extends JPanel implements EditorDockable, PropertyChangeListener {

    private static final int SCROLLBAR_UNIT_INCREMENT = 16;
    private static final String NAME = "LOG_EDITOR";

    private LogEditableElement lee;
    private DockingPanelParameters dockingPanelParameters;
    private Map<String, LogPanel> componentMap;
    private LinkedList<String> endProcessFIFO;
    private JPanel contentPanel;
    //private LogLayerUI layerUI;
    //private JLayer<JComponent> jlayer;

    public LogEditor(ToolBox toolBox, LogEditableElement lee){
        this.setLayout(new MigLayout("fill"));
        this.lee = lee;
        this.lee.addPropertyChangeListener(this);
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setTitleIcon(ToolBoxIcon.getIcon("log"));
        dockingPanelParameters.setDefaultDockingLocation(
                new DockingLocation(DockingLocation.Location.STACKED_ON, toolBox.getReference()));
        dockingPanelParameters.setTitle("WPS log");
        dockingPanelParameters.setName(NAME);
        dockingPanelParameters.setCloseable(false);

        contentPanel = new JPanel(new MigLayout("fill"));
        //layerUI = new LogLayerUI();
        //jlayer = new JLayer<>(contentPanel, layerUI);

        //this.add(jlayer);
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane, "growx, growy");

        componentMap = new HashMap<>();
        endProcessFIFO = new LinkedList<>();
    }

    private void addNewLog(ProcessEditableElement pee){
        LogPanel panel = new LogPanel(pee.getProcess().getTitle());
        componentMap.put(pee.getId(), panel);
        contentPanel.add(panel, "growx, wrap");
    }

    private void removeLog(ProcessEditableElement pee, boolean successful){
        LogPanel lp = componentMap.get(pee.getId());
        if(successful) {
            lp.setState(ProcessEditableElement.ProcessState.COMPLETED);
        }
        else{
            lp.setState(ProcessEditableElement.ProcessState.ERROR);
        }
        String log = "\n=====================================\n"+
                "WPS Process : "+pee.getProcess().getTitle() +"\n"+
                "=====================================\n"+
                "Result : "+pee.getProcessState()+"\n"+
                "Log : \n";
        for(Map.Entry<String, Color> entry : pee.getLogMap().entrySet()){
            log+=entry.getKey()+"\n";
        }
        LoggerFactory.getLogger(LogEditor.class).info(log);
        lp.stop();
        endProcessFIFO.add(pee.getId());
        Timer timer = new Timer(5000, EventHandler.create(ActionListener.class, this, "endInstance"));
        timer.setRepeats(false);
        timer.start();
    }

    public void endInstance(){
        String id = endProcessFIFO.removeFirst();
        LogPanel logPanel= componentMap.get(id);
        //layerUI.start();
        //layerUI.setLogPanel(logPanel);
        logPanel.setVisible(false);
        contentPanel.remove(componentMap.get(id));
        componentMap.remove(id);
    }

    @Override
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public boolean match(EditableElement editableElement) {
        //Return true if the editable is the one contained by the Process editor
        return editableElement instanceof LogEditableElement;
    }

    @Override
    public EditableElement getEditableElement() {
        return lee;
    }

    @Override
    public void setEditableElement(EditableElement editableElement) {
        this.lee = (LogEditableElement)editableElement;
        dockingPanelParameters.setTitle("WPS log");
        lee.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if(event.getPropertyName().equals(ProcessEditableElement.LOG_PROPERTY)){
            ProcessEditableElement pee = (ProcessEditableElement)event.getSource();
            AbstractMap.SimpleEntry<String, Color> entry = (AbstractMap.SimpleEntry<String, Color>)event.getNewValue();
            componentMap.get(pee.getId()).addLogText(entry.getKey());
        }
        if(event.getPropertyName().equals(ProcessEditableElement.STATE_PROPERTY)){
            ProcessEditableElement pee = (ProcessEditableElement)event.getSource();
            switch((ProcessEditableElement.ProcessState)event.getNewValue()){
                case COMPLETED:
                    removeLog(pee, true);
                    break;
                case ERROR:
                    removeLog(pee, false);
                    break;
                case RUNNING:
                    addNewLog(pee);
                    break;
            }
        }
    }
}
