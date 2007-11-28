package org.orbisgis.geoview;

import java.util.ArrayList;

import org.orbisgis.core.wizards.WizardAndId;
import org.orbisgis.core.wizards.WizardGetter;
import org.orbisgis.geoview.layerModel.CRSException;
import org.orbisgis.geoview.layerModel.ILayer;
import org.orbisgis.geoview.layerModel.LayerException;
import org.orbisgis.pluginManager.PluginManager;
import org.orbisgis.pluginManager.ui.ChoosePanel;
import org.orbisgis.tools.ViewContext;
import org.sif.UIFactory;
import org.sif.UIPanel;

public class EPLayerWizardHelper {

	public static void openWizard(GeoView2D geoview) {
		ArrayList<WizardAndId<INewLayer>> wizards = getWizards(null);
		String[] names = new String[wizards.size()];
		String[] ids = new String[wizards.size()];
		for (int i = 0; i < names.length; i++) {
			names[i] = wizards.get(i).getWizard().getName();
			ids[i] = wizards.get(i).getId();
		}
		ChoosePanel cp = new ChoosePanel("Select the resource type", names, ids);
		boolean accepted = UIFactory.showDialog(cp);
		if (accepted) {
			int index = cp.getSelectedIndex();
			runWizard(geoview, wizards.get(index).getWizard());
		}
	}

	private static ILayer[] runWizard(GeoView2D geoview, INewLayer wizard) {
		UIPanel[] panels = wizard.getWizardPanels();
		boolean ok = UIFactory.showDialog(panels);
		if (ok) {
			ILayer[] layers = wizard.getLayers();
			ViewContext vc = geoview.getViewContext();
			ILayer lc = vc.getRootLayer();
			for (ILayer layer : layers) {
				try {
					lc.put(layer);
				} catch (CRSException e) {
					PluginManager.error("The new layer CRS does not "
							+ "have the same CRS as the existing layers", e);
				} catch (LayerException e) {
					PluginManager.error("Cannot add the layer in "
							+ lc.getName(), e);
				}
			}
			return layers;
		}

		return new ILayer[0];
	}

	public static ILayer[] runWizard(GeoView2D geoview, String wizardId) {
		ArrayList<WizardAndId<INewLayer>> wizards = getWizards(wizardId);
		return runWizard(geoview, wizards.get(0).getWizard());
	}

	private static ArrayList<WizardAndId<INewLayer>> getWizards(String id) {
		WizardGetter<INewLayer> wg = new WizardGetter<INewLayer>(
				"org.orbisgis.geoview.NewLayerWizard");
		return wg.getWizards(id);
	}

}
