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

package org.orbisgis.orbistoolbox.process;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Bounding box.
 *
 * @author Sylvain PALOMINOS
 */

public class BoundingBox {
    /** Coordinates of bounding box corner at which the value of each coordinate normally
     *  is the algebraic minimum a within this bounding box. */
    private List<Double> lowerCorner;
    /** Coordinates of bounding box corner at which the value of each coordinate normally
     *  is the algebraic maximum a within this bounding box. */
    private List<Double> upperCorner;
    /** Reference to definition of the CRS used by the LowerCorner and UpperCorner coordinates. */
    private URI crs;
    /** The number of dimensions in this CRS (the length of a coordinate sequence). */
    private int dimensions;

    public BoundingBox() {
        lowerCorner = new ArrayList<>();
        upperCorner = new ArrayList<>();
        crs = null;
        dimensions = 0;
    }

    /**
     * Returns the lower corner.
     * @return The lower corner.
     */
    public List<Double> getLowerCorner() {
        return lowerCorner;
    }

    /**
     * Sets the lower corner values.
     * The list of Double should be at the same size as the bounding box dimension.
     * @param lowerCorner List of double value of the corner.
     */
    public void setLowerCorner(List<Double> lowerCorner) {
        if(lowerCorner.size() == this.dimensions) {
            this.lowerCorner = new ArrayList<>();
            this.lowerCorner.addAll(lowerCorner);
        }
    }

    /**
     * Returns the upper corner.
     * @return The upper corner.
     */
    public List<Double> getUpperCorner() {
        return upperCorner;
    }

    /**
     * Sets the upper corner.
     * The list of Double should be at the same size as the bounding box dimension.
     * @param upperCorner List of double value of the corner.
     */
    public void setUpperCorner(List<Double> upperCorner) {
        if(upperCorner.size() == this.dimensions) {
            this.upperCorner = new ArrayList<>();
            this.upperCorner.addAll(upperCorner);
        }
    }

    /**
     * Returns the URI to the bounding box CRS.
     * @return The CRS URI.
     */
    public URI getCrs() {
        return crs;
    }

    /**
     * Sets the URI of the bounding box CRS.
     * @param crs New URI of the CRS.
     */
    public void setCrs(URI crs) {
        this.crs = crs;
    }

    /**
     * Returns the bounding box dimension.
     * @return The bounding box dimension.
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
     * Sets the bounding box dimension.
     * @param dimensions The bounding box dimension.
     */
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }
}
