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
import java.util.Arrays;
import java.util.List;

/**
 * Process descriptions as well as the associated process inputs and outputs.
 * Other descriptive information shall be recorded in the Metadata element.
 * <p/>
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#19
 *
 * @author Sylvain PALOMINOS
 */

public class DescriptionType {

    /**
     * Title of a process, input, and output. Normally available for display to a human.
     */
    private String
            title;
    /**
     * Brief narrative description of a process, input, and output. Normally available for display to a human.
     */
    private String
            abstrac;
    /**
     * Keywords that characterize a process, its inputs, and outputs.
     */
    private String
            keywords;
    /**
     * Unambiguous identifier of a process, input, and output.
     */
    private URI
            identifier;
    /**
     * Reference to additional metadata about this item.
     */
    private List<Metadata>
            metadata;

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param title      Not null title of a process, input, output.
     * @param identifier Not null unambiguous identifier of a process, input, and output.
     * @throws IllegalArgumentException Exception thrown if one of the parameters is null.
     */
    public DescriptionType(String title,
                           URI identifier)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (title ==
                null) {
            throw new IllegalArgumentException("The parameter \"title\" can not be null");
        }
        if (identifier ==
                null) {
            throw new IllegalArgumentException("The parameter \"identifier\" can not be null");
        }
        //Sets the attributes
        this.title =
                title;
        this.abstrac =
                null;
        this.keywords =
                null;
        this.identifier =
                identifier;
        this.metadata =
                null;
    }

    /**
     * Returns the title.
     *
     * @return The title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Sets the title. Should not be null.
     *
     * @param title Not null title.
     * @throws IllegalArgumentException Exception thrown if the title is null.
     */
    public void setTitle(String title)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (title ==
                null) {
            throw new IllegalArgumentException("The parameter \"title\" can not be null");
        }
        //Sets the attribute
        this.title =
                title;
    }

    /**
     * Returns the abstract.
     *
     * @return The abstract.
     */
    public String getAbstrac() {
        return this.abstrac;
    }

    /**
     * Sets the abstract.
     *
     * @param abstrac The new abstract.
     */
    public void setAbstrac(String abstrac) {
        this.abstrac =
                abstrac;
    }

    /**
     * Returns the list of keywords. Returns null if it is empty.
     *
     * @return The list of keywords.
     */
    public List<String> getKeywords() {
        return Arrays.asList(this.keywords.split(" "));
    }

    /**
     * Adds a keyword to the list if it is not already in.
     *
     * @param keyword Keyword to add.
     */
    public void addKeyword(String keyword) {
        if (keyword !=
                null &&
                !this.keywords.contains(keyword)) {
            if (this.keywords ==
                    null) {
                this.keywords =
                        keyword;
            } else {
                this.keywords +=
                        " " +
                                keyword;
            }
        }
    }

    /**
     * Adds a list of keywords. If a keyword was already added, does nothing.
     *
     * @param keywords List of keywords to add.
     */
    public void addAllKeywords(List<String> keywords) {
        for (String s : keywords) {
            addKeyword(s);
        }
    }

    /**
     * Removes a keyword from the list.
     *
     * @param keyword Keyword to remove.
     */
    public void removeKeyword(String keyword) {
        if (keyword ==
                null) {
            return;
        }
        int
                keywordPosition =
                this.keywords.indexOf(keyword);
        if (keywordPosition !=
                -1) {
            int
                    wordPosition;
            int
                    nextWordPosition;
            if (keywordPosition ==
                    0) {
                wordPosition =
                        0;
                nextWordPosition =
                        keyword.length() +
                                1;
            } else {
                wordPosition =
                        keywordPosition -
                                1;
                nextWordPosition =
                        wordPosition +
                                keyword.length() +
                                1;
            }
            this.keywords =
                    this.keywords.substring(0,
                            wordPosition) +
                            this.keywords.substring(nextWordPosition);
        }
    }

    /**
     * Removes a list of keywords.
     *
     * @param keywords List of keywords to remove.
     */
    public void removeAllKeywords(List<String> keywords) {
        for (String s : keywords) {
            removeKeyword(s);
        }
    }

    /**
     * Sets the list of keywords.
     *
     * @param keywords New list of keywords.
     */
    public void setKeywords(List<String> keywords) {
        if (keywords ==
                null ||
                keywords.size() ==
                        0) {
            this.keywords =
                    null;
        }
        this.keywords =
                "";
        for (String keyword : keywords) {
            addKeyword(keyword);
        }
    }

    /**
     * Returns the identifier.
     *
     * @return The identifier.
     */
    public URI getIdentifier() {
        return this.identifier;
    }

    /**
     * Sets the identifier. It should not be null
     *
     * @param identifier Not null identifier.
     * @throws IllegalArgumentException Exception thrown if the title is null.
     */
    public void setIdentifier(URI identifier)
            throws
            IllegalArgumentException {
        //Verify if the parameters are not null
        if (identifier ==
                null) {
            throw new IllegalArgumentException("The parameter \"identifier\" can not be null");
        }
        this.identifier =
                identifier;
    }

    /**
     * Returns the list of metadata. Returns null if the list is empty.
     *
     * @return The list od metadata.
     */
    public List<Metadata> getMetadata() {
        return this.metadata;
    }

    /**
     * Adds a metadata to the list.
     *
     * @param metadata Metadata to add.
     */
    public void addMetadata(Metadata metadata) {
        if (metadata !=
                null) {
            if (this.metadata ==
                    null) {
                this.metadata =
                        new ArrayList<>();
            }
            if (!this.metadata.contains(metadata)) {
                this.metadata.add(metadata);
            }
        }
    }

    /**
     * Adds a list of metadata.
     *
     * @param metadatas List of metadata to add.
     */
    public void addAllMetadata(List<Metadata> metadatas) {
        for (Metadata m : metadatas) {
            addMetadata(m);
        }
    }

    /**
     * Removes from the list a metadata
     *
     * @param metadata Metadata to remove.
     */
    public void removeMetadata(Metadata metadata) {
        if (this.metadata !=
                null) {
            this.metadata.remove(metadata);
        }
    }

    /**
     * Removes a list of metadata.
     *
     * @param metadatas List of metadata to remove
     */
    public void removeAllMetadatas(List<Metadata> metadatas) {
        this.metadata.removeAll(metadatas);
    }

    /**
     * Sets the list of metadata.
     *
     * @param metadata New metadata list.
     */
    public void setMetadata(List<Metadata> metadata) {
        this.metadata =
                metadata;
    }
}
