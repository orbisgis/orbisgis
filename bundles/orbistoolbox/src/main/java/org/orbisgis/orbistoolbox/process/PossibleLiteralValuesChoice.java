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

import java.util.ArrayList;
import java.util.List;

/**
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#26
 *
 * @author Sylvain PALOMINOS
 */

public class PossibleLiteralValuesChoice {
    private List<Values>
            allowedValues;
    private Boolean
            anyValue =
            true;
    private String
            valuesReference;

    public PossibleLiteralValuesChoice(List<Values> allowedValues)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (allowedValues ==
                null) {
            throw new IllegalArgumentException("The parameter \"allowedValues\" can not be null");
        }
        if (allowedValues.isEmpty()) {
            throw new IllegalArgumentException("Both parameters can not be empty");
        }
        this.allowedValues =
                new ArrayList<>();
        this.allowedValues.addAll(allowedValues);
        this.anyValue =
                null;
        this.valuesReference =
                null;
    }

    public PossibleLiteralValuesChoice(String valuesReference) {
        this.allowedValues =
                null;
        this.anyValue =
                null;
        this.valuesReference =
                valuesReference;
    }
}
