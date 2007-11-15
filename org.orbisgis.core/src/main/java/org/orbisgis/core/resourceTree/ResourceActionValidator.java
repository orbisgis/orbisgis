package org.orbisgis.core.resourceTree;

import javax.swing.tree.TreePath;


public interface ResourceActionValidator {

	boolean acceptsSelection(Object action, TreePath[] selection);

}
