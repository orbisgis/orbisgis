package org.orbisgis.pluginManager.background;

public abstract class AbstractJobId implements JobId {

	@Override
	public boolean equals(Object obj) {
		return this.is((JobId) obj);
	}

	@Override
	public int hashCode() {
		return getHashCode();
	}

	protected abstract int getHashCode();

}
