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

import net.opengis.wps._2_0.DataDescriptionType;
import net.opengis.wps._2_0.InputDescriptionType;
import net.opengis.wps._2_0.OutputDescriptionType;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.BoundingBoxAttribute;
import org.orbisgis.wpsgroovyapi.attributes.DescriptionTypeAttribute;
import org.orbisgis.wpsgroovyapi.attributes.InputAttribute;
import org.orbisgis.wpsgroovyapi.attributes.OutputAttribute;
import org.orbisgis.wpsservice.model.BoundingBoxData;
import org.orbisgis.wpsservice.model.MalformedScriptException;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.URI;
import java.util.UUID;

/**
 * @author Sylvain PALOMINOS
 */
public class BoundingBoxParserTest {

    /**BoundingBox parser class */
    private BoundingBoxParser boundingBoxParser = new BoundingBoxParser();

    /**
     * Tests the parsing of the simplest BoundingBox input.
     */
    @Test
    public void testSimplestParseInput(){
        Field field = null;
        try {
            field = FieldProvider.class.getDeclaredField("simplestBoundingBoxInput");
        } catch (NoSuchFieldException ignored) {}
        Assert.assertNotNull("Unable to get the field to parse (field 'simplestBoundingBoxInput' from class FieldProvider).",
                field);
        InputDescriptionType inputDescriptionType = null;
        String processId = UUID.randomUUID().toString();
        try {
            inputDescriptionType = boundingBoxParser.parseInput(field, "EPSG:4326", URI.create(processId));
        } catch (MalformedScriptException ignored) {}
        Assert.assertNotNull("Unable to parse the field 'simplestBoundingBoxInput'.", inputDescriptionType);

        //Tests the DataDescription from the InputDescriptionType
        Assert.assertNotNull("The JAXBElement from the InputDescriptionType should no be null",
                inputDescriptionType.getDataDescription());
        DataDescriptionType dataDescriptionType = inputDescriptionType.getDataDescription().getValue();
        Assert.assertNotNull("The DataDescription from the InputDescriptionType should no be null",
                dataDescriptionType);
        Assert.assertTrue("The DataDescriptionType from the InputDescriptionType should be an instance of " +
                "BoundingBoxData.", dataDescriptionType instanceof BoundingBoxData);
        BoundingBoxData boundingBoxData = (BoundingBoxData) dataDescriptionType;
        Assert.assertEquals("The BoundingBoxData defaultCrs attribute should be 'EPSG:4326.", "EPSG:4326",
                boundingBoxData.getDefaultCrs());
        Assert.assertArrayEquals("The BoundingBoxData supportedCrs attribute should be empty.", new String[]{},
                boundingBoxData.getSupportedCrs());
        Assert.assertEquals("The BoundingBoxData dimension attribute should be 2.", 2, boundingBoxData.getDimension());
        Assert.assertEquals("The BoundingBoxData defaultValue attribute is not the one expected.", null,
                boundingBoxData.getDefaultValue());

        //Tests the InputAttribute part of the InputDescriptionType
        Assert.assertEquals("The InputDescriptionType maxOccurs attribute should be 1", "1",
                inputDescriptionType.getMaxOccurs());
        Assert.assertEquals("The InputDescriptionType minOccurs attribute should be 1", new BigInteger("1"),
                inputDescriptionType.getMinOccurs());

        //Tests the DescriptionTypeAttribute part of the InputDescriptionType
        Assert.assertFalse("The InputDescriptionType title attribute should not be empty",
                inputDescriptionType.getTitle().isEmpty());
        Assert.assertEquals("The InputDescriptionType title should be 'title'.", "title",
                inputDescriptionType.getTitle().get(0).getValue());
        Assert.assertTrue("The InputDescriptionType description attribute should be empty",
                inputDescriptionType.getAbstract().isEmpty());
        Assert.assertTrue("The InputDescriptionType keywords attribute should be empty",
                inputDescriptionType.getKeywords().isEmpty());
        Assert.assertEquals("The InputDescriptionType identifier is incorrect.", processId+":"+field.getName(),
                inputDescriptionType.getIdentifier().getValue());
        Assert.assertTrue("The InputDescriptionType metadata attribute should be empty",
                inputDescriptionType.getMetadata().isEmpty());
    }

