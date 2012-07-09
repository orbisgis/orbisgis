/*
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information. OrbisGIS is
 * distributed under GPL 3 license. It is produced by the "Atelier SIG" team of
 * the IRSTV Institute <http://www.irstv.cnrs.fr/> CNRS FR 2488.
 * 
 *
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
 * info _at_ orbisgis.org
 */
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import javax.swing.JPasswordField;

public class PasswordType implements InputType {

	private JPasswordField comp = new JPasswordField();

        public PasswordType(int columns, boolean isEditable) {
		comp.setColumns(columns);
                comp.setEditable(isEditable);
	}
        
	public PasswordType(int columns) {
		comp.setColumns(columns);
	}

	public PasswordType() {
		comp.setColumns(5);
	}

        @Override
	public Component getComponent() {
		return comp;
	}
	

        @Override
	public String getValue() {
		return new String(comp.getPassword());
	}

	public void setEditable(boolean b) {
		comp.setEditable(b);
	}

        @Override
	public void setValue(String value) {
		comp.setText(value);
	}
}
