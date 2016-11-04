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
 * Copyright (C) 2015-2016 CNRS (Lab-STICC UMR CNRS 6285)
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
package org.orbisgis.wpsservice.controller.execution;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.DescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.orbisgis.wpsservice.model.DataType;
import org.orbisgis.wpsservice.model.GeometryData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sylvain PALOMINOS
 */
public class GeometryProcessing implements DataProcessing {

    /**Logger */
    private Logger LOGGER = LoggerFactory.getLogger(GeometryProcessing.class);
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(GeometryProcessing.class);

    @Override
    public Class<? extends DataDescriptionType> getDataClass() {
        return GeometryData.class;
    }

    @Override
    public Map<URI, Object> preProcessData(DescriptionType inputOrOutput, Map<URI, Object> dataMap,
                                           ProcessExecutionListener pel) {
        //Check if it is an input
        if(inputOrOutput instanceof InputDescriptionType) {
            Map<URI, Object> map = new HashMap<>();
            InputDescriptionType input = (InputDescriptionType) inputOrOutput;
            //Check if the input is a GeometryData
            if(input.getDataDescription().getValue() instanceof GeometryData) {
                GeometryData geometryData = (GeometryData) input.getDataDescription().getValue();
                String str = dataMap.get(URI.create(inputOrOutput.getIdentifier().getValue())).toString();
                //Read the string to retrieve the Geometry
                Geometry geometry;
                try {
                    geometry = new WKTReader().read(str);
                } catch (ParseException e) {
                    if(pel != null) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("Unable to parse the string {0}" +
                                " into Geometry.", str));
                    }
                    else{
                        LOGGER.error("Unable to parse the string {0} into Geometry.", str);
                    }
                    dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
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
                    if(pel != null) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("The geometry {0} type is not" +
                                " accepted ({1} not allowed).", input.getTitle(), geometry.getGeometryType()));
                    }
                    else{
                        LOGGER.error(I18N.tr("The geometry {0} type is not accepted ({1} not allowed).",
                                input.getTitle(), geometry.getGeometryType()));
                    }
                    dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
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
                    if(pel != null) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("The geometry {0} type is not" +
                                " accepted ({1} not allowed).", input.getTitle(), geometry.getGeometryType()));
                    }
                    else{
                        LOGGER.error(I18N.tr("The geometry {0} type is not accepted ({1} not allowed).",
                                input.getTitle(), geometry.getGeometryType()));
                    }
                    dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
                    return null;
                }
                //Check the geometry dimension
                if((geometryData.getDimension() == 2 && !Double.isNaN(geometry.getCoordinate().z)) ||
                        (geometryData.getDimension() == 3 && Double.isNaN(geometry.getCoordinate().z))){
                    if(pel != null) {
                        pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("The geometry {0} has not a " +
                        "wrong dimension (should be {1}).", input.getTitle(), geometryData.getDimension()));
                    }
                    else{

                        LOGGER.error(I18N.tr("The geometry {0} has not a wrong dimension (should be {1}).",
                                input.getTitle(), geometryData.getDimension()));
                    }
                    dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
                    return null;
                }
                dataMap.put(URI.create(inputOrOutput.getIdentifier().getValue()), geometry);
            }
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
            return map;
        }
        else if(inputOrOutput instanceof OutputDescriptionType){
            Map<URI, Object> map = new HashMap<>();
            map.put(URI.create(inputOrOutput.getIdentifier().getValue()), null);
            return map;
        }
        return null;
    }

    @Override
    public void postProcessData(DescriptionType input, Map<URI, Object> dataMap, Map<URI, Object> stash,
                                ProcessExecutionListener pel) {
        //Check if it is an output
        if(input instanceof OutputDescriptionType) {
            OutputDescriptionType output = (OutputDescriptionType) input;
            //Check if the input is a GeometryData
            if(output.getDataDescription().getValue() instanceof GeometryData) {
                GeometryData geometryData = (GeometryData) output.getDataDescription().getValue();
                Object obj = dataMap.get(URI.create(input.getIdentifier().getValue()));
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
                        if(pel != null) {
                            pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("The geometry {0} type is not" +
                                    " accepted ({1} not allowed).", input.getTitle(), geometry.getGeometryType()));
                        }
                        else{
                            LOGGER.error(I18N.tr("The geometry {0} type is not accepted ({1} not allowed).",
                                    input.getTitle(), geometry.getGeometryType()));
                        }
                        dataMap.put(URI.create(input.getIdentifier().getValue()), null);
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
                        if(pel != null) {
                            pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("The geometry {0} type is not" +
                                    " accepted ({1} not allowed).", input.getTitle(), geometry.getGeometryType()));
                        }
                        else{
                            LOGGER.error(I18N.tr("The geometry {0} type is not accepted ({1} not allowed).",
                                    input.getTitle(), geometry.getGeometryType()));
                        }
                        dataMap.put(URI.create(input.getIdentifier().getValue()), null);
                        return;
                    }
                    //Read the string to retrieve the Geometry
                    String wkt = new WKTWriter(geometryData.getDimension()).write(geometry);
                    if(wkt == null || wkt.isEmpty()){
                        if(pel != null) {
                            pel.appendLog(ProcessExecutionListener.LogType.ERROR, I18N.tr("Unable to read the geometry" +
                                    " {0}.", output.getTitle()));
                        }
                        else{
                            LOGGER.error(I18N.tr("Unable to read the geometry {0}.", output.getTitle()));
                        }
                        dataMap.put(URI.create(input.getIdentifier().getValue()), null);
                        return;
                    }
                    dataMap.put(URI.create(input.getIdentifier().getValue()), wkt);

                    if(pel != null) {
                        pel.appendLog(ProcessExecutionListener.LogType.INFO, I18N.tr("Output geometry {0} is {1}.",
                                output.getTitle(), wkt));
                    }
                }
            }
        }
    }
}
