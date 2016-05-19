package org.orbisgis.wpsservice.controller.execution;

import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.RawData;

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
    public Class<? extends DataDescriptionType> getDataClass() {
        return RawData.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType input, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel) {
        Map<URI, Object> map = new HashMap<>();
        map.put(URI.create(input.getIdentifier().getValue()), null);
        return map;
    }

    @Override
    public void postProcessData(DescriptionType input, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel) {
        //Check if it is an output
        if(input instanceof OutputDescriptionType) {
            OutputDescriptionType output = (OutputDescriptionType) input;
            //Check if the input is a GeometryData
            if(output.getDataDescription().getValue() instanceof RawData) {
                if(pel != null) {
                    pel.appendLog(ProcessExecutionListener.LogType.INFO, "Output RawData '" +
                            output.getTitle() + "' is '" + dataMap.get(URI.create(output.getIdentifier().getValue())) + "'.");
                }
            }
        }
    }
}
