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
package org.orbisgis.coremap.renderer.se.transform;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import org.orbisgis.coremap.map.MapTransform;
import org.orbisgis.coremap.renderer.se.SymbolizerNode;
import org.orbisgis.coremap.renderer.se.common.Uom;
import org.orbisgis.coremap.renderer.se.parameter.ParameterException;

/**
 * Each implementation represent an affine transformation base on RealParameter.
 * It means each transformation can depends on feature attributes
 *
 * @author Maxence Laurent
 */
public interface Transformation extends SymbolizerNode{

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
    AffineTransform getAffineTransform(Map<String,Object> map, Uom uom, MapTransform mt,
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
}
