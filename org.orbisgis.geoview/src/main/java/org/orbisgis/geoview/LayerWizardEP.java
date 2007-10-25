package org.orbisgis.geoview;

import java.util.ArrayList;

import org.orbisgis.core.ChoosePanel;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.pluginManager.ExtensionPointManager;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class LayerWizardEP {

	public static ILayer[] openWizard(GeoView2D geoview) {
		ExtensionPointManager<INewLayer> epm = new ExtensionPointManager<INewLayer>(
				"org.orbisgis.geoview.NewLayerWizard");
		ArrayList<INewLayer> wizards = epm.getInstancesFrom(
				"/extension/wizard", "class");
		String[] names = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getName();
		}
		ChoosePanel cp = new ChoosePanel("Select the layer type", names);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			INewLayer wizard = wizards.get(index);
			UIPanel[] panels = wizard.getWizardPanels();
			boolean ok = UIFactory.showDialog(panels);
			if (ok) {
				ILayer[] layers = wizard.getLayers();
				return layers;
			}
		}

		return new ILayer[0];
	}

}
