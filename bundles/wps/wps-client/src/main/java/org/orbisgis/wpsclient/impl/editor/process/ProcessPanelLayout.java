package org.orbisgis.wpsclient.impl.editor.process;

import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.XElement;
import org.orbisgis.wpsclient.api.utils.ProcessExecutionType;
import org.orbisgis.wpsclient.impl.WpsClientImpl;

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
        if(wpsClient != null) {
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
        if(wpsClient != null) {
            List<ProcessOffering> processOfferingList = wpsClient.getProcessOffering(identifier);
            if (processOfferingList != null && processOfferingList.size() != 0) {
                processOffering = processOfferingList.get(0);
            }
        }
        processEditableElement = new ProcessEditableElement(processOffering, identifier, new HashMap<URI, Object>());
        processEditableElement.setProcessExecutionType(processExecutionType);
    }
}
