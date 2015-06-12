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

import java.util.List;

/**
 * Bounding box data serves a variety of purposes in spatial data processing.
 * Some simple applications are the definition of extents for a clipping operation or the definition of an analysis region.
 * This specification inherits the bounding box specification from OWS Common.
 *
 * For more informations : docs.opengeospatial.org/is/14-065/14-065.html#28
 *
 * @author Sylvain PALOMINOS
 */

public class BoundingBoxData extends DataDescription {
    /** The supported CRS for BoundingBox data. */
    private List<SupportedCRS> supportedCRSs;
    /** Bounding box. */
    private BoundingBox boundingBox;

    /**
     * Constructor with the fewest argument needed.
     * All the arguments can not be null.
     * @param formatList List of formats supported.
     * @param supportedCRSList List of CRS supported. It should contain only one default CRS.
     * @param boundingBox BoundingBox.
     * @throws IllegalArgumentException Exception get if an argument is null or
     * if the list of supported CRS does not contain only one default CRS.
     */
    public BoundingBoxData(List<Format> formatList, List<SupportedCRS> supportedCRSList, BoundingBox boundingBox)
            throws IllegalArgumentException {
        super(formatList);
        if (supportedCRSList == null || supportedCRSList.isEmpty() || supportedCRSList.contains(null)) {
            throw new IllegalArgumentException("The parameter \"supportedCRSList\" can not be null or empty or" +
                    "containing a null value");
        }
        if (boundingBox == null) {
            throw new IllegalArgumentException("The parameter \"boundingBox\" can not be null");
        }

        boolean hasDefault = false;
        for(SupportedCRS crs : supportedCRSList) {
            if (hasDefault && crs.isDefaultCRS()) {
                throw new IllegalArgumentException("The parameter \"supportedCRSList\" can only contain one" +
                        " default CRS");
            }
            if(crs.isDefaultCRS()){
                hasDefault = true;
            }
        }

        if(!hasDefault){
            throw new IllegalArgumentException("The parameter \"supportedCRSList\" should contain a default CRS");
        }

        this.setFormats(formatList);
        this.setSupportedCRSs(supportedCRSList);
        this.boundingBox = boundingBox;
    }

    /**
     * Returns the list of supported CRS.
     * @return List of supported CRS.
     */
    public List<SupportedCRS> getSupportedCRS() {
        return supportedCRSs;
    }

    /**
     * Adds a supported CRS.
     * @param supportedCRS Not nul supported CRS.
     * @throws IllegalArgumentException Exception get if the argument is null or if there is more than one default CRS.
     */
    public void addSupportedCRS(SupportedCRS supportedCRS) throws IllegalArgumentException{
        if (supportedCRSs == null) {
            throw new IllegalArgumentException("The parameter \"supportedCRS\" can not be null");
        }
        if (supportedCRS.isDefaultCRS()) {
            throw new IllegalArgumentException("Only one CRS can be the default one");
        }
        this.supportedCRSs.add(supportedCRS);
    }

    /**
     * Removes a supported CRS.
     * @param supportedCRS Not null CRS to remove. It can not be the last one or the default one.
     * @throws IllegalArgumentException Exception get on trying to remove the default CRS or the last one.
     */
    public void removeSupportedCRS(SupportedCRS supportedCRS) throws IllegalArgumentException {
        if(supportedCRS == null) {
            return;
        }
        if(supportedCRS.isDefaultCRS()){
            throw new IllegalArgumentException("Can not remove the default CRS");
        }
        if (this.supportedCRSs.size() == 1 && this.supportedCRSs.contains(supportedCRS)) {
            throw new IllegalArgumentException("The attribute \"supportedCRSs\" can not be empty");
        }
        this.supportedCRSs.remove(supportedCRS);
    }

    /**
     * Sets the list of supported CRS.
     * @param supportedCRSs List of supported CRS.
     * @throws IllegalArgumentException Exception get if an argument is null or empty or
     * if the list of supported CRS does not contain only one default CRS.
     */
    public void setSupportedCRSs(List<SupportedCRS> supportedCRSs) throws IllegalArgumentException {
        if (supportedCRSs == null || supportedCRSs.isEmpty() || supportedCRSs.contains(null)) {
            throw new IllegalArgumentException("The parameter \"supportedCRSs\" can not be null or empty or" +
                    "containing a null value");
        }

        boolean hasDefault = false;
        for(SupportedCRS crs : supportedCRSs) {
            if (hasDefault && crs.isDefaultCRS()) {
                throw new IllegalArgumentException("The parameter \"supportedCRSs\" can only contain one" +
                        " default CRS");
            }
            if(crs.isDefaultCRS()){
                hasDefault = true;
            }
        }

        if(!hasDefault){
            throw new IllegalArgumentException("The parameter \"supportedCRSs\" should contain a default CRS");
        }

        this.supportedCRSs = supportedCRSs;
    }
}