    /**
     * Tests the parsing of a complex BoundingBox input.
     */
    @Test
    public void testComplexParseInput(){
        Field field = null;
        try {
            field = FieldProvider.class.getDeclaredField("complexBoundingBoxInput");
        } catch (NoSuchFieldException ignored) {}
        Assert.assertNotNull("Unable to get the field to parse (field 'complexBoundingBoxInput' from class FieldProvider).",
                field);
        InputDescriptionType inputDescriptionType = null;
        String processId = UUID.randomUUID().toString();
        try {
            inputDescriptionType = boundingBoxParser.parseInput(field, "EPSG:4326;0,0,1,1", URI.create(processId));
        } catch (MalformedScriptException ignored) {}
        Assert.assertNotNull("Unable to parse the field 'complexBoundingBoxInput'.", inputDescriptionType);

        //Tests the DataDescription from the InputDescriptionType
        Assert.assertNotNull("The JAXBElement from the InputDescriptionType should no be null",
                inputDescriptionType.getDataDescription());
        DataDescriptionType dataDescriptionType = inputDescriptionType.getDataDescription().getValue();
        Assert.assertNotNull("The DataDescription from the InputDescriptionType should no be null",
                dataDescriptionType);
        Assert.assertTrue("The DataDescriptionType from the InputDescriptionType should be an instance of " +
                "BoundingBoxData.", dataDescriptionType instanceof BoundingBoxData);
        BoundingBoxData boundingBoxData = (BoundingBoxData) dataDescriptionType;
        Assert.assertEquals("The BoundingBoxData defaultCrs attribute should be 'EPSG:4326.", "EPSG:4326",
                boundingBoxData.getDefaultCrs());
        Assert.assertArrayEquals("The BoundingBoxData supportedCrs attribute should be empty.",
                new String[]{"EPSG:4326", "EPSG:2000", "EPSG:2001"}, boundingBoxData.getSupportedCrs());
        Assert.assertEquals("The BoundingBoxData dimension attribute should be 2.", 2, boundingBoxData.getDimension());
        Assert.assertEquals("The BoundingBoxData defaultValue attribute is not the one expected.", "0,0,1,1",
                boundingBoxData.getDefaultValue());

        //Tests the InputAttribute part of the InputDescriptionType
        Assert.assertEquals("The InputDescriptionType maxOccurs attribute should be 1", "2",
                inputDescriptionType.getMaxOccurs());
        Assert.assertEquals("The InputDescriptionType minOccurs attribute should be 1", new BigInteger("0"),
                inputDescriptionType.getMinOccurs());

        //Tests the DescriptionTypeAttribute part of the InputDescriptionType
        Assert.assertEquals("The InputDescriptionType title attribute should have a size of 2", 2,
                inputDescriptionType.getTitle().size());
        Assert.assertEquals("The InputDescriptionType first title value is not the one expected", "title",
                inputDescriptionType.getTitle().get(0).getValue());
        Assert.assertEquals("The InputDescriptionType first title language is not the one expected", "en",
                inputDescriptionType.getTitle().get(0).getLang());
        Assert.assertEquals("The InputDescriptionType second title value is not the one expected", "titre",
                inputDescriptionType.getTitle().get(1).getValue());
        Assert.assertEquals("The InputDescriptionType second title language is not the one expected", "fr",
                inputDescriptionType.getTitle().get(1).getLang());

        Assert.assertEquals("The InputDescriptionType description attribute should have a size of 2", 2,
                inputDescriptionType.getAbstract().size());
        Assert.assertEquals("The InputDescriptionType first abstract value is not the one expected", "description",
                inputDescriptionType.getAbstract().get(0).getValue());
        Assert.assertEquals("The InputDescriptionType first abstract language is not the one expected", "en",
                inputDescriptionType.getAbstract().get(0).getLang());
        Assert.assertEquals("The InputDescriptionType second abstract value is not the one expected", "description",
                inputDescriptionType.getAbstract().get(1).getValue());
        Assert.assertEquals("The InputDescriptionType second abstract language is not the one expected", "fr",
                inputDescriptionType.getAbstract().get(1).getLang());

        Assert.assertEquals("The InputDescriptionType keywords attribute should have a size of 1", 1,
                inputDescriptionType.getKeywords().size());
        Assert.assertEquals("The InputDescriptionType first keywordList should have a size of 2", 2,
                inputDescriptionType.getKeywords().get(0).getKeyword().size());
        Assert.assertEquals("The value of the first keyword of the first keywordList is not the one expected",
                "keyword", inputDescriptionType.getKeywords().get(0).getKeyword().get(0).getValue());
        Assert.assertEquals("The language of the first keyword of the first keywordList is not the one expected",
                "en", inputDescriptionType.getKeywords().get(0).getKeyword().get(0).getLang());
        Assert.assertEquals("The value of the first keyword of the first keywordList is not the one expected",
                "motclef", inputDescriptionType.getKeywords().get(0).getKeyword().get(1).getValue());
        Assert.assertEquals("The language of the first keyword of the first keywordList is not the one expected",
                "fr", inputDescriptionType.getKeywords().get(0).getKeyword().get(1).getLang());

        Assert.assertEquals("The InputDesciriptionType identifier is incorrect.", processId+":"+"identifier",
                inputDescriptionType.getIdentifier().getValue());

        Assert.assertEquals("The InputDescriptionType metadata attribute size should be 1", 1,
                inputDescriptionType.getMetadata().size());
        Assert.assertEquals("The role of the first metadata is not the one expected", "role",
                inputDescriptionType.getMetadata().get(0).getRole());
        Assert.assertEquals("The title of the first metadata is not the one expected", "title",
                inputDescriptionType.getMetadata().get(0).getTitle());
    }



