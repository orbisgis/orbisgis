package org.orbisgis.views.geocognition.wizards;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.actions.ActionCode;
import org.orbisgis.geocognition.actions.GeocognitionActionElement;
import org.orbisgis.geocognition.actions.GeocognitionActionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewAction implements INewGeocognitionElement {

	private ActionCode code;

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				if (GeocognitionActionElementFactory.ACTION_ID
						.equals(contentTypeId)) {
					String compile = properties
							.get(GeocognitionActionElement.COMPILE_RESULT);
					if ((compile == null)
							|| compile
									.equals(GeocognitionActionElement.COMPILE_OK)) {
						return IconLoader.getIcon("action.png");
					} else {
						return IconLoader.getIcon("actionError.png");
					}

				} else {
					return null;
				}
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				if (GeocognitionActionElementFactory.ACTION_ID
						.equals(contentTypeId)) {
					return IconLoader.getIcon("play.png");
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
	public GeocognitionElementFactory[] getFactory() {
		return new GeocognitionElementFactory[] { new GeocognitionActionElementFactory() };
	}

	@Override
	public void runWizard() throws GeocognitionException {
		InputStream is = this.getClass().getResourceAsStream("NewAction");
		DataInputStream dis = new DataInputStream(is);
		try {
			byte[] buffer = new byte[dis.available()];
			dis.readFully(buffer);
			ActionCode code = new ActionCode(new String(buffer));
			this.code = code;
		} catch (IOException e) {
			throw new GeocognitionException("Cannot load default "
					+ "action template", e);
		}
	}

	@Override
	public String getName() {
		return "New Action";
	}

	@Override
	public String getBaseName(int elementIndex) {
		return "Action";
	}

	@Override
	public Object getElement(int index) {
		return code;
	}

	@Override
	public int getElementCount() {
		return (code != null) ? 1 : 0;
	}

	@Override
	public String getFixedName(int index) {
		return null;
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return true;
	}

}
