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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * The supported CRS for BoundingBox data.
 * It contain :
 * - a list of not null URI which are the references to a unique CRS
 * - a boolean which determinate if the CRS id the default one or not.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#29
 *
 * @author Sylvain PALOMINOS
 */

public class SupportedCRS {

    /** References to a CRS definition. */
    private List<URI> crs;
    /** Indicates that this CRS is the default CRS. */
    private Boolean defaultCRS;

    /**
     * Constructor which define a CRS and if it is the default one.
     * @param crs Reference to a CRS definition.
     * @param defaultCRS True if the CRS is the default one, false otherwise.
     * @throws IllegalArgumentException Exception get on trying to set the CRS to null
     */
    public SupportedCRS(URI crs, boolean defaultCRS) throws IllegalArgumentException {
        if (crs == null) {
            throw new IllegalArgumentException("The parameter \"crs\" can not be null");
        }
        this.crs = new ArrayList<>();
        this.crs.add(crs);
        this.defaultCRS = defaultCRS;
    }

    /**
     * Constructor which define a CRS and if it is the default one.
     * @param crsList References to a CRS definition.
     * @param defaultCRS True if the CRS is the default one, false otherwise.
     * @throws IllegalArgumentException Exception ge on setting a list which is null, empty or containing null value.
     */
    public SupportedCRS(List<URI> crsList, boolean defaultCRS) throws IllegalArgumentException {
        if (crsList == null) {
            throw new IllegalArgumentException("The parameter \"crsList\" can not be null");
        }
        if (crsList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"crsList\" can not be empty");
        }
        if (crsList.contains(null)) {
            throw new IllegalArgumentException("The parameter \"crsList\" can not contain null values");
        }
        this.crs = new ArrayList<>();
        this.crs.addAll(crsList);
        this.defaultCRS = defaultCRS;
    }

    /**
     * Define if this CRS is the default one.
     * @param defaultCRS True if this CRS is the default one. False otherwise.
     */
    public void setDefaultCRS(boolean defaultCRS) {
        this.defaultCRS = defaultCRS;
    }

    /**
     * Returns true if the CRS is the default one, false otherwise.
     * @return true if the CRS is the default one, false otherwise.
     */
    public boolean isDefaultCRS() {
        return this.defaultCRS;
    }

    /**
     * Sets the CRS list.
     * @param crsList The new CRS list.
     * @throws IllegalArgumentException Exception ge on setting a list which is null, empty or containing null value.
     */
    public void setCRS(List<URI> crsList) throws IllegalArgumentException {
        if (crsList == null) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not be null");
        }
        if (crsList.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not be empty");
        }
        if (crsList.contains(null)) {
            throw new IllegalArgumentException("The parameter \"defaultValue\" can not contain null value");
        }
        this.crs = new ArrayList<>();
        this.crs.addAll(crsList);
    }

    /**
     * Returns the CRS list.
     * @return The CRS list.
     */
    public List<URI> getCRS() {
        return this.crs;
    }
}
