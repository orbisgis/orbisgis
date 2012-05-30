package org.orbisgis.core.renderer.se.parameter.string;

import org.gdms.data.DataSource;
import org.orbisgis.core.renderer.se.parameter.ParameterException;
import org.orbisgis.core.renderer.se.parameter.SeParameter;


/**
 * A String value, stored as a SE parameter.</p><p>
 * A restriction list can be associated to a {@code StringParameter}. This list is used
 * to force values of this parameter to match one of the entries of the list.
 * @author maxence, alexis.
 * @todo implement 05-077r4 11.6.1, 11.6.2, 11.6.3 (String, number and date formating)
 */
public interface StringParameter extends SeParameter {
    
    
    //TODO Is (DataSource, featureId) the right way to access a feature ?
    /**
         * Retrieve a Value using this {@code StringParameter}. It can be retrieved using
         * the given datasource, or not, depending on the realizaton of this interface.
         * @param sds
         * The {@code DataSource} where to search.
         * @param fid
         * The entry where to get the value in the data source. Note that as we don't 
         * know the column where to search, this information must be given externally
         * (as in {@code Valuereference}, for instance.
         * @return
         * A {@code String} instance.
         * @throws ParameterException 
         */
    String getValue(DataSource sds, long fid) throws ParameterException;

    /**
     * Set the list of restrictions</p><p>
     * Restrictions are used to force {@code StringParameter} instances to match one 
     * of the {@code String}s of the list.
     * @param list 
     */
    void setRestrictionTo(String[] list);
}
