/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.ows;

import org.gdms.data.db.DBSource;

/**
 *
 * @author cleglaun
 */
public interface OwsDataSourceCredentialsRequiredListener {
    
    /**
     * Notifies listeners when the user entered valid source's credentials from
     * a dialog box.
     * @param source The data source whose credentials are ok.
     */
    public void credentialsOk(DBSource source);
}
