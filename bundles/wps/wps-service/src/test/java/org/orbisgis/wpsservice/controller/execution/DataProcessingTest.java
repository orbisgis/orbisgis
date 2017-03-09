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
package org.orbisgis.wpsservice.controller.execution;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.model.BoundingBoxData;

import net.opengis.wps._2_0.Format;
import org.orbisgis.wpsservice.model.MalformedScriptException;
import org.orbisgis.wpsservice.model.ObjectFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Sylvain PALOMINOS
 */
public class DataProcessingTest {

    private DataProcessingManager dataProcessingManager = new DataProcessingManager();
    private ObjectFactory objectFactory = new ObjectFactory();
    private WKTWriter wktWriter = new WKTWriter();

    @Test
    public void testBoundingBoxProcessing() throws MalformedScriptException {

        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "EPSG:4326;0,0,1,1");

        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        BoundingBoxData boundingBoxData = new BoundingBoxData(formatList, "EPSG:4326",
                new String[]{"EPSG:4326","EPSG:2000", "EPSG:2001"}, 2);
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));

        dataProcessingManager.preProcessData(input, dataMap, null);

        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of a bounding box should be a geometry.",
                result instanceof Geometry);
        Geometry geometry = (Geometry)result;
        Assert.assertEquals("The bounding box geometry dimension should be 2.",
                2, geometry.getDimension());
        Assert.assertEquals("The bounding box geometry SRID should be 4326.",
                4326, geometry.getSRID());
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))", wktWriter.write(geometry));


        OutputDescriptionType output = new OutputDescriptionType();
        output.setIdentifier(identifier);
        output.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));

        dataProcessingManager.postProcessData(output, dataMap, new HashMap<URI, Object>() ,null);

        Object result2 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the postprocessing of a bounding box should be a String.",
                result2 instanceof String);
        String str = result2.toString();
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                ":4326;0,0,1,1", str);
    }

    @Test
    public void test3DBoundingBoxProcessing() throws MalformedScriptException {

        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "EPSG:4326;0,0,0,1,1,1");

        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        BoundingBoxData boundingBoxData = new BoundingBoxData(formatList, "EPSG:4326",
                new String[]{"EPSG:4326","EPSG:2000", "EPSG:2001"}, 2);
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));

        dataProcessingManager.preProcessData(input, dataMap, null);

        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of a bounding box should be a null.",
                result == null);
    }
}
