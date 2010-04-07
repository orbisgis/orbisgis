package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.core.ui.pluginSystem.AbstractPlugIn;

public class GeocatalogFilterDecorator implements IFilter {

	private String id;
	private String name;
	private IFilter instance;

	public GeocatalogFilterDecorator(String id, String name,
			IFilter allFilterPlugIn) {
		this.id = id;
		this.name = name;
		this.instance = allFilterPlugIn;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean accepts(SourceManager sm, String sourceName) {
		return instance.accepts(sm, sourceName);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GeocatalogFilterDecorator) {
			GeocatalogFilterDecorator f = (GeocatalogFilterDecorator) obj;
			return f.getId().equals(getId());
		}

		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}
}
