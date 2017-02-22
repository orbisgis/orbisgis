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
import javax.swing.JCheckBox;

/**
 * {@code InputType} particularly designed for booleans : if the inner {@code
 * JCheckbox} is checked, the boolean will be true, false otherwise. Note that
 * as {@code InputType} returns only {@code String} values through its {@code
 * {@code getValue} method, the boolean will be returned as a String...
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public class CheckBoxChoice extends JCheckBox implements InputType {
        
        
        public CheckBoxChoice(boolean b) {
                super("", b);
        }

        public CheckBoxChoice(boolean b, String text) {
            super(text,b);
        }

        @Override
        public Component getComponent() {
                return this;
        }       

        /**
         * The {@code String} representation of the associated {@code boolean}.
         * @return
         */
        @Override
        public String getValue() {
                return Boolean.toString(this.isSelected());
        }

        /**
         * Set the value stored in this {@code CheckBoxChoice}. If {@code value}
         * is equal to the string {@code true}, ignoring case, the associated
         * checkbox will be checked. If value is null or equal to anything else,
         * the cehckbox will be unchecked.
         * @param value
         */
        @Override
        public void setValue(String value) {
                this.setSelected(Boolean.parseBoolean(value));
        }
}
