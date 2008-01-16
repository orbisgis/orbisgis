package org.orbisgis.geoview.sif;

import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.VectorLayer;
import org.orbisgis.tools.ViewContext;
import org.sif.multiInputPanel.ComboBoxChoice;
import org.sif.multiInputPanel.InputType;

public class VectorLayerCombo extends ComboBoxChoice implements InputType {

	public VectorLayerCombo(ViewContext view) {
		ILayer[] allLayers = view.getVectorLayers();
		
		String[] names = new String[allLayers.length];
		for (int i = 0; i < names.length; i++) {
			names[i] = allLayers[i].getName();
			
		}
		setChoices(names);
	}

}
