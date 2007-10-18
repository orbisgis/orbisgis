package org.orbisgis.geocatalog;

/**
 * This interface is to be implemented by org.orbisgis.plugin.geocatalog.catalog
 * (Catalog Extension) extensions.
 *
 * @author Samuel CHEMLA
 *
 */
public interface ICatalogExtension {

	/**
	 * Once your class will be instanciated, it will be given a reference to the
	 * catalog, so you will be able to do your stuff...
	 *
	 * @param myCatalog
	 */
	public void setCatalog(Catalog myCatalog);
}
