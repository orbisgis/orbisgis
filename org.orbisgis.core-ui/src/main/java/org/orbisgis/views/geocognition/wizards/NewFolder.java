package org.orbisgis.views.geocognition.wizards;

import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.geocognition.Folder;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewFolder implements INewGeocognitionElement {

	@Override
	public GeocognitionElementFactory getFactory() {
		return null;
	}

	@Override
	public void runWizard() {
	}

	@Override
	public String getName() {
		return "Folder";
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
				if (contentTypeId.equals("org.orbisgis.geocognition.Folder")) {
					return IconLoader.getIcon("folder.png");
				} else {
					return null;
				}
			}

		};
	}

	@Override
	public Object getElement(int index) {
		return new Folder();
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
	public String getBaseName() {
		return "Folder";
	}
}
