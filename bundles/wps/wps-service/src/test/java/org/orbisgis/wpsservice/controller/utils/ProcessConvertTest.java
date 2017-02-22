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
package org.orbisgis.wpsservice.controller.utils;

import net.opengis.wps._2_0.ProcessDescriptionType;
import net.opengis.wps._2_0.ProcessOffering;
import org.junit.Assert;
import org.junit.Test;
import org.orbisgis.wpsgroovyapi.attributes.ProcessAttribute;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author Sylvain PALOMINOS
 */
public class ProcessConvertTest {
    /****************
     * FULL PROCESS *
     ****************/

    /** Field containing the full annotation. */
    @ProcessAttribute(
            language = "en"
    )
    public Object fullProcessAttribute;
    /** Name of the field containing the full ProcessAttribute annotation. */
    private static final String FULL_PROCESS_ATTRIBUTE_FIELD_NAME = "fullProcessAttribute";

    /**
     * Test if the decoding and convert of the full process annotation into its java object is valid.
     */
    @Test
    public void testFullProcessAttributeConvert() {
        try {
            boolean annotationFound = false;
            //Retrieve the Values object
            ProcessDescriptionType process = new ProcessDescriptionType();
            ProcessOffering processOffering = new ProcessOffering();
            processOffering.setProcess(process);
            //Inspect all the annotation of the field to get the ValuesAttribute one
            Field valuesField = this.getClass().getDeclaredField(FULL_PROCESS_ATTRIBUTE_FIELD_NAME);
            for (Annotation annotation : valuesField.getDeclaredAnnotations()) {
                //Once the annotation is get, decode it.
                if (annotation instanceof ProcessAttribute) {
                    annotationFound = true;
                    ProcessAttribute processAnnotation = (ProcessAttribute) annotation;
                    ObjectAnnotationConverter.annotationToObject(processAnnotation, processOffering);
                }
            }

            //If the annotation hasn't been found, the test has failed.
            if (!annotationFound) {
                Assert.fail("Unable to get the annotation '@ValuesAttribute' from the field '" +
                        FULL_PROCESS_ATTRIBUTE_FIELD_NAME + "'.");
            }

            ////////////////////////////
            // Tests the Range Values //
            ////////////////////////////

            String language = "en";

            //Test the maximum
            String messageLanguage = "The process language is not the one expected (" + process.getLang() +
                    " instead of " + language;
            boolean conditionLanguage = process.getLang().equals(language);
            Assert.assertTrue(messageLanguage, conditionLanguage);

        } catch (NoSuchFieldException e) {
            Assert.fail("Unable to get the field '" + FULL_PROCESS_ATTRIBUTE_FIELD_NAME + "' from the class '" +
                    this.getClass().getCanonicalName() + "'.");
        }
    }
}
