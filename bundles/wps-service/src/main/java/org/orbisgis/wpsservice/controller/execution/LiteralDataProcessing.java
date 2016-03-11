package org.orbisgis.wpsservice.controller.execution;

import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.DataDescription;
import org.orbisgis.wpsservice.model.DescriptionType;
import org.orbisgis.wpsservice.model.LiteralData;
import org.orbisgis.wpsservice.model.Output;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Literal data processing
 */
public class LiteralDataProcessing implements DataProcessing {
    @Override
    public void setLocalWpsService(LocalWpsService wpsService) {

    }

    @Override
    public Class<? extends DataDescription> getDataClass() {
        return LiteralData.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, ProcessExecutionListener pel) {
        return new HashMap<>();
    }

    @Override
    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash, ProcessExecutionListener pel) {
        if(inputOrOutput instanceof Output) {
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Literal output : '" +
                        dataMap.get(inputOrOutput.getIdentifier()) + "'");
            }
        }
    }
}
