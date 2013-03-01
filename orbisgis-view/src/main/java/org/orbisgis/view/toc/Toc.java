/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.view.toc;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.StyleType;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.classification.ClassificationMethodException;
import org.orbisgis.core.renderer.se.SeExceptions;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.Job;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.components.actions.DefaultAction;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.edition.EditorManager;
import org.orbisgis.view.geocatalog.EditableSource;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.EditableTransferEvent;
import org.orbisgis.view.map.MapControl;
import org.orbisgis.view.map.MapEditor;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.map.jobs.ZoomToSelection;
import org.orbisgis.view.table.TableEditableElement;
import org.orbisgis.view.toc.actions.*;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.LegendsPanel;
import org.orbisgis.view.toc.actions.cui.legend.EPLegendHelper;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.orbisgis.view.toc.ext.*;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * The Toc Panel component
 */
public class Toc extends JPanel implements EditorDockable, TocExt {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Toc.class);
        private static final Logger LOGGER = Logger.getLogger("gui." + Toc.class);
        private DockingPanelParameters dockingPanelParameters;
        private transient MapContext mapContext = null;
        private JTree tree;
        private transient DefaultTreeModel treeModel;
        private transient TocRenderer treeRenderer;
        //When this boolean is false, the selection event is not fired
        private AtomicBoolean fireSelectionEvent = new AtomicBoolean(true);
        //When this boolean is false, the selection is not propagated to tables
        private AtomicBoolean fireRowSelectionEvent = new AtomicBoolean(true);
        //Listen for map context changes
        private transient TocMapContextListener tocMapContextListener = new TocMapContextListener();
        //Listen for all layers changes
        private transient TocLayerListener tocLayerListener = new TocLayerListener();
        private PropertyChangeListener tocStyleListListener = EventHandler.create(PropertyChangeListener.class, this, "onStyleListChange","");
        private PropertyChangeListener tocStyleListener = EventHandler.create(PropertyChangeListener.class,this,"onStyleChange","");
        //Loaded editable map
        private transient MapElement mapElement = null;
        // Linked table editable element, for geometry selection
        private Map<String, TableEditableElement> linkedEditableElements = new HashMap<String, TableEditableElement>();
        private PropertyChangeListener tableSelectionChangeListener = EventHandler.create(PropertyChangeListener.class,this,"onTableSelectionChange","source");
        private PropertyChangeListener tableEditableClose = EventHandler.create(PropertyChangeListener.class,this,"onTableEditableClose","source");
        private PropertyChangeListener modificationListener = EventHandler.create(PropertyChangeListener.class,this,"onMapModified","");
        private Action saveAction;
        // Selection state cache, reset values before showing popup.
        private Map<String,Boolean> selectionState = new HashMap<String, Boolean>();
        private static final String HAS_LAYER_WITH_ROW_SELECTION = "HAS_LAYER_WITH_ROW_SELECTION";
        private static final String HAS_LAYER_GROUP = "HAS_LAYER_GROUP"; //One of the selected item is a layer group
        // Actions containers
        private ActionCommands popupActions = new ActionCommands();
        private PropertyChangeListener mapContextPropertyChange = EventHandler.create(PropertyChangeListener.class,this,"onMapContextPropertyChange","");
        /**
         * Constructor
         */
        public Toc() {
                super(new BorderLayout());
                //Set docking parameters
                dockingPanelParameters = new DockingPanelParameters();
                dockingPanelParameters.setName("toc");
                dockingPanelParameters.setTitle(I18N.tr("Toc"));
                dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("map"));
                initTitleActions();
                initPopupActions();
                //Initialise an empty tree
                add(new JScrollPane(makeTree()));

        }
        private void initTitleActions() {
                List<Action> tools = new ArrayList<Action>();
                saveAction = new DefaultAction("SAVE_MAP",I18N.tr("Save"),
                        I18N.tr("Save the Map"),OrbisGISIcon.getIcon("save"),
                        EventHandler.create(ActionListener.class,this,"onSaveMapContext"),
                        KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
                saveAction.setEnabled(false);
                tools.add(saveAction);
                dockingPanelParameters.setDockActions(tools);
        }
        private void initPopupActions() {
            // Layer actions
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_REMOVE_LAYER, I18N.tr("Remove layer"),
                    I18N.tr("Remove the layer from the map context"),OrbisGISIcon.getIcon("remove"),
                    EventHandler.create(ActionListener.class, this, "onDeleteLayer"),null));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ZOOM_TO, I18N.tr("Zoom to"),
                    I18N.tr("Zoom to the layer bounding box"), OrbisGISIcon.getIcon("magnifier"),
                    EventHandler.create(ActionListener.class, this, "zoomToLayer"), null));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ZOOM_TO_SELECTION,
                    I18N.tr("Zoom to selection"), I18N.tr("Zoom to selected geometries"),
                    OrbisGISIcon.getIcon("zoom_selected"),
                    EventHandler.create(ActionListener.class, this, "zoomToLayerSelection"), null)
                    .setOnLayerWithRowSelection(true));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_CLEAR_SELECTION,
                    I18N.tr("Clear selection"), I18N.tr("Clear the selected geometries"),
                    OrbisGISIcon.getIcon("edit-clear"),
                    EventHandler.create(ActionListener.class,this, "clearLayerRowSelection"),null)
                    .setOnLayerWithRowSelection(true));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_IMPORT_STYLE,
                    I18N.tr("Import style"), I18N.tr("Import a style from a file."),
                    OrbisGISIcon.getIcon("add"),
                    EventHandler.create(ActionListener.class, this, "onImportStyle"),null)
                    .setSingleSelection(true).setOnRealLayerOnly(true));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ADD_STYLE,
                    I18N.tr("Add style"), I18N.tr("Add a new style."),
                    OrbisGISIcon.getIcon("add"),
                    EventHandler.create(ActionListener.class, this, "onAddStyle"),null)
                    .setSingleSelection(true).setOnRealLayerOnly(true));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_OPEN_ATTRIBUTES,
                    I18N.tr("Open the attributes"), I18N.tr("Open a spreadsheet view of the attributes."),
                    OrbisGISIcon.getIcon("openattributes"),
                    EventHandler.create(ActionListener.class,this, "onMenuShowTable"),null)
                    .setOnRealLayerOnly(true));
            // DataSource Drawing Actions
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_EDIT_GEOMETRY,
                    I18N.tr("Switch to edition mode"), I18N.tr("The geometry edition toolbar will update this layer data source."),
                    OrbisGISIcon.getIcon("pencil"),
                    EventHandler.create(ActionListener.class,this, "onMenuSetActiveLayer"),null).setEnabledOnNotActiveLayer(true).setSingleSelection(true));
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_STOP_EDIT_GEOMETRY,
                    I18N.tr("Stop edition mode"), I18N.tr("Close the geometry edition toolbar."),
                    OrbisGISIcon.getIcon("stop"),
                    EventHandler.create(ActionListener.class,this, "onMenuUnsetActiveLayer"),null).setEnabledOnActiveLayer(true).setSingleSelection(true));
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_SAVE_EDIT_GEOMETRY,
                    I18N.tr("Save modifications"), I18N.tr("Apply the data source modifications"),
                    OrbisGISIcon.getIcon("save"),
                    EventHandler.create(ActionListener.class,this, "onMenuCommitDataSource"),null).setEnabledOnModifiedLayer(true));

            popupActions.addAction(new AddLayerGroupAction(this,TocActionFactory.A_ADD_LAYER_GROUP,
                    I18N.tr("Add layer group"),I18N.tr("Add a the layer group to the map context"),
                    OrbisGISIcon.getIcon("add"),
                    EventHandler.create(ActionListener.class, this, "onAddGroup"),null));
            // Style actions
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_SIMPLE_EDITION,
                    I18N.tr("Simple style edition"), I18N.tr("Open the simple editor for SE styles"),
                    OrbisGISIcon.getIcon("pencil"),
                    EventHandler.create(ActionListener.class, this, "onSimpleEditor"),null));
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_ADVANCED_EDITION,
                    I18N.tr("Advanced style edition"), I18N.tr("Open the advanced editor for SE styles"),
                    OrbisGISIcon.getIcon("pencil"),
                    EventHandler.create(ActionListener.class, this, "onAdvancedEditor"),null));
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_REMOVE_STYLE,
                    I18N.tr("Remove style"), I18N.tr("Remove this style from the associated layer."),
                    OrbisGISIcon.getIcon("remove"),
                    EventHandler.create(ActionListener.class, this, "onDeleteStyle"),null));
            popupActions.addAction(new StyleAction(this, TocActionFactory.A_EXPORT_STYLE,
                    I18N.tr("Export style"), I18N.tr("Export this style from the associated layer."),
                    OrbisGISIcon.getIcon("add"),
                    EventHandler.create(ActionListener.class, this, "onExportStyle"), null));
        }
        /**
         * User click on the save button
         */
        public void onSaveMapContext() {
                if(mapElement!=null) {
                        mapElement.save();
                }
        }

        /**
         * User select the Start/Stop edition of a layer geometries
         */
        public void onMenuSetActiveLayer() {
            if(mapContext!=null) {
                List<ILayer> selectedLayers = getSelectedLayers();
                if(!selectedLayers.isEmpty()) {
                    ILayer selectedLayer = selectedLayers.get(0);
                    if(mapContext.getActiveLayer()==null || !mapContext.getActiveLayer().equals(selectedLayer)) {
                        mapContext.setActiveLayer(selectedLayer);
                    }
                }
            }
        }
        public void onMenuCommitDataSource() {
            if(mapContext!=null) {
                List<ILayer> selectedLayers = getSelectedLayers();
                if(!selectedLayers.isEmpty()) {
                    for(ILayer layer : selectedLayers) {
                        DataSource source = layer.getDataSource();
                        if(source!=null) {
                            if(source.isModified()) {
                                try {
                                    source.commit();
                                } catch (Exception ex) {
                                    LOGGER.error(ex.getLocalizedMessage(),ex);
                                }
                            }
                        }
                    }
                }
            }
        }
        /**
         * User select the Start/Stop edition of a layer geometries
         */
        public void onMenuUnsetActiveLayer() {
            if(mapContext!=null) {
                List<ILayer> selectedLayers = getSelectedLayers();
                if(!selectedLayers.isEmpty()) {
                    ILayer selectedLayer = selectedLayers.get(0);
                    if(mapContext.getActiveLayer()!=null && mapContext.getActiveLayer().equals(selectedLayer)) {
                        mapContext.setActiveLayer(null);
                    }
                }
            }
        }
        /**
         * The Map Element has been updated
         */
        public void onMapModified(PropertyChangeEvent evt) {
                if(mapElement!=null && MapElement.PROP_MODIFIED.equals(evt.getPropertyName())) {
                        saveAction.setEnabled(mapElement.isModified());
                        treeModel.reload();
                }
        }
        /**
         * A linked table selection has been updated
         *
         * @param tableElement the selection container
         */
        public void onTableSelectionChange(TableEditableElement tableElement) {
                if (!updateLayerSelection(tableElement.getSourceName(), tableElement.getSelection(), mapContext.getLayerModel())) {
                        //This data source is no (more) in the MapContext
                        unlinkTableSelectionListening(tableElement);
                }
        }
        /**
         * The open/close state of the table editable element change
         * @param tableElement 
         */
        public void onTableEditableClose(TableEditableElement tableElement) {
                if(!tableElement.isOpen()) {
                        unlinkTableSelectionListening(tableElement);
                }
        }
        /**
         * Broke the selection link between the layers and the table selection container
         * @param tableElement 
         */
        private void unlinkTableSelectionListening(TableEditableElement tableElement) {
                tableElement.removePropertyChangeListener(tableSelectionChangeListener);
                tableElement.removePropertyChangeListener(tableEditableClose);
                linkedEditableElements.remove(tableElement.getSourceName());
        }

        /**
         * Update all layers selection where the provided source name
         * corresponding to the layer DataSource
         * @param sourceName
         * @param newSelection
         * @param layer
         * @return 
         */
        private boolean updateLayerSelection(String sourceName, Set<Integer> newSelection, ILayer layer) {
                boolean updated = false;
                if(layer.acceptsChilds()) {
                        for(ILayer subLayer : layer.getChildren()) {
                                updated = updated || updateLayerSelection(sourceName,newSelection,subLayer);
                        }
                } else {
                        if(layer.getDataSource()!=null && layer.getDataSource().getName().equals(sourceName)) {
                                layer.setSelection(newSelection);
                                updated = true;
                        }
                }
                return updated;
        }
        
        /**
         *
         * @return The editable map
         */
        public MapElement getMapElement() {
                return mapElement;
        }
        
        private TreeNode[] createTreeNodeArray(ILayer[] layers) {
                TreeNode[] nodes = new TreeNode[layers.length];
                for(int i=0;i<layers.length;i++) {
                        nodes[i]=new TocTreeNodeLayer(layers[i]);
                }
                return nodes;
        }

        private TreeNode[] createTreeNodeArray(Style[] styles) {
                TreeNode[] nodes = new TreeNode[styles.length];
                for(int i=0;i<styles.length;i++) {
                        nodes[i]=new TocTreeNodeStyle(styles[i]);
                }
                return nodes;
        }
        private void setTocSelection(MapContext mapContext) {
                ILayer[] layers = mapContext.getSelectedLayers();
                Style[] styles = mapContext.getSelectedStyles();
                TreePath[] selectedPaths = new TreePath[layers.length + styles.length];
                for (int i = 0; i < layers.length; i++) {
                        
                        selectedPaths[i] = new TreePath(createTreeNodeArray(layers[i].getLayerPath()));
                }
                for (int i = 0; i < styles.length; i++) {
                        Style s = styles[i];
                        TreeNode[] lays = createTreeNodeArray(s.getLayer().getLayerPath());
                        TreeNode[] path = new TreeNode[lays.length + 1];
                        System.arraycopy(lays, 0, path, 0, lays.length);
                        path[path.length - 1] = new TocTreeNodeStyle(s);
                        selectedPaths[i + layers.length] = new TreePath(path);
                }
                fireSelectionEvent.set(false);
                try {
                        tree.setSelectionPaths(selectedPaths);
                } finally {
                        fireSelectionEvent.set(true);
                }
        }

        /**
         * Create the Toc JTree
         *
         * @return the Toc JTree
         */
        private JTree makeTree() {
                tree = new JTree();
                //Items can be selected freely
                tree.getSelectionModel().setSelectionMode(
                        TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
                TocTransferHandler handler = new TocTransferHandler(this);
                //Add a drop listener
                handler.getTransferEditableEvent().addListener(this,
                        EventHandler.create(TocTransferHandler.EditableTransferListener.class, this, "onDropEditableElement", ""));
                tree.setDragEnabled(true);
                tree.setTransferHandler(handler);
                tree.setRootVisible(false);
                tree.setShowsRootHandles(true);
                tree.setEditable(true);
                treeRenderer = new TocRenderer(tree);
                tree.setCellRenderer(treeRenderer);
                setEmptyLayerModel(tree);
                tree.setCellEditor(new TocTreeEditor(tree));
                tree.addMouseListener(new PopupMouselistener());
                //Add a tree selection listener
                tree.getSelectionModel().addTreeSelectionListener(
                        EventHandler.create(TreeSelectionListener.class, this, "onTreeSelectionChange"));
                return tree;
        }

        /**
         * The user drop one or multiple EditableElement into the Toc Tree
         *
         * @param editTransfer
         * @throws IllegalArgumentException If drop location is not null,Style
         * or ILayer
         */
        public void onDropEditableElement(EditableTransferEvent editTransfer) {
                JTree.DropLocation dropLocation = (JTree.DropLocation) editTransfer.getDropLocation();
                EditableElement[] editableList = editTransfer.getEditableList();
                //Evaluate Drop Location
                ILayer dropNode;
                if (dropLocation.getPath() != null) {
                        Object node = dropLocation.getPath().getLastPathComponent();
                        if (node instanceof TocTreeNodeStyle) {
                                dropNode = ((TocTreeNodeStyle) node).getStyle().getLayer();
                        } else if (node instanceof TocTreeNodeLayer) {
                                dropNode = ((TocTreeNodeLayer) node).getLayer();
                        } else {
                                throw new IllegalArgumentException("Drop node is not an instance of Style or ILayer");
                        }
                } else {
                        // By default drop on rootNode
                        dropNode = mapContext.getLayerModel();
                }
                int index;
                if (!dropNode.acceptsChilds()) {
                        ILayer parent = dropNode.getParent();
                        if (parent.acceptsChilds()) {
                                index = parent.getIndex(dropNode);
                                dropNode = parent;
                        } else {
                                LOGGER.error(I18N.tr("Cannot drop layer on {0}", dropNode.getName())); //$NON-NLS-1$
                                return;
                        }
                } else {
                        index = dropNode.getLayerCount();
                }

                //Drop content to drop location
                List<EditableSource> sourceToDrop = new ArrayList<EditableSource>();
                List<ILayer> newSelectedLayer = new ArrayList<ILayer>(editableList.length);

                for (EditableElement editableElement : editableList) {
                        if (editableElement instanceof EditableSource) {
                                //From the GeoCatalog
                                sourceToDrop.add((EditableSource) editableElement);
                        } else if (editableElement instanceof EditableLayer) {
                                //From the TOC (move)
                                ILayer layer = ((EditableLayer) editableElement).getLayer();
                                try {
                                        layer.moveTo(dropNode, index);
                                        newSelectedLayer.add(layer);
                                } catch (LayerException ex) {
                                        LOGGER.error(I18N.tr("Cannot drop layer on {0}", dropNode.getName()), ex);
                                }
                        } else {
                                LOGGER.error(I18N.tr("Drop unknown editable of type : {0}", editableElement.getTypeId()));
                        }
                }

                //Select moved layers
                if (!newSelectedLayer.isEmpty()) {
                        mapContext.setSelectedLayers(newSelectedLayer.toArray(new ILayer[newSelectedLayer.size()]));
                }


                if (!sourceToDrop.isEmpty()) {
                        BackgroundManager bm = Services.getService(BackgroundManager.class);//Cancel the drawing process
                        bm.backgroundOperation(new DropDataSourceListProcess(dropNode, index, sourceToDrop));
                        for(Job job : bm.getActiveJobs()) {
                                if(job.getId().toString().startsWith(MapControl.JOB_DRAWING_PREFIX_ID)) {
                                        job.cancel();
                                }
                        }
                }
        }

        public ArrayList<ILayer> getSelectedLayers() {
                return getSelectedLayers(tree.getSelectionPaths());
        }

        /**
         * Cast all ILayer elements of provided tree paths
         *
         * @param selectedPaths Internal Tree path
         * @return All ILayer instances of provided path
         */
        private ArrayList<ILayer> getSelectedLayers(TreePath[] selectedPaths) {
                ArrayList<ILayer> layers = new ArrayList<ILayer>();
                if (selectedPaths != null) {
                        for (int i = 0; i < selectedPaths.length; i++) {
                                Object lastPathComponent = selectedPaths[i].getLastPathComponent();
                                if (lastPathComponent instanceof TocTreeNodeLayer) {
                                        layers.add(((TocTreeNodeLayer) lastPathComponent).getLayer());
                                }
                        }
                }
                return layers;
        }

        /**
         * Copy the selection of the JTree into the selection in the layer
         * model. This method do nothing if the fireSelectionEvent is false
         */
        public void onTreeSelectionChange() {
                if (fireSelectionEvent.getAndSet(false)) {
                        try {
                                //Update the mapcontext selection model
                                //There are selection constraint, then
                                //update also the jtree selection model
                                List<Style> styles = new ArrayList<Style>();
                                List<ILayer> layers = new ArrayList<ILayer>();
                                List<TreePath> keptSelection = new ArrayList<TreePath>();
                                TreePath[] paths = tree.getSelectionPaths();
                                if(paths!=null) {
                                        for(TreePath path : paths) {
                                                TreeNode treeNode = (TreeNode) path.getLastPathComponent();
                                                if(treeNode instanceof TocTreeNodeLayer) {
                                                        //Do not mix the selection
                                                        if(styles.isEmpty()) {
                                                                keptSelection.add(path);
                                                                layers.add(((TocTreeNodeLayer)treeNode).getLayer());
                                                        }
                                                } else if(treeNode instanceof TocTreeNodeStyle) {
                                                        //Do not mix the selection
                                                        if(layers.isEmpty()) {
                                                                keptSelection.add(path);
                                                                styles.add(((TocTreeNodeStyle)treeNode).getStyle());
                                                        }
                                                }
                                        }
                                }
                                //Update the two selection model
                                tree.getSelectionModel().setSelectionPaths(keptSelection.toArray(new TreePath[keptSelection.size()]));
                                mapContext.setSelectedLayers(layers.toArray(new ILayer[layers.size()]));
                                mapContext.setSelectedStyles(styles.toArray(new Style[styles.size()]));
                        } finally {
                                fireSelectionEvent.set(true);
                        }
                }
        }

        private void setEmptyLayerModel(JTree jTree) {
                //Add the treeModel
                DataManager dataManager = Services.getService(DataManager.class);
                jTree.setModel(new DefaultTreeModel(new TocTreeNodeLayer(dataManager.createLayerCollection("root"))));
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return dockingPanelParameters;
        }

        public MapContext getMapContext() {
                return mapContext;
        }
        /**
        * Recursively add property listeners to the provided node
        * @param node 
        */
        private void addPropertyListeners(TreeNode node) {
                if(node instanceof TocTreeNodeLayer) {
                        ILayer layer =((TocTreeNodeLayer) node).getLayer();
                        if(!layer.acceptsChilds()) {
                                layer.addPropertyChangeListener(Layer.PROP_STYLES,tocStyleListListener);
                                for(Style st : layer.getStyles()) {
                                        addPropertyListeners(new TocTreeNodeStyle(st));
                                }
                        } else {
                                for(ILayer subLayer : layer.getChildren()) {
                                        addPropertyListeners(new TocTreeNodeLayer(subLayer));
                                }
                        }
                } else if(node instanceof TocTreeNodeStyle) {
                        Style st = ((TocTreeNodeStyle) node).getStyle();
                        st.addPropertyChangeListener(tocStyleListener);
                }
        }
        /**
         * Recursively remove the property listeners of the provided node
         * @param node 
         */
        private void removePropertyListeners(TreeNode node) {
                if(node instanceof TocTreeNodeLayer) {
                        ILayer layer =((TocTreeNodeLayer) node).getLayer();
                        if(!layer.acceptsChilds()) {
                                layer.removePropertyChangeListener(tocStyleListListener);
                                for(Style st : layer.getStyles()) {
                                        removePropertyListeners(new TocTreeNodeStyle(st));
                                }
                        }
                } else if(node instanceof TocTreeNodeStyle) {
                        Style st = ((TocTreeNodeStyle) node).getStyle();
                        st.removePropertyChangeListener(tocStyleListener);
                }
        }
        /**
         * Load the specified MapElement in the toc
         *
         * @param newMapElement
         */
        public void setEditableMap(MapElement newMapElement) {
                if (newMapElement != null) {
                        MapContext importedMap = ((MapContext) newMapElement.getObject());
                        if (!importedMap.isOpen()) {
                                try {
                                        importedMap.open(null);
                                } catch (LayerException ex) {
                                        throw new IllegalArgumentException(ex);
                                } catch (IllegalStateException ex) {
                                        throw new IllegalArgumentException(ex);
                                }
                        }
                }

                // Remove the listeners
                if (this.mapContext != null) {
                        removePropertyListeners(new TocTreeNodeLayer(this.mapContext.getLayerModel()));
                        this.mapContext.getLayerModel().removeLayerListenerRecursively(tocLayerListener);
                        this.mapContext.removeMapContextListener(tocMapContextListener);
                        this.mapContext.removePropertyChangeListener(mapContextPropertyChange);
                        mapElement.removePropertyChangeListener(modificationListener);
                        for(TableEditableElement editable : linkedEditableElements.values()) {
                                unlinkTableSelectionListening(editable);
                        }
                        linkedEditableElements.clear();
                }


                if (newMapElement != null) {
                        this.mapContext = ((MapContext) newMapElement.getObject());
                        mapContext.addPropertyChangeListener(mapContextPropertyChange);
                        treeRenderer.setMapContext(mapContext);
                        this.mapElement = newMapElement;
                        // Add the listeners to the new MapContext
                        this.mapContext.addMapContextListener(tocMapContextListener);
                        final ILayer root = this.mapContext.getLayerModel();
                        addPropertyListeners(new TocTreeNodeLayer(root));
                        mapElement.addPropertyChangeListener(MapElement.PROP_MODIFIED, modificationListener);
                        root.addLayerListenerRecursively(tocLayerListener);
                        // Apply treeModel and clear the selection        
                        fireSelectionEvent.set(false);
                        try {
                                treeModel = new DefaultTreeModel(new TocTreeNodeLayer(root));
                                tree.setModel(treeModel);
                                tree.getSelectionModel().clearSelection();
                        } finally {
                                fireSelectionEvent.set(true);
                        }

                        setTocSelection(mapContext);
                        fetchForTableEditableElements();
                        //TODO ? repaint 
                }
        }

        /**
         * A property of the MapContext has been updated
         * @param evt Event information
         */
        public void onMapContextPropertyChange(PropertyChangeEvent evt) {
            if(MapContext.PROP_ACTIVELAYER.equals(evt.getPropertyName())) {
                if(evt.getOldValue() instanceof ILayer) {
                    treeModel.nodeChanged(new TocTreeNodeLayer((ILayer)evt.getOldValue()));
                }
                if(evt.getNewValue() instanceof ILayer) {
                    treeModel.nodeChanged(new TocTreeNodeLayer((ILayer)evt.getNewValue()));
                }
            }
        }
        @Override
        public JComponent getComponent() {
                return this;
        }

        @Override
        public boolean isLayerSelection() {
            Object selected = tree.getLastSelectedPathComponent();
            return selected!=null && selected instanceof TocTreeNodeLayer;
        }

        @Override
        public JTree getTree() {
            return tree;
        }

        @Override
        public boolean isStyleSelection() {
            Object selected = tree.getLastSelectedPathComponent();
            return selected!=null && selected instanceof TocTreeNodeStyle;
        }

        @Override
        public boolean hasLayerWithRowSelection() {
            Boolean hasLayerWithRowSelection = selectionState.get(HAS_LAYER_WITH_ROW_SELECTION);
            // If the value has not been cached
            if(hasLayerWithRowSelection==null) {
                hasLayerWithRowSelection = false;
                // Fetch selected layers for Row selection
                TreePath[] selectedItems = tree.getSelectionPaths();
                if(selectedItems!=null) {
                    for (TreePath path : selectedItems) {
                        Object treeNode = path.getLastPathComponent();
                        if (treeNode instanceof TocTreeNodeLayer) {
                            ILayer l = ((TocTreeNodeLayer) treeNode).getLayer();
                            if (!l.getSelection().isEmpty()) {
                                hasLayerWithRowSelection = true;
                                break;
                            }
                        }
                    }
                }
                selectionState.put(HAS_LAYER_WITH_ROW_SELECTION,hasLayerWithRowSelection);
            }
            return hasLayerWithRowSelection;
        }
        @Override
        public boolean hasLayerGroup() {
            Boolean hasLayerGroup = selectionState.get(HAS_LAYER_GROUP);
            // If the value has not been cached
            if(hasLayerGroup==null) {
                hasLayerGroup = false;
                // Fetch selected layers for Row selection
                TreePath[] selectedItems = tree.getSelectionPaths();
                if(selectedItems!=null) {
                    for (TreePath path : selectedItems) {
                        Object treeNode = path.getLastPathComponent();
                        if (treeNode instanceof TocTreeNodeLayer) {
                            ILayer l = ((TocTreeNodeLayer) treeNode).getLayer();
                            if (l instanceof LayerCollection) {
                                hasLayerGroup = true;
                                break;
                            }
                        }
                    }
                }
                selectionState.put(HAS_LAYER_GROUP,hasLayerGroup);
            }
            return hasLayerGroup;
        }

        /**
         * @return Action manager of tree popup.
         */
        public ActionCommands getPopupActions() {
            return popupActions;
        }

        /**
         * Action triggered by the Add group button in the menu of the TOC.
         */
        public void onAddGroup(){
                LayerCollection lc = new LayerCollection("group"+System.currentTimeMillis());
                try {
                        if (tree.getSelectionCount() == 1){
                                Object selected = tree.getLastSelectedPathComponent();
                                if(selected instanceof TocTreeNodeLayer){
                                        ILayer l = ((TocTreeNodeLayer) selected).getLayer();
                                        if(l instanceof LayerCollection){
                                                l.addLayer(lc);
                                        } else {
                                                LayerCollection parent = (LayerCollection) l.getParent();
                                                parent.addLayer(lc);
                                        }
                                }
                        } else {
                                LayerCollection root = (LayerCollection) ((TocTreeNodeLayer)tree.getModel().getRoot()).getLayer();
                                root.addLayer(lc);
                        }
                } catch (LayerException l){
                        LOGGER.error(I18N.tr("Can not add a LayerCollection : {0}", l.getMessage()));
                }
        }

        /**
         * The user click on delete layer menu item.
         */
        public void onDeleteLayer() {
                ILayer[] selectedResources = mapContext.getSelectedLayers();
                boolean saveDataSources = false;
                // Check for modified sources
                for (ILayer resource : selectedResources) {
                    DataSource dataSource = resource.getDataSource();
                    if(dataSource!=null && dataSource.isModified()) {
                        int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                I18N.tr("Some layers use modified data source, do you want to save these modifications before removing them ?"),
                                I18N.tr("Save geometry edits"),
                                JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
                        if(response==JOptionPane.YES_OPTION) {
                            saveDataSources = true;
                        } else if(response==JOptionPane.CANCEL_OPTION) {
                            return;
                        }
                        break;
                    }
                }
                // Commit
                if(saveDataSources) {
                    for (ILayer resource : selectedResources) {
                            DataSource dataSource = resource.getDataSource();
                            if(dataSource!=null && dataSource.isModified()) {
                                try {
                                    dataSource.commit();
                                } catch (Exception ex) {
                                    int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                                            I18N.tr("The layer data source {0} can not be saved, are you sure you want to continue ?",resource.getName()),
                                            I18N.tr("Errors on data source save process"),
                                            JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
                                    if(response==JOptionPane.NO_OPTION) {
                                        return;
                                    }
                                }
                            }
                    }
                }
                // Remove layers
                for (ILayer resource : selectedResources) {
                        try {
                                resource.getParent().remove(resource);
                        } catch (LayerException e) {
                                LOGGER.error(I18N.tr("Cannot delete layer"), e);
                        }
                }
        }
        
        
        /**
         * The user click on zoomto layer menu item.
         */
        public void zoomToLayer() {
                ILayer[] selectedResources = mapContext.getSelectedLayers();
                Envelope env=null;
                for (ILayer resource : selectedResources) {
                        if(env==null) {
                            env = resource.getEnvelope();
                        } else {
                            env.expandToInclude(resource.getEnvelope());
                        }
                }
                if(env!=null) {
                    mapContext.setBoundingBox(env);
                }
        }
        
        /**
         * The user click on the Zoom To Layer selection menu
         */
        public void zoomToLayerSelection() {
                ILayer[] selectedLayers = mapContext.getSelectedLayers();
                ZoomToSelection zoomJob = new ZoomToSelection(mapContext, selectedLayers);
                BackgroundManager bm = Services.getService(BackgroundManager.class);
                bm.backgroundOperation(zoomJob);
        }
        /**
         * The user click on the clear layer selection menu
         */
        public void clearLayerRowSelection() {
                ILayer[] selectedLayers = mapContext.getSelectedLayers();
                for(ILayer layer : selectedLayers) {
                        if(!layer.acceptsChilds()) {
                                layer.setSelection(new IntegerUnion());
                        }
                }
        }
        /**
         * The user choose to delete a style through the dedicated menu.
         */
        public void onDeleteStyle() {
                Style[] styles = mapContext.getSelectedStyles();
                for (Style s : styles) {
                        ILayer l = s.getLayer();
                        l.removeStyle(s);
                }
        }
        /**
        *  The user want to see one or more source content of the selected layers
        */
        public void onMenuShowTable() {
                ILayer[] layers = mapContext.getSelectedLayers();
                EditorManager editorManager = Services.getService(EditorManager.class);
                for (ILayer layer : layers) {
                        DataSource source = layer.getDataSource();
                        if(source!=null) {
                                TableEditableElement tableDocument = new TableEditableElement(source.getName());
                                editorManager.openEditable(tableDocument);
                        }
                }
        }
        
        
        /**
         * The user choose to export a style through the dedicated menu.
         */
        public void onExportStyle() {
                Style[] styles = mapContext.getSelectedStyles();
                if(styles.length == 1){
                        Style style = styles[0];
                        final SaveFilePanel outputXMLPanel = new SaveFilePanel(
                                        "org.orbisgis.core.ui.editorViews.toc.actions.ImportStyle",
                                        "Choose a location");

                        outputXMLPanel.addFilter("se", "Symbology Encoding FeatureTypeStyle");
                        outputXMLPanel.loadState();
                        if (UIFactory.showDialog(outputXMLPanel)) {
                                String seFile = outputXMLPanel.getSelectedFile().getAbsolutePath();
                                style.export(seFile);
                        }
                }
        }

        /**
         * Add a new default style to the selected layer.
         */
        public void onAddStyle() {
                ILayer[] layers = mapContext.getSelectedLayers();
                if (layers.length == 1) {
                        ILayer layer = layers[0];
                        Style s = new Style(layer, true);
                        layer.addStyle(s);

                }
        }

        /**
         * The user choose to import a style and to add it to the selected layer
         * through the dedicated menu.
         */
        public void onImportStyle() {
                ILayer[] layers = mapContext.getSelectedLayers();
                if (layers.length == 1) {
                        ILayer layer = layers[0];
                        final OpenFilePanel inputXMLPanel = new OpenFilePanel(
                                "org.orbisgis.core.ui.editorViews.toc.actions.ImportStyle",
                                "Choose a location");

                        inputXMLPanel.addFilter("se", "Symbology Encoding 2.0 (FeatureTypeStyle");

                        if (UIFactory.showDialog(inputXMLPanel)) {
                                String seFile = inputXMLPanel.getSelectedFile().getAbsolutePath();
                                try {
                                        layer.addStyle(new Style(layer, seFile));
                                } catch (SeExceptions.InvalidStyle ex) {
                                        LOGGER.error(ex.getLocalizedMessage());
                                        String msg = ex.getMessage().replace("<", "\n    - ").replace(',', ' ').replace(": ", "\n - ");
                                        JOptionPane.showMessageDialog(null, msg,
                                                "Error while loading the style", JOptionPane.ERROR_MESSAGE);
                                }
                        }
                }
        }

        /**
         * If used, this method opens an advanced editor for the currently selected
         * style.
         */
        public void onAdvancedEditor(){
                try {
                        Style[] styles = mapContext.getSelectedStyles();
                        if(styles.length == 1){
                                Style style = styles[0];
                                ILayer layer = style.getLayer();
                                int index = layer.indexOf(style);
                                // Obtain MapTransform
                                MapEditor editor = mapElement.getMapEditor();
                                MapTransform mt = editor.getMapControl().getMapTransform();
                                if (mt == null) {
                                        JOptionPane.showMessageDialog(null,I18N.tr("Advanced Editor can't be loaded"));
                                }

                                LegendUIController controller = new LegendUIController(style);

                                if (UIFactory.showDialog((UIPanel)controller.getMainPanel())) {
                                        layer.setStyle(index,controller.getEditedFeatureTypeStyle());
                                }
                        }
		} catch (SeExceptions.InvalidStyle ex) {
			LOGGER.error(I18N.tr("Error while editing the legend"), ex);
		}
        }

        public void onSimpleEditor() {
                TreePath selObjs = tree.getSelectionPath();
                if (selObjs.getLastPathComponent() instanceof TocTreeNodeStyle) {
                        try {
                                Style style = ((TocTreeNodeStyle) selObjs.getLastPathComponent()).getStyle();
                                Layer layer = (Layer) style.getLayer();
                                int index = layer.indexOf(style);
                                Type typ = layer.getDataSource().getMetadata().getFieldType(
                                        layer.getDataSource().getSpatialFieldIndex());
                                //In order to be able to cancel all of our modifications,
                                //we produce a copy of our style.
                                MapEditor editor = mapElement.getMapEditor();
                                MapTransform mt = editor.getMapControl().getMapTransform();
                                JAXBElement<StyleType> jest = style.getJAXBElement();
                                LegendsPanel pan = new LegendsPanel();
                                Style copy = new Style(jest, layer);
                                ILegendPanel[] legends = EPLegendHelper.getLegendPanels(pan);
                                pan.init(mt, typ, copy, legends, layer);
                                if (UIFactory.showDialog(pan)) {
                                        try {
                                                layer.setStyle(index, pan.getStyleWrapper().getStyle());
                                        } catch (ClassificationMethodException e) {
                                                LOGGER.error(e.getMessage());
                                        }
                                }
                        } catch (SeExceptions.InvalidStyle sis) {
                                //I don't know how this could happen : we are creating a style
                                //from a valid style. Should be valid too, consequently...
                                LOGGER.error("The style you're trying to edit is not valid !");
                        } catch (DriverException de) {
                                LOGGER.error("An error occurred while processing the DataSource");
                        } catch (UnsupportedOperationException uoe){
                                LOGGER.error(uoe.getMessage());
                        }
                }
        }

        @Override
        public boolean match(EditableElement editableElement) {
                if(mapContext!=null && editableElement instanceof TableEditableElement) {
                        // A table editor is going to be opened
                        TableEditableElement tableElement = (TableEditableElement) editableElement;
                        registerTableElement(tableElement);
                }
                return editableElement instanceof MapElement;
        }

        private void registerTableElement(TableEditableElement tableElement) {
                if (!linkedEditableElements.containsKey(
                        tableElement.getSourceName())
                        && hasDataSource(mapContext.getLayerModel(),
                        tableElement.getSourceName())) {
                        LOGGER.debug("Link the editable element with the toc");
                        //Need to track geometry selection with this element
                        // MapContext selection change -> Table selection
                        linkedEditableElements.put(tableElement.getSourceName(), tableElement);
                        // Table selection change -> Layer selection
                        tableElement.addPropertyChangeListener(TableEditableElement.PROP_SELECTION, tableSelectionChangeListener);
                        //Unlink the table element when it is closed
                        tableElement.addPropertyChangeListener(TableEditableElement.PROP_OPEN, tableEditableClose);
                        //If the table has already an active selection, load it
                        if (!tableElement.getSelection().isEmpty()) {
                                onTableSelectionChange(tableElement);
                        } else {
                                if(mapContext!=null) {
                                        //If the table has no selection set the current selection
                                        ILayer[] layers = mapContext.getLayers();
                                        Set<Integer> mergedSelection = new IntegerUnion();
                                        for (ILayer layer : layers) {
                                                DataSource source = layer.getDataSource();
                                                if (source != null && source.getName().equals(tableElement.getSourceName())) {
                                                        mergedSelection.addAll(layer.getSelection());
                                                }
                                        }
                                        tableElement.setSelection(mergedSelection);
                                }
                        }
                }
        }
        @Override
        public EditableElement getEditableElement() {
                return mapElement;
        }
        
        /**
         * Link with all table editable element already in editors
         */
        private void fetchForTableEditableElements() {
                //List all registered data source in the current map context
                Set<String> dataSourceNames = new HashSet<String>();
                for(ILayer layer : mapContext.getLayerModel().getLayersRecursively()) {
                        DataSource source = layer.getDataSource();
                        if(source!=null) {
                                dataSourceNames.add(source.getName());
                        }
                }
                //Fetch all editable
                EditorManager manager = Services.getService(EditorManager.class);
                for(EditableElement editable : manager.getEditableElements()) {
                        if(editable instanceof TableEditableElement) {
                                registerTableElement((TableEditableElement)editable);
                        }
                }
        }
        
        /**
         * Search in the layer the provided data source name
         * @param layer
         * @param sourceName
         */
        private boolean hasDataSource(ILayer layer,String sourceName) {
                DataSource source = layer.getDataSource();
                if(source!=null) {
                        return source.getName().equals(sourceName);
                }
                if(layer.acceptsChilds()) {
                        for(ILayer subLayer : layer.getChildren()) {
                                if(hasDataSource(subLayer, sourceName)) {
                                        return true;
                                }
                        }
                }
                return false;
        }

        @Override
        public void setEditableElement(EditableElement editableElement) {
                if (editableElement instanceof MapElement) {
                        MapElement importedMap = (MapElement) editableElement;
                        setEditableMap(importedMap);
                }
        }
        
        public void onStyleChange(PropertyChangeEvent evt) {
                treeModel.nodeChanged(new TocTreeNodeStyle((Style)evt.getSource()));
        }
        public void onStyleListChange(PropertyChangeEvent evt) {
                if(evt.getNewValue() instanceof List) {
                        if(evt.getOldValue()!=null) {
                                for(Style style : (List<Style>)evt.getOldValue()) {
                                        style.removePropertyChangeListener(tocStyleListener);
                                }
                        }
                        for(Style style : (List<Style>)evt.getNewValue()) {
                                style.removePropertyChangeListener(tocStyleListener);
                                style.addPropertyChangeListener(tocStyleListener);
                        }
                } else if(evt.getNewValue() instanceof Style){
                        Style newStyle = (Style)evt.getNewValue();
                        newStyle.removePropertyChangeListener(tocStyleListener);
                        newStyle.addPropertyChangeListener(tocStyleListener);                        
                }
                treeModel.nodeStructureChanged(new TocTreeNodeLayer((ILayer) evt.getSource()));
        }
        
        private class TocLayerListener implements LayerListener {

                @Override
                public void layerAdded(final LayerCollectionEvent e) {
                        for (final ILayer layer : e.getAffected()) {
                                layer.addLayerListenerRecursively(this);
                        }
                        TreeNode parentNode = new TocTreeNodeLayer(e.getParent());
                        addPropertyListeners(parentNode);
                        treeModel.nodeStructureChanged(parentNode);
                        fetchForTableEditableElements();
                }

                @Override
                public void layerMoved(LayerCollectionEvent e) {
                        treeModel.nodeStructureChanged(new TocTreeNodeLayer(e.getParent()));
                }

                @Override
                public boolean layerRemoving(LayerCollectionEvent e) {
                        // Close editors
                        for (final ILayer layer : e.getAffected()) {
                                layer.removePropertyChangeListener(tocStyleListListener);
                                layer.removeLayerListener(this);
                        }
                        return true;
                }

                @Override
                public void layerRemoved(final LayerCollectionEvent e) {
                        for (final ILayer layer : e.getAffected()) {
                                layer.removeLayerListenerRecursively(this);
                        }
                        treeModel.nodeStructureChanged(new TocTreeNodeLayer(e.getParent()));
                }

                @Override
                public void nameChanged(LayerListenerEvent e) {
                        onLayerChanged(e.getAffectedLayer());
                }

                private void onLayerChanged(ILayer layer) {
                        treeModel.nodeChanged(new TocTreeNodeLayer(layer));
                }
                
                @Override
                public void styleChanged(LayerListenerEvent e) {
                        //Deprecated listener
                }

                @Override
                @SuppressWarnings("deprecation")
                public void visibilityChanged(LayerListenerEvent e) {
                        onLayerChanged(e.getAffectedLayer());
                }

                @Override
                public void selectionChanged(SelectionEvent e) {
                        //Layer data rows selection change
                                ILayer layer = ((ILayer)e.getSource());
                                DataSource src = layer.getDataSource();
                                if (src!=null && 
                                    linkedEditableElements.containsKey(src.getName()))
                                {
                                        if(fireRowSelectionEvent.getAndSet(false)) {
                                                try {
                                                        //Update table element selection
                                                        TableEditableElement tableElement = linkedEditableElements.get(src.getName());
                                                        tableElement.setSelection(layer.getSelection());
                                                } finally {
                                                        fireRowSelectionEvent.set(true);
                                                }
                                        }
                                }
                }
        }

        private final class TocMapContextListener implements MapContextListener {

                @Override
                public void layerSelectionChanged(MapContext mapContext) {
                        setTocSelection(mapContext);
                }
        }

        /**
         * The user drop a list of EditableSource in the TOC tree
         */
        private class DropDataSourceListProcess implements BackgroundJob {

                private ILayer dropNode;
                private int dropIndex;
                private List<EditableSource> draggedResources;

                public DropDataSourceListProcess(ILayer dropNode, int dropIndex, List<EditableSource> draggedResources) {
                        this.dropNode = dropNode;
                        this.dropIndex = dropIndex;
                        this.draggedResources = draggedResources;
                }

                @Override
                public void run(ProgressMonitor pm) {

                        DataManager dataManager = Services.getService(DataManager.class);
                        for (int i = 0; i < draggedResources.size(); i++) {
                                String sourceName = draggedResources.get(i).getId();
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(100 * i / draggedResources.size());
                                        try {
                                                ILayer nl = dataManager.createLayer(sourceName);
                                                dropNode.insertLayer(nl, dropIndex);
                                        } catch (LayerException e) {
                                                throw new RuntimeException(I18N.tr("Cannot add the layer to the destination"), e);
                                        }
                                }
                        }
                        treeModel.nodeChanged(new TocTreeNodeLayer(dropNode));
                }

                @Override
                public String getTaskName() {
                        return I18N.tr("Load the data source droped into the toc.");
                }
        }

        /**
         * Implements Popup action on JTree,
         * and checkbox activation on single click (override Look&Feel)
         * 
         */
        private class PopupMouselistener extends MouseAdapter {

                /**
                 * Show/Hide Layer if the checkbox is selected
                 * override Look&Feel behaviour
                 * @param e 
                 */
                @Override
                public void mouseClicked(MouseEvent e) {
                        final int x = e.getX();
                        final int y = e.getY();
                        final int mouseButton = e.getButton();
                        TreePath path = tree.getPathForLocation(x, y);
                        Rectangle layerNodeLocation = tree.getPathBounds(path);

                        if (path != null) {
                                Rectangle checkBoxBounds = treeRenderer.getCheckBoxBounds();
                                checkBoxBounds.translate(
                                        (int) layerNodeLocation.getX(),
                                        (int) layerNodeLocation.getY());
                                if ((checkBoxBounds.contains(e.getPoint()))
                                        && (MouseEvent.BUTTON1 == mouseButton)
                                        && (1 == e.getClickCount())) {
                                        // mouse click inside checkbox
                                        if (path.getLastPathComponent() instanceof TocTreeNodeLayer) {
                                                ILayer layer = ((TocTreeNodeLayer) path.getLastPathComponent()).getLayer();
                                                try {
                                                        layer.setVisible(!layer.isVisible());
                                                } catch (LayerException e1) {
                                                        LOGGER.error(e1);
                                                }
                                        } else if(path.getLastPathComponent() instanceof TocTreeNodeStyle) {
                                                Style style = ((TocTreeNodeStyle) path.getLastPathComponent()).getStyle();
                                                style.setVisible(!style.isVisible());
                                        }
                                }                                
                        }
                }

                
                @Override
                public void mousePressed(MouseEvent e) {
                        maybeShowPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                        maybeShowPopup(e);
                }

                /**
                 * Determines whether {@code path} is one of the {@code
                 * TreePath} contained in {@code selectionPaths}.
                 * @param selectionPaths
                 * @param path
                 * @return
                 */
                private boolean contains(TreePath[] selectionPaths, TreePath path) {
                        for (TreePath treePath : selectionPaths) {
                                boolean equals = true;
                                Object[] objectPath = treePath.getPath();
                                Object[] testPath = path.getPath();
                                if (objectPath.length != testPath.length) {
                                        equals = false;
                                } else {
                                        for (int i = 0; i < testPath.length; i++) {
                                                if (!(testPath[i].equals(objectPath[i]))) {
                                                        equals = false;
                                                }
                                        }
                                }
                                if (equals) {
                                        return true;
                                }
                        }

                        return false;
                }

                private void maybeShowPopup(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                                //Update selection
                                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                                TreePath[] selectionPaths = tree.getSelectionPaths();
                                if (selectionPaths != null && path != null){
                                        if (!contains(selectionPaths, path)) {
                                                if (e.isControlDown()) {
                                                        tree.addSelectionPath(path);
                                                } else {
                                                        tree.setSelectionPath(path);
                                                }
                                        }
                                } else {
                                        tree.setSelectionPath(path);
                                }
                                //Clear selection state cache (optimisation)
                                selectionState.clear();
                                //Show popup
                                JPopupMenu menu = new JPopupMenu();
                                popupActions.copyEnabledActions(menu);
                                menu.show(e.getComponent(),
                                        e.getX(), e.getY());
                        }
                }
        }
}
