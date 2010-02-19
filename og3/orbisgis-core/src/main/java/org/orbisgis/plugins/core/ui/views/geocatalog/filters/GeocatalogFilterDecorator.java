package org.orbisgis.plugins.core.ui.views.geocatalog.filters;

import org.gdms.source.SourceManager;
import org.orbisgis.plugins.core.ui.AbstractPlugIn;

public class GeocatalogFilterDecorator extends AbstractPlugIn {

	private String id;
	private String name;
	private AbstractPlugIn instance;

	public GeocatalogFilterDecorator(String id, String name,
			AbstractPlugIn instance) {
		this.id = id;
		this.name = name;
		this.instance = instance;
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
