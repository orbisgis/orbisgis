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

package org.orbisgis.orbistoolbox.model;


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
    /**
     * Minimum value of the range (can not be null)
     */
    private double minimumValue;
    /**
     * Maximum value of the range (can not be null)
     */
    private double maximumValue;
    /**
     * Spacing between two values
     */
    private double spacing;

    /**
     * Constructor defining a range without spacing between values.
     *
     * @param minimumValue Maximum value of the range (can not be null).
     * @param maximumValue Minimum value of the range (can not be null).
     * @throws MalformedScriptException Exception get if maximum < minimum.
     */
    public Range(double minimumValue, double maximumValue) throws MalformedScriptException {
        if(maximumValue<minimumValue){
            throw new MalformedScriptException(this.getClass(), "maximumValue", "can not be less than the minimum");
        }
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        this.spacing = 0;
    }

    /**
     * Constructor defining a range with spacing between values.
     *
     * @param minimumValue Maximum value of the range (can not be null).
     * @param maximumValue Minimum value of the range (can not be null).
     * @param spacing      Spacing between value. If set to null or 0, it means there is no spacing.
     * @throws MalformedScriptException Exception get if maximum < minimum or if spacing > maximum-minimum.
     */
    public Range(double minimumValue, double maximumValue, double spacing) throws MalformedScriptException {
        if(maximumValue<minimumValue){
            throw new MalformedScriptException(this.getClass(), "maximumValue", "can not be less than the minimum");
        }
        if(spacing > maximumValue-minimumValue){
            throw new MalformedScriptException(this.getClass(), "spacing", "can not be more than the deference " +
                    "between maximum and minimum");
        }
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
        if(spacing <0){
            this.spacing = 0;
        }
        this.spacing = spacing;
    }

    /**
     * Returns the maximum value of the range.
     *
     * @return The maximum value of the range.
     */
    public double getMaximumValue() {
        return maximumValue;
    }

    /**
     * Sets the maximum value of the range. The maximumValue parameter can not be null.
     *
     * @param maximumValue Maximum value of the range (can not be null).
     * @throws MalformedScriptException Exception get on trying to set a maximumValue < minimumValue.
     */
    public void setMaximumValue(double maximumValue) throws MalformedScriptException {
        if (maximumValue < minimumValue) {
            throw new MalformedScriptException(this.getClass(), "maximumValue", "can not be less than the minimum");
        }
        this.maximumValue = maximumValue;
    }


    /**
     * Returns the minimum value value of the range.
     *
     * @return The minimum value value of the range.
     */
    public double getMinimumValue() {
        return minimumValue;
    }


    /**
     * Sets the minimum value of the range. The minimumValue parameter can not be null.
     *
     * @param minimumValue Minimum value of the range (can not be null).
     * @throws MalformedScriptException Exception get on trying to set minimumValue > maximumValue.
     */
    public void setMinimumValue(double minimumValue) throws MalformedScriptException {
        if (minimumValue > maximumValue) {
            throw new MalformedScriptException(this.getClass(), "minimumValue", "can not be more than the maximum");
        }
        this.minimumValue = minimumValue;
    }

    /**
     * Sets the spacing value. If it is set to null or 0, it means there is no spacing.
     *
     * @param spacing Spacing between value.
     * @throws MalformedScriptException Exception get on trying to set spacing > minimumValue - maximumValue.
     */
    public void setSpacingValue(double spacing) throws MalformedScriptException {
        if (spacing > minimumValue - maximumValue) {
            throw new MalformedScriptException(this.getClass(), "spacing", "can not be more than the difference " +
                    "between maximum and minimum");
        }
        if(spacing <0){
            this.spacing = 0;
        }
        this.spacing = spacing;
    }

    /**
     * Returns the spacing value.
     *
     * @return The spacing value.
     */
    public double getSpacing() {
        return spacing;
    }
}
