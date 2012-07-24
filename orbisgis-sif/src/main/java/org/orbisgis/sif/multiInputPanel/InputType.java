/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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

/**
 * {@code InputType} instances are used to build quickly some UIs where the user
 * will be able to provide some informations to the software. This objects can
 * be used to retrieve a {@code Component} where the information can be set.
 * @author Erwan Bocher
 * @author Alexis Guéganno
 */
public interface InputType {

        /**
         * Gets the {@code Component} that will be used to retrieve the inputs
         * from the user.
         * @return
         */
        Component getComponent();

        /**
         * Gets the value currently stored in this {@code InputType} instance.
         * @return
         * The stored value as a {@code String}.
         */
        String getValue();

        /**
         * Sets the value stored in this {@code InputType} to {@code value}.
         * @param value
         */
        void setValue(String value);
}
