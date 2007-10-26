package org.orbisgis.core.resourceTree;

public interface ResourceActionValidator {

	boolean acceptsSelection(Object action, IResource[] res);

}
