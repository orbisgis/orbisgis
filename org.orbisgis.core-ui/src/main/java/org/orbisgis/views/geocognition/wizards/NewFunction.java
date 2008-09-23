package org.orbisgis.views.geocognition.wizards;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.sql.FunctionJavaCode;
import org.orbisgis.geocognition.sql.GeocognitionFunctionFactory;
import org.orbisgis.geocognition.sql.GeocognitionJavaFunction;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewFunction implements INewGeocognitionElement {

	private FunctionJavaCode code;

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				if (GeocognitionFunctionFactory.JAVA_FUNCTION_ID
						.equals(contentTypeId)) {
					String compile = properties
							.get(GeocognitionJavaFunction.COMPILE_RESULT);
					if ((compile == null)
							|| compile
									.equals(GeocognitionJavaFunction.COMPILE_OK)) {
						return IconLoader.getIcon("functionMap.png");
					} else {
						return IconLoader.getIcon("functionMapError.png");
					}

				} else {
					return null;
				}
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				if (GeocognitionFunctionFactory.JAVA_FUNCTION_ID
						.equals(contentTypeId)) {
					return IconLoader.getIcon("functionMap.png");
				} else {
					return null;
				}
			}

		};
	}

	@Override
	public GeocognitionElementFactory[] getFactory() {
		return null;
	}

	@Override
	public void runWizard() throws GeocognitionException {
		InputStream is = this.getClass().getResourceAsStream("NewJavaFunction");
		DataInputStream dis = new DataInputStream(is);
		try {
			byte[] buffer = new byte[dis.available()];
			dis.readFully(buffer);
			FunctionJavaCode code = new FunctionJavaCode(new String(buffer));
			this.code = code;
		} catch (IOException e) {
			throw new GeocognitionException("Cannot load default "
					+ "function template", e);
		}
	}

	@Override
	public String getName() {
		return "New Function";
	}

	@Override
	public String getBaseName(int elementIndex) {
		return "Function";
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
