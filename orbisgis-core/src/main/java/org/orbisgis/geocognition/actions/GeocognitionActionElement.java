package org.orbisgis.geocognition.actions;

import java.util.HashMap;
import java.util.Map;

import org.orbisgis.geocognition.GeocognitionElementFactory;
import org.orbisgis.geocognition.GeocognitionExtensionElement;
import org.orbisgis.geocognition.java.AbstractJavaArtifact;
import org.orbisgis.geocognition.persistence.PropertySet;
import org.orbisgis.geocognition.sql.Code;

public class GeocognitionActionElement extends AbstractJavaArtifact implements
		GeocognitionExtensionElement {

	static final String PERSISTENCE_PROPERTY_NAME = "action-code";

	public GeocognitionActionElement(ActionCode code,
			GeocognitionElementFactory factory) {
		super(code, factory);
	}

	public GeocognitionActionElement(PropertySet propertySet,
			GeocognitionActionElementFactory factory) {
		super(propertySet, factory);
	}

	@Override
	protected Code instantiateJavaCode(String codeContent) {
		return new ActionCode(codeContent);
	}

	@Override
	protected String getCodePropertyName() {
		return PERSISTENCE_PROPERTY_NAME;
	}

	@Override
	public String getTypeId() {
		return GeocognitionActionElementFactory.ACTION_ID;
	}

	@Override
	protected Map<String, String> getPersistentProperties() {
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("group", getAction().getGroup());
		ret.put("menuId", getAction().getMenuId());
		ret.put("text", getAction().getText());

		return ret;
	}

	@Override
	protected void setPersistentProperties(Map<String, String> properties) {
		getAction().setGroup(properties.get("group"));
		getAction().setMenuId(properties.get("menuId"));
		getAction().setText(properties.get("text"));
	}

	private ActionCode getAction() {
		return (ActionCode) code;
	}
}
