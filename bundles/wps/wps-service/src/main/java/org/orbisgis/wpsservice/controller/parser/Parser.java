/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */

package org.orbisgis.wpsservice.controller.parser;

import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsservice.model.MalformedScriptException;

import java.lang.reflect.Field;
import java.net.URI;

/**
 * Interface to define a Parser associated to a data from the model (i.e. LiteralData or RawData).
 * A parse have to be associated to an input and an output groovy annotation used in the script.
 * It have to be also associated to a Data class from the model.
 *
 * @author Sylvain PALOMINOS
 **/

public interface Parser {

    /**
     * Parse the given field as an input and returns the corresponding DataDescription.
     * @param f Field to parse.
     * @param processId The process identifier.
     * @return Parsed DataDescription.
     */
    InputDescriptionType parseInput(Field f, Object defaultValue, URI processId) throws MalformedScriptException;

    /**
     * Parse the given field as an output and returns the corresponding DataDescription.
     * @param f Field to parse.
     * @param processId The process identifier.
     * @return Parsed DataDescription.
     */
    OutputDescriptionType parseOutput(Field f, Object defaultValue, URI processId) throws MalformedScriptException;

    /**
     * Returns the groovy annotation associated to this parser.
     * @return The grovvy annotation associated to this parse.
     */
    Class getAnnotation();

}
