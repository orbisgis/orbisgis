/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.orbisgis.core.sif.CRFlowLayout;
import org.orbisgis.core.sif.CarriageReturn;
import org.orbisgis.utils.I18N;

public class RenderingConfigurationPanel extends JPanel implements ItemListener {

	private JCheckBox compositeCheck;
	private JComboBox compositeCb;
	private JCheckBox antialiasingCheck;

	String alpha = "1.0";

	private ViewRenderingPanel view;
	private boolean antialiasing;
	private boolean composite;
	private String composite_value;

	public RenderingConfigurationPanel(boolean antialiasing, boolean composite,
			String composite_value) {
		this.antialiasing = antialiasing;
		this.composite = composite;
		this.composite_value = composite_value;
	}

	public void init() {

		this.setLayout(new BorderLayout());
		this.add(getCheckPanel(), BorderLayout.WEST);
		this.add(new CarriageReturn());
		view = new ViewRenderingPanel(composite_value);
		this.add(view, BorderLayout.CENTER);
	}

	public JPanel getCheckPanel() {
		CRFlowLayout crf = new CRFlowLayout();
		crf.setAlignment(CRFlowLayout.LEFT);
		JPanel checkJPanel = new JPanel(crf);
		setAntialiasingCheck(new JCheckBox());
		getAntialiasingCheck()
				.setText(
						I18N
								.getString("orbisgis.org.orbisgis.configuration.activateAntialiasing"));
		getAntialiasingCheck().setSelected(antialiasing);
		getAntialiasingCheck().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!getAntialiasingCheck().isSelected()) {
					getAntialiasingCheck().setSelected(false);
				} else {
					getAntialiasingCheck().setEnabled(true);
					view.changeAntialiasing(true);
				}
			}
		});

		Vector<String> items = new Vector<String>();
		items.add(RenderingConfigurationConstants.items1);
		items.add(RenderingConfigurationConstants.items2);
		items.add(RenderingConfigurationConstants.items3);
		items.add(RenderingConfigurationConstants.items4);

		compositeCb = new JComboBox(items);
		if (composite_value != null)
			compositeCb.setSelectedItem(composite_value);
		getCompositeCb().setEnabled(composite);
		getCompositeCb().addItemListener(this);
		setCompositeCheck(new JCheckBox());
		getCompositeCheck()
				.setText(
						I18N
								.getString("orbisgis.org.orbisgis.configuration.activateSourceOver"));
		getCompositeCheck().setSelected(composite);

		getCompositeCheck().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!getCompositeCheck().isSelected()) {
					getCompositeCheck().setSelected(false);
					getCompositeCb().setEnabled(false);
				} else {
					getCompositeCb().setEnabled(true);
				}
			}
		});

		checkJPanel.add(getAntialiasingCheck());
		checkJPanel.add(new CarriageReturn());
		checkJPanel.add(getCompositeCheck());
		checkJPanel.add(getCompositeCb());

		return checkJPanel;

	}

	public void itemStateChanged(ItemEvent e) {

		if (e.getStateChange() != ItemEvent.SELECTED) {
			return;
		}
		Object choice = e.getSource();
		if (choice == getCompositeCb()) {
			alpha = (String) getCompositeCb().getSelectedItem();
			view.changeRule(alpha);
		} else {

		}

	}

	public void setAntialiasingCheck(JCheckBox antialiasingCheck) {
		this.antialiasingCheck = antialiasingCheck;
	}

	public JCheckBox getAntialiasingCheck() {
		return antialiasingCheck;
	}

	public void setCompositeCheck(JCheckBox compositeCheck) {
		this.compositeCheck = compositeCheck;
	}

	public JCheckBox getCompositeCheck() {
		return compositeCheck;
	}

	public void setCompositeCb(JComboBox compositeCb) {
		this.compositeCb = compositeCb;
	}

	public JComboBox getCompositeCb() {
		return compositeCb;
	}

}
