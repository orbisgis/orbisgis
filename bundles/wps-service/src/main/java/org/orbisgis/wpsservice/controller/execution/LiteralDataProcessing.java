package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps._2_0.*;
import org.orbisgis.wpsservice.LocalWpsService;

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
    public Class<? extends DataDescriptionType> getDataClass() {
        return LiteralDataType.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType input, Map<URI, Object> dataMap, ProcessExecutionListener pel) {
        return new HashMap<>();
    }

    @Override
    public void postProcessData(DescriptionType input, Map<URI, Object> dataMap, Map<URI, Object> stash, ProcessExecutionListener pel) {
        if(input instanceof OutputDescriptionType) {
            if(pel != null) {
                pel.appendLog(ProcessExecutionListener.LogType.INFO, "Literal output : '" +
                        dataMap.get(URI.create(input.getIdentifier().getValue())) + "'");
            }
        }
    }
}
