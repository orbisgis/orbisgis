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
import java.beans.EventHandler;
import javax.swing.*;
import org.apache.log4j.Logger;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.map.TransformListener;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.components.button.DropDownButton;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.geocatalog.EditableSource;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.tool.Automaton;
import org.orbisgis.view.map.tool.TransitionException;
import org.orbisgis.view.map.tools.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief The Map Editor Panel
 */
public class MapEditor extends JPanel implements EditorDockable, TransformListener   {
    protected final static I18n I18N = I18nFactory.getI18n(MapEditor.class);
    private static final Logger GUILOGGER = Logger.getLogger("gui."+MapEditor.class);
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L; 
    private MapControl mapControl = new MapControl();
    private MapContext mapContext = null;
    private MapElement mapEditable;
    private DockingPanelParameters dockingPanelParameters;
    private MapTransferHandler dragDropHandler;
    private MapStatusBar mapStatusBar = new MapStatusBar();
    /**
     * Constructor
     */
    public MapEditor() {
        super(new BorderLayout());
        dockingPanelParameters = new DockingPanelParameters();
        dockingPanelParameters.setName("map_editor");
        dockingPanelParameters.setTitle(I18N.tr("Map"));
        dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("map"));
        dockingPanelParameters.setMinimizable(false);
        dockingPanelParameters.setExternalizable(false);
        dockingPanelParameters.setCloseable(false);
        add(mapControl, BorderLayout.CENTER);
        add(mapStatusBar, BorderLayout.PAGE_END);
        mapControl.setDefaultTool(new ZoomInTool());
        //Declare Tools of Map Editors
        //For debug purpose, also add the toolbar in the frame
        //add(createToolBar(false), BorderLayout.SOUTH);
        //Add the tools in the docking Panel title
        dockingPanelParameters.setToolBar(createToolBar(true));

       
        //Set the Drop target
        dragDropHandler = new MapTransferHandler();        
        this.setTransferHandler(dragDropHandler);
    }
    /**
     * Notifies this component that it now has a parent component.
     * When this method is invoked, the chain of parent 
     * components is set up with KeyboardAction event listeners.
     */
    @Override
    public void addNotify() {
        super.addNotify();
        //Register listener
        dragDropHandler.getTransferEditableEvent().addListener(this, EventHandler.create(Listener.class, this, "onDropEditable","editableList"));
    }
    
    
    /**
     * The user Drop a list of Editable
     * @param editableList 
     */
    public void onDropEditable(EditableElement[] editableList) {
        BackgroundManager bm = (BackgroundManager) Services.getService(BackgroundManager.class);
        //Load the layers in the background
        bm.backgroundOperation(new DropDataSourceProcess(editableList));
    }
    
    /**
     * Load a new map context
     * @param element 
     */
    public final void loadMap(MapElement element) {
        try {         
            mapEditable = element;
            mapContext = (MapContext) element.getObject();
            //We (unfortunately) need a cross reference here : this way, we'll
            //be able to retrieve the MapTransform from the Toc..
            element.setMapEditor(this);
            mapControl.setMapContext(mapContext);
            mapControl.getMapTransform().setExtent(mapContext.getBoundingBox());
            mapControl.setElement(this);
            mapControl.initMapControl();
            repaint();
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
        //Selection button
        autoSelection.add(addButton(toolBar, new SelectionTool(), useButtonText));
        //Navigation Tools
        autoSelection.add(addButton(toolBar,new ZoomInTool(),useButtonText));
        autoSelection.add(addButton(toolBar,new ZoomOutTool(),useButtonText));
        autoSelection.add(addButton(toolBar,new PanTool(),useButtonText));
        //Full extent button
        toolBar.add(addButton(OrbisGISIcon.getIcon("world"),
                I18N.tr("Full extent"),
                I18N.tr("Zoom to show all geometries"),
                useButtonText,"onFullExtent"));

        //Mesure Tools
        JPopupMenu mesureMenu = new JPopupMenu();
        JMenuItem defaultMenu = createMenuItem(new MesureLineTool());
        mesureMenu.add(createMenuItem(new MesurePolygonTool()));
        mesureMenu.add(defaultMenu);
        mesureMenu.add(createMenuItem(new CompassTool()));
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

    /**
     * Add the automaton tool to a Menu
     * @param automaton
     * @return 
     */
    private JMenuItem createMenuItem(Automaton automaton) {
        JMenuItem automatonMenuItem = new JMenuItem(automaton.getName(), automaton.getImageIcon());
        automatonMenuItem.setToolTipText(automaton.getTooltip());
        automatonMenuItem.addActionListener(new AutomatonItemListener(automaton));        
        return automatonMenuItem;
    }
    
    /**
     * Create a simple button
     * @param icon
     * @param buttonText
     * @param buttonToolTip
     * @param useButtonText
     * @param localMethodName The name of the method to call on this
     * @return The button
     */
    private AbstractButton addButton(ImageIcon icon, String buttonText,String buttonToolTip,boolean useButtonText,String localMethodName) {
        String text="";
        if(useButtonText) {
           text = buttonText;
        }
        JButton newButton = new JButton(text,icon);
        newButton.setToolTipText(buttonToolTip);
        newButton.addActionListener(EventHandler.create(ActionListener.class,this,localMethodName));
        return newButton;
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
     * The user click on the button Full Extent
     */
    public void onFullExtent() {
        mapControl.getMapTransform().setExtent(mapContext.getLayerModel().getEnvelope());
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
        GUILOGGER.debug("Choose the tool named "+automaton.getName());
        try {
            mapControl.setTool(automaton);
        } catch (TransitionException ex) {
            GUILOGGER.error(I18N.tr("Unable to choose this tool"),ex);
        }
    }

    /**
     * Gets the {@link MapControl} linked to this {@code MapEditor}.
     * @return
     */
    public MapControl getMapControl(){
            return mapControl;
    }

    @Override
    public void extentChanged(Envelope oldExtent, MapTransform mapTransform) {
            //Update the scale in the MapEditor status bar
            mapStatusBar.setScaleDenominator(mapTransform.getScaleDenominator());
    }

    public void imageSizeChanged(int oldWidth, int oldHeight, MapTransform mapTransform) {
        //do nothing
    }

        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        public EditableElement getEditableElement() {
                return mapEditable;
        }

        public void setEditableElement(EditableElement editableElement) {
                if(editableElement instanceof MapElement) {
                        loadMap((MapElement)editableElement);
                }
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
    /**
     * This task is created when the user Drag Source from GeoCatalog
     * to the map directly. The layer is created directly on the root.
     */
    private class DropDataSourceProcess implements BackgroundJob {
        private EditableElement[] editableList;

        public DropDataSourceProcess(EditableElement[] editableList) {
            this.editableList = editableList;
        }
        
        public void run(org.orbisgis.progress.ProgressMonitor pm) {
            DataManager dataManager = (DataManager) Services.getService(DataManager.class);
            ILayer dropLayer = mapContext.getLayerModel();
            int i=0;
            for(EditableElement eElement : editableList) {
                pm.progressTo(100 * i++ / editableList.length);
                if(eElement instanceof EditableSource) {
                    String sourceName = ((EditableSource)eElement).getId();
                    try {
                        dropLayer.addLayer(dataManager.createLayer(sourceName));
                    } catch (LayerException e) {
                        //This layer can not be inserted, we continue to the next layer
                        GUILOGGER.warn(I18N.tr("Unable to create and drop the layer"),e);
                    }
                }
            }
        }

        public String getTaskName() {
            return I18N.tr("Load the data source droped into the map editor.");
        }    
        
    }
}