    /**
     * Tests the parsing of the simplest BoundingBox output.
     */
    @Test
    public void testSimplestParseOutput(){
        Field field = null;
        try {
            field = FieldProvider.class.getDeclaredField("simplestBoundingBoxOutput");
        } catch (NoSuchFieldException ignored) {}
        Assert.assertNotNull("Unable to get the field to parse (field 'simplestBoundingBoxOutput' from class FieldProvider).",
                field);
        OutputDescriptionType outputDescriptionType = null;
        String processId = UUID.randomUUID().toString();
        try {
            outputDescriptionType = boundingBoxParser.parseOutput(field, "EPSG:4326", URI.create(processId));
        } catch (MalformedScriptException ignored) {}
        Assert.assertNotNull("Unable to parse the field 'simplestBoundingBoxOutput'.", outputDescriptionType);

        //Tests the DataDescription from the OutputDescriptionType
        Assert.assertNotNull("The JAXBElement from the OutputDescriptionType should no be null",
                outputDescriptionType.getDataDescription());
        DataDescriptionType dataDescriptionType = outputDescriptionType.getDataDescription().getValue();
        Assert.assertNotNull("The DataDescription from the OutputDescriptionType should no be null",
                dataDescriptionType);
        Assert.assertTrue("The DataDescriptionType from the OutputDescriptionType should be an instance of " +
                "BoundingBoxData.", dataDescriptionType instanceof BoundingBoxData);
        BoundingBoxData boundingBoxData = (BoundingBoxData) dataDescriptionType;
        Assert.assertEquals("The BoundingBoxData defaultCrs attribute should be 'EPSG:4326.", "EPSG:4326",
                boundingBoxData.getDefaultCrs());
        Assert.assertArrayEquals("The BoundingBoxData supportedCrs attribute should be empty.", new String[]{},
                boundingBoxData.getSupportedCrs());
        Assert.assertEquals("The BoundingBoxData dimension attribute should be 2.", 2, boundingBoxData.getDimension());

        //Tests the DescriptionTypeAttribute part of the OutputDescriptionType
        Assert.assertFalse("The OutputDescriptionType title attribute should not be empty",
                outputDescriptionType.getTitle().isEmpty());
        Assert.assertEquals("The OutputDescriptionType title should be 'title'.", "title",
                outputDescriptionType.getTitle().get(0).getValue());
        Assert.assertTrue("The OutputDescriptionType description attribute should be empty",
                outputDescriptionType.getAbstract().isEmpty());
        Assert.assertTrue("The OutputDescriptionType keywords attribute should be empty",
                outputDescriptionType.getKeywords().isEmpty());
        Assert.assertEquals("The OutputDescriptionType identifier is incorrect.", processId+":"+field.getName(),
                outputDescriptionType.getIdentifier().getValue());
        Assert.assertTrue("The OutputDescriptionType metadata attribute should be empty",
                outputDescriptionType.getMetadata().isEmpty());
    }

