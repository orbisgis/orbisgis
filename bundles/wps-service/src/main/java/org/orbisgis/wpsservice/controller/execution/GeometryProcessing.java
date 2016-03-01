package org.orbisgis.wpsservice.controller.execution;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.orbisgis.wpsservice.LocalWpsService;
import org.orbisgis.wpsservice.model.*;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class GeometryProcessing implements DataProcessing {

    private LocalWpsService wpsService;

    @Override
    public void setLocalWpsService(LocalWpsService wpsService) {
        this.wpsService = wpsService;
    }

    @Override
    public Class<? extends DataDescription> getDataClass() {
        return GeometryData.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel) {
        //Check if it is an input
        if(inputOrOutput instanceof Input) {
            Map<URI, Object> map = new HashMap<>();
            Input input = (Input) inputOrOutput;
            //Check if the input is a GeometryData
            if(input.getDataDescription() instanceof GeometryData) {
                GeometryData geometryData = (GeometryData) input.getDataDescription();
                String str = dataMap.get(inputOrOutput.getIdentifier()).toString();
                //Read the string to retrieve the Geometry
                Geometry geometry;
                try {
                    geometry = new WKTReader().read(str);
                } catch (ParseException e) {
                    pel.appendLog(ProcessExecutionListener.LogType.ERROR,"Unable to parse the string '" + str +
                            "' into Geometry.");
                    dataMap.put(inputOrOutput.getIdentifier(), null);
                    return null;
                }
                //Check the Geometry has the good type
                //If the geometry type list is empty, all the type are accepted so sets the flag to true
                boolean flag = geometryData.getGeometryTypeList().isEmpty();
                for(DataType dataType : geometryData.getGeometryTypeList()){
                    if(dataType.name().equalsIgnoreCase(geometry.getGeometryType())){
                        flag = true;
                    }
                }
                if(!flag){
                    pel.appendLog(ProcessExecutionListener.LogType.ERROR,"The geometry '"+input.getTitle()+
                            "' type is not accepted ('"+geometry.getGeometryType()+"' not allowed.");
                    dataMap.put(inputOrOutput.getIdentifier(), null);
                    return null;
                }
                //Check the Geometry has not an excluded type
                flag = true;
                for(DataType dataType : geometryData.getExcludedTypeList()){
                    if(dataType.name().equalsIgnoreCase(geometry.getGeometryType())){
                        flag = false;
                    }
                }
                if(!flag){
                    pel.appendLog(ProcessExecutionListener.LogType.ERROR,"The geometry '"+input.getTitle()+
                            "' type is not accepted ('"+geometry.getGeometryType()+"' not allowed.");
                    dataMap.put(inputOrOutput.getIdentifier(), null);
                    return null;
                }
                //Check the geometry dimension
                if(geometryData.getDimension() != geometry.getDimension()){
                    pel.appendLog(ProcessExecutionListener.LogType.ERROR,"The geometry '"+input.getTitle()+
                            "' has not a wrong dimension (should be '"+geometryData.getDimension()+"'.");
                    dataMap.put(inputOrOutput.getIdentifier(), null);
                    return null;
                }
                dataMap.put(inputOrOutput.getIdentifier(), geometry);
            }
            map.put(inputOrOutput.getIdentifier(), null);
            return map;
        }
        else if(inputOrOutput instanceof Output){
            Map<URI, Object> map = new HashMap<>();
            map.put(inputOrOutput.getIdentifier(), null);
            return map;
        }
        return null;
    }

    @Override
    public void postProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel) {
        //Check if it is an output
        if(inputOrOutput instanceof Output) {
            Output output = (Output) inputOrOutput;
            //Check if the input is a GeometryData
            if(output.getDataDescription() instanceof GeometryData) {
                GeometryData geometryData = (GeometryData) output.getDataDescription();
                Object obj = dataMap.get(inputOrOutput.getIdentifier());
                if(obj instanceof Geometry) {
                    Geometry geometry = (Geometry) obj;
                    //Check the Geometry has the good type
                    //If the geometry type list is empty, all the type are accepted so sets the flag to true
                    boolean flag = geometryData.getGeometryTypeList().isEmpty();
                    for (DataType dataType : geometryData.getGeometryTypeList()) {
                        if (dataType.name().equalsIgnoreCase(geometry.getGeometryType())) {
                            flag = true;
                        }
                    }
                    if (!flag) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR,"The geometry '" + output.getTitle() +
                                "' type is not accepted ('" + geometry.getGeometryType() + "' not allowed.");
                        dataMap.put(inputOrOutput.getIdentifier(), null);
                        return;
                    }
                    //Check the Geometry has not an excluded type
                    flag = true;
                    for (DataType dataType : geometryData.getExcludedTypeList()) {
                        if (dataType.name().equalsIgnoreCase(geometry.getGeometryType())) {
                            flag = false;
                        }
                    }
                    if (!flag) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR,"The geometry '" + output.getTitle() +
                                "' type is not accepted ('" + geometry.getGeometryType() + "' not allowed.");
                        dataMap.put(inputOrOutput.getIdentifier(), null);
                        return;
                    }
                    //Read the string to retrieve the Geometry
                    String wkt = new WKTWriter(geometryData.getDimension()).write(geometry);
                    if(wkt == null || wkt.isEmpty()){
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR,"Unable to read the geometry '" +
                                output.getTitle() + "'.");
                        dataMap.put(inputOrOutput.getIdentifier(), null);
                        return;
                    }
                    dataMap.put(inputOrOutput.getIdentifier(), wkt);

                    pel.appendLog(ProcessExecutionListener.LogType.ERROR,"Output geometry '" +
                            output.getTitle() + "' is '"+wkt+"'.");
                }
            }
        }
    }
}
