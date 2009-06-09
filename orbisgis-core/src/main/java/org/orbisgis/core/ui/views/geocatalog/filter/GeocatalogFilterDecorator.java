package org.orbisgis.core.ui.views.geocatalog.filter;

import org.gdms.source.SourceManager;

public class GeocatalogFilterDecorator implements IGeocatalogFilter {

	private String id;
	private String name;
	private IGeocatalogFilter instance;

	public GeocatalogFilterDecorator(String id, String name,
			IGeocatalogFilter instance) {
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

	@Override
	public boolean accept(SourceManager sm, String sourceName) {
		return instance.accept(sm, sourceName);
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
