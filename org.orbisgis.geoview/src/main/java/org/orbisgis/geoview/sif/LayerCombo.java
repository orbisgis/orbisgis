package org.orbisgis.geoview.sif;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.ViewContext;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.InputType;

public class LayerCombo extends ComboBoxChoice implements InputType {

	public LayerCombo(ViewContext view) {
		ILayer root = view.getRootLayer();
		ILayer[] allLayers = root.getLayersRecursively();
		String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();
		}
		setChoices(names);
	}

}
