package org.orbisgis.pluginManager.background;

public class UniqueJobID extends AbstractJobId implements JobId {

	private static int count = 0;
	private int id;

	public UniqueJobID() {
		this.id = count++;
	}

	public boolean is(JobId processId) {
		if (processId instanceof UniqueJobID) {
			return id == ((UniqueJobID) processId).id;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return Integer.toString(id);
	}

	@Override
	protected int getHashCode() {
		return id;
	}

}
