package org.orbisgis.ui.sif;

import org.gdms.driver.DriverException;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.layerModel.MapContext;
import org.sif.multiInputPanel.ComboBoxChoice;

public class VectorLayerCombo extends ComboBoxChoice {
	public VectorLayerCombo(MapContext view) throws DriverException {
		final ILayer[] allLayers = view.getLayerModel().getVectorLayers();
		final String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();
		}
		setChoices(names);
	}
}