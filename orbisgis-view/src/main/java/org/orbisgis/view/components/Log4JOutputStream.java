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
package org.orbisgis.view.components;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Define an outputstream to Log4J, can be used with PrintStream.
 * @author Nicolas Fortin
 */
public class Log4JOutputStream extends OutputStream {
        private Logger logger;
        private Level level;
        private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public Log4JOutputStream(Logger logger, Level level) {
                this.logger = logger;
                this.level = level;
        }
        @Override
        public void write(int i) throws IOException {
                buffer.write(i);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if(len > 0 && '\n' == b[len-1]) {
                // Print at each end of line
                buffer.write(b,off,len - 1);
                flush();
            } else {
                buffer.write(b,off,len);
            }
        }

        @Override
        public void flush() throws IOException {
                super.flush();
                // Fetch lines in the byte array
                String messages = buffer.toString();
                if(!messages.isEmpty()) {
                        logger.log(level, messages);
                }
                buffer.reset();
        }
}
