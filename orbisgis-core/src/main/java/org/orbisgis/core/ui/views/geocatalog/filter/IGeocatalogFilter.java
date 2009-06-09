package org.orbisgis.core.ui.views.geocatalog.filter;

import org.gdms.source.SourceManager;

public interface IGeocatalogFilter {

	/**
	 * Return true to make the specified source appear in the geocatalog. Return
	 * false otherwise
	 * 
	 * @param sourceName
	 * @return
	 */
	boolean accept(SourceManager sm, String sourceName);

}