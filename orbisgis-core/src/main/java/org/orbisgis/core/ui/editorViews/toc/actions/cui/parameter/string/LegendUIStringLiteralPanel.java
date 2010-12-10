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



package org.orbisgis.core.ui.editorViews.toc.actions.cui.parameter.string;

import javax.swing.Icon;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIComponent;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.LegendUIController;
import org.orbisgis.core.ui.editorViews.toc.actions.cui.components.TextInput;
import org.orbisgis.core.ui.preferences.lookandfeel.OrbisGISIcon;

/**
 *
 * This component edit string literals
 *
 * @author maxence
 */
public abstract class LegendUIStringLiteralPanel extends LegendUIComponent
		implements LegendUIStringComponent {

	private StringLiteral string;
	private TextInput input;

	public LegendUIStringLiteralPanel(String name, LegendUIController controller,
			LegendUIComponent parent, StringLiteral s, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);
		this.string = s;

		/*
		 *  TextInput ask to implement  the valueChanged method, which propagate any new value edited by the user
		 */
		input = new TextInput(name, string.getValue(null), 20, false) {

			@Override
			protected void valueChanged(String s) {
				LegendUIStringLiteralPanel.this.string.setValue(s);
			}
		};
	}

	@Override
	public Icon getIcon() {
		return OrbisGISIcon.PENCIL;
	}

	@Override
	protected void mountComponent() {
		editor.add(input);
	}

	@Override
	public StringParameter getStringParameter() {
		return string;
	}

	@Override
	public Class getEditedClass(){
		return StringLiteral.class;
	}

	@Override
	protected void turnOff() {
		stringChanged(null);
	}

	@Override
	protected void turnOn() {
		stringChanged(this.string);
	}

	/**
	 * This method is primarly called when user want to activate or disactivate
	 * @param string
	 */
	protected abstract void stringChanged(StringLiteral string);
}
