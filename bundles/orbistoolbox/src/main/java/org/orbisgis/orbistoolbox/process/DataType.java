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
import java.net.URISyntaxException;


/**
 * @author Sylvain PALOMINOS
 */


public enum DataType {
    STRING("http://www.w3.org/2001/XMLSchema#string"),
    INTEGER("http://www.w3.org/2001/XMLSchema#integer"),
    DECIMAL("http://www.w3.org/2001/XMLSchema#decimal"),
    BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean"),
    DOUBLE("http://www.w3.org/2001/XMLSchema#double"),
    FLOAT("http://www.w3.org/2001/XMLSchema#float");

    private URI
            uri;

    DataType(String uri) {
        try {
            this.uri =
                    new URI(uri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public URI getUri() {
        return uri;
    }
}
