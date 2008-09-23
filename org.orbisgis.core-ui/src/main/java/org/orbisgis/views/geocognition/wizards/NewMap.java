package org.orbisgis.views.geocognition.wizards;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionMapContextFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.layerModel.DefaultMapContext;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewMap implements INewGeocognitionElement {

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
