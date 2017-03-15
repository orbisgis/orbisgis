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
import com.vividsolutions.jts.io.WKTWriter;
import net.opengis.ows._2.CodeType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsservice.controller.utils.FormatFactory;
import org.orbisgis.wpsservice.model.*;

import net.opengis.wps._2_0.Format;

import java.net.URI;
import java.util.*;

/**
 * This test class aim is to check if the DataProcessing implementation registered in the DataProcessingManager
 * works well
 *
 * @author Sylvain PALOMINOS
 */
public class DataProcessingTest {

    /** Object containing the DataProcessing classes **/
    private DataProcessingManager dataProcessingManager = new DataProcessingManager();
    /** Object factory used to generate JAXBElement from the classes extending the ComplexDataType class. */
    private ObjectFactory objectFactory = new ObjectFactory();
    /** WKT writer. */
    private WKTWriter wktWriter = new WKTWriter();

    /**
     * Test the preprocessing of an incoming BoundingBoxData and if the geometry generated is correct.
     * Then check its postprocessing and if its string representation is correct.
     */
    @Test
    public void testBoundingBoxProcessing() {
        //Store into the dataMap the boundingBox data
        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "EPSG:4326;0,0,1,1");
        //Generate the InputDescriptionType object containing the boundingBox
        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        BoundingBoxData boundingBoxData = null;
        try {
            boundingBoxData = new BoundingBoxData(formatList, "EPSG:4326",
                    new String[]{"EPSG:4326","EPSG:2000", "EPSG:2001"}, 2);
        } catch (MalformedScriptException ignored) {}
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));
        //Preprocess the bounding box
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Check if the resulting Geometry object is the one expected
        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the bounding box should be a geometry.",
                result instanceof Geometry);
        Geometry geometry = (Geometry)result;
        Assert.assertEquals("The bounding box geometry dimension should be 2.",
                2, geometry.getDimension());
        Assert.assertEquals("The bounding box geometry SRID should be 4326.",
                4326, geometry.getSRID());
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))", wktWriter.write(geometry));

        //Test a bounding box with the SRID and the coordinate in the reverse order
        dataMap.put(uri, "0,0,1,1;EPSG:4326");
        //Preprocess the bounding box
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Check if the resulting Geometry object is the one expected
        Object result2 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the bounding box should be a geometry.",
                result2 instanceof Geometry);
        Geometry geometry2 = (Geometry)result;
        Assert.assertEquals("The bounding box geometry dimension should be 2.",
                2, geometry2.getDimension());
        Assert.assertEquals("The bounding box geometry SRID should be 4326.",
                4326, geometry2.getSRID());
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))", wktWriter.write(geometry2));


        //Generate the OutputDescriptionType object to postprocess
        OutputDescriptionType output = new OutputDescriptionType();
        output.setIdentifier(identifier);
        output.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));
        //Postprocess the output
        dataProcessingManager.postProcessData(output, dataMap, new HashMap<URI, Object>() ,null);
        //Check if the resulting object is a string containing the representation of the bounding box
        Object result3 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the postprocessing of the bounding box should be a String.",
                result3 instanceof String);
        String str = result3.toString();
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                ":4326;0,0,1,1", str);
    }

    /**
     * Test the preprocessing of a 3D bounding box (now not supported). It should return a null value.
     */
    @Test
    public void test3DBoundingBoxProcessing() {
        //Store into the dataMap the boundingBox data
        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "EPSG:4326;0,0,0,1,1,1");
        //Generate the InputDescriptionType object containing the boundingBox
        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        BoundingBoxData boundingBoxData = null;
        try {
            boundingBoxData = new BoundingBoxData(formatList, "EPSG:4326",
                    new String[]{"EPSG:4326","EPSG:2000", "EPSG:2001"}, 2);
        } catch (MalformedScriptException ignored) {}
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createBoundingBoxData(boundingBoxData));
        //Preprocess the bounding box
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Check if the resulting object is null
        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the bounding box should be null.",
                result == null);
    }

    /**
     * Test the preprocessing of an incoming GeometryData and if the geometry generated is correct.
     * Then check its postprocessing and if its string representation is correct.
     */
    @Test
    public void testGeometryProcessing() {
        //Store into the dataMap the geometryData data
        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))");
        //Generate the InputDescriptionType object containing the GeometryData
        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        List<DataType> geometryList = new ArrayList<>();
        geometryList.add(DataType.POLYGON);
        GeometryData geometryData = null;
        try {
            geometryData = new GeometryData(formatList, geometryList);
        } catch (MalformedScriptException ignored) {}
        geometryData.setDimension(2);
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createGeometryData(geometryData));
        //Preprocess the GeometryData
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Test if the result object is the expected geometry
        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the geometry data should be a geometry.",
                result instanceof Geometry);
        Geometry geometry = (Geometry)result;
        Assert.assertEquals("The geometry dimension should be 2.",
                2, geometry.getDimension());
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))", wktWriter.write(geometry));

        //Generate the OutputDescriptionType object to postprocess
        OutputDescriptionType output = new OutputDescriptionType();
        output.setIdentifier(identifier);
        output.setDataDescription(objectFactory.createGeometryData(geometryData));
        //Postprocess the output
        dataProcessingManager.postProcessData(output, dataMap, new HashMap<URI, Object>() ,null);
        //Test if the result object is a string containing the representation of the GeometryData
        Object result2 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the postprocessing of the bounding box should be a String.",
                result2 instanceof String);
        String str = result2.toString();
        Assert.assertEquals("The bounding box geometry wasn't the one expected.",
                "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))", str);
    }

    /**
     * Test the preprocessing in the different cases of wrong GeometryData configuration (bad geometry, bad dimension ...)
     */
    @Test
    public void testBadGeometryProcessing() {
        //Store into the dataMap the geometryData data
        URI uri = URI.create(UUID.randomUUID().toString());
        Map<URI, Object> dataMap = new HashMap<>();
        dataMap.put(uri, "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0");
        //Generate the InputDescriptionType object containing the GeometryData
        List<Format> formatList = FormatFactory.getFormatsFromExtensions(new String[]{".txt"});
        List<DataType> geometryList = new ArrayList<>();
        geometryList.add(DataType.BOOLEAN);
        GeometryData geometryData = null;
        List<DataType> excludedList = new ArrayList<>();
        excludedList.add(DataType.POLYGON);
        try {
            geometryData = new GeometryData(formatList, geometryList);
            geometryData.setExcludedTypeList(excludedList);
        } catch (MalformedScriptException e) {}
        geometryData.setDimension(2);
        CodeType identifier = new CodeType();
        identifier.setValue(uri.toString());
        InputDescriptionType input = new InputDescriptionType();
        input.setIdentifier(identifier);
        input.setDataDescription(objectFactory.createGeometryData(geometryData));

        //Test the preprocessing with a malformed WKT
        //Preprocess the GeometryData
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Test if the result object is the expected geometry
        Object result = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the Geometry should be null.",
                result == null);


        //Test the preprocessing with a geometry which is not allowed (not in the list of authorized DataType)
        dataMap.put(uri, "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))");
        //Preprocess the GeometryData
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Test if the result object is the expected geometry
        Object result2 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the Geometry should be null.",
                result2 == null);


        //Test the preprocessing with a geometry which is excluded (in the list of excluded DataType)
        dataMap.put(uri, "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))");
        geometryList.clear();
        //Preprocess the GeometryData
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Test if the result object is the expected geometry
        Object result3 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the Geometry should be null.",
                result3 == null);


        //Test the preprocessing with a geometry with a bad dimension
        dataMap.put(uri, "POLYGON ((0 0, 1 0, 1 1, 0 1, 0 0))");
        excludedList.clear();
        geometryData.setDimension(3);
        //Preprocess the GeometryData
        dataProcessingManager.preProcessData(input, dataMap, null);
        //Test if the result object is the expected geometry
        Object result4 = dataMap.get(uri);
        Assert.assertTrue("The object resulting of the preprocessing of the Geometry should be null.",
                result4 == null);
    }
}
