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
@XmlType(name = "Geometry", propOrder = {"geometryTypeList", "excludedTypeList", "dimension"})
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
}
