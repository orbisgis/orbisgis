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
package org.orbisgis.wpsservice.model;

import net.opengis.wps._2_0.ComplexDataType;
import net.opengis.wps._2_0.Format;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;


import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * GeometryData extends the ComplexData class.
 * It represents a geometry.
 *
 * @author Sylvain PALOMINOS
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Geometry", propOrder = {"geometryTypeList", "excludedTypeList", "dimension", "defaultValue"})
public class GeometryData extends ComplexDataType {

    /** List of type accepted for the geometry.*/
    @XmlElement(name = "GeometryType", namespace = "http://orbisgis.org")
    private List<DataType> geometryTypeList;
    /** List of type excluded for the geometry.*/
    @XmlElement(name = "ExcludedType", namespace = "http://orbisgis.org")
    private List<DataType> excludedTypeList;
    /** Dimension of the geometry. Can be 2(D) or 3(D). */
    @XmlAttribute(name = "dimension")
    private int dimension;
    /** Default value of the GeometryData. */
    @XmlAttribute(name = "defaultValue")
    private String defaultValue;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(GeometryData.class);

    /**
     * Main Constructor.
     * @param formatList Format allowed.
     * @param geometryTypeList List of the type accepted for this geometry.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public GeometryData(List<Format> formatList, List<DataType> geometryTypeList) throws MalformedScriptException {
        format = formatList;
        this.geometryTypeList = geometryTypeList;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected GeometryData(){
        super();
    }

    /**
     * Returns the list of valid type for the geometry.
     * @return List of accepted DataType.
     */
    public List<DataType> getGeometryTypeList() {
        return geometryTypeList;
    }

    /**
     * Sets the list of excluded type for the geometry.
     * @param excludedTypeList List of excluded DataType.
     */
    public void setExcludedTypeList(List<DataType> excludedTypeList) throws MalformedScriptException {
        for(DataType excludedType : excludedTypeList){
            for(DataType dataType : geometryTypeList){
                if(excludedType.equals(dataType)){
                    throw new MalformedScriptException(GeometryData.class, "excludedTypeList", I18N.tr("A same DataType" +
                            " is accepted and excluded."));
                }
            }
        }
        this.excludedTypeList = excludedTypeList;
    }

    /**
     * Returns the list of excluded type for the field.
     * @return List of excluded DataType.
     */
    public List<DataType> getExcludedTypeList() {
        return excludedTypeList;
    }

    /**
     * Sets the dimension of the geometry. Can be 2(D) or 3(D).
     * @param dimension The dimension of the geometry. Can be 2(D) or 3(D).
     */
    public void setDimension(int dimension){
        this.dimension = dimension;
    }

    /**
     * Returns the dimension of the geometry. Can be 2(D) or 3(D).
     * @return The dimension of the geometry. Can be 2(D) or 3(D).
     */
    public int getDimension(){
        return dimension;
    }

    /**
     * Sets the default value of the geometry.
     * @param defaultValue Default value of the geometry.
     */
    public void setDefaultValue(String defaultValue){
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value of the geometry.
     * @return The default value of the geometry.
     */
    public String getDefaultValue(){
        return defaultValue;
    }
}
