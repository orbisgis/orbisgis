package org.orbisgis.core.ui.views.geocatalog.newSourceWizards.wms;

import java.util.ArrayList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.gvsig.remoteClient.wms.WMSClient;
import org.gvsig.remoteClient.wms.WMSLayer;

public class WMSLayerTreeModel implements TreeModel {

	private WMSClient client;
	private ArrayList<TreeModelListener> listeners = new ArrayList<TreeModelListener>();

	public WMSLayerTreeModel(WMSClient client) {
		this.client = client;
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		return ((WMSLayer) parent).getChildren().get(index);
	}

	@Override
	public int getChildCount(Object parent) {
		return ((WMSLayer) parent).getChildren().size();
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return ((WMSLayer) parent).getChildren().indexOf(child);
	}

	@Override
	public Object getRoot() {
		return client.getRootLayer();
	}

	@Override
	public boolean isLeaf(Object node) {
		return ((WMSLayer) node).getChildren().size() == 0;
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
	}

}
