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

import org.orbisgis.orbistoolbox.model.ShapeFileData;
import org.orbisgis.orbistoolbox.model.Input;
import org.orbisgis.orbistoolbox.model.MalformedScriptException;
import org.orbisgis.orbistoolbox.model.Output;
import org.orbisgis.orbistoolboxapi.annotations.model.DescriptionTypeAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.InputAttribute;
import org.orbisgis.orbistoolboxapi.annotations.model.ShapeFileAttribute;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * Parser dedicated to the RawDataParsing.
 *
 * @author Sylvain PALOMINOS
 **/

public class ShapeFileParser implements Parser {

    @Override
    public Input parseInput(Field f, String processName) {
        //Instantiate the RawData
        ShapeFileData shapeFile = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(ShapeFileAttribute.class));

        Input input;
        try {
            //Instantiate the returned input
            input = new Input(f.getName(),
                    URI.create("orbisgis:wps:"+processName+":input:"+f.getName()),
                    shapeFile);
        } catch (MalformedScriptException e) {
            e.printStackTrace();
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(InputAttribute.class), input);
        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), input);

        return input;
    }

    @Override
    public Output parseOutput(Field f, String processName) {
        //Instantiate the RawData
        ShapeFileData shapeFile = ObjectAnnotationConverter.annotationToObject(f.getAnnotation(ShapeFileAttribute.class));

        Output output;
        try {
            //Instantiate the returned output
            output = new Output(f.getName(),
                    URI.create("orbisgis:wps:"+processName+":output:"+f.getName()),
                    shapeFile);
        } catch (MalformedScriptException e) {
            e.printStackTrace();
            return null;
        }

        ObjectAnnotationConverter.annotationToObject(f.getAnnotation(DescriptionTypeAttribute.class), output);

        return output;
    }

    @Override
    public Class getAnnotation() {
        return ShapeFileAttribute.class;
    }
}
