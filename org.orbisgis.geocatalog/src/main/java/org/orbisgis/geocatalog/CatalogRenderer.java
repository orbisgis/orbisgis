//SAM : COMPLETE
package org.orbisgis.geocatalog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.orbisgis.geocatalog.resources.IResource;

public class CatalogRenderer extends DefaultTreeCellRenderer {

	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private OurJPanel ourJPanel = null;

	public CatalogRenderer() {
		ourJPanel = new OurJPanel();
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		ourJPanel.setNodeCosmetic(tree, (IResource) value, sel, expanded,
				leaf, row, hasFocus);
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

		public void setNodeCosmetic(JTree tree, IResource node,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			Icon icon = node.getIcon(expanded);
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			} else {
				iconAndLabel.setIcon(null);
			}
			iconAndLabel.setText(node.getName());
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
