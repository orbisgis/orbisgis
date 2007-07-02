package org.orbisgis.plugin.view.ui.workbench;

import java.util.ArrayList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.orbisgis.plugin.view.layerModel.BasicLayer;
import org.orbisgis.plugin.view.layerModel.ILayer;
import org.orbisgis.plugin.view.layerModel.ILayerAction;
import org.orbisgis.plugin.view.layerModel.LayerCollection;
import org.orbisgis.plugin.view.layerModel.LayerCollectionEvent;
import org.orbisgis.plugin.view.layerModel.LayerCollectionListener;
import org.orbisgis.plugin.view.layerModel.LayerListenerEvent;

public class LayerTreeModel implements TreeModel {
	private ILayer root;

	private LayerListener layerListener;

	private TOC toc;

	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public LayerTreeModel(final ILayer root) {
		this.root = root;
		layerListener = new LayerListener();
		listen(root);
	}

	public void setTree(TOC tree) {
		this.toc = tree;
	}

	private void listen(ILayer node) {
		LayerCollection.processLayersNodes(node, new ILayerAction() {
			public void action(ILayer layer) {
				layer.addLayerListener(layerListener);
				if (layer instanceof LayerCollection) {
					((LayerCollection) layer)
							.addCollectionListener(layerListener);
				}
			}
		});
	}

	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	public Object getChild(Object parent, int index) {
		if ((0 <= index) && (index < getChildCount(parent))) {
			assert parent instanceof LayerCollection;
			return ((LayerCollection) parent).getLayerByIndex(index);
		} else {
			return null;
		}
	}

	public int getChildCount(Object parent) {
		if (parent instanceof LayerCollection) {
			return ((LayerCollection) parent).size();
		} else {
			return 0;
		}
	}

	public int getIndexOfChild(Object parent, Object child) {
		int n = getChildCount(parent);
		for (int index = 0; index < n; index++) {
			if (getChild(parent, index).equals(child))
				return index;
		}
		return -1;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isRoot(Object node) {
		return root.equals(node);
	}

	/**
	 * May return false even if the node has no children (a LayerCollection with
	 * no embedded layer)
	 */
	public boolean isLeaf(Object node) {
		return (node instanceof BasicLayer) ? true : false;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	private class LayerListener implements LayerCollectionListener,
			org.orbisgis.plugin.view.layerModel.LayerListener {

		public void layerAdded(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.addLayerListener(this);
			}
			fireChange();
		}

		private void fireChange() {
			TreeModelEvent event = new TreeModelEvent(this, new TreePath(
					getRoot()));
			for (TreeModelListener listener : listeners) {
				listener.treeStructureChanged(event);
			}
		}

		public void layerMoved(LayerCollectionEvent listener) {
		}

		public void layerRemoved(LayerCollectionEvent listener) {
			for (ILayer layer : listener.getAffected()) {
				layer.removeLayerListener(this);
			}
		}

		public void nameChanged(LayerListenerEvent e) {
			toc.repaint();
		}

		public void visibilityChanged(LayerListenerEvent e) {
			toc.repaint();
		}

		public void styleChanged(LayerListenerEvent e) {
		}
	}
}