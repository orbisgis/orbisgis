package org.orbisgis.views.geocognition;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.orbisgis.geocognition.GeocognitionElement;
import org.orbisgis.views.geocognition.wizard.ElementRenderer;

public class GeocognitionRenderer implements TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private JPanel panel;

	private JLabel label;

	private ElementRenderer[] renderers = new ElementRenderer[0];

	public GeocognitionRenderer() {
		panel = new JPanel();
		label = new JLabel();
		panel.add(label);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setNodeCosmetic(tree, (GeocognitionElement) value, selected, expanded,
				leaf, row, hasFocus);
		return panel;
	}

	public void setNodeCosmetic(JTree tree, GeocognitionElement node,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		Icon icon = getRendererIcon(node);
		if (null != icon) {
			label.setIcon(icon);
		}
		label.setText(node.getId());
		label.setVisible(true);

		if (selected) {
			panel.setBackground(SELECTED);
			label.setForeground(SELECTED_FONT);
		} else {
			panel.setBackground(DESELECTED);
			label.setForeground(DESELECTED_FONT);
		}
	}

	private Icon getRendererIcon(GeocognitionElement element) {
		String typeId = element.getTypeId();
		Map<String, String> properties = element.getProperties();
		for (ElementRenderer renderer : renderers) {
			Icon icon = renderer.getIcon(typeId, properties);
			if (icon != null) {
				return icon;
			}
		}

		return null;
	}

	public void setRenderers(ElementRenderer[] renderers) {
		this.renderers = renderers;
	}

}
