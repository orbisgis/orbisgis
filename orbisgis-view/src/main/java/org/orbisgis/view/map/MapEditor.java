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

import com.vividsolutions.jts.geom.Envelope;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.orbisgis.core.edition.EditableElement;
import org.orbisgis.core.layerModel.DefaultMapContext;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.progress.NullProgressMonitor;
import org.orbisgis.view.components.button.DropDownButton;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief The Map Editor Panel
 */
public class MapEditor extends JPanel implements DockingPanel, TransformListener   {
    protected final static I18n I18N = I18nFactory.getI18n(MapEditor.class);
    private static final Logger GUILOGGER = Logger.getLogger("gui."+MapEditor.class);
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L; 
    private MapControl mapControl = new MapControl();
    private MapContext mapContext = null;
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
        this.add(mapControl, BorderLayout.CENTER);
        mapControl.setDefaultTool(new ZoomInTool());
        //Declare Tools of Map Editors
        //For debug purpose, also add the toolbar in the frame
        //add(createToolBar(false), BorderLayout.SOUTH);
        //Add the tools in the docking Panel title
        dockingPanelParameters.setToolBar(createToolBar(true));
    }

    
    public void loadMap(MapElement element) {
        try {            
            mapContext = (MapContext) element.getObject();
            mapContext.open(new NullProgressMonitor());
            mapControl.setMapContext(mapContext);
            mapControl.setElement(element);
            mapControl.initMapControl();
        } catch (LayerException ex) {            
            GUILOGGER.error(ex);
        } catch (IllegalStateException ex) {
            GUILOGGER.error(ex);
        } catch (TransitionException ex) {
            GUILOGGER.error(ex);
        }        
    }
    /**
     * Create a toolbar corresponding to the current state of the Editor
     * @return 
     */
    private JToolBar createToolBar(boolean useButtonText) {
        JToolBar toolBar = new JToolBar();
        ButtonGroup autoSelection = new ButtonGroup();
        //Navigation Tools
        autoSelection.add(addButton(toolBar,new ZoomInTool(),useButtonText));
        autoSelection.add(addButton(toolBar,new ZoomOutTool(),useButtonText));
        autoSelection.add(addButton(toolBar,new PanTool(),useButtonText));
        //Mesure Tools
        JPopupMenu mesureMenu = new JPopupMenu();
        JMenuItem defaultMenu = createMenuItem(new MesureLineTool(),autoSelection);
        mesureMenu.add(createMenuItem(new MesurePolygonTool(),autoSelection));
        mesureMenu.add(defaultMenu);
        mesureMenu.add(createMenuItem(new CompassTool(),autoSelection));
        //Create the Mesure Tools Popup Button
        DropDownButton mesureButton = new DropDownButton();
        if(useButtonText) {
            mesureButton.setName(I18N.tr("Mesure tools"));
        }
        mesureButton.setButtonAsMenuItem(true);
        //Add Menu to the Popup Button
        mesureButton.setComponentPopupMenu(mesureMenu);
        autoSelection.add(mesureButton);
        toolBar.add(mesureButton);
        mesureButton.setSelectedItem(defaultMenu);
        toolBar.addSeparator();
        return toolBar;
    }

    private JMenuItem createMenuItem(Automaton automaton,ButtonGroup autoSelection) {
        JMenuItem automatonMenuItem = new JMenuItem(automaton.getName(), automaton.getImageIcon());
        automatonMenuItem.setToolTipText(automaton.getTooltip());
        automatonMenuItem.addActionListener(new AutomatonItemListener(automaton));        
        return automatonMenuItem;
    }
    /**
     * Add the automaton on the toolBar
     * @param toolBar
     * @param text
     * @param automaton
     * @param useButtonText Show a text inside the ToolBar button.
     * With DockingFrames, this text appear only on popup menu list
     * @return 
     */
    private AbstractButton addButton(JToolBar toolBar,Automaton automaton,boolean useButtonText) {
        String text="";
        if(useButtonText) {
           text = automaton.getName();
        }
        JToggleButton button = new JToggleButton(text,automaton.getImageIcon());
        //Select it, if this is the currently used tool
        if(mapControl.getTool().getClass().equals(automaton.getClass()) ) {
            button.setSelected(true);
        }
        button.setToolTipText(automaton.getTooltip());
        button.addItemListener(new AutomatonItemListener(automaton));
        toolBar.add(button);
        return button;
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
    public void onToolSelected(Automaton automaton) {
        GUILOGGER.info(I18N.tr("Choose the tool named {0}",automaton.getName()));
        try {
            mapControl.setTool(automaton);
        } catch (TransitionException ex) {
            GUILOGGER.error(I18N.tr("Unable to choose this tool"),ex);
        }
    }

    public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {
        //do nothing
    }

    public void imageSizeChanged(int oldWidth, int oldHeight, MapTransform mapTransform) {
        //do nothing
    }
    
    /**
     * Internal Listener that store an automaton
     */
    private class AutomatonItemListener implements ItemListener,ActionListener {
        private Automaton automaton;
        AutomatonItemListener(Automaton automaton) {
            this.automaton = automaton;
        }
        /**
         * Used with Toggle Button (new state can be DESELECTED)
         */
        public void itemStateChanged(ItemEvent ie) {
            if(ie.getStateChange() == ItemEvent.SELECTED) {
                onToolSelected(automaton);
            }
        }
        /**
         * Used with Menu Item
         * @param ae 
         */
        public void actionPerformed(ActionEvent ae) {
            onToolSelected(automaton);
        }
    }
}
