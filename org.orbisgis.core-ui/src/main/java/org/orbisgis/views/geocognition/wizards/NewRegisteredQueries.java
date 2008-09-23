package org.orbisgis.views.geocognition.wizards;

import java.util.Map;

import javax.swing.ImageIcon;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.sql.GeocognitionBuiltInCustomQuery;
import org.orbisgis.geocognition.sql.GeocognitionCustomQueryFactory;
import org.orbisgis.images.IconLoader;
import org.orbisgis.views.geocognition.wizard.INewGeocognitionElement;

public class NewRegisteredQueries extends AbstractRegisteredSQLArtifact
		implements INewGeocognitionElement {

	@Override
	public GeocognitionElementFactory getFactory() {
		return new GeocognitionCustomQueryFactory();
	}

	@Override
	public String getName() {
		return "Built-in custom query";
	}

	@Override
	protected String getName(Object sqlArtifact) {
		return ((CustomQuery) sqlArtifact).getName();
	}

	@Override
	protected ImageIcon getIcon(String contentTypeId,
			Map<String, String> properties) {
		if (GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID
				.equals(contentTypeId)) {
			String registered = properties
					.get(GeocognitionBuiltInCustomQuery.REGISTERED);
			if ((registered != null)
					&& registered
							.equals(GeocognitionBuiltInCustomQuery.IS_REGISTERED)) {
				return IconLoader.getIcon("builtInCustomQueryMap.png");
			} else {
				return IconLoader.getIcon("builtInCustomQueryMapError.png");
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean isUniqueIdRequired(int index) {
		return false;
	}

	@Override
	public String getBaseName() {
		return null;
	}

	@Override
	protected Class<?> getArtifact(String name) {
		return QueryManager.getQuery(name).getClass();
	}

	@Override
	protected String[] getArtifactNames() {
		return QueryManager.getQueryNames();
	}

}
