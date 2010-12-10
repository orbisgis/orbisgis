/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the geo-informatic team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Lead Erwan BOCHER, scientific researcher,
 *
 *  Developer lead : Pierre-Yves FADET, computer engineer.
 *
 *  User support lead : Gwendall Petit, geomatic engineer.
 *
 * Previous computer developer : Thomas LEDUC, scientific researcher, Fernando GONZALEZ
 * CORTES, computer engineer.
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://orbisgis.cerma.archi.fr/>
 * <http://sourcesup.cru.fr/projects/orbisgis/>
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * Pierre-Yves.Fadet _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 **/

package org.orbisgis.core.ui.plugins.views.geocatalog;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.gdms.source.Source;
import org.gdms.source.SourceManager;
import org.orbisgis.core.DataManager;
import org.orbisgis.core.Services;
import org.orbisgis.core.ui.geocatalog.newSourceWizards.SourceRenderer;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;
import org.orbisgis.core.ui.preferences.lookandfeel.images.IconLoader;

public class SourceListRenderer implements ListCellRenderer {

	private static final Color SELECTED = Color.lightGray;
	private static final Color DESELECTED = Color.white;
	private static final Color SELECTED_FONT = Color.white;
	private static final Color DESELECTED_FONT = Color.black;

	private SourceRenderer[] renderers = new SourceRenderer[0];
	private static final Icon raster = OrbisGISIcon.IMAGE;
	private static final Icon alphanumeric_database = IconLoader
			.getIcon("database.png");
	private static final Icon system_table = IconLoader
	.getIcon("drive.png");
	private static final Icon spatial = OrbisGISIcon.GEOFILE;
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
					if (src.getFile() != null) {
						if (!src.getFile().exists()) {
							icon = OrbisGISIcon.REMOVE;
						}
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
					else if ((sourceType & SourceManager.SYSTEM_TABLE) == SourceManager.SYSTEM_TABLE) {
						icon = system_table;
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
