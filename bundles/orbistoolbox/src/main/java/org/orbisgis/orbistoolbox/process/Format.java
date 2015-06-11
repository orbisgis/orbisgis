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

import java.net.URL;

/**
 * Format of an input or of an output.
 *
 * For more information : http://docs.opengeospatial.org/is/14-065/14-065.html#20
 *
 * @author Sylvain PALOMINOS
 */

public class Format {
    /** Media type of the data. */
    private String mimeType;
    /** Encoding procedure or character set of the data.*/
    private String encoding;
    /** Identification of the data schema.*/
    private URL schema;
    /** The maximum size of the input data, in megabytes.*/
    private Integer maximumMegaBytes;
    /** Indicates that this format is the default format.*/
    private boolean defaultFormat;

    /**
     * Unique constructor providing the necessary attributes according to the WPS specification.
     * All the parameters should not be null.
     *
     * @param mimeType Media type of the data.
     * @param schema   Identification of the data schema.
     * @throws IllegalArgumentException Exception thrown if one of the parameters is null.
     */
    public Format(String mimeType, URL schema) throws IllegalArgumentException {
        if (mimeType == null) {
            throw new IllegalArgumentException("The parameter \"mimeType\" can not be null");
        }
        if (schema == null) {
            throw new IllegalArgumentException("The parameter \"schema\" can not be null");
        }
        this.mimeType = mimeType;
        this.encoding = "simple";
        this.schema = schema;
        this.maximumMegaBytes = null;
        this.defaultFormat = false;
    }

    /**
     * Returns the media type of the data.
     *
     * @return The media type of the data.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the media type of the data. It should not be null.
     *
     * @param mimeType The media type of the data
     * @throws IllegalArgumentException Exception thrown if the parameters is null.
     */
    public void setMimeType(String mimeType) throws IllegalArgumentException {
        if (mimeType == null) {
            throw new IllegalArgumentException("The parameter \"mimeType\" can not be null");
        }
        this.mimeType = mimeType;
    }

    /**
     * Returns the encoding procedure.
     *
     * @return The encoding procedure.
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * Returns the data schema.
     *
     * @return The data schema
     */
    public URL getSchema() {
        return schema;
    }

    /**
     * Sets the schema of the data. It should not be null.
     *
     * @param schema The data schema.
     * @throws IllegalArgumentException Exception thrown if the parameters is null.
     */
    public void setSchema(URL schema) throws IllegalArgumentException {
        if (schema == null) {
            throw new IllegalArgumentException("The parameter \"schema\" can not be null");
        }
        this.schema = schema;
    }

    /**
     * Returns the maximum size of the input data in MB or null if there is no limit.
     *
     * @return The maximum size of the input data.
     */
    public int getMaximumMegaBytes() {
        return maximumMegaBytes;
    }

    /**
     * Sets the maximum size od the input data in MB (0 and negative values stand for no limits).
     *
     * @param maximumMegaBytes Maximum size of the input data.
     */
    public void setMaximumMegaBytes(int maximumMegaBytes) {
        if (maximumMegaBytes < 0) {
            this.maximumMegaBytes = null;
        } else {
            this.maximumMegaBytes = maximumMegaBytes;
        }
    }

    /**
     * Sets the format as the default one.
     * @param defaultFormat True if it is the default format, false otherwise.
     */
    public void setDefaultFormat(boolean defaultFormat){
        this.defaultFormat = defaultFormat;
    }

    /**
     * Tells if the format is the default one.
     * @return True if it is the default format, false otherwise.
     */
    public boolean isDefaultFormat(){
        return this.defaultFormat;
    }
}
