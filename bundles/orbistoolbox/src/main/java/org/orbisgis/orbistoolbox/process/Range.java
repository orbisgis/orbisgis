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
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for moredetails.
 *
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.orbistoolbox.process;


/**
 * Simple range of values. It contains the minimum and maximum value and the spacing between values.
 * The maximum and the minimum values can not be null.
 * If the spacing is not set or set to null, the rage is continuous (no spacing between two value).
 * All the attributes are represented as String.
 * This class comes from the OWS specification :  ows:RangeType and ows:Range
 *
 * @author Sylvain PALOMINOS
 */

public class Range extends Values {
    /** Minimum value of the range (can not be null) */
    private String minimumValue;
    /** Maximum value of the range (can not be null) */
    private String maximumValue;
    /** Spacing between two values */
    private String spacing;

    /**
     * Constructor defining a range without spacing between values.
     * @param minimumValue Maximum value of the range (can not be null).
     * @param maximumValue Minimum value of the range (can not be null).
     * @throws IllegalArgumentException Exception get on trying to set maximumValue or minimumValue to null.
     */
    public Range(String minimumValue, String maximumValue) throws IllegalArgumentException {
        if (minimumValue == null) {
            throw new IllegalArgumentException("The parameter \"minimumValue\" can not be null");
        }
        if (maximumValue == null) {
            throw new IllegalArgumentException("The parameter \"maximumValue\" can not be null");
        }
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.spacing = null;
    }

    /**
     * Constructor defining a range with spacing between values.
     * @param minimumValue Maximum value of the range (can not be null).
     * @param maximumValue Minimum value of the range (can not be null).
     * @param spacing Spacing between value. If set to null or 0, it means there is no spacing.
     * @throws IllegalArgumentException Exception get on trying to set maximumValue or minimumValue to null.
     */
    public Range(String minimumValue, String maximumValue, String spacing) throws IllegalArgumentException {
        if (minimumValue == null) {
            throw new IllegalArgumentException("The parameter \"minimumValue\" can not be null");
        }
        if (maximumValue == null) {
            throw new IllegalArgumentException("The parameter \"maximumValue\" can not be null");
        }
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        if(spacing.equals("0")){
            this.spacing = null;
        }
        else {
            this.spacing = spacing;
        }
    }

    /**
     * Returns the maximum value of the range.
     * @return The maximum value of the range.
     */
    public String getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the maximum value of the range. The maximumValue parameter can not be null.
     * @param maximumValue Maximum value of the range (can not be null).
     * @throws IllegalArgumentException Exception get on trying to set maximumValue to null.
     */
    public void setMaximumValue(String maximumValue) throws IllegalArgumentException {
        if (maximumValue == null) {
            throw new IllegalArgumentException("The parameter \"maximumValue\" can not be null");
        }
        this.maximumValue =
                maximumValue;
    }


    /**
     * Returns the minimum value value of the range.
     * @return The minimum value value of the range.
     */
    public String getMinimumValue() {
        return minimumValue;
    }


    /**
     * Sets the minimum value of the range. The minimumValue parameter can not be null.
     * @param minimumValue Minimum value of the range (can not be null).
     * @throws IllegalArgumentException Exception get on trying to set minimumValue to null.
     */
    public void setMinimumValue(String minimumValue) throws IllegalArgumentException {
        if (minimumValue == null) {
            throw new IllegalArgumentException("The parameter \"minimumValue\" can not be null");
        }
        this.minimumValue = minimumValue;
    }

    /**
     * Sets the spacing value. If it is set to null or 0, it means there is no spacing.
     * @param spacing Spacing between value.
     */
    public void setSpacingValue(String spacing) {
        this.spacing =
                spacing;
    }

    /**
     * Returns the spacing value.
     * @return The spacing value.
     */
    public String getSpacing() {
        return spacing;
    }
}
