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

package org.orbisgis.toolboxeditor.dataui;

import net.opengis.wps._2_0.DescriptionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;

import javax.swing.*;
import java.net.URI;
import java.util.Map;

/**
 * Interface for the class that will create the UI for a specific data type (LiteralData, RawData, ShapeFileData ...) .
 * The UI should allow the user to configure the Inputs and Outputs during the process configuration.
 *
 * @author Sylvain PALOMINOS
 **/

public interface DataUI {

    /**
     * Orientation of the UI build in the 'createUI' method.
     */
    enum Orientation{VERTICAL, HORIZONTAL}

    /**
     * Return the UI (a JComponent) which contain all the element to configure the input or output in argument.
     * @param inputOrOutput Input or output to render.
     * @param dataMap Map that will contain the data once the input or output configured.
     * @param orientation Orientation of the UI.
     * @return JComponent containing the UI.
     */
    JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Orientation orientation);

    /**
     * Returns the map of default input value if it exists.
     * @param inputOrOutput Input or Output to analyse.
     * @return The default input or output value map.
     */
    Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput);

    /**
     * Return an image icon representing the data or null if there is no image associated.
     * @param inputOrOutput Input or output to analyse.
     * @return The icon associated to the given data.
     */
    ImageIcon getIconFromData(DescriptionType inputOrOutput);

    /**
     * Sets the ToolBox associated to the DataUI.
     * @param wpsClient ToolBox
     */
    void setWpsClient(WpsClientImpl wpsClient);
}
