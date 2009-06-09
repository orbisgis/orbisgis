package org.orbisgis.core.ui.views.geocognition.wizards;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.core.Services;
import org.orbisgis.core.renderer.legend.Legend;
import org.orbisgis.core.renderer.legend.carto.LegendManager;
import org.orbisgis.core.ui.components.sif.ChoosePanel;
import org.orbisgis.core.ui.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.core.ui.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.symbology.GeocognitionLegendFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.sif.UIFactory;

public class NewLegend implements INewGeocognitionElement {

	private Object legend;

	@Override
	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionLegendFactory() };
	}

	@Override
	public void runWizard() {
		LegendManager legendManager = Services.getService(LegendManager.class);
		Legend[] availableLegends = legendManager.getAvailableLegends();
		String[] names = new String[availableLegends.length];
		String[] ids = new String[availableLegends.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = availableLegends[i].getLegendTypeId();
			names[i] = availableLegends[i].getLegendTypeName();
		}
		ChoosePanel cp = new ChoosePanel("Select the legend type", names, ids);
		if (UIFactory.showDialog(cp)) {
			Legend legend = legendManager.getNewLegend(ids[cp
					.getSelectedIndex()]);
			this.legend = legend;
		} else {
			this.legend = null;
		}
	}

	@Override
	public String getName() {
		return "Legend";
	}

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				return getDefaultIcon(contentTypeId);
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				if (getFactory()[0].acceptContentTypeId(contentTypeId)) {
					return IconLoader.getIcon("palette.png");
				} else {
					return null;
				}
			}

			@Override
			public String getTooltip(GeocognitionElement element) {
				return null;
			}

		};
	}

	@Override
	public Object getElement(int index) {
		return legend;
	}

	@Override
	public int getElementCount() {
		return (legend != null) ? 1 : 0;
	}

	@Override
	public String getFixedName(int index) {
		return null;
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	@Override
	public String getBaseName(int elementIndex) {
		return "Legend";
	}

}
