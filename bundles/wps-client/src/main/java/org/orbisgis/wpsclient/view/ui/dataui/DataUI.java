/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsclient.view.ui.dataui;

import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsclient.WpsClient;

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
     * Return the UI (a JComponent) which contain all the element to configure the input or output in argument.
     * @param inputOrOutput Input or output to render.
     * @param dataMap Map that will contain the data once the input or output configured.
     * @return JComponent containing the UI.
     */
    public JComponent createUI(DescriptionType inputOrOutput, Map<URI, Object> dataMap);

    /**
     * Returns the map of default input value if it exists.
     * @param inputOrOutput Input or Output to analyse.
     * @return The default input or output value map.
     */
    public Map<URI, Object> getDefaultValue(DescriptionType inputOrOutput);

    /**
     * Return an image icon representing the data or null if there is no image associated.
     * @param inputOrOutput Input or output to analyse.
     * @return The icon associated to the given data.
     */
    public ImageIcon getIconFromData(DescriptionType inputOrOutput);

    /**
     * Sets the ToolBox associated to the DataUI.
     * @param wpsClient ToolBox
     */
    public void setWpsClient(WpsClient wpsClient);
}
