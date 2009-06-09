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
package org.orbisgis.core.ui.views.geocatalog.actions.create;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.gdms.data.types.Constraint;
import org.gdms.data.types.ConstraintFactory;
import org.orbisgis.sif.AbstractUIPanel;

public class LiteralConstraintPanel extends AbstractUIPanel implements
		UIConstraintPanel {

	private JPanel panel;
	private boolean isInt;
	private JTextField txtPanel;
	private int constraintCode;

	public LiteralConstraintPanel(int constraintCode, boolean isInt,
			String value) {
		this.constraintCode = constraintCode;
		this.isInt = isInt;
		panel = new JPanel();
		txtPanel = new JTextField(value, 8);
		panel.add(new JLabel("Value: "));
		panel.add(txtPanel);
	}

	public Constraint getConstraint() {
		if (isInt) {
			int intValue = Integer.parseInt(txtPanel.getText());
			return ConstraintFactory.createConstraint(constraintCode, intValue);
		} else {
			return ConstraintFactory.createConstraint(constraintCode, txtPanel
					.getText().trim());
		}
	}

	public Component getComponent() {
		return panel;
	}

	public String getTitle() {
		return "Specify the value for the "
				+ ConstraintFactory.getConstraintName(constraintCode);
	}

	public String validateInput() {
		if (isInt) {
			try {
				Integer.parseInt(txtPanel.getText());
			} catch (NumberFormatException e) {
				return "the value must be int";
			}
		}

		return null;
	}

}
