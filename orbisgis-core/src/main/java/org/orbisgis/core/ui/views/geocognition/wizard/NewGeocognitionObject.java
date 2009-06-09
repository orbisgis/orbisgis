package org.orbisgis.core.ui.views.geocognition.wizard;

public class NewGeocognitionObject {

	private String baseName;
	private Object object;
	private boolean fixedName = false;
	private boolean uniqueId = false;

	public NewGeocognitionObject(String baseName, Object object) {
		super();
		this.baseName = baseName;
		this.object = object;
	}
	
	public void setFixedName(boolean fixedName) {
		this.fixedName = fixedName;
	}

	public String getBaseName() {
		return baseName;
	}

	public Object getObject() {
		return object;
	}

	public boolean isFixedName() {
		return fixedName;
	}

	public boolean isUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(boolean uniqueId) {
		this.uniqueId = uniqueId;
	}

}
