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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.StyleType;
import org.apache.log4j.Logger;
import org.h2gis.utilities.SFSUtilities;
import org.h2gis.utilities.TableLocation;
import org.orbisgis.core.Services;
import org.orbisgis.core.common.IntegerUnion;
import org.orbisgis.core.layerModel.BeanLayer;
import org.orbisgis.core.layerModel.ILayer;
import org.orbisgis.core.layerModel.Layer;
import org.orbisgis.core.layerModel.LayerCollection;
import org.orbisgis.core.layerModel.LayerCollectionEvent;
import org.orbisgis.core.layerModel.LayerException;
import org.orbisgis.core.layerModel.LayerListener;
import org.orbisgis.core.layerModel.LayerListenerEvent;
import org.orbisgis.core.layerModel.MapContext;
import org.orbisgis.core.layerModel.MapContextListener;
import org.orbisgis.core.layerModel.SelectionEvent;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.CompositeSymbolizer;
import org.orbisgis.core.renderer.se.Rule;
import org.orbisgis.core.renderer.se.SeExceptions;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.core.renderer.se.Symbolizer;
import org.orbisgis.mapeditorapi.MapElement;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.SIFWizard;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.sif.components.OpenFilePanel;
import org.orbisgis.sif.components.SaveFilePanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.background.ZoomToSelection;
import org.orbisgis.view.components.actions.ActionCommands;
import org.orbisgis.view.edition.EditableTransferEvent;
import org.orbisgis.view.edition.EditableTransferListener;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.table.TableEditableElementImpl;
import org.orbisgis.view.toc.actions.EditLayerSourceAction;
import org.orbisgis.view.toc.actions.LayerAction;
import org.orbisgis.view.toc.actions.StyleAction;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.SimpleStyleEditor;
import org.orbisgis.view.toc.actions.cui.legend.wizard.LegendWizard;
import org.orbisgis.viewapi.components.actions.DefaultAction;
import org.orbisgis.viewapi.docking.DockingPanelParameters;
import org.orbisgis.viewapi.edition.EditableElement;
import org.orbisgis.viewapi.edition.EditableSource;
import org.orbisgis.viewapi.edition.EditorDockable;
import org.orbisgis.viewapi.edition.EditorManager;
import org.orbisgis.viewapi.table.TableEditableElement;
import org.orbisgis.viewapi.toc.ext.TocActionFactory;
import org.orbisgis.viewapi.toc.ext.TocExt;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * The Toc Panel component
 */
