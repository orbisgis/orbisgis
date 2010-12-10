package org.orbisgis.core.ui.configuration;

import javax.swing.JTree;

import org.orbisgis.core.ui.components.resourceTree.AbstractTreeRenderer;
import org.orbisgis.core.ui.pluginSystem.menu.IMenu;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

class ConfigurationTreeRenderer extends AbstractTreeRenderer {
	@Override
	protected void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		icon = null;
		tooltip = null;
		if (value instanceof IMenu) {
			icon = OrbisGISIcon.FOLDER;
		}
	}
}
