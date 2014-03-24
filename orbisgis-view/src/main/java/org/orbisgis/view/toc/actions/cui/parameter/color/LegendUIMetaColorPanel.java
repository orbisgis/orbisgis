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
package org.orbisgis.view.toc.actions.cui.parameter.color;

import javax.swing.Icon;
import org.orbisgis.view.icons.OrbisGISIcon;
import org.orbisgis.core.renderer.se.parameter.color.Categorize2Color;
import org.orbisgis.core.renderer.se.parameter.color.ColorAttribute;
import org.orbisgis.core.renderer.se.parameter.color.ColorLiteral;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.color.Interpolate2Color;
import org.orbisgis.core.renderer.se.parameter.color.Recode2Color;
import org.orbisgis.core.renderer.se.parameter.real.RealAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringAttribute;
import org.orbisgis.view.toc.actions.cui.LegendUIAbstractMetaPanel;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;
import org.orbisgis.view.toc.actions.cui.parameter.LegendUICategorizePanel;
import org.orbisgis.view.toc.actions.cui.parameter.LegendUIPropertyNamePanel;
import org.orbisgis.view.toc.actions.cui.parameter.LegendUIRecodePanel;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIMetaColorPanel extends LegendUIAbstractMetaPanel {

	private ColorParameter color;
	private int initial;
	private LegendUIComponent comp;
	private final Class[] classes = { ColorLiteral.class, ColorAttribute.class, Categorize2Color.class, Recode2Color.class};

	public LegendUIMetaColorPanel(String name, LegendUIController controller, LegendUIComponent parent, ColorParameter c, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);

		this.color = c;

		if (color == null) {
			comp = null;
		} else {
			comp = getCompForClass(color.getClass());
		}
	}

	@Override
	protected final LegendUIComponent getCompForClass(Class newClass) {
		if (newClass == ColorLiteral.class) {
			ColorLiteral literal;
			if (color instanceof ColorLiteral) {
				literal = (ColorLiteral) color;
			} else {
				literal = new ColorLiteral();
			}

			return new LegendUIColorLiteralPanel("Constant " + getName(), controller, this, literal, false) {

				@Override
				protected void colorChanged(ColorLiteral color) {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};
		} else if (newClass == ColorAttribute.class) {
			ColorAttribute pName;
			if (color instanceof ColorAttribute) {
				pName = (ColorAttribute) color;
			} else {
				pName = new ColorAttribute("");
			}


			return new LegendUIPropertyNamePanel("Attribute " + getName(), controller, this, pName, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};


		} else if (newClass == Categorize2Color.class) {
			Categorize2Color categorize;
			if (color instanceof Categorize2Color) {
				categorize = (Categorize2Color) color;
			} else {
				categorize = new Categorize2Color(new ColorLiteral(),
						new ColorLiteral(), new RealAttribute(""));
			}

			return new LegendUICategorizePanel("Categorized " + getName(), controller, this, categorize, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};

		} else if (newClass == Recode2Color.class) {
			Recode2Color recode;
			if (color instanceof Recode2Color) {
				recode = (Recode2Color) color;
			} else {
				recode = new Recode2Color(new ColorLiteral(), new StringAttribute((String)null));
			}
			return new LegendUIRecodePanel("UniqueValue map " + getName(), controller, this, recode, false) {

				@Override
				protected void turnOff() {
					throw new UnsupportedOperationException("Unreachable code.");
				}

				@Override
				protected void turnOn() {
					throw new UnsupportedOperationException("Unreachable code.");
				}
			};


		} else if (newClass == Interpolate2Color.class) {
			Interpolate2Color interpol;
			if (color instanceof Interpolate2Color) {
				interpol = (Interpolate2Color) color;
			} else {
				interpol = new Interpolate2Color(new ColorLiteral());
			}

			return null;
			//_return new LegendUIInterpolll...I("UniqueValue map " + name, controller, this, interpol, false);

			/*comps[4] = new LegendUIAlgebricalPanel("Alg. " + name, controller, this, color){
			@Override
			public void colorChanged(ColorParameter newColor) {
			switchTo(comps[4]);
			}
			};*/
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
			this.color = ((LegendUIColorComponent) comp).getColorParameter();
			this.colorChanged(color);
		} else {
			this.colorChanged(null);
		}
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
	}

	public abstract void colorChanged(ColorParameter newColor);

	@Override
	public Class getEditedClass() {
		return color.getClass();
	}
}
