package org.orbisgis.geocatalog;

import org.orbisgis.geocatalog.resources.IResource;

public interface NodeFilter {

	public boolean accept(IResource resource);

}
