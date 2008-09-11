/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able
 * to manipulate and create vector and raster spatial information. OrbisGIS
 * is distributed under GPL 3 license. It is produced  by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/>, CNRS FR 2488:
 *    Erwan BOCHER, scientific researcher,
 *    Thomas LEDUC, scientific researcher,
 *    Fernando GONZALEZ CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OrbisGIS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult:
 *    <http://orbisgis.cerma.archi.fr/>
 *    <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 *    erwan.bocher _at_ ec-nantes.fr
 *    fergonco _at_ gmail.com
 *    thomas.leduc _at_ cerma.archi.fr
 */
package org.orbisgis.editorViews.toc;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import org.gdms.data.SpatialDataSourceDecorator;
import org.gdms.driver.DriverException;
import org.orbisgis.Services;
import org.orbisgis.layerModel.ILayer;
import org.orbisgis.renderer.legend.Legend;
import org.sif.CRFlowLayout;

public class TocRenderer extends TocAbstractRenderer implements
		TreeCellRenderer {
	private static final Color SELECTED = Color.lightGray;

	private static final Color DESELECTED = Color.white;

	private static final Color SELECTED_FONT = Color.white;

	private static final Color DESELECTED_FONT = Color.black;

	private Toc toc;

	private LayerRenderPanel ourJPanel;

	public TocRenderer(Toc toc) {
		this.toc = toc;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof ILayer) {
			ourJPanel = new LayerRenderPanel();
			ourJPanel.setNodeCosmetic(tree, (ILayer) value, selected, expanded,
					leaf, row, hasFocus);
			return ourJPanel;
		} else {
			LegendRenderPanel ourJPanel = new LegendRenderPanel();
			TocTreeModel.LegendNode legendNode = (TocTreeModel.LegendNode) value;
			ourJPanel.setNodeCosmetic(tree, legendNode.getLayer(), legendNode
					.getLegendIndex(), selected, expanded, leaf, row, hasFocus);
			return ourJPanel;
		}
	}

	public Rectangle getCheckBoxBounds() {
		return ourJPanel.getCheckBoxBounds();
	}

	public class LayerRenderPanel extends JPanel {
		private JCheckBox check;

		private JLabel iconAndLabel;

		public LayerRenderPanel() {
			FlowLayout fl = new FlowLayout(CRFlowLayout.LEADING);
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

			Icon icon = null;
			try {
				icon = getLayerIcon(node);
			} catch (DriverException e) {
			} catch (IOException e) {
			}
			if (null != icon) {
				iconAndLabel.setIcon(icon);
			}
			String name = node.getName();
			SpatialDataSourceDecorator dataSource = node.getDataSource();
			if ((dataSource != null) && (dataSource.isModified())) {
				name += "*";
			}
			iconAndLabel.setText(name);
			iconAndLabel.setVisible(true);

			if (toc.isActive(node)) {
				Font font = iconAndLabel.getFont();
				font = font.deriveFont(Font.ITALIC, font.getSize());
				iconAndLabel.setFont(font);
			}

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

	public class LegendRenderPanel extends JPanel {

		private JLabel lblLegend;

		public LegendRenderPanel() {
			FlowLayout fl = new FlowLayout(FlowLayout.LEADING);
			fl.setHgap(0);
			setLayout(fl);
			lblLegend = new JLabel();
			add(lblLegend);
		}

		public void setNodeCosmetic(JTree tree, ILayer node, int legendIndex,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			try {
				this.setBackground(DESELECTED);
				Graphics2D dummyGraphics = new BufferedImage(10, 10,
						BufferedImage.TYPE_INT_ARGB).createGraphics();
				Legend legend = node.getRenderingLegend()[legendIndex];
				int[] imageSize = legend.getImageSize(dummyGraphics);
				if ((imageSize[0] != 0) && (imageSize[1] != 0)) {
					BufferedImage legendImage = new BufferedImage(imageSize[0],
							imageSize[1], BufferedImage.TYPE_INT_ARGB);
					legend.drawImage(legendImage.createGraphics());
					ImageIcon imageIcon = new ImageIcon(legendImage);
					lblLegend.setIcon(imageIcon);
				}
			} catch (DriverException e) {
				Services.getErrorManager().error(
						"Cannot access the legends in layer " + node.getName(),
						e);
			}
		}

	}

}
