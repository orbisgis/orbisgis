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

package org.orbisgis.orbistoolbox.view.utils;

import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.XElement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Sylvain PALOMINOS
 */
public class ProcessPanelLayout implements DockingPanelLayout {
    private ProcessEditableElement pee;

    public ProcessPanelLayout(ProcessEditableElement processEditableElement){
        this.pee = processEditableElement;
    }

    public ProcessPanelLayout(){
        this.pee = null;
    }

    @Override
    public void writeStream(DataOutputStream out) throws IOException {

    }

    @Override
    public void readStream(DataInputStream in) throws IOException {

    }

    @Override
    public void writeXML(XElement element) {

    }

    @Override
    public void readXML(XElement element) {

    }

    public ProcessEditableElement getProcessEditableElement(){
        return pee;
    }
}