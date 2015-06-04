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
import java.util.ArrayList;
import java.util.List;

/**
 * cf OGC 06-121r9
 *
 * @author Sylvain PALOMINOS
 */

public class BoundingBox {
    private List<Number>
            lowerCorner;
    private List<Number>
            upperCorner;
    private URI
            crs;
    private int
            dimensions;

    public BoundingBox() {
        lowerCorner =
                new ArrayList<>();
        upperCorner =
                new ArrayList<>();
        crs =
                null;
        dimensions =
                0;
    }

    public List<Number> getLowerCorner() {
        return lowerCorner;
    }

    public void setLowerCorner(List<Number> lowerCorner) {
        this.lowerCorner =
                lowerCorner;
    }

    public List<Number> getUpperCorner() {
        return upperCorner;
    }

    public void setUpperCorner(List<Number> upperCorner) {
        this.upperCorner =
                upperCorner;
    }

    public URI getCrs() {
        return crs;
    }

    public void setCrs(URI crs) {
        this.crs =
                crs;
    }

    public int getDimensions() {
        return dimensions;
    }

    public void setDimensions(int dimensions) {
        this.dimensions =
                dimensions;
    }


}
