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
import org.orbisgis.wpsservice.LocalWpsServerImpl;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sylvain PALOMINOS
 **/

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JDBCTableField", propOrder = {"jdbcTableIdentifier", "dataTypeList", "excludedTypeList",
        "jdbcTableFieldValueList", "multiSelection", "defaultValues"})
public class JDBCTableField extends ComplexDataType {

    /** Identifier of the parent JDBCTable */
    @XmlElement(name = "JDBCTableId", namespace = "http://orbisgis.org")
    private URI jdbcTableIdentifier;
    /** Indicates if the JDBCTableField should be reloaded because of a modification of the parent JDBCTable.*/
    @XmlTransient
    private boolean isSourceModified = true;
    /** List of type accepted for the field.*/
    @XmlElement(name = "FieldType", namespace = "http://orbisgis.org")
    private List<DataType> dataTypeList;
    /** List of type excluded for the field.*/
    @XmlElement(name = "ExcludedType", namespace = "http://orbisgis.org")
    private List<DataType> excludedTypeList;
    /** List of JDBCTableFieldValue liked to the JDBCTableField */
    @XmlElement(name = "JDBCTableFieldValue", namespace = "http://orbisgis.org")
    private List<JDBCTableFieldValue> jdbcTableFieldValueList;
    /** Indicates if the use can choose more than one field*/
    @XmlAttribute(name = "multiSelection")
    private boolean multiSelection = false;
    /** Default values of the JDBCTableField. */
    @XmlAttribute(name = "defaultValues")
    private String[] defaultValues;
    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(LocalWpsServerImpl.class);

    /**
     * Main constructor.
     * @param formatList Formats of the data accepted.
     * @param dataTypeList List of the type accepted for this field.
     * @param jdbcTableURI Identifier of the parent jdbcTable.
     * @throws MalformedScriptException
     */
    public JDBCTableField(List<Format> formatList, List<DataType> dataTypeList, URI jdbcTableURI) throws MalformedScriptException {
        format = formatList;
        jdbcTableFieldValueList = new ArrayList<>();
        this.dataTypeList = dataTypeList;
        this.jdbcTableIdentifier = jdbcTableURI;
    }

    /**
     * Protected empty constructor used in the ObjectFactory class for JAXB.
     */
    protected JDBCTableField(){
        super();
        dataTypeList = null;
        excludedTypeList = null;
        jdbcTableFieldValueList = null;
        jdbcTableIdentifier = null;
    }

    /**
     * Returns the identifier of the parent JDBCTable.
     * @return The identifier of the JDBCTable.
     */
    public URI getJDBCTableIdentifier(){
        return jdbcTableIdentifier;
    }

    /**
     * Tells if the parent JDBCTable has been modified since last time it was checked.
     * @return True if the parent JDBCTable has been modified, false otherwise.
     */
    public boolean isSourceModified() {
        return isSourceModified;
    }

    /**
     * Sets if the parent jdbcTable has been modified
     * @param isSourceModified True if the parent JDBCTable has been modified, false otherwise.
     */
    public void setSourceModified(boolean isSourceModified) {
        this.isSourceModified = isSourceModified;
        if(jdbcTableFieldValueList != null) {
            for (JDBCTableFieldValue jdbcTableFieldValue : jdbcTableFieldValueList) {
                jdbcTableFieldValue.setJDBCTableModified(isSourceModified);
            }
        }
    }

    /**
     * Returns the list of valid type for the field.
     * @return List of accepted DataType.
     */
    public List<DataType> getDataTypeList() {
        return dataTypeList;
    }

    /**
     * Adds a JDBCTableFieldValue as a 'child' of the JDBCTableField.
     * @param jdbcTableFieldValue JDBCTableFieldValue to add.
     */
    public void addJDBCTableFieldValue(JDBCTableFieldValue jdbcTableFieldValue){
        this.jdbcTableFieldValueList.add(jdbcTableFieldValue);
    }

    /**
     * Return the list of 'child' JDBCTableFieldValue.
     * @return List of JDBCTableFieldValue.
     */
    public List<JDBCTableFieldValue> getJDBCTableFieldValueList(){
        return jdbcTableFieldValueList;
    }

    /**
     * Sets the list of excluded type for the field.
     * @param excludedTypeList List of excluded DataType.
     */
    public void setExcludedTypeList(List<DataType> excludedTypeList) throws MalformedScriptException {
        for(DataType excludedType : excludedTypeList){
            for(DataType dataType : dataTypeList){
                if(excludedType.equals(dataType)){
                    throw new MalformedScriptException(JDBCTableField.class, "excludedTypeList", I18N.tr("A same DataType is" +
                            " accepted and excluded."));
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
     * Returns true if the user can select more than one field, false otherwise.
     * @return True if the user can select more than one field, false otherwise.
     */
    public boolean isMultiSelection() {
        return multiSelection;
    }

    /**
     * Sets if the user can select more than one field or not.
     * @param multiSelection True if the user can select more than one field, false otherwise.
     */
    public void setMultiSelection(boolean multiSelection) {
        this.multiSelection = multiSelection;
    }

    /**
     * Sets the default values of the geometry.
     * @param defaultValues Default values of the geometry.
     */
    public void setDefaultValues(String[] defaultValues){
        this.defaultValues = defaultValues;
    }

    /**
     * Returns the default values of the geometry.
     * @return The default values of the geometry.
     */
    public String[] getDefaultValues(){
        return defaultValues;
    }
}
