package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class LegendListRenderer implements ListCellRenderer {

	private LegendListRenderPanel ourJPanel = null;
	private LegendsPanel legendsPanel;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED = Color.red;

	public LegendListRenderer(LegendsPanel legendsPanel) {
		this.legendsPanel = legendsPanel;
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		ourJPanel = new LegendListRenderPanel();
		ourJPanel.setNodeCosmetic(list, value, index, isSelected, cellHasFocus);
		return ourJPanel;

	}

	public class LegendListRenderPanel extends JPanel {

		private JCheckBox jCheckBox;
		private JLabel label;

		public LegendListRenderPanel() {

			jCheckBox = new JCheckBox();
			jCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			label = new JLabel();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(jCheckBox);
			add(label);
			this.setBackground(DESELECTED);

		}

		public void setNodeCosmetic(JList list, Object value, int legendIndex,
				boolean isSelected, boolean cellHasFocus) {
			jCheckBox.setVisible(true);

			if (legendsPanel.getLegends()[legendIndex].isVisible()) {
				jCheckBox.setSelected(true);
			} else {
				jCheckBox.setSelected(false);
			}

			if (isSelected) {
				label.setForeground(SELECTED);
				jCheckBox.setForeground(SELECTED);
			} else {
				jCheckBox.setBackground(DESELECTED);
				jCheckBox.setForeground(DESELECTED);
			}

			label.setText(value.toString());

		}

		public Rectangle getCheckBoxBounds() {
			return jCheckBox.getBounds();

		}
	}

	public Rectangle getCheckBoxBounds() {

		return ourJPanel.getCheckBoxBounds();

	}

}
