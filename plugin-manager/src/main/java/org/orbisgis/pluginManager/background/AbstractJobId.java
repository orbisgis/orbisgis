package org.orbisgis.pluginManager.background;

public abstract class AbstractJobId implements ProcessId {

	@Override
	public boolean equals(Object obj) {
		return this.is((ProcessId) obj);
	}

	@Override
	public int hashCode() {
		return getHashCode();
	}

	protected abstract int getHashCode();

}
