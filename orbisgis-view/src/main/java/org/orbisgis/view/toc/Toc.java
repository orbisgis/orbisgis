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
package org.orbisgis.view.toc;

import com.vividsolutions.jts.geom.Envelope;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.xml.bind.JAXBElement;
import net.opengis.se._2_0.core.StyleType;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.gdms.data.types.Type;
import org.gdms.driver.DriverException;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.classification.ClassificationMethodException;
import org.orbisgis.core.renderer.se.SeExceptions;
import org.orbisgis.core.renderer.se.Style;
import org.orbisgis.progress.ProgressMonitor;
import org.orbisgis.sif.OpenFilePanel;
import org.orbisgis.sif.SaveFilePanel;
import org.orbisgis.sif.UIFactory;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.view.background.BackgroundJob;
import org.orbisgis.view.background.BackgroundManager;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.edition.EditorDockable;
import org.orbisgis.view.geocatalog.EditableSource;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.EditableTransferEvent;
import org.orbisgis.view.map.MapEditor;
import org.orbisgis.view.map.MapElement;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.LegendsPanel;
import org.orbisgis.view.toc.actions.cui.legend.EPLegendHelper;
import org.orbisgis.view.toc.actions.cui.legend.ILegendPanel;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


/**
 * @brief The Toc Panel component
 */
public class Toc extends JPanel implements EditorDockable {
        //The UID must be incremented when the serialization is not compatible with the new version of this class

        private static final long serialVersionUID = 1L;
        protected final static I18n I18N = I18nFactory.getI18n(Toc.class);
        private final static Logger LOGGER = Logger.getLogger("gui." + Toc.class);
        DockingPanelParameters dockingPanelParameters;
        private MapContext mapContext = null;
        private JTree tree;
        private TocTreeModel treeModel;
        private TocRenderer treeRenderer;
        //When this boolean is false, the selection event is not fired
        private AtomicBoolean fireSelectionEvent = new AtomicBoolean(true);
        //Listen for map context changes
        private TocMapContextListener tocMapContextListener = new TocMapContextListener();
        //Listen for all layers changes
        private TocLayerListener tocLayerListener = new TocLayerListener();
        //Loaded editable map
        private MapElement mapElement = null;

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

