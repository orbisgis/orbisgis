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
package org.orbisgis.toolboxeditor.editor.process;

import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.XElement;
import org.orbiswps.client.api.utils.ProcessExecutionType;
import org.orbisgis.toolboxeditor.WpsClientImpl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessPanelLayout implements DockingPanelLayout {

    private ProcessEditableElement processEditableElement;
    private WpsClientImpl wpsClient;

    public ProcessPanelLayout(ProcessEditableElement processEditableElement, WpsClientImpl wpsClient) {
        this.processEditableElement = processEditableElement;
        this.wpsClient = wpsClient;
    }

    public ProcessEditableElement getProcessEditableElement() {
        return processEditableElement;
    }

    @Override
    public void writeStream(DataOutputStream out) throws IOException {
        out.writeUTF(processEditableElement.getProcess().getIdentifier().getValue());
        out.writeUTF(processEditableElement.getProcessExecutionType().name());
    }

    @Override
    public void readStream(DataInputStream in) throws IOException {
        URI identifier = URI.create(in.readUTF());
        ProcessExecutionType processExecutionType = ProcessExecutionType.valueOf(in.readUTF());
        ProcessOffering processOffering = null;
        if (wpsClient != null) {
            List<ProcessOffering> processOfferingList = wpsClient.getProcessOffering(identifier);
            if (processOfferingList != null && processOfferingList.size() != 0) {
                processOffering = processOfferingList.get(0);
            }
        }
        processEditableElement = new ProcessEditableElement(processOffering, identifier, new HashMap<URI, Object>());
        processEditableElement.setProcessExecutionType(processExecutionType);
    }

    @Override
    public void writeXML(XElement element) {
        element.addString("processUri", processEditableElement.getProcess().getIdentifier().getValue());
        element.addString("executionType", processEditableElement.getProcessExecutionType().name());
    }

    @Override
    public void readXML(XElement element) {
        URI identifier = URI.create(element.getString("processUri"));
        ProcessExecutionType processExecutionType = ProcessExecutionType.valueOf(element.getString("executionType"));
        ProcessOffering processOffering = null;
        if (wpsClient != null) {
            List<ProcessOffering> processOfferingList = wpsClient.getProcessOffering(identifier);
            if (processOfferingList != null && processOfferingList.size() != 0) {
                processOffering = processOfferingList.get(0);
            }
        }
        processEditableElement = new ProcessEditableElement(processOffering, identifier, new HashMap<URI, Object>());
        processEditableElement.setProcessExecutionType(processExecutionType);
    }
}