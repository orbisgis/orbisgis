package org.orbisgis.orbistoolbox;

import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.controller.process.ProcessIdentifier;
import org.orbisgis.orbistoolbox.controller.process.ProcessManager;

import java.io.File;
import java.net.URI;
import java.util.Map;

public class WpsService {
    /** String of the Groovy file extension. */
    public static final String GROOVY_EXTENSION = "groovy";

    /** Process manager which contains all the loaded scripts. */
    private ProcessManager processManager;

    public WpsService(){
        processManager = new ProcessManager();
    }

    /**
     * Returns the process manager.
     * @return The process manager.
     */
    public ProcessManager getProcessManager(){
        return processManager;
    }

    public ProcessIdentifier addLocalScript(File f, String iconName, boolean isDefaultScript){
        if(f.getName().endsWith(GROOVY_EXTENSION)) {
            processManager.addLocalScript(f.toURI(), iconName, isDefaultScript);
            ProcessIdentifier pi = processManager.getProcessIdentifier(f.toURI());
            if(pi != null) {
                return pi;
            }
        }
        return null;
    }

    public Process getProcess(URI uri){
        return processManager.getProcess(uri);
    }

    public void removeProcess(URI uri){
        processManager.removeProcess(processManager.getProcess(uri));
    }

    public boolean checkProcess(URI uri){
        ProcessIdentifier pi = processManager.getProcessIdentifier(uri);
        if(pi != null){
            processManager.removeProcess(pi.getProcess());
        }
        return (processManager.addLocalScript(uri, pi.getCategory(), pi.isDefault()) != null);
    }

    public void executeProcess(Process process, Map<URI, Object> dataMap, Map<String, Object> properties){
        processManager.executeProcess(process, dataMap, properties);
    }
}
