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

package org.orbisgis.orbistoolbox.view.ui;

import org.orbisgis.orbistoolbox.view.ui.ProcessUIBuilder;
import org.orbisgis.sif.UIPanel;
import org.orbisgis.orbistoolbox.model.Process;

import java.awt.*;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * SIF UIPanel for the configuration of a process before executing it.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessInputConfiguration implements UIPanel {

    private Map<URI, Object> uriObjectMap;
    private Component component;

    @Override
    public URL getIconURL() {
        return null;
    }

    @Override
    public String getTitle() {
        return "Process inputs configuration.";
    }

    @Override
    public String validateInput() {
        return null;
    }

    @Override
    public Component getComponent() {
        return component;
    }

    public void buildUI(Process process, ProcessUIBuilder processUIBuilder){
        uriObjectMap = new HashMap<>();
        this.component = processUIBuilder.buildUI(process, uriObjectMap);
    }

    public Map<URI, Object> getData(){
        return uriObjectMap;
    }
}
