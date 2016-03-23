package org.orbisgis.wpsservice.model;

import java.util.List;

/**
 * GeometryData extends the ComplexData class.
 * It represents a geometry.
 *
 * @author Sylvain PALOMINOS
 */

public class GeometryDataOld extends ComplexData {

    /** List of type accepted for the geometry.*/
    private List<DataType> geometryTypeList;
    /** List of type excluded for the geometry.*/
    private List<DataType> excludedTypeList;
    /** Dimension of the geometry. Can be 2(D) or 3(D). */
    private int dimension;

    /**
     * Main Constructor.
     * @param format Format allowed.
     * @param geometryTypeList List of the type accepted for this geometry.
     * @throws MalformedScriptException Exception get on setting a format which is null or is not the default one.
     */
    public GeometryDataOld(Format format, List<DataType> geometryTypeList) throws MalformedScriptException {
        super(format);
        this.geometryTypeList = geometryTypeList;
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
                    throw new MalformedScriptException(GeometryDataOld.class, "excludedTypeList", "A same DataType is" +
                            " accepted and excluded");
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
