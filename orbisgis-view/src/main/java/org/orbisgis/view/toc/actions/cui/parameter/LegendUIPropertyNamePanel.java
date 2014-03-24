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
package org.orbisgis.view.toc.actions.cui.parameter;

import org.orbisgis.view.toc.actions.cui.parameter.string.LegendUIStringComponent;
import org.orbisgis.view.toc.actions.cui.parameter.real.LegendUIRealComponent;
import org.orbisgis.view.toc.actions.cui.parameter.color.LegendUIColorComponent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.gdms.data.DataSource;
import org.gdms.data.schema.Metadata;
import org.gdms.driver.DriverException;
import org.orbisgis.core.renderer.se.parameter.ValueReference;
import org.orbisgis.core.renderer.se.parameter.real.RealParameter;
import org.orbisgis.view.toc.actions.cui.components.ComboBoxInput;
import org.orbisgis.view.icons.OrbisGISIcon;

import org.gdms.data.types.Type;
import org.orbisgis.core.renderer.se.parameter.color.ColorParameter;
import org.orbisgis.core.renderer.se.parameter.geometry.GeometryAttribute;
import org.orbisgis.core.renderer.se.parameter.string.StringParameter;
import org.orbisgis.view.toc.actions.cui.LegendUIComponent;
import org.orbisgis.view.toc.actions.cui.LegendUIController;

/**
 *
 * @author Maxence Laurent
 */
public abstract class LegendUIPropertyNamePanel extends LegendUIComponent
		implements LegendUIColorComponent, LegendUIRealComponent,
				   LegendUIStringComponent {

	private ComboBoxInput fieldList;
	private ValueReference p;

	public LegendUIPropertyNamePanel(String name, LegendUIController controller,
            LegendUIComponent parent, ValueReference val, boolean isNullable) {
		super(name, controller, parent, 0, isNullable);
        if(val == null){
                throw new IllegalArgumentException("This ValueReference should not be null !");
        }
        this.p = val;


		Metadata meta;
		int mask = 0;

		ArrayList<String> possibilities = new ArrayList<String>();
		DataSource ds = controller.getEditedFeatureTypeStyle().getLayer().getDataSource();

		int current = 0;

		try {
			meta = ds.getMetadata();

			if (p instanceof RealParameter) {
				mask = Type.BYTE + Type.DOUBLE + Type.FLOAT + Type.INT
						+ Type.LONG + Type.SHORT;
			} else if (p instanceof StringParameter) {
				mask = Type.BINARY + Type.BOOLEAN + Type.BYTE + Type.DATE
						+ Type.DOUBLE + Type.FLOAT + Type.INT + Type.LONG
						+ Type.SHORT + Type.STRING + Type.TIMESTAMP + Type.TIME;
			} else if (p instanceof GeometryAttribute) {
				mask = Type.GEOMETRY + Type.RASTER;
			} else if (p instanceof ColorParameter) {
				mask = Type.STRING;
			}

			for (int i = 0; i < meta.getFieldCount(); i++) {
				if ((meta.getFieldType(i).getTypeCode() & mask) > 0) {
					possibilities.add(meta.getFieldName(i));
                                        if(p.getColumnName() == null || p.getColumnName().equals("")){
                                                p.setColumnName(meta.getFieldName(i));
                                        }
					if (p.getColumnName().equalsIgnoreCase(meta.getFieldName(i))) {
						current = possibilities.size() - 1;
					}
				}
			}

		} catch (DriverException ex) {
			Logger.getLogger(LegendUIPropertyNamePanel.class.getName()).log(Level.SEVERE, null, ex);
		}


		fieldList = new ComboBoxInputImpl(possibilities.toArray(new String[0]), current, p);
	}

	@Override
	public Icon getIcon() {
        return OrbisGISIcon.getIcon("palette");
	}

	@Override
	protected void mountComponent() {
		editor.add(fieldList);
	}

	@Override
	public ColorParameter getColorParameter() {
		if (p instanceof ColorParameter) {
			return (ColorParameter) p;
		} else {
			return null;
		}
	}

	@Override
	public RealParameter getRealParameter() {
		if (p instanceof RealParameter) {
			return (RealParameter) p;
		} else {
			return null;
		}
	}

	@Override
	public StringParameter getStringParameter() {
		if (p instanceof StringParameter){
			return (StringParameter) p;
		}else{
			return null;
		}
	}

	private static class ComboBoxInputImpl extends ComboBoxInput {

		private String[] possibilities;
		private ValueReference pName;

		private ComboBoxInputImpl(String[] possibilities, int current, ValueReference pName) {
			super(possibilities, current);
			this.possibilities = possibilities;
			this.pName = pName;
		}

		@Override
		protected void valueChanged(int i) {
			pName.setColumnName(possibilities[i]);
		}
	}
	
	@Override
	public Class getEditedClass() {
		return p.getClass();
	}

}