public class Toc extends JPanel implements EditorDockable, TocExt {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        private static final I18n I18N = I18nFactory.getI18n(Toc.class);
        private static final Logger LOGGER_POPUP = Logger.getLogger("gui.popup" + Toc.class);
        private static final Logger LOGGER = Logger.getLogger("gui." + Toc.class);
        private DockingPanelParameters dockingPanelParameters;
        private transient MapContext mapContext = null;
        private JTree tree;
        private transient DefaultTreeModel treeModel;
        private transient org.orbisgis.view.toc.TocRenderer treeRenderer;
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
        private PropertyChangeListener modificationListener = EventHandler.create(PropertyChangeListener.class,this,"onMapModified","");
        private Action saveAction;
        // Actions containers
        private ActionCommands popupActions = new ActionCommands();
        private PropertyChangeListener mapContextPropertyChange = EventHandler.create(PropertyChangeListener.class,this,"onMapContextPropertyChange","");
        private EditorManager editorManager;
        /**
         * Constructor
         */
        public Toc(EditorManager editorManager) {
                super(new BorderLayout());
                this.editorManager = editorManager;
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
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ZOOM_TO, I18N.tr("Zoom to"),
                    I18N.tr("Zoom to the layer bounding box"), OrbisGISIcon.getIcon("magnifier"),
                    EventHandler.create(ActionListener.class, this, "zoomToLayer"), null)
                        .setLogicalGroup(TocActionFactory.G_ZOOM));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ZOOM_TO_SELECTION,
                    I18N.tr("Zoom to selection"), I18N.tr("Zoom to selected geometries"),
                    OrbisGISIcon.getIcon("zoom_selected"),
                    EventHandler.create(ActionListener.class, this, "zoomToLayerSelection"), null)
                        .setOnLayerWithRowSelection(true)
                        .setLogicalGroup(TocActionFactory.G_ZOOM));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_CLEAR_SELECTION,
                    I18N.tr("Clear selection"), I18N.tr("Clear the selected geometries"),
                    OrbisGISIcon.getIcon("edit-clear"),
                    EventHandler.create(ActionListener.class,this, "clearLayerRowSelection"),null)
                        .setOnLayerWithRowSelection(true)
                        .setLogicalGroup(TocActionFactory.G_SELECTION));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_IMPORT_STYLE,
                    I18N.tr("Import style"), I18N.tr("Import a style from a file."),
                    OrbisGISIcon.getIcon("palette_import"),
                    EventHandler.create(ActionListener.class, this, "onImportStyle"),null)
                        .setSingleSelection(true)
                        .setOnRealLayerOnly(true)
                        .setOnVectorSourceOnly(true)
                        .setLogicalGroup(TocActionFactory.G_STYLE));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_ADD_STYLE,
                    I18N.tr("Create a thematic map"), I18N.tr("Add a new legend."),
                    OrbisGISIcon.getIcon("palette_add"),
                    EventHandler.create(ActionListener.class, this, "onAddStyle"),null)
                        .setSingleSelection(true)
                        .setOnRealLayerOnly(true)
                        .setOnVectorSourceOnly(true)
                        .setLogicalGroup(TocActionFactory.G_STYLE));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_OPEN_ATTRIBUTES,
                    I18N.tr("Open the attributes"), I18N.tr("Open a spreadsheet view of the attributes."),
                    OrbisGISIcon.getIcon("openattributes"),
                    EventHandler.create(ActionListener.class,this, "onMenuShowTable"),null)
                        .setOnRealLayerOnly(true)
                        .setOnVectorSourceOnly(true)
                        .setLogicalGroup(TocActionFactory.G_ATTRIBUTES));
            // DataSource Drawing Actions
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_EDIT_GEOMETRY,
                    I18N.tr("Start editing"), I18N.tr("The edit geometry toolbar will update this layer's data source."),
                    OrbisGISIcon.getIcon("pencil"),
                    EventHandler.create(ActionListener.class,this, "onMenuSetActiveLayer"),null)
                        .setEnabledOnNotActiveLayer(true)
                        .setSingleSelection(true));
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_STOP_EDIT_GEOMETRY,
                    I18N.tr("Stop editing"), I18N.tr("Close the edit geometry toolbar."),
                    OrbisGISIcon.getIcon("stop"),
                    EventHandler.create(ActionListener.class,this, "onMenuUnsetActiveLayer"),null)
                        .setEnabledOnActiveLayer(true)
                        .setSingleSelection(true));
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_SAVE_EDIT_GEOMETRY,
                    I18N.tr("Save modifications"), I18N.tr("Apply the data source modifications"),
                    OrbisGISIcon.getIcon("save"),
                    EventHandler.create(ActionListener.class,this, "onMenuCommitDataSource"),null)
                        .setEnabledOnModifiedLayer(true));
            popupActions.addAction(new EditLayerSourceAction(this,TocActionFactory.A_CANCEL_EDIT_GEOMETRY,
                    I18N.tr("Cancel modifications"), I18N.tr("Undo all data source modifications"),
                    OrbisGISIcon.getIcon("cancel"),
                    EventHandler.create(ActionListener.class,this, "onMenuSyncDataSource"),null)
                        .setEnabledOnModifiedLayer(true));


            popupActions.addAction(new LayerAction(this,TocActionFactory.A_ADD_LAYER_GROUP,
                    I18N.tr("Add layer group"),I18N.tr("Add layer group to the map context"),
                    OrbisGISIcon.getIcon("add"),
                    EventHandler.create(ActionListener.class, this, "onAddGroup"),null)
                        .setOnLayerGroup(true)
                        .setSingleSelection(true)
                        .setOnEmptySelection(true)
                        .setLogicalGroup(TocActionFactory.G_LAYER_GROUP));
            popupActions.addAction(new LayerAction(this, TocActionFactory.A_REMOVE_LAYER, I18N.tr("Remove layer"),
                        I18N.tr("Remove the layer from the map context"),OrbisGISIcon.getIcon("remove"),
                        EventHandler.create(ActionListener.class, this, "onDeleteLayer"),null)
                        .setLogicalGroup(TocActionFactory.G_REMOVE));
            // Style actions
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_ADD_LEGEND,
                    I18N.tr("Create a thematic map"), I18N.tr("Add a legend in this style"),
                    OrbisGISIcon.getIcon("palette_add"),
                    EventHandler.create(ActionListener.class, this, "onAddLegend"),null).setOnSingleStyleSelection(true));
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_SIMPLE_EDITION,
                    I18N.tr("Simple style editor"), I18N.tr("Open the simple editor for SE styles"),
                    OrbisGISIcon.getIcon("palette_edit"),
                    EventHandler.create(ActionListener.class, this, "onSimpleEditor"),null).setOnSingleStyleSelection(true));
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_ADVANCED_EDITION,
                    I18N.tr("Advanced style editor"), I18N.tr("Open the advanced editor for SE styles"),
                    OrbisGISIcon.getIcon("palette_edit"),
                    EventHandler.create(ActionListener.class, this, "onAdvancedEditor"),null).setOnSingleStyleSelection(true));
            popupActions.addAction(new StyleAction(this,TocActionFactory.A_REMOVE_STYLE,
                    I18N.tr("Remove style"), I18N.tr("Remove this style from the associated layer."),
                    OrbisGISIcon.getIcon("palette_remove"),
                    EventHandler.create(ActionListener.class, this, "onDeleteStyle"),null));
            popupActions.addAction(new StyleAction(this, TocActionFactory.A_EXPORT_STYLE,
                    I18N.tr("Export style"), I18N.tr("Export this style from the associated layer."),
                    OrbisGISIcon.getIcon("palette_export"),
                    EventHandler.create(ActionListener.class, this, "onExportStyle"), null).setOnSingleStyleSelection(true));
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
         * The user starts or stops editing a layer's geometries
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
            // TODO clear UNDO/REDO history
            /*
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
            */
        }

        /**
         * The user cancels modifications on layer node
         */
        public void onMenuSyncDataSource() {
            // TODO UNDO All commands
            /*
            int response = JOptionPane.showConfirmDialog(UIFactory.getMainFrame(),
                    I18N.tr("Are you sure to cancel all your modifications ?"),
                    I18N.tr("Return to the original state"),
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(response==JOptionPane.YES_OPTION) {
                if(mapContext!=null) {
                    List<ILayer> selectedLayers = getSelectedLayers();
                    for(ILayer layer : selectedLayers) {
                        DataSource dataSource = layer.getDataSource();
                        if(dataSource!=null) {
                            try {
                                dataSource.syncWithSource();
                            } catch (DriverException ex) {
                                LOGGER.error(ex.getLocalizedMessage(),ex);
                            }
                        }
                    }
                }
            }
            */
        }
        /**
         * The user starts or stops editing a layer's geometries
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
                        EventHandler.create(EditableTransferListener.class, this, "onDropEditableElement", ""));
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
        }

        /**
         * @return A list of selected layers, same as {@link org.orbisgis.core.layerModel.MapContext#getSelectedLayers()}
         */
        public List<ILayer> getSelectedLayers() {
                return Arrays.asList(mapContext.getSelectedLayers());
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
                                List<Style> styles = new ArrayList<>();
                                List<ILayer> layers = new ArrayList<>();
                                List<TreePath> keptSelection = new ArrayList<>();
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
                jTree.setModel(new DefaultTreeModel(new TocTreeNodeLayer(new LayerCollection("empty"))));
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
         * @param newMapElement Map element to load into toc tree.
         */
        public void setEditableMap(MapElement newMapElement) {
                if (newMapElement != null) {
                        MapContext importedMap = ((MapContext) newMapElement.getObject());
                        if (!importedMap.isOpen()) {
                                try {
                                        importedMap.open(null);
                                } catch (LayerException | IllegalStateException ex) {
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
                        //TODO ? repaint 
                }
        }

        /**
         * Get the path for a node
         * @param node
         * @return TreePath to use with this JTree
         */
        private TreePath getPathFromNode(TreeNode node) {
            List<Object> nodeHierarchy = new LinkedList<Object>();
            nodeHierarchy.add(node);
            TreeNode parent = node.getParent();
            while(parent!=null) {
                nodeHierarchy.add(parent);
                parent = parent.getParent();
            }
            Collections.reverse(nodeHierarchy);
            return new TreePath(nodeHierarchy.toArray(new Object[nodeHierarchy.size()]));
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
        public JTree getTree() {
            return tree;
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
                for (ILayer layer : layers) {
                        String table = layer.getTableReference();
                        if(table!=null && !table.isEmpty()) {
                                TableEditableElement tableDocument = new TableEditableElementImpl(layer.getTableReference(), layer.getDataManager());
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
         * Called by EventHandler. This methods opens a wizard for the configuration
         * of a legend that will be added to the selected style in a dedicated Rule.
         */
        public void onAddLegend(){
            Style[] styles = mapContext.getSelectedStyles();
            if(styles.length == 1){
                Style base = styles[0];
                BeanLayer l = (BeanLayer) base.getLayer();
                MapTransform mt = new MapTransform();
                LegendWizard lw = new LegendWizard();
                SIFWizard wizard = lw.getSIFWizard(l, mt);
                //Show a wizard to add new thematic map in a Layer
                if(UIFactory.showWizard(wizard)){
                    Symbolizer sym = lw.getSymbolizer();
                    Rule r = new Rule(l);
                    CompositeSymbolizer cs = r.getCompositeSymbolizer();
                    Symbolizer def = cs.getSymbolizerList().get(0);
                    cs.removeSymbolizer(def);
                    cs.addSymbolizer(sym);
                    base.addRule(r);
                    l.onStyleChanged(new PropertyChangeEvent(base, ILayer.PROP_STYLES, base, base));
                }
            }
        }     

        /**
         * Add a new default style to the selected layer.
         */
        public void onAddStyle() {
                ILayer[] layers = mapContext.getSelectedLayers();
                if (layers.length == 1) {
                    LegendWizard lw = new LegendWizard();
                    ILayer layer = layers[0];
                    if (isStyleAllowed(layer)) {
                        MapTransform mt = new MapTransform();
                        SIFWizard wizard = lw.getSIFWizard(layer, mt);
                        if(UIFactory.showWizard(wizard)){
                            Style s = lw.getStyle();
                            layer.addStyle(s);
                        }
                    } else {
                        LOGGER.info(I18N.tr("This functionality is not supported."));
                    }

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
                        if (isStyleAllowed(layer)){
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
                        }}
                        else{
                            LOGGER.info("This functionality is not supported.");
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
                    if(isStyleAllowed(layer)){
                        int index = layer.indexOf(style);
                        LegendUIController controller = new LegendUIController(index,style);
                        if (UIFactory.showDialog((UIPanel)controller.getMainPanel())) {
                            layer.setStyle(index,controller.getEditedFeatureTypeStyle());
                        }
                    }else{
                        LOGGER.info("This functionality is not supported.");
                    }
                }
            } catch (SeExceptions.InvalidStyle ex) {
                LOGGER.error(I18N.tr("Error while editing the legend"), ex);
            }
        }

        /**
         * Opens the simple editor, if we have only known configurations.
         */
        public void onSimpleEditor() {
                TreePath selObjs = tree.getSelectionPath();
                if (selObjs.getLastPathComponent() instanceof TocTreeNodeStyle) {
                        try {
                                Style style = ((TocTreeNodeStyle) selObjs.getLastPathComponent()).getStyle();
                                final Layer layer = (Layer) style.getLayer();
                                if(isStyleAllowed(layer)){
                                    final int index = layer.indexOf(style);
                                    //In order to be able to cancel all of our modifications,
                                    //we produce a copy of our style.
                                    JAXBElement<StyleType> jest = style.getJAXBElement();

                                    MapTransform mt = new MapTransform();
                                    int geometryType;
                                    TableLocation tableLocation = TableLocation.parse(layer.getTableReference());
                                    try(Connection connection = mapContext.getDataManager().getDataSource().getConnection()) {
                                        geometryType = SFSUtilities.getGeometryType(connection, tableLocation,"");
                                    }
                                    Style copy = new Style(jest, layer);

                                    final SimpleStyleEditor pan = new SimpleStyleEditor(mt, geometryType, layer, copy);
                                    ActionListener apply = new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent actionEvent) {
                                            Style s1 = pan.getStyleWrapper().getStyle();
                                            JAXBElement<StyleType> jaxbElement = s1.getJAXBElement();
                                            try {
                                                Style s2 = new Style(jaxbElement, layer);
                                                layer.setStyle(index, s2);
                                            } catch (SeExceptions.InvalidStyle invalidStyle) {
                                                LOGGER.error(I18N.tr("You produced an invalid style while copying " +
                                                        "a valid one. Things are getting really wrong here."));
                                            }
                                        }
                                    };
                                    if (UIFactory.showApplyDialog(pan, apply, false)) {
                                        layer.setStyle(index, pan.getStyleWrapper().getStyle());
                                    }
                                }else{
                                    LOGGER.info(I18N.tr("Styles can be set only on vector layers."));
                                }
                        } catch (SeExceptions.InvalidStyle sis) {
                                //I don't know how this could happen : we are creating a style
                                //from a valid style. Should be valid too, consequently...
                                LOGGER.error(I18N.tr("The style you're trying to edit is not valid !"));
                        } catch (SQLException de) {
                                LOGGER.error(I18N.tr("An error occurred while processing the DataSource"));
                        } catch (UnsupportedOperationException uoe){
                                 LOGGER_POPUP.info(I18N.tr("Cannot create the user interface for this style. \n"
                                         + "Please uses the advanced style editor."), uoe);
                        }
                }
        }

        @Override
        public boolean match(EditableElement editableElement) {
                return editableElement instanceof MapElement;
        }

        @Override
        public EditableElement getEditableElement() {
                return mapElement;
        }

        /**
         * Search in the layer the provided data source name
         * @param layer
         * @param sourceName
         */
        private boolean hasDataSource(ILayer layer,String sourceName) {
                String table = layer.getTableReference();
                if(table!=null && !table.isEmpty()) {
                        return table.equalsIgnoreCase(sourceName);
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

       /**
         * Return true is the layer is vectorial because of only SE geometry style
        * is currently supported
         */
        private boolean isStyleAllowed(ILayer layer) {
            return layer.getTableReference() != null && !layer.getTableReference().isEmpty();
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
                        List<TreePath> dropPaths = new ArrayList<TreePath>(draggedResources.size());
                        for (int i = 0; i < draggedResources.size(); i++) {
                                String sourceName = draggedResources.get(i).getId();
                                if (pm.isCancelled()) {
                                        break;
                                } else {
                                        pm.progressTo(100 * i / draggedResources.size());
                                        try {
                                                    ILayer nl = mapContext.createLayer(sourceName);
                                                    dropNode.insertLayer(nl, dropIndex);
                                                    dropPaths.add(getPathFromNode(new TocTreeNodeLayer(nl)));
                                        } catch (Exception e) {
                                                throw new RuntimeException(I18N.tr("Cannot add the layer to the destination"), e);
                                        }
                                }
                        }
                        treeModel.nodeChanged(new TocTreeNodeLayer(dropNode));
                        // Select the new layer(s) if there is no selection
                        if(tree.getSelectionCount()==0) {
                            tree.setSelectionPaths(dropPaths.toArray(new TreePath[dropPaths.size()]));
                        }
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
                                //Show popup
                                JPopupMenu menu = new JPopupMenu();
                                popupActions.copyEnabledActions(menu);
                                menu.show(e.getComponent(),
                                        e.getX(), e.getY());
                        }
                }
        }
}
