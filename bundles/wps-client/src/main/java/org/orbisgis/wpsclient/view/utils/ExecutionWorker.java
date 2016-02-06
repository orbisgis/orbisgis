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

package org.orbisgis.wpsclient.view.utils;

import org.orbisgis.commons.progress.SwingWorkerPM;
import org.orbisgis.wpsclient.view.utils.editor.process.ProcessEditableElement;
import org.orbisgis.wpsservice.WpsService;
import org.orbisgis.wpsservice.model.Process;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Swing worker executing a process.
 * This worker execute a WPS process in three steps:
 *  - Pre-process the input/output : Some input/output need to be prepared before been used for the execution.
 *  - Execute : execute the process by running the Groovy script.
 *  - Post-process input/output : Some input/output need to be processed to be on the good form
 *      (like exporting a DataStore in the CSV file format.)
 * It uses the ProcessEditableElement to communicates the execution log and process state to the rest of the ToolBox.
 *
 * @author Sylvain PALOMINOS
 **/

public class ExecutionWorker extends SwingWorkerPM{

    /** Process to execute */
    private Process process;
    /** Input and output data map */
    private Map<URI, Object> dataMap;
    /** ToolBox */
    private WpsService wpsService;
    /** Process element containing all the information it */
    private ProcessEditableElement pee;

    /**
     * Main constructor.
     * @param pee ProcessEditableElement which will be used to communicate the state and the log of the process.
     */
    public ExecutionWorker(ProcessEditableElement pee, WpsService wpsService){
        this.pee = pee;
        this.process = pee.getProcess();
        this.dataMap = new HashMap<>();
        this.dataMap.putAll(pee.getInputDataMap());
        this.dataMap.putAll(pee.getOutputDataMap());
        this.wpsService = wpsService;
    }

    @Override
    protected Object doInBackground() throws Exception {
        wpsService.executeProcess(process, dataMap, pee);
        return null;
    }
}
