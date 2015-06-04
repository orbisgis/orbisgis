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
 * @author Sylvain PALOMINOS
 */

public class BoundingBoxData
        extends DataDescription {
    private List<SupportedCRS>
            supportedCRSs;
    private BoundingBox
            boundingBox;

    public BoundingBoxData(List<Format> formatList,
                           List<SupportedCRS> supportedCRSList)
            throws
            IllegalArgumentException {
        super();
        if (formatList ==
                null) {
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null");
        }
        if (supportedCRSList ==
                null) {
            throw new IllegalArgumentException("The parameter \"literalDataDomainList\" can not be null");
        }
        if (boundingBox ==
                null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }

        if (formatList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"formatList\" can not be null");
        }
        if (supportedCRSList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"literalDataDomainList\" can not be null");
        }

        this.setFormats(formatList);
        this.setSupportedCRSs(supportedCRSList);
        this.boundingBox =
                boundingBox;
    }

    public List<SupportedCRS> getSupportedCRS() {
        return supportedCRSs;
    }

    public void addSupportedCRS(SupportedCRS supportedCRS) {
        if (supportedCRSs !=
                null) {
            this.supportedCRSs.add(supportedCRS);
        }
    }

    public void addAllSupportedCRSs(List<SupportedCRS> literalDataDomains) {
        for (SupportedCRS ldd : supportedCRSs) {
            this.addSupportedCRS(ldd);
        }
    }

    public void removeSupportedCRS(SupportedCRS supportedCRS)
            throws
            IllegalArgumentException {
        if (this.supportedCRSs.size() ==
                1 &&
                this.supportedCRSs.contains(supportedCRS)) {
            throw new IllegalArgumentException("The attribute \"supportedCRSs\" can not be empty");
        }
        this.supportedCRSs.remove(supportedCRS);
    }

    public void removeAllSupportedCRSs(List<SupportedCRS> supportedCRSs)
            throws
            IllegalArgumentException {
        if (this.supportedCRSs.size() ==
                supportedCRSs.size() &&
                this.supportedCRSs.containsAll(supportedCRSs)) {
            throw new IllegalArgumentException("The attribute \"supportedCRSs\" can not be empty");
        }
        this.supportedCRSs.removeAll(supportedCRSs);
    }

    public void setSupportedCRSs(List<SupportedCRS> supportedCRSs)
            throws
            IllegalArgumentException {
        if (supportedCRSs ==
                null ||
                supportedCRSs.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"supportedCRSs\" can not be null or empty");
        }
        this.supportedCRSs =
                supportedCRSs;
    }
}
