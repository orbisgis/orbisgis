package org.orbisgis.configuration.ui;

import javax.swing.JTree;

import org.orbisgis.action.IMenu;
import org.orbisgis.images.IconLoader;
import org.orbisgis.ui.resourceTree.AbstractTreeRenderer;

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
