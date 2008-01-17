package org.orbisgis.geoview.sif;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.ViewContext;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.InputType;

public class RasterLayerCombo extends ComboBoxChoice implements InputType {

	public RasterLayerCombo(ViewContext view) {

		ILayer[] allLayers = view.getLayerModel().getRasterLayers();
		String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();

		}
		setChoices(names);
	}

}
