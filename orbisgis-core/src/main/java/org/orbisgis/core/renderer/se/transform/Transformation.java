/**
 * OrbisGIS is a GIS application dedicated to scientific spatial simulation.
 * This cross-platform GIS is developed at French IRSTV institute and is able to
 * manipulate and create vector and raster spatial information.
 *
 * OrbisGIS is distributed under GPL 3 license. It is produced by the "Atelier SIG"
 * team of the IRSTV Institute <http://www.irstv.fr/> CNRS FR 2488.
 *
 * Copyright (C) 2007-1012 IRSTV (FR CNRS 2488)
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
package org.orbisgis.core.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.gdms.data.values.Value;
import org.orbisgis.core.map.MapTransform;
import org.orbisgis.core.renderer.se.common.Uom;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.UsedAnalysis;

/**
 * Each implementation represent an affine transformation base on RealParameter.
 * It means each transformation can depends on feature attributes
 *
 * @author maxence
 */
public interface Transformation {

    /**
         * This method whall return {@code true} if the transformation can be
         * applied to geometry objects.
         * @return
         * {@code true} if this method can be applied on geometries, false
         * otherwise.
         */
    boolean allowedForGeometries();

    /**
     * Get the AWT {@code AffineTransform} that is represented by this {@code
     * Transformation}.
     * @param map
     * @param uom
     * @param mt
     * @param width
     * @param height
     * @return
     * @throws ParameterException
     * @throws IOException
     */
    AffineTransform getAffineTransform(Map<String,Value> map, Uom uom, MapTransform mt,
            Double width, Double height) throws ParameterException, IOException;

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBElement} that contains a {@code LabelType} specialization.
     */
    JAXBElement<?> getJAXBElement();

    /**
     * Get a JAXB representation of this {@code Label}
     * @return
     * A {@code JAXBType} that represents a {@code LabelType} specialization.
     */
    Object getJAXBType();

    /**
     * Get a String representation of the list of features this {@code Transformation}
     * depends on.
     * @return
     * The features this {@code Transformation} depends on, in a {@code String}.
     */
    HashSet<String> dependsOnFeature();

    /**
     * Retrieve an object describing the type of analysis made in the
     * symbolizer.
     * @return
     */
    UsedAnalysis getUsedAnalysis();
}
