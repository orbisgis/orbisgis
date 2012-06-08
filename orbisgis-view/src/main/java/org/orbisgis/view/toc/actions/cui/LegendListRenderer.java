package org.orbisgis.view.toc.actions.cui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import javax.swing.*;

/**
 * As a {@code CellRenderer}, this class is used in {@code LegendList} to render
 * the elements of the list of {@code Legend}. Each time a change occur in the
 * list organization, it is called to perform the rendering.
 * @author alexis
 */
public class LegendListRenderer implements ListCellRenderer {

	private LegendListRenderPanel ourJPanel = null;
	private LegendsPanel legendsPanel;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED = Color.red;

        /**
         * Build a new {@code ListCellRenderer} dedicated to the rendering of
         * {@code Legend} elements and affiliated.
         * @param legendsPanel
         */
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

        /**
         * The {@code CellRenderer} is used by its owning {@code LegendList} to
         * produce instances of {@code Component} each time it's needed. This
         * goal is achieved by {@code LegendListRenderer} by using this inner
         * class.
         */
	public class LegendListRenderPanel extends JPanel {

		private JCheckBox jCheckBox;
		private JLabel label;

                /**
                 * Prepare the {@code Component} that will be returned to the
                 * {@code LegendList}.
                 */
		public LegendListRenderPanel() {

			jCheckBox = new JCheckBox();
			jCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
			label = new JLabel();
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(jCheckBox);
			add(label);
			this.setBackground(DESELECTED);

		}

                /**
                 * Configure the {@code Component} that will be returned to the
                 * {@code LegendList}.
                 * @param list
                 * @param value
                 * @param legendIndex
                 * @param isSelected
                 * @param cellHasFocus
                 */
		public void setNodeCosmetic(JList list, Object value, int legendIndex,
				boolean isSelected, boolean cellHasFocus) {
			jCheckBox.setVisible(true);

//			if (legendsPanel.getLegends()[legendIndex].isVisible()) {
				jCheckBox.setSelected(true);
//			} else {
//				jCheckBox.setSelected(false);
//			}

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

        /**
         * Get the bounds of the last produced {@code Component}.
         * @return
         */
	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

}
