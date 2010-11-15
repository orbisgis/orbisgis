/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.orbisgis.core.ui.plugins.views.sqlConsole.syntax;

import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;

/**
 * Special Completion class for SQL & OrbisGIS Functions
 * @author Antoine Gourlay
 */
public class SQLFunctionCompletion extends FunctionCompletion {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReplacementText() {
        return super.getReplacementText() + '(';
    }

    /**
     * {@inheritDoc}
     */
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
        StringBuffer sb = new StringBuffer();
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

    /**
     * {@inheritDoc}
     * @return true if the underlying functions have the same name
     */
//    @Override
//    public boolean equals(Object obj) {
//        if (obj == null) {
//            return false;
//        }
//        if (getClass() != obj.getClass()) {
//            return false;
//        }
//        final SQLFunctionCompletion other = (SQLFunctionCompletion) obj;
//        if (this.getName().equalsIgnoreCase(other.getName())) {
//            return true;
//        }
//        return false;
//    }

    public SQLFunctionCompletion(CompletionProvider provider, String name, String returnType) {
        super(provider, name.toUpperCase(), returnType);
    }
}
