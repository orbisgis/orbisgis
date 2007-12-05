package org.orbisgis.geoview.fromXmlToSQLTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.orbisgis.persistence.Menu;
import org.orbisgis.persistence.MenuItem;

public class ToolsMenuPanelTreeCellRenderer implements TreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private static final Icon FOLDER_ICON = new ImageIcon(
			ToolsMenuPanelTreeCellRenderer.class.getResource("folder.png"));

	private static final Icon FOLDER_MAGNIFY_ICON = new ImageIcon(
			ToolsMenuPanelTreeCellRenderer.class
					.getResource("folder_magnify.png"));

	private static final Icon MAP_ICON = new ImageIcon(
			ToolsMenuPanelTreeCellRenderer.class.getResource("map.png"));

	private NodeJPanel nodeJPanel = null;

	public ToolsMenuPanelTreeCellRenderer() {
		nodeJPanel = new NodeJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (leaf) {
			nodeJPanel.setNodeCosmetic(tree, (MenuItem) value, selected,
					expanded, leaf, row, hasFocus);
		} else {
			nodeJPanel.setNodeCosmetic(tree, (Menu) value, selected, expanded,
					leaf, row, hasFocus);
		}
		return nodeJPanel;
	}

	private class NodeJPanel extends JPanel {
		private JLabel iconAndLabel;

		public NodeJPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, Menu node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Icon icon = (expanded) ? FOLDER_ICON : FOLDER_MAGNIFY_ICON;

			iconAndLabel.setIcon(icon);
			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		public void setNodeCosmetic(JTree tree, MenuItem node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			iconAndLabel.setIcon(MAP_ICON);
			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}
	}
}