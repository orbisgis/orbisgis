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

package org.orbisgis.wpsservice.controller.utils;

import groovy.lang.Closure;
import net.sourceforge.cobertura.CoverageIgnore;
import org.h2gis.utilities.wrapper.StatementWrapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Groovy closure used to ba able to cancel a running process.
 *
 * @author Sylvain PALOMINOS
 */
@CoverageIgnore
public class CancelClosure extends Closure {

    private List<StatementWrapper> statementList;

    public CancelClosure(Object owner) {
        super(owner);
        statementList = new ArrayList<>();
    }

    /**
     * Called method by groovy.
     * @param stmt
     */
    public void doCall(StatementWrapper stmt){
        statementList.add(stmt);
    }

    /**
     * Cancel all the running sql queries.
     */
    public void cancel(){
        for(StatementWrapper stmt : statementList){
            try {
                stmt.cancel();
            } catch (SQLException ignored) {
            }
        }
    }
}
