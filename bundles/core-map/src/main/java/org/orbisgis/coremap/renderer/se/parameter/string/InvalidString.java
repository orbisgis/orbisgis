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
package org.orbisgis.coremap.renderer.se.parameter.string;

/**
 * Exception to be thrown when a StringParameter can't be validate against its
 * restrictions.
 * @author Alexis Guéganno
 */
public class InvalidString extends RuntimeException {

        /**
         * Default constructor.
         */
        public InvalidString() {
        }

        /**
         * Builds a new InvalidString because of {@code cause} with message
         * {@code message}.
         * @param message
         * @param cause
         */
        public InvalidString(String message, Throwable cause) {
                super(message, cause);
        }

        /**
         * Builds a new InvalidString with message {@code message}.
         * @param message
         */
        public InvalidString(String message) {
                super(message);
        }

        /**
         * Builds a new InvalidString because of {@code cause}.
         * @param cause
         */
        public InvalidString(Throwable cause) {
                super(cause);
        }

}
