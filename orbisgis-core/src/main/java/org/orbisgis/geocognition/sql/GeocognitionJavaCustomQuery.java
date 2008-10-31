package org.orbisgis.geocognition.sql;

import java.util.Map;

import org.gdms.sql.customQuery.CustomQuery;
import org.gdms.sql.customQuery.QueryManager;
import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.persistence.PropertySet;

/**
 * Custom query java implementation
 * 
 * @author Fernando Gonzalez Cortes
 * 
 */
public class GeocognitionJavaCustomQuery extends AbstractJavaSQLArtifact
		implements GeocognitionExtensionElement {

	public GeocognitionJavaCustomQuery(Code code,
			GeocognitionElementFactory factory) {
		super(code, factory);
	}

	public GeocognitionJavaCustomQuery(PropertySet properties,
			GeocognitionElementFactory factory) throws ClassNotFoundException {
		super(properties, factory);
	}

	@Override
	public String getTypeId() {
		return GeocognitionCustomQueryFactory.JAVA_QUERY_ID;
	}

	@Override
	protected void removeArtifact(String id) {
		QueryManager.remove(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void addArtifact(Class<?> cl) {
		QueryManager.registerQuery((Class<? extends CustomQuery>) cl);
	}

	@Override
	protected Code instantiateJavaCode(String codeContent) {
		return new CustomQueryJavaCode(codeContent);
	}

	@Override
	protected String getInterfaceName() {
		return CustomQuery.class.getName();
	}

	@Override
	protected Map<String, String> getPersistentProperties() {
		return null;
	}

	@Override
	protected void setPersistentProperties(Map<String, String> properties) {

	}
}
