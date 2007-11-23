package org.orbisgis.geocatalog;

/**
 * Interface to implement by the extensions to the IGeocatalogAction
 *
 * @author Fernando Gonzalez Cortes
 */
public interface IGeocatalogAction {

	/**
	 * Execute here the action
	 *
	 * @param catalog
	 *            instance of the catalog where this action is hosted
	 */
	public void actionPerformed(Catalog catalog);

	public boolean isVisible(GeoCatalog geoCatalog);

	public boolean isEnabled(GeoCatalog geoCatalog);

}