                //Initialise an empty tree
                add(new JScrollPane(makeTree()));

        }

        /**
         *
         * @return The editable map
         */
        public MapElement getMapElement() {
                return mapElement;
        }

        private void setTocSelection(MapContext mapContext) {
                ILayer[] selected = mapContext.getSelectedLayers();
                Style[] styles = mapContext.getSelectedStyles();
                TreePath[] selectedPaths = new TreePath[selected.length + styles.length];
                for (int i = 0; i < selected.length; i++) {
                        selectedPaths[i] = new TreePath(selected[i].getLayerPath());
                }
                for (int i = 0; i < styles.length; i++) {
                        Style s = styles[i];
                        ILayer[] lays = s.getLayer().getLayerPath();
                        Object[] objs = new Object[lays.length + 1];
                        System.arraycopy(lays, 0, objs, 0, lays.length);
                        objs[objs.length - 1] = s;
                        selectedPaths[i + selected.length] = new TreePath(objs);
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
                        EventHandler.create(Listener.class, this, "onDropEditableElement", ""));
                tree.setDragEnabled(true);
                tree.setTransferHandler(handler);
                tree.setRootVisible(false);
                tree.setShowsRootHandles(true);
                tree.setEditable(true);
                setEmptyLayerModel(tree);
                TocRenderer.install(tree); //Set the TreeCellEditorRenderer
                treeRenderer = (TocRenderer)tree.getCellRenderer();
                tree.setCellEditor(new TocTreeEditor(tree));
                tree.addMouseListener(new PopupMouselistener());
                //Add a tree selection listener
                tree.getSelectionModel().addTreeSelectionListener(EventHandler.create(TreeSelectionListener.class, this, "onTreeSelectionChange"));
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
                        if (node instanceof Style) {
                                dropNode = ((Style) node).getLayer();
                        } else if (node instanceof ILayer) {
                                dropNode = (ILayer) node;
                        } else {
                                throw new IllegalArgumentException("Drop node is not an instance of Style or ILayer");
                        }
                } else {
                        // By default drop on rootNode
                        dropNode = (ILayer) treeModel.getRoot();
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
                        BackgroundManager bm = (BackgroundManager) Services.getService(BackgroundManager.class);
                        bm.nonBlockingBackgroundOperation(new DropDataSourceListProcess(dropNode, index, sourceToDrop));
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
                                if (lastPathComponent instanceof ILayer) {
                                        layers.add((ILayer) lastPathComponent);
                                }
                        }
                }
                return layers;
        }

        /**
         * Cast all Style elements of provided tree paths
         *
         * @param selectedPaths Internal Tree path
         * @return All Style instances of provided path
         */
        private ArrayList<Style> getSelectedStyles(TreePath[] selectedPaths) {
                ArrayList<Style> rules = new ArrayList<Style>(selectedPaths.length);
                for (int i = 0; i < selectedPaths.length; i++) {
                        Object lastPathComponent = selectedPaths[i].getLastPathComponent();
                        if (lastPathComponent instanceof Style) {
                                rules.add(((Style) lastPathComponent));
                        }
                }
                return rules;
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
                                                Object node = path.getLastPathComponent();
                                                if(node instanceof ILayer) {
                                                        //Do not mix the selection
                                                        if(styles.isEmpty()) {
                                                                keptSelection.add(path);
                                                                layers.add((ILayer)node);
                                                        }
                                                } else if(node instanceof Style) {
                                                        //Do not mix the selection
                                                        if(layers.isEmpty()) {
                                                                keptSelection.add(path);
                                                                styles.add((Style)node);
                                                        }
                                                }
                                        }
                                }
                                //Update the two selection model
                                tree.getSelectionModel().setSelectionPaths(keptSelection.toArray(new TreePath[0]));
                                mapContext.setSelectedLayers(layers.toArray(new ILayer[0]));
                                mapContext.setSelectedStyles(styles.toArray(new Style[0]));
                        } finally {
                                fireSelectionEvent.set(true);
                        }
                }
                LOGGER.debug("There are "+mapContext.getSelectedLayers().length+" selected layers.");
        }

        private void setEmptyLayerModel(JTree jTree) {
                //Add the treeModel
                DataManager dataManager = (DataManager) Services.getService(DataManager.class);
                treeModel = new TocTreeModel(dataManager.createLayerCollection("root"), //$NON-NLS-1$
                        jTree);
                jTree.setModel(treeModel);
        }

        @Override
        public DockingPanelParameters getDockingParameters() {
                return dockingPanelParameters;
        }

        public MapContext getMapContext() {
                return mapContext;
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
                        this.mapContext.getLayerModel().removeLayerListenerRecursively(tocLayerListener);
                        this.mapContext.removeMapContextListener(tocMapContextListener);
                }


                if (newMapElement != null) {
                        this.mapContext = ((MapContext) newMapElement.getObject());

                        this.mapElement = newMapElement;
                        // Add the listeners to the new MapContext
                        this.mapContext.addMapContextListener(tocMapContextListener);
                        final ILayer root = this.mapContext.getLayerModel();
                        root.addLayerListenerRecursively(tocLayerListener);

                        treeModel = new TocTreeModel(root, tree);
                        // Apply treeModel and clear the selection        
                        fireSelectionEvent.set(false);
                        try {
                                tree.setModel(treeModel);
                                tree.getSelectionModel().clearSelection();
                        } finally {
                                fireSelectionEvent.set(true);
                        }

                        setTocSelection(mapContext);
                        //TODO ? repaint 
                }
        }

        boolean isActive(ILayer layer) {
                if (mapContext != null) {
                        return layer == mapContext.getActiveLayer();
                } else {
                        return false;
                }
        }

        @Override
        public JComponent getComponent() {
                return this;
        }

        /**
         * Called each time on popup trigger button
         *
         * @return The constructed PopUp menu, depending to the state of the
         * tree selection
         */
        private JPopupMenu makePopupMenu() {
                JPopupMenu popup = new JPopupMenu();
                Object selected = tree.getLastSelectedPathComponent();
                //Popup:delete layer
                if (tree.getSelectionCount() > 0 && selected instanceof ILayer) {
                        JMenuItem deleteLayer = new JMenuItem(I18N.tr("Remove layer"), OrbisGISIcon.getIcon("remove"));
                        deleteLayer.setToolTipText(I18N.tr("Remove the layer from the map context"));
                        deleteLayer.addActionListener(EventHandler.create(ActionListener.class, this, "onDeleteLayer"));
                        JMenuItem zoomToLayer = new JMenuItem(I18N.tr("Zoom to"), OrbisGISIcon.getIcon("magnifier"));
                        zoomToLayer.setToolTipText(I18N.tr("Zoom to the layer bounding box"));
                        zoomToLayer.addActionListener(EventHandler.create(ActionListener.class, this, "zoomToLayer"));
                        popup.add(zoomToLayer);
                        popup.add(deleteLayer);
                        if (tree.getSelectionCount() == 1) {
                                //display the menu to add a style from a file
                                JMenuItem importStyle = new JMenuItem(I18N.tr("Import style"), OrbisGISIcon.getIcon("add"));
                                importStyle.setToolTipText(I18N.tr("Import a style from a file."));
                                importStyle.addActionListener(EventHandler.create(ActionListener.class, this, "onImportStyle"));
                                popup.add(importStyle);
                        }
                }
                if (selected instanceof Style) {
                        makePopupStyle(popup);
                }
                return popup;
        }

        /**
         * If we've right-clicked on a style node
         *
         * @param popup
         */
        private void makePopupStyle(JPopupMenu popup) {
                //Display the menu to enter in the simple style editor.
                JMenuItem simpleEdtiorLayer = new JMenuItem(I18N.tr("Simple style edition"), OrbisGISIcon.getIcon("pencil"));
                simpleEdtiorLayer.setToolTipText(I18N.tr("Open the simple editor for SE styles"));
                simpleEdtiorLayer.addActionListener(EventHandler.create(ActionListener.class, this, "onSimpleEditor"));
                popup.add(simpleEdtiorLayer);
                //Display the menu to enter in the advanced style editor.
                JMenuItem advancedEditorLayer = new JMenuItem(I18N.tr("Adavanced style edition"), OrbisGISIcon.getIcon("pencil"));
                advancedEditorLayer.setToolTipText(I18N.tr("Open the adavanced editor for SE styles"));
                advancedEditorLayer.addActionListener(EventHandler.create(ActionListener.class, this, "onAdvancedEditor"));
                popup.add(advancedEditorLayer);
                //Display the menu to remove the currently selected style
                JMenuItem deleteStyle = new JMenuItem(I18N.tr("Remove style"), OrbisGISIcon.getIcon("remove"));
                deleteStyle.setToolTipText(I18N.tr("Remove this style from the associater layer."));
                deleteStyle.addActionListener(EventHandler.create(ActionListener.class, this, "onDeleteStyle"));
                popup.add(deleteStyle);
                //Export this style in a file
                JMenuItem exportStyle = new JMenuItem(I18N.tr("Export style"), OrbisGISIcon.getIcon("add"));
                exportStyle.setToolTipText(I18N.tr("Export this style from the associater layer."));
                exportStyle.addActionListener(EventHandler.create(ActionListener.class, this, "onExportStyle"));
                popup.add(exportStyle);

        }

        /**
         * The user click on delete layer menu item.
         */
        public void onDeleteLayer() {
                ILayer[] selectedResources = mapContext.getSelectedLayers();
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
                Envelope env = selectedResources[0].getEnvelope();
                for (ILayer resource : selectedResources) {
                        env.expandToInclude(resource.getEnvelope());
                }
                mapContext.setBoundingBox(env);
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

                        if (UIFactory.showDialog(outputXMLPanel)) {
                                String seFile = outputXMLPanel.getSelectedFile().getAbsolutePath();
                                style.export(seFile);
                        }
                }
        }

        /**
         * The user choose to import a style and to add it to the selected layer
         * through the dedicated menu.
         */
        public void onImportStyle() {
                try {
                        ILayer[] layers = mapContext.getSelectedLayers();
                        if (layers.length == 1) {
                                ILayer layer = layers[0];
                                Type typ = layer.getDataSource().getMetadata().getFieldType(layer.getDataSource().getSpatialFieldIndex());

                                final OpenFilePanel inputXMLPanel = new OpenFilePanel(
                                        "org.orbisgis.core.ui.editorViews.toc.actions.ImportStyle",
                                        "Choose a location");

                                inputXMLPanel.addFilter("se", "Symbology Encoding 2.0 (FeatureTypeStyle");

                                if (UIFactory.showDialog(inputXMLPanel)) {
                                        String seFile = inputXMLPanel.getSelectedFile().getAbsolutePath();
                                        try {
                                                layer.addStyle(0, new Style(layer, seFile));
                                        } catch (SeExceptions.InvalidStyle ex) {
                                                LOGGER.error(I18N.tr(ex.getLocalizedMessage()));
                                                String msg = ex.getMessage().replace("<", "\n    - ").replace(',', ' ').replace(": ", "\n - ");
                                                JOptionPane.showMessageDialog(null, msg,
                                                        "Error while loading the style", JOptionPane.ERROR_MESSAGE);
                                        }
                                }
                        }

                } catch (DriverException e) {
                        LOGGER.error("Error while loading the style", e);
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
                                // Obtain MapTransform
                                MapEditor editor = mapElement.getMapEditor();
                                MapTransform mt = editor.getMapControl().getMapTransform();
                                if (mt == null) {
                                        JOptionPane.showMessageDialog(null,I18N.tr("Advanced Editor can't be loaded"));
                                }

                                LegendUIController controller = new LegendUIController(style);

                                if (UIFactory.showDialog((UIPanel)controller.getMainPanel())) {
                                        layer.setStyle(0,controller.getEditedFeatureTypeStyle());
                                }
                        }
		} catch (SeExceptions.InvalidStyle ex) {
			LOGGER.error(I18N.tr("Error while editing the legend"), ex);
		}
        }

        public void onSimpleEditor() {
                TreePath selObjs = tree.getSelectionPath();
                if (selObjs.getLastPathComponent() instanceof Style) {
                        try {
                                Style style = (Style) selObjs.getLastPathComponent();
                                Layer layer = (Layer) style.getLayer();
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
                                                layer.setStyle(0, pan.getStyleWrapper().getStyle());
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

        @Override
        public void setEditableElement(EditableElement editableElement) {
                if (editableElement instanceof MapElement) {
                        MapElement importedMap = (MapElement) editableElement;
                        setEditableMap(importedMap);
                }
        }

        private class TocLayerListener implements LayerListener, EditionListener,
                DataSourceListener {

                @Override
                public void layerAdded(final LayerCollectionEvent e) {
                        for (final ILayer layer : e.getAffected()) {
                                layer.addLayerListenerRecursively(this);
                        }
                        treeModel.refresh();
                }

                @Override
                public void layerMoved(LayerCollectionEvent e) {
                        treeModel.refresh();
                }

                @Override
                public boolean layerRemoving(LayerCollectionEvent e) {
                        // Close editors
                        for (final ILayer layer : e.getAffected()) {
                                ILayer[] layers = new ILayer[]{layer};
                                if (layer.acceptsChilds()) {
                                        layers = layer.getLayersRecursively();
                                }
                                for (ILayer lyr : layers) {
                                        //TODO Close editors attached to this ILayer
                                        //Or do the job on the Editor (logic)
                                        /*
                                         * EditorManager em =
                                         * Services.getService(EditorManager.class);
                                         * IEditor[] editors = em.getEditor(new
                                         * EditableLayer(element, lyr)); for
                                         * (IEditor editor : editors) { if
                                         * (!em.closeEditor(editor)) { return
                                         * false; } }
                                         *
                                         */
                                }
                        }
                        return true;
                }

                @Override
                public void layerRemoved(final LayerCollectionEvent e) {
                        for (final ILayer layer : e.getAffected()) {
                                layer.removeLayerListenerRecursively(this);
                        }
                        treeModel.refresh();
                }

                @Override
                public void nameChanged(LayerListenerEvent e) {
                }

                @Override
                public void styleChanged(LayerListenerEvent e) {
                        treeModel.refresh();
                }

                @Override
                public void visibilityChanged(LayerListenerEvent e) {
                        treeModel.refresh();
                }

                @Override
                public void selectionChanged(SelectionEvent e) {
                        treeModel.refresh();
                }

                @Override
                public void multipleModification(MultipleEditionEvent e) {
                        treeModel.refresh();
                }

                @Override
                public void singleModification(EditionEvent e) {
                        treeModel.refresh();
                }

                @Override
                public void cancel(DataSource ds) {
                }

                @Override
                public void commit(DataSource ds) {
                        treeModel.refresh();
                }

                @Override
                public void open(DataSource ds) {
                        treeModel.refresh();
                }
        }

        private final class TocMapContextListener implements MapContextListener {

                @Override
                public void layerSelectionChanged(MapContext mapContext) {
                        setTocSelection(mapContext);
                }

                @Override
                public void activeLayerChanged(ILayer previousActiveLayer,
                        MapContext mapContext) {
                        treeModel.refresh();
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

                        DataManager dataManager = (DataManager) Services.getService(DataManager.class);
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
                                        if (path.getLastPathComponent() instanceof ILayer) {
                                                ILayer layer = (ILayer) path.getLastPathComponent();
                                                try {
                                                        layer.setVisible(!layer.isVisible());
                                                } catch (LayerException e1) {
                                                        LOGGER.error(e1);
                                                }
                                        } else if(path.getLastPathComponent() instanceof Style) {
                                                Style style = (Style) path.getLastPathComponent();
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

                private boolean contains(TreePath[] selectionPaths, TreePath path) {
                        for (TreePath treePath : selectionPaths) {
                                boolean equals = true;
                                Object[] objectPath = treePath.getPath();
                                Object[] testPath = path.getPath();
                                if (objectPath.length != testPath.length) {
                                        equals = false;
                                } else {
                                        for (int i = 0; i < testPath.length; i++) {
                                                if (testPath[i] != objectPath[i]) {
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
                                if ((selectionPaths != null) && (path != null)) {
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
                                makePopupMenu().show(e.getComponent(),
                                        e.getX(), e.getY());
                        }
                }
        }
}
