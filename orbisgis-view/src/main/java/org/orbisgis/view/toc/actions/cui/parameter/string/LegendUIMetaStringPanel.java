/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.parameter.string;

import javax.swing.Icon;

import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;

import org.orbisgis.core.renderer.se.parameter.string.Categorize2String;
import org.orbisgis.core.renderer.se.parameter.string.Recode2String;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringLiteral;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;

import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;

import org.orbisgis.view.toc.actions.cui.parameter.LegendUICategorizePanel;
import org.orbisgis.view.toc.actions.cui.parameter.LegendUIPropertyNamePanel;
import org.orbisgis.view.toc.actions.cui.parameter.LegendUIRecodePanel;
import org.orbisgis.view.icons.OrbisGISIcon;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIMetaStringPanel extends LegendUIAbstractMetaPanel {

	private StringParameter string;
	private LegendUIComponent comp;

	private final Class[] classes = {StringLiteral.class, StringAttribute.class, Categorize2String.class, Recode2String.class};

	public LegendUIMetaStringPanel(String name, LegendUIController controller, LegendUIComponent parent, StringParameter s, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		this.string = s;

		comp = null;
		if (string != null) {
			comp = getCompForClass(string.getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == StringLiteral.class) {
			StringLiteral s;
			if (string instanceof StringLiteral) {
				s = (StringLiteral) string;
			} else {
				s = new StringLiteral();
			}

			return new LegendUIStringLiteralPanel("Constant" + getName(), controller, this, s, false) {

				@Override
				protected void stringChanged(StringLiteral string) {
					throw new UnsupportedOperationException("Unreachable code.");
				}

			};
		} else if (newClass == StringAttribute.class) {
			StringAttribute s;
			if (string instanceof StringAttribute) {
				s = (StringAttribute) string;
			} else {
				s = new StringAttribute("");
			}

			return new LegendUIPropertyNamePanel("Attribute " + getName(), controller, this, s, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == Categorize2String.class) {
			Categorize2String s;
			if (string instanceof Categorize2String) {
				s = (Categorize2String) string;
			} else {
				s = new Categorize2String(new StringLiteral("Class1"),
						new StringLiteral("FallbackValue"),
						new RealAttribute(""));
			}

			return new LegendUICategorizePanel("Categorized " + getName(), controller, this, s, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == Recode2String.class) {
			Recode2String s;

			if (string instanceof Recode2String) {
				s = (Recode2String) string;
			} else {
				s = new Recode2String(new StringLiteral("n/a"), new StringAttribute(""));
			}
			return new LegendUIRecodePanel("UniqueValue mapping " + getName(), controller, this, s, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else {
			return null;
		}
	}

	@Override
	public void init() {
		init(classes, comp);
	}

	@Override
	protected void switchTo(LegendUIComponent comp) {
		if (comp != null) {
			this.string = ((LegendUIStringComponent) comp).getStringParameter();
			this.stringChanged(string);
		} else {
			this.stringChanged(null);
		}
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("pencil");
	}

	public abstract void stringChanged(StringParameter newString);

	@Override
	public Class getEditedClass() {
		return string.getClass();
	}
}
