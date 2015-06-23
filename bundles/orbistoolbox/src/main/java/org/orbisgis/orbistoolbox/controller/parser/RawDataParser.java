/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.controller.parser;

import org.orbisgis.orbistoolbox.model.*;
import org.orbisgis.orbistoolboxapi.annotations.input.RawDataInput;
import org.orbisgis.orbistoolboxapi.annotations.model.*;
import org.orbisgis.orbistoolboxapi.annotations.output.RawDataOutput;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parser dedicated to the RawDataParsing.
 *
 * @author Sylvain PALOMINOS
 **/

public class RawDataParser implements Parser {

    @Override
    public Input parseInput(Field f, String processName) {
        List<Format> formatList = new ArrayList<>();

        RawDataAttribute rawDataAttribute = f.getAnnotation(RawDataAttribute.class);
        for(FormatAttribute formatAttribute : rawDataAttribute.formats()){
            Format format = new Format(formatAttribute.mimeType(), URI.create(formatAttribute.schema()));
            format.setDefaultFormat(formatAttribute.defaultFormat());
            format.setMaximumMegaBytes(formatAttribute.maximumMegaBytes());
            formatList.add(format);
        }

        //Instantiate the RawData
        RawData rawData = new RawData(formatList);
        rawData.setData(f, f.getType());

        //Instantiate the returned output
        Input input = new Input(f.getName(),
                URI.create("orbisgis:wps:"+processName+":input:"+f.getName()),
                rawData);
        InputAttribute wpsInput = f.getAnnotation(InputAttribute.class);
        input.setMinOccurs(0);
        input.setMaxOccurs(wpsInput.maxOccurs());
        input.setMinOccurs(wpsInput.minOccurs());

        //Read the DescriptionTypeAttribute annotation to set the Output non mandatory attributes
        DescriptionTypeAttribute descriptionTypeAttribute = wpsInput.descriptionTypeAttribute();
        if(!descriptionTypeAttribute.title().equals("")){
            input.setTitle(descriptionTypeAttribute.title());
        }
        if(!descriptionTypeAttribute.abstrac().equals("")){
            input.setAbstrac(descriptionTypeAttribute.abstrac());
        }
        if(!descriptionTypeAttribute.identifier().equals("")){
            input.setIdentifier(URI.create(descriptionTypeAttribute.identifier()));
        }
        if(!descriptionTypeAttribute.keywords().equals("")){
            input.setKeywords(Arrays.asList(descriptionTypeAttribute.keywords().split(",")));
        }
        //TODO : implements for metadata.
        if(!descriptionTypeAttribute.metadata().equals("")){
            input.setMetadata(null);
        }

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processName) {
        List<Format> formatList = new ArrayList<>();

        RawDataAttribute rawDataAttribute = f.getAnnotation(RawDataAttribute.class);
        for(FormatAttribute formatAttribute : rawDataAttribute.formats()){
            Format format = new Format(formatAttribute.mimeType(), URI.create(formatAttribute.schema()));
            format.setDefaultFormat(formatAttribute.defaultFormat());
            format.setMaximumMegaBytes(formatAttribute.maximumMegaBytes());
            formatList.add(format);
        }

        //Instantiate the RawData
        RawData rawData = new RawData(formatList);
        rawData.setData(f, f.getType());

        //Instantiate the returned output
        Output output = new Output(f.getName(),
                URI.create("orbisgis:wps:"+processName+":output:"+f.getName()),
                rawData);
        OutputAttribute wpsOutput = f.getAnnotation(OutputAttribute.class);

        //Read the DescriptionTypeAttribute annotation to set the Output non mandatory attributes
        DescriptionTypeAttribute descriptionTypeAttribute = wpsOutput.descriptionTypeAttribute();
        if(!descriptionTypeAttribute.title().equals("")){
            output.setTitle(descriptionTypeAttribute.title());
        }
        if(!descriptionTypeAttribute.abstrac().equals("")){
            output.setAbstrac(descriptionTypeAttribute.abstrac());
        }
        if(!descriptionTypeAttribute.identifier().equals("")){
            output.setIdentifier(URI.create(descriptionTypeAttribute.identifier()));
        }
        if(!descriptionTypeAttribute.keywords().equals("")){
            output.setKeywords(Arrays.asList(descriptionTypeAttribute.keywords().split(",")));
        }
        //TODO : implements for metadata.
        if(!descriptionTypeAttribute.metadata().equals("")){
            output.setMetadata(null);
        }

        return output;
    }

    @Override
    public Class<? extends Annotation> getAnnotationInput() {
        return RawDataInput.class;
    }

    @Override
    public Class<? extends Annotation> getAnnotationOutput() {
        return RawDataOutput.class;
    }
}
