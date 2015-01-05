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
package org.orbisgis.view.docking.internals;

import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.util.xml.XElement;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Docking Frames does not use common interface for ApplicationResource and MultipleCDockableLayout
 * @author Nicolas Fortin
 */
public class ApplicationRessourceDecorator implements ApplicationResource {
        MultipleCDockableLayout decorated;

        public ApplicationRessourceDecorator(MultipleCDockableLayout decorated) {
                this.decorated = decorated;
        }

        @Override
        public void write(DataOutputStream out) throws IOException {
                decorated.writeStream(out);
        }

        @Override
        public void read(DataInputStream in) throws IOException {
                decorated.readStream(in);
        }

        @Override
        public void writeXML(XElement element) {
                decorated.writeXML(element);
        }

        @Override
        public void readXML(XElement element) {
                decorated.readXML(element);
        }
        
}
