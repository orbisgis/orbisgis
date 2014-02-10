/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-2012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.view.toc.actions.cui.legend;

import java.awt.Component;
import org.orbisgis.view.toc.actions.cui.LegendContext;

/**
 * Root interface for UI panels in the SimpleStyleEditor.
 *
 * @author Alexis Guéganno
 */
public interface ISELegendPanel {
    static final String NAME_PROPERTY = "name_property";

	/**
	 * This function will return the Component of the object (normally a
	 * JPanel).
	 * 
	 * @return Component
	 */
	Component getComponent();

    /**
     * Gets the identifier of this panel.
     * @return
     */
    String getId();

    /**
     * Associates an identifier to this panel. Particularly useful if we
     * want to put this panel in a {@code CardLayout}.
     * @param newId
     */
    void setId(String newId);

	/**
	 * @return {@code null} if the status of the edited legend is ok. An error message
	 *         if the legend cannot be created
	 */
	String validateInput();
}
