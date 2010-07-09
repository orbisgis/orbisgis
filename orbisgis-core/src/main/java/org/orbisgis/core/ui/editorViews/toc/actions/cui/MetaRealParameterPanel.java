/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 *
 *
 *  Team leader Erwan BOCHER, scientific researcher,
 *
 *  User support leader : Gwendall Petit, geomatic engineer.
 *
 *
 * Copyright (C) 2007 Erwan BOCHER, Fernando GONZALEZ CORTES, Thomas LEDUC
 *
 * Copyright (C) 2010 Erwan BOCHER, Pierre-Yves FADET, Alexis GUEGANNO, Maxence LAURENT
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
 *
 * or contact directly:
 * erwan.bocher _at_ ec-nantes.fr
 * gwendall.petit _at_ ec-nantes.fr
 */
package org.orbisgis.core.ui.editorViews.toc.actions.cui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.orbisgis.core.renderer.se.parameter.real.Categorize2Real;
import org.orbisgis.core.renderer.se.parameter.real.Interpolate2Real;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.real.RealBinaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.RealLiteral;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.core.renderer.se.parameter.real.RealUnitaryOperator;
import org.orbisgis.core.renderer.se.parameter.real.Recode2Real;

/**
 *
 * @author maxence
 */
public abstract class MetaRealParameterPanel extends JPanel {

	JButton changeType;
	JPanel currentPanel;
	EditRealLiteralPanel literalPanel;
	//EditColorAttributePanel attributePanel;

	public MetaRealParameterPanel(RealParameter p) {
		super(new FlowLayout());

		currentPanel = null;
		if (p != null) {
			if (p instanceof RealLiteral) {
				literalPanel = new EditRealLiteralPanel((RealLiteral) p);
				currentPanel = literalPanel;
			} else if (p instanceof RealAttribute) {
				//literalPanel = new EditRealAttributePanel((RealLiteral) p, min, max);
				//currentPanel = literalPanel;
			} else if (p instanceof Categorize2Real) {
				//literalPanel = new
				//currentPanel = literalPanel;
			} else if (p instanceof Recode2Real) {
				//literalPanel = new EditRealLiteralPanel((RealLiteral) p, min, max);
				//currentPanel = literalPanel;
			} else if (p instanceof Interpolate2Real) {
				//literalPanel = new EditRealLiteralPanel((RealLiteral) p, min, max);
				//currentPanel = literalPanel;
			} else if (p instanceof RealBinaryOperator || p instanceof RealUnitaryOperator) {
				//literalPanel = new EditRealLiteralPanel((RealLiteral) p, min, max);
				//currentPanel = literalPanel;
			}
		} else {
			currentPanel = new EmptyPanel("no value");
		}

		if (currentPanel == null) {
			currentPanel = new EmptyPanel("not yet implemented");
		}

		this.add(currentPanel);

		changeType = new JButton("ct");
		changeType.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showInputDialog(I, e, TOOL_TIP_TEXT_KEY, WIDTH, null, selectionValues, literalPanel)
			}
		});
		this.add(changeType);
	}

	public abstract void realChanged(RealParameter newReal);
}
