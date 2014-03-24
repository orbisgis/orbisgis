/*
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
