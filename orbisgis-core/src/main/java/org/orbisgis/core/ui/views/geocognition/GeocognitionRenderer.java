package org.orbisgis.core.ui.views.geocognition;

import java.util.Map;

import javax.swing.Icon;
import javax.swing.JTree;

import org.orbisgis.core.Services;
import org.orbisgis.core.geocognition.GeocognitionElement;
import org.orbisgis.core.ui.components.resourceTree.AbstractTreeRenderer;
import org.orbisgis.core.ui.views.geocognition.wizard.ElementRenderer;

public class GeocognitionRenderer extends AbstractTreeRenderer {
	private ElementRenderer[] renderers = new ElementRenderer[0];

	public void setRenderers(ElementRenderer[] renderers) {
		this.renderers = renderers;
	}

	@Override
	protected void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		icon = null;
		tooltip = null;

		GeocognitionElement element = (GeocognitionElement) value;
		String typeId = element.getTypeId();
		Map<String, String> properties = element.getProperties();

		for (ElementRenderer renderer : renderers) {
			try {
				Icon elementIcon = renderer.getIcon(typeId, properties);
				if (elementIcon != null) {
					icon = elementIcon;
					// tooltip = resizeTooltip(renderer.getTooltip(element));
					tooltip = renderer.getTooltip(element);
					break;
				}
			} catch (RuntimeException e) {
				Services.getErrorManager().error("bug in tree renderer", e);
			}
		}
	}
}
