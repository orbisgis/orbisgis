package org.orbisgis.pluginManager.background;

public class DefaultProcessId implements ProcessId {
	private String id;

	public DefaultProcessId(String id) {
		this.id = id;
	}

	public boolean is(ProcessId pi) {
		if (pi instanceof DefaultProcessId) {
			DefaultProcessId dpi = (DefaultProcessId) pi;
			return id.equals(dpi.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return id;
	}
}
