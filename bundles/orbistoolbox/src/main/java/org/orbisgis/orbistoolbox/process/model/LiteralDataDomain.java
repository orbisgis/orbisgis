/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 *
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 *
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process.model;

import java.net.URI;

/**
 * The valid domain for literal data.
 *
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#26
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralDataDomain {
    /** Identifies a valid format for an input or output. */
    private PossibleLiteralValuesChoice plvc;
    /** Reference to the data type of this set of values. */
    private DataType dataType;
    /** Indicates that this quantity has units and provides the unit of measurement. */
    private URI uom;
    /** Default value for this quantity. */
    private Values defaultValue;
    /** Indicates that this is the default/native domain. */
    private boolean defaultDomain;

    /**
     * Constructor giving the fewest argument needed for the instantiation.
     * All the arguments can not be null.
     * @param plvc Identifies a valid format for an input or output.
     * @param dataType Reference to the data type of this set of values.
     * @param defaultValue Default value for this quantity.
     * @throws IllegalArgumentException Exception get on giving a null argument.
     */
    public LiteralDataDomain(PossibleLiteralValuesChoice plvc, DataType dataType, Values defaultValue)
            throws IllegalArgumentException {
        //Verify if the parameters are not null
        if (plvc == null) {
            throw new IllegalArgumentException("The parameter \"plvc\" can not be null");
        }
        if (dataType == null) {
            throw new IllegalArgumentException("The parameter \"dataType\" can not be null");
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not be null");
        }
        this.plvc = plvc;
        this.dataType = dataType;
        this.uom = null;
        this.defaultValue = defaultValue;
        this.defaultDomain = false;
    }

    /**
     * Sets the PossibleLiteralValuesChoice.
     * @param plvc Not null PossibleLiteralValuesChoice.
     * @throws IllegalArgumentException Exception get on giving a null argument.
     */
    public void setPossibleLiteralValuesChoice(PossibleLiteralValuesChoice plvc)
            throws IllegalArgumentException {
        //Verify if the parameters are not null
        if (plvc == null) {
            throw new IllegalArgumentException("The parameter \"plvc\" can not be null");
        }
        this.plvc = plvc;
    }

    /**
     * Returns the PossibleLiteralValuesChoice.
     * @return The PossibleLiteralValuesChoice.
     */
    public PossibleLiteralValuesChoice getPossibleLiteralValuesChoice() {
        return plvc;
    }

    /**
     * Sets the data type.
     * @param dataType Not null DataType.
     * @throws IllegalArgumentException Exception get on giving a null argument.
     */
    public void setDataType(DataType dataType) throws IllegalArgumentException {
        //Verify if the parameters are not null
        if (dataType == null) {
            throw new IllegalArgumentException("The parameter \"dataType\" can not be null");
        }
        this.dataType = dataType;
    }

    /**
     * Returns the data type.
     * @return The data type.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Sets the URI to the unit of this quantity.
     * If it is set to null it means that the quantity has no unit.
     * @param uom Unit of the quantity.
     */
    public void setUom(URI uom) {
        this.uom = uom;
    }

    /**
     * Returns the URI of the unit of this quantity.
     * If it return null it means that the quantity has no unit.
     * @return The unit of this quantity.
     */
    public URI getUom() {
        return this.uom;
    }

    /**
     * Indicates if this domain is the default one or not.
     * @param defaultDomain True if it is the default domain, false otherwise.
     */
    public void setDefaultDomain(boolean defaultDomain) throws IllegalArgumentException {
        this.defaultDomain = defaultDomain;
    }

    /**
     * Returns if this domain is the default one.
     * @return True if it is the default domain, false otherwise.
     */
    public boolean isDefaultDomain() {
        return this.defaultDomain;
    }

    /**
     * Sets the default value.
     * @param defaultValue Not null default value.
     * @throws IllegalArgumentException Exception get on setting a null default value.
     */
    public void setDefaultValue(Values defaultValue) throws IllegalArgumentException {
        if (defaultDomain && defaultValue == null) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not be null if \"defaultDomain\" is true");
        }
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the default value.
     * @return The default value.
     */
    public Values getDefaultValue() {
        return this.defaultValue;
    }
}
