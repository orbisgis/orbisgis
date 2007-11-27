package org.gdms.source;


public class SourceRemovalEvent extends SourceEvent {

	private String[] names;

	public SourceRemovalEvent(String name, String[] names, boolean wellKnownName,
			SourceManager sourceManager) {
		super(name, wellKnownName, sourceManager);
		this.names = names;
	}

	public String[] getNames() {
		return names;
	}

}
