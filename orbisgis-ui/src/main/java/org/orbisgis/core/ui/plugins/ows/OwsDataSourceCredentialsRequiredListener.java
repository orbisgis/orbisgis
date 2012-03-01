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
    
    public void credentialsOk(DBSource source);
}
