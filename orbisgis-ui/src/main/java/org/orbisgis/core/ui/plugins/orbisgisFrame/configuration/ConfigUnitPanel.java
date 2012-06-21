/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.core.ui.plugins.orbisgisFrame.configuration;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.core.sif.multiInputPanel.InputType;

public class ConfigUnitPanel extends JPanel {
	public ConfigUnitPanel(String title, JCheckBox checkbox, String enableText,
			String[] labels, final InputType[] inputs) {
		setLayout(new CRFlowLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createEtchedBorder(), title));

		if (checkbox != null) {
			JPanel checkPanel = new JPanel();
			checkPanel.add(checkbox);
			checkPanel.add(new JLabel(enableText));
			add(checkPanel);
			add(new CarriageReturn());
		}

		CRFlowLayout layout = new CRFlowLayout();
		layout.setAlignment(CRFlowLayout.LEFT);
		JPanel labelPanel = new JPanel(layout);
		for (int i = 0; i < labels.length; i++) {
			JLabel l = new JLabel(labels[i]);
			Dimension size = l.getPreferredSize();
			size.height = inputs[i].getComponent().getPreferredSize().height;
			l.setPreferredSize(size);
			labelPanel.add(l);
			labelPanel.add(new CarriageReturn());
		}

		JPanel compPanel = new JPanel(layout);
		for (InputType input : inputs) {
			compPanel.add(input.getComponent());
			compPanel.add(new CarriageReturn());
		}

		add(labelPanel);
		add(compPanel);
		Dimension min = labelPanel.getPreferredSize();
		min.width += compPanel.getPreferredSize().width + 35;
		setMinimumSize(min);
	}
}
