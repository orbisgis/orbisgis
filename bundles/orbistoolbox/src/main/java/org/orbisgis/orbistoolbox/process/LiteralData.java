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

import java.util.List;

/**
 * The LiteralData type encodes atomic data such as scalars, linear units, or well-known names.
 * Domains for LiteralData are a combination of data types (e.g. Double, Integer, String),
 * a given value range, and an associated unit (e.g. meters, degrees Celsius).
 * <p/>
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#25
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralData
        extends DataDescription {
    private List<LiteralDataDomain>
            literalDataDomains;
    private LiteralValue
            value;

    public LiteralData(List<Format> formatList,
                       List<LiteralDataDomain> literalDataDomainList,
                       LiteralValue value)
            throws
            IllegalArgumentException {
        super();
        if (formatList ==
                null) {
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null");
        }
        if (literalDataDomainList ==
                null) {
            throw new IllegalArgumentException("The parameter \"literalDataDomainList\" can not be null");
        }
        if (value ==
                null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }

        if (formatList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null");
        }
        if (literalDataDomainList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"literalDataDomainList\" can not be null");
        }

        this.setFormats(formatList);
        this.setLiteralDomainType(literalDataDomainList);
        this.value =
                value;
    }

    public List<LiteralDataDomain> getLiteralDomainType() {
        return literalDataDomains;
    }

    public void addLiteralDomainType(LiteralDataDomain literalDataDomain) {
        if (literalDataDomain !=
                null) {
            this.literalDataDomains.add(literalDataDomain);
        }
    }

    public void addAllLiteralDomainType(List<LiteralDataDomain> literalDataDomains) {
        for (LiteralDataDomain ldd : literalDataDomains) {
            this.addLiteralDomainType(ldd);
        }
    }

    public void removeLiteralDomainType(LiteralDataDomain literalDataDomain)
            throws
            IllegalArgumentException {
        if (this.literalDataDomains.size() ==
                1 &&
                this.literalDataDomains.contains(literalDataDomain)) {
            throw new IllegalArgumentException("The attribute \"literalDataDomains\" can not be empty");
        }
        this.literalDataDomains.remove(literalDataDomain);
    }

    public void removeAllLiteralDomainType(List<LiteralDataDomain> literalDataDomains)
            throws
            IllegalArgumentException {
        if (this.literalDataDomains.size() ==
                literalDataDomains.size() &&
                this.literalDataDomains.containsAll(literalDataDomains)) {
            throw new IllegalArgumentException("The attribute \"literalDataDomains\" can not be empty");
        }
        this.literalDataDomains.removeAll(literalDataDomains);
    }

    public void setLiteralDomainType(List<LiteralDataDomain> literalDataDomain)
            throws
            IllegalArgumentException {
        if (literalDataDomain ==
                null ||
                literalDataDomain.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"literalDataDomains\" can not be null or empty");
        }
        this.literalDataDomains =
                literalDataDomain;
    }
}
