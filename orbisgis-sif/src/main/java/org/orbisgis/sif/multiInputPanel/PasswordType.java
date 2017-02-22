/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.sif.multiInputPanel;

import java.awt.Component;
import javax.swing.JPasswordField;

/**
 * This {@link InputType} is used to retrieve passwords from the users without
 * displaying them in the {@code Component}.
 * @author Alexis Guéganno
 * @author Erwan Bocher
 */
public class PasswordType implements InputType {

	private JPasswordField comp = new JPasswordField();

        /**
         * Builds a new {@code PasswordType}.
         * @param columns
         *  The number of columns for the underlying component.
         * @param isEditable
         *  To specify if the component can be edited or not.
         */
        public PasswordType(int columns, boolean isEditable) {
		comp.setColumns(columns);
                comp.setEditable(isEditable);
	}

        /**
         * Builds a new {@code PasswordType}.
         * @param columns
         *  The number of columns for the underlying component.
         */
	public PasswordType(int columns) {
		comp.setColumns(columns);
	}


        /**
         * Builds a new {@code PasswordType} with 5 columns.
         */
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
