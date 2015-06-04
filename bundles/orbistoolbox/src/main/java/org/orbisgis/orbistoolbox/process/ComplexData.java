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
 * The ComplexData type does not describe the particular structure for value encoding.
 * Instead, the passed values must comply with the given format and the extended information, if provided.
 * <p/>
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#22
 *
 * @author Sylvain PALOMINOS
 */

public abstract class ComplexData
        extends DataDescription {
    /**
     * Other descriptive elements
     */
    private List<Object>
            anys;

    public ComplexData() {
        super();
        anys =
                new ArrayList<>();
    }

    protected List<Object> getAnys() {
        return anys;
    }

    protected void addAny(Object any) {
        this.anys.add(any);
    }

    protected void addAllAny(List<Object> anys) {
        this.anys.addAll(anys);
    }

    protected void removeAny(Object any) {
        this.anys.remove(any);
    }

    protected void removeAllAny(List<Object> anys) {
        this.anys.removeAll(anys);
    }

    protected void setAnys(List<Object> anys) {
        this.anys =
                anys;
    }
}