    /**
     * Tests the parsing of a complex BoundingBox output.
     */
    @Test
    public void testComplexParseOutput(){
        Field field = null;
        try {
            field = FieldProvider.class.getDeclaredField("complexBoundingBoxInput");
        } catch (NoSuchFieldException ignored) {}
        Assert.assertNotNull("Unable to get the field to parse (field 'complexBoundingBoxInput' from class FieldProvider).",
                field);
        OutputDescriptionType outputDescriptionType = null;
        String processId = UUID.randomUUID().toString();
        try {
            outputDescriptionType = boundingBoxParser.parseOutput(field, "EPSG:4326", URI.create(processId));
        } catch (MalformedScriptException ignored) {}
        Assert.assertNotNull("Unable to parse the field 'complexBoundingBoxInput'.", outputDescriptionType);

        //Tests the DataDescription from the OutputDescriptionType
        Assert.assertNotNull("The JAXBElement from the OutputDescriptionType should no be null",
                outputDescriptionType.getDataDescription());
        DataDescriptionType dataDescriptionType = outputDescriptionType.getDataDescription().getValue();
        Assert.assertNotNull("The DataDescription from the OutputDescriptionType should no be null",
                dataDescriptionType);
        Assert.assertTrue("The DataDescriptionType from the OutputDescriptionType should be an instance of " +
                "BoundingBoxData.", dataDescriptionType instanceof BoundingBoxData);
        BoundingBoxData boundingBoxData = (BoundingBoxData) dataDescriptionType;
        Assert.assertEquals("The BoundingBoxData defaultCrs attribute should be 'EPSG:4326.", "EPSG:4326",
                boundingBoxData.getDefaultCrs());
        Assert.assertArrayEquals("The BoundingBoxData supportedCrs attribute should be empty.",
                new String[]{"EPSG:4326", "EPSG:2000", "EPSG:2001"}, boundingBoxData.getSupportedCrs());
        Assert.assertEquals("The BoundingBoxData dimension attribute should be 2.", 2, boundingBoxData.getDimension());

        //Tests the DescriptionTypeAttribute part of the OutputDescriptionType
        Assert.assertEquals("The OutputDescriptionType title attribute should have a size of 2", 2,
                outputDescriptionType.getTitle().size());
        Assert.assertEquals("The OutputDescriptionType first title value is not the one expected", "title",
                outputDescriptionType.getTitle().get(0).getValue());
        Assert.assertEquals("The OutputDescriptionType first title language is not the one expected", "en",
                outputDescriptionType.getTitle().get(0).getLang());
        Assert.assertEquals("The OutputDescriptionType second title value is not the one expected", "titre",
                outputDescriptionType.getTitle().get(1).getValue());
        Assert.assertEquals("The OutputDescriptionType second title language is not the one expected", "fr",
                outputDescriptionType.getTitle().get(1).getLang());

        Assert.assertEquals("The OutputDescriptionType description attribute should have a size of 2", 2,
                outputDescriptionType.getAbstract().size());
        Assert.assertEquals("The OutputDescriptionType first abstract value is not the one expected", "description",
                outputDescriptionType.getAbstract().get(0).getValue());
        Assert.assertEquals("The OutputDescriptionType first abstract language is not the one expected", "en",
                outputDescriptionType.getAbstract().get(0).getLang());
        Assert.assertEquals("The OutputDescriptionType second abstract value is not the one expected", "description",
                outputDescriptionType.getAbstract().get(1).getValue());
        Assert.assertEquals("The OutputDescriptionType second abstract language is not the one expected", "fr",
                outputDescriptionType.getAbstract().get(1).getLang());

        Assert.assertEquals("The OutputDescriptionType keywords attribute should have a size of 1", 1,
                outputDescriptionType.getKeywords().size());
        Assert.assertEquals("The OutputDescriptionType first keywordList should have a size of 2", 2,
                outputDescriptionType.getKeywords().get(0).getKeyword().size());
        Assert.assertEquals("The value of the first keyword of the first keywordList is not the one expected",
                "keyword", outputDescriptionType.getKeywords().get(0).getKeyword().get(0).getValue());
        Assert.assertEquals("The language of the first keyword of the first keywordList is not the one expected",
                "en", outputDescriptionType.getKeywords().get(0).getKeyword().get(0).getLang());
        Assert.assertEquals("The value of the first keyword of the first keywordList is not the one expected",
                "motclef", outputDescriptionType.getKeywords().get(0).getKeyword().get(1).getValue());
        Assert.assertEquals("The language of the first keyword of the first keywordList is not the one expected",
                "fr", outputDescriptionType.getKeywords().get(0).getKeyword().get(1).getLang());

        Assert.assertEquals("The InputDesciriptionType identifier is incorrect.", processId+":"+"identifier",
                outputDescriptionType.getIdentifier().getValue());

        Assert.assertEquals("The OutputDescriptionType metadata attribute size should be 1", 1,
                outputDescriptionType.getMetadata().size());
        Assert.assertEquals("The role of the first metadata is not the one expected", "role",
                outputDescriptionType.getMetadata().get(0).getRole());
        Assert.assertEquals("The title of the first metadata is not the one expected", "title",
                outputDescriptionType.getMetadata().get(0).getTitle());
    }

