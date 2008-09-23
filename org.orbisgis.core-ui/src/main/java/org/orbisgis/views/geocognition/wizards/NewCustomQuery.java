package org.orbisgis.views.geocognition.wizards;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.swing.Icon;

import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.mapContext.GeocognitionException;
import org.orbisgis.geocognition.sql.CustomQueryJavaCode;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.geocognition.sql.GeocognitionJavaCustomQuery;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewCustomQuery implements INewGeocognitionElement {

	private CustomQueryJavaCode code;

	@Override
	public ElementRenderer getElementRenderer() {
		return new ElementRenderer() {

			@Override
			public Icon getIcon(String contentTypeId,
					Map<String, String> properties) {
				if (GeocognitionCustomQueryFactory.JAVA_QUERY_ID
						.equals(contentTypeId)) {
					String compile = properties
							.get(GeocognitionJavaCustomQuery.COMPILE_RESULT);
					if ((compile == null)
							|| compile
									.equals(GeocognitionJavaCustomQuery.COMPILE_OK)) {
						return IconLoader.getIcon("customQueryMap.png");
					} else {
						return IconLoader.getIcon("customQueryMapError.png");
					}

				} else {
					return null;
				}
			}

			@Override
			public Icon getDefaultIcon(String contentTypeId) {
				if (GeocognitionCustomQueryFactory.JAVA_QUERY_ID
						.equals(contentTypeId)) {
					return IconLoader.getIcon("customQueryMap.png");
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
		InputStream is = this.getClass().getResourceAsStream(
				"NewJavaCustomQuery");
		DataInputStream dis = new DataInputStream(is);
		try {
			byte[] buffer = new byte[dis.available()];
			dis.readFully(buffer);
			CustomQueryJavaCode code = new CustomQueryJavaCode(new String(
					buffer));
			this.code = code;
		} catch (IOException e) {
			throw new GeocognitionException("Cannot load default "
					+ "custom query template", e);
		}
	}

	@Override
	public String getName() {
		return "New Custom query";
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

	@Override
	public String getBaseName(int elementIndex) {
		return "Query";
	}

}
