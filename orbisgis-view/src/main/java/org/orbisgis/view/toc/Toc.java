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

import java.awt.BorderLayout;
import java.beans.EventHandler;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.apache.log4j.Logger;
import org.gdms.data.DataSource;
import org.gdms.data.DataSourceListener;
import org.gdms.data.edition.EditionEvent;
import org.gdms.data.edition.EditionListener;
import org.gdms.data.edition.MultipleEditionEvent;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.events.Listener;
import org.orbisgis.core.layerModel.*;
import org.orbisgis.view.docking.DockingPanel;
import org.orbisgis.view.docking.DockingPanelParameters;
import org.orbisgis.view.edition.EditableElement;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.view.map.EditableTransferEvent;
import org.orbisgis.view.map.MapElement;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * @brief The Toc Panel component
 */

public class Toc extends JPanel implements DockingPanel  {
    //The UID must be incremented when the serialization is not compatible with the new version of this class
    private static final long serialVersionUID = 1L; 
    protected final static I18n I18N = I18nFactory.getI18n(Toc.class);
    private final static Logger LOGGER = Logger.getLogger("gui."+Toc.class);
    DockingPanelParameters dockingPanelParameters;
    private MapContext mapContext = null;
    private JTree tree;
    private TocTreeModel treeModel;
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
        dockingPanelParameters.setTitle(I18N.tr("orbisgis.view.toc.TocTitle"));
        dockingPanelParameters.setTitleIcon(OrbisGISIcon.getIcon("map"));
        
        //Initialise an empty tree
        add(new JScrollPane( makeTree()));

    }
    
    private void setTocSelection(MapContext mapContext) {
        ILayer[] selected = mapContext.getSelectedLayers();
        TreePath[] selectedPaths = new TreePath[selected.length];
        for (int i = 0; i < selectedPaths.length; i++) {
                selectedPaths[i] = new TreePath(selected[i].getLayerPath());
        }

        
        fireSelectionEvent.set(false);
        tree.setSelectionPaths(selectedPaths);
        fireSelectionEvent.set(true);
    }    
    /**
     * Create the Toc JTree
     * @return the Toc JTree
     */
    private JTree makeTree() {
        tree = new JTree();
        //Items can be selected freely
        tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        TocTransferHandler handler = new TocTransferHandler();
        //Add a drop listener
        handler.getTransferEditableEvent().addListener(this,
                EventHandler.create(Listener.class,this,"onDropEditableElement",""));
        tree.setTransferHandler(handler);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.setEditable(true);        
        setEmptyLayerModel(tree);
        tree.setCellRenderer(new TocRenderer(this));
        tree.setCellEditor(new TocEditor(tree));
        return tree;
    }
    /**
     * The user drop one or multiple EditableElement into the Toc Tree
     * @param editTransfer 
     */
    public void onDropEditableElement(EditableTransferEvent editTransfer) {
        LOGGER.info("Drop of "+editTransfer.getEditableList().length+" editables on node "+((JTree.DropLocation)editTransfer.getDropLocation()).getPath());
    }
    
    private void setEmptyLayerModel(JTree jTree) {
        //Add the treeModel
        DataManager dataManager = (DataManager) Services.getService(DataManager.class);
        treeModel = new TocTreeModel(dataManager.createLayerCollection("root"), //$NON-NLS-1$
				jTree);
        jTree.setModel(treeModel);
    }
    public DockingPanelParameters getDockingParameters() {
        return dockingPanelParameters;
    }

        public MapContext getMapContext() {
                return mapContext;
        }

        public void setEditableMap(MapElement newMapElement) {

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

			// Clear selection        
                        fireSelectionEvent.set(false);
                        tree.getSelectionModel().clearSelection();
                        fireSelectionEvent.set(true);
                        
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
    public JComponent getComponent() {
        return this;
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
					EditorManager em = Services.getService(EditorManager.class);
					IEditor[] editors = em.getEditor(new EditableLayer(element,
							lyr));
					for (IEditor editor : editors) {
						if (!em.closeEditor(editor)) {
							return false;
						}
					}
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
}
