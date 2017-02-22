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
package org.orbisgis.sif.multiInputPanel;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * An Numeric constraint for a specific field
 * @author Nicolas Fortin
 */
public abstract class MIPValidationNumeric<T extends Number & Comparable<T>> implements MIPValidation {
    private String fieldName;
    private String fieldLabel;
    private T minValue = null;
    private T maxValue = null;
    private final I18n i18n = I18nFactory.getI18n(MIPValidationNumeric.class);
    private final Class<T> type;
    /**
     * Constructor
     * @param fieldName Field id
     * @param fieldLabel Field label, for error messages
     * @param type Class, ex: Integer.class
     */
    public MIPValidationNumeric(String fieldName, String fieldLabel, Class<T> type) {
        this.fieldName = fieldName;
        this.fieldLabel = fieldLabel;
        this.type = type;
    }

    /**
     * Set minimum value
     * @param minValue
     */
    public void setMinValue(T minValue) {
        this.minValue = minValue;
    }

    /**
     * Set maximum value
     * @param maxValue
     */
    public void setMaxValue(T maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Convert into T
     * @param value String representation
     * @return T instance
     * @throws NumberFormatException
     */
    protected abstract T valueOf(String value) throws NumberFormatException;

    @Override
    public String validate(MultiInputPanel mid) {
        String value = mid.getInput(fieldName);
        if(value!=null) {
            try {
                T val = valueOf(value);
                if(minValue!=null && Math.signum(val.compareTo(minValue))==-1) {
                    return i18n.tr("The {0} field must be greater or equal than {1}",fieldLabel,minValue);
                } else if(maxValue!=null && Math.signum(val.compareTo(maxValue))==1) {
                    return i18n.tr("The {0} field must be lower or equal than {1}",fieldLabel,maxValue);
                }
            } catch (NumberFormatException ex) {
                return i18n.tr("The {0} field must be {1}",fieldLabel,type.getSimpleName().toLowerCase());
            }
        }
        return null;
    }
}
