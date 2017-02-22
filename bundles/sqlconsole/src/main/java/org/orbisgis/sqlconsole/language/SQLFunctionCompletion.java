/**
 * OrbisGIS is a java GIS application dedicated to research in GIScience.
 * OrbisGIS is developed by the GIS group of the DECIDE team of the 
 * Lab-STICC CNRS laboratory, see <http://www.lab-sticc.fr/>.
 *
 * The GIS group of the DECIDE team is located at :
 *
 * Laboratoire Lab-STICC – CNRS UMR 6285
 * Equipe DECIDE
 * UNIVERSITÉ DE BRETAGNE-SUD
 * Institut Universitaire de Technologie de Vannes
 * 8, Rue Montaigne - BP 561 56017 Vannes Cedex
 * 
 * OrbisGIS is distributed under GPL 3 license.
 *
 * Copyright (C) 2007-2014 CNRS (IRSTV FR CNRS 2488)
 * Copyright (C) 2015-2017 CNRS (Lab-STICC UMR CNRS 6285)
 *
 * This file is part of OrbisGIS.
 *
 * OrbisGIS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OrbisGIS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * OrbisGIS. If not, see <http://www.gnu.org/licenses/>.
 *
 * For more information, please consult: <http://www.orbisgis.org/>
 * or contact directly:
 * info_at_ orbisgis.org
 */
package org.orbisgis.sqlconsole.language;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;

/**
 * Special Completion class for SQL & OrbisGIS Functions
 * @author Antoine Gourlay
 */
public class SQLFunctionCompletion extends FunctionCompletion {

    @Override
    public String getReplacementText() {
        return super.getReplacementText() + '(';
    }

    @Override
    public String getName() {
        return super.getName().replace("(", "").toUpperCase();
    }

    /**
     * Returns the function formatted as follow : "NAME(TYPE,TYPE,...) : RETURN_TYPE"
     * @return the formatted function
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String type;

        // Add the item being described's name.
        sb.append(getName());

        // Add parameters for functions.
        CompletionProvider provider = getProvider();
        sb.append(provider.getParameterListStart());
        for (int i = 0; i < getParamCount(); i++) {
            Parameter param = getParam(i);
            type = param.getType();
            String name = param.getName();
            if (type != null) {
                sb.append(type);
                if (name != null) {
                    sb.append(' ');
                }
            }
            if (name != null) {
                sb.append(name);
            }
            if (i < getParamCount() - 1) {
                sb.append(provider.getParameterListSeparator());
            }
        }
        sb.append(provider.getParameterListEnd());


        // add function return type if applicable
        type = getType();
        if (type != null) {
            sb.append(" : ").append(type);
        }

        return sb.toString();
    }

    public SQLFunctionCompletion(CompletionProvider provider, String name, String returnType) {
        super(provider, name.toUpperCase(), returnType);
    }
}
