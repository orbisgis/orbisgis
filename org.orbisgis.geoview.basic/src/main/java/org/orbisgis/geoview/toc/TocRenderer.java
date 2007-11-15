package org.orbisgis.geoview.toc;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.orbisgis.geoview.layerModel.ILayer;

public class TocRenderer implements TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private RenderPanel ourJPanel;

	public TocRenderer() {
		ourJPanel = new RenderPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		ourJPanel.setNodeCosmetic(tree, (ILayer) value,
				selected, expanded, leaf, row, hasFocus);
		return ourJPanel;
	}

	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

	public class RenderPanel extends JPanel {
		private JCheckBox check;

		private JLabel iconAndLabel;

		public RenderPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			check = new JCheckBox();
			iconAndLabel = new JLabel();
			add(check);
			add(iconAndLabel);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, boolean selected,
				boolean expanded, boolean leaf, int row, boolean hasFocus) {
			check.setVisible(true);
			check.setSelected(node.isVisible());

			Icon icon = node.getIcon();
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			iconAndLabel.setText(node.getName());
			iconAndLabel.setVisible(true);

			if (selected) {
				this.setBackground(SELECTED);
				check.setBackground(SELECTED);
				iconAndLabel.setForeground(SELECTED_FONT);
			} else {
				this.setBackground(DESELECTED);
				check.setBackground(DESELECTED);
				iconAndLabel.setForeground(DESELECTED_FONT);
			}
		}

		private Rectangle getCheckBoxBounds() {
			return check.getBounds();
		}
	}

}
