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

import java.net.URI;

/**
 * The valid domain for literal data.
 * <p/>
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#26
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralDataDomain {

    private PossibleLiteralValuesChoice
            plvc;
    private DataType
            dataType;
    private URI
            uom;
    private Values
            defaultValue;
    private boolean
            defaultDomain;

    public LiteralDataDomain(PossibleLiteralValuesChoice plvc,
                             DataType dataType)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (plvc ==
                null) {
            throw new IllegalArgumentException("The parameter \"plvc\" can not be null");
        }
        if (dataType ==
                null) {
            throw new IllegalArgumentException("The parameter \"dataType\" can not be null");
        }
        this.plvc =
                plvc;
        this.dataType =
                dataType;
        this.uom =
                null;
        this.defaultValue =
                null;
        this.defaultDomain =
                false;
    }

    public void setPossibleLiteralValuesChoice(PossibleLiteralValuesChoice plvc)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (plvc ==
                null) {
            throw new IllegalArgumentException("The parameter \"plvc\" can not be null");
        }
        this.plvc =
                plvc;
    }

    public PossibleLiteralValuesChoice getPossibleLiteralValuesChoice() {
        return plvc;
    }

    public void setDataType(DataType dataType)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (dataType ==
                null) {
            throw new IllegalArgumentException("The parameter \"dataType\" can not be null");
        }
        this.dataType =
                dataType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setUom(URI uom) {
        this.uom =
                uom;
    }

    public URI getUom() {
        return this.uom;
    }

    public void setDefaultDomain(boolean defaultDomain)
            throws
            IllegalArgumentException {
        if (defaultDomain &&
                this.defaultValue ==
                        null) {
            throw new IllegalArgumentException("The parameter \"defaultDomain\" can not be true if no defaultValue was specified");
        }
        this.defaultDomain =
                defaultDomain;
    }

    public void setDefaultDomain(boolean defaultDomain,
                                 Values defaultValue)
            throws
            IllegalArgumentException {
        if (defaultDomain &&
                defaultValue ==
                        null) {
            throw new IllegalArgumentException("The parameter \"defaultDomain\" can not be true if \"defaultValue\" is null");
        }
        this.defaultDomain =
                defaultDomain;
        this.defaultValue =
                defaultValue;
    }

    public boolean isDefaultDomain() {
        return this.defaultDomain;
    }

    public void setDefaultValue(Values defaultValue)
            throws
            IllegalArgumentException {
        if (defaultDomain &&
                defaultValue ==
                        null) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not be null if \"defaultDomain\" is true");
        }
        this.defaultValue =
                defaultValue;
    }

    public Values getDefaultValue() {
        return this.defaultValue;
    }
}