    /**
     * Class used to declare and get fields with the annotation to parse.
     */
    private class FieldProvider{
        /** The simplest BoundingBox input declaration */
        @BoundingBoxAttribute
        @InputAttribute
        @DescriptionTypeAttribute(title = {"title"})
        private String simplestBoundingBoxInput = "0,0,1,1;EPSG:4326";

        /** A complex BoundingBox input declaration */
        @BoundingBoxAttribute(supportedCRS = {"EPSG:4326", "EPSG:2000", "EPSG:2001"}, dimension = 2)
        @InputAttribute(maxOccurs = 2, minOccurs = 0)
        @DescriptionTypeAttribute(
                title = {"title", "en", "titre", "fr"},
                description = {"description", "en","description","fr"},
                keywords = {"keyword", "en", "motclef", "fr"},
                identifier = "identifier",
                metadata = {"role","title"}
        )
        private String complexBoundingBoxInput = "EPSG:4326;0,0,1,1";

        /** The simplest BoundingBox output declaration */
        @BoundingBoxAttribute
        @OutputAttribute
        @DescriptionTypeAttribute(title = {"title"})
        private String simplestBoundingBoxOutput;

        /** A complex BoundingBox output declaration */
        @BoundingBoxAttribute(supportedCRS = {"EPSG:4326", "EPSG:2000", "EPSG:2001"}, dimension = 2)
        @OutputAttribute
        @DescriptionTypeAttribute(
                title = {"title", "en", "titre", "fr"},
                description = {"description", "en","description","fr"},
                keywords = {"keyword", "en", "motclef", "fr"},
                identifier = "identifier",
                metadata = {"role","title"}
        )
        private String complexBoundingBoxOutput = "EPSG:4326";
    }
}
