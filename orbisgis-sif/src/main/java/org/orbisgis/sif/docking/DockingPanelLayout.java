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
package org.orbisgis.sif.docking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * When the application start,
 * this layout saved from the previous application instance 
 * feed the appropriate DockingPanelFactory to restore the DockingPanel state.
 */

public interface DockingPanelLayout {
        /**
         * Writes the content of this layout into <code>out</code>.
         * @param out the stream to write into
         * @throws java.io.IOException if an I/O-error occurs
         */
        public void writeStream( DataOutputStream out ) throws IOException;

        /**
         * Reads the content of this layout from <code>out</code>. All
         * properties should be set to their default value or to the value read
         * from the stream.
         * @param in the stream to read
         * @throws IOException if an I/O-error occurs
         */
        public void readStream( DataInputStream in ) throws IOException;

        /**
         * Writes the content of this layout into <code>element</code>.
         * @param element the xml element into which this method can write,
         * the attributes of <code>element</code> should not be changed
         */
        public void writeXML( XElement element );

        /**
         * Initialise this instance with the
         */
        public void readXML( XElement element );
}
