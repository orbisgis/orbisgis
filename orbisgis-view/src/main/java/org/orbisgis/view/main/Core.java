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
package org.orbisgis.view.main;

import bibliothek.gui.dock.support.lookandfeel.ComponentCollector;
import java.awt.Component;
import java.util.Collection;
import org.orbisgis.view.frames.MainFrame;

/**
 * The core manage the look and feel for each view of the Main Frame
 * This is the main UIContext
 */
public class Core implements ComponentCollector {
    private MainFrame mainFrame;
    public Collection<Component> listComponents() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    	/**
	 * Starts the application. This method creates the {@link MainFrame}
	 */
	public void startup(){
            if(mainFrame!=null) {
                
            }
        }
}
