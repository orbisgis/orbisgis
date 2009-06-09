package org.orbisgis.core.geocognition.sql;

import java.util.HashMap;
import java.util.Map;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.core.geocognition.GeocognitionElementFactory;
import org.orbisgis.core.geocognition.GeocognitionExtensionElement;
import org.orbisgis.core.geocognition.persistence.PropertySet;

public class GeocognitionBuiltInCustomQuery extends AbstractBuiltInSQLArtifact
		implements GeocognitionExtensionElement {

	public GeocognitionBuiltInCustomQuery(Class<? extends CustomQuery> cqClass,
			GeocognitionElementFactory factory) {
		super(cqClass, factory);
	}

	public GeocognitionBuiltInCustomQuery(PropertySet propertySet,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(propertySet, factory);
	}

	@Override
	public String getTypeId() {
		return GeocognitionCustomQueryFactory.BUILT_IN_QUERY_ID;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getFixedId() {
		try {
			return ((Class<? extends CustomQuery>) class_).newInstance()
					.getName();
		} catch (InstantiationException e) {
			throw new RuntimeException("Bug!", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Bug!", e);
		}
	}
	
	@Override
	public Map<String, String> getProperties() {
		boolean registered = false;
		String queryName = getFixedId();
		CustomQuery query = QueryManager.getQuery(queryName);
		registered = (query != null) && query.getClass().equals(class_);
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put(REGISTERED, (registered) ? IS_REGISTERED : IS_NOT_REGISTERED);
		return ret;
	}


}
