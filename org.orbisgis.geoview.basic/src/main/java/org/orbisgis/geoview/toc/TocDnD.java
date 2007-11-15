package org.orbisgis.geoview.toc;

import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceTreeModel;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;

public class TocDnD {
//
//	public boolean drop(ResourceTreeModel model, IResource[] draggedNodes,
//			IResource dropNode) {
//		ILayer targetLayer = ((ILayerResource) dropNode).getLayer();
//
//		if (targetLayer.acceptsChilds()) {
//			System.out.println("Accepts childs");
//			for (IResource resource : draggedNodes) {
//				ILayer layer = ((ILayerResource) resource).getLayer();
//				layer.getParent().remove(layer);
//				try {
//					targetLayer.put(layer);
//				} catch (CRSException e) {
//					throw new RuntimeException("bug");
//				}
//			}
//			return true;
//		} else {
//			System.out.println("Does not accepts childs");
//			ILayer parent = targetLayer.getParent();
//			if (parent != null) {
//				boolean someMovement = false;
//				for (IResource resource : draggedNodes) {
//					int index = parent.getIndex(targetLayer);
//					ILayer layer = ((ILayerResource) resource).getLayer();
//					layer.getParent().remove(layer);
//					try {
//						parent.insertLayer(layer, index);
//					} catch (CRSException e) {
//						throw new RuntimeException("bug");
//					}
//				}
//
//				return someMovement;
//			}
//
//			return false;
//		}
//	}

}
