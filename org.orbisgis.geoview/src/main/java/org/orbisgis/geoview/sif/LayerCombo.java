package org.orbisgis.geoview.sif;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.ViewContext;
import org.sif.multiInputPanel.ComboBoxChoice;

public class LayerCombo extends ComboBoxChoice {

	public LayerCombo(ViewContext view) {
		final ILayer root = view.getLayerModel();
		final ILayer[] allLayers = root.getLayersRecursively();
		final String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();
		}
		setChoices(names);
	}
}