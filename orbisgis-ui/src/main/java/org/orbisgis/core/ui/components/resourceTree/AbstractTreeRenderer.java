package org.orbisgis.core.ui.components.resourceTree;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

public abstract class AbstractTreeRenderer implements TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;
	private static final Color DESELECTED = Color.white;
	private static final Color SELECTED_FONT = Color.white;
	private static final Color DESELECTED_FONT = Color.black;

	private JPanel panel;
	private JLabel label;
	protected Icon icon;
	protected String tooltip;

	public AbstractTreeRenderer() {
		panel = new JPanel();
		label = new JLabel();
		panel.add(label);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		setNodeCosmetic(value, selected);
		updateIconAndTooltip(tree, value, selected, expanded, leaf, row,
				hasFocus);
		label.setIcon(icon);
		panel.setToolTipText(resizeTooltip(tooltip));
		return panel;
	}

	public void setNodeCosmetic(Object element, boolean selected) {
		label.setText(element.toString());
		label.setVisible(true);

		if (selected) {
			panel.setBackground(SELECTED);
			label.setForeground(SELECTED_FONT);
		} else {
			panel.setBackground(DESELECTED);
			label.setForeground(DESELECTED_FONT);
		}
	}

	private String resizeTooltip(String tooltip) {
		if (tooltip != null) {
			// I don't know a way to obtain components graphics
			Graphics g = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB)
					.createGraphics();
			FontMetrics fm = g.getFontMetrics();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int maxWidth = screenSize.width / 3;
			String[] parts = tooltip.split("\\Q \\E");
			String ret = "";
			String currentLineText = "";
			for (int i = 0; i < parts.length; i++) {
				int currentLineWidth = (int) fm.getStringBounds(
						currentLineText + " ", g).getWidth();
				int newPartWidth = (int) fm.getStringBounds(parts[i], g)
						.getWidth();
				if (currentLineWidth + newPartWidth > maxWidth) {
					ret += currentLineText + "<br/>" + parts[i];
					currentLineText = "";
				} else {
					currentLineText += " " + parts[i];
				}
			}
			ret += currentLineText;
			return "<html>" + ret + "</html>";
		} else {
			return null;
		}
	}

	protected abstract void updateIconAndTooltip(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus);
}
