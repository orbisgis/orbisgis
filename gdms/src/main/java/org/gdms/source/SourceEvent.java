package org.gdms.source;

public class SourceEvent {

	private String name;
	private SourceManager sourceManager;
	private boolean wellKnownName;
	private String newName;

	public SourceEvent(String name, boolean wellKnownName,
			SourceManager sourceManager) {
		this(name, wellKnownName, sourceManager, name);
	}

	public SourceEvent(String name, boolean wellKnownName,
			SourceManager sourceManager, String newName) {
		this.name = name;
		this.sourceManager = sourceManager;
		this.wellKnownName = wellKnownName;
		this.newName = newName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SourceManager getSourceManager() {
		return sourceManager;
	}

	public void setSourceManager(SourceManager sourceManager) {
		this.sourceManager = sourceManager;
	}

	public boolean isWellKnownName() {
		return wellKnownName;
	}

	public String getNewName() {
		return newName;
	}

}
