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

package org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing;

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.view.ToolBox;

import java.net.URI;
import java.util.Map;

/**
 * Default processing class
 *
 * @author Sylvain PALOMINOS
 **/

public class DefaultProcessing implements ProcessingData{
    @Override
    public Class<? extends DataDescription> getDataClass() {
        return DataDescription.class;
    }

    @Override
    public void preProcessing(DescriptionType inputOrOutput,
                              Map<URI, Object> dataMap,
                              ToolBox toolBox) {}

    @Override
    public void postProcessing(DescriptionType inputOrOutput,
                               Map<URI, Object> dataMap,
                               ToolBox toolBox) {}
}
