/**
 * OrbisToolBox is an OrbisGIS plugin dedicated to create and manage processing.
 * <p/>
 * OrbisToolBox is distributed under GPL 3 license. It is produced by CNRS <http://www.cnrs.fr/> as part of the
 * MApUCE project, funded by the French Agence Nationale de la Recherche (ANR) under contract ANR-13-VBDU-0004.
 * <p/>
 * OrbisToolBox is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p/>
 * OrbisToolBox is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with OrbisToolBox. If not, see
 * <http://www.gnu.org/licenses/>.
 * <p/>
 * For more information, please consult: <http://www.orbisgis.org/> or contact directly: info_at_orbisgis.org
 */

package org.orbisgis.wpsservice.model;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Exception thrown when a Groovy WPS script is malformed.
 *
 * @author Sylvain PALOMINOS
 **/

public class MalformedScriptException extends Exception {

    /** I18N object */
    private static final I18n I18N = I18nFactory.getI18n(MalformedScriptException.class);

    /**
     * Create an exception with a message constructed that way :
     * "Error on implementing '<wpsModelClass>', the argument '<wrongArgument>'<reason>"
     * @param wpsModelClass Object that can not be instantiated.
     * @param wrongArgument Wrong argument.
     * @param reason Reason why the argument is wrong.
     */
    public MalformedScriptException(Class wpsModelClass, String wrongArgument, String reason){
        super(I18N.tr("Error on implementing '{0}', the argument '{1}' {2}.",
                wpsModelClass.getSimpleName(), wrongArgument, reason));
    }
}
