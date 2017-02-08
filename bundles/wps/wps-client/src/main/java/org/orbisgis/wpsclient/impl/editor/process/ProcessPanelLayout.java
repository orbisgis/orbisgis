package org.orbisgis.wpsclient.impl.editor.process;

import org.orbisgis.sif.docking.DockingPanelLayout;
import org.orbisgis.sif.docking.XElement;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 *
 * @author Sylvain PALOMINOS
 */
public class ProcessPanelLayout implements DockingPanelLayout {

    private ProcessEditableElement processEditableElement;

    public ProcessPanelLayout(ProcessEditableElement processEditableElement) {
        this.processEditableElement = processEditableElement;
    }

    public ProcessEditableElement getProcessEditableElement() {
        return processEditableElement;
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
}
