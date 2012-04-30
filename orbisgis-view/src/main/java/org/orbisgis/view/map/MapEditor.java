/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 *
 * or contact directly:
 * info _at_ orbisgis.org
 */
package org.orbisgis.view.map;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tools.ZoomInTool;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief The Map Editor Panel
 */
public class MapEditor extends JPanel implements DockingPanel   {
    protected final static I18n I18N = I18nFactory.getI18n(MapEditor.class);
    
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L; 
    private MapControl mapControl;
    DockingPanelParameters dockingPanelParameters;
    
    /**
     * Constructor
     */
    public MapEditor() {
        super(new BorderLayout());
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName("map_editor");
        dockingPanelParameters.setTitle(I18N.tr("orbisgis.view.map.MapEditorTitle"));
        dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("map"));
        dockingPanelParameters.setMinimizable(false);
        dockingPanelParameters.setExternalizable(false);
        dockingPanelParameters.setCloseable(false);
        //Declare Tools of Map Editors
        JToolBar toolBar = createToolBar();
        //For debug purpose, also add the toolbar in the frame
        add(toolBar, BorderLayout.PAGE_START);
        //Add the tools in the docking Panel title
        dockingPanelParameters.setToolBar(toolBar);
        mapControl = new MapControl();
        this.add(mapControl, BorderLayout.CENTER);
    }

    /**
     * Create a toolbar corresponding to the current state of the Editor
     * @return 
     */
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        //Navigation Tools
        addButton(toolBar,I18N.tr("Zoom in"),new ZoomInTool());
        return toolBar;
    }
    
    private void addButton(JToolBar toolBar,String name,Automaton automaton) {
        JToggleButton button = new JToggleButton(name,automaton.getImageIcon());
        button.setToolTipText(automaton.getTooltip());
        button.addActionListener(new AutomatonActionListener(automaton));
        toolBar.add(button);
    }
    /**
     * Give information on the behaviour of this panel related to the current
     * docking system
     * @return The panel parameter instance
     */
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

    public JComponent getComponent() {
        return this;
    }
    /**
     * The user click on a Map Tool
     * @param automaton 
     */
    public void onToolClick(Automaton automaton) {
        mapControl.setDefaultTool(automaton);
    }
    
    /**
     * Internal Listener that store an automaton
     */
    private class AutomatonActionListener implements ActionListener {
        private Automaton automaton;
        AutomatonActionListener(Automaton automaton) {
            this.automaton = automaton;
        }
        public void actionPerformed(ActionEvent ae) {
            onToolClick(automaton);
        }        
    }
}
