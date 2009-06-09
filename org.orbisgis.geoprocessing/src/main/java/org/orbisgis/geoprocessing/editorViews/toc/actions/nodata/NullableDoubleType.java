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
package org.orbisgis.geoprocessing.editorViews.toc.actions.nodata;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.orbisgis.sif.SQLUIPanel;
import org.orbisgis.sif.multiInputPanel.InputType;

public class NullableDoubleType implements InputType, ChangeListener {

	private JPanel panel = new JPanel();
	private JCheckBox jCheckBox = new JCheckBox();
	private JTextField jTextField = new JTextField();

	public NullableDoubleType(int columns) {
		jCheckBox.setSelected(false);
		jCheckBox.addChangeListener(this);
		jTextField.setColumns(columns);
		jTextField.setEnabled(false);
		panel.add(jCheckBox);
		panel.add(jTextField);
	}

	public Component getComponent() {
		return panel;
	}

	public int getType() {
		return SQLUIPanel.DOUBLE;
	}

	public String getValue() {
		if (jCheckBox.isSelected()) {
			return jTextField.getText();
		}
		return null;
	}

	public boolean isPersistent() {
		return true;
	}

	public void setValue(String value) {
		jCheckBox.setSelected(value != null);
		jTextField.setText(value);

	}

	public void stateChanged(ChangeEvent e) {
		jTextField.setEnabled(jCheckBox.isSelected());
	}

}
