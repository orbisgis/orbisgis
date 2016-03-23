package org.orbisgis.wpsservice.controller.execution;

import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class RawDataProcessing implements DataProcessing {

    private LocalWpsService wpsService;

    @Override
    public void setLocalWpsService(LocalWpsService wpsService) {
        this.wpsService = wpsService;
    }

    @Override
    public Class<? extends DataDescription> getDataClass() {
        return RawDataOld.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel) {
        Map<URI, Object> map = new HashMap<>();
        map.put(inputOrOutput.getIdentifier(), null);
        return map;
    }

    @Override
    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel) {
        //Check if it is an output
        if(inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            //Check if the input is a GeometryData
            if(output.getDataDescription() instanceof RawDataOld) {
                if(pel != null) {
                    pel.appendLog(ProcessExecutionListener.LogType.INFO, "Output RawData '" +
                            output.getTitle() + "' is '" + dataMap.get(output.getIdentifier()) + "'.");
                }
            }
        }
    }
}
