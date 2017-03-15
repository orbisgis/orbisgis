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

package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Object representing a bounding box.
 *
 * @author Sylvain PALOMINOS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BoundingBoxData",
        propOrder = {"defaultCrs", "supportedCrs", "dimension"})
public class BoundingBoxData extends ComplexDataType {

    /** Default CRS of the BoundingBox. Should be a string with the pattern : authority:code, like EPSG:2000.*/
    @XmlAttribute(name = "defaultCrs", namespace = "http://orbisgis.org")
    private String defaultCrs;
    /** List of CRS supported by the BoundingBox data without the default one. Should be a string with the pattern :
     *      authority:code, like EPSG:2000.*/
    @XmlAttribute(name = "supportedCrs", namespace = "http://orbisgis.org")
    String[] supportedCrs;
    /** Dimension of the bounding box.*/
    @XmlAttribute(name = "dimension", namespace = "http://orbisgis.org")
    private int dimension;

    /**
     * Main constructor.
     * @param formatList Formats of the data accepted.
     * @param defaultCrs Default CRS of the BoundingBox.
     * @param supportedCrs List of CRS supported by the BoundingBox data without the default one.
     * @param dimension Dimension of the bounding box.
     * @throws MalformedScriptException
     */
    public BoundingBoxData(List<Format> formatList, String defaultCrs, String[] supportedCrs, int dimension)
            throws MalformedScriptException {
        format = formatList;
        this.defaultCrs = defaultCrs;
        if(supportedCrs.length > 0) {
            boolean isContained = false;
            for (String crs : supportedCrs) {
                if (crs.equals(defaultCrs)) {
                    isContained = true;
                }
            }
            if (!isContained) {
                throw new MalformedScriptException(BoundingBoxData.class, "supportedCrs", "should contains the " +
                        "default CRS");
            }
        }
        this.supportedCrs = supportedCrs;
        if(dimension != 2 && dimension != 3){
            throw new MalformedScriptException(BoundingBoxData.class, "dimension", "dimension should be 2 or 3");
        }
        if(dimension == 3){
            throw new MalformedScriptException(BoundingBoxData.class, "dimension", "3D Bounding Box is not supported yet.");
        }
        this.dimension = dimension;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected BoundingBoxData(){
        super();
    }

    /**
     * Sets the default CRS.
     * @param defaultCrs The default CRS.
     */
    public void setDefaultCrs(String defaultCrs) {
        this.defaultCrs = defaultCrs;
    }

    /**
     * Returns the default CRS.
     * @return The default CRS.
     */
    public String getDefaultCrs() {
        return defaultCrs;
    }

    /**
     * Sets the list of the supported CRS.
     * @param supportedCrs The list of the supported CRS.
     */
    public void setSupportedCrs(String[] supportedCrs) {
        this.supportedCrs = supportedCrs;
    }

    /**
     * Returns the list of the supported CRS.
     * @return The list of the supported CRS.
     */
    public String[] getSupportedCrs() {
        return supportedCrs;
    }

    /**
     * Sets the dimension od the BoundingBox.
     * @param dimension The dimension of the BoundingBox.
     */
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    /**
     * Returns the dimension of the BoundingBox.
     * @return The dimension of the BoundingBox.
     */
    public int getDimension() {
        return dimension;
    }
}
