package org.orbisgis.views.documentCatalog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public class DocumentRenderer implements TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private JPanel panel;

	private JLabel label;

	public DocumentRenderer() {
		panel = new JPanel();
		label = new JLabel();
		panel.add(label);
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setNodeCosmetic(tree, (IDocument) value, selected, expanded, leaf, row,
				hasFocus);
		return panel;
	}

	public void setNodeCosmetic(JTree tree, IDocument node, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {

		Icon icon = node.getIcon();
		if (null != icon) {
			label.setIcon(icon);
		}
		label.setText(node.getName());
		label.setVisible(true);

		if (selected) {
			panel.setBackground(SELECTED);
			label.setForeground(SELECTED_FONT);
		} else {
			panel.setBackground(DESELECTED);
			label.setForeground(DESELECTED_FONT);
		}
	}

}
