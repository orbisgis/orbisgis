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
 * The LiteralData type encodes atomic data such as scalars, linear units, or well-known names.
 * Domains for LiteralData are a combination of data types (e.g. Double, Integer, String),
 * a given value range, and an associated unit (e.g. meters, degrees Celsius).
 *
 * For more informations : http://docs.opengeospatial.org/is/14-065/14-065.html#25
 *
 * @author Sylvain PALOMINOS
 */

public class LiteralData extends DataDescription {
    /** The valid domain for literal data */
    private List<LiteralDataDomain> literalDataDomains;
    /** The literal value */
    private LiteralValue value;

    /**
     * Constructor giving a list of format, a list of valid domain and the literal value.
     * The Format list can not be null and only one of the format should be set as the default one.
     * @param formatList Not null default format..
     * @param literalDataDomainList Not null valid domain list for literal data.
     * @param value Not null value.
     * @throws IllegalArgumentException Exception get on :
     *  - giving a null, empty or containing null list of LiteralDataDomain
     *  - giving a list of Format with less or more than one default LiteralDataDomain.
     *  - giving a null, empty or containing null list of Format
     *  - giving a list of Format with less or more than one default Format.
     *  - giving a null value
     */
    public LiteralData(List<Format> formatList, List<LiteralDataDomain> literalDataDomainList, LiteralValue value)
            throws IllegalArgumentException  {
        super(formatList);
        if (literalDataDomainList == null || literalDataDomainList.isEmpty() || literalDataDomainList.contains(null)) {
            throw new IllegalArgumentException("The parameter \"literalDataDomainList\" can not be null or empty or " +
                    "containing a null value");
        }
        if (value == null) {
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        boolean hasDefault = false;
        for(LiteralDataDomain ldd : literalDataDomainList){
            if(ldd.isDefaultDomain() && hasDefault){
                throw new IllegalArgumentException("Only one LiteralDataDomain can be the default one");
            }
            hasDefault = true;
        }
        if(!hasDefault){
            throw new IllegalArgumentException("One LiteralDataDomain should be the default one");
        }

        this.setFormats(formatList);
        this.setLiteralDomainType(literalDataDomainList);
        this.value = value;
        if(value.getData() == null){
            for(LiteralDataDomain ldd : literalDataDomainList){
                if(ldd.isDefaultDomain()){
                    value.setData(ldd.getDefaultValue());
                }
            }
        }
    }

    /**
     * Returns the list of valid domains for literal data.
     * @return The list of valid domains for literal data.
     */
    public List<LiteralDataDomain> getLiteralDomainType() {
        return literalDataDomains;
    }

    /**
     * Adds a valid domain for literal data.
     * @param literalDataDomain Not null valid domain.
     * @throws IllegalArgumentException Exception get on giving a null argument.
     */
    public void addLiteralDomainType(LiteralDataDomain literalDataDomain) {
        if (literalDataDomain == null) {
            throw new IllegalArgumentException("The parameter \"literalDataDomain\" can not be null");
        }
        this.literalDataDomains.add(literalDataDomain);
    }

    /**
     * Removes a valid domain for literal data.
     * @param literalDataDomain Valid domain.
     */
    public void removeLiteralDomainType(LiteralDataDomain literalDataDomain) {
        if (this.literalDataDomains.size() == 1 && this.literalDataDomains.contains(literalDataDomain)) {
            throw new IllegalArgumentException("The attribute \"literalDataDomains\" can not be empty");
        }
        this.literalDataDomains.remove(literalDataDomain);
    }

    /**
     * Sets the list of valid domain for literal data.
     * @param literalDataDomains Not null list of not null valid domain.
     */
    public void setLiteralDomainType(List<LiteralDataDomain> literalDataDomains) {
        if (literalDataDomains == null || literalDataDomains.isEmpty()) {
            throw new IllegalArgumentException("The parameter \"literalDataDomains\" can not be null or empty");
        }
        this.literalDataDomains =
                literalDataDomains;
    }


    /**
     * Sets the given literalDataDomain as the default one.
     * @param literalDataDomain Not null new default literalDataDomain.
     * @throws IllegalArgumentException Exception get on setting a null or a not contained literalDataDomain as the default one.
     */
    protected void setDefaultLiteralDataDomain(LiteralDataDomain literalDataDomain){
        if(literalDataDomain == null){
            throw new IllegalArgumentException("The parameter \"literalDataDomain\" can not be null;");
        }
        if(!this.literalDataDomains.contains(literalDataDomain)) {
            throw new IllegalArgumentException("The literalDataDomain list does not contain the given literalDataDomain");
        }
        for(LiteralDataDomain ldd : literalDataDomains){
            ldd.setDefaultDomain(false);
        }
        literalDataDomain.setDefaultDomain(true);
    }

    /**
     * Sets the value.
     * @param value Not null new value.
     * @throws IllegalArgumentException Exception get on setting a null value.
     */
    public void setValue(LiteralValue value) throws IllegalArgumentException {
        if(value == null){
            throw new IllegalArgumentException("The parameter \"value\" can not be null");
        }
        this.value = value;
    }

    /**
     * Returns the value.
     * @return The value.
     */
    public LiteralValue getValue(){
        return this.value;
    }
}
