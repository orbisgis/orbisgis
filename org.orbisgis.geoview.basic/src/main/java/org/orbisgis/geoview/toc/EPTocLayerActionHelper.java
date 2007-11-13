package org.orbisgis.geoview.toc;
import org.orbisgis.core.resourceTree.IResource;
import org.orbisgis.core.resourceTree.ResourceTreeActionExtensionPointHelper;
import org.orbisgis.geoview.GeoView2D;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.pluginManager.ExtensionPointManager;


public class EPTocLayerActionHelper extends ResourceTreeActionExtensionPointHelper {

	public static void execute(GeoView2D geoview, String actionId, IResource[] selectedResources) {
		ExtensionPointManager<ILayerAction> epm = new ExtensionPointManager<ILayerAction>(
				"org.orbisgis.geoview.toc.LayerAction");
		ILayerAction action = epm.instantiateFrom("/extension/action[@id='"
				+ actionId + "']", "class");
		if (selectedResources.length == 0) {
			action.execute(geoview, null);
		} else {
			for (IResource resource : selectedResources) {
				action.execute(geoview, ((ILayerResource) resource)
						.getLayer());
			}
		}
		ILayer[] layers = toLayerArray(selectedResources);
		action.executeAll(geoview, layers);

	}

	public static ILayer[] toLayerArray(IResource[] selectedResources) {
		ILayer[] layers = new ILayer[selectedResources.length];
		for (int i = 0; i < layers.length; i++) {
			layers[i] = ((ILayerResource) selectedResources[i]).getLayer();
		}
		return layers;
	}

}
