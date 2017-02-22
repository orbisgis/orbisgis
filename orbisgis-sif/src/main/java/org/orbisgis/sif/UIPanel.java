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
