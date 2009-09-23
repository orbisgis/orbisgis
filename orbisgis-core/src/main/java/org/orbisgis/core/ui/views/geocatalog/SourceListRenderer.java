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
//SAM : COMPLETE
package org.orbisgis.core.ui.views.geocatalog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.gdms.data.DataSource;
import org.gdms.data.DataSourceCreationException;
import org.gdms.data.NoSuchTableException;
import org.gdms.driver.DriverException;
import org.gdms.driver.driverManager.DriverLoadException;
import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.ui.views.geocatalog.newSourceWizard.SourceRenderer;
import org.orbisgis.images.IconLoader;

public class SourceListRenderer implements ListCellRenderer {

	private static final Color SELECTED = Color.lightGray;
	private static final Color DESELECTED = Color.white;
	private static final Color SELECTED_FONT = Color.white;
	private static final Color DESELECTED_FONT = Color.black;

	private SourceRenderer[] renderers = new SourceRenderer[0];
	private static final Icon raster = IconLoader.getIcon("image.png");
	private static final Icon alphanumeric_database = IconLoader
			.getIcon("database.png");
	private static final Icon spatial = IconLoader.getIcon("geofile.png");
	private static final Icon alphanumeric_file = IconLoader
			.getIcon("flatfile.png");
	private static final Icon server_connect = IconLoader
			.getIcon("server_connect.png");

	private OurJPanel ourJPanel = null;

	public SourceListRenderer() {
		ourJPanel = new OurJPanel();
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		ourJPanel.setNodeCosmetic((String) value, isSelected, cellHasFocus);
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

		public void setNodeCosmetic(String source, boolean selected,
				boolean hasFocus) {
			DataManager dataManager = (DataManager) Services
					.getService(DataManager.class);
			SourceManager sourceManager = dataManager.getSourceManager();

			Icon icon = null;
			for (SourceRenderer renderer : renderers) {
				icon = renderer.getIcon(sourceManager, source);
				if (icon != null) {
					break;
				}
			}
			Source src = sourceManager.getSource(source);

			if (src != null) {

				if (src.isFileSource()) {
					if (!src.getFile().exists()) {
						icon = IconLoader.getIcon("remove.png");
					}
				}

				if (icon == null) {
					int sourceType = src.getType();
					if ((sourceType & SourceManager.VECTORIAL) == SourceManager.VECTORIAL) {
						icon = spatial;
					} else if ((sourceType & SourceManager.RASTER) == SourceManager.RASTER) {
						icon = raster;
					} else if ((sourceType & SourceManager.WMS) == SourceManager.WMS) {
						icon = server_connect;
					} else if ((sourceType & SourceManager.FILE) == SourceManager.FILE) {
						icon = alphanumeric_file;
					} else if ((sourceType & SourceManager.DB) == SourceManager.DB) {
						icon = alphanumeric_database;
					}
				}
				if (null != icon) {
					iconAndLabel.setIcon(icon);
				} else {
					iconAndLabel.setIcon(null);
				}
				String text = null;
				for (SourceRenderer renderer : renderers) {
					text = renderer.getText(sourceManager, source);
					if (text != null) {
						break;
					}
				}
				if (text == null) {
					text = source;
					text += " (" + src.getTypeName() + ")";
				}
				iconAndLabel.setText(text);
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

	public void setRenderers(SourceRenderer[] renderers) {
		this.renderers = renderers;
	}

}
