package org.orbisgis.geoview.sif;

import org.gdms.driver.DriverException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.tools.ViewContext;
import org.sif.multiInputPanel.ComboBoxChoice;

public class RasterLayerCombo extends ComboBoxChoice {
	public RasterLayerCombo(ViewContext view) throws DriverException {
		final ILayer[] allLayers = view.getLayerModel().getRasterLayers();
		final String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();

		}
		setChoices(names);
	}
}