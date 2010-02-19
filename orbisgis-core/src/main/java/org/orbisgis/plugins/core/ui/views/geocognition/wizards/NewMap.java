package org.orbisgis.plugins.core.ui.views.geocognition.wizards;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.plugins.core.geocognition.GeocognitionElement;
import org.orbisgis.plugins.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.plugins.core.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.plugins.core.layerModel.DefaultMapContext;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.plugins.core.ui.views.geocognition.wizard.INewGeocognitionElement;
import org.orbisgis.plugins.images.IconLoader;

public class NewMap implements INewGeocognitionElement {

	public NewMap() {
	}

	@Override
	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionMapContextFactory() };
	}

	@Override
	public void runWizard() {
	}

	@Override
	public String getName() {
		return "Map";
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
					return IconLoader.getIcon("map.png");
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
		return new DefaultMapContext();
	}

	@Override
	public int getElementCount() {
		return 1;
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
		return "Map";
	}

}
