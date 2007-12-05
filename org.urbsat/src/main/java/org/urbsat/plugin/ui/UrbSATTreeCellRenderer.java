package org.urbsat.plugin.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.orbisgis.toolsMenuPanel.jaxb.Menu;
import org.orbisgis.toolsMenuPanel.jaxb.MenuItem;

public class UrbSATTreeCellRenderer implements TreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private OurJPanel ourJPanel = null;

	public UrbSATTreeCellRenderer() {
		ourJPanel = new OurJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (leaf) {
			ourJPanel.setNodeCosmetic(tree, (MenuItem) value, selected,
					expanded, leaf, row, hasFocus);
		} else {
			ourJPanel.setNodeCosmetic(tree, (Menu) value, selected, expanded,
					leaf, row, hasFocus);
		}
		return ourJPanel;
	}

	private class OurJPanel extends JPanel {
		private JLabel iconAndLabel;

		public OurJPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			iconAndLabel = new JLabel();
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, Menu node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			Icon icon;
			if (expanded) {
				icon = new ImageIcon(this.getClass().getResource("folder.png"));
			} else {
				icon = new ImageIcon(this.getClass().getResource(
						"folder_magnify.png"));
			}

			iconAndLabel.setIcon(icon);
			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				this.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				this.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		public void setNodeCosmetic(JTree tree, MenuItem node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Icon icon = new ImageIcon(this.getClass().getResource("map.png"));

			iconAndLabel.setIcon(icon);
			iconAndLabel.setText(node.getLabel());
			iconAndLabel.setVisible(true);

			if (selected) {
				this.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				this.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}
	}
}