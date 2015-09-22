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

package org.orbisgis.orbistoolbox.controller.processexecution.dataprocessing;

import groovy.lang.GroovyObject;
import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolbox.model.Process;
import org.orbisgis.orbistoolbox.view.ToolBox;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.OutputAttribute;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Manager for the processing tools which pre and post process the input and output to well form the data.
 *
 * @author Sylvain PALOMINOS
 **/

public class ProcessingManager {
    /** ToolBox*/
    ToolBox toolBox;
    /** Map of the ProcessingData and the corresponding data class. */
    private Map<Class, ProcessingData> classProcessingMap;

    /**
     * Main constructor.
     */
    public ProcessingManager(ToolBox toolBox){
        this.toolBox = toolBox;
        classProcessingMap = new HashMap<>();
        classProcessingMap.put(GeoData.class, new GeoDataProcessing());
    }

    /**
     * Returns the ProcessingData class corresponding to the given class.
     * @param clazz Class to analyse.
     * @return ProcessingData corresponding to the given class.
     */
    private ProcessingData getProcessingData(Class<? extends DataDescription> clazz){
        ProcessingData processingData = classProcessingMap.get(clazz);
        if(processingData != null) {
            return classProcessingMap.get(clazz);
        }
        return null;
    }

    /**
     * PreProcess the data of the given process.
     * @param process Process to preProcess.
     * @param dataMap Data map of the input and output.
     */
    public void preProcessData(Process process,
                               Map<URI, Object> dataMap){
        ProcessingData prossData;
        for(Input input : process.getInput()){
            if((prossData = getProcessingData(input.getDataDescription().getClass())) != null) {
                prossData.preProcessing(input, dataMap, toolBox);
            }
        }
        for(Output output : process.getOutput()){
            if((prossData = getProcessingData(output.getDataDescription().getClass())) != null) {
                prossData.preProcessing(output, dataMap, toolBox);
            }
        }
    }

    /**
     * PostProcess the data of the given process.
     * @param process Process to postProcess.
     * @param dataMap Data map of the input and output.
     * @param groovyObject GroovyObject containing the process result.
     */
    public void postProcessData(Process process,
                                Map<URI, Object> dataMap,
                                GroovyObject groovyObject){
        for (Field field : groovyObject.getClass().getDeclaredFields()) {
            if (field.getAnnotation(OutputAttribute.class) != null) {
                field.setAccessible(true);
                DescriptionTypeAttribute annot = field.getAnnotation(DescriptionTypeAttribute.class);
                URI uri;
                if(annot != null && !annot.identifier().isEmpty()) {
                    uri = URI.create(annot.identifier());
                }
                else{
                    uri = URI.create(process.getIdentifier() + ":output:" + field.getName());
                }
                try {
                    dataMap.put(uri, field.get(groovyObject));
                } catch (IllegalAccessException e) {
                    dataMap.put(uri, null);
                    LoggerFactory.getLogger(ProcessingManager.class).error(e.getMessage());
                }
            }
        }
        ProcessingData prossData;
        for(Input input : process.getInput()){
            if((prossData = getProcessingData(input.getDataDescription().getClass())) != null) {
                prossData.postProcessing(input, dataMap, toolBox);
            }
        }
        for(Output output : process.getOutput()){
            if((prossData = getProcessingData(output.getDataDescription().getClass())) != null) {
                prossData.postProcessing(output, dataMap, toolBox);
            }
        }
    }
}
