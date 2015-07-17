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
     * @throws MalformedScriptException Exception get on giving a null values as argument
     */
    public Metadata(String title, URI role, URI href) throws MalformedScriptException {
        if(title == null){
            throw new MalformedScriptException(this.getClass(), "title", "can not be null");
        }
        if(role == null){
            throw new MalformedScriptException(this.getClass(), "role", "can not be null");
        }
        if(href == null){
            throw new MalformedScriptException(this.getClass(), "href", "can not be null");
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
     * @param title Not null documentation title.
     * @throws MalformedScriptException Exception get on giving a null values as argument
     */
    public void setTitle(String title) throws MalformedScriptException {
        if(title == null){
            throw new MalformedScriptException(this.getClass(), "title", "can not be null");
        }
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
     * @param role Not null documentation role.
     * @throws MalformedScriptException Exception get on giving a null values as argument
     */
    public void setRole(URI role) throws MalformedScriptException {
        if(role == null){
            throw new MalformedScriptException(this.getClass(), "role", "can not be null");
        }
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
     * @param href Not null documentation reference.
     * @throws MalformedScriptException Exception get on giving a null values as argument
     */
    public void setHref(URI href) throws MalformedScriptException {
        if(href == null){
            throw new MalformedScriptException(this.getClass(), "href", "can not be null");
        }
        this.href = href;
    }
}
