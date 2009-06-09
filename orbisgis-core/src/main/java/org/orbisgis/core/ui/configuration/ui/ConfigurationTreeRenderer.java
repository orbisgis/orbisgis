package org.orbisgis.core.ui.configuration.ui;

import javax.swing.JTree;

import org.orbisgis.core.ui.action.IMenu;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeRenderer;
import org.orbisgis.images.IconLoader;

class ConfigurationTreeRenderer extends AbstractTreeRenderer {
	@Override
	protected void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		icon = null;
		tooltip = null;
		if (value instanceof IMenu) {
			icon = IconLoader.getIcon("folder.png");
		}
	}
}
