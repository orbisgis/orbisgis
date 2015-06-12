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

import java.net.URI;
import java.net.URL;

/**
 * @author Sylvain PALOMINOS
 */

public class Metadata {
    /** Title of the documentation. Normally available for display to a human. */
    private String title;
    /** Type of the xlink, fixed to simple. */
    private String linkType = "simple";
    /** Role identifier, indicating the role of the linked document. */
    private URI role;
    /** Reference to a documentation site for a process, input, or output. */
    private URI href;

    /**
     * Main constructor.
     * All the argument needed can no be null.
     * @param title Title of the documentation.
     * @param role Role identifier.
     * @param href Reference to the documentation.
     * @throws IllegalArgumentException Exception get on giving a null values as argument
     */
    public Metadata(String title, URI role, URI href) {
        if(title == null || role == null || href == null){
            throw new IllegalArgumentException("None of the arguments can be null");
        }
        this.title = title;
        this.role = role;
        this.href = href;

    }

    /**
     * Returns the documentation title.
     * @return The documentation title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the documentation title.
     * @param title The documentation title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the link type.
     * @return The link type.
     */
    public String getLinkType() {
        return linkType;
    }

    /**
     * Returns the documentation role.
     * @return The documentation role.
     */
    public URI getRole() {
        return role;
    }

    /**
     * Sets the documentation role.
     * @param role The documentation role.
     */
    public void setRole(URI role) {
        this.role = role;
    }

    /**
     * Returns the documentation reference.
     * @return The documentation reference.
     */
    public URI getHref() {
        return href;
    }

    /**
     * Sets the documentation reference.
     * @param href The documentation reference.
     */
    public void setHref(URI href) {
        this.href = href;
    }
}
