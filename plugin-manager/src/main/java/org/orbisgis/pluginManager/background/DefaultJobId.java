package org.orbisgis.pluginManager.background;

public class DefaultJobId extends AbstractJobId implements JobId {
	private String id;

	public DefaultJobId(String id) {
		this.id = id;
	}

	public boolean is(JobId pi) {
		if (pi instanceof DefaultJobId) {
			DefaultJobId dpi = (DefaultJobId) pi;
			return id.equals(dpi.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return id;
	}

	@Override
	protected int getHashCode() {
		return id.hashCode();
	}
}
