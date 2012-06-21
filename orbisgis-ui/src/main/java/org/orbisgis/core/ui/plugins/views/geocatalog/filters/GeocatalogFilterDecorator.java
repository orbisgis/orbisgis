/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.views.geocatalog.filters;

import org.gdms.source.SourceManager;

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

	public boolean equals(Object obj) {
		if (obj instanceof GeocatalogFilterDecorator) {
			GeocatalogFilterDecorator f = (GeocatalogFilterDecorator) obj;
			return f.getId().equals(getId());
		}

		return false;
	}

	public int hashCode() {
		return getId().hashCode();
	}
}
