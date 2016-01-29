/**
 * OrbisGIS is a GIS application dedicated to scientific spatial analysis.
 * This cross-platform GIS is developed at the Lab-STICC laboratory by the DECIDE 
 * team located in University of South Brittany, Vannes.
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 IRSTV (FR CNRS 2488)
 * Copyright (C) 2015-2016 CNRS (UMR CNRS 6285)
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
package org.orbisgis.sif;

import java.awt.Component;
import java.net.URL;

/**
 * Interface that provides the necessary information to the SIF framework to
 * show dialogs and perform some validation
 *
 */
public interface UIPanel {

        /**
         * Gets the icon of the UIPanel. If this method returns null, the
         * default icon in {@link UIFactory} is used
         *
         * @return
         */
        URL getIconURL();

        /**
         * Gets the title to show in the dialog
         *
         * @return
         */
        String getTitle();

        /**
         * A method invoked when the user clicks on the ok or next button to validate the contents of the interface
         *
         * @return A String An error description if the validation fails, a
         * warning or null if everything is ok
         */
        String validateInput();

        /**
         * Gets the swing component to show in the dialog
         *
         * @return
         */
        Component getComponent();
        
}
