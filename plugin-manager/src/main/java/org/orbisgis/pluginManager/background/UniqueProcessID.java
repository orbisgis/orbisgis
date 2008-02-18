package org.orbisgis.pluginManager.background;


public class UniqueProcessID implements ProcessId {

	private static int count = 0;
	private int id;

	public UniqueProcessID() {
		this.id = count++;
	}

	public boolean is(ProcessId processId) {
		if (processId instanceof UniqueProcessID) {
			return id == ((UniqueProcessID) processId).id;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

}
