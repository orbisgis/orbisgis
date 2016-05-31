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

package org.orbisgis.wpsservice.controller.parser;

import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.attributes.ProcessAttribute;
import org.orbisgis.wpsservice.controller.utils.ObjectAnnotationConverter;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

public class ProcessParser {

    public ProcessOffering parseProcess(List<InputDescriptionType> inputList,
                                        List<OutputDescriptionType> outputList,
                                        Method processingMethod,
                                        URI processURI){
        ProcessDescriptionType process = new ProcessDescriptionType();
        ObjectAnnotationConverter.annotationToObject(processingMethod.getAnnotation(DescriptionTypeAttribute.class),
                process);
        process.getOutput().clear();
        process.getOutput().addAll(outputList);
        process.getInput().clear();
        process.getInput().addAll(inputList);

        if(process.getIdentifier() == null){
            CodeType codeType = new CodeType();
            codeType.setValue(processURI.toString());
            process.setIdentifier(codeType);
        }
        ProcessOffering processOffering = new ProcessOffering();
        processOffering.setProcess(process);
        ObjectAnnotationConverter.annotationToObject(processingMethod.getAnnotation(ProcessAttribute.class),
                processOffering);
        return processOffering;
    }
}
